package cs.cvut.fel.pjv.gamedemo.common_classes;

import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.stage.Stage;

public class Inventory {
    public final int inventorySize;
    private Item[] itemsArray;
    private Label itemNameLabel = new Label();

    private int deltaX = 1;
    private int deltaY = 0;

    public Inventory(int size) {
        inventorySize = size;
        itemsArray = new Item[size];
    }
    public Scene openInventory(boolean playerInventory) {//this implementation allows player to only take items from inventory, but not to put them back
        Pane grid = new Pane();
        drawInventory(300, 100, grid, playerInventory);
        Scene scene = new Scene(grid, Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
        return scene;
    }
    private void drawMainHandSlot(int leftCornerX, int leftCornerY, Pane grid) {
        int slotSize = 64;
        int slotGap = 20;
        int mainHandSlotGap = 1;
        Rectangle mainHandSlot = new Rectangle(slotSize, slotSize);
        setRoundBorders(mainHandSlot);
        mainHandSlot.setStyle("-fx-fill: #a20808; -fx-stroke: #ffffff; -fx-stroke-width: 10");
        //set main hand slot near inventory center at the left with gap
        mainHandSlot.setX(leftCornerX - (slotSize + slotGap) - mainHandSlotGap * (slotSize + slotGap));
        mainHandSlot.setY(leftCornerY + (inventorySize / Constants.INVENTORY_MAX_WIDTH / 2) * (slotSize + slotGap));
        grid.getChildren().add(mainHandSlot);
    }
    private void drawCraftTable(int leftCornerX, int leftCornerY, Pane grid) {
        int slotSize = 64;
        int slotGap = 20;
        int craftSlotGap = 1;
        //2 slots for crafting, 1 for result

        int x = leftCornerX - (slotSize + slotGap) - craftSlotGap * (slotSize + slotGap);
        int y = leftCornerY + (inventorySize / Constants.INVENTORY_MAX_WIDTH) * (slotSize + slotGap) + craftSlotGap * (slotSize + slotGap);

        Rectangle craftSlot1 = new Rectangle(slotSize, slotSize);
        setRoundBorders(craftSlot1);
        craftSlot1.setStyle("-fx-fill: #a20808; -fx-stroke: #ffffff; -fx-stroke-width: 10");
        craftSlot1.setX(x);
        craftSlot1.setY(y);

        Rectangle craftSlot2 = new Rectangle(slotSize, slotSize);
        setRoundBorders(craftSlot2);
        craftSlot2.setStyle("-fx-fill: #a20808; -fx-stroke: #ffffff; -fx-stroke-width: 10");
        craftSlot2.setX(x + 1 * (slotSize + slotGap));
        craftSlot2.setY(y);

        ImageView imageView = new ImageView(new Image("arrow.png"));
        imageView.setFitHeight(slotSize);
        imageView.setFitWidth(slotSize);
        imageView.setX(x + 2 * (slotSize + slotGap));
        imageView.setY(y);

        Rectangle resultSlot = new Rectangle(slotSize, slotSize);
        setRoundBorders(resultSlot);
        resultSlot.setStyle("-fx-fill: #476946; -fx-stroke: #ffffff; -fx-stroke-width: 10");
        resultSlot.setX(x + 3 * (slotSize + slotGap));
        resultSlot.setY(y);

        grid.getChildren().add(craftSlot1);
        grid.getChildren().add(craftSlot2);
        grid.getChildren().add(imageView);
        grid.getChildren().add(resultSlot);
    }
    private void drawInventory(int leftCornerX, int leftCornerY, Pane grid, boolean playerInventory) {
        int slotSize = 64;
        int slotGap = 20;
        int mainHandSlotGap = 3;

        //add big border to inventory

        Rectangle border = new Rectangle(Constants.INVENTORY_MAX_WIDTH * (slotSize + slotGap) + slotGap, (inventorySize / Constants.INVENTORY_MAX_WIDTH) * (slotSize + slotGap) + slotGap);
        border.setStyle("-fx-stroke: #ffffff; -fx-stroke-width: 10");
        border.setX(leftCornerX - slotGap);
        border.setY(leftCornerY - slotGap);
        grid.getChildren().add(border);

        grid.setStyle("-fx-background-color: #000000; -fx-border-color: #ffffff;");

        if (playerInventory) {
            drawMainHandSlot(leftCornerX, leftCornerY, grid);
            drawCraftTable(leftCornerX, leftCornerY, grid);
        }

        for (int i = 0; i < inventorySize; i++) {
            int i1 = leftCornerX + i % Constants.INVENTORY_MAX_WIDTH * (slotSize + slotGap);
            int i2 = leftCornerY + i / Constants.INVENTORY_MAX_WIDTH * (slotSize + slotGap);
            if (itemsArray[i] != null) {
                ImageView imageView = new ImageView(itemsArray[i].texturePath);
                imageView.setFitHeight(slotSize);
                imageView.setFitWidth(slotSize);
                //add item to grid (left aligned)
                imageView.setX(i1);
                imageView.setY(i2);
                imageView.setStyle("-fx-stroke: #ffffff; -fx-stroke-width: 10");
                grid.getChildren().add(imageView);
            } else {
                //add empty slot
                Rectangle rectangle = new Rectangle(slotSize, slotSize);
                setRoundBorders(rectangle);
                rectangle.setStyle("-fx-stroke: #ffffff; -fx-stroke-width: 10");
                rectangle.setX(i1);
                rectangle.setY(i2);
                grid.getChildren().add(rectangle);
            }
        }
    }

    private void setRoundBorders(Rectangle rectangle) {
        rectangle.setArcHeight(10);
        rectangle.setArcWidth(10);
    }

    private void addSceneHandler(Scene scene, GridPane grid) {//works but not as expected; when item selected, there is no way to deselect it and there is need to click twice to put;
        scene.setOnMouseMoved(e -> {
            int x = (int) e.getX() / (64 + 10) - deltaX;
            int y = (int) e.getY() / (64 + 10) - deltaY;
            int index = y * Constants.INVENTORY_MAX_WIDTH + x;

            grid.getChildren().remove(itemNameLabel);
            itemNameLabel.setStyle("-fx-font-size: 20; -fx-text-fill: #ffffff;");
            if (index >= 0) {
                if (index < inventorySize && itemsArray[index] != null) {
                    itemNameLabel.setText(itemsArray[index].name);
                    //if clicked on item, select it (border)
                    scene.setOnMouseClicked(event -> {
                        Shape shape = new Rectangle(64, 64);
                        shape.setStyle("-fx-stroke: #ffffff;");
                        grid.add(shape, x + deltaX, y + deltaY);

                        scene.setOnMouseClicked(event1 -> {
                            int x1 = (int) event1.getX() / (64 + 10) - deltaX;
                            int y1 = (int) event1.getY() / (64 + 10) - deltaY;
                            int index1 = y1 * Constants.INVENTORY_MAX_WIDTH + x1;
                            if (index1 >= 0 && index1 < inventorySize && itemsArray[index1] == null) {
                                itemsArray[index1] = itemsArray[index];
                                itemsArray[index] = null;
                                grid.getChildren().remove(shape);
                                grid.getChildren().remove(itemNameLabel);
                                drawInventory(grid, true);
                            } else if (index1 >= 0 && index1 < inventorySize) {
                                if (index1 == index) {
                                    grid.getChildren().remove(shape);
                                }
                            }
                            scene.setOnMouseClicked(null);
                        });
                    });
                } else {
                    itemNameLabel.setText("Empty slot");
                }
            } else {
                itemNameLabel.setText("Main hand slot");
            }
            grid.add(itemNameLabel, Constants.INVENTORY_MAX_WIDTH + deltaX, y + deltaY);
        });
    }
    private void drawInventory(GridPane grid, boolean playerInventory) {//should be implemented
        grid.getChildren().clear();
        grid.setStyle("-fx-background-color: #000000; -fx-border-color: #ffffff;");
        grid.setPrefSize(800, 800);
        //add style to grid lines
        grid.setHgap(10);
        grid.setVgap(10);
        int lowestY = inventorySize / Constants.INVENTORY_MAX_WIDTH;

        int deltaX = 1;
        int deltaY = 0;

        if (playerInventory) {

            Rectangle mainHandSlot = new Rectangle(64, 64);
            mainHandSlot.setStyle("-fx-fill: #af0303; -fx-stroke: #490606;");

            Rectangle craftSlot = new Rectangle(64, 64);
            craftSlot.setStyle("-fx-fill: #af0303; -fx-stroke: #490606;");

            grid.add(mainHandSlot, 0, 0);
        }
        for (int i = 0; i < inventorySize; i++) {
            if (itemsArray[i] != null) {
                ImageView imageView = new ImageView(itemsArray[i].texturePath);
                imageView.setFitHeight(64);
                imageView.setFitWidth(64);
                //add item to grid (left aligned)
                grid.add(imageView, i % Constants.INVENTORY_MAX_WIDTH + deltaX, i / Constants.INVENTORY_MAX_WIDTH + deltaY);
            } else {
                //add empty slot
                Rectangle rectangle = new Rectangle(64, 64);
                rectangle.setStyle("-fx-fill: #af0303; -fx-stroke: #490606;");
                grid.add(rectangle, i % Constants.INVENTORY_MAX_WIDTH + deltaX, i / Constants.INVENTORY_MAX_WIDTH + deltaY);
            }
        }
    }
    public void closeInventory(Stage stage) {
        stage.setScene(null);
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
        // Inventory is full
        return false;
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
        // Item not found
        return false;
    }

    public void setItemsArray(Item[] items) {
        itemsArray = items;
    }

    public Item[] getItemsArray() {
        return itemsArray;
    }
}