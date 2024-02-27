public class MeleeWeapon extends Item {
    private int damage;
    public final attackSpeed;

    public Firearm(int id, String name, String texturePath) {
        super(id, name, texturePath, "WEAPON");
    }

    public Firearm(int id, String name, String texturePath, int damage, int attackSpeed) {
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
}