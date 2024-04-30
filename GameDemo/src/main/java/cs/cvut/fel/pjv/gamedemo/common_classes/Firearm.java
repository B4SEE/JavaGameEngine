package cs.cvut.fel.pjv.gamedemo.common_classes;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Firearm extends Item {

    //region Attributes
    @JsonProperty("damage")
    private int damage;
    @JsonProperty("shootingSpeed")
    private int shootingSpeed;
    //endregion

    //region Constructors
    @JsonCreator
    public Firearm(@JsonProperty("name") String name, @JsonProperty("texturePath") String texturePath, @JsonProperty("damage") int damage, @JsonProperty("shootingSpeed") int shootingSpeed) {
        super(name, texturePath, Constants.ItemType.FIREARM);
        this.damage = damage;
        this.shootingSpeed = shootingSpeed;
    }
    //endregion

    //region Getters & Setters

    //region Getters
    @JsonIgnore
    public int getDamage() {
        return damage;
    }
    @JsonIgnore
    public int getShootingSpeed() {
        return shootingSpeed;
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