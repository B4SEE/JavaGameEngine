package cs.cvut.fel.pjv.gamedemo.common_classes;

import javafx.scene.image.ImageView;

public class Entity {
    public final int id;
    public final String name;
    private String texturePath;
    private String type;
    private int positionX;
    private int positionY;
    private int hitBoxSize;
    private int health;
    private final int maxHealth;
    private int damage;
    private ImageView entityView;

    protected Wagon currentWagon;

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

    public void setHitBoxSize(int hitBoxSize) {
        this.hitBoxSize = hitBoxSize;
    }

    public int getHitBoxSize() {
        return hitBoxSize;
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

    public Entity closestEntity(Entity[] entities) {
        Entity closest = null;
        //find closest Entity
        return closest;
    }
}