package cs.cvut.fel.pjv.gamedemo.common_classes;

/**
 * Represents a melee weapon, which is a subclass of Item. Used by the player to attack enemies.
 */
public class MeleeWeapon extends Item {
    private int damage;
    private final int attackSpeed;

    public MeleeWeapon(String name, String texturePath, int attackSpeed) {
        super(name, texturePath, "WEAPON");
        this.attackSpeed = attackSpeed;
    }

    public MeleeWeapon(String name, String texturePath, int damage, int attackSpeed) {
        super(name, texturePath, "WEAPON");
        this.damage = damage;
        this.attackSpeed = attackSpeed;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    public int getDamage() {
        return damage;
    }

    public int getAttackSpeed() {
        return attackSpeed;
    }
}