package cs.cvut.fel.pjv.gamedemo.common_classes;

import com.fasterxml.jackson.annotation.*;
import cs.cvut.fel.pjv.gamedemo.engine.Atmospheric;
import cs.cvut.fel.pjv.gamedemo.engine.Checker;
import cs.cvut.fel.pjv.gamedemo.engine.EntitiesCreator;
import javafx.scene.shape.Shape;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.Objects;

/**
 * Represents a player in the game.
 */
public class Player extends Entity {
    @JsonIgnore
    private static final Logger logger = LogManager.getLogger(Player.class);
    @JsonProperty("hunger")
    private int hunger;
    @JsonIgnore//inventory will be saved in separate file
    private PlayerInventory playerInventory;
    @JsonIgnore
    private boolean shouldStarve = false;
    @JsonIgnore
    private boolean canHeal = true;
    @JsonIgnore
    private long whenHealed;
    @JsonIgnore
    private long whenStarved;
    @JsonCreator
    public Player(@JsonProperty("positionX") int positionX, @JsonProperty("positionY") int positionY) {
        super("PLAYER", Constants.PLAYER_TEXTURE_PATH);
        EntitiesCreator.setAsDefaultPlayer(this);
        super.setPositionX(positionX);
        super.setPositionY(positionY);
        this.hunger = Constants.PLAYER_MAX_HUNGER;
        this.playerInventory = new PlayerInventory();
    }
    @JsonSetter("hunger")
    public void setHunger(int hunger) {
        this.hunger = hunger;
    }
    @JsonIgnore
    public int getHunger() {
        return hunger;
    }
    @JsonIgnore
    public void setHandItem(Item handItem) {
        this.playerInventory.setMainHandItem(handItem);
    }
    @JsonIgnore
    public Item getHandItem() {
        return playerInventory.getMainHandItem();
    }
    @JsonIgnore
    public void setPlayerInventory(PlayerInventory playerInventory) {
        this.playerInventory = playerInventory;
    }
    @JsonIgnore
    public PlayerInventory getPlayerInventory() {
        return playerInventory;
    }

    /**
     * Method to heal the player; the player heals 1 health point every healCooldown seconds if his hunger/saturation is greater than his health
     * @param time current time
     */
    @JsonIgnore
    public void heal(long time) {
        int healCooldown = 2;
        if (canHeal) {
            if (hunger > super.getHealth()) {
                setHealth(super.getHealth() + 1);
                setHunger(hunger - 1);
                logger.info("Player healed, current health: " + super.getHealth());
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
    @JsonIgnore
    public void eat(Food food) {
        if ((hunger + food.getNourishment()) > Constants.PLAYER_MAX_HUNGER) {
            hunger = Constants.PLAYER_MAX_HUNGER;
        } else {
            hunger += food.getNourishment();
        }
        logger.info("Player ate " + food.getName() + ", current hunger: " + hunger);
    }

    /**
     * Method to starve the player; the player loses 1 health point every starveCooldown seconds if his hunger/saturation is less than his health
     * @param time current time
     */
    @JsonIgnore
    public void starve(long time) {
        int starveCooldown = 3;
        if (shouldStarve) {
            if (hunger <= 0) {
                setHealth(super.getHealth() - 1);
                logger.info("Player starved, current health: " + super.getHealth());
            } else  {
                setHunger(hunger - 1);
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
    @JsonIgnore
    public void useHandItem(long time) {
        if (playerInventory.getMainHandItem() == null) {
            return;
        }
        if (playerInventory.getMainHandItem() instanceof Food food) {
            eat(food);
            playerInventory.setMainHandItem(null);
        } else if (playerInventory.getMainHandItem() instanceof MeleeWeapon meleeWeapon) {
            logger.info("Player attacked with melee weapon");
            super.setDamage(super.getDamage() + (meleeWeapon.getDamage()));
            super.setCooldown(meleeWeapon.getAttackSpeed());
            List<Entity> targets = super.getCurrentWagon().getEntities();
            tryAttack(this, targets, time);
            super.setDamage(Constants.PLAYER_BASIC_DAMAGE);
            super.setCooldown(2);//entities will not judge player's attack with knife, because they "can't see" the knife.
        } else if (playerInventory.getMainHandItem() instanceof Firearm) {//hit (!shoot) with firearm
            logger.info("Player attacked with firearm");
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
    @JsonIgnore
    public void shoot(Firearm firearm, List<Entity> targets, int aimX, int aimY, long time, Shape obstacles) {
        logger.info("Player is trying to shoot");
        if (playerInventory.getAmmo() <= 0) {
            logger.info("No ammo");
            return;
        }
        logger.info("Player has ammo");
        Atmospheric.playSound("game_resources/sounds/gun/gunshot.wav");
        playerInventory.setAmmo(playerInventory.getAmmo() - 1);
        for (Entity target : targets) {
            if (target != null && target.isAlive() && Checker.checkIfPlayerCanShoot(this, aimX, aimY, target, obstacles, time)) {
                logger.info("Player hit " + target.getName() + ", ammo left: " + playerInventory.getAmmo());
                target.takeDamage(firearm.getDamage());
                for (Entity entity : targets) {
                    if (Checker.checkIfEntityRemember(entity, this, obstacles, time) && Objects.equals(entity.getBehaviour(), Constants.Behaviour.NEUTRAL) && Objects.equals(target.getBehaviour(), Constants.Behaviour.NEUTRAL)) {
                        logger.info("Entity " + entity.getName() + " saw player shooting at " + target.getName() + ", changing behaviour to AGGRESSIVE");
                        entity.setBehaviour(Constants.Behaviour.AGGRESSIVE);
                    }
                }
                return;
            }
        }
        logger.info("Player missed");
    }
}