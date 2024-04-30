package cs.cvut.fel.pjv.gamedemo.engine.gamelogic;

import cs.cvut.fel.pjv.gamedemo.common_classes.Constants;
import cs.cvut.fel.pjv.gamedemo.common_classes.Entity;
import cs.cvut.fel.pjv.gamedemo.common_classes.Player;
import cs.cvut.fel.pjv.gamedemo.common_classes.Wagon;
import cs.cvut.fel.pjv.gamedemo.engine.Events;
import cs.cvut.fel.pjv.gamedemo.engine.Isometric;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class UpdateLogic {
    private static GameData gameData;
    public static void setGameData(GameData gameData) {
        UpdateLogic.gameData = gameData;
    }
    //region Update methods
    /**
     * Update the game state.
     */
    public static void updateGame() {
        gameData.getIsometric().updateWalls();
        updateEntities();
        updatePlayer();
        if (!gameData.getPlayer().isAlive()) {
            gameData.getIsometric().setHandleToNull();
            GameManagement.stopGame();
        }
    }

    /**
     * Update player's position and stats.
     */
    private static void updatePlayer() {
        gameData.getIsometric().updatePlayerPosition();
        gameData.getIsometric().drawPlayerMainHandSlot(gameData.getPlayer().getHandItem());
        gameData.getPlayer().heal(gameData.getTime());
        gameData.getPlayer().starve(gameData.getTime());
        GameLogicVisuals.showHint();
    }

    /**
     * Update the entities' previous positions to check for stuck entities.
     */
    public static void updateEntitiesPrevPos() {
        Wagon wagon = gameData.getWagon();
        Entity conductor = gameData.getConductor();
        Entity grandmother = gameData.getGrandmother();

        wagon.getEntities().stream()
                .filter(entity -> entity != null && entity.isAlive())
                .forEach(entity -> {
                    entity.setCounter(0);
                    entity.getPreviousPositions().clear();
                });
        Stream.of(conductor, grandmother)
                .filter(Objects::nonNull)
                .forEach(entity -> {
                    entity.setCounter(0);
                    entity.getPreviousPositions().clear();
                });
    }

    /**
     * Update the entities.
     */
    public static void updateEntities() {
        Wagon wagon = gameData.getWagon();
        Player player = gameData.getPlayer();
        long time = gameData.getTime();
        Isometric isometric = gameData.getIsometric();
        Entity boss = gameData.getBoss();

        List<Entity> entities = wagon.getEntities();
        if (entities == null) return;

        List<Entity> entitiesToRemove = new ArrayList<>();
        boolean shouldRemove = false;
        for (Entity entity : entities) {
            if (entity == null) continue;

            if (!entity.isAlive()) {
                if (entity.getType() == Constants.EntityType.GUARD) {
                    Events.setPlayerKilledGuard(true);
                    Events.setGuardCalled(false);
                }
                entitiesToRemove.add(entity);
                shouldRemove = true;
                continue;
            }
            if (entity.getNegativeCount() >= entity.getNegativeThreshold()) {
                EntitiesLogic.handleResponse(entity);
            }
            if (entity.getBehaviour() != Constants.Behaviour.NEUTRAL) {
                if (entity.getBehaviour() == Constants.Behaviour.AGGRESSIVE) {
                    entity.tryAttack(entity, List.of(player), time);
                }
                if (entity.getBehaviour() == Constants.Behaviour.BULLY) {
                    EntitiesLogic.handleBully(entity);
                }
            }
        }
        if (shouldRemove) {
            wagon.getEntities().removeAll(entitiesToRemove);
            isometric.setEntities(wagon.getEntities());
            isometric.updateAll();
        }
        EntitiesMovementLogic.moveEntities();
        if (boss != null) {
            EntitiesLogic.handleBoss();
        }
    }
    //endregion
}
