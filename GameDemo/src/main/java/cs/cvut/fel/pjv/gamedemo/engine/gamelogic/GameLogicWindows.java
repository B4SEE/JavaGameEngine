package cs.cvut.fel.pjv.gamedemo.engine.gamelogic;

import cs.cvut.fel.pjv.gamedemo.common_classes.*;
import cs.cvut.fel.pjv.gamedemo.engine.utils.Checker;
import cs.cvut.fel.pjv.gamedemo.engine.Dialogue;
import cs.cvut.fel.pjv.gamedemo.engine.utils.RandomHandler;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GameLogicWindows {
    private static final Logger logger = LogManager.getLogger(GameLogicWindows.class);
    private static GameData gameData;
    public static void setGameData(GameData gameData) {
        GameLogicWindows.gameData = gameData;
    }

    //region Inventory methods
    /**
     * Return the items to the inventory if the player inventory is full or the player does not have enough money.
     * @param addedItems items that were added to the player inventory
     * @param itemsToRemove items that were taken from the object inventory
     * @param inventory object inventory
     */
    public static void returnItems(List<Item> addedItems, List<Item> itemsToRemove, Inventory inventory) {
        for (Item remainingItem : itemsToRemove) {
            if (!addedItems.contains(remainingItem)) {
                inventory.addItem(remainingItem);
                inventory.removeTakenItem(remainingItem);
            }
        }
    }
    //endregion

    //region Dialogue methods
    /**
     * Open the dialogue with the entity.
     * @param entity entity to speak with
     */
    public static void openDialogue(Entity entity) {
        Player player = gameData.getPlayer();
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
            GameLogicHandlers.setDialogueHandle(questDialogue, entity);
            if (!questNPC.isQuestCompleted()) {
                questNPC.setQuestCompleted(questNPC.checkIfPlayerHasQuestItem(player));
                questNPC.setDialoguePath(entity.getName() + "_default.json");
            }
        } else {
            Dialogue dialogue = new Dialogue(entity.getDialoguePath());
            dialogue.setEntity(entity);
            GameLogicHandlers.setDialogueHandle(dialogue, entity);
        }
    }

    /**
     * Handle dialogue actions/reactions.
     * @param action action to be handled
     * @param dialogueEntity entity to speak with
     */
    public static void handleDialogueAction(String action, Entity dialogueEntity) {//answer types: 1 - negative, 2 - fight, 3 - trade, 4 - check ticket
        Player player = gameData.getPlayer();
        // Handle negative response
        if (Objects.equals(action, "negative")) {
            dialogueEntity.setNegativeCount(dialogueEntity.getNegativeCount() + 1);
            if (dialogueEntity.getNegativeCount() >= dialogueEntity.getNegativeThreshold()) {
                EntitiesLogic.handleResponse(dialogueEntity);
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
                gameData.setDeathMessage("You escaped the train");
                GameManagement.stopGame();
            } else {
                dialogueEntity.setNegativeCount(dialogueEntity.getNegativeCount() + 1);
                if (dialogueEntity.getNegativeCount() >= dialogueEntity.getNegativeThreshold()) {
                    dialogueEntity.setBehaviour(Constants.Behaviour.AGGRESSIVE);
                }
            }
        }
    }
    //endregion

    //region TradeWindow methods
    /**
     * Open the trade window with the vendor.
     * @param vendor vendor to trade with
     */
    public static void openTradeWindow(Vendor vendor) {
        Player player = gameData.getPlayer();
        logger.info("Opening trade window with " + vendor.getName() + "...");
        Inventory inventory = vendor.getVendorInventory();
        if (inventory != null) {

            Scene scene = gameData.getStage().getScene();
            GameLogicHandlers.resetSceneHandlers(scene);
            Scene vendorInventoryScene = inventory.openInventory();

            gameData.getStage().setScene(vendorInventoryScene);

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
                    inventory.closeInventory(gameData.getStage());
                    gameData.getStage().setScene(scene);
                    GameLogicHandlers.setPlayerHandle();
                    GameManagement.resumeGame();
                }
            });
            logger.info("Trade window with " + vendor.getName() + " opened");
        }
    }
    //endregion
}
