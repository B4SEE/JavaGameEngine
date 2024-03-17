package cs.cvut.fel.pjv.gamedemo.engine;

import cs.cvut.fel.pjv.gamedemo.common_classes.*;
import cs.cvut.fel.pjv.gamedemo.common_classes.Object;
import javafx.animation.AnimationTimer;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GameLogic {
    private Isometric isometric;
    //test
    private boolean added = false;
    private Stage stage;
    private long time;
    private Player player;
    private Wagon wagon;
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

                isometric.updateEntities();

                isometric.updatePlayerPosition();

                isometric.updateTime(time);

                if (isometric.checkIfPlayerCanInteract() != null) {
                    isometric.updateHint("Press E to interact", player.getPositionX(), player.getPositionY() - 25);
                } else {
                    isometric.updateHint("Player: " + player.getHealth() + " HP, " + player.getHunger() + " hunger", player.getPositionX(), player.getPositionY() - 25);
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
        setPlayerHandle();
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
        stage.setScene(scene);
        scene.onKeyPressedProperty().set(keyEvent -> {
            if (keyEvent.getCode().toString().equals("ENTER")) {
                stage.setScene(null);
                mainMenu();
            }
            if (keyEvent.getCode().toString().equals("R")) {
                stage.setScene(isoScene);
                restartGame();
            }
        });
    }

    private void setPlayerHandle() {
        Scene scene = stage.getScene();
        scene.setOnKeyReleased(keyEvent -> {
            switch (keyEvent.getCode()) {
                case W, S:
                    isometric.updatePlayerDeltaY(0);
                    break;
                case A, D:
                    isometric.updatePlayerDeltaX(0);
                    break;
            }
        });
        scene.setOnKeyPressed(keyEvent -> {
            switch (keyEvent.getCode()) {
                case W:
                    isometric.updatePlayerTexture("player_back.png");
                    isometric.updatePlayerDeltaY(-Constants.PLAYER_BASIC_SPEED_Y);
                    break;
                case S:
                    isometric.updatePlayerTexture("player_front.png");
                    isometric.updatePlayerDeltaY(Constants.PLAYER_BASIC_SPEED_Y);
                    break;
                case A:
                    isometric.updatePlayerTexture("player_left.png");
                    isometric.updatePlayerDeltaX(-Constants.PLAYER_BASIC_SPEED_X);
                    break;
                case D:
                    isometric.updatePlayerTexture("player_right.png");
                    isometric.updatePlayerDeltaX(Constants.PLAYER_BASIC_SPEED_X);
                    break;
                case R:
                    player.tryAttack(player, isometric.getEntities(), time);
                    break;
                case TAB:
                    setPlayerInventoryHandle();
                    break;
                case E:
                    if (isometric.checkIfPlayerCanInteract() != null) {
                        Object object = isometric.checkIfPlayerCanInteract();
//                        if (object.equals("WD")) {
//                            openDoor();
//                        }
                        if (Objects.equals(object.getTwoLetterId(), "CO")) {
                            setObjectInventoryHandle(object);
                        } else if (Objects.equals(object.getTwoLetterId(),"LD")) {
                            useLockableDoor(object);
                            isometric.updateAll();
                        }
                    }
            }
            isometric.updateWalls();
        });
    }
    private void useLockableDoor(Object object) {
        if (object.isSolid()) {
            object.setIsSolid(false);
            object.setTexturePath("lockable_door_opened.png");
        } else {
            object.setIsSolid(true);
            object.setTexturePath("lockable_door_closed.png");
        }
    }

    private void setObjectInventoryHandle(Object object) {
        Inventory objectInventory = object.getObjectInventory();
        if (objectInventory != null) {

            //for testing purposes
            if (objectInventory.inventorySize >= 3 && !added) {
                objectInventory.setVendor(true);
                Item item = new Item("block", "block_wall.png");
                item.setValue(10);
                Item item2 = new Item("seat", "seat_1.png");
                item2.setValue(20);
                Item item3 = new Item("box", "chest_object_1.png");
                item3.setValue(30);
                objectInventory.addItem(item);
                objectInventory.addItem(item2);
                objectInventory.addItem(item3);
                added = true;
            }
            //

            Scene scene = stage.getScene();
            scene.setOnKeyReleased(null);
            scene.setOnKeyPressed(null);

            timer.stop();

            Scene objectInventoryScene = objectInventory.openInventory();

            stage.setScene(objectInventoryScene);

            objectInventoryScene.setOnKeyPressed(keyEvent -> {
                if (Objects.requireNonNull(keyEvent.getCode()) == KeyCode.TAB) {
                    if (!objectInventory.getTakenItems().isEmpty()) {
                        List<Item> takenItems = objectInventory.getTakenItems();
                        List<Item> itemsToRemove = new ArrayList<>(takenItems);
                        List<Item> addedItems = new ArrayList<>();
                        if (objectInventory.isVendor()) {
                            for (Item item : itemsToRemove) {
                                if (player.getPlayerInventory().addItem(item) && player.getPlayerInventory().getMoney() >= item.getValue()) {
                                    objectInventory.removeTakenItem(item);
                                    player.getPlayerInventory().setMoney(player.getPlayerInventory().getMoney() - item.getValue());
                                    addedItems.add(item);
                                    objectInventory.removeTakenItem(item);
                                } else {
                                    returnItems(addedItems, itemsToRemove, objectInventory);
                                    break;
                                }
                            }
                        } else {
                            for (Item item : itemsToRemove) {
                                if (player.getPlayerInventory().addItem(item)) {
                                    objectInventory.removeTakenItem(item);
                                    addedItems.add(item);
                                    objectInventory.removeTakenItem(item);
                                } else {
                                    returnItems(addedItems, itemsToRemove, objectInventory);
                                    break;
                                }
                            }
                        }
                    }
                    objectInventory.closeInventory(stage);
                    stage.setScene(scene);
                    setPlayerHandle();
                    timer.start();
                }
            });
        }
    }

    private void returnItems(List<Item> addedItems, List<Item> itemsToRemove, Inventory inventory) {
        for (Item remainingItem : itemsToRemove) {
            if (!addedItems.contains(remainingItem)) {
                inventory.addItem(remainingItem);
                inventory.removeTakenItem(remainingItem);
            }
        }
    }

    private void setPlayerInventoryHandle() {
        Scene scene = stage.getScene();
        scene.setOnKeyReleased(null);
        scene.setOnKeyPressed(null);

        timer.stop();

        Scene playerInventory = player.getPlayerInventory().openInventory();
        stage.setScene(playerInventory);

        playerInventory.setOnKeyPressed(keyEvent -> {
            if (Objects.requireNonNull(keyEvent.getCode()) == KeyCode.TAB) {
                player.getPlayerInventory().closeInventory(stage);
                stage.setScene(scene);
                setPlayerHandle();
                timer.start();
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
        this.player = player;
        this.wagon = wagon;

        //question: entities represented by wagon in array, how can I add new entities to the wagon?
        //should I make fixed size of the array for maximum entities count and then add entities to the array?
        //or should I make the array dynamic and add entities to the array? *List

        isometric.initialiseStage(stage);
        isometric.setPlayer(player);
        wagon.generateWagon();
        System.out.println(wagon.getSeed());
        isometric.initialiseWagon(wagon);
        setPlayerHandle();
    }
    /**
     * Saves the game.
     */
    public void saveGame() {
    }

    public Scene previewMap() {
        timer.stop();
        if (player == null || wagon == null) {
            throw new IllegalArgumentException("Invalid input");
        }
        isometric.clearAll();
        isometric.initialiseStage(stage);
        isometric.setPlayer(player);
        isometric.initialiseWagon(wagon);
        return isometric.previewWagon(wagon);
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
