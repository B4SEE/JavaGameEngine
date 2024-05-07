package cs.cvut.fel.pjv.gamedemo.engine.gamelogic;

import cs.cvut.fel.pjv.gamedemo.common_classes.Object;
import cs.cvut.fel.pjv.gamedemo.common_classes.*;
import cs.cvut.fel.pjv.gamedemo.engine.*;
import cs.cvut.fel.pjv.gamedemo.engine.utils.Atmospheric;
import cs.cvut.fel.pjv.gamedemo.engine.utils.Checker;
import javafx.application.Platform;
import javafx.scene.shape.Circle;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.List;

public class EntitiesLogic {
    private static final Logger logger = LogManager.getLogger(EntitiesLogic.class);
    private static GameData gameData;

    public static void setGameData(GameData gameData) {
        EntitiesLogic.gameData = gameData;
    }

    //region Spawn methods

    //region Immediate spawn methods
    /**
     * Spawn grandmother in the wagon.
     * @param wagon wagon to spawn the grandmother in
     */
    public static void spawnGrandmother(Wagon wagon) {
        logger.debug("Spawning grandmother in wagon " + wagon.getId() + "...");
        EntitiesCreator.createGrandmother(wagon);
        gameData.setGrandmother(wagon.getEntities().getLast());
        gameData.getIsometric().setEntities(wagon.getEntities());
        gameData.getIsometric().updateAll();
        Events.setGrandmotherSpawned(true);
        gameData.getIsometric().setEntities(gameData.getWagon().getEntities());
        gameData.getIsometric().updateAll();
        logger.info("Grandmother spawned at " + gameData.getGrandmother().getPositionX() + ", " + gameData.getGrandmother().getPositionY());
    }

    /**
     * Spawn conductor.
     */
    private static void spawnConductor() {
        Wagon wagon = gameData.getTrain().getWagonById(gameData.getTrain().findMinWagonId());//get the oldest wagon
        logger.debug("Spawning conductor in wagon " + wagon.getId() + "...");
        EntitiesCreator.createConductor(wagon);
        gameData.setConductor(wagon.getEntities().getLast());
        gameData.getIsometric().setEntities(wagon.getEntities());
        gameData.getIsometric().updateAll();
        Events.setCanSpawnKey(true);
        gameData.getIsometric().setEntities(gameData.getWagon().getEntities());
        gameData.getIsometric().updateAll();
        System.out.println("Conductor wagon id: " + gameData.getConductor().getCurrentWagon().getId());
        logger.info("Conductor spawned at " + gameData.getConductor().getPositionX() + ", " + gameData.getConductor().getPositionY());
    }

    /**
     * Spawn random number of enemies for the trap event.
     */
    public static void spawnEnemies() {
        int enemyCount = Math.max(Constants.MIN_TRAP_ENEMIES_COUNT, (int) (Math.random() * Constants.MAX_TRAP_ENEMIES_COUNT));
        for (int i = 0; i < enemyCount; i++) {
            Entity enemy = EntitiesCreator.createRandomEnemy(gameData.getWagon().getType());
            if (enemy == null) continue;

            logger.debug("Setting enemy position...");
            Object[] freeSpace = gameData.getWagon().getAllFloorObjects();
            Object freeSpaceObject = freeSpace[(int) (Math.random() * freeSpace.length)];
            enemy.setPositionX(freeSpaceObject.getIsoX() - 32);
            enemy.setPositionY(freeSpaceObject.getIsoY() - 80);
            enemy.setCurrentWagon(gameData.getWagon());
            gameData.getWagon().addEntity(enemy);
            logger.info("Enemy spawned in wagon ID " + gameData.getWagon().getId() + " at " + enemy.getPositionX() + ", " + enemy.getPositionY());
        }
        gameData.getIsometric().setEntities(gameData.getWagon().getEntities());//otherwise the entities' hitboxes are drawn in the wrong place
        gameData.getIsometric().updateAll();
    }

    /**
     * Spawn the boss.
     * @param name name of the boss
     */
    public static void spawnBoss(String name) {
        logger.debug("Spawning boss " + name + "...");
        Object trap = gameData.getWagon().getTrap();
        gameData.setBoss(new Entity(name, "textures/default/entities/bosses/" + name + ".png", Constants.EntityType.BOSS, trap.getIsoX() - 48, trap.getIsoY() - 80));
        int[] bossStats = Constants.BOSS_STATS.get(name);
        EntitiesCreator.setAsBoss(gameData.getBoss(), bossStats);
        gameData.getBoss().setCurrentWagon(gameData.getWagon());
        gameData.getWagon().addEntity(gameData.getBoss());
        gameData.getIsometric().setEntities(gameData.getWagon().getEntities());
        gameData.getIsometric().updateAll();
        gameData.setDeathMessage(Constants.BOSS_DEATH_MESSAGES.get(name));
        logger.info("Boss " + name + " spawned at " + gameData.getBoss().getPositionX() + ", " + gameData.getBoss().getPositionY());
    }
    //endregion

