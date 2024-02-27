package cs.cvut.fel.pjv.gamedemo.common_classes;
public class Inventory {
    public final int inventorySize;
    private Item[] itemsArray;

    public Inventory(int size) {
        inventorySize = size;
        itemsArray = new Item[size];
    }

    public void openInventory() {
        // Open the inventory
    }

    public void closeInventory() {
        // Close the inventory
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