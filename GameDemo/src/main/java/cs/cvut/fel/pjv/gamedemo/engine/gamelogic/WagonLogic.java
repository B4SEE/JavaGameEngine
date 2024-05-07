package cs.cvut.fel.pjv.gamedemo.engine.gamelogic;

import cs.cvut.fel.pjv.gamedemo.common_classes.*;
import cs.cvut.fel.pjv.gamedemo.common_classes.Object;
import cs.cvut.fel.pjv.gamedemo.engine.utils.Atmospheric;
import cs.cvut.fel.pjv.gamedemo.engine.utils.Checker;
import cs.cvut.fel.pjv.gamedemo.engine.Events;
import cs.cvut.fel.pjv.gamedemo.engine.Isometric;
import cs.cvut.fel.pjv.gamedemo.engine.utils.RandomHandler;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class WagonLogic {
    private static final Logger logger = LogManager.getLogger(WagonLogic.class);
    private static GameData gameData;
    public static void setGameData(GameData gameData) {
        WagonLogic.gameData = gameData;
    }

    //region Wagon methods

    //region Interactive object methods
    /**
     * Open/close the door.
     * @param object door to be opened/closed
     */
    public static void useLockableDoor(Object object) {
        logger.debug("Using lockable door...");
        if (object.isSolid()) {
            object.setIsSolid(false);
            object.setTexturePath(object.getTexturePath().replace("closed", "opened"));
            logger.info("Door opened");
        } else {
            object.setIsSolid(true);
            object.setTexturePath(object.getTexturePath().replace("opened", "closed"));
            logger.info("Door closed");
        }
        gameData.getIsometric().updateAll();
    }
    //endregion

    //region Door methods
    /**
     * Unlock the wagon doors.
     */
    public static void unlockWagonDoors() {
        Wagon wagon = gameData.getWagon();
        wagon.getDoorLeft().unlock();
        wagon.getDoorRight().unlock();
    }

    /**
     * Handle walking through the wagon door by the player.
     * @param object door to be opened
     */
    public static void handleWagonDoor(Door object) {
        if (Events.getCurrentEvent() == Constants.Event.TRAP_EVENT) {
            logger.info("Player tried to open the door during the trap event");
            object.lock();
        }
        if (object.isLocked()) {
            logger.debug("Door is locked");
            if (Checker.checkIfPlayerHasKeyInMainHand(gameData.getPlayer())) {
                if (gameData.getConductor().getCurrentWagon() == gameData.getPlayer().getCurrentWagon()) {
                    logger.info("Player tried to open the door behind the conductor");
                    //TODO: play wrong key sound
                    return;
                }
                gameData.getPlayer().getPlayerInventory().setMainHandItem(null);
                object.unlock();
                //TODO: play unlock sound
            } else {
                logger.debug("Player tried to open the locked door without the key");
            }
        } else {
            //TODO: play door sound
            if (Events.getTimeLoopCounter() > 0) {
                logger.info("Player tried to open the door during the time loop event");
                TrapLogic.handleTimeLoop(object);
                return;
            }
            logger.info("Player opened the wagon door");
            openWagonDoor(object);
        }
    }

    /**
     * Open wagon door (teleport the player to the next wagon).
     * @param door door to be opened
     */
    private static void openWagonDoor(Door door) {
        Atmospheric.resetVolume();
        Events.setCurrentEvent(Constants.Event.DEFAULT_EVENT);
        Events.setGuardCalled(false);
        gameData.setEventStartTime(0);
        gameData.setEventDuration(0);
        if (door.getTargetId() == -1) {
            logger.info("No wagon behind the door, generating new wagon...");
            generateNextWagonAndOpenDoor(door);
        } else {
            logger.info("Existing wagon behind the door, opening the door...");
            openExistingWagonDoor(door);
        }
    }

    /**
     * Open the existing wagon door (teleport the player to the next wagon).
     * @param door door to be opened
     */
    private static void openExistingWagonDoor(Door door) {
        for (Wagon trainWagon : gameData.getTrain().getWagonsArray()) {
            if (trainWagon.getId() == door.getTargetId()) {
                if (door == gameData.getWagon().getDoorLeft()) {
                    EntitiesLogic.setPursuers(gameData.getRightWagonPursuers());
                    gameData.getIsometric().initialiseWagon(trainWagon);
                    gameData.getIsometric().updateAll();
                    trainWagon.setObstacles(gameData.getIsometric().getWalls());
                    gameData.getWagon().getDoorLeft().teleport(gameData.getPlayer());
                } else {
                    EntitiesLogic.setPursuers(gameData.getLeftWagonPursuers());
                    gameData.getIsometric().initialiseWagon(trainWagon);
                    gameData.getIsometric().updateAll();
                    trainWagon.setObstacles(gameData.getIsometric().getWalls());
                    gameData.getWagon().getDoorRight().teleport(gameData.getPlayer());
                }
                gameData.setWagon(trainWagon);
                gameData.getPlayer().setCurrentWagon(trainWagon);
                return;
            }
        }
    }
    //endregion

    //region Wagon generation methods
    /**
     * Generate the next wagon and open the door.
     * @param door door to be opened
     */
    private static void generateNextWagonAndOpenDoor(Door door) {
        String wagonType = RandomHandler.getRandomWagonType();
        Wagon nextWagon = new Wagon(gameData.getTrain().findMaxWagonId() + 1, wagonType);

        if (door == gameData.getWagon().getDoorLeft()) {
            generateLeftWagon(nextWagon);
        } else if (door == gameData.getWagon().getDoorRight()) {
            generateRightWagon(nextWagon);
        }

        setEntitiesPositions(nextWagon);
    }

    /**
     * Generate the left wagon.
     * @param nextWagon next wagon to be generated
     */
    private static void generateLeftWagon(Wagon nextWagon) {
        logger.debug("Generating left wagon...");
        EntitiesLogic.setPursuers(gameData.getRightWagonPursuers());

        nextWagon.generateNextWagon(gameData.getWagon(), true);
        gameData.getTrain().addWagon(nextWagon);

        if (gameData.getWagon().getId() == Constants.TRAIN_WAGONS - 3 && !Events.isGrandmotherSpawned()) {
            logger.debug("Generating grandmother in wagon " + nextWagon.getId() + "...");

            String wagonType = RandomHandler.getRandomWagonType();
            Wagon nextNextWagon = new Wagon(gameData.getTrain().findMaxWagonId() + 1, wagonType);
            nextNextWagon.generateNextWagon(nextWagon, true);
            gameData.getTrain().addWagon(nextNextWagon);

            gameData.getIsometric().initialiseWagon(nextNextWagon);
            gameData.getIsometric().updateAll();
            nextNextWagon.setObstacles(gameData.getIsometric().getWalls());

            EntitiesLogic.spawnGrandmother(nextNextWagon);
        }

        gameData.getIsometric().initialiseWagon(nextWagon);
        gameData.getIsometric().updateAll();
        nextWagon.setObstacles(gameData.getIsometric().getWalls());
        nextWagon.getDoorRight().unlock();// Player already unlocked the door (locked door can spawn when generating the wagon)

        gameData.getWagon().getDoorLeft().teleport(gameData.getPlayer());
        gameData.setWagon(nextWagon);
        gameData.getPlayer().setCurrentWagon(gameData.getWagon());
        // Update all
        gameData.getIsometric().setEntities(gameData.getWagon().getEntities());
        gameData.getIsometric().updateAll();
    }

    /**
     * Generate the right wagon.
     * @param nextWagon next wagon to be generated
     */
    private static void generateRightWagon(Wagon nextWagon) {
        logger.debug("Generating right wagon...");
        EntitiesLogic.setPursuers(gameData.getLeftWagonPursuers());

        nextWagon.generateNextWagon(gameData.getWagon(), false);
        gameData.getTrain().addWagon(nextWagon);

        gameData.getIsometric().initialiseWagon(nextWagon);
        gameData.getIsometric().updateAll();
        nextWagon.setObstacles(gameData.getIsometric().getWalls());
        nextWagon.getDoorLeft().unlock();// Same as above

        gameData.getWagon().getDoorRight().teleport(gameData.getPlayer());
        gameData.setWagon(nextWagon);
        gameData.getPlayer().setCurrentWagon(gameData.getWagon());
        // Update all
        gameData.getIsometric().setEntities(gameData.getWagon().getEntities());
        gameData.getIsometric().updateAll();
    }

    public static void setEntitiesPositions(Wagon wagon) {
        logger.debug("Setting entities positions...");
        if (!wagon.getEntities().isEmpty()) {
            if (wagon.getEntities().getFirst().getPositionX() == 0) {
                int counter = 0;
                for (cs.cvut.fel.pjv.gamedemo.common_classes.Object[] objects : wagon.getObjectsArray()) {
                    for (Object object : objects) {
                        if (object != null) {
                            if (object.getHeight() == 0) {
                                String letterId = object.getTwoLetterId();
                                if (letterId.equals(Constants.ENEMY_SPAWN) || letterId.equals(Constants.VENDOR_SPAWN) || letterId.equals(Constants.NPC_SPAWN) || letterId.equals(Constants.QUEST_SPAWN)) {
                                    wagon.getEntities().get(counter).setPositionX(object.getIsoX() - 32);
                                    wagon.getEntities().get(counter).setPositionY(object.getIsoY() - 80);
                                    logger.debug("Entity position set: " + wagon.getEntities().get(counter).getName() + " in wagon " + wagon.getId() + " at " + wagon.getEntities().get(counter).getPositionX() + ", " + wagon.getEntities().get(counter).getPositionY());
                                    counter++;
                                }
                            }
                        }
                    }
                }
            }
        }
        gameData.getIsometric().setEntities(wagon.getEntities());
        gameData.getIsometric().updateAll();
    }
    //endregion

    //endregion
}