    //region Spawn handlers
    /**
     * Call the guard (spawn the guard).
     */
    private static void callGuard() {
        if (Events.getCurrentEvent() == Constants.Event.TRAP_EVENT) {//guard was called during the trap event, needs to be handled differently (reimplemented)
            logger.info("Trying to call the guard during the trap event, guard will not be called");
            return;
        }
        logger.debug("Calling the guard...");
        Entity guard = EntitiesCreator.createGuard();
        guard.setBehaviour(Constants.Behaviour.BULLY);
        guard.setCurrentWagon(gameData.getWagon());
        guard.setPositionX(gameData.getWagon().getDoorLeftTarget().getIsoX() - 32);
        guard.setPositionY(gameData.getWagon().getDoorLeftTarget().getIsoY() - 64);
        gameData.getWagon().addEntity(guard);
        gameData.getIsometric().setEntities(gameData.getWagon().getEntities());
        gameData.getIsometric().updateAll();
        guard.setStartIndex(guard.findOnWhatObject());
        logger.info("Guard called");
    }

    /**
     * Handle conductor spawn.
     */
    public static void handleConductorSpawn() {
        Events.setConductorSpawned(true);
        Thread conductorSpawn = new Thread(() -> {
            try {
                Thread.sleep(100 * Constants.CONDUCTOR_SPAWN_DELAY);
            } catch (InterruptedException e) {
                logger.error("Conductor spawn interrupted", e);
            }
            Platform.runLater(EntitiesLogic::spawnConductor);// Avoid modifying the entities list from a different thread
        });
        conductorSpawn.start();
    }

    /**
     * Handle guard call.
     */
    public static void handleGuardCall() {
        logger.debug("Guard should be called");
        Events.setShouldCallGuard(false);
        Events.setGuardCalled(true);
        Thread guardCall = new Thread(() -> {
            try {
                logger.debug("Trying to call the guard...");
                Thread.sleep(100 * Constants.GUARD_CALL_DELAY);
            } catch (InterruptedException e) {
                logger.error("Guard call interrupted", e);
            }
            Platform.runLater(EntitiesLogic::callGuard);// Avoid modifying the entities list from a different thread
        });
        guardCall.start();
    }
    //endregion

    //endregion

    //region Entities utility methods
    /**
     * Handle entity response/reaction.
     * @param entity entity to handle
     */
    public static void handleResponse(Entity entity) {
        if (Math.random() * 100 > Constants.CHANCE_TO_CALL_GUARD) {
            entity.setBehaviour(Constants.Behaviour.AGGRESSIVE);
        } else {
            Events.setShouldCallGuard(true);
        }
    }

    public static void placePlayerInWagon(Wagon wagon) {
        logger.debug("Placing player in wagon " + wagon.getId() + "...");
        Object[] freeSpace = gameData.getWagon().getAllFloorObjects();
        Object freeSpaceObject = freeSpace[(int) (Math.random() * freeSpace.length)];
        gameData.getPlayer().setPositionX(freeSpaceObject.getIsoX() - 32);
        gameData.getPlayer().setPositionY(freeSpaceObject.getIsoY() - 80);
        logger.debug("Player placed in wagon " + wagon.getId() + " at " + gameData.getPlayer().getPositionX() + ", " + gameData.getPlayer().getPositionY());
    }
    //endregion

    //region Player methods
    /**
     * Use player's item in hand: if the player has no item in hand, the player tries to attack the entity,
     * otherwise the player uses the item.
     */
    public static void playerUseHand() {
        if (gameData.getPlayer().getHandItem() == null) {
            gameData.getPlayer().tryAttack(gameData.getPlayer(), gameData.getWagon().getEntities(), gameData.getTime());
            return;
        }
        gameData.getPlayer().useHandItem(gameData.getTime());
    }
    /**
     * Stops player's movement.
     */
    public static void stopPlayer() {
        gameData.getIsometric().updatePlayerDeltaX(0);
        gameData.getIsometric().updatePlayerDeltaY(0);
    }
    //endregion

