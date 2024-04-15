package cs.cvut.fel.pjv.gamedemo.engine;

import cs.cvut.fel.pjv.gamedemo.CustomMapLoader;
import cs.cvut.fel.pjv.gamedemo.common_classes.Constants;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class Game {
    private static Stage stage;
    private static final GameSaver game = new GameSaver();
    public static void setStage(Stage stage) {
        Game.stage = stage;
    }
    public static void start() {
        game.setStage(stage);
        stage.setScene(showMainMenuScene());
        stage.show();
    }
    public static Scene showMainMenuScene() {
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

        GUI.exitButton.setOnAction(actionEvent -> {
            System.exit(0);
        });

        return GUI.mainMenuScene;
    }
    public static GameSaver getGameSaver() {
        return game;
    }
}
