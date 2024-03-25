package cs.cvut.fel.pjv.gamedemo.common_classes;

import cs.cvut.fel.pjv.gamedemo.engine.Checker;
import cs.cvut.fel.pjv.gamedemo.engine.PathFinder;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Shape;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents an entity in the game, such as the player, enemies, NPCs, etc.
 */
public class Entity {
    private String name;
    private String texturePath;
    private String type;
    private String initialBehaviour;
    private String behaviour;
    private int intelligence = 2;//0 - 2; 0 - can't find path, 1 - can find path, 2 - can find path and go through doors
    private int negativeThreshold = 2;
    private int negativeCount = 0;
    private double startPositionX;
    private double startPositionY;
    private int[] startIndex;
    private List<Point2D> previousPositions = new ArrayList<>();
    private int counter = 0;
    private int[] lastKnownPosition = new int[]{0, 0};
    private double positionX;
    private double positionY;
    private int height;
    private int speed_x;
    private int speed_y;
    private int hitBoxSize;
    private Shape hitbox;
    private Shape trackPoint;
    private Shape attackRange;
    private int attackRangeSize;
    private int health;
    private int maxHealth;
    private int damage;
    //
    private int cooldown;
    private boolean canAttack = true;
    private long whenAttacked;
    private long whenStartedPursuing = 0;
    private ImageView entityView;

    private Wagon currentWagon;

    public Entity(String name, String texturePath) {
        this.name = name;
        this.texturePath = texturePath;
        this.maxHealth = Constants.ENTITY_BASIC_MAX_HEALTH;
    }