    //region Enemies methods
    /**
     * Handle bully entity.
     * @param entity entity to be handled
     */
    public static void handleBully(Entity entity) {
        if (Checker.checkIfEntityRemember(entity, gameData.getPlayer(), gameData.getIsometric().getTwoAndTallerWalls(), gameData.getTime())) {
            if (Checker.checkCollision(entity.getAttackRange(), gameData.getPlayer().getHitbox())) {
                if (gameData.getKidnapStage() != gameData.getKidnapStageCount()) {
                    if (entity.getCanAttack()) {
                        gameData.setKidnapStage(gameData.getKidnapStage() + 1);
                        entity.setCanAttack(false);
                        entity.setWhenAttacked(gameData.getTime());
                    }
                    if (!entity.getCanAttack() && (gameData.getTime() - entity.getWhenAttacked() != 0) && (gameData.getTime() - entity.getWhenAttacked()) % entity.getCooldown() == 0) {
                        entity.setCanAttack(true);
                        entity.setWhenAttacked(0);
                    }
                    gameData.getIsometric().showKidnappingProgress(gameData.getKidnapStageCount(), gameData.getKidnapStage());
                }
            } else {
                gameData.setKidnapStage(0);// Reset the kidnap stage if the player is not in the attack range
            }
            if (gameData.getKidnapStage() == gameData.getKidnapStageCount()) {
                entity.setSpeedX(3);
                entity.setSpeedY(3);
                Events.setPlayerKidnapped(true);
                GameLogicHandlers.resetSceneHandlers(gameData.getStage().getScene());
                Door door;

                if (Math.abs(entity.getPositionX() - gameData.getWagon().getDoorLeft().getIsoX()) < Math.abs(entity.getPositionX() - gameData.getWagon().getDoorRight().getIsoX())) {
                    door = gameData.getWagon().getDoorLeft();
                } else {
                    door = gameData.getWagon().getDoorRight();
                }
                Circle playerHitbox = (Circle) gameData.getPlayer().getHitbox();
                playerHitbox.setRadius(0.1);
                gameData.getPlayer().setPositionX(entity.getPositionX() - 15);
                gameData.getPlayer().setPositionY(entity.getPositionY() - 15);
                EntitiesMovementLogic.moveTowardsDoor(entity, door);
                if (Checker.checkIfEntityStuck(entity)) {
                    int oldSpeedX = entity.getSpeedX();
                    int oldSpeedY = entity.getSpeedY();
                    entity.setSpeedX(entity.getSpeedX() + 3);
                    entity.setSpeedY(entity.getSpeedY() + 3);
                    EntitiesMovementLogic.moveTowardsDoor(entity, door);
                    entity.setSpeedX(oldSpeedX);
                    entity.setSpeedY(oldSpeedY);
                }
                if (Checker.checkCollision(entity.getAttackRange(), door.getObjectHitbox())) {
                    gameData.setDeathMessage("You were thrown out of the train!");
                    GameManagement.stopGame();
                }
            }
        }
    }
    //endregion

    //region Pursuers methods
    /**
     * Set the pursuers in the wagon.
     * @param wagonPursuers list of pursuers
     */
    public static void setPursuers(List<Entity> wagonPursuers) {
        logger.debug("Looking for pursuers in the wagon");
        wagonPursuers.clear();
        for (Entity entity : gameData.getWagon().getEntities()) {
            if (entity.getBehaviour() == Constants.Behaviour.AGGRESSIVE && entity.getType() != Constants.EntityType.CONDUCTOR) {//conductor uses different logic
                if (entity.getIntelligence() == Constants.Intelligence.HIGH) {
                    if (Checker.checkIfEntityRemember(entity, gameData.getPlayer(), gameData.getIsometric().getTwoAndTallerWalls(), gameData.getTime())) {
                        logger.info("Entity " + entity.getName() + " remembered the player and will pursue him");
                        wagonPursuers.add(entity);
                    }
                }
            }
        }
        logger.debug("Pursuers: " + wagonPursuers.size());
    }
    //endregion

