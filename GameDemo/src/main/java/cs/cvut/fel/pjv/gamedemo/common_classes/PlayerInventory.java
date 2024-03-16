package cs.cvut.fel.pjv.gamedemo.common_classes;

import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Rectangle;

public class PlayerInventory extends Inventory {
    private Item mainHandItem;
    private Item firstCraftingItem;
    private Item secondCraftingItem;
    private Item resultItem;

    private int[] mainHandSlotXY = new int[2];
    private int[] firstCraftingSlotXY = new int[2];
    private int[] secondCraftingSlotXY = new int[2];
    private int[] resultSlotXY = new int[2];
    public PlayerInventory() {
        super(Constants.PLAYER_INVENTORY_SIZE);
        itemsArray = new Item[Constants.PLAYER_INVENTORY_SIZE];
    }

    @Override
    public Scene openInventory() {
        updateInventory();
        scene = new Scene(grid, Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
        setSceneHandlers();
        return scene;
    }
    private Button deleteButton(int index) {
        Button deleteButton = new Button("Delete");
        deleteButton.setPrefSize(100, 50);
        deleteButton.setLayoutX(Constants.INVENTORY_LEFT_CORNER_X - (Constants.SLOT_SIZE + Constants.SLOT_GAP) - 1 * (Constants.SLOT_SIZE + Constants.SLOT_GAP));
        deleteButton.setLayoutY(Constants.INVENTORY_LEFT_CORNER_Y + (Constants.SLOT_SIZE / 2) - 25 + 75 + (index / Constants.INVENTORY_MAX_WIDTH) * (Constants.SLOT_SIZE + Constants.SLOT_GAP));
        deleteButton.setStyle("-fx-background-color: #a20808; -fx-text-fill: #ffffff; -fx-font-size: 20;");
        EventHandler deleteButtonHandler = e -> {
            //delete item from inventory
            removeItem(getSelectedItem());
            setSelectedItem(null);
            grid.getChildren().removeIf(node -> node instanceof Button);
            clearSceneHandlers();
            setSceneHandlers();
            updateInventory();
        };
        deleteButton.setOnAction(deleteButtonHandler);
        return deleteButton;
    }
    private Button putBackButton(int x, int y) {
        Button putBackButton = new Button("Put back");
        putBackButton.setPrefSize(125, 50);
        putBackButton.setLayoutX(Constants.INVENTORY_LEFT_CORNER_X + 60 - (Constants.SLOT_SIZE + Constants.SLOT_GAP) - 1 * (Constants.SLOT_SIZE + Constants.SLOT_GAP) - (x * (-1) - 1) * (Constants.SLOT_SIZE + Constants.SLOT_GAP));
        putBackButton.setLayoutY(Constants.INVENTORY_LEFT_CORNER_Y + (Constants.SLOT_SIZE / 2) - 25 + (y / Constants.INVENTORY_MAX_WIDTH) * (Constants.SLOT_SIZE + Constants.SLOT_GAP) + (y - 1) * (Constants.SLOT_SIZE + Constants.SLOT_GAP));
        putBackButton.setStyle("-fx-background-color: #383838; -fx-text-fill: #ffffff; -fx-font-size: 20;");
        EventHandler putBackButtonHandler = e -> {
            //put item back to inventory
            if (mainHandItem == getSelectedItem()) {
                addItem(mainHandItem);
                mainHandItem = null;
                setSelectedItem(null);
                grid.getChildren().removeIf(node -> node instanceof Button);
                clearSceneHandlers();
                setSceneHandlers();
                updateInventory();
            } else if (firstCraftingItem == getSelectedItem()) {
                addItem(firstCraftingItem);
                firstCraftingItem = null;
                resultItem = null;
                setSelectedItem(null);
                grid.getChildren().removeIf(node -> node instanceof Button);
                clearSceneHandlers();
                setSceneHandlers();
                updateInventory();
            } else if (secondCraftingItem == getSelectedItem()) {
                addItem(secondCraftingItem);
                secondCraftingItem = null;
                resultItem = null;
                setSelectedItem(null);
                grid.getChildren().removeIf(node -> node instanceof Button);
                clearSceneHandlers();
                setSceneHandlers();
                updateInventory();
            } else if (resultItem == getSelectedItem()) {
                addItem(resultItem);
                firstCraftingItem = null;
                secondCraftingItem = null;
                resultItem = null;
                setSelectedItem(null);
                grid.getChildren().removeIf(node -> node instanceof Button);
                clearSceneHandlers();
                setSceneHandlers();
                updateInventory();
            }
        };
        putBackButton.setOnAction(putBackButtonHandler);
        return putBackButton;
    }
    private Button putToCraftTableButton(int index) {
        Button putToCraftTableButton = new Button("Craft");
        putToCraftTableButton.setPrefSize(100, 50);
        putToCraftTableButton.setLayoutX(Constants.INVENTORY_LEFT_CORNER_X - (Constants.SLOT_SIZE + Constants.SLOT_GAP) - 1 * (Constants.SLOT_SIZE + Constants.SLOT_GAP));
        putToCraftTableButton.setLayoutY(Constants.INVENTORY_LEFT_CORNER_Y + (Constants.SLOT_SIZE / 2) - 25 - 75 + (index / Constants.INVENTORY_MAX_WIDTH) * (Constants.SLOT_SIZE + Constants.SLOT_GAP));
        putToCraftTableButton.setStyle("-fx-background-color: #563102; -fx-text-fill: #ffffff; -fx-font-size: 20;");
        EventHandler putToCraftTableButtonHandler = e -> {
            if (firstCraftingItem == null) {
                firstCraftingItem = takeItem(getSelectedItemIndex());
                setSelectedItem(null);
                grid.getChildren().removeIf(node -> node instanceof Button);
                clearSceneHandlers();
                setSceneHandlers();
                updateInventory();
            } else if (secondCraftingItem == null) {
                secondCraftingItem = takeItem(getSelectedItemIndex());
                setSelectedItem(null);
                grid.getChildren().removeIf(node -> node instanceof Button);
                clearSceneHandlers();
                setSceneHandlers();
                updateInventory();
            }
            if (firstCraftingItem != null && secondCraftingItem != null) {
                putToCraftTableButton.setDisable(true);
                System.out.println("both items in craft table");
                Item result = new Craft().craft(firstCraftingItem, secondCraftingItem);
                if (result != null) {
                    System.out.println("result is not null");
                    resultItem = result;
                }
                updateInventory();
            }
        };
        putToCraftTableButton.setOnAction(putToCraftTableButtonHandler);
        return putToCraftTableButton;
    }
    private Button mainHandSlotButton(int index) {
        Button mainHandSlotButton = new Button("Equip");
        mainHandSlotButton.setPrefSize(100, 50);
        mainHandSlotButton.setLayoutX(Constants.INVENTORY_LEFT_CORNER_X - (Constants.SLOT_SIZE + Constants.SLOT_GAP) - 1 * (Constants.SLOT_SIZE + Constants.SLOT_GAP));
        mainHandSlotButton.setLayoutY(Constants.INVENTORY_LEFT_CORNER_Y + (Constants.SLOT_SIZE / 2) - 25 + (index / Constants.INVENTORY_MAX_WIDTH) * (Constants.SLOT_SIZE + Constants.SLOT_GAP));
        mainHandSlotButton.setStyle("-fx-background-color: #fada14; -fx-text-fill: #ffffff; -fx-font-size: 20;");
        EventHandler equipButtonHandler = e -> {
            //equip item to player main hand
            //if there is already an item, put it back to inventory
            //if there is no item, put it to main hand
            if (mainHandItem == null) {
                mainHandItem = takeItem(getSelectedItemIndex());
                setSelectedItem(null);
                grid.getChildren().removeIf(node -> node instanceof Button);
                clearSceneHandlers();
                setSceneHandlers();
                updateInventory();
            }
        };
        mainHandSlotButton.setOnAction(equipButtonHandler);
        return mainHandSlotButton;
    }
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
    }
    private void drawMainHandSlot() {
        int mainHandSlotGap = 1;
        //create main hand slot
        Rectangle mainHandSlot = new Rectangle(Constants.SLOT_SIZE, Constants.SLOT_SIZE);
        setRoundBorders(mainHandSlot);
        mainHandSlot.setStyle("-fx-fill: #a20808; -fx-stroke: #ffffff; -fx-stroke-width: 10");
        //set main hand slot near inventory center at the left with gap
        mainHandSlot.setX(Constants.INVENTORY_LEFT_CORNER_X - (Constants.SLOT_SIZE + Constants.SLOT_GAP) - mainHandSlotGap * (Constants.SLOT_SIZE + Constants.SLOT_GAP));
        mainHandSlot.setY(Constants.INVENTORY_LEFT_CORNER_Y + ((double) inventorySize / Constants.INVENTORY_MAX_WIDTH / 2) * (Constants.SLOT_SIZE + Constants.SLOT_GAP));
        mainHandSlotXY[0] = getGridX((int) mainHandSlot.getX());//remember position for later (for handler)
        mainHandSlotXY[1] = getGridY((int) mainHandSlot.getY());
        grid.getChildren().add(mainHandSlot);
        if (mainHandItem != null) {
            drawItemPreview(mainHandSlot, mainHandItem);
        }
    }

