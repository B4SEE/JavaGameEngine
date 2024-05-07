package cs.cvut.fel.pjv.gamedemo.common_classes;

import com.fasterxml.jackson.annotation.*;
import cs.cvut.fel.pjv.gamedemo.engine.utils.Checker;
import cs.cvut.fel.pjv.gamedemo.engine.Events;
import cs.cvut.fel.pjv.gamedemo.engine.utils.PathFinder;
import javafx.geometry.Point2D;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Shape;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

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

    //region Attributes
    @JsonIgnore
    private static final Logger logger = LogManager.getLogger(Entity.class);
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
    @JsonProperty("intelligence")
    private Constants.Intelligence intelligence = Constants.Intelligence.DEFAULT;
    @JsonProperty("negativeThreshold")
    private int negativeThreshold = Constants.ENTITY_BASIC_DEFAULT_NEGATIVE_THRESHOLD;
    @JsonProperty("negativeCount")
    private int negativeCount = 0;
    @JsonProperty("startPositionX")
    private double startPositionX;
    @JsonProperty("startPositionY")
    private double startPositionY;
    @JsonIgnore
    private int[] startIndex;
    @JsonIgnore
    private final List<Point2D> previousPositions = new ArrayList<>();
    @JsonIgnore
    private int counter = 0;
    @JsonIgnore
    private int[] lastKnownPosition = new int[]{0, 0};
    @JsonProperty("positionX")
    private double positionX;
    @JsonProperty("positionY")
    private double positionY;
    @JsonProperty("height")
    private int height = Constants.ENTITY_BASIC_HEIGHT;
    @JsonProperty
    private int width = Constants.ENTITY_BASIC_WIDTH;
    @JsonProperty("speed_x")
    private int speed_x;
    @JsonProperty("speed_y")
    private int speed_y;
    @JsonProperty("hitBoxSize")
    private int hitBoxSize = Constants.ENTITY_BASIC_HITBOX_SIZE;
    @JsonIgnore
    private Shape hitbox;
    @JsonIgnore
    private Shape trackPoint;
    @JsonIgnore
    private Shape attackRange;
    @JsonProperty("attackRangeSize")
    private int attackRangeSize = Constants.ENTITY_BASIC_ATTACK_RANGE_SIZE;
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
    @JsonIgnore
    private Wagon currentWagon;
    @JsonIgnore
    private Thread soundThread;
    //endregion

    //region Constructors
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
    //endregion

    //region Methods
    /**
     * Decreases the health of the entity by the specified amount.
     * @param damage the amount of damage
     */
    @JsonIgnore
    public void takeDamage(int damage) {
        health -= damage;
        logger.info("Entity " + this.getName() + " took " + damage + " damage, entity health: " + this.getHealth());
        if (health <= 0) {
            logger.info("Entity " + this.getName() + " died...");
            return;
        }
        if (Objects.equals(this.getBehaviour(), Constants.Behaviour.NEUTRAL)) {
            logger.info("Entity " + this.getName() + " is attacked, changing behaviour to AGGRESSIVE and calling guard...");
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
        for (Entity entity : entities) {
            if (entity != null) {
                if (entity != this) {
                    if (entity.isAlive() && Checker.checkCollision(attackRange, entity.getHitbox())) {
                        inRange.add(entity);
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
                    logger.info("Entity " + entity.getName() + " attacks " + target.getName() + "...");
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
    //endregion

    //region Getters & Setters

    //region Getters
    @JsonIgnore
    public List<Point2D> getPreviousPositions() {
        return previousPositions;
    }
    @JsonIgnore
    public int getCounter() {
        return counter;
    }
    @JsonIgnore
    public int getNegativeThreshold() {
        return negativeThreshold;
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
    public String getTexturePath() {
        return texturePath;
    }
    @JsonIgnore
    public String getDialoguePath() {
        return dialoguePath;
    }
    @JsonIgnore
    public Constants.EntityType getType() {
        return type;
    }
    @JsonIgnore
    public Constants.Behaviour getBehaviour() {
        return behaviour;
    }
    @JsonIgnore
    public Constants.Intelligence getIntelligence() {
        return intelligence;
    }
    @JsonIgnore
    public double getStartPositionX() {
        return startPositionX;
    }
    @JsonIgnore
    public double getStartPositionY() {
        return startPositionY;
    }
    @JsonIgnore
    public int[] getStartIndex() {
        return startIndex;
    }
    @JsonIgnore
    public double getPositionX() {
        return positionX;
    }
    @JsonIgnore
    public double getPositionY() {
        return positionY;
    }
    @JsonIgnore
    public int getHeight() {
        return height;
    }
    @JsonIgnore
    public int getWidth() {
        return width;
    }
    @JsonIgnore
    public int getSpeedX() {
        return speed_x;
    }
    @JsonIgnore
    public int getSpeedY() {
        return speed_y;
    }
    @JsonIgnore
    public int getHitBoxSize() {
        return hitBoxSize;
    }
    @JsonIgnore
    public Shape getHitbox() {
        return hitbox;
    }
    @JsonIgnore
    public Shape getTrackPoint() {
        return trackPoint;
    }
    @JsonIgnore
    public Shape getAttackRange() {
        return attackRange;
    }
    @JsonIgnore
    public int getAttackRangeSize() {
        return attackRangeSize;
    }
    @JsonIgnore
    public int getHealth() {
        return health;
    }
    @JsonIgnore
    public int getMaxHealth() {
        return maxHealth;
    }
    @JsonIgnore
    public int getDamage() {
        return damage;
    }
    @JsonIgnore
    public int getCooldown() {
        return cooldown;
    }
    @JsonIgnore
    public boolean getCanAttack() {
        return canAttack;
    }
    @JsonIgnore
    public long getWhenAttacked() {
        return whenAttacked;
    }
    @JsonIgnore
    public long getWhenStartedPursuing() {
        return whenStartedPursuing;
    }
    @JsonIgnore
    public ImageView getEntityView() {
        return entityView;
    }
    @JsonIgnore
    public Wagon getCurrentWagon() {
        return currentWagon;
    }
    @JsonIgnore
    public Thread getSoundThread() {
        return soundThread;
    }
    //endregion

    //region Setters
    @JsonIgnore
    public void setIntelligence(Constants.Intelligence intelligence) {
        this.intelligence = intelligence;
    }
    @JsonIgnore
    public void setStartPositionY(double startPositionY) {
        this.startPositionY = startPositionY;
    }
    @JsonIgnore
    public void setCounter(int counter) {
        this.counter = counter;
    }
    @JsonIgnore
    public void setNegativeThreshold(int negativeThreshold) {
        this.negativeThreshold = negativeThreshold;
    }
    @JsonIgnore
    public void setNegativeCount(int negativeCount) {
        logger.info("Entity " + this.getName() + " negative count: " + negativeCount + "/" + negativeThreshold);
        this.negativeCount = negativeCount;
    }
    @JsonIgnore
    public void setTexturePath(String texturePath) {
        this.texturePath = texturePath;
    }
    @JsonIgnore
    public void setDialoguePath(String dialoguePath) {
        this.dialoguePath = dialoguePath;
    }
    @JsonIgnore
    public void setType(Constants.EntityType type) {
        this.type = type;
    }
    @JsonIgnore
    public void setBehaviour(Constants.Behaviour behaviour) {
        this.behaviour = behaviour;
    }
    @JsonIgnore
    public void setStartPositionX(double startPositionX) {
        this.startPositionX = startPositionX;
    }
    @JsonIgnore
    public void setStartIndex(int[] startIndex) {
        this.startIndex = startIndex;
    }
    @JsonIgnore
    public void setPositionX(double positionX) {
        this.positionX = positionX;
    }
    @JsonIgnore
    public void setHeight(int height) {
        this.height = height;
    }
    @JsonIgnore
    public void setWidth(int width) {
        this.width = width;
    }
    @JsonIgnore
    public void setPositionY(double positionY) {
        this.positionY = positionY;
    }
    @JsonIgnore
    public void setSpeedX(int speed_x) {
        this.speed_x = speed_x;
    }
    @JsonIgnore
    public void setSpeedY(int speed_y) {
        this.speed_y = speed_y;
    }
    @JsonSetter("hitBoxSize")
    public void setHitBoxSize(int hitBoxSize) {
        this.hitBoxSize = hitBoxSize;
    }
    @JsonIgnore
    public void setHitbox(Shape hitbox) {
        this.hitbox = hitbox;
    }
    @JsonIgnore
    public void setTrackPoint(Shape trackPoint) {
        this.trackPoint = trackPoint;
    }
    @JsonIgnore
    public void setAttackRange(Shape attackRange) {
        this.attackRange = attackRange;
    }
    @JsonIgnore
    public void setAttackRangeSize(int attackRangeSize) {
        this.attackRangeSize = attackRangeSize;
    }
    @JsonSetter("health")
    public void setHealth(int health) {
        this.health = health;
    }
    @JsonIgnore
    public void setMaxHealth(int maxHealth) {
        this.maxHealth = maxHealth;
    }
    @JsonSetter("damage")
    public void setDamage(int damage) {
        this.damage = damage;
    }
    @JsonIgnore
    public void setCooldown(int cooldown) {
        this.cooldown = cooldown;
    }
    @JsonIgnore
    public void setCanAttack(boolean canAttack) {
        this.canAttack = canAttack;
    }
    @JsonIgnore
    public void setWhenAttacked(long whenAttacked) {
        this.whenAttacked = whenAttacked;
    }
    @JsonIgnore
    public void setWhenStartedPursuing(long whenStartedPursuing) {
        this.whenStartedPursuing = whenStartedPursuing;
    }
    @JsonIgnore
    public void setEntityView(ImageView entityView) {
        this.entityView = entityView;
    }
    @JsonIgnore
    public void setCurrentWagon(Wagon currentWagon) {
        this.currentWagon = currentWagon;
    }
    @JsonIgnore
    public void setSoundThread(Thread soundThread) {
        this.soundThread = soundThread;
    }
    //endregion

    //endregion
}