    //region Conductor/Grandmother methods
    /**
     * Handle conductor's logic.
     * Logic:
     * <ul>
     *    <li>If the player is near, conductor will start dialogue and check the ticket.</li>
     *    <li>If the player does not have the ticket, the conductor becomes aggressive.</li>
     *    <li>If conductor is aggressive, he will not start dialogue.</li>
     *    <li>If conductor is aggressive and the player is in the same wagon, he will pursue the player.</li>
     *    <li>If conductor is aggressive and the player is in another wagon, conductor will move normally.</li>
     *    <li>Forbid the player to go behind the conductor (conductor will lock doors behind him).</li>
     * </ul>
     * Note: There is a small (very small) chance for the conductor to become neutral again if the player is <b>not</b> in the same wagon.
     */
    public static void handleConductor() {
        if (!gameData.getConductor().getCurrentWagon().getDoorRight().isLocked()) gameData.getConductor().getCurrentWagon().getDoorRight().lock();
        if (Checker.checkIfEntityStuck(gameData.getConductor())) handleConductorStuck(gameData.getConductor());

        if (gameData.getConductor().getBehaviour() == Constants.Behaviour.NEUTRAL) {
            if (gameData.getConductor().getCurrentWagon().getId() == gameData.getPlayer().getCurrentWagon().getId()) {
                if (Checker.checkIfConductorNearPlayer(gameData.getConductor(), gameData.getPlayer())) {
                    logger.info("Conductor is near the player, starting dialogue...");
                    GameLogicWindows.openDialogue(gameData.getConductor());
                    //TODO: play conductor sound (read first sentence)
                }
            }
        } else {
            if (gameData.getConductor().getCurrentWagon().getId() == gameData.getPlayer().getCurrentWagon().getId()) {
                EntitiesMovementLogic.intelligenceTwoPursue(gameData.getConductor(), gameData.getPlayer());
                return;
            }
        }
        EntitiesMovementLogic.moveTowardsDoor(gameData.getConductor(), gameData.getConductor().getCurrentWagon().getDoorLeft());
    }

    /**
     * Handle grandmother's logic.
     * Logic:
     * <ul>
     *     <li>Forbid the player to go ahead of the grandmother (grandmother will lock doors ahead of her).</li>
     *     <li>Grandmother will not attack when aggressive but change the dialogue and call the guard.</li>
     *     <li>Player can trade with the grandmother (works like normal vendor).</li>
     * </ul>
     * Note: There is a small (very small) chance for the grandmother to become neutral again if the player is <b>not</b> in the same wagon.
     */
    public static void handleGrandmother() {
        if (gameData.getConductor().getBehaviour() == Constants.Behaviour.NEUTRAL) {
            EntitiesMovementLogic.moveTowardsDoor(gameData.getGrandmother(), gameData.getConductor().getCurrentWagon().getDoorLeft());
        } else {
            if (gameData.getConductor().getCurrentWagon().getId() == gameData.getPlayer().getCurrentWagon().getId()) {
                gameData.getGrandmother().setBehaviour(Constants.Behaviour.NEUTRAL);
                gameData.getGrandmother().setDialoguePath("grandmother_aggressive.json");
                Events.setShouldCallGuard(true);
            } else {
                EntitiesMovementLogic.moveTowardsDoor(gameData.getGrandmother(), gameData.getConductor().getCurrentWagon().getDoorLeft());
            }
        }
        if (!gameData.getConductor().getCurrentWagon().getDoorLeft().isLocked()) gameData.getConductor().getCurrentWagon().getDoorLeft().lock();
        if (Checker.checkIfEntityStuck(gameData.getGrandmother())) handleConductorStuck(gameData.getGrandmother());
    }

    /**
     * Generate the next wagon and teleport the conductor to the next wagon.
     */
    public static void generateNextWagonByConductor(Entity conductor) {
        logger.info("Generating next wagon by the " + conductor.getName() + "...");
        String wagonType = "CARGO";
        Wagon nextWagon = new Wagon(gameData.getTrain().findMaxWagonId() + 1, wagonType);
        nextWagon.generateNextWagon(conductor.getCurrentWagon(), true);
        gameData.getTrain().addWagon(nextWagon);
        // Initialise the next wagon
        gameData.getIsometric().initialiseWagon(nextWagon);
        gameData.getIsometric().updateAll();
        nextWagon.setObstacles(gameData.getIsometric().getWalls());
        // Set original/player's wagon
        gameData.getIsometric().initialiseWagon(gameData.getWagon());
        gameData.getIsometric().updateAll();
    }

    /**
     * Open the existing wagon door and teleport the conductor to the next wagon.
     */
    public static void openExistingWagonDoorByConductor(Entity conductor) {
        logger.info("Opening existing wagon door by the " + conductor.getName() + "...");
        if (gameData.getPlayer().getCurrentWagon().getId() == gameData.getTrain().findMinWagonId()) {
            // Do not open the door if the player is in the oldest wagon (aka same wagon as the conductor)
            return;
        }
        Door wagonDoor = conductor.getCurrentWagon().getDoorLeft();
        if (wagonDoor.isLocked()) {
            // Unlock the next wagon door (mainly for the grandmother)
            wagonDoor.unlock();
        }
        wagonDoor.teleport(conductor);
        Wagon nextWagon = gameData.getTrain().getWagonById(wagonDoor.getTargetId());
        conductor.getCurrentWagon().getEntities().remove(conductor);
        conductor.setCurrentWagon(nextWagon);
        nextWagon.addEntity(conductor);
        // Update all hitboxes
        gameData.getIsometric().setEntities(conductor.getCurrentWagon().getEntities());
        gameData.getIsometric().updateAll();
        gameData.getIsometric().setEntities(gameData.getWagon().getEntities());
        gameData.getIsometric().updateAll();
    }

