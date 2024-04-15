package cs.cvut.fel.pjv.gamedemo.common_classes;

import com.fasterxml.jackson.annotation.*;
import cs.cvut.fel.pjv.gamedemo.engine.Checker;
import cs.cvut.fel.pjv.gamedemo.engine.Events;
import cs.cvut.fel.pjv.gamedemo.engine.PathFinder;
import cs.cvut.fel.pjv.gamedemo.engine.RandomHandler;
import javafx.geometry.Point2D;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Shape;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents an entity in the game, such as the player, enemies, NPCs, etc.
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "class"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = Player.class, name = "Player"),
        @JsonSubTypes.Type(value = Vendor.class, name = "Vendor"),
        @JsonSubTypes.Type(value = QuestNPC.class, name = "QuestNPC")
})
public class Entity {
    @JsonProperty("name")
    private String name;
    @JsonProperty("texturePath")
    private String texturePath;
    @JsonProperty("dialoguePath")
    private String dialoguePath;
    @JsonProperty("type")
    private Constants.EntityType type;
    @JsonProperty("behaviour")
    private Constants.Behaviour behaviour;
    @JsonProperty("initialBehaviour")
    private Constants.Behaviour initialBehaviour;
    @JsonProperty("intelligence")
    private int intelligence = 0;//0 - 2; 0 - can't find path, 1 - can find path, 2 - can find path and go through doors
    @JsonProperty("negativeThreshold")
    private int negativeThreshold = 2;
    @JsonProperty("negativeCount")
    private int negativeCount = 0;
    @JsonProperty("startPositionX")
    private double startPositionX;
    @JsonProperty("startPositionY")
    private double startPositionY;
    @JsonIgnore
    private int[] startIndex;
    @JsonIgnore
    private List<Point2D> previousPositions = new ArrayList<>();
    @JsonIgnore
    private int counter = 0;
    @JsonIgnore
    private int[] lastKnownPosition = new int[]{0, 0};
    @JsonProperty("positionX")
    private double positionX;
    @JsonProperty("positionY")
    private double positionY;
    @JsonProperty("height")
    private int height;
    @JsonProperty("speed_x")
    private int speed_x;
    @JsonProperty("speed_y")
    private int speed_y;
    @JsonProperty("hitBoxSize")
    private int hitBoxSize;
    @JsonIgnore
    private Shape hitbox;
    @JsonIgnore
    private Shape trackPoint;
    @JsonIgnore
    private Shape attackRange;
    @JsonProperty("attackRangeSize")
    private int attackRangeSize;
    @JsonProperty("health")
    private int health;
    @JsonProperty("maxHealth")
    private int maxHealth;
    @JsonProperty("damage")
    private int damage;
    @JsonProperty("cooldown")
    private int cooldown;
    @JsonIgnore
    private boolean canAttack = true;
    @JsonIgnore
    private long whenAttacked;
    @JsonIgnore
    private long whenStartedPursuing = 0;
    @JsonIgnore
    private ImageView entityView;
    @JsonIgnore//to prevent infinite loop
    private Wagon currentWagon;
    @JsonIgnore
    private Thread soundThread;
    @JsonIgnore
    private Thread animationThread;

    @JsonCreator
    public Entity(@JsonProperty("name") String name, @JsonProperty("texturePath") String texturePath, @JsonProperty("type") Constants.EntityType type, @JsonProperty("positionX") double positionX, @JsonProperty("positionY") double positionY) {
        this.name = name;
        this.texturePath = texturePath;
        this.type = type;
        this.positionX = positionX;
        this.positionY = positionY;
        this.maxHealth = health;
    }

    public Entity(String name, String texturePath) {
        this.name = name;
        this.texturePath = texturePath;
        this.maxHealth = Constants.ENTITY_BASIC_MAX_HEALTH;
    }

