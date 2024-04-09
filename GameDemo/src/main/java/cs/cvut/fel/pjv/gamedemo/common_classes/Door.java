package cs.cvut.fel.pjv.gamedemo.common_classes;

import com.fasterxml.jackson.annotation.*;

/**
 * Object child class representing a door, which functions as a teleport to another wagon.
 */
public class Door extends Object {
    @JsonProperty("targetId")
    private int targetId;
    @JsonProperty("teleport")
    private Object teleport;
    @JsonProperty("isLocked")
    private boolean isLocked;
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
    @JsonIgnore
    public int getTargetId() {
        return targetId;
    }
    @JsonSetter("targetId")
    public void setTargetId(int targetId) {
        this.targetId = targetId;
    }
    @JsonIgnore
    public Object getTeleport() {
        return teleport;
    }
    @JsonSetter("teleport")
    public void setTeleport(Object teleport) {
        this.teleport = teleport;
    }

    /**
     * Teleports the entity to the position of the teleport object.
     * @param entity - entity to be teleported
     */
    @JsonIgnore
    public void teleport(Entity entity) {
        double objectIsoX = teleport.getIsoX();
        double objectIsoY = teleport.getIsoY();
        int height = entity.getHeight();
        entity.setPositionX((int) objectIsoX);
        entity.setPositionY((int) objectIsoY - height * 32);
        System.out.println("Teleporting to " + objectIsoX + " " + objectIsoY);
    }
    /**
     * Locks the door.
     */
    @JsonIgnore
    public void lock() {
        isLocked = true;
    }
    /**
     * Unlocks the door.
     */
    @JsonIgnore
    public void unlock() {
        isLocked = false;
    }
    @JsonIgnore
    public boolean isLocked() {
        return isLocked;
    }
    @JsonSetter("isLocked")
    public void setIsLocked(boolean isLocked) {
        this.isLocked = isLocked;
    }
}