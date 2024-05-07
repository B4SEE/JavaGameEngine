package cs.cvut.fel.pjv.gamedemo.engine.gamelogic;

import cs.cvut.fel.pjv.gamedemo.common_classes.Constants;
import cs.cvut.fel.pjv.gamedemo.common_classes.Door;
import cs.cvut.fel.pjv.gamedemo.common_classes.Entity;
import cs.cvut.fel.pjv.gamedemo.common_classes.Object;
import cs.cvut.fel.pjv.gamedemo.engine.utils.Checker;
import cs.cvut.fel.pjv.gamedemo.engine.Events;
import javafx.scene.shape.Shape;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class EntitiesMovementLogic {
    private static final Logger logger = LogManager.getLogger(EntitiesMovementLogic.class);
    private static GameData gameData;

    public static void setGameData(GameData gameData) {
        System.out.println("Setting game data in EntitiesMovementLogic");
        EntitiesMovementLogic.gameData = gameData;
    }

    //region Entities movement methods
    /**
     * Move the entities towards the player.
     * Handle the entities' intelligence.
     * Check if the entity is stuck.
     * @see #intelligenceZeroPursue(Entity, Entity)
     * @see #intelligenceOnePursue(Entity, Entity)
     * @see #intelligenceTwoPursue(Entity, Entity)
     * @see #moveEntity(Entity, int[])
     */
    public static void moveEntities() {
        List<Entity> entities = gameData.getWagon().getEntities();
        if (entities == null) return;

        for (Entity entity : entities) {
            if (entity != null && entity.isAlive() && !Objects.equals(entity.getBehaviour(), Constants.Behaviour.NEUTRAL) && !Events.isPlayerKidnapped()) {
                if (Checker.checkIfEntityRemember(entity, gameData.getPlayer(), gameData.getIsometric().getTwoAndTallerWalls(), gameData.getTime())) {
                    switch (entity.getIntelligence()) {
                        case Constants.Intelligence.DEFAULT -> intelligenceZeroPursue(entity, gameData.getPlayer());
                        case Constants.Intelligence.MEDIUM -> intelligenceOnePursue(entity, gameData.getPlayer());
                        case Constants.Intelligence.HIGH -> intelligenceTwoPursue(entity, gameData.getPlayer());
                        default -> { return; }
                    }
                    if (Checker.checkIfEntityStuck(entity)) {
                        intelligenceZeroPursue(entity, gameData.getPlayer());
                    }
                } else {
                    returnToStart(gameData.getWagon().getMapForPathFinder(false), entity);
                }
            }
        }
        if (Events.getCurrentEvent() != Constants.Event.TRAP_EVENT) {
            // Move pursuers and conductors only if there is no event
            if (!gameData.getLeftWagonPursuers().isEmpty()) {
                movePursuers(gameData.getLeftWagonPursuers(), gameData.getLeftWagonPursuers().getFirst().getCurrentWagon().getDoorRight());
            }
            if (!gameData.getRightWagonPursuers().isEmpty()) {
                movePursuers(gameData.getRightWagonPursuers(), gameData.getRightWagonPursuers().getFirst().getCurrentWagon().getDoorLeft());
            }
            moveConductor(gameData.getConductor());
            moveConductor(gameData.getGrandmother());
        }
    }

    /**
     * Move the entity.
     * @param entity entity to be moved
     * @param index index of the next position found by the pathfinder
     */
    private static void moveEntity(Entity entity, int[] index) {
        if (index == null || index.length == 0) {
            return;
        }

        Object object = gameData.getWagon().getObjectsArray()[index[0]][index[1]];

        int x = (int) object.getIsoX();
        int y = (int) object.getIsoY() + (object.getHeight() != 0 ? object.getHeight() * Constants.TILE_HEIGHT : 0);

        double deltaX = ((entity.getPositionX() + 32) - x);
        double deltaY = ((entity.getPositionY() + 80) - y);

        deltaX = deltaX > 0 ? -1 : 1;
        deltaY = deltaY > 0 ? -1 : 1;

        if (entity.getCurrentWagon() != gameData.getPlayer().getCurrentWagon()) {
            moveEntityInOtherWagon(entity, (int) deltaX, (int) deltaY);
        } else {
            gameData.getIsometric().updateEntityPosition(entity, deltaX, deltaY, entity.getSpeedX(), entity.getSpeedX());
        }
    }

    /**
     * Move the entity in another wagon (handle entity movement in other than the player's wagon).
     * @param entity entity to be moved
     * @param deltaX delta x
     * @param deltaY delta y
     */
    private static void moveEntityInOtherWagon(Entity entity, int deltaX, int deltaY) {
        Shape oldWalls = gameData.getIsometric().getWalls();
        gameData.getIsometric().setWalls(entity.getCurrentWagon().getObstacles());
        gameData.getIsometric().updateEntityPosition(entity, deltaX, deltaY, entity.getSpeedX(), entity.getSpeedX());
        gameData.getIsometric().setWalls(oldWalls);
    }

    /**
     * Move the pursuers to the next wagon.
     * @param pursuers pursuers to be moved
     * @param wagonDoor door to be opened
     */
    private static void movePursuers(List<Entity> pursuers, Door wagonDoor) {
        // Get copy of the list to avoid ConcurrentModificationException
        List<Entity> wagonPursuers = new LinkedList<>(pursuers);
        for (Entity pursuer : wagonPursuers) {
            if (pursuer != null) {
                if (gameData.getPlayer().getCurrentWagon() == pursuer.getCurrentWagon()) {
                    pursuers.remove(pursuer);
                    continue;
                }
                if (Checker.checkCollision(pursuer.getAttackRange(), wagonDoor.getObjectHitbox())) {
                    logger.info("Teleporting pursuer to the next wagon...");
                    wagonDoor.teleport(pursuer);
                    pursuers.remove(pursuer);
                    pursuer.getCurrentWagon().getEntities().remove(pursuer);
                    pursuer.setCurrentWagon(gameData.getWagon());
                    gameData.getWagon().addEntity(pursuer);
                    gameData.getIsometric().setEntities(gameData.getWagon().getEntities());
                    gameData.getIsometric().updateAll();
                    continue;
                }
                EntitiesMovementLogic.moveTowardsDoor(pursuer, wagonDoor);
            }
        }
    }

    /**
     * Move conductor.
     * @param conductor conductor to be moved
     */
    private static void moveConductor(Entity conductor) {
        if (conductor != null) {
            Door wagonDoor = conductor.getCurrentWagon().getDoorLeft();
            if (Checker.checkCollision(conductor.getAttackRange(), wagonDoor.getObjectHitbox())) {
                if (wagonDoor.isLocked()) {
                    wagonDoor.unlock();
                }
                if (wagonDoor.getTargetId() == -1) {
                    EntitiesLogic.generateNextWagonByConductor(conductor);
                }
                // Conductor will not move to the next wagon until checks player's ticket
                if (conductor == gameData.getConductor()) {
                    if (conductor.getCurrentWagon() != gameData.getPlayer().getCurrentWagon()) {//for the conductor
                        EntitiesLogic.openExistingWagonDoorByConductor(conductor);
                    }
                } else {
                    EntitiesLogic.openExistingWagonDoorByConductor(conductor);
                }
            }
            if (conductor == gameData.getConductor()) {
                EntitiesLogic.handleConductor();
            }
            if (conductor == gameData.getGrandmother()) {
                EntitiesLogic.handleGrandmother();
            }
        }
    }
    //endregion

    //region Entities pursue methods
    /**
     * Move the entity towards the target with intelligence 0. The entity moves towards the target in a straight line and does not avoid obstacles.
     * @param entity entity to be moved
     * @param target target to be followed
     */
    public static void intelligenceZeroPursue(Entity entity, Entity target) {
        double deltaX = entity.getPositionX() - target.getPositionX();
        double deltaY = entity.getPositionY() - target.getPositionY();

        deltaX = deltaX > 0 ? -1 : 1;
        deltaY = deltaY > 0 ? -1 : 1;

        gameData.getIsometric().updateEntityPosition(entity, deltaX, deltaY, entity.getSpeedX(), entity.getSpeedX());
    }

    /**
     * Move the entity towards the target with intelligence 1. The entity moves towards the target and avoids obstacles.
     * @param entity entity to be moved
     * @param target target to be followed
     */
    public static void intelligenceOnePursue(Entity entity, Entity target) {
        int[][] map = gameData.getWagon().getMapForPathFinder(false);
        int[][] path = entity.findPath(map, target.findOnWhatObject());
        pursue(entity, target, map, path);
    }

    /**
     * Move the entity towards the target with intelligence 2. The entity moves towards the target, avoids obstacles and opens the door if the entity is near.
     * @param entity entity to be moved
     * @param target target to be followed
     */
    public static void intelligenceTwoPursue(Entity entity, Entity target) {
        int[][] map = gameData.getWagon().getMapForPathFinder(true);
        int[][] path = entity.findPath(map, target.findOnWhatObject());

        if (Checker.checkIfCanInteract(entity, gameData.getWagon().getInteractiveObjects()) != null) {
            cs.cvut.fel.pjv.gamedemo.common_classes.Object object = Checker.checkIfCanInteract(entity, gameData.getWagon().getInteractiveObjects());
            if (Objects.equals(object.getTwoLetterId(), Constants.LOCKABLE_DOOR) && object.isSolid()) {
                WagonLogic.useLockableDoor(object);
            }
        }
        pursue(entity, target, map, path);
    }

    /**
     * Move the entity towards the target.
     * @param entity entity to be moved
     * @param target target to be followed
     * @param map map for the returnToStart() method
     * @param path path to be followed
     * @see #returnToStart(int[][], Entity)
     */
    private static void pursue(Entity entity, Entity target, int[][] map, int[][] path) {
        if (Checker.checkCollision(entity.getAttackRange(), target.getAttackRange())) {
            intelligenceZeroPursue(entity, target);
        } else {
            int[] deltaXY = getDeltaXY(entity, path, map);
            moveEntity(entity, deltaXY);
        }
    }
    //endregion

    //region Helper methods
    /**
     * Move the entity back to the start position.
     * @param map map for the pathfinder
     * @param entity entity to be moved
     */
    private static void returnToStart(int[][] map, Entity entity) {
        if (entity.getPositionX() == entity.getStartPositionX() && entity.getPositionY() == entity.getStartPositionY()) {
            return;
        }
        int[][] path = entity.findPath(map, entity.getStartIndex());
        if (path == null || path.length == 0) {
            return;
        }
        int[] deltaXY;
        if (path.length >= 2) {
            deltaXY = path[path.length - 2];
        } else {
            deltaXY = path[0];
        }
        moveEntity(entity, deltaXY);
    }

    /**
     * Get the delta x and delta y for the entity.
     * @param entity entity to be moved
     * @param path path to be followed
     * @param map map for the pathfinder
     * @return delta x and delta y
     */
    private static int[] getDeltaXY(Entity entity, int[][] path, int[][] map) {
        int[] deltaXY = new int[0];
        if (path.length >= 2) {
            deltaXY = path[path.length - 2];
        } else if (path.length == 1) {
            deltaXY = path[0];
        } else {
            if (Checker.checkIfEntityRemember(entity, gameData.getPlayer(), gameData.getIsometric().getTwoAndTallerWalls(), gameData.getTime())) {
                // If the entity remembers the player, stay still (wait for the player)
                deltaXY = entity.findOnWhatObject();
            } else {
                returnToStart(map, entity);
            }
        }
        return deltaXY;
    }

    /**
     * Move entity towards the door.
     * @param entity entity to be moved
     * @param wagonDoor door to be reached
     */
    public static void moveTowardsDoor(Entity entity, Door wagonDoor) {
        for (int i = 0; i < entity.getCurrentWagon().getObjectsArray().length; i++) {
            for (int j = 0; j < entity.getCurrentWagon().getObjectsArray()[i].length; j++) {
                if (entity.getCurrentWagon().getObjectsArray()[i][j] == wagonDoor) {
                    int[] targetIndex = {i, j};
                    int[][] path = entity.findPath(entity.getCurrentWagon().getMapForPathFinder(true), targetIndex);
                    int[] deltaXY = getDeltaXY(entity, path, entity.getCurrentWagon().getMapForPathFinder(true));
                    moveEntity(entity, deltaXY);
                }
            }
        }
    }
    //endregion
}
