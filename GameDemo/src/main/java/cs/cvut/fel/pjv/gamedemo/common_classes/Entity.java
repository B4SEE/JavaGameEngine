package cs.cvut.fel.pjv.gamedemo.common_classes;

import javafx.scene.image.ImageView;
import javafx.scene.shape.Shape;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Entity {
    private int id;//might be unnecessary
    private String name;
    private String texturePath;
    private String type;
    private String initialBehaviour;
    private String behaviour;
    private int startPositionX;
    private int startPositionY;
    private int positionX;
    private int positionY;
    private int height;
    private int speed_x;
    private int speed_y;
    private int hitBoxSize;
    private Shape hitbox;
    private Shape attackRange;
    private int attackRangeSize;
    private int health;
    private int maxHealth;
    private int damage;
    //
    private int cooldown;
    private boolean canAttack = true;
    private long whenAttacked;
    //
    private ImageView entityView;

    private Wagon currentWagon;

    public Entity(String name, String texturePath) {
        this.name = name;
        this.texturePath = texturePath;
        this.maxHealth = Constants.ENTITY_BASIC_MAX_HEALTH;
    }

    public Entity(int id, String name, String texturePath, String type, int positionX, int positionY, int hitBoxSize, int health, int damage, Wagon currentWagon) {
        this.id = id;
        this.name = name;
        this.texturePath = texturePath;
        this.type = type;
        this.positionX = positionX;
        this.positionY = positionY;
        this.hitBoxSize = hitBoxSize;
        this.health = health;
        this.maxHealth = health;
        this.damage = damage;
        this.currentWagon = currentWagon;
    }
    public void setAsDefaultEnemy() {
        setType(Constants.ENEMY);

        setBehaviour(Constants.AGGRESSIVE);
        setInitialBehaviour(getBehaviour());

        setHeight(Constants.ENEMY_BASIC_HEIGHT);

        speed_x = (int) (Math.random() * (Constants.ENEMY_BASIC_SPEED_X_MAX - Constants.ENEMY_BASIC_SPEED_X_MIN) + Constants.ENEMY_BASIC_SPEED_X_MIN);
        System.out.println(speed_x);
        speed_y = (int) (Math.random() * (Constants.ENEMY_BASIC_SPEED_Y_MAX - Constants.ENEMY_BASIC_SPEED_Y_MIN) + Constants.ENEMY_BASIC_SPEED_Y_MIN);
        System.out.println(speed_y);

        int maxHealth = (int) (Math.random() * (Constants.ENEMY_BASIC_MAX_HEALTH_MAX - Constants.ENEMY_BASIC_MAX_HEALTH_MIN) + Constants.ENEMY_BASIC_MAX_HEALTH_MIN);
        maxHealth = (maxHealth / 10) * 10;
        this.maxHealth = maxHealth;

        setHealth(maxHealth);

        setHitBoxSize(Constants.ENEMY_BASIC_HITBOX);
        setAttackRangeSize(Constants.ENEMY_BASIC_ATTACK_RANGE);

        setDamage(Constants.ENEMY_BASIC_DAMAGE);
        setCooldown(Constants.ENEMY_BASIC_COOLDOWN);
    }

    public void setAsDefaultNPC() {
        setType(Constants.NPC);
        setBehaviour(Constants.NEUTRAL);
        setInitialBehaviour(getBehaviour());
        setHeight(Constants.ENEMY_BASIC_HEIGHT);
        setHitBoxSize(Constants.ENEMY_BASIC_HITBOX);
        setAttackRangeSize(Constants.ENEMY_BASIC_ATTACK_RANGE);
        setDamage(Constants.ENEMY_BASIC_DAMAGE);
        setCooldown(Constants.ENEMY_BASIC_COOLDOWN);
        setHealth(maxHealth);
    }
    public int getId() {
        return id;
    }
    public String getName() {
        return name;
    }

    public void setTexturePath(String texturePath) {
        this.texturePath = texturePath;
    }

    public String getTexturePath() {
        return texturePath;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public String getInitialBehaviour() {
        return initialBehaviour;
    }

    public void setInitialBehaviour(String initialType) {
        this.initialBehaviour = initialType;
    }

    public void setBehaviour(String behaviour) {
        this.behaviour = behaviour;
    }

    public String getBehaviour() {
        return behaviour;
    }

    public void setStartPositionX(int startPositionX) {
        this.startPositionX = startPositionX;
    }

    public int getStartPositionX() {
        return startPositionX;
    }

    public void setStartPositionY(int startPositionY) {
        this.startPositionY = startPositionY;
    }

    public int getStartPositionY() {
        return startPositionY;
    }
    public void setPositionX(int positionX) {
        this.positionX = positionX;
    }

    public int getPositionX() {
        return positionX;
    }

    public void setPositionY(int positionY) {
        this.positionY = positionY;
    }

    public int getPositionY() {
        return positionY;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getHeight() {
        return height;
    }

    public void setSpeedX(int speed_x) {
        this.speed_x = speed_x;
    }
    public int getSpeedX() {
        return speed_x;
    }
    public void setSpeedY(int speed_y) {
        this.speed_y = speed_y;
    }
    public int getSpeedY() {
        return speed_y;
    }
    public void setHitBoxSize(int hitBoxSize) {
        this.hitBoxSize = hitBoxSize;
    }

    public int getHitBoxSize() {
        return hitBoxSize;
    }
    public void setHitbox(Shape hitbox) {
        this.hitbox = hitbox;
    }
    public Shape getHitbox() {
        return hitbox;
    }
    public void setAttackRange(Shape attackRange) {
        this.attackRange = attackRange;
    }
    public Shape getAttackRange() {
        return attackRange;
    }
    public void setAttackRangeSize(int attackRangeSize) {
        this.attackRangeSize = attackRangeSize;
    }
    public int getAttackRangeSize() {
        return attackRangeSize;
    }
    public void setHealth(int health) {
        this.health = health;
    }

    public int getHealth() {
        return health;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    public int getDamage() {
        return damage;
    }

    public void setCooldown(int cooldown) {
        this.cooldown = cooldown;
    }

    public int getCooldown() {
        return cooldown;
    }

    public void setCanAttack(boolean canAttack) {
        this.canAttack = canAttack;
    }

    public boolean getCanAttack() {
        return canAttack;
    }

    public void setWhenAttacked(long whenAttacked) {
        this.whenAttacked = whenAttacked;
    }

    public long getWhenAttacked() {
        return whenAttacked;
    }

    public void setEntityView(ImageView entityView) {
        this.entityView = entityView;
    }

    public ImageView getEntityView() {
        return entityView;
    }

    public void setCurrentWagon(Wagon currentWagon) {
        this.currentWagon = currentWagon;
    }

    public Wagon getCurrentWagon() {
        return currentWagon;
    }

    public void takeDamage(int damage) {
        System.out.println(name + " took " + damage + " damage.");
        health -= damage;
    }

    public void attack(Entity target) {
        target.takeDamage(damage);
        if (Objects.equals(target.getBehaviour(), "NEUTRAL")) {
            target.setBehaviour("ENEMY");
        }
    }

    public void move(int deltaX, int deltaY) {
        positionX += deltaX;
        positionY += deltaY;
    }

    public boolean isAlive() {
        return health > 0;
    }

    /**
     * Returns an array of entities in the attack range of the entity.
     * @param entities array of entities
     * @return array of entities in the attack range of the entity
     */
    public List<Entity> inAttackRange(List<Entity> entities) {
        List<Entity> inRange = new ArrayList<>();
        int i = 0;
        for (Entity entity : entities) {
            if (entity != null) {
                if (entity != this) {
                    if (entity.isAlive() && checkIntersection(attackRange, entity.getHitbox())) {
                        inRange.add(entity);
                        i++;
                    }
                }
            }
        }
        return inRange;
    }
    /**
     * Checks if the entity's attack range intersects with the hitbox of another entity.
     * @param shape1 attack range of the entity
     * @param shape2 hitbox of another entity
     * @return true if the attack range intersects with the hitbox of another entity, false otherwise
     */
    private boolean checkIntersection(Shape shape1, Shape shape2) {
        return shape1.getBoundsInParent().intersects(shape2.getBoundsInParent());
    }

    /**
     * Checks if the entity can attack the player.
     * If the entity is in attack range and can attack, the entity attacks the player.
     * Entity cannot attack while the cooldown is active.
     * @param entity the entity
     */
    public void tryAttack(Entity entity, List<Entity> targets, long time) { //needs to be moved to Entity class
        if (entity.getCooldown() == 0) {
            entity.setCanAttack(true);
        }
        List<Entity> inAttackRange = entity.inAttackRange(targets);
        if (entity.getCanAttack()) {
            for (Entity target : inAttackRange) {
                if (target != null) {
                    entity.attack(target);
                }
            }
            entity.setCanAttack(false);
            entity.setWhenAttacked(time);
            return;
        }
        if (!entity.getCanAttack() && (time - entity.getWhenAttacked() != 0) && (time - entity.getWhenAttacked()) % entity.getCooldown() == 0) {
            entity.setCanAttack(true);
            entity.setWhenAttacked(0);
        }
    }
}