    public Entity(String name, String texturePath, Constants.EntityType type, int positionX, int positionY, int hitBoxSize, int health, int damage, Wagon currentWagon) {
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
    @JsonIgnore
    public void setNegativeThreshold(int negativeThreshold) {
        this.negativeThreshold = negativeThreshold;
    }
    @JsonIgnore
    public int getNegativeThreshold() {
        return negativeThreshold;
    }
    @JsonIgnore
    public void setNegativeCount(int negativeCount) {
        this.negativeCount = negativeCount;
    }
    @JsonIgnore
    public int getNegativeCount() {
        return negativeCount;
    }
    @JsonIgnore
    public String getName() {
        return name;
    }
    @JsonIgnore
    public void setTexturePath(String texturePath) {
        this.texturePath = texturePath;
    }
    @JsonIgnore
    public String getTexturePath() {
        return texturePath;
    }
    @JsonIgnore
    public void setDialoguePath(String dialoguePath) {
        this.dialoguePath = dialoguePath;
    }
    @JsonIgnore
    public String getDialoguePath() {
        return dialoguePath;
    }
    @JsonIgnore
    public void setType(Constants.EntityType type) {
        this.type = type;
    }
    @JsonIgnore
    public Constants.EntityType getType() {
        return type;
    }
    @JsonIgnore
    public void setInitialBehaviour(Constants.Behaviour initialBehaviour) {
        this.initialBehaviour = initialBehaviour;
    }
    @JsonIgnore
    public Constants.Behaviour getInitialBehaviour() {
        return initialBehaviour;
    }
    @JsonIgnore
    public void setBehaviour(Constants.Behaviour behaviour) {
        this.behaviour = behaviour;
    }
    @JsonIgnore
    public Constants.Behaviour getBehaviour() {
        return behaviour;
    }
    @JsonIgnore
    public void setIntelligence(int intelligence) {
        this.intelligence = intelligence;
    }
    @JsonIgnore
    public int getIntelligence() {
        return intelligence;
    }
    @JsonIgnore
    public void setStartPositionX(double startPositionX) {
        this.startPositionX = startPositionX;
    }
    @JsonIgnore
    public double getStartPositionX() {
        return startPositionX;
    }
    @JsonIgnore
    public void setStartPositionY(double startPositionY) {
        this.startPositionY = startPositionY;
    }
    @JsonIgnore
    public double getStartPositionY() {
        return startPositionY;
    }
    @JsonIgnore
    public void setStartIndex(int[] startIndex) {
        this.startIndex = startIndex;
    }
    @JsonIgnore
    public int[] getStartIndex() {
        return startIndex;
    }
    @JsonIgnore
    public void setPositionX(double positionX) {
        this.positionX = positionX;
    }
    @JsonIgnore
    public double getPositionX() {
        return positionX;
    }
    @JsonIgnore
    public void setPositionY(double positionY) {
        this.positionY = positionY;
    }
    @JsonIgnore
    public double getPositionY() {
        return positionY;
    }
    @JsonIgnore
    public void setHeight(int height) {
        this.height = height;
    }
    @JsonIgnore
    public int getHeight() {
        return height;
    }
    @JsonIgnore
    public void setSpeedX(int speed_x) {
        this.speed_x = speed_x;
    }
    @JsonIgnore
    public int getSpeedX() {
        return speed_x;
    }
    @JsonIgnore
    public void setSpeedY(int speed_y) {
        this.speed_y = speed_y;
    }
    @JsonIgnore
    public int getSpeedY() {
        return speed_y;
    }
    @JsonSetter("hitBoxSize")
    public void setHitBoxSize(int hitBoxSize) {
        this.hitBoxSize = hitBoxSize;
    }
    @JsonIgnore
    public int getHitBoxSize() {
        return hitBoxSize;
    }
    @JsonIgnore
    public void setHitbox(Shape hitbox) {
        this.hitbox = hitbox;
    }
    @JsonIgnore
    public Shape getHitbox() {
        return hitbox;
    }
    @JsonIgnore
    public void setTrackPoint(Shape trackPoint) {
        this.trackPoint = trackPoint;
    }
    @JsonIgnore
    public Shape getTrackPoint() {
        return trackPoint;
    }
    @JsonIgnore
    public void setAttackRange(Shape attackRange) {
        this.attackRange = attackRange;
    }
    @JsonIgnore
    public Shape getAttackRange() {
        return attackRange;
    }
    @JsonIgnore
    public void setAttackRangeSize(int attackRangeSize) {
        this.attackRangeSize = attackRangeSize;
    }
    @JsonIgnore
    public int getAttackRangeSize() {
        return attackRangeSize;
    }
    @JsonSetter("health")
    public void setHealth(int health) {
        this.health = health;
    }
    @JsonIgnore
    public int getHealth() {
        return health;
    }
    @JsonIgnore
    public void setMaxHealth(int maxHealth) {
        this.maxHealth = maxHealth;
    }
    @JsonIgnore
    public int getMaxHealth() {
        return maxHealth;
    }
    @JsonSetter("damage")
    public void setDamage(int damage) {
        this.damage = damage;
    }
    @JsonIgnore
    public int getDamage() {
        return damage;
    }
    @JsonIgnore
    public void setCooldown(int cooldown) {
        this.cooldown = cooldown;
    }
    @JsonIgnore
    public int getCooldown() {
        return cooldown;
    }
    @JsonIgnore
    public void setCanAttack(boolean canAttack) {
        this.canAttack = canAttack;
    }
    @JsonIgnore
    public boolean getCanAttack() {
        return canAttack;
    }
    @JsonIgnore
    public void setWhenAttacked(long whenAttacked) {
        this.whenAttacked = whenAttacked;
    }
    @JsonIgnore
    public long getWhenAttacked() {
        return whenAttacked;
    }
    @JsonIgnore
    public void setWhenStartedPursuing(long whenStartedPursuing) {
        this.whenStartedPursuing = whenStartedPursuing;
    }
    @JsonIgnore
    public long getWhenStartedPursuing() {
        return whenStartedPursuing;
    }
    @JsonIgnore
    public void setEntityView(ImageView entityView) {
        this.entityView = entityView;
    }
    @JsonIgnore
    public ImageView getEntityView() {
        return entityView;
    }
    @JsonIgnore
    public void setCurrentWagon(Wagon currentWagon) {
        this.currentWagon = currentWagon;
    }
    @JsonIgnore
    public Wagon getCurrentWagon() {
        return currentWagon;
    }
    @JsonIgnore
    public void setSoundThread(Thread soundThread) {
        this.soundThread = soundThread;
    }
    @JsonIgnore
    public Thread getSoundThread() {
        return soundThread;
    }
    @JsonIgnore
    public void setAnimationThread(Thread animationThread) {
        this.animationThread = animationThread;
    }
    @JsonIgnore
    public Thread getAnimationThread() {
        return animationThread;
    }


    /**
     * Decreases the health of the entity by the specified amount.
     * @param damage the amount of damage
     */
    @JsonIgnore
    public void takeDamage(int damage) {
        System.out.println(name + " took " + damage + " damage.");
        health -= damage;
        if (Objects.equals(this.getBehaviour(), Constants.Behaviour.NEUTRAL)) {
            this.setBehaviour(Constants.Behaviour.AGGRESSIVE);
            Events.setShouldCallGuard(true);
        }
    }
    /**
     * Checks if the entity is alive.
     * @return true if the entity is alive, false otherwise
     */
    @JsonIgnore
    public boolean isAlive() {
        return health > 0;
    }

    /**
     * Returns an array of entities in the attack range of the entity.
     * @param entities array of entities
     * @return list of entities in the attack range of the entity
     */
    @JsonIgnore
    public List<Entity> inAttackRange(List<Entity> entities) {
        List<Entity> inRange = new ArrayList<>();
        int i = 0;
        for (Entity entity : entities) {
            if (entity != null) {
                if (entity != this) {
                    if (entity.isAlive() && Checker.checkCollision(attackRange, entity.getHitbox())) {
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
    @JsonIgnore
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
    @JsonIgnore
    public int[][] findPath(int[][] map, int[] targetIndex) {
        if (this.startIndex == null) {
            this.startIndex = findOnWhatObject();
        }

        int[][] path;
        int[] startPosition = findOnWhatObject();

        if (startPosition == null || targetIndex == null) {
            return null;
        }

        PathFinder.Pair src = new PathFinder.Pair(startPosition[0], startPosition[1]);
        PathFinder.Pair dest = new PathFinder.Pair(targetIndex[0], targetIndex[1]);

        PathFinder pathFinder = new PathFinder();
        path = pathFinder.aStarSearch(map, map.length, map[0].length, src, dest);

        return path;
    }
    @JsonIgnore
    public int[] findOnWhatObject() {
        Object[][] objectsArray = currentWagon.getObjectsArray();
        for (int i = 0; i < objectsArray.length; i++) {
            for (int j = 0; j < objectsArray[0].length; j++) {
                if (objectsArray[i][j] != null) {
                    //reduce the hitbox size of the object to make it easier to find the object
                    if (Checker.checkCollision(this.getTrackPoint(), objectsArray[i][j].getObjectHitbox())) {
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
    @JsonIgnore
    public List<Point2D> getPreviousPositions() {
        return previousPositions;
    }
    @JsonIgnore
    public void setCounter(int counter) {
        this.counter = counter;
    }
    @JsonIgnore
    public int getCounter() {
        return counter;
    }

}