package cs.cvut.fel.pjv.gamedemo.engine.gamelogic;

import cs.cvut.fel.pjv.gamedemo.common_classes.*;
import cs.cvut.fel.pjv.gamedemo.common_classes.Object;
import cs.cvut.fel.pjv.gamedemo.engine.utils.Checker;
import cs.cvut.fel.pjv.gamedemo.engine.Dialogue;
import cs.cvut.fel.pjv.gamedemo.engine.Isometric;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GameLogicHandlers {
    private static final Logger logger = LogManager.getLogger(GameLogicHandlers.class);
    private static GameData gameData;
    public static void setGameData(GameData gameData) {
        GameLogicHandlers.gameData = gameData;
    }

    //region Player handler
    /**
     * Set the handle for the player movement and interaction.
     */
    public static void setPlayerHandle() {
        logger.debug("Setting player handle...");
        Scene scene = gameData.getStage().getScene();

        EventHandler<? super KeyEvent> movement_handle = keyEvent -> {
            switch (keyEvent.getCode()) {
                case W:
                    if (!gameData.getPlayer().getTexturePath().contains("back")) gameData.getIsometric().updatePlayerTexture("textures/default/entities/player/player_back.png");
                    gameData.getIsometric().updatePlayerDeltaY(-Constants.PLAYER_BASIC_SPEED_Y);
                    break;
                case S:
                    if (!gameData.getPlayer().getTexturePath().contains("front")) gameData.getIsometric().updatePlayerTexture("textures/default/entities/player/player_front.png");
                    gameData.getIsometric().updatePlayerDeltaY(Constants.PLAYER_BASIC_SPEED_Y);
                    break;
                case A:
                    if (!gameData.getPlayer().getTexturePath().contains("left")) gameData.getIsometric().updatePlayerTexture("textures/default/entities/player/player_left.png");
                    gameData.getIsometric().updatePlayerDeltaX(-Constants.PLAYER_BASIC_SPEED_X);
                    break;
                case D:
                    if (!gameData.getPlayer().getTexturePath().contains("right")) gameData.getIsometric().updatePlayerTexture("textures/default/entities/player/player_right.png");
                    gameData.getIsometric().updatePlayerDeltaX(Constants.PLAYER_BASIC_SPEED_X);
                    break;
            }
            if (gameData.getIsometric().getPlayerDeltaX() != 0 || gameData.getIsometric().getPlayerDeltaY() != 0) GameLogicVisuals.playPlayerFootsteps();
        };

        EventHandler<? super KeyEvent> movement_stopped_handle = keyEvent -> {
            switch (keyEvent.getCode()) {
                case W, S:
                    gameData.getIsometric().updatePlayerDeltaY(0);
                    break;
                case A, D:
                    gameData.getIsometric().updatePlayerDeltaX(0);
                    break;
            }
            if (gameData.getIsometric().getPlayerDeltaX() == 0 && gameData.getIsometric().getPlayerDeltaY() == 0) GameLogicVisuals.stopPlayerFootsteps();
        };

        EventHandler<? super KeyEvent> interact_handle = keyEvent -> {
            if (Objects.requireNonNull(keyEvent.getCode()) == KeyCode.E) {
                if (Checker.checkIfCanInteract(gameData.getPlayer(), gameData.getWagon().getInteractiveObjects()) != null) {
                    Object object = Checker.checkIfCanInteract(gameData.getPlayer(), gameData.getWagon().getInteractiveObjects());
                    if (object instanceof Door door) {
                        logger.debug("Player interacted with door at " + door.getIsoX() + ", " + door.getIsoY());
                        WagonLogic.handleWagonDoor(door);
                    }
                    if (Objects.equals(object.getTwoLetterId(), Constants.CHEST_OBJECT)) {
                        logger.debug("Player interacted with chest at " + object.getIsoX() + ", " + object.getIsoY());
                        GameLogicVisuals.stopPlayerFootsteps();
                        setInventoryHandle(object.getObjectInventory());
                    }
                    if (Objects.equals(object.getTwoLetterId(), Constants.LOCKABLE_DOOR)) {
                        logger.debug("Player interacted with lockable door at " + object.getIsoX() + ", " + object.getIsoY());
                        WagonLogic.useLockableDoor(object);
                    }
                }
                if (Checker.checkIfPlayerCanSpeak(gameData.getPlayer(), gameData.getWagon().getEntities()) != null) {
                    GameLogicVisuals.stopPlayerFootsteps();
                    Entity entity = Checker.checkIfPlayerCanSpeak(gameData.getPlayer(), gameData.getWagon().getEntities());
                    GameLogicWindows.openDialogue(entity);
                }
            }
        };
        //handle TAB, ESCAPE, R
        EventHandler<? super KeyEvent> other_handle = keyEvent -> {
            switch (keyEvent.getCode()) {
                case TAB:
                    GameLogicVisuals.stopPlayerFootsteps();
                    setPlayerInventoryHandle();
                    break;
                case ESCAPE:
                    GameLogicVisuals.stopPlayerFootsteps();
                    GameLogicVisuals.showPauseScreen();
                    break;
                case R:
                    EntitiesLogic.playerUseHand();
                    break;
            }
        };

        scene.setOnKeyPressed(keyEvent -> {
            movement_handle.handle(keyEvent);
            interact_handle.handle(keyEvent);
            other_handle.handle(keyEvent);
            gameData.getIsometric().updateWalls();
        });

        scene.setOnKeyReleased(keyEvent -> {
            movement_stopped_handle.handle(keyEvent);
            gameData.getIsometric().resetAimLine();
        });

        scene.setOnMouseClicked(mouseEvent -> {
            if (gameData.getPlayer().getHandItem() instanceof Firearm firearm) {
                gameData.getPlayer().shoot(firearm, gameData.getWagon().getEntities(), (int) mouseEvent.getX(), (int) mouseEvent.getY(), gameData.getTime(), gameData.getIsometric().getTwoAndTallerWalls());
            } else {
                gameData.getIsometric().resetAimLine();
            }
        });

        scene.setOnMouseMoved(mouseEvent -> {
            if (gameData.getPlayer().getHandItem() instanceof Firearm) {
                gameData.getIsometric().drawPlayerFirearmAim((int) mouseEvent.getX(), (int) mouseEvent.getY());
            } else {
                gameData.getIsometric().resetAimLine();
            }
        });
        logger.debug("Player handle set");
    }
    //endregion

    //region Dialogue handler
    /**
     * Set dialogue handle.
     * @param dialogue dialogue to open
     * @param dialogueEntity entity to speak with
     */
    public static void setDialogueHandle(Dialogue dialogue, Entity dialogueEntity) {
        Scene scene = gameData.getStage().getScene();
        resetSceneHandlers(scene);
        GameManagement.pauseGame();
        logger.debug("Setting dialogue handle...");
        Scene dialogueScene = dialogue.openDialogue();
        gameData.getStage().setScene(dialogueScene);

        EventHandler<? super KeyEvent> prev_handle = dialogueScene.getOnKeyReleased();
        dialogueScene.setOnKeyReleased(keyEvent -> {
            prev_handle.handle(keyEvent);
            if (Objects.equals(dialogue.getAction(), "trade")) {
                logger.info("Dialogue action: trade with " + dialogueEntity.getName());
                dialogue.closeDialogue();
                gameData.getStage().setScene(scene);
                dialogue.setAction(null);
                GameLogicWindows.openTradeWindow((Vendor) dialogueEntity);
                return;
            }
            if (dialogue.getAction() != null) {
                GameLogicWindows.handleDialogueAction(dialogue.getAction(), dialogueEntity);
                dialogue.setAction(null);
                if (Objects.equals(dialogueEntity.getBehaviour(), Constants.Behaviour.AGGRESSIVE)) {
                    logger.info("Dialogue action: " + dialogueEntity.getName() + " is aggressive, ending dialogue");
                    dialogue.closeDialogue();
                    gameData.getStage().setScene(scene);
                    setPlayerHandle();
                    GameManagement.resumeGame();
                    return;
                }
            }
            if (Objects.requireNonNull(keyEvent.getCode()) == KeyCode.TAB) {
                logger.info("Closing dialogue with " + dialogueEntity.getName());
                dialogue.closeDialogue();
                gameData.getStage().setScene(scene);
                setPlayerHandle();
                GameManagement.resumeGame();
            }
        });
        logger.debug("Dialogue handle set");
    }
    //endregion

    //region Inventory handler
    /**
     * Set the handle for the player inventory: clear the scene, clear the player handle, pause the game, set the handle for the player inventory.
     * When the player inventory is closed, the handle for the player is set again and the game is resumed.
     */
    private static void setPlayerInventoryHandle() {
        logger.info("Opening player inventory...");

        Scene scene = gameData.getStage().getScene();
        EntitiesLogic.stopPlayer();// Otherwise the player continues to move after the inventory is closed if movement keys were pressed before opening the inventory
        GameManagement.pauseGame();

        Scene playerInventory = gameData.getPlayer().getPlayerInventory().openInventory();
        gameData.getStage().setScene(playerInventory);

        playerInventory.setOnKeyPressed(keyEvent -> {
            if (Objects.requireNonNull(keyEvent.getCode()) == KeyCode.TAB) {
                logger.info("Player inventory closed");
                gameData.getPlayer().getPlayerInventory().closeInventory(gameData.getStage());
                gameData.getStage().setScene(scene);
                GameManagement.resumeGame();
            }
        });
        logger.info("Player inventory opened");
    }

    /**
     * Set the handle for the inventory: check if the inventory is not null, clear the scene and the player handle,
     * pause the game, set the handle for the inventory.
     * When the inventory is closed, the handle for the player is set again and the game is resumed.
     * @param inventory inventory to be handled
     */
    private static void setInventoryHandle(Inventory inventory) {
        logger.info("Opening inventory...");
        if (inventory != null) {
            Scene scene = gameData.getStage().getScene();
            resetSceneHandlers(scene);
            GameManagement.pauseGame();
            Scene objectInventoryScene = inventory.openInventory();

            gameData.getStage().setScene(objectInventoryScene);

            objectInventoryScene.setOnKeyPressed(keyEvent -> {
                if (Objects.requireNonNull(keyEvent.getCode()) == KeyCode.TAB) {
                    if (!inventory.getTakenItems().isEmpty()) {
                        List<Item> takenItems = inventory.getTakenItems();
                        List<Item> itemsToRemove = new ArrayList<>(takenItems);
                        List<Item> addedItems = new ArrayList<>();
                        for (Item item : itemsToRemove) {
                            if (gameData.getPlayer().getPlayerInventory().addItem(item)) {
                                inventory.removeTakenItem(item);
                                addedItems.add(item);
                                inventory.removeTakenItem(item);
                                logger.info("Player took " + item.getName());
                            } else {
                                logger.info("Player does not have enough space in the inventory");
                                GameLogicWindows.returnItems(addedItems, itemsToRemove, inventory);
                                break;
                            }
                        }
                    }
                    logger.info("Inventory closed");
                    inventory.closeInventory(gameData.getStage());
                    gameData.getStage().setScene(scene);
                    setPlayerHandle();
                    GameManagement.resumeGame();
                }
            });
            logger.info("Inventory opened");
        }
    }
    //endregion

    //region Reset handler
    /**
     * Reset the scene handlers.
     * @param scene scene to be reset
     */
    public static void resetSceneHandlers(Scene scene) {
        scene.setOnKeyReleased(null);
        scene.setOnMouseClicked(null);
        scene.setOnMouseMoved(null);
        scene.setOnKeyPressed(null);
    }
    //endregion
}
