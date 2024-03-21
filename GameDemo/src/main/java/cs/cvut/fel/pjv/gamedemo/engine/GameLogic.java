package cs.cvut.fel.pjv.gamedemo.engine;

import cs.cvut.fel.pjv.gamedemo.common_classes.*;
import cs.cvut.fel.pjv.gamedemo.common_classes.Object;
import javafx.animation.AnimationTimer;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.Line;
import javafx.scene.shape.Shape;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Class with the main game logic, handles the game state, player input and game events.
 */
public class GameLogic {
    private final Checker checker = new Checker();
    private final Isometric isometric;
    //test
    private boolean added = false;
    private Stage stage;
    private long time;
    private Player player;
    private Wagon wagon;
    private Train train;
    private int gameState = 0;
    private AnimationTimer timer = new AnimationTimer() {
        final long INTERVAL = 1_000_000_000_000L;
        long lastTime = -1;
        /**
         * Update the game state every INTERVAL nanoseconds.
         * @param l current time in nanoseconds
         */
        @Override
        public void handle(long l) {
            time = l / (INTERVAL / 1000);
            if (lastTime < 0) {
                lastTime = l;
            } else if (l - lastTime < INTERVAL) {

                isometric.updateTime(time);

                isometric.updateWalls();

                updateEntities();

                updatePlayer();

                isometric.removeDeadEntities();

                if (!player.isAlive()) {
                    isometric.setHandleToNull();
                    stopGame();
                }
            }
        }
    };
    private void updatePlayer() {
        isometric.updatePlayerPosition();
        player.heal(time);
        player.starve(time);
        if (checker.checkIfPlayerCanInteract(player, wagon.getInteractiveObjects()) != null) {
            isometric.updateHint("Press E to open", (int) player.getPositionX(), (int) player.getPositionY() - 25);
        } else {
            isometric.updateHint("", (int) player.getPositionX(), (int) player.getPositionY() - 25);
        }
    }
    public GameLogic(Stage stage) {
        isometric = new Isometric();
        this.stage = stage;
    }
    /**
     * Restart the game.
     */
    public void restartGame() {
        isometric.reset();
        setPlayerHandle();
        resumeGame();
    }
    /**
     * Start the game.
     */
    public void start() {
        isometric.start();
        timer.start();
    }
    /**
     * Pause the game.
     */
    public void pauseGame() {
        timer.stop();
    }
    /**
     * Resume the game.
     */
    public void resumeGame() {
        timer.start();
    }
    /**
     * End the game.
     */
    public void endGame() {
        pauseGame();
        stage.close();
        System.exit(0);
    }
    /**
     * Stop the game.
     * Show the death scene.
     * Allow the player to restart the game or exit to the main menu.
     * @see #mainMenu()
     */
    public void stopGame() {
        pauseGame();
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

    /**
     * Set the handle for the player movement and interaction.
     */
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
                    playerUseHand();
                    break;
                case TAB:
                    setPlayerInventoryHandle();
                    break;
                case E:
                    if (checker.checkIfPlayerCanInteract(player, wagon.getInteractiveObjects()) != null) {
                        Object object = checker.checkIfPlayerCanInteract(player, wagon.getInteractiveObjects());
                        if (Objects.equals(object.getTwoLetterId(), "WD") && gameState != Constants.GAME_STATES.get("trap")) {
                            openWagonDoor((Door) object);
                        }
                        if (Objects.equals(object.getTwoLetterId(), "CO")) {
                            setInventoryHandle(object.getObjectInventory());
                        }
                        if (Objects.equals(object.getTwoLetterId(),"LD")) {
                            useLockableDoor(object);
                            isometric.updateAll();
                        }
                    }
            }
            isometric.updateWalls();
        });
    }

    private void openWagonDoor(Door door) {
        if (door.getTargetId() == -1) {
//            String[] wagonTypes = {"COMPARTMENT", "RESTAURANT", "SLEEPER", "CARGO", "DEFAULT"};
            String[] wagonTypes = {"CARGO"};
            //select random wagon type
            String wagonType = wagonTypes[(int) (Math.random() * wagonTypes.length)];
            Wagon nextWagon = new Wagon(train.findMaxWagonId() + 1, wagonType, "wall_3.png");
            if (door == wagon.getDoorLeft()) {
                nextWagon.generateNextWagon(wagon, true);

                train.addWagon(nextWagon);

                isometric.initialiseWagon(nextWagon);

                isometric.updateAll();

                wagon.getDoorLeft().teleport(player);

                this.wagon = nextWagon;

                player.setCurrentWagon(wagon);
            } else if (door == wagon.getDoorRight()) {
                nextWagon.generateNextWagon(wagon, false);

                train.addWagon(nextWagon);

                isometric.initialiseWagon(nextWagon);

                isometric.updateAll();

                wagon.getDoorRight().teleport(player);

                this.wagon = nextWagon;

                player.setCurrentWagon(wagon);
            }
        } else {
            for (Wagon wagon : train.wagonsArray) {
                if (wagon.getId() == door.getTargetId()) {
                    if (door == this.wagon.getDoorLeft()) {
                        isometric.initialiseWagon(wagon);
                        isometric.updateAll();
                        this.wagon.getDoorLeft().teleport(player);
                        this.wagon = wagon;
                        player.setCurrentWagon(wagon);
                    } else {
                        isometric.initialiseWagon(wagon);
                        isometric.updateAll();
                        this.wagon.getDoorRight().teleport(player);
                        this.wagon = wagon;
                        player.setCurrentWagon(wagon);
                    }
                    return;
                }
            }
        }
    }

    /**
     * Use player's item in hand: if the player has no item in hand, the player tries to attack the entity,
     * otherwise the player uses the item.
     */
    private void playerUseHand() {
        if (player.getHandItem() == null) {
            player.tryAttack(player, isometric.getEntities(), time);
        }
        if (player.getHandItem() != null) {
            player.useHandItem(time);
        }
    }

    /**
     * Open/close the door.
     * @param object door to be opened/closed
     */
    private void useLockableDoor(Object object) {
        if (object.isSolid()) {
            object.setIsSolid(false);
            object.setTexturePath("default/objects/interactive_objects/lockable_door/lockable_door_1_opened.png");
        } else {
            object.setIsSolid(true);
            object.setTexturePath("default/objects/interactive_objects/lockable_door/lockable_door_1_closed.png");
        }
    }

    /**
     * Set the handle for the inventory: check if the inventory is not null, clear the scene and the player handle,
     * pause the game, set the handle for the inventory.
     * When the inventory is closed, the handle for the player is set again and the game is resumed.
     * @param inventory inventory to be handled
     */
    private void setInventoryHandle(Inventory inventory) {
        if (inventory != null) {

//            //for testing purposes
//            if (inventory.inventorySize >= 3 && !added) {
////                inventory.setVendor(true);
//                Food item = new Food("orange", "orange.png", 15);
//                item.setValue(10);
//                Item item2 = new Item("seat", "seat_1.png");
//                item2.setValue(20);
//                Item item3 = new Item("box", "chest_object_1.png");
//                item3.setValue(30);
//                inventory.addItem(item);
//                inventory.addItem(item2);
//                inventory.addItem(item3);
//                added = true;
//            }
//            //

            Scene scene = stage.getScene();
            scene.setOnKeyReleased(null);
            scene.setOnKeyPressed(null);

            pauseGame();

            Scene objectInventoryScene = inventory.openInventory();

            stage.setScene(objectInventoryScene);

            objectInventoryScene.setOnKeyPressed(keyEvent -> {
                if (Objects.requireNonNull(keyEvent.getCode()) == KeyCode.TAB) {
                    if (!inventory.getTakenItems().isEmpty()) {
                        List<Item> takenItems = inventory.getTakenItems();
                        List<Item> itemsToRemove = new ArrayList<>(takenItems);
                        List<Item> addedItems = new ArrayList<>();
                        if (inventory.isVendor()) {
                            for (Item item : itemsToRemove) {
                                if (player.getPlayerInventory().addItem(item) && player.getPlayerInventory().getMoney() >= item.getValue()) {
                                    inventory.removeTakenItem(item);
                                    player.getPlayerInventory().setMoney(player.getPlayerInventory().getMoney() - item.getValue());
                                    addedItems.add(item);
                                    inventory.removeTakenItem(item);
                                } else {
                                    returnItems(addedItems, itemsToRemove, inventory);
                                    break;
                                }
                            }
                        } else {
                            for (Item item : itemsToRemove) {
                                if (player.getPlayerInventory().addItem(item)) {
                                    inventory.removeTakenItem(item);
                                    addedItems.add(item);
                                    inventory.removeTakenItem(item);
                                } else {
                                    returnItems(addedItems, itemsToRemove, inventory);
                                    break;
                                }
                            }
                        }
                    }
                    inventory.closeInventory(stage);
                    stage.setScene(scene);
                    setPlayerHandle();
                    resumeGame();
                }
            });
        }
    }

    /**
     * Return the items to the inventory if the player inventory is full or the player does not have enough money.
     * @param addedItems items that were added to the player inventory
     * @param itemsToRemove items that were taken from the object inventory
     * @param inventory object inventory
     */
    private void returnItems(List<Item> addedItems, List<Item> itemsToRemove, Inventory inventory) {
        for (Item remainingItem : itemsToRemove) {
            if (!addedItems.contains(remainingItem)) {
                inventory.addItem(remainingItem);
                inventory.removeTakenItem(remainingItem);
            }
        }
    }

    /**
     * Set the handle for the player inventory: clear the scene, clear the player handle, pause the game, set the handle for the player inventory.
     * When the player inventory is closed, the handle for the player is set again and the game is resumed.
     */
    private void setPlayerInventoryHandle() {
        Scene scene = stage.getScene();
        scene.setOnKeyReleased(null);
        scene.setOnKeyPressed(null);

        pauseGame();

        Scene playerInventory = player.getPlayerInventory().openInventory();
        stage.setScene(playerInventory);

        playerInventory.setOnKeyPressed(keyEvent -> {
            if (Objects.requireNonNull(keyEvent.getCode()) == KeyCode.TAB) {
                player.getPlayerInventory().closeInventory(stage);
                stage.setScene(scene);
                setPlayerHandle();
                resumeGame();
            }
        });
    }

    /**
     * Load the game.
     * @param player player
     * @param wagon wagon to be loaded
     */
    public void loadGame(Player player, Wagon wagon) {
        if (player == null || wagon == null) {
            throw new IllegalArgumentException("Invalid input");
        }
        this.player = player;
        this.wagon = wagon;
        this.train = new Train();
        train.addWagon(wagon);

        player.setCurrentWagon(wagon);

        isometric.initialiseStage(stage);
        isometric.setPlayer(player);
        wagon.generateWagon();
        System.out.println(wagon.getSeed());
        isometric.initialiseWagon(wagon);
        setPlayerHandle();
//        System.out.println("------------");
//        System.out.println(train.wagonsArray[0].getSeed());
//        System.out.println("------------");
    }
    /**
     * Save the game.
     */
    public void saveGame() {
    }
    /**
     * Show the main menu.
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
    /**
     * Update the entities.
     */
    public void updateEntities() {
        if (wagon.getEntities() == null) {
            return;
        }
        for (Entity entity : wagon.getEntities()) {
            if (entity != null && entity.isAlive()) {
                if (Objects.equals(entity.getBehaviour(), Constants.NEUTRAL)) {
                    return;
                }
                moveEntities();
                entity.tryAttack(entity, List.of(player), time);
            }
        }
    }
    /**
     * Move the entities towards the player.
     */
    public void moveEntities() {
        //clear the lines
        isometric.getPane().getChildren().removeIf(node -> node instanceof Line);
        if (wagon.getEntities() != null) {
            for (Entity entity : wagon.getEntities()) {
                if (entity != null && entity.isAlive()) {
//
                    int[][] map = wagon.getMapForPathFinder();

                    int[][] path = entity.findPath(map, wagon.getObjectsArray(), player);

                    if (path.length == 0) {
                        double deltaX = entity.getPositionX() - entity.getStartPositionX();
                        double deltaY = entity.getPositionY() - entity.getStartPositionY();
                        deltaX = deltaX > 0 ? -1 : 1;
                        deltaY = deltaY > 0 ? -1 : 1;
                        isometric.updateEntityPosition(entity, deltaX, deltaY, entity.getSpeedX(), entity.getSpeedX());
                    }

                    //check if player in attack range
                    if (checker.checkCollision(entity.getAttackRange(), player.getAttackRange())) {
                        double deltaX = entity.getPositionX() - player.getPositionX();
                        double deltaY = entity.getPositionY() - player.getPositionY();

                        deltaX = deltaX > 0 ? -1 : 1;
                        deltaY = deltaY > 0 ? -1 : 1;

                        isometric.updateEntityPosition(entity, deltaX, deltaY, entity.getSpeedX(), entity.getSpeedX());
                    } else {
                        //take the last element of the path
                        int[] deltaXY;
                        if (path == null) {
                            System.out.println("path is null");
                            return;
                        } else if (path.length >= 2) {
                            deltaXY = path[path.length - 2];
                        } else if (path.length == 1) {
                            deltaXY = path[0];
                        } else {
                            System.out.println("path is empty");
                            return;
                        }

                        int map_x = deltaXY[0];
                        int map_y = deltaXY[1];

                        System.out.println("the next position is: " + map_x + " " + map_y);

                        int x = (int) wagon.getObjectsArray()[map_x][map_y].getIsoX();
                        int y = (int) wagon.getObjectsArray()[map_x][map_y].getIsoY();

                        double deltaX = ((entity.getPositionX() + 32) - x);
                        double deltaY = ((entity.getPositionY() + 72) - y);

                        //calculate the direction: deltaX and deltaY
                        deltaX = deltaX > 0 ? -1 : 1;
                        deltaY = deltaY > 0 ? -1 : 1;

                        //A* algorithm needed

                        isometric.updateEntityPosition(entity, deltaX, deltaY, entity.getSpeedX(), entity.getSpeedX());
                        System.out.println("entity: " + (entity.getPositionX() + 32) + " " + (entity.getPositionY() + 72) + " target " + x + " " + y);
                        //create a line with the path
                        Line line = new Line(entity.getPositionX() + 32, entity.getPositionY() + 72, x, y);
                        line.setStrokeWidth(2);
                        line.setStroke(javafx.scene.paint.Color.RED);
                        isometric.getPane().getChildren().add(line);
                    }
//                        int map_x = deltaXY[0];
//                        int map_y = deltaXY[1];
//
//                        int x = (int) wagon.getObjectsArray()[map_x][map_y].getIsoX();
//                        int y = (int) wagon.getObjectsArray()[map_x][map_y].getIsoY();
//
////                        entity.setPositionX(x);
////                        entity.setPositionY(y);
//
////                        updateEntities();
//
//                        int deltaX = x;
//                        int deltaY = y;

//                        deltaX = deltaX > 0 ? -1 : 1;
//                        deltaY = deltaY > 0 ? -1 : 1;
//
//                        //A* algorithm needed
//
//                        isometric.updateEntityPosition(entity, deltaX, deltaY, entity.getSpeedX(), entity.getSpeedX());

//                    int deltaX = (int) (entity.getHitbox().getLayoutX() - player.getHitbox().getLayoutX());
//                    int deltaY = (int) (entity.getHitbox().getLayoutY() - player.getHitbox().getLayoutY());
//
//                    deltaX = deltaX > 0 ? -1 : 1;
//                    deltaY = deltaY > 0 ? -1 : 1;
//
//                    isometric.updateEntityPosition(entity, deltaX, deltaY, entity.getSpeedX(), entity.getSpeedX());
                }
            }
        }
    }
}
