package cs.cvut.fel.pjv.gamedemo.engine;

import cs.cvut.fel.pjv.gamedemo.common_classes.Constants;
import cs.cvut.fel.pjv.gamedemo.common_classes.Entity;
import cs.cvut.fel.pjv.gamedemo.common_classes.Player;
import javafx.animation.AnimationTimer;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public class GameLogic {
    private Isometric isometric;
    private Stage stage;
    private long time;
    private AnimationTimer timer = new AnimationTimer() {
        final long INTERVAL = 1_000_000_000_000L;
        long lastTime = -1;
        /**
         * Updates the game state every INTERVAL nanoseconds.
         * Updates the walls and player position.
         * If the last time is not set, sets it to the current time.
         * If the time difference between the last time and the current time is less than INTERVAL,
         * updates the walls, shows the cursor coordinates, and updates the player position.
         * @param l current time in nanoseconds
         */
        @Override
        public void handle(long l) {
            time = l / (INTERVAL / 1000);
            if (lastTime < 0) {
                lastTime = l;
            } else if (l - lastTime < INTERVAL) {
                isometric.updateWalls();

                isometric.showHealth();

                isometric.updateEntities();

                isometric.updatePlayerPosition();

                isometric.updateTime(time);

                if (isometric.checkEntities()) {
                    isometric.setHandleToNull();
                    stopGame();
                }
            }
        }
    };
    public void restartGame() {
        isometric.reset();
        timer.start();
    }
    public GameLogic(Stage stage) {
        isometric = new Isometric();
        this.stage = stage;
    }
    public void start() {
        isometric.start();
        timer.start();
    }
    public void pauseGame() {
        timer.stop();
    }
    public void resumeGame() {
        isometric.resumeGame();
        timer.start();
    }
    public void endGame() {
        timer.stop();
        stage.close();
    }
    public void stopGame() {
        timer.stop();
        //save game scene
        Scene isoScene = stage.getScene();
        //create grid and scene for death
        GridPane grid = new GridPane();
        grid.setStyle("-fx-background-color: #000000; -fx-border-color: #ffffff;");
        grid.setPrefSize(800, 800);
        Label label = new Label();
        label.setText("Game over, press ENTER to exit, R to restart");
        label.setLayoutX(500);
        label.setLayoutY(500);
        label.setStyle("-fx-font-size: 50; -fx-text-fill: #ffffff;");
        grid.getChildren().add(label);
        Scene scene = new Scene(grid, Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
        //show death scene
        stage.setScene(scene);
        scene.onKeyPressedProperty().set(keyEvent -> {
            if (keyEvent.getCode().toString().equals("ENTER")) {
                //reset scene, new will be set in mainMenu()
                stage.setScene(null);
                mainMenu();
            }
            if (keyEvent.getCode().toString().equals("R")) {
                //set game scene
                stage.setScene(isoScene);
                restartGame();
            }
        });
    }
    public void loadGame(Player player, Entity[] entities, String mapPath) {
        if (player == null || mapPath == null) {
            throw new IllegalArgumentException("Invalid input");
        }
        isometric.initialiseStage(stage);
        isometric.setPlayer(player);
        if (entities != null) {
            isometric.setEntities(entities);
        }
        isometric.setMap(mapPath);
        start();
    }
    public void saveGame() {
    }
    public void mainMenu() {
        isometric.clearAll();
        GridPane grid = new GridPane();
        grid.setStyle("-fx-background-color: #000000; -fx-border-color: #ffffff;");
        grid.setPrefSize(800, 800);
        Label label = new Label("Main menu");
        label.setStyle("-fx-font-size: 50; -fx-text-fill: #f55757;");
        grid.add(label, 0, 0);
        Scene mainMenuScene = new Scene(grid, Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
        stage.setScene(mainMenuScene);
    }
}
