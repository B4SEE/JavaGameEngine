package cs.cvut.fel.pjv.gamedemo.common_classes;

import java.util.List;

public class Firearm extends Item {
    private int damage = 40;
    private int shootingSpeed;

    public Firearm(String name, String texturePath, int shootingSpeed) {
        super(name, texturePath, "WEAPON");
        this.shootingSpeed = shootingSpeed;
    }

    public int getDamage() {
        return damage;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }
    public int getShootingSpeed() {
        return shootingSpeed;
    }

    public void setShootingSpeed(int shootingSpeed) {
        this.shootingSpeed = shootingSpeed;
    }
}