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
import javafx.scene.shape.Shape;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * Class with the main game logic, handles the game state, player input and game events.
 */
public class GameLogic {
    private final Isometric isometric;
    private final Stage stage;
    private long time;
    private Player player;
    private final List<Entity> leftWagonPursuers = new java.util.ArrayList<>();//TODO forbid saving during pursuit (player should escape first)
    private final List<Entity> rightWagonPursuers = new java.util.ArrayList<>();
    private List<Entity> conductors = new java.util.ArrayList<>();
    private Wagon wagon;
    private final Train train;
    private long eventStartTime = 0;//TODO forbid saving the game during the trap event
    private int eventDuration;
    private String deathMessage = "";
    private final AnimationTimer timer = new AnimationTimer() {
        final long INTERVAL = 1_000_000_000_000L;
        long lastTime = -1;
        /**
         * Update the game state every INTERVAL nanoseconds.
         * @param l current time in nanoseconds
         */
        public void handle(long l) {
            time = l / (INTERVAL / 1000);
            if (lastTime < 0) {
                lastTime = l;
            } else if (l - lastTime < INTERVAL) {

                updateGame();

                if (everyNSecondsDo(5)) {
                    updateEntitiesPrevPos();
                    //if wagon has trap inside, start the trap event
                    if (Checker.checkIfWagonHasTrap(wagon.getObjectsArray())) {
                        generateAndSetTrap();
                    }
                    if (Events.getCurrentEvent() != Constants.Event.DEFAULT_EVENT) {
                        checkIfAllEnemiesAreDead();
                    }
                }
            }
        }
    };

    /**
     * Return true if the time is divisible by n (returns true every n seconds).
     * @param n time interval in seconds
     * @return true if the time is divisible by n
     */
    private boolean everyNSecondsDo(int n) {
        return time % n == 0;
    }

    /**
     * Update the game state.
     */
    private void updateGame() {
        isometric.updateWalls();
        updateEntities();
        updatePlayer();
        isometric.removeDeadEntities();
        if (!player.isAlive()) {
            isometric.setHandleToNull();
            stopGame();
        }
    }

    /**
     * Generate and set the random trap event (trap with enemies, boss, time loop, silence).
     */
    private void generateAndSetTrap() {
        if (eventStartTime == 0) {
            eventStartTime = time;
            eventDuration = Constants.TIME_TO_ESCAPE_TRAP;
            int trapType = (int) (Math.random() * 4) + 1;
            switch (trapType) {
                case 1:
                    Events.setNextEvent(Constants.Event.TRAP_EVENT);
                    break;
                case 2:
                    Events.setNextEvent(Constants.Event.TRAP_EVENT);
                    //TODO: spawn boss
                    break;
                case 3:
                    Events.setNextEvent(Constants.Event.TIME_LOOP_EVENT);
                    int randomCounter = (int) (Math.random() * Constants.MAX_TIME_LOOP_COUNTER);
                    Events.setTimeLoopCounter(randomCounter);
                    break;
                case 4:
                    Events.setNextEvent(Constants.Event.SILENCE_EVENT);
                    eventDuration = Constants.TIME_TO_ESCAPE_SILENCE;
                    Atmospheric.fadeOutMusic(0.05);
                    break;
            }
        }
        activateTrap();
    }

