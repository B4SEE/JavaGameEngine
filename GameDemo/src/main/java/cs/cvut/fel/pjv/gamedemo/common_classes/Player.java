package cs.cvut.fel.pjv.gamedemo.common_classes;

import cs.cvut.fel.pjv.gamedemo.engine.Checker;
import javafx.scene.Scene;
import javafx.scene.shape.Shape;

import java.util.List;
import java.util.Objects;

/**
 * Represents a player in the game.
 */
public class Player extends Entity {
    private int hunger;
    private final int maxHunger;
//    private Item handItem;
    private PlayerInventory playerInventory;
    private boolean shouldStarve = false;
    private boolean canHeal = true;
    private long whenHealed;
    private long whenStarved;

    public Player(String name, String texturePath, int positionX, int positionY) {
        super(name, texturePath, "PLAYER", positionX, positionY, 0, Constants.PLAYER_MAX_HEALTH, 0, null);
        super.setHeight(2);
        super.setDamage(Constants.PLAYER_BASIC_DAMAGE);
        this.hunger = Constants.PLAYER_MAX_HUNGER;
        this.maxHunger = Constants.PLAYER_MAX_HUNGER;
        this.playerInventory = new PlayerInventory();
    }

    public Player(Wagon currentWagon) {
        super("PLAYER_NAME", "texturePath", "PLAYER", Constants.PLAYER_START_POS_X, Constants.PLAYER_START_POS_Y, Constants.PLAYER_HITBOX, Constants.PLAYER_MAX_HEALTH, Constants.PLAYER_BASIC_DAMAGE, currentWagon);
        super.setHeight(2);
        super.setDamage(Constants.PLAYER_BASIC_DAMAGE);
        this.hunger = Constants.PLAYER_MAX_HUNGER;
        this.maxHunger = Constants.PLAYER_MAX_HUNGER;
        this.playerInventory = new PlayerInventory();
    }

    public void setHunger(int hunger) {
        this.hunger = hunger;
    }

    public int getHunger() {
        return hunger;
    }
    public int getMaxHunger() {
        return maxHunger;
    }
    public void setHandItem(Item handItem) {
        this.playerInventory.setMainHandItem(handItem);
    }

    public Item getHandItem() {
        return playerInventory.getMainHandItem();
    }

    public void setPlayerInventory(PlayerInventory playerInventory) {
        this.playerInventory = playerInventory;
    }

    public PlayerInventory getPlayerInventory() {
        return playerInventory;
    }

    /**
     * Method to heal the player; the player heals 1 health point every healCooldown seconds if his hunger/saturation is greater than his health
     * @param time current time
     */
    public void heal(long time) {
        int healCooldown = 2;
        if (canHeal) {
            if (hunger >= super.getHealth()) {
                setHealth(super.getHealth() + 1);
                setHunger(hunger - 1);
            }
            whenHealed = time;
            canHeal = false;
            return;
        }
        if ((time - whenHealed != 0) && (time - whenHealed) % healCooldown == 0) {
            whenStarved = 0;
            canHeal = true;
        }
    }

    /**
     * Method to eat food and increase the player's hunger/saturation
     * @param food food to be eaten
     */
    public void eat(Food food) {
        if ((hunger + food.nourishment) > maxHunger) {
            hunger = maxHunger;
        } else {
            hunger += food.nourishment;
        }
    }

    /**
     * Method to starve the player; the player loses 1 health point every starveCooldown seconds if his hunger/saturation is less than his health
     * @param time current time
     */
    public void starve(long time) {
        int starveCooldown = 3;
        if (shouldStarve) {
            if (hunger <= 0) {
                setHealth(super.getHealth() - 1);
            } else  {
                setHunger(hunger - 1);
                System.out.println("Hunger: " + hunger);
            }
            whenStarved = time;
            shouldStarve = false;
            return;
        }
        if (hunger <= 0) {
            shouldStarve = true;
            return;
        }
        if ((time - whenStarved != 0) && (time - whenStarved) % starveCooldown == 0) {
            whenStarved = 0;
            shouldStarve = true;
        }
    }
    /**
     * Method to use the player's hand item;
     * if the hand item is food, the player eats it;
     * if the hand item is a firearm, the player shoots it;
     * if the hand item is a melee weapon, the player attacks with it.
     * @param time current time
     */
    public void useHandItem(long time) {
        if (playerInventory.getMainHandItem() == null) {
            return;
        }
        if (playerInventory.getMainHandItem() instanceof Food) {
            eat((Food) playerInventory.getMainHandItem());
            playerInventory.setMainHandItem(null);
        } else if (playerInventory.getMainHandItem() instanceof MeleeWeapon) {
            super.setDamage(super.getDamage() + ((MeleeWeapon) playerInventory.getMainHandItem()).getDamage());
            super.setCooldown(((MeleeWeapon) playerInventory.getMainHandItem()).getAttackSpeed());
            List<Entity> targets = super.getCurrentWagon().getEntities();
            tryAttack(this, targets, time);
            super.setDamage(Constants.PLAYER_BASIC_DAMAGE);
            super.setCooldown(2);//entities will not judge player's attack with knife, because they "can't see" the knife.
        } else if (playerInventory.getMainHandItem() instanceof Firearm) {
            List<Entity> targets = super.getCurrentWagon().getEntities();
            tryAttack(this, targets, time);
        }
    }
    /**
     * Method to shoot a firearm at a target
     * @param firearm firearm to be
     * @param targets list of entities to shoot at
     * @param aimX x-coordinate of the mouse cursor
     * @param aimY y-coordinate of the mouse cursor
     * @param time current time
     * @param obstacles obstacles that the bullet can't pass through
     */
    public void shoot(Firearm firearm, List<Entity> targets, int aimX, int aimY, long time, Shape obstacles) {
        Checker checker = new Checker();
        System.out.println(playerInventory.getAmmo());
        if (playerInventory.getAmmo() <= 0) {
            //sound of no ammo
            return;
        }
        System.out.println("Shooting");
        playerInventory.setAmmo(playerInventory.getAmmo() - 1);
        for (Entity target : targets) {
            if (target != null && target.isAlive() && checker.checkIfPlayerCanShoot(this, aimX, aimY, target, obstacles, time) && playerInventory.getAmmo() >= 0) {
                target.takeDamage(firearm.getDamage());
                System.out.println("Target health: " + target.getHealth());
                for (Entity entity : targets) {
                    if (checker.checkIfEntityCanSee(entity, this, obstacles, time) && Objects.equals(entity.getBehaviour(), Constants.NEUTRAL)) {
                        entity.setBehaviour(Constants.AGGRESSIVE);
                    }
                }
                //sound of shooting
                return;
            }
        }
        //sound of missed shot
    }
}