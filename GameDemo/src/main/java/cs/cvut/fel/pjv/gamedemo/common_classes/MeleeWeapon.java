package cs.cvut.fel.pjv.gamedemo.common_classes;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents a melee weapon, which is a subclass of Item. Used by the player to attack enemies.
 */
public class MeleeWeapon extends Item {

    //region Attributes
    @JsonProperty("damage")
    private int damage;
    @JsonProperty("attackSpeed")
    private final int attackSpeed;
    //endregion

    //region Constructors
    @JsonCreator
    public MeleeWeapon(@JsonProperty("name") String name, @JsonProperty("texturePath") String texturePath, @JsonProperty("damage") int damage, @JsonProperty("attackSpeed") int attackSpeed) {
        super(name, texturePath, Constants.ItemType.MELEE_WEAPON);
        this.damage = damage;
        this.attackSpeed = attackSpeed;
    }
    //endregion

    //region Getters & Setters

    //region Getters
    @JsonIgnore
    public int getDamage() {
        return damage;
    }
    @JsonIgnore
    public int getAttackSpeed() {
        return attackSpeed;
    }
    //endregion

    //region Setters
    @JsonIgnore
    public void setDamage(int damage) {
        this.damage = damage;
    }
    //endregion

    //endregion
}