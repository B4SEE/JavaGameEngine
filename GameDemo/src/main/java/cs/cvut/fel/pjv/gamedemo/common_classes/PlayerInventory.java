package cs.cvut.fel.pjv.gamedemo.common_classes;

import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Rectangle;

/**
 * Represents player's inventory, extends Inventory class.
 * It has additional slots for main hand, crafting table and result slot and shows player's money and ammo.
 */
public class PlayerInventory extends Inventory {
    private Item mainHandItem;
    private Item firstCraftingItem;
    private Item secondCraftingItem;
    private Item resultItem;
    private int money = 0;
    private int ammo = 0;

    private final int[] mainHandSlotXY = new int[2];
    private final int[] firstCraftingSlotXY = new int[2];
    private final int[] secondCraftingSlotXY = new int[2];
    private final int[] resultSlotXY = new int[2];
    public PlayerInventory() {
        super(Constants.PLAYER_INVENTORY_SIZE);
        itemsArray = new Item[Constants.PLAYER_INVENTORY_SIZE];
    }

    /**
     * Open inventory and set basic handler
     * @return scene
     */
    @Override
    public Scene openInventory() {
        updateInventory();
        scene = new Scene(grid, Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
        setSceneBasicHandler();
        return scene;
    }

    /**
     * Clear all buttons and reset handlers
     */
    private void clearButton() {
        setSelectedItem(null);
        grid.getChildren().removeIf(node -> node instanceof Button);
        clearSceneHandlers();
        setSceneBasicHandler();
        updateInventory();
    }

    /**
     * Create delete button for selected item
     * @param index - index of selected item
     * @return delete button
     */
    private Button deleteButton(int index) {
        Button deleteButton = new Button("Delete");
        deleteButton.setPrefSize(100, 50);
        deleteButton.setLayoutX(Constants.INVENTORY_LEFT_CORNER_X - (Constants.SLOT_SIZE + Constants.SLOT_GAP) - (Constants.SLOT_SIZE + Constants.SLOT_GAP));
        deleteButton.setLayoutY(Constants.INVENTORY_LEFT_CORNER_Y + ((double) Constants.SLOT_SIZE / 2) - 25 + 75 + ((double) index / Constants.INVENTORY_MAX_WIDTH) * (Constants.SLOT_SIZE + Constants.SLOT_GAP));
        deleteButton.setStyle("-fx-background-color: #a20808; -fx-text-fill: #ffffff; -fx-font-size: 20;");
        EventHandler deleteButtonHandler = e -> {
            removeItem(getSelectedItem());
            clearButton();
        };
        deleteButton.setOnAction(deleteButtonHandler);
        return deleteButton;
    }

    /**
     * Create put back button for selected item in main hand, crafting table or result slot
     * @param x - x coordinate of selected item
     * @param y - y coordinate of selected item
     * @return put back button
     */
    private Button putBackButton(int x, int y) {
        Button putBackButton = new Button("Put back");
        putBackButton.setPrefSize(125, 50);
        putBackButton.setLayoutX(Constants.INVENTORY_LEFT_CORNER_X + 60 - (Constants.SLOT_SIZE + Constants.SLOT_GAP) - (Constants.SLOT_SIZE + Constants.SLOT_GAP) - (x * (-1) - 1) * (Constants.SLOT_SIZE + Constants.SLOT_GAP));
        putBackButton.setLayoutY(Constants.INVENTORY_LEFT_CORNER_Y + ((double) Constants.SLOT_SIZE / 2) - 25 + ((double) y / Constants.INVENTORY_MAX_WIDTH) * (Constants.SLOT_SIZE + Constants.SLOT_GAP) + (y - 1) * (Constants.SLOT_SIZE + Constants.SLOT_GAP));
        putBackButton.setStyle("-fx-background-color: #383838; -fx-text-fill: #ffffff; -fx-font-size: 20;");
        EventHandler putBackButtonHandler = e -> {
            if (mainHandItem == getSelectedItem()) {
                if (addItem(mainHandItem)) {
                    mainHandItem = null;
                    clearButton();
                }
            } else if (firstCraftingItem == getSelectedItem()) {
                if (addItem(firstCraftingItem)) {
                    firstCraftingItem = null;
                    resultItem = null;
                    clearButton();
                }
            } else if (secondCraftingItem == getSelectedItem()) {
                if (addItem(secondCraftingItem)) {
                    secondCraftingItem = null;
                    resultItem = null;
                    clearButton();
                }
            } else if (resultItem == getSelectedItem()) {
                if (addItem(resultItem)) {
                    firstCraftingItem = null;
                    secondCraftingItem = null;
                    resultItem = null;
                    clearButton();
                }
            }
        };
        putBackButton.setOnAction(putBackButtonHandler);
        return putBackButton;
    }

    /**
     * Create put to craft table button for selected item
     * @param index - index of selected item
     * @return put to craft table button
     */
    private Button putToCraftTableButton(int index) {
        Button putToCraftTableButton = new Button("Craft");
        putToCraftTableButton.setPrefSize(100, 50);
        putToCraftTableButton.setLayoutX(Constants.INVENTORY_LEFT_CORNER_X - (Constants.SLOT_SIZE + Constants.SLOT_GAP) - (Constants.SLOT_SIZE + Constants.SLOT_GAP));
        putToCraftTableButton.setLayoutY(Constants.INVENTORY_LEFT_CORNER_Y + ((double) Constants.SLOT_SIZE / 2) - 25 - 75 + ((double) index / Constants.INVENTORY_MAX_WIDTH) * (Constants.SLOT_SIZE + Constants.SLOT_GAP));
        putToCraftTableButton.setStyle("-fx-background-color: #563102; -fx-text-fill: #ffffff; -fx-font-size: 20;");
        EventHandler putToCraftTableButtonHandler = e -> {
            if (firstCraftingItem == null) {
                firstCraftingItem = takeItem(getSelectedItemIndex());
                clearButton();
            } else if (secondCraftingItem == null) {
                secondCraftingItem = takeItem(getSelectedItemIndex());
                clearButton();
            }
            if (firstCraftingItem != null && secondCraftingItem != null) {
                putToCraftTableButton.setDisable(true);
                resultItem = new Craft().craft(firstCraftingItem, secondCraftingItem);
                updateInventory();
            }
        };
        putToCraftTableButton.setOnAction(putToCraftTableButtonHandler);
        return putToCraftTableButton;
    }

    /**
     * Create equip button for selected item
     * @param index - index of selected item
     * @return equip button
     */
    private Button mainHandSlotButton(int index) {
        Button mainHandSlotButton = new Button("Equip");
        mainHandSlotButton.setPrefSize(100, 50);
        mainHandSlotButton.setLayoutX(Constants.INVENTORY_LEFT_CORNER_X - (Constants.SLOT_SIZE + Constants.SLOT_GAP) - (Constants.SLOT_SIZE + Constants.SLOT_GAP));
        mainHandSlotButton.setLayoutY(Constants.INVENTORY_LEFT_CORNER_Y + ((double) Constants.SLOT_SIZE / 2) - 25 + ((double) index / Constants.INVENTORY_MAX_WIDTH) * (Constants.SLOT_SIZE + Constants.SLOT_GAP));
        mainHandSlotButton.setStyle("-fx-background-color: #fada14; -fx-text-fill: #ffffff; -fx-font-size: 20;");
        EventHandler equipButtonHandler = e -> {
            if (mainHandItem == null) {
                mainHandItem = takeItem(getSelectedItemIndex());
                clearButton();
            }
        };
        mainHandSlotButton.setOnAction(equipButtonHandler);
        return mainHandSlotButton;
    }

    /**
     * Update inventory
     */
    @Override
    public void updateInventory() {
        grid.getChildren().clear();
        Label inventoryLabel = new Label("Player inventory");
        inventoryLabel.setLayoutX(0);
        inventoryLabel.setLayoutY(0);
        inventoryLabel.setStyle("-fx-font-size: 20; -fx-text-fill: #ffffff;");
        grid.getChildren().add(inventoryLabel);
        drawInventory();
        drawMainHandSlot();
        drawCraftTable();
        showMoney();
        showAmmo();
    }

    /**
     * Draw main hand slot
     */
    private void drawMainHandSlot() {
        int mainHandSlotGap = 1;
        Rectangle mainHandSlot = new Rectangle(Constants.SLOT_SIZE, Constants.SLOT_SIZE);
        setRoundBorders(mainHandSlot);
        mainHandSlot.setStyle("-fx-fill: #a20808; -fx-stroke: #ffffff; -fx-stroke-width: 10");
        mainHandSlot.setX(Constants.INVENTORY_LEFT_CORNER_X - (Constants.SLOT_SIZE + Constants.SLOT_GAP) - mainHandSlotGap * (Constants.SLOT_SIZE + Constants.SLOT_GAP));
        mainHandSlot.setY(Constants.INVENTORY_LEFT_CORNER_Y + ((double) inventorySize / Constants.INVENTORY_MAX_WIDTH / 2) * (Constants.SLOT_SIZE + Constants.SLOT_GAP));
        mainHandSlotXY[0] = getGridX((int) mainHandSlot.getX());//remember position for later (for handler)
        mainHandSlotXY[1] = getGridY((int) mainHandSlot.getY());
        grid.getChildren().add(mainHandSlot);
        if (mainHandItem != null) {
            drawItemPreview(mainHandSlot, mainHandItem);
        }
    }

    /**
     * Draw crafting table
     */
    private void drawCraftTable() {
        int craftSlotGap = 1;

        int x = Constants.INVENTORY_LEFT_CORNER_X - (Constants.SLOT_SIZE + Constants.SLOT_GAP) - craftSlotGap * (Constants.SLOT_SIZE + Constants.SLOT_GAP);
        int y = Constants.INVENTORY_LEFT_CORNER_Y + (inventorySize / Constants.INVENTORY_MAX_WIDTH) * (Constants.SLOT_SIZE + Constants.SLOT_GAP) + craftSlotGap * (Constants.SLOT_SIZE + Constants.SLOT_GAP);

        Rectangle craftSlot1 = new Rectangle(Constants.SLOT_SIZE, Constants.SLOT_SIZE);
        setRoundBorders(craftSlot1);
        craftSlot1.setStyle("-fx-fill: #a20808; -fx-stroke: #ffffff; -fx-stroke-width: 10");
        craftSlot1.setX(x);
        craftSlot1.setY(y);

        firstCraftingSlotXY[0] = getGridX((int) craftSlot1.getX());
        firstCraftingSlotXY[1] = getGridY((int) craftSlot1.getY());
        grid.getChildren().add(craftSlot1);

        if (firstCraftingItem != null) {
            drawItemPreview(craftSlot1, firstCraftingItem);
        }

        Rectangle craftSlot2 = new Rectangle(Constants.SLOT_SIZE, Constants.SLOT_SIZE);
        setRoundBorders(craftSlot2);
        craftSlot2.setStyle("-fx-fill: #a20808; -fx-stroke: #ffffff; -fx-stroke-width: 10");
        craftSlot2.setX(x + (Constants.SLOT_SIZE + Constants.SLOT_GAP));
        craftSlot2.setY(y);

        secondCraftingSlotXY[0] = getGridX((int) craftSlot2.getX());
        secondCraftingSlotXY[1] = getGridY((int) craftSlot2.getY());
        grid.getChildren().add(craftSlot2);

        if (secondCraftingItem != null) {
            drawItemPreview(craftSlot2, secondCraftingItem);
        }

        ImageView imageView = new ImageView(new Image("arrow.png"));
        imageView.setFitHeight(Constants.SLOT_SIZE);
        imageView.setFitWidth(Constants.SLOT_SIZE);
        imageView.setX(x + 2 * (Constants.SLOT_SIZE + Constants.SLOT_GAP));
        imageView.setY(y);

        grid.getChildren().add(imageView);

        Rectangle resultSlot = new Rectangle(Constants.SLOT_SIZE, Constants.SLOT_SIZE);
        setRoundBorders(resultSlot);
        resultSlot.setStyle("-fx-fill: #476946; -fx-stroke: #ffffff; -fx-stroke-width: 10");
        resultSlot.setX(x + 3 * (Constants.SLOT_SIZE + Constants.SLOT_GAP));
        resultSlot.setY(y);

        resultSlotXY[0] = getGridX((int) resultSlot.getX());
        resultSlotXY[1] = getGridY((int) resultSlot.getY());
        grid.getChildren().add(resultSlot);

        if (resultItem != null) {
            drawItemPreview(resultSlot, resultItem);
        }
    }

    /**
     * Get grid x coordinate of the slot
     * @param x - actual x coordinate of the slot
     * @return grid x coordinate
     */
    private int getGridX(int x) {
        return (x - Constants.INVENTORY_LEFT_CORNER_X) / (Constants.SLOT_SIZE + Constants.SLOT_GAP);
    }

    /**
     * Get grid y coordinate of the slot
     * @param y - actual y coordinate of the slot
     * @return grid y coordinate
     */
    private int getGridY(int y) {
        return (y - Constants.INVENTORY_LEFT_CORNER_Y) / (Constants.SLOT_SIZE + Constants.SLOT_GAP);
    }

    /**
     * Draw item preview
     * @param slot - slot where to draw
     * @param item - item to draw
     */
    private void drawItemPreview(Rectangle slot, Item item) {
        ImageView imageView = new ImageView(new Image(item.getTexturePath()));
        imageView.setFitHeight(Constants.SLOT_SIZE);
        imageView.setFitWidth(Constants.SLOT_SIZE);
        imageView.setX(slot.getX());
        imageView.setY(slot.getY());
        grid.getChildren().add(imageView);
    }

    /**
     * Set basic handler for inventory: show item name on hover, select item on click.
     */
    private void setSceneBasicHandler() {
        scene.setOnMouseMoved(e -> {

            grid.getChildren().remove(itemNameLabel);

            int x = getMouseGridXY(e.getX(), e.getY())[0];
            int y = getMouseGridXY(e.getX(), e.getY())[1];

            int index = y * Constants.INVENTORY_MAX_WIDTH + x;

            if (index >= 0) {
                if (index < inventorySize && itemsArray[index] != null) {
                    itemNameLabel.setText(itemsArray[index].getName() + " | " + itemsArray[index].getValue());
                    //if clicked on item, select it (border)
                } else {
                    itemNameLabel.setText(x + " " + y + " | " + e.getX() + " " + e.getY());
                }
                itemNameLabel.setLayoutX(Constants.INVENTORY_MAX_WIDTH);
                itemNameLabel.setLayoutY(25);
                itemNameLabel.setStyle("-fx-font-size: 20; -fx-text-fill: #ffffff;");
                grid.getChildren().add(itemNameLabel);
            }
            this.setSceneSelectHandler();
        });
    }

    /**
     * Get grid x and y coordinates of mouse
     * @param actualX - x coordinate of mouse
     * @param actualY - y coordinate of mouse
     * @return grid x and y coordinates
     */
    private int[] getMouseGridXY(double actualX, double actualY) {
        int x = (int) ((actualX - Constants.INVENTORY_LEFT_CORNER_X) / (Constants.SLOT_SIZE + Constants.SLOT_GAP));
        int y = (int) ((actualY - Constants.INVENTORY_LEFT_CORNER_Y) / (Constants.SLOT_SIZE + Constants.SLOT_GAP));

        if ((actualX - Constants.INVENTORY_LEFT_CORNER_X) < 0) {//cannot use without if, because it becomes less accurate
            x = (int) ((actualX - Constants.INVENTORY_LEFT_CORNER_X) / (Constants.SLOT_SIZE + Constants.SLOT_GAP) - 0.7);//0.7 works better than Math.round
        //x = Math.round((e.getX() - Constants.INVENTORY_LEFT_CORNER_X) / (Constants.SLOT_SIZE + Constants.SLOT_GAP));
        }

        return new int[]{x, y};
    }

    /**
     * Set select handler for inventory: select item on click, deselect on second click.
     */
    private void setSceneSelectHandler() {
        scene.setOnMouseClicked(e -> {

            int index = -1;

            int x = getMouseGridXY(e.getX(), e.getY())[0];
            int y = getMouseGridXY(e.getX(), e.getY())[1];

            if (x >= 0 && y >= 0) {
                index = y * Constants.INVENTORY_MAX_WIDTH + x;
            }

            if (index >= 0 && index < inventorySize && itemsArray[index] != null) {
                clearSceneHandlers();
                updateInventory();
                Rectangle slot = (Rectangle) getSlot(x, y);
                slot.setStyle("-fx-stroke: #ff0000; -fx-stroke-width: 10;");
                setSelectedItem(itemsArray[index]);

                Button deleteButton = deleteButton(index);
                grid.getChildren().add(deleteButton);

                Button putToCraftTableButton = putToCraftTableButton(index);
                grid.getChildren().add(putToCraftTableButton);

                Button mainHandSlotButton = mainHandSlotButton(index);
                grid.getChildren().add(mainHandSlotButton);

                setSceneDeselectHandler();
            } else if (x == mainHandSlotXY[0] && y == mainHandSlotXY[1]) {
                if (mainHandItem != null) {
                    setNonInventorySelectionHandler(x, y, mainHandItem, "Unequip");
                }
            } else if (x == resultSlotXY[0] && y == resultSlotXY[1]) {
                    if (resultItem != null) {
                        setNonInventorySelectionHandler(x, y, resultItem, "Take");
                    }
            } else if (x == firstCraftingSlotXY[0] && y == firstCraftingSlotXY[1]) {
                if (firstCraftingItem != null) {
                    setNonInventorySelectionHandler(x, y, firstCraftingItem, "Put back");
                }
            } else if (x == secondCraftingSlotXY[0] && y == secondCraftingSlotXY[1]) {
                if (secondCraftingItem != null) {
                    setNonInventorySelectionHandler(x, y, secondCraftingItem, "Put back");
                }
            }
        });
    }

    /**
     * Set selection handler for main hand slot, crafting table and result slot
     * @param x - x coordinate of selected item
     * @param y - y coordinate of selected item
     * @param item - selected item
     * @param buttonText - text of put back button
     * <br>
     * Note: this method is used for main hand slot, crafting table and result slot because they are not part of the inventory array;
     * it means that they cannot be selected by the basic handler (that select items from the inventory array by their index);
     * this also means that they are separate inventory slots and can hold items, making actual inventory size bigger (size + main hand slot + crafting table 2 slots)
     */
    private void setNonInventorySelectionHandler(int x, int y, Item item, String buttonText) {
        clearSceneHandlers();
        updateInventory();
        Rectangle slot = (Rectangle) getSlot(x, y);
        slot.setStyle("-fx-stroke: #ff0000; -fx-stroke-width: 10;");
        setSelectedItem(item);
        Button putBackButton = putBackButton(x, y);
        putBackButton.setText(buttonText);
        grid.getChildren().add(putBackButton);
        setSceneDeselectHandler();
    }

    /**
     * Set deselect handler for inventory: deselect item on click, clear all buttons and reset handlers.
     */
    private void setSceneDeselectHandler() {
        scene.setOnMouseClicked(e1 -> {
            updateInventory();
            grid.getChildren().removeIf(node -> node instanceof Button);
            setSelectedItem(null);
            clearSceneHandlers();
            setSceneBasicHandler();
        });
    }

    /**
     * Show player's money
     */
    private void showMoney() {
        Label moneyLabel = new Label("Money: " + money);
        moneyLabel.setLayoutX(0);
        moneyLabel.setLayoutY(50);
        moneyLabel.setStyle("-fx-font-size: 20; -fx-text-fill: #ffffff;");
        grid.getChildren().add(moneyLabel);
    }

    /**
     * Show player's ammo
     */
    private void showAmmo() {
        Label ammoLabel = new Label("Ammo: " + ammo);
        ammoLabel.setLayoutX(0);
        ammoLabel.setLayoutY(75);
        ammoLabel.setStyle("-fx-font-size: 20; -fx-text-fill: #ffffff;");
        grid.getChildren().add(ammoLabel);
    }
    public void setMoney(int money) {
        this.money = money;
    }
    public int getMoney() {
        return money;
    }
    public void setAmmo(int ammo) {
        this.ammo = ammo;
    }
    public int getAmmo() {
        return ammo;
    }
    public void setMainHandItem(Item item) {
        mainHandItem = item;
    }

    public Item getMainHandItem() {
        return mainHandItem;
    }
}