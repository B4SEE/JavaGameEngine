public class Player extends Entity {
    private int hunger;
    private final maxHunger;
    private Item handItem;
    private Inventory playerInventory;

    public Player() {
        super(0, "PLAYER_NAME", "texturePath", "PLAYER", Constants.PLAYER_START_POS_X, Constants.PLAYER_START_POS_Y, Constants.PLAYER_HITBOX, Constants.PLAYER_MAX_HEALTH, Constants.PLAYER_BASIC_DAMAGE);
        this.hunger = Constants.PLAYER_MAX_HUNGER;
        this.maxHunger = Constants.PLAYER_MAX_HUNGER;
        this.handItem = null;
        this.playerInventory = new Inventory(Constants.PLAYER_INVENTORY_SIZE)
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

    public void heal() {
        while (hunger >= health) {
            setHealth(health + 1);
            starve();
        }
        return;
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
        setHunger(hunger - 1);
    }

    public void useItem(Item item) {
        if (item.getType().equals("WEAPON")) {
            // Attack
        } else if (item.getType().equals("FOOD")) {
            eat(item);
        }
    }
}