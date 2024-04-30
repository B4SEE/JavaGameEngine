package cs.cvut.fel.pjv.gamedemo.common_classes;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import cs.cvut.fel.pjv.gamedemo.engine.Isometric;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.stage.Stage;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Class for inventory, responsible for adding, removing, taking items, and showing inventory.
 * Mainly used for chest and vendor inventories. For player inventory, see PlayerInventory class.
 * @see PlayerInventory
 */
public class Inventory {
    @JsonIgnore
    private static final Logger logger = LogManager.getLogger(Inventory.class);
    @JsonProperty("inventorySize")
    public final int inventorySize;
    @JsonProperty("itemsArray")
    protected Item[] itemsArray;
    @JsonIgnore
    protected final Label itemNameLabel = new Label();
    @JsonIgnore
    protected Scene scene;
    @JsonIgnore
    protected Pane grid = new Pane();
    @JsonIgnore
    private Label inventoryLabel = new Label();
    @JsonProperty
    private boolean vendor;
    @JsonIgnore
    private Item selectedItem;
    @JsonIgnore
    private List<Item> takenItems = new ArrayList<>();
    @JsonCreator
    public Inventory(@JsonProperty("size") int size) {
        inventorySize = size;
        itemsArray = new Item[size];
    }
    @JsonIgnore
    public boolean isVendor() {
        return vendor;
    }
    @JsonSetter("vendor")
    public void setVendor(boolean vendor) {
        this.vendor = vendor;
    }

