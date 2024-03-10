package cs.cvut.fel.pjv.gamedemo.common_classes;

import javafx.scene.image.ImageView;
import javafx.scene.shape.Shape;

public class Entity {
    private final int id;
    private final String name;
    private String texturePath;
    private String type;
    private String behaviour;
    private int startPositionX;
    private int startPositionY;
    private int positionX;
    private int positionY;
    private int height;
    private int hitBoxSize;
    private Shape hitbox;
    private Shape attackRange;
    private int attackRangeSize;
    private int health;
    private final int maxHealth;
    private int damage;
    private ImageView entityView;

    private Wagon currentWagon;

    public Entity(int id, String name, String texturePath, int maxHealth) {
        this.id = id;
        this.name = name;
        this.texturePath = texturePath;
        this.maxHealth = maxHealth;
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
        health -= damage;
    }

    public void attack(Entity target) {
        target.takeDamage(damage);
    }

    public void move(int deltaX, int deltaY) {
        positionX += deltaX;
        positionY += deltaY;
    }

    public boolean isAlive() {
        return health > 0;
    }

    public Entity[] inAttackRange(Entity[] entities) {
        Entity[] inRange = new Entity[entities.length];
        int i = 0;
        for (Entity entity : entities) {
            if (entity != null) {
                if (entity != this) {
                    if (entity.isAlive() && checkIntersection(attackRange, entity.getHitbox())) {
                        inRange[i] = entity;
                        i++;
                    }
                }
            }
        }
        return inRange;
    }

    private boolean checkIntersection(Shape shape1, Shape shape2) {
        return shape1.getBoundsInParent().intersects(shape2.getBoundsInParent());
    }
}