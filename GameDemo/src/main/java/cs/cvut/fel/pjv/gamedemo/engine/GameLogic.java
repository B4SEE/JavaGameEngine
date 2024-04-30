package cs.cvut.fel.pjv.gamedemo.engine;

import cs.cvut.fel.pjv.gamedemo.common_classes.Object;
import cs.cvut.fel.pjv.gamedemo.common_classes.*;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;
import javafx.stage.Stage;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * Class with the main game logic, handles the game state, player input and game events.
 */
public class GameLogic {
    private static final Logger logger = LogManager.getLogger(GameLogic.class);
    private Isometric isometric;
    private final Stage stage;
    private int kidnapStage = 0;
    private int kidnapStageCount = 3;
    private long time;
    private Player player;
    private Entity boss = null;
    private final List<Entity> leftWagonPursuers = new java.util.ArrayList<>();//TODO forbid saving during pursuit (player should escape first)
    private final List<Entity> rightWagonPursuers = new java.util.ArrayList<>();
    private Entity conductor;
    private Entity grandmother;
    private Wagon wagon;
    private Train train;
    private long eventStartTime = 0;//TODO forbid saving the game during the trap event
    private int eventDuration;
    private String deathMessage = "";
    private String hint = "";
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

                if (everyNSecondsDo(197)) {//update the background music every 197 seconds
                    Atmospheric.updateBackgroundMusic();
                }

                if (everyNSecondsDo(5)) {
                    updateEntitiesPrevPos();
                    //if wagon has trap inside, start the trap event
                    if (Checker.checkIfWagonHasTrap(wagon.getObjectsArray())) generateAndSetTrap();
                    //if trap event is active, check if it should be ended
                    if (Events.getCurrentEvent() != Constants.Event.DEFAULT_EVENT) checkIfAllEnemiesAreDead();

                    if (Events.shouldCallGuard() && !Events.isGuardCalled()) {
                        logger.debug("Guard should be called");
                        //call with delay
                        Events.setShouldCallGuard(false);
                        Events.setGuardCalled(true);
                        Thread guardCall = new Thread(() -> {
                            try {
                                logger.debug("Trying to call the guard...");
                                Thread.sleep(5000);
                            } catch (InterruptedException e) {
                                logger.error("Guard call interrupted", e);
                            }
                            Platform.runLater(() -> {//to avoid modifying the entities list from a different thread
                                callGuard();
                            });
                        });
                        guardCall.start();
                    }
                    if (!Events.isConductorSpawned()) {
                        //spawn with delay
                        Events.setConductorSpawned(true);
                        Thread conductorSpawn = new Thread(() -> {
                            try {
                                Thread.sleep(3000);//30 seconds before the conductor spawns
                            } catch (InterruptedException e) {
                                logger.error("Conductor spawn interrupted", e);
                            }
                            Platform.runLater(() -> {//to avoid modifying the entities list from a different thread
                                spawnConductor();
                            });
                        });
                        conductorSpawn.start();
                    }
                    if (Events.isConductorSpawned() && conductor != null && !conductor.isAlive()) {
                        deathMessage = "You are doomed to wander the train forever";
                        stopGame();
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
                    Events.setNextEvent(Constants.Event.BOSS_EVENT);
                    Atmospheric.fadeOutMusic(0.00);
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
            logger.debug("Trap event generated: " + Events.getNextEvent());
        }
        activateTrap();
    }

