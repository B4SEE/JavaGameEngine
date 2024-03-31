package cs.cvut.fel.pjv.gamedemo.engine;

import cs.cvut.fel.pjv.gamedemo.common_classes.Object;
import cs.cvut.fel.pjv.gamedemo.common_classes.*;
import javafx.animation.AnimationTimer;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Shape;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Class with the main game logic, handles the game state, player input and game events.
 */
public class GameLogic {
    private final Isometric isometric;
    private Stage stage;
    private long time;
    private Player player;
    private Wagon wagon;
    private Train train;
    private long eventStartTime = 0;
    private int eventDuration;
    private String deathMessage = "";
//    private Constants.Event gameState = Constants.Event.DEFAULT_EVENT;
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

                //update entity's previous positions and counter
                if (time % 5 == 0) {
                    for (Entity entity : wagon.getEntities()) {
                        if (entity != null && entity.isAlive()) {
                            entity.setCounter(0);
                            entity.getPreviousPositions().clear();
                        }
                    }
                    //if wagon has trap inside, start the trap event
                    if (Checker.checkIfWagonHasTrap(wagon.getObjectsArray())) {
                        if (eventStartTime == 0) {
                            eventStartTime = time;
                            //time to escape from the trap (to another wagon)
                            eventDuration = Constants.TIME_TO_ESCAPE_TRAP;
                            //set trap event: 1 - trap with enemies, 2 - trap with boss, 3 - trap with time loop, 4 - silence
                            int trapType = (int) (Math.random() * 4) + 1;
                            switch (trapType) {
                                case 1:
                                    Events.setNextEvent(Constants.Event.TRAP_EVENT);
                                    System.out.println("Trap event");
                                    break;
                                case 2:
                                    Events.setNextEvent(Constants.Event.TRAP_EVENT);
                                    //TODO: spawn boss
                                    break;
                                case 3:
                                    Events.setNextEvent(Constants.Event.TIME_LOOP_EVENT);
                                    int randomCounter = (int) (Math.random() * Constants.MAX_TIME_LOOP_COUNTER);
                                    System.out.println("Time loop event. Number of loops: " + randomCounter);
                                    Events.setTimeLoopCounter(randomCounter);
                                    break;
                                case 4:
                                    Events.setNextEvent(Constants.Event.SILENCE_EVENT);
                                    eventDuration = Constants.TIME_TO_ESCAPE_SILENCE;
                                    System.out.println("Silence event");
                                    break;
                            }
                        }
                        //if the player does not escape from the trap in time, spawn enemies
                        if ((time - eventStartTime != 0) && (time - eventStartTime) >= eventDuration && Events.getCurrentEvent() == Constants.Event.DEFAULT_EVENT) {
                            Events.setCurrentEvent(Events.getNextEvent());
                            Events.setNextEvent(Constants.Event.DEFAULT_EVENT);
                            if (Events.getCurrentEvent() == Constants.Event.TRAP_EVENT) {
                                spawnEnemies();
                            }
                            if (Events.getCurrentEvent() == Constants.Event.SILENCE_EVENT) {
                                deathMessage = "You were killed by the silence";
                                player.setHealth(-1);//instant death
                                wagon.removeTrap();
                                eventStartTime = 0;
                                eventDuration = 0;
                                Events.setCurrentEvent(Constants.Event.DEFAULT_EVENT);//silence event is over once the player dies or moves to another wagon
                            }
                        }
                    }
                    if (Events.getCurrentEvent() != Constants.Event.DEFAULT_EVENT) {
                        checkIfAllEnemiesAreDead();
                    }
                }
            }
        }
    };
    private void checkIfAllEnemiesAreDead() {
        int count = 0;
        for (Entity entity : wagon.getEntities()) {
            if (entity != null && entity.isAlive()) {
                count++;
            }
        }
        if (count == 0) {
            Events.setCurrentEvent(Constants.Event.DEFAULT_EVENT);
            eventStartTime = 0;
            eventDuration = 0;
            wagon.removeTrap();
            isometric.setObjectsToDraw(wagon.getObjectsArray());
            isometric.updateAll();
        }
    }
    private void spawnEnemies() {
        int enemyCount = Math.max(Constants.MIN_TRAP_ENEMIES_COUNT, (int) (Math.random() * Constants.MAX_TRAP_ENEMIES_COUNT));
        for (int i = 0; i < enemyCount; i++) {
            String[] names = Constants.WAGON_TYPE_ENEMIES.get(wagon.getType());
            String name = names[(int) (Math.random() * names.length)];
            Entity enemy = new Entity(name, name + "_front.png");
            enemy.setAsDefaultEnemy();
            enemy.setPositionX(player.getPositionX());
            enemy.setPositionY(player.getPositionY());
            wagon.addEntity(enemy);
        }
        isometric.setEntities(wagon.getEntities());//otherwise the entities' hitboxes are drawn in the wrong place
        isometric.updateAll();
    }
    private void updatePlayer() {
        isometric.updatePlayerPosition();
        player.heal(time);
        player.starve(time);
        Object interactiveObject = Checker.checkIfCanInteract(player, wagon.getInteractiveObjects());
        if (interactiveObject instanceof Door door && door.isLocked()) {
            if (player.getHandItem() != null && player.getHandItem().getType() == Constants.ItemType.KEY) {
                isometric.updateHint("Press E to unlock", (int) player.getPositionX(), (int) player.getPositionY() - 25);
            } else {
                isometric.updateHint("Locked", (int) player.getPositionX(), (int) player.getPositionY() - 25);
            }
        } else if (interactiveObject != null) {
            isometric.updateHint("Press E to open", (int) player.getPositionX(), (int) player.getPositionY() - 25);
        } else if (Checker.checkIfPlayerCanSpeak(player, wagon.getEntities()) != null) {
            isometric.updateHint("Press E to speak", (int) player.getPositionX(), (int) player.getPositionY() - 25);
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
        Atmospheric.fadeOutMusic(0.00f);
        //save game scene
        Scene isoScene = stage.getScene();
        //create grid and scene for death
        GridPane grid = new GridPane();
        grid.setStyle("-fx-background-color: #000000; -fx-border-color: #ffffff;");
        grid.setPrefSize(800, 800);
        Label label = new Label();
        label.setText("Game over, press ENTER to exit, R to restart");
        Label deathMessageLabel = new Label(deathMessage);
        deathMessageLabel.setStyle("-fx-font-size: 50; -fx-text-fill: #ffffff;");
        label.setStyle("-fx-font-size: 50; -fx-text-fill: #ffffff;");
        grid.add(label, 0, 0);
        grid.add(deathMessageLabel, 0, 1);
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
            isometric.resetAimLine();
            switch (keyEvent.getCode()) {
                case W, S:
                    isometric.updatePlayerDeltaY(0);
                    break;
                case A, D:
                    isometric.updatePlayerDeltaX(0);
                    break;
            }
        });
        scene.setOnMouseClicked(mouseEvent -> {
            if (player.getHandItem() instanceof Firearm firearm) {
                player.shoot(firearm, wagon.getEntities(), (int) mouseEvent.getX(), (int) mouseEvent.getY(), time, isometric.getTwoAndTallerWalls());
            } else {
                isometric.resetAimLine();
            }
        });
        scene.setOnMouseMoved(mouseEvent -> {
            if (player.getHandItem() instanceof Firearm) {
                isometric.drawPlayerFirearmAim((int) mouseEvent.getX(), (int) mouseEvent.getY());
            } else {
                isometric.resetAimLine();
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
                    if (Checker.checkIfCanInteract(player, wagon.getInteractiveObjects()) != null) {
                        Object object = Checker.checkIfCanInteract(player, wagon.getInteractiveObjects());
                        if (Objects.equals(object.getTwoLetterId(), Constants.WAGON_DOOR)) {
                            if (Events.getCurrentEvent() == Constants.Event.TRAP_EVENT) {
                                ((Door) object).lock();//player cannot escape from the trap even if he has the key to open the door
                            }
                            if (((Door) object).isLocked()) {
                                if (Checker.checkIfPlayerHasKeyInMainHand(player)) {
                                    player.getPlayerInventory().setMainHandItem(null);//remove the key from the player's main hand
                                    ((Door) object).unlock();
                                }
                            } else {
                                if (Events.getTimeLoopCounter() > 0) {
                                    if (object == wagon.getDoorLeft()) {
                                        //loop the doors: the left door teleports the player to the right door in the same wagon
                                        //and the right door teleports the player to the left door in the same wagon
                                        //the doors are looped until the time loop counter is 0
                                        Object actualTeleport = wagon.getDoorLeft().getTeleport();
                                        wagon.getDoorLeft().setTeleport(wagon.getDoorRightTarget());
                                        wagon.getDoorLeft().teleport(player);
                                        wagon.getDoorLeft().setTeleport(actualTeleport);
                                    } else {
                                        Object actualTeleport = wagon.getDoorRight().getTeleport();
                                        wagon.getDoorRight().setTeleport(wagon.getDoorLeftTarget());
                                        wagon.getDoorRight().teleport(player);
                                        wagon.getDoorRight().setTeleport(actualTeleport);
                                    }
                                    Events.decrementTimeLoopCounter();
                                    if (Events.getTimeLoopCounter() == 0) {
                                        Events.setCurrentEvent(Constants.Event.DEFAULT_EVENT);
                                        wagon.removeTrap();
                                        eventStartTime = 0;
                                        eventDuration = 0;
                                    }
                                    return;//do not teleport the player to the next wagon
                                }
                                openWagonDoor((Door) object);
                            }
                        }
                        if (Objects.equals(object.getTwoLetterId(), Constants.CHEST_OBJECT)) {
                            setInventoryHandle(object.getObjectInventory());
                        }
                        if (Objects.equals(object.getTwoLetterId(),Constants.LOCKABLE_DOOR)) {
                            useLockableDoor(object);
                            isometric.updateAll();
                        }
                    }
                    if (Checker.checkIfPlayerCanSpeak(player, wagon.getEntities()) != null) {
                        Entity entity = Checker.checkIfPlayerCanSpeak(player, wagon.getEntities());
                        openDialogue(entity);
                    }
            }
            isometric.updateWalls();
        });
    }
    private void openDialogue(Entity entity) {
        System.out.println(entity.getName());
        if (entity instanceof QuestNPC questNPC) {
            questNPC.setQuestCompleted(questNPC.checkIfPlayerHasQuestItem(player));
            entity.setDialoguePath(entity.getDialoguePath());
            if (questNPC.isQuestCompleted()) {
                entity.setDialoguePath(entity.getName() + "_completed.json");
            }
            Dialogue questDialogue = new Dialogue(entity.getDialoguePath());
            questDialogue.setEntity(entity);
            setDialogueHandle(questDialogue, entity);
            if (!questNPC.isQuestCompleted()) {
                questNPC.setDialoguePath(entity.getName() + "_default.json");
            }
        } else {
            Dialogue dialogue = new Dialogue(entity.getDialoguePath());
            dialogue.setEntity(entity);
            setDialogueHandle(dialogue, entity);
        }
    }
    private void setDialogueHandle(Dialogue dialogue, Entity dialogueEntity) {
        Scene scene = stage.getScene();

        resetSceneHandlers(scene);

        pauseGame();

        Scene dialogueScene = dialogue.openDialogue();

        stage.setScene(dialogueScene);

        //add other onKeyReleased handlers
        EventHandler<? super KeyEvent> prev_handle = dialogueScene.getOnKeyReleased();
        dialogueScene.setOnKeyReleased(keyEvent -> {
            prev_handle.handle(keyEvent);
            if (Objects.equals(dialogue.getAction(), "trade")) {
                System.out.println("**trade**");
                dialogue.closeDialogue();
                stage.setScene(scene);
                dialogue.setAction(null);
                openTradeWindow((Vendor) dialogueEntity);
                return;
            }
            if (dialogue.getAction() != null) {//work only from the second option
                handleDialogueAction(dialogue.getAction(), dialogueEntity);
                dialogue.setAction(null);
                if (Objects.equals(dialogueEntity.getBehaviour(), Constants.Behaviour.AGGRESSIVE)) {
                    dialogue.closeDialogue();
                    stage.setScene(scene);
                    setPlayerHandle();
                    resumeGame();
                    return;
                }
            }
            if (Objects.requireNonNull(keyEvent.getCode()) == KeyCode.TAB) {
                dialogue.closeDialogue();
                stage.setScene(scene);
                setPlayerHandle();
                resumeGame();
            }
        });
    }
    private void openTradeWindow(Vendor vendor) {
        Inventory inventory = vendor.getVendorInventory();
        if (inventory != null) {

            Scene scene = stage.getScene();
            resetSceneHandlers(scene);
            Scene vendorInventoryScene = inventory.openInventory();

            stage.setScene(vendorInventoryScene);

            vendorInventoryScene.setOnKeyPressed(keyEvent -> {
                if (Objects.requireNonNull(keyEvent.getCode()) == KeyCode.TAB) {
                    if (!inventory.getTakenItems().isEmpty()) {
                        List<Item> takenItems = inventory.getTakenItems();
                        List<Item> itemsToRemove = new ArrayList<>(takenItems);
                        List<Item> addedItems = new ArrayList<>();
                        for (Item item : itemsToRemove) {
                            if (player.getPlayerInventory().getMoney() >= item.getValue()) {
                                System.out.println("Enough money");
                                if (player.getPlayerInventory().addItem(item)) {
                                    inventory.removeTakenItem(item);
                                    player.getPlayerInventory().setMoney(player.getPlayerInventory().getMoney() - item.getValue());
                                    addedItems.add(item);
                                    inventory.removeTakenItem(item);
                                } else {
                                    //vendor will not be angry if the player does not have enough space in the inventory
                                    returnItems(addedItems, itemsToRemove, inventory);
                                    break;
                                }
                            } else {
                                //if the player does not have enough money, the vendor gets negative points
                                System.out.println("Not enough money");
                                vendor.setNegativeCount(vendor.getNegativeCount() + 1);
                                System.out.println(vendor.getNegativeCount());
                                System.out.println(vendor.getNegativeThreshold());
                                returnItems(addedItems, itemsToRemove, inventory);
                                break;
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
    private void handleDialogueAction(String action, Entity dialogueEntity) {//answer types: 1 - negative, 2 - fight, 3 - trade, 4 - check ticket
        System.out.println(action);
        if (Objects.equals(action, "negative")) {
            dialogueEntity.setNegativeCount(dialogueEntity.getNegativeCount() + 1);
            if (dialogueEntity.getNegativeCount() >= dialogueEntity.getNegativeThreshold()) {
                dialogueEntity.setBehaviour(Constants.Behaviour.AGGRESSIVE);
            }
            System.out.println(dialogueEntity.getNegativeCount());
        }
        if (Objects.equals(action, "fight")) {
            dialogueEntity.setBehaviour(Constants.Behaviour.AGGRESSIVE);
        }
        if (Objects.equals(action, "check ticket")) {
            if (Checker.checkIfPlayerHasTicket(player.getPlayerInventory().getItemsArray())) {
                dialogueEntity.setBehaviour(Constants.Behaviour.NEUTRAL);
            } else {
                dialogueEntity.setNegativeCount(dialogueEntity.getNegativeCount() + 1);
                if (dialogueEntity.getNegativeCount() >= dialogueEntity.getNegativeThreshold()) {
                    dialogueEntity.setBehaviour(Constants.Behaviour.AGGRESSIVE);
                }
            }
        }
    }

    private void openWagonDoor(Door door) {
        Atmospheric.resetVolume();//set music to normal volume
        Events.setCurrentEvent(Constants.Event.DEFAULT_EVENT);
        eventStartTime = 0;
        eventDuration = 0;
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
            player.tryAttack(player, wagon.getEntities(), time);
            return;
        }
        player.useHandItem(time);
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
            Scene scene = stage.getScene();
            resetSceneHandlers(scene);
            pauseGame();
            Scene objectInventoryScene = inventory.openInventory();

            stage.setScene(objectInventoryScene);

            objectInventoryScene.setOnKeyPressed(keyEvent -> {
                if (Objects.requireNonNull(keyEvent.getCode()) == KeyCode.TAB) {
                    if (!inventory.getTakenItems().isEmpty()) {
                        List<Item> takenItems = inventory.getTakenItems();
                        List<Item> itemsToRemove = new ArrayList<>(takenItems);
                        List<Item> addedItems = new ArrayList<>();
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
        resetSceneHandlers(scene);

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
        System.out.println(wagon.getSeed());
        isometric.initialiseWagon(wagon);
        setPlayerHandle();
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
                if (entity.getNegativeCount() >= entity.getNegativeThreshold()) {
                    entity.setBehaviour(Constants.Behaviour.AGGRESSIVE);
                }
                if (Objects.equals(entity.getBehaviour(), Constants.Behaviour.NEUTRAL)) {
                    continue;
                }
                entity.tryAttack(entity, List.of(player), time);
            }
        }
        moveEntities();
    }
    /**
     * Move the entities towards the player.
     * Handle the entities' intelligence.
     * Check if the entity is stuck.
     * @see #intelligenceZeroPursue(Entity, Entity)
     * @see #intelligenceOnePursue(Entity, Entity)
     * @see #intelligenceTwoPursue(Entity, Entity)
     * @see #moveEntity(Entity, int[])
     */
    public void moveEntities() {
        if (wagon.getEntities() != null) {
            for (Entity entity : wagon.getEntities()) {
                if (entity != null && entity.isAlive() && Objects.equals(entity.getBehaviour(), Constants.Behaviour.AGGRESSIVE)) {
                    //check if the entity can see the player
                    if (!Checker.checkIfEntityCanSee(entity, player, isometric.getTwoAndTallerWalls(), time)) {
                        returnToStart(wagon.getMapForPathFinder(false), entity);
                        return;
                    }

                    //move the entity with appropriate intelligence
                    if (entity.getIntelligence() == 0) {
                        intelligenceZeroPursue(entity, player);
                    } else if (entity.getIntelligence() == 1) {
                        intelligenceOnePursue(entity, player);
                    } else if (entity.getIntelligence() == 2) {
                        intelligenceTwoPursue(entity, player);
                    } else {
                        return;
                    }

                    //check if the entity is stuck
                    if (Checker.checkIfEntityStuck(entity)) {
                        //use intelligence 0 to move the entity if entity is stuck;
                        //intelligence 0 is the simplest but has the advantage of not getting stuck (if slipping is big enough)
                        intelligenceZeroPursue(entity, player);
                        return;
                    }
                }
            }
        }
    }

    /**
     * Move the entity towards the target with intelligence 0. The entity moves towards the target in a straight line and does not avoid obstacles.
     * @param entity entity to be moved
     * @param target target to be followed
     */
    private void intelligenceZeroPursue(Entity entity, Entity target) {
        double deltaX = entity.getPositionX() - target.getPositionX();
        double deltaY = entity.getPositionY() - target.getPositionY();

        deltaX = deltaX > 0 ? -1 : 1;
        deltaY = deltaY > 0 ? -1 : 1;

        isometric.updateEntityPosition(entity, deltaX, deltaY, entity.getSpeedX(), entity.getSpeedX());
    }

    /**
     * Move the entity towards the target with intelligence 1. The entity moves towards the target and avoids obstacles.
     * @param entity entity to be moved
     * @param target target to be followed
     */
    private void intelligenceOnePursue(Entity entity, Entity target) {
        int[][] map = wagon.getMapForPathFinder(false);
        int[][] path = entity.findPath(map, wagon.getObjectsArray(), target, false);
        pursue(entity, target, map, path);
    }

    /**
     * Move the entity towards the target with intelligence 2. The entity moves towards the target, avoids obstacles and opens the door if the entity is near.
     * @param entity entity to be moved
     * @param target target to be followed
     */
    private void intelligenceTwoPursue(Entity entity, Entity target) {
        int[][] map = wagon.getMapForPathFinder(true);
        int[][] path = entity.findPath(map, wagon.getObjectsArray(), target, false);
        //open the door if the entity is near
        if (Checker.checkIfCanInteract(entity, wagon.getInteractiveObjects()) != null) {
            Object object = Checker.checkIfCanInteract(entity, wagon.getInteractiveObjects());
            if (Objects.equals(object.getTwoLetterId(),Constants.LOCKABLE_DOOR) && object.isSolid()) {
                useLockableDoor(object);
                isometric.updateAll();
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
    private void pursue(Entity entity, Entity target, int[][] map, int[][] path) {
        if (Checker.checkCollision(entity.getAttackRange(), target.getAttackRange())) {
            intelligenceZeroPursue(entity, target);
        } else {
            int[] deltaXY = new int[0];
            if (path.length >= 2) {
                deltaXY = path[path.length - 2];
            } else if (path.length == 1) {
                deltaXY = path[0];
            } else {
                returnToStart(map, entity);
            }
            moveEntity(entity, deltaXY);
        }
    }

    /**
     * Move the entity back to the start position.
     * @param map map for the pathfinder
     * @param entity entity to be moved
     */
    private void returnToStart(int[][] map, Entity entity) {
        if (entity.getPositionX() == entity.getStartPositionX() && entity.getPositionY() == entity.getStartPositionY()) {
            return;
        }
        int[][] path = entity.findPath(map, wagon.getObjectsArray(), entity, true);
        if (path.length == 0) {
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
     * Move the entity.
     * @param entity entity to be moved
     * @param index index of the next position found by the pathfinder
     */
    private void moveEntity(Entity entity, int[] index) {
        if (index == null || index.length == 0) {
            return;
        }

        int x = (int) wagon.getObjectsArray()[index[0]][index[1]].getIsoX();
        int y = (int) wagon.getObjectsArray()[index[0]][index[1]].getIsoY();

        double deltaX = ((entity.getPositionX() + 32) - x);
        double deltaY = ((entity.getPositionY() + 80) - y);

        deltaX = deltaX > 0 ? -1 : 1;
        deltaY = deltaY > 0 ? -1 : 1;

        isometric.updateEntityPosition(entity, deltaX, deltaY, entity.getSpeedX(), entity.getSpeedX());
    }
    private void resetSceneHandlers(Scene scene) {
        scene.setOnKeyReleased(null);
        scene.setOnMouseClicked(null);
        scene.setOnMouseMoved(null);
        scene.setOnKeyPressed(null);
    }
}