    /**
     * Handle the stuck conductor.
     * If the conductor is stuck, change strategy: speed up if the player is in the same wagon,
     * teleport to the next wagon if the player is in another wagon.
     */
    private static void handleConductorStuck(Entity conductor) {
        logger.debug("Conductor " + conductor.getName() + " is stuck, changing strategy...");

        if (conductor.getCurrentWagon().getId() == gameData.getPlayer().getCurrentWagon().getId()) {
            int originalSpeedX = conductor.getSpeedX();
            int originalSpeedY = conductor.getSpeedY();
            conductor.setSpeedX(originalSpeedX * 2);
            conductor.setSpeedY(originalSpeedY * 2);
            EntitiesMovementLogic.intelligenceTwoPursue(conductor, gameData.getPlayer());
            conductor.setSpeedX(originalSpeedX);
            conductor.setSpeedY(originalSpeedY);
        } else {
            if (conductor.getCurrentWagon().getDoorLeft().getTargetId() == -1) {
                generateNextWagonByConductor(conductor);
            } else if (gameData.getTime() % 5 == 0) {
                openExistingWagonDoorByConductor(conductor);
                if (Math.random() < 0.05) {
                    logger.info("Conductor " + conductor.getName() + " is no longer aggressive");
                    conductor.setBehaviour(Constants.Behaviour.NEUTRAL);
                    if (conductor.getDialoguePath().contains("aggressive")) {
                        // Replace the aggressive dialogue with the default one
                        conductor.setDialoguePath(conductor.getDialoguePath().replace("aggressive", "default"));
                    }
                }
            }
        }
    }
    //endregion

    //region Boss methods
    /**
     * Handle boss entity.
     */
    public static void handleBoss() {
        if (Events.isBossFight()) {
            Atmospheric.startBossMusic(gameData.getBoss().getName());
            if (Checker.checkIfEntityCanSee(gameData.getBoss(), gameData.getPlayer(), gameData.getIsometric().getTwoAndTallerWalls())) {
                if (Checker.checkCollision(gameData.getBoss().getAttackRange(), gameData.getPlayer().getHitbox())) {
                    gameData.getBoss().tryAttack(gameData.getBoss(), List.of(gameData.getPlayer()), gameData.getTime());
                }
                bossAttack(gameData.getPlayer().getPositionX() + 32, gameData.getPlayer().getPositionY() + 80, gameData.getBoss().getAttackRangeSize(), gameData.getBoss().getCooldown() - 1);
            }
        }
    }

    /**
     * Handle the boss attack.
     * @param x x coordinate
     * @param y y coordinate
     * @param radius radius of the attack
     * @param seconds time for player to react
     * <br>
     * <br>
     * Note: Even if game is paused, the boss can still attack the player. It will <b>not</b> be reimplemented.
     */
    private static void bossAttack(double x, double y, int radius, int seconds) {
        if (!Events.isBossFight() || !gameData.getBoss().getCanAttack()) return;

        gameData.getIsometric().showBossAttackTarget(x, y, radius, seconds);

        Thread attackTarget = new Thread(() -> {
            Circle attackCircle = new Circle(x, y, radius * Constants.TILE_WIDTH);
            try {
                logger.info(gameData.getBoss().getName() + " trying to attack, player has " + seconds + " seconds to react...");
                Thread.sleep(seconds * 1000L);
            } catch (InterruptedException e) {
                logger.error("Error while boss attacking: " + e.getMessage());
            }
            if (Checker.checkCollision(attackCircle, gameData.getPlayer().getHitbox())) {
                logger.info(gameData.getBoss().getName() + " attacked the player");
                gameData.getPlayer().takeDamage(gameData.getBoss().getDamage());
            }
        });

        attackTarget.start();

        gameData.getBoss().setCanAttack(false);
        gameData.getBoss().setWhenAttacked(gameData.getTime());

        if (!gameData.getBoss().getCanAttack()
                && (gameData.getTime() - gameData.getBoss().getWhenAttacked() != 0)
                && (gameData.getTime() - gameData.getBoss().getWhenAttacked()) % gameData.getBoss().getCooldown() == 0) {
            gameData.getBoss().setCanAttack(true);
            gameData.getBoss().setWhenAttacked(0);
        }
    }
    //endregion
}