    /**
     * Activate the trap event if the time is up (if the player does not escape from the trap in time).
     */
    private void activateTrap() {
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

    /**
     * Update the entities' previous positions to check for stuck entities.
     */
    private void updateEntitiesPrevPos() {
        for (Entity entity : wagon.getEntities()) {
            if (entity != null && entity.isAlive()) {
                entity.setCounter(0);
                entity.getPreviousPositions().clear();
            }
        }
    }

    /**
     * Check if all entities from trap event are dead and remove the trap if they are.
     */
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
            wagon.getDoorLeft().unlock();
            wagon.getDoorRight().unlock();
            isometric.setObjectsToDraw(wagon.getObjectsArray());
            isometric.updateAll();
        }
    }

    /**
     * Spawn random number of enemies for the trap event.
     */
    private void spawnEnemies() {
        int enemyCount = Math.max(Constants.MIN_TRAP_ENEMIES_COUNT, (int) (Math.random() * Constants.MAX_TRAP_ENEMIES_COUNT));
        for (int i = 0; i < enemyCount; i++) {
            String[] names = Constants.WAGON_TYPE_ENEMIES.get(wagon.getType());
            String name = names[(int) (Math.random() * names.length)];
            Entity enemy = new Entity(name, name + "_front.png");
            EntitiesCreator.setAsDefaultEnemy(enemy);
            enemy.setPositionX(player.getPositionX());
            enemy.setPositionY(player.getPositionY());
            wagon.addEntity(enemy);
        }
        isometric.setEntities(wagon.getEntities());//otherwise the entities' hitboxes are drawn in the wrong place
        isometric.updateAll();
    }

    /**
     * Update player's position and stats.
     */
    private void updatePlayer() {
        isometric.updatePlayerPosition();
        player.heal(time);
        player.starve(time);
        showHint();
    }

    /**
     * Show the hint for the player to interact with the object.
     */
    private void showHint() {
        Object interactiveObject = Checker.checkIfCanInteract(player, wagon.getInteractiveObjects());
        if (interactiveObject instanceof Door door && door.isLocked()) {
            String hint = (player.getHandItem() != null && player.getHandItem().getType() == Constants.ItemType.KEY) ? "Press E to unlock" : "Locked";
            isometric.updateHint(hint, (int) player.getPositionX(), (int) player.getPositionY() - 25);
        } else if (interactiveObject != null) {
            isometric.updateHint("Press E to open", (int) player.getPositionX(), (int) player.getPositionY() - 25);
        } else if (Checker.checkIfPlayerCanSpeak(player, wagon.getEntities()) != null) {
            isometric.updateHint("Press E to speak", (int) player.getPositionX(), (int) player.getPositionY() - 25);
        } else {
            isometric.updateHint("", (int) player.getPositionX(), (int) player.getPositionY() - 25);
        }
    }
    public GameLogic(Stage stage, Train train) {
        isometric = new Isometric();
        this.stage = stage;
        this.train = train;
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
        this.wagon.setObstacles(isometric.getWalls());
        conductors = new LinkedList<>(wagon.getEntities());
        conductors.removeIf(entity -> entity.getType() != Constants.EntityType.CONDUCTOR);
        for (Entity conductor : conductors) {
            conductor.setPositionX(wagon.getDoorRightTarget().getIsoX());
            conductor.setPositionY(wagon.getDoorRightTarget().getIsoY() - 64);
        }
        isometric.setEntities(wagon.getEntities());
        isometric.updateAll();
        timer.start();
    }
    /**
     * Pause the game.
     */
    public void pauseGame() {
        timer.stop();
    }

    /**
     * Show the pause screen.
     */
    private void showPauseScreen() {
        Atmospheric.fadeOutMusic(0.00);
        pauseGame();
        //save game scene
        Scene isoScene = stage.getScene();
        //create grid and scene for pause
        GridPane grid = new GridPane();
        grid.setStyle("-fx-background-color: #000000; -fx-border-color: #ffffff;");
        grid.setPrefSize(800, 800);
        Label label = new Label();
        label.setText("Game paused, press ESC to resume");
        label.setStyle("-fx-font-size: 50; -fx-text-fill: #ffffff;");
        grid.add(label, 0, 0);
        Scene scene = new Scene(grid, Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
        stage.setScene(scene);
        scene.onKeyPressedProperty().set(keyEvent -> {
            if (keyEvent.getCode().toString().equals("ESCAPE")) {
                stage.setScene(isoScene);
                resumeGame();
            }
        });
        Button resumeButton = new Button("Resume");
        Button saveButton = new Button("Save");
        Button mainMenuButton = new Button("Main menu");
        Button exitButton = new Button("Exit");
        resumeButton.setOnAction(actionEvent -> {
            stage.setScene(isoScene);
            resumeGame();
        });
        saveButton.setOnAction(actionEvent -> {
            if (Events.canSaveGame()) {
                saveGame();
                Button continueButton = new Button("Continue");
                Button mainMenuButton1 = new Button("Main menu");
                continueButton.setOnAction(actionEvent1 -> {
                    stage.setScene(isoScene);
                    resumeGame();
                });
                mainMenuButton1.setOnAction(actionEvent1 -> {
                    stage.setScene(null);
                    mainMenu();
                });
                Label label1 = new Label("Game saved");
                label1.setStyle("-fx-font-size: 20; -fx-text-fill: #ffffff;");
                GridPane grid1 = new GridPane();
                grid1.setStyle("-fx-background-color: #000000; -fx-border-color: #ffffff;");
                grid1.setPrefSize(800, 800);
                grid1.add(label1, 0, 0);
                grid1.add(continueButton, 0, 1);
                grid1.add(mainMenuButton1, 0, 2);
                grid1.add(exitButton, 0, 3);
                Scene scene1 = new Scene(grid1, Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
                stage.setScene(scene1);
            } else {
                Label label1 = new Label("You cannot save the game now");
                label1.setStyle("-fx-font-size: 20; -fx-text-fill: #ffffff;");
                GridPane grid1 = new GridPane();
                grid1.setStyle("-fx-background-color: #000000; -fx-border-color: #ffffff;");
                grid1.setPrefSize(800, 800);
                grid1.add(label1, 0, 0);
                grid1.add(resumeButton, 0, 1);
                grid1.add(mainMenuButton, 0, 2);
                grid1.add(exitButton, 0, 3);
                Scene scene1 = new Scene(grid1, Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
                stage.setScene(scene1);
            }
        });
        mainMenuButton.setOnAction(actionEvent -> {
            Button confirmButton = new Button("Confirm");
            Button cancelButton = new Button("Cancel");
            Label label1 = new Label("Are you sure you want to exit to the main menu?");
            label1.setStyle("-fx-font-size: 20; -fx-text-fill: #ffffff;");
            GridPane grid1 = new GridPane();
            grid1.setStyle("-fx-background-color: #000000; -fx-border-color: #ffffff;");
            grid1.setPrefSize(800, 800);
            grid1.add(label1, 0, 0);
            grid1.add(confirmButton, 0, 1);
            grid1.add(cancelButton, 0, 2);
            Scene scene1 = new Scene(grid1, Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
            stage.setScene(scene1);
            confirmButton.setOnAction(actionEvent1 -> {
                stage.setScene(null);
                mainMenu();
            });
            cancelButton.setOnAction(actionEvent1 -> {
                stage.setScene(isoScene);
                resumeGame();
            });
        });
        exitButton.setOnAction(actionEvent -> {
            Button confirmButton = new Button("Confirm");
            Button cancelButton = new Button("Cancel");
            Label label1 = new Label("Are you sure you want to exit the game?");
            label1.setStyle("-fx-font-size: 20; -fx-text-fill: #ffffff;");
            GridPane grid1 = new GridPane();
            grid1.setStyle("-fx-background-color: #000000; -fx-border-color: #ffffff;");
            grid1.setPrefSize(800, 800);
            grid1.add(label1, 0, 0);
            grid1.add(confirmButton, 0, 1);
            grid1.add(cancelButton, 0, 2);
            Scene scene1 = new Scene(grid1, Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
            stage.setScene(scene1);
            confirmButton.setOnAction(actionEvent1 -> {
                stage.close();
                System.exit(0);
            });
            cancelButton.setOnAction(actionEvent1 -> {
                stage.setScene(isoScene);
                resumeGame();
            });
        });
        grid.add(resumeButton, 0, 1);
        grid.add(saveButton, 0, 2);
        grid.add(mainMenuButton, 0, 3);
        grid.add(exitButton, 0, 4);
    }
    /**
     * Resume the game.
     */
    public void resumeGame() {
        timer.start();
    }

    /**
     * Stop the game.
     * Show the death scene.
     * Allow the player to restart the game or exit to the main menu.
     * @see #mainMenu()
     */
    public void stopGame() {
        pauseGame();
        Atmospheric.fadeOutMusic(0.00);
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

        //handle movement
        EventHandler<? super KeyEvent> movement_handle = keyEvent -> {
            switch (keyEvent.getCode()) {
                case W:
                    if (!player.getTexturePath().contains("back")) isometric.updatePlayerTexture("player_back.png");
                    isometric.updatePlayerDeltaY(-Constants.PLAYER_BASIC_SPEED_Y);
                    break;
                case S:
                    if (!player.getTexturePath().contains("front")) isometric.updatePlayerTexture("player_front.png");
                    isometric.updatePlayerDeltaY(Constants.PLAYER_BASIC_SPEED_Y);
                    break;
                case A:
                    if (!player.getTexturePath().contains("left")) isometric.updatePlayerTexture("player_left.png");
                    isometric.updatePlayerDeltaX(-Constants.PLAYER_BASIC_SPEED_X);
                    break;
                case D:
                    if (!player.getTexturePath().contains("right")) isometric.updatePlayerTexture("player_right.png");
                    isometric.updatePlayerDeltaX(Constants.PLAYER_BASIC_SPEED_X);
                    break;
            }
            if (isometric.getPlayerDeltaX() != 0 || isometric.getPlayerDeltaY() != 0) playPlayerFootsteps();
        };
        //handle movement keys released
        EventHandler<? super KeyEvent> movement_stopped_handle = keyEvent -> {
            switch (keyEvent.getCode()) {
                case W, S:
                    isometric.updatePlayerDeltaY(0);
                    break;
                case A, D:
                    isometric.updatePlayerDeltaX(0);
                    break;
            }
            if (isometric.getPlayerDeltaX() == 0 && isometric.getPlayerDeltaY() == 0) stopPlayerFootsteps();
        };
        //handle interaction with objects and entities
        EventHandler<? super KeyEvent> interact_handle = keyEvent -> {
            if (Objects.requireNonNull(keyEvent.getCode()) == KeyCode.E) {
                if (Checker.checkIfCanInteract(player, wagon.getInteractiveObjects()) != null) {
                    Object object = Checker.checkIfCanInteract(player, wagon.getInteractiveObjects());
                    if (object instanceof Door door) {
                        handleWagonDoor(door);
                    }
                    if (Objects.equals(object.getTwoLetterId(), Constants.CHEST_OBJECT)) {
                        stopPlayerFootsteps();
                        setInventoryHandle(object.getObjectInventory());
                    }
                    if (Objects.equals(object.getTwoLetterId(), Constants.LOCKABLE_DOOR)) {
                        useLockableDoor(object);
                        isometric.updateAll();
                    }
                }
                if (Checker.checkIfPlayerCanSpeak(player, wagon.getEntities()) != null) {
                    stopPlayerFootsteps();
                    Entity entity = Checker.checkIfPlayerCanSpeak(player, wagon.getEntities());
                    openDialogue(entity);
                }
            }
        };
        //handle TAB, ESCAPE, R
        EventHandler<? super KeyEvent> other_handle = keyEvent -> {
            switch (keyEvent.getCode()) {
                case TAB:
                    stopPlayerFootsteps();
                    setPlayerInventoryHandle();
                    break;
                case ESCAPE:
                    stopPlayerFootsteps();
                    showPauseScreen();
                    break;
                case R:
                    playerUseHand();
                    break;
            }
        };
        //add scene handlers
        //add onKeyPressed handlers
        scene.setOnKeyPressed(keyEvent -> {
            movement_handle.handle(keyEvent);
            interact_handle.handle(keyEvent);
            other_handle.handle(keyEvent);
            isometric.updateWalls();
        });
        //add onKeyReleased handlers
        scene.setOnKeyReleased(keyEvent -> {
            movement_stopped_handle.handle(keyEvent);
            isometric.resetAimLine();
        });
        //add onMouseClicked handler
        scene.setOnMouseClicked(mouseEvent -> {
            if (player.getHandItem() instanceof Firearm firearm) {
                player.shoot(firearm, wagon.getEntities(), (int) mouseEvent.getX(), (int) mouseEvent.getY(), time, isometric.getTwoAndTallerWalls());
            } else {
                isometric.resetAimLine();
            }
        });
        //add onMouseMoved handler
        scene.setOnMouseMoved(mouseEvent -> {
            if (player.getHandItem() instanceof Firearm) {
                isometric.drawPlayerFirearmAim((int) mouseEvent.getX(), (int) mouseEvent.getY());
            } else {
                isometric.resetAimLine();
            }
        });
    }
    private void playPlayerFootsteps() {
        //do not start a new thread if the previous one is still running
        if (player.getSoundThread() != null) {
            return;
        }

        Thread footstepLoop = new Thread(() -> {
            String prev = "_1";
            while (true) {
                String texturePath = player.getTexturePath();
                // Change texture path based on player movement
                if (!texturePath.contains("_1") && !texturePath.contains("_2")) {//if the texture is normal
                    player.setTexturePath(texturePath.replace(".png", prev + ".png"));
                    prev = (prev.equals("_1")) ? "_2" : "_1";
                } else {
                    player.setTexturePath(texturePath.contains("_1") ? texturePath.replace("_1", "_2") : texturePath.replace("_2", "_1"));
                }
                //TODO play sound
//                    Atmospheric.playSound("resources/sounds/footstep_sounds/single_footstep_sound.wav");
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    break;
                }
            }
        });

        player.setSoundThread(footstepLoop);
        player.getSoundThread().start();
    }

    private void stopPlayerFootsteps() {
        //stop the previous thread if it exists
        if (player.getSoundThread() != null) player.getSoundThread().interrupt();
        //reset the texture
        player.setTexturePath(player.getTexturePath().replaceAll("_[12]", ""));
        //set the thread to null
        player.setSoundThread(null);
    }
    private void handleWagonDoor(Door object) {
        if (Events.getCurrentEvent() == Constants.Event.TRAP_EVENT) {
            object.lock();//player cannot escape from the trap even if he has the key to open the door
        }
        if (object.isLocked()) {
            if (Checker.checkIfPlayerHasKeyInMainHand(player)) {
                player.getPlayerInventory().setMainHandItem(null);//remove the key from the player's main hand
                object.unlock();
            }
        } else {
            if (Events.getTimeLoopCounter() > 0) {
                handleTimeLoop(object);
                return;//do not teleport the player to the next wagon
            }
            System.out.println("Time loop counter: " + Events.getTimeLoopCounter());
            openWagonDoor(object);
        }
    }
    private void handleTimeLoop(Door object) {
        System.out.println("Time loop counter: " + Events.getTimeLoopCounter());
        if (object == wagon.getDoorLeft()) {
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
    }
    /**
     * Open the dialogue with the entity.
     * @param entity entity to speak with
     */
    private void openDialogue(Entity entity) {
        if (entity instanceof QuestNPC questNPC) {
            questNPC.setQuestCompleted(questNPC.checkIfPlayerHasQuestItem(player));
            entity.setDialoguePath(entity.getDialoguePath());
            if (questNPC.isQuestCompleted()) {
                entity.setDialoguePath(entity.getName() + "_completed.json");
                player.getPlayerInventory().addItem(RandomHandler.getRandomFoodItem());
            }
            Dialogue questDialogue = new Dialogue(entity.getDialoguePath());
            questDialogue.setEntity(entity);
            setDialogueHandle(questDialogue, entity);
            if (!questNPC.isQuestCompleted()) questNPC.setDialoguePath(entity.getName() + "_default.json");
        } else {
            Dialogue dialogue = new Dialogue(entity.getDialoguePath());
            dialogue.setEntity(entity);
            setDialogueHandle(dialogue, entity);
        }
    }

    /**
     * Set dialogue handle.
     * @param dialogue dialogue to open
     * @param dialogueEntity entity to speak with
     */
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

    /**
     * Open the trade window with the vendor.
     * @param vendor vendor to trade with
     */
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

    /**
     * Handle dialogue actions/reactions.
     * @param action action to be handled
     * @param dialogueEntity entity to speak with
     */
    private void handleDialogueAction(String action, Entity dialogueEntity) {//answer types: 1 - negative, 2 - fight, 3 - trade, 4 - check ticket
        // Handle negative response
        if (Objects.equals(action, "negative")) {
            dialogueEntity.setNegativeCount(dialogueEntity.getNegativeCount() + 1);
            if (dialogueEntity.getNegativeCount() >= dialogueEntity.getNegativeThreshold()) {
                dialogueEntity.setBehaviour(Constants.Behaviour.AGGRESSIVE);
            }
        }

        // Handle fight response
        if (Objects.equals(action, "fight")) {
            dialogueEntity.setBehaviour(Constants.Behaviour.AGGRESSIVE);
        }

        // Handle checking ticket response
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

    /**
     * Open wagon door (teleport the player to the next wagon).
     * @param door door to be opened
     */
    private void openWagonDoor(Door door) {
        Atmospheric.resetVolume();//set music to normal volume
        Events.setCurrentEvent(Constants.Event.DEFAULT_EVENT);
        eventStartTime = 0;
        eventDuration = 0;
        if (door.getTargetId() == -1) {
            System.out.println("generate next wagon");
            generateNextWagonAndOpenDoor(door);
        } else {
            System.out.println("open existing wagon door");
            openExistingWagonDoor(door);
        }
    }

    /**
     * Generate the next wagon and open the door.
     * @param door door to be opened
     */
    private void generateNextWagonAndOpenDoor(Door door) {
//        String wagonType = RandomHandler.getRandomWagonType();
        String wagonType = "CARGO";
        Wagon nextWagon = new Wagon(train.findMaxWagonId() + 1, wagonType);

        if (door == wagon.getDoorLeft()) {
            generateLeftWagon(nextWagon);
        } else if (door == wagon.getDoorRight()) {
            generateRightWagon(nextWagon);
        }
    }
    private void generateLeftWagon(Wagon nextWagon) {
        setPursuers(rightWagonPursuers);
        System.out.println("right pursuers: " + rightWagonPursuers.size());

        nextWagon.generateNextWagon(wagon, true);
        train.addWagon(nextWagon);

        isometric.initialiseWagon(nextWagon);
        isometric.updateAll();
        nextWagon.setObstacles(isometric.getWalls());

        wagon.getDoorLeft().teleport(player);
        this.wagon = nextWagon;
        player.setCurrentWagon(wagon);
    }

    private void generateRightWagon(Wagon nextWagon) {
        setPursuers(leftWagonPursuers);
        System.out.println("left wagon pursuers: " + leftWagonPursuers.size());

        nextWagon.generateNextWagon(wagon, false);
        train.addWagon(nextWagon);

        isometric.initialiseWagon(nextWagon);
        isometric.updateAll();
        nextWagon.setObstacles(isometric.getWalls());

        wagon.getDoorRight().teleport(player);
        this.wagon = nextWagon;
        player.setCurrentWagon(wagon);
    }

    private void setPursuers(List<Entity> wagonPursuers) {
        System.out.println("set pursuers");
        wagonPursuers.clear();
        for (Entity entity : wagon.getEntities()) {
            if (entity.getBehaviour() == Constants.Behaviour.AGGRESSIVE) {
                if (entity.getIntelligence() == 2) {
                    if (Checker.checkIfEntityCanSee(entity, player, isometric.getTwoAndTallerWalls(), time)) {
                        wagonPursuers.add(entity);
                    }
                }
            }
        }
    }

    /**
     * Open the existing wagon door (teleport the player to the next wagon).
     * @param door door to be opened
     */
    private void openExistingWagonDoor(Door door) {
        for (Wagon wagon : train.getWagonsArray()) {
            if (wagon.getId() == door.getTargetId()) {
                if (door == this.wagon.getDoorLeft()) {
                    setPursuers(rightWagonPursuers);
                    isometric.initialiseWagon(wagon);
                    isometric.updateAll();
                    wagon.setObstacles(isometric.getWalls());
                    this.wagon.getDoorLeft().teleport(player);
                    this.wagon = wagon;
                    player.setCurrentWagon(wagon);
                } else {
                    setPursuers(leftWagonPursuers);
                    isometric.initialiseWagon(wagon);
                    isometric.updateAll();
                    wagon.setObstacles(isometric.getWalls());
                    this.wagon.getDoorRight().teleport(player);
                    this.wagon = wagon;
                    player.setCurrentWagon(wagon);
                }
                return;
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
        if (stage == null || train == null) {
            throw new IllegalArgumentException("Stage or train is null");
        }
        if (player == null || wagon == null) {
            throw new IllegalArgumentException("Invalid input");
        }
        this.player = player;
        this.wagon = wagon;

        player.setCurrentWagon(wagon);

        isometric.initialiseStage(stage);
        isometric.setPlayer(this.player);
        System.out.println(wagon.getSeed());
        isometric.initialiseWagon(this.wagon);
        setPlayerHandle();
    }
    /**
     * Save the game.
     */
    public void saveGame() {
        GameSaver game = new GameSaver(player, train);
        game.saveGame();
    }
    /**
     * Show the main menu.
     */
    public void mainMenu() {
        isometric.clearAll();
        Scene mainMenuScene = Game.getMainMenuScene();
        stage.setScene(mainMenuScene);
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
                    switch (entity.getIntelligence()) {
                        case 0 -> intelligenceZeroPursue(entity, player);
                        case 1 -> intelligenceOnePursue(entity, player);
                        case 2 -> intelligenceTwoPursue(entity, player);
                        default -> { return; }
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
        if (!leftWagonPursuers.isEmpty()) {
            System.out.println(leftWagonPursuers.getFirst().getName());
            movePursuers(leftWagonPursuers, leftWagonPursuers.getFirst().getCurrentWagon().getDoorRightTarget(), leftWagonPursuers.getFirst().getCurrentWagon().getDoorRight());
        }
        if (!rightWagonPursuers.isEmpty()) {
            System.out.println(rightWagonPursuers.getFirst().getName());
            movePursuers(rightWagonPursuers, rightWagonPursuers.getFirst().getCurrentWagon().getDoorLeftTarget(), rightWagonPursuers.getFirst().getCurrentWagon().getDoorLeft());
        }
        moveConductors();
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
        int[][] path = entity.findPath(map, target.findOnWhatObject());
        pursue(entity, target, map, path);
    }

    /**
     * Move the entity towards the target with intelligence 2. The entity moves towards the target, avoids obstacles and opens the door if the entity is near.
     * @param entity entity to be moved
     * @param target target to be followed
     */
    private void intelligenceTwoPursue(Entity entity, Entity target) {
        int[][] map = wagon.getMapForPathFinder(true);
        int[][] path = entity.findPath(map, target.findOnWhatObject());
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
            int[] deltaXY = getDeltaXY(entity, path, map);
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
        int[][] path = entity.findPath(map, entity.getStartIndex());
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

        if (entity.getCurrentWagon() != player.getCurrentWagon()) {
            moveEntityInOtherWagon(entity, (int) deltaX, (int) deltaY);
        } else {
            isometric.updateEntityPosition(entity, deltaX, deltaY, entity.getSpeedX(), entity.getSpeedX());
        }
    }
    private void moveEntityInOtherWagon(Entity entity, int deltaX, int deltaY) {
        Shape oldWalls = isometric.getWalls();
        isometric.setWalls(entity.getCurrentWagon().getObstacles());
        isometric.updateEntityPosition(entity, deltaX, deltaY, entity.getSpeedX(), entity.getSpeedX());
        isometric.setWalls(oldWalls);
    }
    private void movePursuers(List<Entity> pursuers, Object target, Door wagonDoor) {
        System.out.println("Moving pursuers");
        //get copy of the list to avoid ConcurrentModificationException
        List<Entity> wagonPursuers = new LinkedList<>(pursuers);
        for (Entity pursuer : wagonPursuers) {
            if (pursuer != null) {
                if (player.getCurrentWagon() == pursuer.getCurrentWagon()) {
                    pursuers.remove(pursuer);
                    continue;
                }
                if (target == null) {
                    System.out.println("Target is null");
                    return;
                }
                if (Checker.checkCollision(pursuer.getAttackRange(), wagonDoor.getObjectHitbox())) {
                    //might be unnecessary: pursuer will pursue the player to the next wagon, meaning the door is open and already generated;
                    if (wagonDoor.isLocked()) {
                        System.out.println("Door is locked");
                        continue;
                    }
                    if (wagonDoor.getTargetId() == -1) {
                        System.out.println("Door target id is -1");
                        continue;
                    }
                    //teleport the pursuer to the next wagon
                    System.out.println("Teleporting pursuer to the next wagon");
                    wagonDoor.teleport(pursuer);
                    //remove from original list and current wagon
                    pursuers.remove(pursuer);
                    pursuer.getCurrentWagon().getEntities().remove(pursuer);
                    pursuer.setCurrentWagon(wagon);
                    wagon.getEntities().add(pursuer);
                    isometric.setEntities(wagon.getEntities());
                    isometric.updateAll();
                    continue;
                }
                //find target index
                for (int i = 0; i < pursuer.getCurrentWagon().getObjectsArray().length; i++) {
                    for (int j = 0; j < pursuer.getCurrentWagon().getObjectsArray()[i].length; j++) {
                        if (pursuer.getCurrentWagon().getObjectsArray()[i][j] == target) {
                            System.out.println("Target found");
                            int[] targetIndex = {i, j};
                            //find path
                            int[][] path = pursuer.findPath(pursuer.getCurrentWagon().getMapForPathFinder(true), targetIndex);
                            //move pursuer
                            int[] deltaXY = getDeltaXY(pursuer, path, pursuer.getCurrentWagon().getMapForPathFinder(true));
                            System.out.println("Pursuer moved");
                            moveEntity(pursuer, deltaXY);
                        }
                    }
                }
            }
        }
    }
    private void moveConductors() {
        if (!conductors.isEmpty()) {
            for (Entity conductor : conductors) {
                if (conductor != null) {
                    Door wagonDoor = conductor.getCurrentWagon().getDoorLeft();
                    if (Checker.checkCollision(conductor.getAttackRange(), wagonDoor.getObjectHitbox())) {
                        System.out.println("door collision");
                        //conductors can open the door if it is locked or if the next wagon is not generated
                        if (wagonDoor.isLocked()) {
                            System.out.println("door is locked");
                            wagonDoor.unlock();
                            continue;
                        }
                        if (wagonDoor.getTargetId() == -1) {
                            System.out.println("generate next wagon");
                            String wagonType = "CARGO";
                            Wagon nextWagon = new Wagon(train.findMaxWagonId() + 1, wagonType);

                            nextWagon.generateNextWagon(conductor.getCurrentWagon(), true);
                            train.addWagon(nextWagon);
                            //initialise the next wagon
                            isometric.initialiseWagon(nextWagon);
                            isometric.updateAll();
                            nextWagon.setObstacles(isometric.getWalls());
                            //set original/player's wagon
                            isometric.initialiseWagon(wagon);
                            isometric.updateAll();
                            continue;
                        }
                        System.out.println("open existing wagon door");
                        System.out.println("From " + conductor.getPositionX() + " " + conductor.getPositionY());
                        wagonDoor.teleport(conductor);
                        Wagon nextWagon = train.getWagonById(wagonDoor.getTargetId());
                        conductor.getCurrentWagon().getEntities().remove(conductor);
                        conductor.setCurrentWagon(nextWagon);
                        nextWagon.getEntities().add(conductor);
                        //update all hitboxes
                        isometric.setEntities(conductor.getCurrentWagon().getEntities());
                        isometric.updateAll();
                        isometric.setEntities(wagon.getEntities());
                        isometric.updateAll();
                    }
                    //find target index
                    for (int i = 0; i < conductor.getCurrentWagon().getObjectsArray().length; i++) {
                        for (int j = 0; j < conductor.getCurrentWagon().getObjectsArray()[i].length; j++) {
                            if (conductor.getCurrentWagon().getObjectsArray()[i][j] == wagonDoor) {
                                int[] targetIndex = {i, j};
                                //find path
                                int[][] path = conductor.findPath(conductor.getCurrentWagon().getMapForPathFinder(true), targetIndex);
                                //move conductor
                                int[] deltaXY = getDeltaXY(conductor, path, conductor.getCurrentWagon().getMapForPathFinder(true));
                                moveEntity(conductor, deltaXY);
                            }
                        }
                    }
                }
            }
        }
    }
    private int[] getDeltaXY(Entity entity, int[][] path, int[][] map) {
        int[] deltaXY = new int[0];
        if (path.length >= 2) {
            deltaXY = path[path.length - 2];
        } else if (path.length == 1) {
            deltaXY = path[0];
        } else {
            returnToStart(map, entity);
        }
        return deltaXY;
    }

    /**
     * Reset the scene handlers.
     * @param scene scene to be reset
     */
    private void resetSceneHandlers(Scene scene) {
        scene.setOnKeyReleased(null);
        scene.setOnMouseClicked(null);
        scene.setOnMouseMoved(null);
        scene.setOnKeyPressed(null);
    }
}
