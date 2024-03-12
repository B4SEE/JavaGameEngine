package cs.cvut.fel.pjv.gamedemo.common_classes;

import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class Inventory {
    public final int inventorySize;
    private Item[] itemsArray;

    public Inventory(int size) {
        inventorySize = size;
        itemsArray = new Item[size];
    }

    public void openInventory(Stage stage) {
        GridPane grid = new GridPane();
        grid.setStyle("-fx-background-color: #000000; -fx-border-color: #ffffff;");
        grid.setPrefSize(800, 800);
        //add style to grid lines
        grid.setHgap(10);
        grid.setVgap(10);
        for (int i = 0; i < inventorySize; i++) {
            if (itemsArray[i] != null) {
                ImageView imageView = new ImageView(itemsArray[i].texturePath);
                imageView.setFitHeight(50);
                imageView.setFitWidth(50);
                grid.add(imageView, i % 5, i / 5);
            } else {
                Rectangle rectangle = new Rectangle(50, 50);
                rectangle.setStyle("-fx-fill: #af0303; -fx-stroke: #490606;");
                grid.add(rectangle, i % Constants.INVENTORY_WIDTH, i / Constants.INVENTORY_WIDTH);
            }
        }
        grid.setGridLinesVisible(true);
        Scene inventoryScene = new Scene(grid, Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
        stage.setScene(inventoryScene);
    }
    public void closeInventory(Stage stage) {
        stage.setScene(null);
    }

    public void addItem(Item item) {
        if (item == null) {
            return;
        }
        for (int i = 0; i < inventorySize; i++) {
            if (itemsArray[i] == null) {
                itemsArray[i] = item;
                return;
            }
        }
        // Inventory is full
        return;
    }

    public void removeItem(Item item) {
        if (item == null) {
            return;
        }
        for (int i = 0; i < inventorySize; i++) {
            if (itemsArray[i] == item) {
                itemsArray[i] = null;
                return;
            }
        }
        // Item not found
        return;
    }

    public void setItemsArray(Item[] items) {
        itemsArray = items;
    }

    public Item[] getItemsArray() {
        return itemsArray;
    }
}