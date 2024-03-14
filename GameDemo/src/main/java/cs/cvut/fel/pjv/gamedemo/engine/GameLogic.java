package cs.cvut.fel.pjv.gamedemo.engine;

import cs.cvut.fel.pjv.gamedemo.common_classes.Constants;
import cs.cvut.fel.pjv.gamedemo.common_classes.Player;
import cs.cvut.fel.pjv.gamedemo.common_classes.Wagon;
import javafx.animation.AnimationTimer;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class GameLogic {
    private Isometric isometric;
    private Stage stage;
    private long time;
    private AnimationTimer timer = new AnimationTimer() {
        final long INTERVAL = 1_000_000_000_000L;
        long lastTime = -1;
        /**
         * Updates the game state every INTERVAL nanoseconds.
         * @param l current time in nanoseconds
         */
        @Override
        public void handle(long l) {
            time = l / (INTERVAL / 1000);
            if (lastTime < 0) {
                lastTime = l;
            } else if (l - lastTime < INTERVAL) {
                isometric.updateWalls();

//                isometric.showHealth();

                isometric.updateEntities();

                isometric.updatePlayerPosition();

                isometric.updateTime(time);

//                if (time % 2 == 0) {
//                    //check if player can interact with object
//
//                }
                if (isometric.checkIfPlayerCanInteract()) {
                    System.out.println("Player can interact");
                }

                if (isometric.checkEntities()) {
                    isometric.setHandleToNull();
                    stopGame();
                }
            }
        }
    };
    public GameLogic(Stage stage) {
        isometric = new Isometric();
        this.stage = stage;
    }
    /**
     * Restarts the game.
     */
    public void restartGame() {
        isometric.reset();
        timer.start();
    }
    /**
     * Starts the game.
     */
    public void start() {
        isometric.start();
        timer.start();
    }
    /**
     * Pauses the game.
     */
    public void pauseGame() {
        timer.stop();
    }
    /**
     * Resumes the game.
     */
    public void resumeGame() {
        timer.start();
    }
    /**
     * Ends the game.
     */
    public void endGame() {
        timer.stop();
        stage.close();
        System.exit(0);
    }
    /**
     * Stops the game.
     * Shows the death scene.
     * Allows the player to restart the game or exit to the main menu.
     * @see #mainMenu()
     */
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

    /**
     * Loads the game.
     * @param player player
     * @param wagon wagon to be loaded
     */
    public void loadGame(Player player, Wagon wagon) {
        if (player == null || wagon == null) {
            throw new IllegalArgumentException("Invalid input");
        }

        //question: entities represented by wagon in array, how can I add new entities to the wagon?
        //should I make fixed size of the array for maximum entities count and then add entities to the array?
        //or should I make the array dynamic and add entities to the array? *

        //is my code even correct?

        isometric.initialiseStage(stage);
        isometric.setPlayer(player);
        wagon.generateWagon();
        System.out.println(wagon.getSeed());
        isometric.initialiseWagon(wagon);
    }
    /**
     * Saves the game.
     */
    public void saveGame() {
    }
    /**
     * Shows the main menu.
     */
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

        Button exitButton = new Button("Exit");
        exitButton.setOnAction(actionEvent -> {
            endGame();
        });
        exitButton.setStyle("-fx-font-size: 20; -fx-text-fill: #e31111;");
        grid.add(exitButton, 0, 1);
    }
}
