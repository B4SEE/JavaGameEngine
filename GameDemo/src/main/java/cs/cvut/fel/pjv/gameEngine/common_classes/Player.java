public class Player extends Entity {
    private int hunger;
    private final maxHunger;
    private Item handItem;
    private Inventory playerInventory;

    public Player(int x, int y, int health, int hunger, int maxHunger, Item handItem, Inventory playerInventory) {
        super(0, "PLAYER_NAME", "texturePath", "PLAYER", x, y, 1, health, 10);
        this.hunger = hunger;
        this.maxHunger = maxHunger;
        this.handItem = handItem;
        this.playerInventory = playerInventory;
    }

    public void setHunger(int hunger) {
        this.hunger = hunger;
    }

    public int getHunger() {
        return hunger;
    }

    public void setHandItem(Item handItem) {
        this.handItem = handItem;
    }

    public Item getHandItem() {
        return handItem;
    }

    public void setPlayerInventory(Inventory playerInventory) {
        this.playerInventory = playerInventory;
    }

    public Inventory getPlayerInventory() {
        return playerInventory;
    }

    public void eat(Item item) {
        if (item.getType().equals("FOOD")) {
            if (hunger + item.nourishment > maxHunger) {
                hunger = maxHunger;
            } else {
                hunger += item.nourishment;
            }
        }
    }

    public void starve() {
        hunger--;
    }

    public void useItem(Item item) {
        if (item.getType().equals("WEAPON")) {
            // Attack
        } else if (item.getType().equals("FOOD")) {
            eat(item);
        }
    }
}