    public Entity(String name, String texturePath, String type, int positionX, int positionY, int hitBoxSize, int health, int damage, Wagon currentWagon) {
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
    public void setNegativeThreshold(int negativeThreshold) {
        this.negativeThreshold = negativeThreshold;
    }
    public int getNegativeThreshold() {
        return negativeThreshold;
    }
    public void setNegativeCount(int negativeCount) {
        this.negativeCount = negativeCount;
    }
    public int getNegativeCount() {
        return negativeCount;
    }

    /**
     * Sets the entity as the default enemy.
     * Speed and health are set to random values within the specified range.
     * Note: hitbox size, attack range size, damage and cooldown are set to default values, but it will be changed in the future (randomized).
     */
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

    /**
     * Not functional yet.
     */
    public void setAsDefaultNPC() {
        setType(Constants.NPC);
        setBehaviour(Constants.NEUTRAL);
        setInitialBehaviour(getBehaviour());
        setHeight(Constants.ENEMY_BASIC_HEIGHT);
        speed_x = (int) (Math.random() * (Constants.ENEMY_BASIC_SPEED_X_MAX - Constants.ENEMY_BASIC_SPEED_X_MIN) + Constants.ENEMY_BASIC_SPEED_X_MIN);
        System.out.println(speed_x);
        speed_y = (int) (Math.random() * (Constants.ENEMY_BASIC_SPEED_Y_MAX - Constants.ENEMY_BASIC_SPEED_Y_MIN) + Constants.ENEMY_BASIC_SPEED_Y_MIN);
        System.out.println(speed_y);
        setHitBoxSize(Constants.ENEMY_BASIC_HITBOX);
        setAttackRangeSize(Constants.ENEMY_BASIC_ATTACK_RANGE);
        setDamage(Constants.ENEMY_BASIC_DAMAGE);
        setCooldown(Constants.ENEMY_BASIC_COOLDOWN);
        setHealth(maxHealth);
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

    public void setIntelligence(int intelligence) {
        this.intelligence = intelligence;
    }

    public int getIntelligence() {
        return intelligence;
    }

    public void setStartPositionX(double startPositionX) {
        this.startPositionX = startPositionX;
    }

    public double getStartPositionX() {
        return startPositionX;
    }

    public void setStartPositionY(double startPositionY) {
        this.startPositionY = startPositionY;
    }

    public double getStartPositionY() {
        return startPositionY;
    }

    public void setStartIndex(int[] startIndex) {
        this.startIndex = startIndex;
    }

    public int[] getStartIndex() {
        return startIndex;
    }
    public void setPositionX(double positionX) {
        this.positionX = positionX;
    }

    public double getPositionX() {
        return positionX;
    }

    public void setPositionY(double positionY) {
        this.positionY = positionY;
    }

    public double getPositionY() {
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
    public void setTrackPoint(Shape trackPoint) {
        this.trackPoint = trackPoint;
    }
    public Shape getTrackPoint() {
        return trackPoint;
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

    public void setWhenStartedPursuing(long whenStartedPursuing) {
        this.whenStartedPursuing = whenStartedPursuing;
    }

    public long getWhenStartedPursuing() {
        return whenStartedPursuing;
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

    /**
     * Decreases the health of the entity by the specified amount.
     * @param damage the amount of damage
     */

    public void takeDamage(int damage) {
        System.out.println(name + " took " + damage + " damage.");
        health -= damage;
        if (Objects.equals(this.getBehaviour(), Constants.NEUTRAL)) {
            this.setBehaviour(Constants.AGGRESSIVE);
        }
    }
    /**
     * Checks if the entity is alive.
     * @return true if the entity is alive, false otherwise
     */
    public boolean isAlive() {
        return health > 0;
    }

    /**
     * Returns an array of entities in the attack range of the entity.
     * @param entities array of entities
     * @return list of entities in the attack range of the entity
     */
    public List<Entity> inAttackRange(List<Entity> entities) {
        Checker checker = new Checker();
        List<Entity> inRange = new ArrayList<>();
        int i = 0;
        for (Entity entity : entities) {
            if (entity != null) {
                if (entity != this) {
                    if (entity.isAlive() && checker.checkCollision(attackRange, entity.getHitbox())) {
                        inRange.add(entity);
                        i++;
                    }
                }
            }
        }
        return inRange;
    }

    /**
     * Checks if the entity can attack the player.
     * If the entity is in attack range and can attack, the entity attacks the player.
     * Entity cannot attack while the cooldown is active.
     * @param entity the entity
     */
    public void tryAttack(Entity entity, List<Entity> targets, long time) {
        if (entity.getCooldown() == 0) {
            entity.setCanAttack(true);
        }
        List<Entity> inAttackRange = entity.inAttackRange(targets);
        if (entity.getCanAttack()) {
            for (Entity target : inAttackRange) {
                if (target != null) {
                    target.takeDamage(entity.getDamage());
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
    public int[][] findPath(int[][] map, Object[][] objectsArray, Entity target, boolean returnToStart) {
        if (startIndex == null) {
            startIndex = findOnWhatObject(this, objectsArray);
        }

        int[][] path;

        int[] startPosition = findOnWhatObject(this, objectsArray);
        int[] targetPosition = findOnWhatObject(target, objectsArray);

        if (startPosition == null || targetPosition == null) {
            return null;
        }

        if (returnToStart) {
            targetPosition = startIndex;
        }

        PathFinder.Pair src = new PathFinder.Pair(startPosition[0], startPosition[1]);
        PathFinder.Pair dest = new PathFinder.Pair(targetPosition[0], targetPosition[1]);

        PathFinder pathFinder = new PathFinder();
        path = pathFinder.aStarSearch(map, map.length, map[0].length, src, dest);

        return path;
    }
    public int[] findOnWhatObject(Entity target, Object[][] objectsArray) {
        Checker checker = new Checker();
        for (int i = 0; i < objectsArray.length; i++) {
            for (int j = 0; j < objectsArray[0].length; j++) {
                if (objectsArray[i][j] != null) {
                    //reduce the hitbox size of the object to make it easier to find the object
                    if (checker.checkCollision(target.getTrackPoint(), objectsArray[i][j].getObjectHitbox())) {
                        int[] result = new int[2];
                        result[0] = i;
                        result[1] = j;
                        lastKnownPosition = result;
                        return result;
                    }
                }
            }
        }
        return lastKnownPosition;
    }
    public List<Point2D> getPreviousPositions() {
        return previousPositions;
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }

    public int getCounter() {
        return counter;
    }
}