    /**
     * Activate the trap event if the time is up (if the player does not escape from the trap in time).
     */
    private void activateTrap() {
        if ((time - eventStartTime != 0) && (time - eventStartTime) >= eventDuration && Events.getCurrentEvent() == Constants.Event.DEFAULT_EVENT) {
            logger.info("Trap event activated: " + Events.getNextEvent());
            Events.setCurrentEvent(Events.getNextEvent());
            Events.setNextEvent(Constants.Event.DEFAULT_EVENT);
            if (Events.getCurrentEvent() == Constants.Event.TRAP_EVENT) {
                spawnEnemies();
            }
            if (Events.getCurrentEvent() == Constants.Event.BOSS_EVENT) {
                Events.setBossFight(true);
                Events.setCurrentEvent(Constants.Event.TRAP_EVENT);//to lock the doors
                String bossName = Constants.WAGON_TYPES_BOSSES.get(wagon.getType());
                spawnBoss(bossName);
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
        if (conductor != null) {
            conductor.setCounter(0);
            conductor.getPreviousPositions().clear();
        }
        if (grandmother != null) {
            grandmother.setCounter(0);
            grandmother.getPreviousPositions().clear();
        }
    }

    /**
     * Check if all entities from trap event are dead and remove the trap if they are.
     */
    private void checkIfAllEnemiesAreDead() {
        //no reward for default event
        if (Events.getCurrentEvent() == Constants.Event.DEFAULT_EVENT) return;

        int count = 0;
        for (Entity entity : wagon.getEntities()) {
            if (entity != null && entity.isAlive()) {
                if (entity.getType() == Constants.EntityType.BOSS || entity.getType() == Constants.EntityType.ENEMY) {
                    count++;
                }
            }
        }
        if (count == 0 || Events.getCurrentEvent() == Constants.Event.TIME_LOOP_EVENT || Events.getCurrentEvent() == Constants.Event.SILENCE_EVENT) {
            //reward the player only if there is trap event
            if (Events.getCurrentEvent() == Constants.Event.TRAP_EVENT || Events.getCurrentEvent() == Constants.Event.BOSS_EVENT) {
                //random reward for escaping the trap
                int moneyReward = (int) (Math.random() * Constants.MAX_TRAP_REWARD) + Constants.MIN_TRAP_REWARD;
                int ammoReward = (int) (Math.random() * Constants.MAX_TRAP_REWARD) + Constants.MIN_TRAP_REWARD;
                Events.setCurrentEvent(Constants.Event.DEFAULT_EVENT);
                Events.setBossFight(false);
                if (boss != null) {
                    //increase the reward if the player defeats the boss
                    moneyReward += Constants.MAX_TRAP_REWARD;
                    ammoReward += Constants.MAX_TRAP_REWARD;
                }
                logger.info("Trap event reward: " + moneyReward + " money, " + ammoReward + " ammo");
                player.getPlayerInventory().setMoney(player.getPlayerInventory().getMoney() + moneyReward);
                player.getPlayerInventory().setAmmo(player.getPlayerInventory().getAmmo() + ammoReward);
                logger.info("Trap event ended");
            }
            if (Checker.checkIfWagonHasTrap(wagon.getObjectsArray())) {
                deathMessage = "";
                boss = null;
                Atmospheric.stopBossMusic();
                eventStartTime = 0;
                eventDuration = 0;
                wagon.removeTrap();
                wagon.getDoorLeft().unlock();
                wagon.getDoorRight().unlock();
                isometric.setObjectsToDraw(wagon.getObjectsArray());
                isometric.updateAll();
                logger.debug("Trap event - doors unlocked");
            }
        }
    }

    /**
     * Spawn random number of enemies for the trap event.
     */
    private void spawnEnemies() {
        int enemyCount = Math.max(Constants.MIN_TRAP_ENEMIES_COUNT, (int) (Math.random() * Constants.MAX_TRAP_ENEMIES_COUNT));
        for (int i = 0; i < enemyCount; i++) {
            Entity enemy = EntitiesCreator.createRandomEnemy(wagon.getType());
            if (enemy == null) continue;

            logger.debug("Setting enemy position...");
            Object[] freeSpace = wagon.getAllFloorObjects();
            Object freeSpaceObject = freeSpace[(int) (Math.random() * freeSpace.length)];
            enemy.setPositionX(freeSpaceObject.getIsoX() - 32);
            enemy.setPositionY(freeSpaceObject.getIsoY() - 80);
            enemy.setCurrentWagon(wagon);
            wagon.addEntity(enemy);
            logger.info("Enemy spawned in wagon ID " + wagon.getId() + " at " + enemy.getPositionX() + ", " + enemy.getPositionY());
        }
        isometric.setEntities(wagon.getEntities());//otherwise the entities' hitboxes are drawn in the wrong place
        isometric.updateAll();
    }

    /**
     * Update player's position and stats.
     */
    private void updatePlayer() {
        isometric.updatePlayerPosition();
        isometric.drawPlayerMainHandSlot(player.getHandItem());
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
            hint = (player.getHandItem() != null && player.getHandItem().getType() == Constants.ItemType.KEY) ? "Press E to unlock" : "Locked";
        } else if (interactiveObject != null) {
            hint = "Press E to open";
        } else if (Checker.checkIfPlayerCanSpeak(player, wagon.getEntities()) != null) {
            hint = "Press E to speak";
        } else {
            hint = "";
        }
        isometric.updateHint(hint, (int) player.getPositionX(), (int) player.getPositionY() - 25);
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
        train = game.getTrain();
        wagon = game.getCurrentWagon();
        player = game.getPlayer();
        isometric = new Isometric();
        loadGame(player, wagon);
        start();
        logger.info("Game restarted");
    }

    /**
     * Reset all game variables.
     */
    private void resetAll() {
        leftWagonPursuers.clear();
        rightWagonPursuers.clear();
        kidnapStage = 0;
        kidnapStageCount = 3;
        time = 0;
        player = null;
        boss = null;
        eventStartTime = 0;
        eventDuration = 0;
        deathMessage = "";
        hint = "";
        Atmospheric.resetAll();
        logger.info("Game reset");
    }

    /**
     * Start the game.
     */
    public void start() {
        logger.info("Starting game...");
        isometric.start();
        this.wagon.setObstacles(isometric.getWalls());
        logger.debug("Wagon obstacles set");
        //update all wagons
        logger.debug("Checking wagons...");
        for (Wagon wagon : train.getWagonsArray()) {
            if (wagon != null) {
                isometric.initialiseWagon(wagon);
                wagon.setObstacles(isometric.getWalls());
                for (Entity entity : wagon.getEntities()) {
                    if (entity.getType() == Constants.EntityType.CONDUCTOR) {
                        if (entity.getName().equals(Constants.CONDUCTOR)) {
                            Events.setConductorSpawned(true);
                            conductor = entity;
                            logger.debug("Found conductor in wagon " + wagon.getId());
                        }
                        if (entity.getName().equals(Constants.GRANDMOTHER)) {
                            Events.setConductorSpawned(true);
                            grandmother = entity;
                            logger.debug("Found grandmother in wagon " + wagon.getId());
                        }
                    }
                }
                isometric.updateAll();
            }
        }
        isometric.initialiseWagon(wagon);

        logger.debug("Setting entities positions...");
        if (!wagon.getEntities().isEmpty()) {
            if (wagon.getEntities().getFirst().getPositionX() == 0) {
                int counter = 0;
                for (Object[] objects : wagon.getObjectsArray()) {
                    for (Object object : objects) {
                        if (object != null) {
                            if (object.getHeight() == 0) {
                                String letterId = object.getTwoLetterId();
                                if (letterId.equals(Constants.ENEMY_SPAWN) || letterId.equals(Constants.VENDOR_SPAWN) || letterId.equals(Constants.NPC_SPAWN) || letterId.equals(Constants.QUEST_SPAWN)) {
                                    wagon.getEntities().get(counter).setPositionX(object.getIsoX() - 32);
                                    wagon.getEntities().get(counter).setPositionY(object.getIsoY() - 80);
                                    logger.debug("Entity position set: " + wagon.getEntities().get(counter).getName() + " in wagon " + wagon.getId() + " at " + wagon.getEntities().get(counter).getPositionX() + ", " + wagon.getEntities().get(counter).getPositionY());
                                    counter++;
                                }
                            }
                        }
                    }
                }
            }
        }
        isometric.setEntities(wagon.getEntities());
        isometric.updateAll();
        timer.start();
        logger.info("Game started");
    }

    /**
     * Pause the game.
     */
    public void pauseGame() {
        logger.info("Game paused");
        timer.stop();
    }

    /**
     * Show the pause screen.
     */
    private void showPauseScreen() {
        pauseGame();

        Atmospheric.playPauseScreenMusic();
        //save game scene
        Scene isoScene = stage.getScene();

        GUI.initPauseScreen();
        GUI.initConfirmScene();

        EventHandler<? super KeyEvent> onEscResume = keyEvent -> {
            if (keyEvent.getCode().toString().equals("ESCAPE")) {
                Atmospheric.stopPauseScreenMusic();
                stage.setScene(isoScene);
                resumeGame();
            }
        };

        EventHandler<? super KeyEvent> onEscPause = keyEvent -> {
            if (keyEvent.getCode().toString().equals("ESCAPE")) {
                GUI.initConfirmScene();
                stage.setScene(GUI.pauseScreen);
            }
        };

        EventHandler<? super KeyEvent> onEnterResume = keyEvent -> {
            if (keyEvent.getCode().toString().equals("ENTER")) {
                Atmospheric.stopPauseScreenMusic();
                stage.setScene(isoScene);
                resumeGame();
            }
        };

        EventHandler<ActionEvent> cancel = actionEvent -> {
            GUI.initConfirmScene();
            stage.setScene(GUI.pauseScreen);
        };

        GUI.resumeButton.setOnAction(actionEvent -> {
            Atmospheric.stopPauseScreenMusic();
            stage.setScene(isoScene);
            resumeGame();
        });

        GUI.mainMenuButton.setOnAction(actionEvent -> {
            GUI.confirmButton.setOnAction(actionEvent1 -> {
                stage.setScene(null);
                mainMenu();
            });
            GUI.cancelButton.setOnAction(cancel);
            GUI.confirmScene.setOnKeyPressed(keyEvent -> {
                onEscPause.handle(keyEvent);
                if (keyEvent.getCode().toString().equals("ENTER")) {
                    stage.setScene(null);
                    mainMenu();
                }
            });
            stage.setScene(GUI.confirmScene);
        });

        GUI.exitButton.setOnAction(actionEvent -> {
            GUI.confirmButton.setOnAction(actionEvent1 -> {
                stage.close();
                System.exit(0);
            });
            GUI.cancelButton.setOnAction(cancel);
            GUI.confirmScene.setOnKeyPressed(keyEvent -> {
                onEscPause.handle(keyEvent);
                if (keyEvent.getCode().toString().equals("ENTER")) {
                    stage.close();
                    System.exit(0);
                }
            });
            stage.setScene(GUI.confirmScene);
        });

        GUI.saveButton.setOnAction(actionEvent -> {
            Events.setCanSaveGame(Events.getCurrentEvent() == Constants.Event.DEFAULT_EVENT && leftWagonPursuers.isEmpty() && rightWagonPursuers.isEmpty());
            if (Events.canSaveGame()) {
                saveGame();
                GUI.initGameSavedScreen();
            } else {
                GUI.initCannotSaveGameScreen();
            }
            GUI.confirmButton.setOnAction(actionEvent1 -> {
                Atmospheric.stopPauseScreenMusic();
                stage.setScene(isoScene);
                resumeGame();
            });
            GUI.cancelButton.setOnAction(actionEvent1 -> {
                stage.setScene(null);
                mainMenu();
            });
            stage.setScene(GUI.confirmScene);
            GUI.confirmScene.setOnKeyPressed(keyEvent -> {
                onEscPause.handle(keyEvent);
                onEnterResume.handle(keyEvent);
            });
        });

        GUI.pauseScreen.onKeyPressedProperty().set(onEscResume);
        stage.setScene(GUI.pauseScreen);
    }

    /**
     * Resume the game.
     */
    public void resumeGame() {
        logger.info("Game resumed");
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
        logger.info("Game stopped");
        Scene isoScene = stage.getScene();
        Atmospheric.fadeOutMusic(0.00);
        GUI.initDeathScreen(deathMessage);
        logger.info("Game over with message: " + deathMessage);
        GUI.confirmButton.setOnAction(actionEvent1 -> {
            stage.setScene(isoScene);
            restartGame();
        });
        GUI.cancelButton.setOnAction(actionEvent1 -> {
            stage.setScene(null);
            mainMenu();
        });
        stage.setScene(GUI.confirmScene);
    }

    /**
     * Set the handle for the player movement and interaction.
     */
    private void setPlayerHandle() {
        logger.debug("Setting player handle...");
        Scene scene = stage.getScene();

        //handle movement
        EventHandler<? super KeyEvent> movement_handle = keyEvent -> {
            switch (keyEvent.getCode()) {
                case W:
                    if (!player.getTexturePath().contains("back")) isometric.updatePlayerTexture("textures/default/entities/player/player_back.png");
                    isometric.updatePlayerDeltaY(-Constants.PLAYER_BASIC_SPEED_Y);
                    break;
                case S:
                    if (!player.getTexturePath().contains("front")) isometric.updatePlayerTexture("textures/default/entities/player/player_front.png");
                    isometric.updatePlayerDeltaY(Constants.PLAYER_BASIC_SPEED_Y);
                    break;
                case A:
                    if (!player.getTexturePath().contains("left")) isometric.updatePlayerTexture("textures/default/entities/player/player_left.png");
                    isometric.updatePlayerDeltaX(-Constants.PLAYER_BASIC_SPEED_X);
                    break;
                case D:
                    if (!player.getTexturePath().contains("right")) isometric.updatePlayerTexture("textures/default/entities/player/player_right.png");
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
        logger.debug("Player handle set");
    }

    /**
     * Play the player's footsteps sound and show the player's footsteps animation.
     */
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

    /**
     * Stop the player's footsteps sound and show the player's normal texture.
     */
    private void stopPlayerFootsteps() {
        //stop the previous thread if it exists
        if (player.getSoundThread() != null) player.getSoundThread().interrupt();
        //reset the texture
        player.setTexturePath(player.getTexturePath().replaceAll("_[12]", ""));
        //set the thread to null
        player.setSoundThread(null);
    }

    /**
     * Handle walking through the wagon door by the player.
     * @param object door to be opened
     */
    private void handleWagonDoor(Door object) {
        if (Events.getCurrentEvent() == Constants.Event.TRAP_EVENT) {
            logger.info("Player tried to open the door during the trap event");
            object.lock();//player cannot escape from the trap even if he has the key to open the door
        }
        if (object.isLocked()) {
            if (Checker.checkIfPlayerHasKeyInMainHand(player)) {
                if (conductor.getCurrentWagon() == player.getCurrentWagon()) {
                    logger.info("Player tried to open the door behind the conductor");
                    //player cannot unlock the door behind the conductor
                    //TODO: play wrong key sound
                    return;
                }
                player.getPlayerInventory().setMainHandItem(null);//remove the key from the player's main hand
                object.unlock();
                //TODO: play unlock sound
            }
        } else {
            //TODO: play door sound
            if (Events.getTimeLoopCounter() > 0) {
                logger.info("Player tried to open the door during the time loop event");
                handleTimeLoop(object);
                return;//do not teleport the player to the next wagon
            }
            logger.info("Player opened the wagon door");
            openWagonDoor(object);
        }
    }

    /**
     * Handle time loop event.
     * @param object door to be opened
     */
    private void handleTimeLoop(Door object) {
        logger.debug("Time loop event loops count: " + Events.getTimeLoopCounter());
        Door door = (object == wagon.getDoorLeft()) ? wagon.getDoorLeft() : wagon.getDoorRight();
        Object actualTeleport = door.getTeleport();
        door.setTeleport((object == wagon.getDoorLeft()) ? wagon.getDoorRightTarget() : wagon.getDoorLeftTarget());
        door.teleport(player);
        door.setTeleport(actualTeleport);

        Events.decrementTimeLoopCounter();
        if (Events.getTimeLoopCounter() == 0) {
            logger.info("Time loop event ended");
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
        logger.info("Opening dialogue with " + entity.getName());
        if (entity instanceof QuestNPC questNPC) {
            entity.setDialoguePath(entity.getDialoguePath());
            if (questNPC.isQuestCompleted()) {
                logger.info(questNPC.getName() + " quest completed");
                entity.setDialoguePath(entity.getName() + "_completed.json");
                player.getPlayerInventory().addItem(RandomHandler.getRandomFoodItem());
            }
            Dialogue questDialogue = new Dialogue(entity.getDialoguePath());
            questDialogue.setEntity(entity);
            setDialogueHandle(questDialogue, entity);
            if (!questNPC.isQuestCompleted()) {
                questNPC.setQuestCompleted(questNPC.checkIfPlayerHasQuestItem(player));
                questNPC.setDialoguePath(entity.getName() + "_default.json");
            }
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
        logger.debug("Setting dialogue handle...");
        Scene dialogueScene = dialogue.openDialogue();
        stage.setScene(dialogueScene);
        //add other onKeyReleased handlers
        EventHandler<? super KeyEvent> prev_handle = dialogueScene.getOnKeyReleased();
        dialogueScene.setOnKeyReleased(keyEvent -> {
            prev_handle.handle(keyEvent);
            if (Objects.equals(dialogue.getAction(), "trade")) {
                logger.info("Dialogue action: trade with " + dialogueEntity.getName());
                dialogue.closeDialogue();
                stage.setScene(scene);
                dialogue.setAction(null);
                openTradeWindow((Vendor) dialogueEntity);
                return;
            }
            if (dialogue.getAction() != null) {
                handleDialogueAction(dialogue.getAction(), dialogueEntity);
                dialogue.setAction(null);
                if (Objects.equals(dialogueEntity.getBehaviour(), Constants.Behaviour.AGGRESSIVE)) {
                    logger.info("Dialogue action: " + dialogueEntity.getName() + " is aggressive, ending dialogue");
                    dialogue.closeDialogue();
                    stage.setScene(scene);
                    setPlayerHandle();
                    resumeGame();
                    return;
                }
            }
            if (Objects.requireNonNull(keyEvent.getCode()) == KeyCode.TAB) {
                logger.info("Closing dialogue with " + dialogueEntity.getName());
                dialogue.closeDialogue();
                stage.setScene(scene);
                setPlayerHandle();
                resumeGame();
            }
        });
        logger.debug("Dialogue handle set");
    }

    /**
     * Open the trade window with the vendor.
     * @param vendor vendor to trade with
     */
    private void openTradeWindow(Vendor vendor) {
        logger.info("Opening trade window with " + vendor.getName() + "...");
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
                        logger.info("Player bought: " + takenItems);
                        for (Item item : itemsToRemove) {
                            if (player.getPlayerInventory().getMoney() >= item.getValue()) {
                                if (player.getPlayerInventory().addItem(item)) {
                                    logger.info("Player bought " + item.getName() + " for " + item.getValue() + " money");
                                    inventory.removeTakenItem(item);
                                    player.getPlayerInventory().setMoney(player.getPlayerInventory().getMoney() - item.getValue());
                                    addedItems.add(item);
                                    inventory.removeTakenItem(item);
                                } else {
                                    //vendor will not be angry if the player does not have enough space in the inventory
                                    logger.info("Player does not have enough space in the inventory");
                                    returnItems(addedItems, itemsToRemove, inventory);
                                    break;
                                }
                            } else {
                                //if the player does not have enough money, the vendor gets negative points
                                logger.info("Player does not have enough money to buy " + item.getName());
                                vendor.setNegativeCount(vendor.getNegativeCount() + 1);
                                returnItems(addedItems, itemsToRemove, inventory);
                                break;
                            }
                        }
                    }
                    logger.info("Trade window with " + vendor.getName() + " closed");
                    inventory.closeInventory(stage);
                    stage.setScene(scene);
                    setPlayerHandle();
                    resumeGame();
                }
            });
            logger.info("Trade window with " + vendor.getName() + " opened");
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
                handleResponse(dialogueEntity);
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
                deathMessage = "You escaped the train";
                stopGame();
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
        Events.setGuardCalled(false);
        eventStartTime = 0;
        eventDuration = 0;
        if (door.getTargetId() == -1) {
            logger.info("No wagon behind the door, generating new wagon...");
            generateNextWagonAndOpenDoor(door);
        } else {
            logger.info("Existing wagon behind the door, opening the door...");
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

    /**
     * Generate the left wagon.
     * @param nextWagon next wagon to be generated
     */
    private void generateLeftWagon(Wagon nextWagon) {
        logger.debug("Generating left wagon...");
        setPursuers(rightWagonPursuers);

        nextWagon.generateNextWagon(wagon, true);
        train.addWagon(nextWagon);

        if (wagon.getId() == Constants.TRAIN_WAGONS - 3 && !Events.isGrandmotherSpawned()) {
            logger.debug("Generating grandmother in wagon " + nextWagon.getId() + "...");

            String wagonType = "CARGO";
            Wagon nextNextWagon = new Wagon(train.findMaxWagonId() + 1, wagonType);
            nextNextWagon.generateNextWagon(nextWagon, true);
            train.addWagon(nextNextWagon);

            isometric.initialiseWagon(nextNextWagon);
            isometric.updateAll();
            nextNextWagon.setObstacles(isometric.getWalls());

            spawnGrandmother(nextNextWagon);
        }

        isometric.initialiseWagon(nextWagon);
        isometric.updateAll();
        nextWagon.setObstacles(isometric.getWalls());
        nextWagon.getDoorRight().unlock();//player already unlocked the door (locked door can spawn when generating the wagon)

        wagon.getDoorLeft().teleport(player);
        this.wagon = nextWagon;
        player.setCurrentWagon(wagon);
    }

    /**
     * Generate the right wagon.
     * @param nextWagon next wagon to be generated
     */
    private void generateRightWagon(Wagon nextWagon) {
        logger.debug("Generating right wagon...");
        setPursuers(leftWagonPursuers);

        nextWagon.generateNextWagon(wagon, false);
        train.addWagon(nextWagon);

        isometric.initialiseWagon(nextWagon);
        isometric.updateAll();
        nextWagon.setObstacles(isometric.getWalls());
        nextWagon.getDoorLeft().unlock();//player already unlocked the door

        wagon.getDoorRight().teleport(player);
        this.wagon = nextWagon;
        player.setCurrentWagon(wagon);
    }

    /**
     * Spawn grandmother in the wagon.
     * @param wagon wagon to spawn the grandmother in
     */
    private void spawnGrandmother(Wagon wagon) {
        logger.debug("Spawning grandmother in wagon " + wagon.getId() + "...");
        EntitiesCreator.createGrandmother(wagon);
        grandmother = wagon.getEntities().getLast();
        isometric.setEntities(wagon.getEntities());
        isometric.updateAll();
        Events.setGrandmotherSpawned(true);
        isometric.setEntities(this.wagon.getEntities());
        isometric.updateAll();
        logger.info("Grandmother spawned at " + grandmother.getPositionX() + ", " + grandmother.getPositionY());
    }

    /**
     * Spawn conductor.
     */
    private void spawnConductor() {
        Wagon wagon = train.getWagonById(train.findMinWagonId());//get the oldest wagon
        logger.debug("Spawning conductor in wagon " + wagon.getId() + "...");
        EntitiesCreator.createConductor(wagon);
        conductor = wagon.getEntities().getLast();
        isometric.setEntities(wagon.getEntities());
        isometric.updateAll();
        Events.setCanSpawnKey(true);
        isometric.setEntities(this.wagon.getEntities());
        isometric.updateAll();
        logger.info("Conductor spawned at " + conductor.getPositionX() + ", " + conductor.getPositionY());
    }

    private void setPursuers(List<Entity> wagonPursuers) {
        logger.debug("Looking for pursuers in the wagon");
        wagonPursuers.clear();
        for (Entity entity : wagon.getEntities()) {
            if (entity.getBehaviour() == Constants.Behaviour.AGGRESSIVE && entity.getType() != Constants.EntityType.CONDUCTOR) {//conductor uses different logic
                if (entity.getIntelligence() == 2) {
                    if (Checker.checkIfEntityRemember(entity, player, isometric.getTwoAndTallerWalls(), time)) {
                        logger.info("Entity " + entity.getName() + " remembered the player and will pursue him");
                        wagonPursuers.add(entity);
                    }
                }
            }
        }
        logger.debug("Pursuers: " + wagonPursuers.size());
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
        logger.debug("Using lockable door...");
        if (object.isSolid()) {
            object.setIsSolid(false);
            object.setTexturePath("textures/default/objects/interactive_objects/lockable_door/lockable_door_1_opened.png");
            logger.info("Door opened");
        } else {
            object.setIsSolid(true);
            object.setTexturePath("textures/default/objects/interactive_objects/lockable_door/lockable_door_1_closed.png");
            logger.info("Door closed");
        }
        isometric.updateAll();
    }

    /**
     * Set the handle for the inventory: check if the inventory is not null, clear the scene and the player handle,
     * pause the game, set the handle for the inventory.
     * When the inventory is closed, the handle for the player is set again and the game is resumed.
     * @param inventory inventory to be handled
     */
    private void setInventoryHandle(Inventory inventory) {
        logger.info("Opening inventory...");
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
                                logger.info("Player took " + item.getName());
                            } else {
                                logger.info("Player does not have enough space in the inventory");
                                returnItems(addedItems, itemsToRemove, inventory);
                                break;
                            }
                        }
                    }
                    logger.info("Inventory closed");
                    inventory.closeInventory(stage);
                    stage.setScene(scene);
                    setPlayerHandle();
                    resumeGame();
                }
            });
            logger.info("Inventory opened");
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
        logger.info("Opening player inventory...");

        Scene scene = stage.getScene();
        resetSceneHandlers(scene);//unnecessary
        pauseGame();

        Scene playerInventory = player.getPlayerInventory().openInventory();
        stage.setScene(playerInventory);

        playerInventory.setOnKeyPressed(keyEvent -> {
            if (Objects.requireNonNull(keyEvent.getCode()) == KeyCode.TAB) {
                logger.info("Player inventory closed");
                player.getPlayerInventory().closeInventory(stage);
                stage.setScene(scene);
                setPlayerHandle();//unnecessary
                resumeGame();
            }
        });
        logger.info("Player inventory opened");
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
        isometric.initialiseWagon(this.wagon);
        setPlayerHandle();
    }
    /**
     * Save the game.
     */
    public void saveGame() {
        GameSaver game = new GameSaver(player, train, train.indexOf(wagon));
        game.saveGame();
    }
    /**
     * Show the main menu.
     */
    public void mainMenu() {
        isometric.clearAll();
        Scene mainMenuScene = Game.showMainMenuScene();
        stage.setScene(mainMenuScene);
    }
    /**
     * Update the entities.
     */
    public void updateEntities() {
        List<Entity> entities = wagon.getEntities();
        if (entities == null) return;

        List<Entity> entitiesToRemove = new ArrayList<>();
        boolean shouldRemove = false;
        for (Entity entity : entities) {
            if (entity == null) continue;

            if (!entity.isAlive()) {
                if (entity.getType() == Constants.EntityType.GUARD) {
                    Events.setPlayerKilledGuard(true);
                    Events.setGuardCalled(false);
                }
                entitiesToRemove.add(entity);
                shouldRemove = true;
                continue;
            }
            if (entity.getNegativeCount() >= entity.getNegativeThreshold()) {
                handleResponse(entity);
            }
            if (entity.getBehaviour() != Constants.Behaviour.NEUTRAL) {
                if (entity.getBehaviour() == Constants.Behaviour.AGGRESSIVE) {
                    entity.tryAttack(entity, List.of(player), time);
                }
                if (entity.getBehaviour() == Constants.Behaviour.BULLY) {
                    handleBully(entity);
                }
            }
        }
        if (shouldRemove) {
            wagon.getEntities().removeAll(entitiesToRemove);
            isometric.setEntities(wagon.getEntities());
            isometric.updateAll();
        }
        moveEntities();
        if (boss != null) {
            handleBoss();
        }
    }

    /**
     * Handle entity response/reaction.
     * @param entity entity to handle
     */
    private void handleResponse(Entity entity) {
        if (Math.random() < 0.5) {
            entity.setBehaviour(Constants.Behaviour.AGGRESSIVE);
        } else {
            Events.setShouldCallGuard(true);
        }
    }

    /**
     * Call the guard (spawn the guard).
     */
    private void callGuard() {
        if (Events.getCurrentEvent() == Constants.Event.TRAP_EVENT) {//guard was called during the trap event, needs to be handled differently (reimplemented)
            logger.info("Trying to call the guard during the trap event, guard will not be called");
            return;
        }
        logger.debug("Calling the guard...");
        Entity guard = EntitiesCreator.createGuard();
        guard.setBehaviour(Constants.Behaviour.BULLY);
        guard.setCurrentWagon(wagon);
        //add delay
        guard.setPositionX(wagon.getDoorLeftTarget().getIsoX() - 32);
        guard.setPositionY(wagon.getDoorLeftTarget().getIsoY() - 64);
        wagon.addEntity(guard);
        isometric.setEntities(wagon.getEntities());
        isometric.updateAll();
        guard.setStartIndex(guard.findOnWhatObject());
        logger.info("Guard called");
    }

    /**
     * Handle bully entity.
     * @param entity entity to be handled
     */
    private void handleBully(Entity entity) {
        if (Checker.checkIfEntityRemember(entity, player, isometric.getTwoAndTallerWalls(), time)) {
            if (Checker.checkCollision(entity.getAttackRange(), player.getHitbox())) {
                if (kidnapStage != kidnapStageCount) {
                    if (entity.getCanAttack()) {
                        kidnapStage++;
                        entity.setCanAttack(false);
                        entity.setWhenAttacked(time);
                    }
                    if (!entity.getCanAttack() && (time - entity.getWhenAttacked() != 0) && (time - entity.getWhenAttacked()) % entity.getCooldown() == 0) {
                        entity.setCanAttack(true);
                        entity.setWhenAttacked(0);
                    }
                    isometric.showKidnappingProgress(kidnapStageCount, kidnapStage);
                }
            } else {
                kidnapStage = 0;//reset the kidnap stage if the player is not in the attack range
            }
            if (kidnapStage == kidnapStageCount) {
                entity.setSpeedX(3);
                entity.setSpeedY(3);
                Events.setPlayerKidnapped(true);
                resetSceneHandlers(stage.getScene());
                Door door;

                if (Math.abs(entity.getPositionX() - wagon.getDoorLeft().getIsoX()) < Math.abs(entity.getPositionX() - wagon.getDoorRight().getIsoX())) {
                    door = wagon.getDoorLeft();
                } else {
                    door = wagon.getDoorRight();
                }
                Circle playerHitbox = (Circle) player.getHitbox();
                playerHitbox.setRadius(0.1);
                player.setPositionX(entity.getPositionX() - 15);
                player.setPositionY(entity.getPositionY() - 15);
                moveTowardsDoor(entity, door);
                if (Checker.checkIfEntityStuck(entity)) {
                    int oldSpeedX = entity.getSpeedX();
                    int oldSpeedY = entity.getSpeedY();
                    entity.setSpeedX(entity.getSpeedX() + 3);
                    entity.setSpeedY(entity.getSpeedY() + 3);
                    moveTowardsDoor(entity, door);
                    entity.setSpeedX(oldSpeedX);
                    entity.setSpeedY(oldSpeedY);
                }
                if (Checker.checkCollision(entity.getAttackRange(), door.getObjectHitbox())) {
                    deathMessage = "You were thrown out of the train!";
                    stopGame();
                }
            }
        }
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
        List<Entity> entities = wagon.getEntities();
        if (entities == null) return;

        for (Entity entity : entities) {
            if (entity != null && entity.isAlive() && !Objects.equals(entity.getBehaviour(), Constants.Behaviour.NEUTRAL) && !Events.isPlayerKidnapped()) {
                //check if the entity remembers the player
                if (Checker.checkIfEntityRemember(entity, player, isometric.getTwoAndTallerWalls(), time)) {
                    //move the entity with appropriate intelligence
                    switch (entity.getIntelligence()) {
                        case 0 -> intelligenceZeroPursue(entity, player);
                        case 1 -> intelligenceOnePursue(entity, player);
                        case 2 -> intelligenceTwoPursue(entity, player);
                        default -> { return; }
                    }
                    //check if the entity is stuck
                    if (Checker.checkIfEntityStuck(entity)) {
                        intelligenceZeroPursue(entity, player);
                    }
                } else {
                    returnToStart(wagon.getMapForPathFinder(false), entity);
                }
            }
        }
        if (Events.getCurrentEvent() != Constants.Event.TRAP_EVENT) {
            //move pursuers and conductors only if there is no event
            if (!leftWagonPursuers.isEmpty()) {
                movePursuers(leftWagonPursuers, leftWagonPursuers.getFirst().getCurrentWagon().getDoorRight());
            }
            if (!rightWagonPursuers.isEmpty()) {
                movePursuers(rightWagonPursuers, rightWagonPursuers.getFirst().getCurrentWagon().getDoorLeft());
            }
            moveConductor(conductor);
            moveConductor(grandmother);
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
            if (Objects.equals(object.getTwoLetterId(), Constants.LOCKABLE_DOOR) && object.isSolid()) {
                useLockableDoor(object);
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
        if (path == null || path.length == 0) {
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

        Object object = wagon.getObjectsArray()[index[0]][index[1]];

        int x = (int) object.getIsoX();
        int y = (int) object.getIsoY() + (object.getHeight() != 0 ? object.getHeight() * Constants.TILE_HEIGHT : 0);

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

    /**
     * Move the entity in another wagon (handle entity movement in other than the player's wagon).
     * @param entity entity to be moved
     * @param deltaX delta x
     * @param deltaY delta y
     */
    private void moveEntityInOtherWagon(Entity entity, int deltaX, int deltaY) {
        Shape oldWalls = isometric.getWalls();
        isometric.setWalls(entity.getCurrentWagon().getObstacles());
        isometric.updateEntityPosition(entity, deltaX, deltaY, entity.getSpeedX(), entity.getSpeedX());
        isometric.setWalls(oldWalls);
    }

    /**
     * Move the pursuers to the next wagon.
     * @param pursuers pursuers to be moved
     * @param wagonDoor door to be opened
     */
    private void movePursuers(List<Entity> pursuers, Door wagonDoor) {
        //get copy of the list to avoid ConcurrentModificationException
        List<Entity> wagonPursuers = new LinkedList<>(pursuers);
        for (Entity pursuer : wagonPursuers) {
            if (pursuer != null) {
                if (player.getCurrentWagon() == pursuer.getCurrentWagon()) {
                    pursuers.remove(pursuer);
                    continue;
                }
                if (Checker.checkCollision(pursuer.getAttackRange(), wagonDoor.getObjectHitbox())) {
                    //teleport the pursuer to the next wagon
                    logger.info("Teleporting pursuer to the next wagon...");
                    wagonDoor.teleport(pursuer);
                    //remove from original list and current wagon
                    pursuers.remove(pursuer);
                    pursuer.getCurrentWagon().getEntities().remove(pursuer);
                    pursuer.setCurrentWagon(wagon);
                    wagon.addEntity(pursuer);
                    isometric.setEntities(wagon.getEntities());
                    isometric.updateAll();
                    continue;
                }
                //find target index
                moveTowardsDoor(pursuer, wagonDoor);
            }
        }
    }

    /**
     * Move conductor.
     * @param conductor conductor to be moved
     */
    private void moveConductor(Entity conductor) {
        if (conductor != null) {
            Door wagonDoor = conductor.getCurrentWagon().getDoorLeft();
            if (Checker.checkCollision(conductor.getAttackRange(), wagonDoor.getObjectHitbox())) {
                if (wagonDoor.isLocked()) {
                    wagonDoor.unlock();
                }
                if (wagonDoor.getTargetId() == -1) {
                    generateNextWagonByConductor(conductor);
                }
                //conductor will not move to the next wagon until checks player's ticket
                if (conductor == this.conductor) {
                    if (conductor.getCurrentWagon() != player.getCurrentWagon()) {//for the conductor
                        openExistingWagonDoorByConductor(conductor);
                    }
                } else {//for the grandmother
                    openExistingWagonDoorByConductor(conductor);
                }
            }
            if (conductor == this.conductor) {
                handleConductor();
            }
            if (conductor == grandmother) {
                handleGrandmother();
            }
        }
    }

    /**
     * Get the delta x and delta y for the entity.
     * @param entity entity to be moved
     * @param path path to be followed
     * @param map map for the pathfinder
     * @return delta x and delta y
     */
    private int[] getDeltaXY(Entity entity, int[][] path, int[][] map) {
        int[] deltaXY = new int[0];
        if (path.length >= 2) {
            deltaXY = path[path.length - 2];
        } else if (path.length == 1) {
            deltaXY = path[0];
        } else {
            if (Checker.checkIfEntityRemember(entity, player, isometric.getTwoAndTallerWalls(), time)) {
                //if remember - stay in the same place
                deltaXY = entity.findOnWhatObject();
            } else {
                returnToStart(map, entity);
            }
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

    /**
     * Handle conductor's logic.
     * Logic:
     * <ul>
     *    <li>If the player is near, conductor will start dialogue and check the ticket.</li>
     *    <li>If the player does not have the ticket, the conductor becomes aggressive.</li>
     *    <li>If conductor is aggressive, he will not start dialogue.</li>
     *    <li>If conductor is aggressive and the player is in the same wagon, he will pursue the player.</li>
     *    <li>If conductor is aggressive and the player is in another wagon, conductor will move normally.</li>
     *    <li>Forbid the player to go behind the conductor (conductor will lock doors behind him).</li>
     * </ul>
     * Note: There is a small (very small) chance for the conductor to become neutral again if the player is <b>not</b> in the same wagon.
     */
    private void handleConductor() {
        Constants.Behaviour conductorBehaviour = conductor.getBehaviour();
        Wagon conductorWagon = conductor.getCurrentWagon();
        Wagon playerWagon = player.getCurrentWagon();
        Door leftDoor = conductorWagon.getDoorLeft();
        Door rightDoor = conductorWagon.getDoorRight();

        if (!rightDoor.isLocked()) rightDoor.lock();
        if (Checker.checkIfEntityStuck(conductor)) handleConductorStuck(conductor);

        if (conductorBehaviour == Constants.Behaviour.NEUTRAL) {
            if (conductorWagon.getId() == playerWagon.getId()) {
                if (Checker.checkIfConductorNearPlayer(conductor, player)) {
                    logger.info("Conductor is near the player, starting dialogue...");
                    openDialogue(conductor);
                    //TODO: play conductor sound (read first sentence)
                }
            }
        } else {
            if (conductorWagon.getId() == playerWagon.getId()) {
                intelligenceTwoPursue(conductor, player);
                return;
            }
        }
        moveTowardsDoor(conductor, leftDoor);
    }

    /**
     * Handle grandmother's logic.
     * Logic:
     * <ul>
     *     <li>Forbid the player to go ahead of the grandmother (grandmother will lock doors ahead of her).</li>
     *     <li>Grandmother will not attack when aggressive but change the dialogue and call the guard.</li>
     *     <li>Player can trade with the grandmother (works like normal vendor).</li>
     * </ul>
     * Note: There is a small (very small) chance for the grandmother to become neutral again if the player is <b>not</b> in the same wagon.
     */
    private void handleGrandmother() {
        Constants.Behaviour conductorBehaviour = grandmother.getBehaviour();
        Wagon conductorWagon = grandmother.getCurrentWagon();
        Wagon playerWagon = player.getCurrentWagon();
        Door leftDoor = conductorWagon.getDoorLeft();

        if (conductorBehaviour == Constants.Behaviour.NEUTRAL) {
            moveTowardsDoor(grandmother, leftDoor);
        } else {
            if (conductorWagon.getId() == playerWagon.getId()) {
                grandmother.setBehaviour(Constants.Behaviour.NEUTRAL);
                grandmother.setDialoguePath("grandmother_aggressive.json");
                Events.setShouldCallGuard(true);
            } else {
                moveTowardsDoor(grandmother, leftDoor);
            }
        }
        if (!leftDoor.isLocked()) leftDoor.lock();
        if (Checker.checkIfEntityStuck(grandmother)) handleConductorStuck(grandmother);
    }

    /**
     * Move entity towards the door.
     * @param entity entity to be moved
     * @param wagonDoor door to be reached
     */
    private void moveTowardsDoor(Entity entity, Door wagonDoor) {
        for (int i = 0; i < entity.getCurrentWagon().getObjectsArray().length; i++) {
            for (int j = 0; j < entity.getCurrentWagon().getObjectsArray()[i].length; j++) {
                if (entity.getCurrentWagon().getObjectsArray()[i][j] == wagonDoor) {
                    int[] targetIndex = {i, j};
                    int[][] path = entity.findPath(entity.getCurrentWagon().getMapForPathFinder(true), targetIndex);
                    int[] deltaXY = getDeltaXY(entity, path, entity.getCurrentWagon().getMapForPathFinder(true));
                    moveEntity(entity, deltaXY);
                }
            }
        }
    }

    /**
     * Generate the next wagon and teleport the conductor to the next wagon.
     */
    private void generateNextWagonByConductor(Entity conductor) {
        logger.info("Generating next wagon by the " + conductor.getName() + "...");
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
    }

    /**
     * Open the existing wagon door and teleport the conductor to the next wagon.
     */
    private void openExistingWagonDoorByConductor(Entity conductor) {
        logger.info("Opening existing wagon door by the " + conductor.getName() + "...");
        if (player.getCurrentWagon().getId() == train.findMinWagonId()) {
            return;//do not open the door if the player is in the oldest wagon
        }
        Door wagonDoor = conductor.getCurrentWagon().getDoorLeft();
        if (wagonDoor.isLocked()) {
            wagonDoor.unlock();//unlock the next wagon door (mainly for the grandmother)
        }
        wagonDoor.teleport(conductor);
        Wagon nextWagon = train.getWagonById(wagonDoor.getTargetId());
        conductor.getCurrentWagon().getEntities().remove(conductor);
        conductor.setCurrentWagon(nextWagon);
        nextWagon.addEntity(conductor);
        //update all hitboxes
        isometric.setEntities(conductor.getCurrentWagon().getEntities());
        isometric.updateAll();
        isometric.setEntities(wagon.getEntities());
        isometric.updateAll();
    }

    /**
     * Handle the stuck conductor.
     * If the conductor is stuck, change strategy: speed up if the player is in the same wagon,
     * teleport to the next wagon if the player is in another wagon.
     */
    private void handleConductorStuck(Entity conductor) {
        logger.debug("Conductor " + conductor.getName() + " is stuck, changing strategy...");
        if (conductor.getCurrentWagon().getId() == player.getCurrentWagon().getId()) {
            int originalSpeedX = conductor.getSpeedX();
            int originalSpeedY = conductor.getSpeedY();
            conductor.setSpeedX(originalSpeedX * 2);
            conductor.setSpeedY(originalSpeedY * 2);
            intelligenceTwoPursue(conductor, player);
            conductor.setSpeedX(originalSpeedX);
            conductor.setSpeedY(originalSpeedY);
        } else {
            if (conductor.getCurrentWagon().getDoorLeft().getTargetId() == -1) {
                generateNextWagonByConductor(conductor);
            } else if (time % 5 == 0) {
                openExistingWagonDoorByConductor(conductor);
                if (Math.random() < 0.05) {
                    logger.info("Conductor " + conductor.getName() + " is no longer aggressive");
                    conductor.setBehaviour(Constants.Behaviour.NEUTRAL);
                    if (conductor.getDialoguePath().contains("aggressive")) {
                        //replace the aggressive dialogue with the default one
                        conductor.setDialoguePath(conductor.getDialoguePath().replace("aggressive", "default"));
                    }
                }
            }
        }
    }

    /**
     * Handle boss entity.
     */
    private void handleBoss() {
        if (Events.isBossFight()) {
            Atmospheric.startBossMusic(boss.getName());
            if (Checker.checkIfEntityCanSee(boss, player, isometric.getTwoAndTallerWalls())) {
                if (Checker.checkCollision(boss.getAttackRange(), player.getHitbox())) {
                    boss.tryAttack(boss, List.of(player), time);
                }
                bossAttack(player.getPositionX() + 32, player.getPositionY() + 80, boss.getAttackRangeSize(), boss.getCooldown() - 1);
            }
        }
    }

    /**
     * Handle the boss attack.
     * @param x x coordinate
     * @param y y coordinate
     * @param radius radius of the attack
     * @param seconds time for player to react
     * <br>
     * <br>
     * Note: Even if game is paused, the boss can still attack the player. It will <b>not</b> be reimplemented.
     */
    private void bossAttack(double x, double y, int radius, int seconds) {
        if (!Events.isBossFight() || !boss.getCanAttack()) return;

        isometric.showBossAttackTarget(x, y, radius, seconds);
        Thread attackTarget = new Thread(() -> {
            Circle attackCircle = new Circle(x, y, radius * Constants.TILE_WIDTH);
            try {
                logger.info(boss.getName() + " trying to attack, player has " + seconds + " seconds to react...");
                Thread.sleep(seconds * 1000L);
            } catch (InterruptedException e) {
                logger.error("Error while boss attacking: " + e.getMessage());
            }
            if (Checker.checkCollision(attackCircle, player.getHitbox())) {
                logger.info(boss.getName() + " attacked the player");
                player.takeDamage(boss.getDamage());
            }
        });
        attackTarget.start();
        boss.setCanAttack(false);
        boss.setWhenAttacked(time);

        if (!boss.getCanAttack() && (time - boss.getWhenAttacked() != 0) && (time - boss.getWhenAttacked()) % boss.getCooldown() == 0) {
            boss.setCanAttack(true);
            boss.setWhenAttacked(0);
        }
    }

    /**
     * Spawn the boss.
     * @param name name of the boss
     */
    private void spawnBoss(String name) {
        logger.debug("Spawning boss " + name + "...");
        Object trap = wagon.getTrap();
        boss = new Entity(name, "textures/default/entities/bosses/" + name + ".png", Constants.EntityType.BOSS, trap.getIsoX() - 48, trap.getIsoY() - 80);
        int[] bossStats = Constants.BOSS_STATS.get(name);
        EntitiesCreator.setAsBoss(boss, bossStats);
        boss.setCurrentWagon(wagon);
        wagon.addEntity(boss);
        isometric.setEntities(wagon.getEntities());
        isometric.updateAll();
        deathMessage = Constants.BOSS_DEATH_MESSAGES.get(name);
        logger.info("Boss " + name + " spawned at " + boss.getPositionX() + ", " + boss.getPositionY());
    }
}
