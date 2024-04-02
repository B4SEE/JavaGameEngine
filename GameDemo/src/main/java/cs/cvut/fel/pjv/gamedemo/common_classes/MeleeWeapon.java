package cs.cvut.fel.pjv.gamedemo.common_classes;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents a melee weapon, which is a subclass of Item. Used by the player to attack enemies.
 */
public class MeleeWeapon extends Item {
    @JsonProperty("damage")
    private int damage;
    @JsonProperty("attackSpeed")
    private final int attackSpeed;

    public MeleeWeapon(String name, String texturePath, int damage, int attackSpeed) {
        super(name, texturePath, Constants.ItemType.MELEE_WEAPON);
        this.damage = damage;
        this.attackSpeed = attackSpeed;
    }
    @JsonIgnore
    public void setDamage(int damage) {
        this.damage = damage;
    }
    @JsonIgnore
    public int getDamage() {
        return damage;
    }
    @JsonIgnore
    public int getAttackSpeed() {
        return attackSpeed;
    }
}