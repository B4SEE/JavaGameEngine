package cs.cvut.fel.pjv.gamedemo.common_classes;

public class Firearm extends Item {
    private int damage;
    private int shootingSpeed;

    public Firearm(String name, String texturePath, int damage, int shootingSpeed) {
        super(name, texturePath, "WEAPON");
        this.damage = damage;
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