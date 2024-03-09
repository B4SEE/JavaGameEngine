package cs.cvut.fel.pjv.gamedemo.common_classes;
public class MeleeWeapon extends Item {
    private int damage;
    private final int attackSpeed;

    public MeleeWeapon(int id, String name, String texturePath, int attackSpeed) {
        super(id, name, texturePath, "WEAPON");
        this.attackSpeed = attackSpeed;
    }

    public MeleeWeapon(int id, String name, String texturePath, int damage, int attackSpeed) {
        super(id, name, texturePath, "WEAPON");
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