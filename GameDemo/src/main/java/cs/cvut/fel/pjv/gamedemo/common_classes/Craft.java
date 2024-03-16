package cs.cvut.fel.pjv.gamedemo.common_classes;

import java.util.List;

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

    public Item craft(Item firstItem, Item secondItem) {
//        String[] craftItems = {firstItem.getName(), secondItem.getName()};
        List<String> craftItems = List.of(firstItem.getName(), secondItem.getName());
        //check if in Constants.CRAFT_RECIPES
        String name = Constants.CRAFT_RECIPES.get(craftItems);
        System.out.println(name);
        if (name != null) {
            return new Item(name, Constants.ITEM_TEXTURES.get(name));
        } else {
            List<String> craftItemsReversed = List.of(secondItem.getName(), firstItem.getName());
            name = Constants.CRAFT_RECIPES.get(craftItemsReversed);
            if (name != null) {
                return new Item(name, Constants.ITEM_TEXTURES.get(name));
            }
        }
        return null;
    }
}