    private void drawCraftTable() {
        int craftSlotGap = 1;
        //2 slots for crafting, 1 for result, arrow between

        int x = Constants.INVENTORY_LEFT_CORNER_X - (Constants.SLOT_SIZE + Constants.SLOT_GAP) - craftSlotGap * (Constants.SLOT_SIZE + Constants.SLOT_GAP);
        int y = Constants.INVENTORY_LEFT_CORNER_Y + (inventorySize / Constants.INVENTORY_MAX_WIDTH) * (Constants.SLOT_SIZE + Constants.SLOT_GAP) + craftSlotGap * (Constants.SLOT_SIZE + Constants.SLOT_GAP);

        //create first craft slot
        Rectangle craftSlot1 = new Rectangle(Constants.SLOT_SIZE, Constants.SLOT_SIZE);
        setRoundBorders(craftSlot1);
        craftSlot1.setStyle("-fx-fill: #a20808; -fx-stroke: #ffffff; -fx-stroke-width: 10");
        craftSlot1.setX(x);
        craftSlot1.setY(y);
        //remember position for later
        firstCraftingSlotXY[0] = getGridX((int) craftSlot1.getX());
        firstCraftingSlotXY[1] = getGridY((int) craftSlot1.getY());
        grid.getChildren().add(craftSlot1);
        if (firstCraftingItem != null) {
            drawItemPreview(craftSlot1, firstCraftingItem);
        }
        //create second craft slot
        Rectangle craftSlot2 = new Rectangle(Constants.SLOT_SIZE, Constants.SLOT_SIZE);
        setRoundBorders(craftSlot2);
        craftSlot2.setStyle("-fx-fill: #a20808; -fx-stroke: #ffffff; -fx-stroke-width: 10");
        craftSlot2.setX(x + (Constants.SLOT_SIZE + Constants.SLOT_GAP));
        craftSlot2.setY(y);
        //remember position for later
        secondCraftingSlotXY[0] = getGridX((int) craftSlot2.getX());
        secondCraftingSlotXY[1] = getGridY((int) craftSlot2.getY());
        grid.getChildren().add(craftSlot2);
        if (secondCraftingItem != null) {
            drawItemPreview(craftSlot2, secondCraftingItem);
        }
        //create arrow; only for visual purposes
        ImageView imageView = new ImageView(new Image("arrow.png"));
        imageView.setFitHeight(Constants.SLOT_SIZE);
        imageView.setFitWidth(Constants.SLOT_SIZE);
        imageView.setX(x + 2 * (Constants.SLOT_SIZE + Constants.SLOT_GAP));
        imageView.setY(y);
        //create result slot
        Rectangle resultSlot = new Rectangle(Constants.SLOT_SIZE, Constants.SLOT_SIZE);
        setRoundBorders(resultSlot);
        resultSlot.setStyle("-fx-fill: #476946; -fx-stroke: #ffffff; -fx-stroke-width: 10");
        resultSlot.setX(x + 3 * (Constants.SLOT_SIZE + Constants.SLOT_GAP));
        resultSlot.setY(y);
        grid.getChildren().add(resultSlot);
        if (resultItem != null) {
            drawItemPreview(resultSlot, resultItem);
        }
        //remember position for later
        resultSlotXY[0] = getGridX((int) resultSlot.getX());
        resultSlotXY[1] = getGridY((int) resultSlot.getY());

        grid.getChildren().add(imageView);
    }

