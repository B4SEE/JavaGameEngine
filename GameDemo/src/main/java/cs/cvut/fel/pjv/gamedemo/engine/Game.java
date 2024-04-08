package cs.cvut.fel.pjv.gamedemo.engine;

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
        stage.setScene(getMainMenuScene());
        stage.show();
    }
    public static Scene getMainMenuScene() {
        GridPane grid = new GridPane();
        grid.setStyle("-fx-background-color: #000000; -fx-border-color: #ffffff;");
        grid.setPrefSize(800, 800);

        Label label = new Label("Main menu");
        label.setStyle("-fx-font-size: 50; -fx-text-fill: #f55757;");

        grid.add(label, 0, 0);
        Scene mainMenuScene = new Scene(grid, Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);

        Button startNewGameButton = new Button("Start new game");
        startNewGameButton.setOnAction(actionEvent -> {
            game.resetGame();
            game.setStage(stage);
            game.startGame();
        });

        Button loadGameButton = new Button("Load game");
        loadGameButton.setOnAction(actionEvent -> {
            game.resetGame();
            game.setStage(stage);
            game.loadGame();
            game.startGame();
        });

        Button exitButton = new Button("Exit");
        exitButton.setOnAction(actionEvent -> {
            System.exit(0);
        });

        startNewGameButton.setStyle("-fx-font-size: 20; -fx-text-fill: #e38f11;");
        loadGameButton.setStyle("-fx-font-size: 20; -fx-text-fill: #70e311;");
        exitButton.setStyle("-fx-font-size: 20; -fx-text-fill: #e31111;");

        grid.add(startNewGameButton, 0, 1);
        grid.add(loadGameButton, 0, 2);
        grid.add(exitButton, 0, 3);

        return mainMenuScene;
    }
}
