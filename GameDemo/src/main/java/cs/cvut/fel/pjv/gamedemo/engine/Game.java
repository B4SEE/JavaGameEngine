package cs.cvut.fel.pjv.gamedemo.engine;

import cs.cvut.fel.pjv.gamedemo.CustomMapLoader;
import cs.cvut.fel.pjv.gamedemo.engine.utils.Atmospheric;
import cs.cvut.fel.pjv.gamedemo.engine.utils.GUI;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class Game {
    private static final Logger logger = LogManager.getLogger(Game.class);
    private static Stage stage;
    private static final GameSaver game = new GameSaver();
    public static void setStage(Stage stage) {
        Game.stage = stage;
    }
    public static void start() {
        logger.info("Starting game...");
        game.setStage(stage);
        stage.setScene(showMainMenuScene());
        stage.show();
    }
    public static Scene showMainMenuScene() {
        logger.info("Showing main menu scene...");

        Atmospheric.playPauseScreenMusic();

        GUI.initMainMenu();

        GUI.startNewGameButton.setOnAction(actionEvent -> {
            Atmospheric.stopPauseScreenMusic();
            game.resetGame();
            game.setStage(stage);
            game.startGame();
        });

        GUI.loadGameButton.setOnAction(actionEvent -> {
            game.resetGame();
            game.setStage(stage);
            game.loadGame();
            game.startGame();
        });

        GUI.mapLoadButton.setOnAction(actionEvent -> {
            CustomMapLoader mapLoader = new CustomMapLoader();
            try {
                mapLoader.start(stage);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        GUI.exitButton.setOnAction(actionEvent -> System.exit(0));

        return GUI.mainMenuScene;
    }
    public static GameSaver getGameSaver() {
        return game;
    }
}
