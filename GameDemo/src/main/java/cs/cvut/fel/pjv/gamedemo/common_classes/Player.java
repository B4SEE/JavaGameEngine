package cs.cvut.fel.pjv.gamedemo.common_classes;
public class Player extends Entity {
    private int hunger;
    private final int maxHunger;
    private Item handItem;
    private Inventory playerInventory;

    public Player(int id, String name, String texturePath, int positionX, int positionY) {
        super(id, name, texturePath, "PLAYER", positionX, positionY, 0, 0, 0, null);
        this.maxHunger = 0;
    }

    public Player(Wagon currentWagon) {
        super(0, "PLAYER_NAME", "texturePath", "PLAYER", Constants.PLAYER_START_POS_X, Constants.PLAYER_START_POS_Y, Constants.PLAYER_HITBOX, Constants.PLAYER_MAX_HEALTH, Constants.PLAYER_BASIC_DAMAGE, currentWagon);
        this.hunger = Constants.PLAYER_MAX_HUNGER;
        this.maxHunger = Constants.PLAYER_MAX_HUNGER;
        this.handItem = null;
        this.playerInventory = new Inventory(Constants.PLAYER_INVENTORY_SIZE);
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
        while (hunger >= super.getHealth()) {
            setHealth(super.getHealth() + 1);
            starve();
        }
        return;
    }

    public void eat(Food food) {
        if ((hunger + food.nourishment) > maxHunger) {
            hunger = maxHunger;
        } else {
            hunger += food.nourishment;
        }
    }

    public void starve() {
        setHunger(hunger - 1);
    }

    public void shoot(Firearm firearm) {
        if (firearm.getAmmo() > 0) {
            firearm.setAmmo(firearm.getAmmo() - 1);
        }
    }

    public void useHandItem(Item item) {
        if (item instanceof Food) {
            eat((Food) item);
        } else if (item instanceof Firearm) {
            shoot((Firearm) item);
        } else if (item instanceof MeleeWeapon) {
            super.setDamage(super.getDamage() + ((MeleeWeapon) item).getDamage());
            super.attack(super.closestEntity(super.currentWagon.getEntitiesArray()));
        }
    }
}