    /**
     * Open inventory and set basic handler
     * @return scene
     */
    @JsonIgnore
    public Scene openInventory() {
        updateInventory();
        scene = new Scene(grid, Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
        setSceneBasicHandler();
        return scene;
    }
    /**
     * Update inventory
     */
    @JsonIgnore
    public void updateInventory() {
        grid.getChildren().clear();
        drawInventory();
        inventoryLabel.setLayoutX(0);
        inventoryLabel.setLayoutY(0);
        inventoryLabel.setStyle("-fx-font-size: 20; -fx-text-fill: #ffffff;");
        grid.getChildren().add(inventoryLabel);
    }

    /**
     * Close inventory
     * @param stage stage
     */
    @JsonIgnore
    public void closeInventory(Stage stage) {
        clearSceneHandlers();
        stage.setScene(null);
        grid.getChildren().clear();
        grid = new Pane();
        scene = null;
    }

    /**
     * Add item to inventory
     * @param item item to add
     * @return true if added, false if not
     */
    @JsonIgnore
    public boolean addItem(Item item) {
        if (item == null) return false;
        for (int i = 0; i < inventorySize; i++) {
            if (itemsArray[i] == null) {
                itemsArray[i] = item;
                return true;
            }
        }
        return false;
    }

    /**
     * Take item from inventory by index
     * @param index index of item
     * @return item
     */
    @JsonIgnore
    public Item takeItem(int index) {
        if (index < 0 || index >= inventorySize) return null;
        Item item = itemsArray[index];
        itemsArray[index] = null;
        return item;
    }

    /**
     * Remove item from inventory
     * @param item item to remove
     */
    @JsonIgnore
    public void removeItem(Item item) {
        if (item == null) return;
        for (int i = 0; i < inventorySize; i++) {
            if (itemsArray[i] == item) {
                itemsArray[i] = null;
                return;
            }
        }
    }

    /**
     * Get item with the same name from inventory
     * @param item item to compare
     * @return item with the same name
     */
    @JsonIgnore
    public Item getWithSameName(Item item) {
        for (int i = 0; i < inventorySize; i++) {
            if (itemsArray[i] != null) {
                if (itemsArray[i].getName().equals(item.getName())) {
                    return itemsArray[i];
                }
            }
        }
        return null;
    }
    @JsonSetter("itemsArray")
    public void setItemsArray(Item[] items) {
        itemsArray = items;
    }
    @JsonIgnore
    public Item[] getItemsArray() {
        return itemsArray;
    }
    /**
     * Set basic handler for inventory: show item name on hover, select item on click.
     */
    @JsonIgnore
    private void setSceneBasicHandler() {
         scene.setOnMouseMoved(e -> {
            grid.getChildren().remove(itemNameLabel);

            int x = (int) (e.getX() - Constants.INVENTORY_LEFT_CORNER_X) / (Constants.SLOT_SIZE + Constants.SLOT_GAP);
            int y = (int) (e.getY() - Constants.INVENTORY_LEFT_CORNER_Y) / (Constants.SLOT_SIZE + Constants.SLOT_GAP);

            int index = y * Constants.INVENTORY_MAX_WIDTH + x;

            if (index >= 0) {
                if (index < inventorySize && itemsArray[index] != null) {
                    String name = itemsArray[index].getName();
                    String info = getInfoString(itemsArray[index]);
                    itemNameLabel.setText(" | " + name + info);
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
    protected String getInfoString(Item item) {
        String separator = " | ";
        String value = ((item.getValue() != 0 && vendor) ? item.getValue() + " coins" : (vendor) ? "Free" : "");
        String melee_damage = item instanceof MeleeWeapon ? "Damage: " + ((MeleeWeapon) item).getDamage() : "";
        String attack_speed = item instanceof MeleeWeapon ? "Attack speed: " + ((MeleeWeapon) item).getAttackSpeed() : "";
        String firearm_damage = item instanceof Firearm ? "Damage: " + ((Firearm) item).getDamage() : "";
        String shooting_speed = item instanceof Firearm ? "Shooting speed: " + ((Firearm) item).getShootingSpeed() : "";
        String nourishment = item instanceof Food ? "Nourishment: " + ((Food) item).getNourishment() : "";
        // return all non-empty strings separated by separator
        return separator + Stream.of(value, melee_damage, attack_speed, firearm_damage, shooting_speed, nourishment)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.joining(separator));
    }

    /**
     * Clear scene handlers
     */
    @JsonIgnore
    protected void clearSceneHandlers() {
        scene.setOnMouseClicked(null);
        scene.setOnMouseMoved(null);
    }

    /**
     * Set select handler for inventory: select item on click, deselect on second click.
     */
    @JsonIgnore
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
                logger.debug("Selected item: " + selectedItem.getName());
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
    /**
     * Set deselect handler for inventory: deselect item on click, clear all buttons and reset handlers.
     */
    @JsonIgnore
    private void setSceneDeselectHandler() {
        scene.setOnMouseClicked(e1 -> {
            int x2 = (int) (e1.getX() - Constants.INVENTORY_LEFT_CORNER_X) / (64 + 20);
            int y2 = (int) (e1.getY() - Constants.INVENTORY_LEFT_CORNER_Y) / (64 + 20);
            int index2 = y2 * Constants.INVENTORY_MAX_WIDTH + x2;
            if (index2 >= 0 && index2 < inventorySize && itemsArray[index2] != null) {
                updateInventory();
                grid.getChildren().removeIf(node -> node instanceof Button);
                logger.debug("Deselected item: " + selectedItem.getName());
                selectedItem = null;
                clearSceneHandlers();
                setSceneBasicHandler();
            }
        });
    }

    /**
     * Get slot by coordinates
     * @param x actual x
     * @param y actual y
     * @return slot
     */
    @JsonIgnore
    protected Shape getSlot(int x, int y) {
        return grid.getChildren().stream()
                .filter(node -> node instanceof Rectangle)
                .map(node -> (Rectangle) node)
                .filter(node -> node.getX() == x * (Constants.SLOT_SIZE + Constants.SLOT_GAP) + Constants.INVENTORY_LEFT_CORNER_X && node.getY() == y * (Constants.SLOT_SIZE + Constants.SLOT_GAP) + Constants.INVENTORY_LEFT_CORNER_Y)
                .findFirst()
                .orElse(null);
    }

    /**
     * Create border for inventory
     * @return border
     */
    @JsonIgnore
    private Shape getBorder() {
        int borderWidth = Constants.INVENTORY_MAX_WIDTH * (Constants.SLOT_SIZE + Constants.SLOT_GAP) + Constants.SLOT_GAP;
        int borderHeight = inventorySize / Constants.INVENTORY_MAX_WIDTH * (Constants.SLOT_SIZE + Constants.SLOT_GAP) + Constants.SLOT_GAP;

        if (inventorySize % Constants.INVENTORY_MAX_WIDTH != 0) {
            borderHeight += Constants.SLOT_SIZE + Constants.SLOT_GAP;
            if (inventorySize < Constants.INVENTORY_MAX_WIDTH) {
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

    /**
     * Draw inventory
     */
    @JsonIgnore
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

    /**
     * Create take button for selected item
     * @param index index of selected item
     * @return take button
     */
    @JsonIgnore
    private Button getTakeButton(int index) {
        Button takeButton = new Button("Take");
        int takeButtonGap = 1;
        takeButton.setPrefSize(100, 50);
        takeButton.setLayoutX(Constants.INVENTORY_LEFT_CORNER_X - (Constants.SLOT_SIZE + Constants.SLOT_GAP) - takeButtonGap * (Constants.SLOT_SIZE + Constants.SLOT_GAP));
        takeButton.setLayoutY(Constants.INVENTORY_LEFT_CORNER_Y + ((double) Constants.SLOT_SIZE / 2) - 25 + ((double) index / Constants.INVENTORY_MAX_WIDTH) * (Constants.SLOT_SIZE + Constants.SLOT_GAP));
        takeButton.setStyle("-fx-background-color: #484848; -fx-text-fill: #ffffff; -fx-font-size: 20;");
        EventHandler takeButtonHandler = e -> {
            addTakenItem(takeItem(getSelectedItemIndex()));
            clearButton();
        };
        takeButton.setOnAction(takeButtonHandler);
        return takeButton;
    }

    /**
     * Create buy button for selected item
     * @param index index of selected item
     * @return buy button
     */
    @JsonIgnore
    private Button getBuyButton(int index) {
        Button buyButton = getTakeButton(index);
        buyButton.setText("Buy");
        buyButton.setStyle("-fx-background-color: #c0561a; -fx-text-fill: #ffffff; -fx-font-size: 20;");
        return buyButton;
    }

    /**
     * Clear all buttons and reset handlers
     */
    @JsonIgnore
    private void clearButton() {
        grid.getChildren().removeIf(node -> node instanceof Button);
        clearSceneHandlers();
        setSceneBasicHandler();
        updateInventory();
    }
    @JsonIgnore
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
    @JsonIgnore
    protected Item getSelectedItem() {
        return selectedItem;
    }
    @JsonIgnore
    protected void setSelectedItem(Item selectedItem) {
        this.selectedItem = selectedItem;
    }
    @JsonIgnore
    public void addTakenItem(Item takenItem) {
        this.takenItems.add(takenItem);
    }
    @JsonIgnore
    public List<Item> getTakenItems() {
        return takenItems;
    }
    @JsonIgnore
    public void removeTakenItem(Item takenItem) {
        this.takenItems.remove(takenItem);
    }

    /**
     * Set round borders for slot and border
     * @param rectangle rectangle to set round borders
     */
    @JsonIgnore
    protected void setRoundBorders(Rectangle rectangle) {
        //set round borders
        rectangle.setArcHeight(10);
        rectangle.setArcWidth(10);
    }
    @JsonIgnore
    public void setInventoryLabel(String text) {
        inventoryLabel.setText(text);
    }
}