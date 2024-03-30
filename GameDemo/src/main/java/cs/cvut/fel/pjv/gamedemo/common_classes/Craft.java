package cs.cvut.fel.pjv.gamedemo.common_classes;

import java.util.List;

/**
 * Represents a craft of two items, responsible for crafting logic.
 */
public class Craft {
    private Item firstItem;
    private Item secondItem;

    public Craft() {
        this.firstItem = null;
        this.secondItem = null;
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

    /**
     * Returns a new item if the combination of two items is in Constants.CRAFT_RECIPES; does not depend on the order of the items.
     * @param firstItem first item to craft
     * @param secondItem second item to craft
     * @return result item
     */
    public Item craft(Item firstItem, Item secondItem) {
        List<String> craftItems = List.of(firstItem.getName(), secondItem.getName());
        //check if in Constants.CRAFT_RECIPES
        String name = Constants.CRAFT_RECIPES.get(craftItems);
        System.out.println(name);
        if (name != null) {
            return new Item(name, Constants.ITEM_TEXTURES.get(name), (firstItem.getValue() + secondItem.getValue() - (int) (Math.random() * 10)));
        } else {
            List<String> craftItemsReversed = List.of(secondItem.getName(), firstItem.getName());
            name = Constants.CRAFT_RECIPES.get(craftItemsReversed);
            if (name != null) {
                return new Item(name, Constants.ITEM_TEXTURES.get(name), (firstItem.getValue() + secondItem.getValue() - (int) (Math.random() * 10)));
            }
        }
        return null;
    }
}