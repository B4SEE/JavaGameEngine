package cs.cvut.fel.pjv.gamedemo.common_classes;

import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class Inventory {
    public final int inventorySize;
    protected Item[] itemsArray;
    protected final Label itemNameLabel = new Label();
    protected Scene scene;
    protected Pane grid = new Pane();
    private boolean vendor;
    private Item selectedItem;
    private List<Item> takenItems = new ArrayList<>();
    public Inventory(int size) {
        inventorySize = size;
        itemsArray = new Item[size];
    }
    public boolean isVendor() {
        return vendor;
    }
    public void setVendor(boolean vendor) {
        this.vendor = vendor;
    }
    public Scene openInventory() {
        updateInventory();
        scene = new Scene(grid, Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
        setSceneBasicHandler();
        return scene;
    }
    public void updateInventory() {
        grid.getChildren().clear();
        drawInventory();
        Label inventoryLabel = new Label("Chest");
        inventoryLabel.setLayoutX(0);
        inventoryLabel.setLayoutY(0);
        inventoryLabel.setStyle("-fx-font-size: 20; -fx-text-fill: #ffffff;");
        grid.getChildren().add(inventoryLabel);
    }
    public void closeInventory(Stage stage) {
        clearSceneHandlers();
        stage.setScene(null);
        grid.getChildren().clear();
        grid = new Pane();
        scene = null;
    }
    public boolean addItem(Item item) {
        if (item == null) {
            return false;
        }
        for (int i = 0; i < inventorySize; i++) {
            if (itemsArray[i] == null) {
                itemsArray[i] = item;
                return true;
            }
        }
        return false;
    }
    public Item takeItem(int index) {
        if (index < 0 || index >= inventorySize) {
            return null;
        }
        Item item = itemsArray[index];
        itemsArray[index] = null;
        return item;
    }
    public boolean removeItem(Item item) {
        if (item == null) {
            return false;
        }
        for (int i = 0; i < inventorySize; i++) {
            if (itemsArray[i] == item) {
                itemsArray[i] = null;
                return true;
            }
        }
        return false;
    }
    public void setItemsArray(Item[] items) {
        itemsArray = items;
    }
    public Item[] getItemsArray() {
        return itemsArray;
    }
    private void setSceneBasicHandler() {
         scene.setOnMouseMoved(e -> {
            grid.getChildren().remove(itemNameLabel);

            int x = (int) (e.getX() - Constants.INVENTORY_LEFT_CORNER_X) / (Constants.SLOT_SIZE + Constants.SLOT_GAP);
            int y = (int) (e.getY() - Constants.INVENTORY_LEFT_CORNER_Y) / (Constants.SLOT_SIZE + Constants.SLOT_GAP);

            int index = y * Constants.INVENTORY_MAX_WIDTH + x;

            if (index >= 0) {
                if (index < inventorySize && itemsArray[index] != null) {
                    itemNameLabel.setText(itemsArray[index].getName());
                } else {
                    itemNameLabel.setText("Empty");
                }
                itemNameLabel.setLayoutX(Constants.INVENTORY_MAX_WIDTH);
                itemNameLabel.setLayoutY(25);
                itemNameLabel.setStyle("-fx-font-size: 20; -fx-text-fill: #ffffff;");
                grid.getChildren().add(itemNameLabel);
            }
            setSceneSelectHandler();
        });
    }

    protected void clearSceneHandlers() {
        scene.setOnMouseClicked(null);
        scene.setOnMouseMoved(null);
    }

    private void setSceneSelectHandler() {
        scene.setOnMouseClicked(e -> {
            int x = (int) (e.getX() - Constants.INVENTORY_LEFT_CORNER_X) / (64 + 20);
            int y = (int) (e.getY() - Constants.INVENTORY_LEFT_CORNER_Y) / (64 + 20);
            int index = y * Constants.INVENTORY_MAX_WIDTH + x;
            if (index >= 0 && index < inventorySize && itemsArray[index] != null) {
                clearSceneHandlers();
                updateInventory();
                Rectangle slot = (Rectangle) getSlot(x, y);
                slot.setStyle("-fx-stroke: #ff0000; -fx-stroke-width: 10;");
                selectedItem = itemsArray[index];
                if (vendor) {
                    Button buyButton = getBuyButton(index);
                    grid.getChildren().add(buyButton);
                } else {
                    Button takeButton = getTakeButton(index);
                    grid.getChildren().add(takeButton);
                }
                setSceneDeselectHandler();
            }
        });
    }
    private void setSceneDeselectHandler() {
        scene.setOnMouseClicked(e1 -> {
            int x2 = (int) (e1.getX() - Constants.INVENTORY_LEFT_CORNER_X) / (64 + 20);
            int y2 = (int) (e1.getY() - Constants.INVENTORY_LEFT_CORNER_Y) / (64 + 20);
            int index2 = y2 * Constants.INVENTORY_MAX_WIDTH + x2;
            if (index2 >= 0 && index2 < inventorySize && itemsArray[index2] != null) {
                updateInventory();
                grid.getChildren().removeIf(node -> node instanceof Button);
                selectedItem = null;
                clearSceneHandlers();
                setSceneBasicHandler();
            }
        });
    }
    protected Shape getSlot(int x, int y) {
        return grid.getChildren().stream()
                .filter(node -> node instanceof Rectangle)
                .map(node -> (Rectangle) node)
                .filter(node -> node.getX() == x * (64 + 20) + Constants.INVENTORY_LEFT_CORNER_X && node.getY() == y * (64 + 20) + Constants.INVENTORY_LEFT_CORNER_Y)
                .findFirst()
                .orElse(null);
    }
    private Shape getBorder() {
        int borderWidth = Constants.INVENTORY_MAX_WIDTH * (Constants.SLOT_SIZE + Constants.SLOT_GAP) + Constants.SLOT_GAP;
        int borderHeight = inventorySize / Constants.INVENTORY_MAX_WIDTH * (Constants.SLOT_SIZE + Constants.SLOT_GAP) + Constants.SLOT_GAP;

        if (inventorySize % Constants.INVENTORY_MAX_WIDTH != 0) {
            borderHeight += Constants.SLOT_SIZE + Constants.SLOT_GAP;
            if (inventorySize < 10) {
                borderWidth = inventorySize % Constants.INVENTORY_MAX_WIDTH * (Constants.SLOT_SIZE + Constants.SLOT_GAP) + Constants.SLOT_GAP;
            }
        }

        Rectangle border = new Rectangle(borderWidth, borderHeight);
        border.setStyle("-fx-stroke: #ffffff; -fx-stroke-width: 10");
        border.setX(Constants.INVENTORY_LEFT_CORNER_X - Constants.SLOT_GAP);
        border.setY(Constants.INVENTORY_LEFT_CORNER_Y - Constants.SLOT_GAP);
        setRoundBorders(border);

        return border;
    }
    protected void drawInventory() {
        grid.getChildren().add(getBorder());
        grid.setStyle("-fx-background-color: #000000; -fx-border-color: #ffffff;");

        for (int i = 0; i < inventorySize; i++) {
            int i1 = Constants.INVENTORY_LEFT_CORNER_X + i % Constants.INVENTORY_MAX_WIDTH * (Constants.SLOT_SIZE + Constants.SLOT_GAP);
            int i2 = Constants.INVENTORY_LEFT_CORNER_Y + i / Constants.INVENTORY_MAX_WIDTH * (Constants.SLOT_SIZE + Constants.SLOT_GAP);

            Rectangle rectangle = new Rectangle(Constants.SLOT_SIZE, Constants.SLOT_SIZE);
            setRoundBorders(rectangle);
            rectangle.setStyle("-fx-stroke: #ffffff; -fx-stroke-width: 10;");
            rectangle.setX(i1);
            rectangle.setY(i2);
            grid.getChildren().add(rectangle);

            if (itemsArray[i] != null) {
                ImageView imageView = new ImageView(itemsArray[i].getTexturePath());
                imageView.setFitHeight(Constants.SLOT_SIZE);
                imageView.setFitWidth(Constants.SLOT_SIZE);
                imageView.setX(i1);
                imageView.setY(i2);
                imageView.setStyle("-fx-stroke: #ffffff; -fx-stroke-width: 10");
                grid.getChildren().add(imageView);
            }
        }
    }
    private Button getTakeButton(int index) {
        Button takeButton = new Button("Take");
        int takeButtonGap = 1;
        takeButton.setPrefSize(100, 50);
        takeButton.setLayoutX(Constants.INVENTORY_LEFT_CORNER_X - (Constants.SLOT_SIZE + Constants.SLOT_GAP) - takeButtonGap * (Constants.SLOT_SIZE + Constants.SLOT_GAP));
        takeButton.setLayoutY(Constants.INVENTORY_LEFT_CORNER_Y + ((double) Constants.SLOT_SIZE / 2) - 25 + (index / Constants.INVENTORY_MAX_WIDTH) * (Constants.SLOT_SIZE + Constants.SLOT_GAP));
        takeButton.setStyle("-fx-background-color: #484848; -fx-text-fill: #ffffff; -fx-font-size: 20;");
        EventHandler takeButtonHandler = e -> {
            addTakenItem(takeItem(getSelectedItemIndex()));
            clearButton();
        };
        takeButton.setOnAction(takeButtonHandler);
        return takeButton;
    }
    private Button getBuyButton(int index) {
        Button buyButton = getTakeButton(index);
        buyButton.setText("Buy");
        buyButton.setStyle("-fx-background-color: #c0561a; -fx-text-fill: #ffffff; -fx-font-size: 20;");
        return buyButton;
    }
    private void clearButton() {
        grid.getChildren().removeIf(node -> node instanceof Button);
        clearSceneHandlers();
        setSceneBasicHandler();
        updateInventory();
    }
    protected int getSelectedItemIndex() {
        for (int i = 0; i < inventorySize; i++) {
            if (itemsArray[i] != null) {
                if (itemsArray[i] == selectedItem) {
                    return i;
                }
            }
        }
        return -1;
    }
    protected Item getSelectedItem() {
        return selectedItem;
    }
    protected void setSelectedItem(Item selectedItem) {
        this.selectedItem = selectedItem;
    }
    public void addTakenItem(Item takenItem) {
        this.takenItems.add(takenItem);
    }
    public List<Item> getTakenItems() {
        return takenItems;
    }
    public void removeTakenItem(Item takenItem) {
        this.takenItems.remove(takenItem);
    }
    protected void setRoundBorders(Rectangle rectangle) {
        //set round borders
        rectangle.setArcHeight(10);
        rectangle.setArcWidth(10);
    }
}