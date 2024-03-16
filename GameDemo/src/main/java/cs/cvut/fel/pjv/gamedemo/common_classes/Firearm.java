package cs.cvut.fel.pjv.gamedemo.common_classes;
public class Firearm extends Item {
    private int ammo;
    public final int shootingSpeed;

    public Firearm(String name, String texturePath, int shootingSpeed) {
        super(name, texturePath, "WEAPON");
        this.shootingSpeed = shootingSpeed;
    }

    public Firearm(String name, String texturePath, int ammo, int shootingSpeed) {
        super(name, texturePath, "WEAPON");
        this.ammo = ammo;
        this.shootingSpeed = shootingSpeed;
    }

    public void setAmmo(int ammo) {
        this.ammo = ammo;
    }

    public int getAmmo() {
        return ammo;
    }
}