    private int getGridX(int x) {
        return (x - Constants.INVENTORY_LEFT_CORNER_X) / (Constants.SLOT_SIZE + Constants.SLOT_GAP);
    }
    private int getGridY(int y) {
        return (y - Constants.INVENTORY_LEFT_CORNER_Y) / (Constants.SLOT_SIZE + Constants.SLOT_GAP);
    }
    private void drawItemPreview(Rectangle slot, Item item) {
        ImageView imageView = new ImageView(new Image(item.getTexturePath()));
        imageView.setFitHeight(Constants.SLOT_SIZE);
        imageView.setFitWidth(Constants.SLOT_SIZE);
        imageView.setX(slot.getX());
        imageView.setY(slot.getY());
        grid.getChildren().add(imageView);
    }
    private void setSceneHandlers() {
        setSceneBasicHandler();
    }
    private void setSceneBasicHandler() {
        //basic means chest inventory
        //when clicked, item is selected and button to take it to player inventory is shown
        //only one item can be selected at a time
        //item cannot be taken if player inventory is full

        scene.setOnMouseMoved(e -> {

            grid.getChildren().remove(itemNameLabel);

            int x = getMouseGridXY(e.getX(), e.getY())[0];
            int y = getMouseGridXY(e.getX(), e.getY())[1];

            int index = y * Constants.INVENTORY_MAX_WIDTH + x;

            if (index >= 0) {
                if (index < inventorySize && itemsArray[index] != null) {
                    itemNameLabel.setText(itemsArray[index].getName());
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

    private int[] getMouseGridXY(double actualX, double actualY) {
        int x = (int) ((actualX - Constants.INVENTORY_LEFT_CORNER_X) / (Constants.SLOT_SIZE + Constants.SLOT_GAP));
        int y = (int) ((actualY - Constants.INVENTORY_LEFT_CORNER_Y) / (Constants.SLOT_SIZE + Constants.SLOT_GAP));

        if ((actualX - Constants.INVENTORY_LEFT_CORNER_X) < 0) {//cannot use without if, because it becomes less accurate
            x = (int) ((actualX - Constants.INVENTORY_LEFT_CORNER_X) / (Constants.SLOT_SIZE + Constants.SLOT_GAP) - 0.7);//0.7 is a magic number (works better than Math.round)
        //x = Math.round((e.getX() - Constants.INVENTORY_LEFT_CORNER_X) / (Constants.SLOT_SIZE + Constants.SLOT_GAP));
        }

        return new int[]{x, y};
    }
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

                //show delete button
                Button deleteButton = deleteButton(index);
                grid.getChildren().add(deleteButton);

                //show put to craft table button
                Button putToCraftTableButton = putToCraftTableButton(index);
                grid.getChildren().add(putToCraftTableButton);

                //show equip button
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
    private void setSceneDeselectHandler() {
        scene.setOnMouseClicked(e1 -> {
            updateInventory();

            //remove button
            grid.getChildren().removeIf(node -> node instanceof Button);

            setSelectedItem(null);

            clearSceneHandlers();
            setSceneBasicHandler();
        });
    }
}