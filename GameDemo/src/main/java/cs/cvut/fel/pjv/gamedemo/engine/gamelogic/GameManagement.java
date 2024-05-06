package cs.cvut.fel.pjv.gamedemo.engine.gamelogic;

import cs.cvut.fel.pjv.gamedemo.common_classes.*;
import cs.cvut.fel.pjv.gamedemo.common_classes.Object;
import cs.cvut.fel.pjv.gamedemo.engine.*;
import cs.cvut.fel.pjv.gamedemo.engine.utils.Atmospheric;
import cs.cvut.fel.pjv.gamedemo.engine.utils.Checker;
import cs.cvut.fel.pjv.gamedemo.engine.utils.GUI;
import javafx.animation.AnimationTimer;
import javafx.scene.Scene;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class GameManagement {
    private static final Logger logger = LogManager.getLogger(GameManagement.class);
    private static GameData gameData;

    public static void setGameData(GameData gameData) {
        GameManagement.gameData = gameData;
    }
    private static final AnimationTimer timer = new AnimationTimer() {
        final long INTERVAL = 1_000_000_000_000L;
        long lastTime = -1;

        /**
         * Update the game state every INTERVAL nanoseconds.
         * @param l current time in nanoseconds
         */
        public void handle(long l) {
            gameData.setTime(l / (INTERVAL / 1000));
            if (lastTime < 0) {
                lastTime = l;
            } else if (l - lastTime < INTERVAL) {

                UpdateLogic.updateGame();

                if (everyNSecondsDo(197)) {
                    Atmospheric.updateBackgroundMusic();
                }

                if (everyNSecondsDo(500)) {
                    String randomEra = Constants.ERAS[(int) (Math.random() * Constants.ERAS.length)];
                    Events.setEra(randomEra);
                }

                if (everyNSecondsDo(5)) {
                    UpdateLogic.updateEntitiesPrevPos();
                    checkAndSetTrap();
                    checkAndHandleDeadEnemies();
                    checkAndHandleGuardCall();
                    checkAndHandleConductorSpawn();
                    checkAndHandleConductorDeath();
                }
            }
        }
    };

    //region Checkers
    /**
     * Check if the trap event should be set and set it.
     */
    private static void checkAndSetTrap() {
        if (Checker.checkIfWagonHasTrap(gameData.getWagon().getObjectsArray())) TrapLogic.generateAndSetTrap();
    }

    /**
     * Check if all enemies are dead and handle trap event.
     */
    private static void checkAndHandleDeadEnemies() {
        if (Events.getCurrentEvent() != Constants.Event.DEFAULT_EVENT) checkIfAllEnemiesAreDead();
    }

    /**
     * Check if the guard should be called and call the guard.
     */
    private static void checkAndHandleGuardCall() {
        if (Events.shouldCallGuard() && !Events.isGuardCalled()) EntitiesLogic.handleGuardCall();
    }

    /**
     * Check if the conductor should spawn and spawn the conductor.
     */
    private static void checkAndHandleConductorSpawn() {
        if (!Events.isConductorSpawned()) EntitiesLogic.handleConductorSpawn();
    }

    /**
     * Check if the conductor is dead and stop the game.
     */
    private static void checkAndHandleConductorDeath() {
        if (Events.isConductorSpawned() && gameData.getConductor() != null && !gameData.getConductor().isAlive()) {
            gameData.setDeathMessage("You are doomed to wander the train forever");
            stopGame();
        }
    }

    /**
     * Check if all entities from trap event are dead and remove the trap if they are.
     */
    private static void checkIfAllEnemiesAreDead() {
        if (Events.getCurrentEvent() == Constants.Event.DEFAULT_EVENT) return;
        if (countEnemies() == 0 || Events.getCurrentEvent() == Constants.Event.TIME_LOOP_EVENT || Events.getCurrentEvent() == Constants.Event.SILENCE_EVENT) {
            TrapLogic.deactivateTrap();// If enemies are dead or the trap was a time loop or silence event then deactivate the trap (unlock the doors)
            TrapLogic.generateReward();// Reward the player
        }
    }
    //endregion

    //region Game management methods
    /**
     * Start the game.
     */
    public static void start() {
        if (gameData.getStage() == null || gameData.getTrain() == null) {
            throw new IllegalArgumentException("Stage or train is null");
        }
        Isometric isometric = gameData.getIsometric();
        Wagon wagon = gameData.getWagon();
        Train train = gameData.getTrain();

        logger.info("Starting game...");
        isometric.start();
        wagon.setObstacles(isometric.getWalls());
        logger.debug("Wagon obstacles set");
        // Update all wagons
        logger.debug("Checking wagons...");
        for (Wagon trainWagon : train.getWagonsArray()) {
            if (trainWagon != null) {
                isometric.initialiseWagon(trainWagon);
                trainWagon.setObstacles(isometric.getWalls());
                for (Entity entity : trainWagon.getEntities()) {
                    if (entity.getType() == Constants.EntityType.CONDUCTOR) {
                        if (entity.getName().equals(Constants.CONDUCTOR)) {
                            Events.setConductorSpawned(true);
                            gameData.setConductor(entity);
                            logger.debug("Found conductor in wagon " + trainWagon.getId());
                        }
                        if (entity.getName().equals(Constants.GRANDMOTHER)) {
                            Events.setConductorSpawned(true);
                            gameData.setGrandmother(entity);
                            logger.debug("Found grandmother in wagon " + trainWagon.getId());
                        }
                    }
                }
                isometric.updateAll();
            }
        }
        isometric.initialiseWagon(wagon);

        WagonLogic.setEntitiesPositions(wagon);

        isometric.setEntities(wagon.getEntities());
        isometric.updateAll();
        timer.start();
        logger.info("Game started");
    }

    /**
     * Restart the game.
     */
    public static void restartGame() {
        resetAll();
        GameSaver game = Game.getGameSaver();
        game.resetGame();
        game.loadGame();
        game.removeUnnecessaryFiles();
        if (game.getPlayer() == null) {
            game.prepareEvents();
            game.createWagon();
            game.createPlayer();
            game.createTrain();
        }
        gameData.setTrain(game.getTrain());
        gameData.setWagon(game.getCurrentWagon());
        gameData.setPlayer(game.getPlayer());
        gameData.setIsometric(new Isometric());
        loadGame(gameData.getPlayer(), gameData.getWagon());
        start();
        logger.info("Game restarted");
    }

    /**
     * Reset all game variables.
     */
    private static void resetAll() {
        gameData.resetAll();
        Atmospheric.resetAll();
        logger.info("Game reset");
    }

    /**
     * Pause the game.
     */
    public static void pauseGame() {
        logger.info("Game paused");
        timer.stop();
    }

    /**
     * Resume the game.
     */
    public static void resumeGame() {
        logger.info("Game resumed");
        timer.start();
    }

    /**
     * Stop the game.
     * Show the death scene.
     * Allow the player to restart the game or exit to the main menu.
     */
    public static void stopGame() {
        pauseGame();
        logger.info("Game stopped");
        Scene isoScene = gameData.getStage().getScene();
        Atmospheric.fadeOutMusic(0.00);
        GUI.initDeathScreen(gameData.getDeathMessage());
        logger.info("Game over with message: " + gameData.getDeathMessage());
        GUI.confirmButton.setOnAction(actionEvent1 -> {
            gameData.getStage().setScene(isoScene);
            restartGame();
        });
        GUI.cancelButton.setOnAction(actionEvent1 -> {
            gameData.getStage().setScene(null);
            GameLogicVisuals.mainMenu();
        });
        gameData.getStage().setScene(GUI.confirmScene);
    }

    /**
     * Load the game.
     * @param player player
     * @param wagon wagon to be loaded
     */
    public static void loadGame(Player player, Wagon wagon) {
        if (gameData.getStage() == null || gameData.getTrain() == null) {
            throw new IllegalArgumentException("Stage or train is null");
        }
        if (player == null || wagon == null) {
            throw new IllegalArgumentException("Invalid input");
        }
        gameData.setPlayer(player);
        gameData.setWagon(wagon);

        player.setCurrentWagon(wagon);

        gameData.getIsometric().initialiseStage(gameData.getStage());
        gameData.getIsometric().setPlayer(gameData.getPlayer());
        gameData.getIsometric().initialiseWagon(gameData.getWagon());
        GameLogicHandlers.setPlayerHandle();
    }
    /**
     * Save the game.
     */
    public static void saveGame() {
        GameSaver game = new GameSaver(gameData.getPlayer(), gameData.getTrain(), gameData.getTrain().indexOf(gameData.getWagon()));
        game.saveGame();
    }
    //endregion

    //region Utility methods
    /**
     * Return true if the time is divisible by n (returns true every n seconds).
     * @param n time interval in seconds
     * @return true if the time is divisible by n
     */
    private static boolean everyNSecondsDo(int n) {
        return gameData.getTime() % n == 0;
    }

    /**
     * Count the number of enemies in the wagon.
     * @return number of enemies in the wagon
     */
    private static int countEnemies() {
        Wagon wagon = gameData.getWagon();
        return (int) wagon.getEntities().stream()
                .filter(entity ->
                        entity != null
                                && entity.isAlive()
                                && (entity.getType() == Constants.EntityType.BOSS || entity.getType() == Constants.EntityType.ENEMY))
                .count();
    }
}
