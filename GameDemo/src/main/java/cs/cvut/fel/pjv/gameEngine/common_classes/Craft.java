public class Craft {
    private Item firstItem;
    private Item secondItem;

    public Craft() {
        this.firstItem = null;
        this.secondItem = null;
    }

    public Craft(Item firstItem, Item secondItem) {
        this.firstItem = firstItem;
        this.secondItem = secondItem;
    }

    public void setFirstItem(Item firstItem) {
        this.firstItem = firstItem;
    }

    public Item getFirstItem() {
        return firstItem;
    }

    public void setSecondItem(Item secondItem) {
        this.secondItem = secondItem;
    }

    public Item getSecondItem() {
        return secondItem;
    }

    public void craft(Item firstItem, Item secondItem) {
        //check if in dictionary
    }
}