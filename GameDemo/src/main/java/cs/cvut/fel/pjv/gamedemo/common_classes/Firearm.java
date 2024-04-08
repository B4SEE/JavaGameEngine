package cs.cvut.fel.pjv.gamedemo.common_classes;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Firearm extends Item {
    @JsonProperty("damage")
    private int damage;
    @JsonProperty("shootingSpeed")
    private int shootingSpeed;

    @JsonCreator
    public Firearm(@JsonProperty("name") String name, @JsonProperty("texturePath") String texturePath, @JsonProperty("damage") int damage, @JsonProperty("shootingSpeed") int shootingSpeed) {
        super(name, texturePath, Constants.ItemType.FIREARM);
        this.damage = damage;
        this.shootingSpeed = shootingSpeed;
    }
    @JsonIgnore
    public int getDamage() {
        return damage;
    }
    @JsonIgnore
    public void setDamage(int damage) {
        this.damage = damage;
    }
    @JsonIgnore
    public int getShootingSpeed() {
        return shootingSpeed;
    }
    @JsonIgnore
    public void setShootingSpeed(int shootingSpeed) {
        this.shootingSpeed = shootingSpeed;
    }
}