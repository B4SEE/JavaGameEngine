package cs.cvut.fel.pjv.gamedemo.common_classes;

import com.fasterxml.jackson.annotation.*;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 * Object child class representing a door, which functions as a teleport to another wagon.
 */
public class Door extends Object {

    //region Attributes
    @JsonIgnore
    private static final Logger logger = LogManager.getLogger(Door.class);
    @JsonProperty("targetId")
    private int targetId;
    @JsonProperty("teleport")
    private Object teleport;
    @JsonProperty("isLocked")
    private boolean isLocked;
    //endregion

    //region Constructors
    @JsonCreator
    public Door(@JsonProperty("id") int id, @JsonProperty("name") String name, @JsonProperty("texturePath") String texturePath, @JsonProperty("targetId") int targetId, @JsonProperty("teleport") Object teleport, @JsonProperty("isLocked") boolean isLocked) {
        super(id, name, texturePath);
        super.setTwoLetterId(Constants.WAGON_DOOR);
        super.setObjectInventory(null);
        super.setIsSolid(true);
        this.targetId = targetId;
        this.teleport = teleport;
        this.isLocked = isLocked;
    }
    //endregion

    //region Methods
    /**
     * Teleports the entity to the position of the teleport object.
     * @param entity - entity to be teleported
     */
    @JsonIgnore
    public void teleport(Entity entity) {
        logger.info("Teleporting entity " + entity.getName() + " to wagon with ID " + targetId);
        double objectIsoX = teleport.getIsoX();
        double objectIsoY = teleport.getIsoY();
        int height = entity.getHeight();
        entity.setPositionX((int) objectIsoX);
        entity.setPositionY((int) objectIsoY - height * 32);
        logger.debug("Entity " + entity.getName() + " teleported to wagon ID " + targetId + " to " + objectIsoX + ", " + objectIsoY);
    }
    /**
     * Locks the door.
     */
    @JsonIgnore
    public void lock() {
        isLocked = true;
        if (getIsoX() != 0 && getIsoY() != 0) {
            logger.info("Wagon door at " + getIsoX() + ", " + getIsoY() + " locked");
        }
        else {
            logger.info("Wagon door locked");
        }
    }
    /**
     * Unlocks the door.
     */
    @JsonIgnore
    public void unlock() {
        isLocked = false;
        if (getIsoX() != 0 && getIsoY() != 0) {
            logger.info("Wagon door at " + getIsoX() + ", " + getIsoY() + " unlocked");
        }
        else {
            logger.info("Wagon door unlocked");
        }
    }
    //endregion

    //region Getters & Setters

    //region Getters
    @JsonIgnore
    public int getTargetId() {
        return targetId;
    }
    @JsonIgnore
    public Object getTeleport() {
        return teleport;
    }
    @JsonIgnore
    public boolean isLocked() {
        return isLocked;
    }
    //endregion

    //region Setters
    @JsonSetter("targetId")
    public void setTargetId(int targetId) {
        this.targetId = targetId;
    }
    @JsonSetter("teleport")
    public void setTeleport(Object teleport) {
        this.teleport = teleport;
    }
    @JsonSetter("isLocked")
    public void setIsLocked(boolean isLocked) {
        this.isLocked = isLocked;
    }
    //endregion

    //endregion
}