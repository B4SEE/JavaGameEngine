package cs.cvut.fel.pjv.gamedemo.common_classes;
public class Door extends Object {
    private int targetId;
    private Object teleport;
    private boolean isLocked;
    public Door(int id, String name, String texturePath, int targetId, Object teleport, boolean isLocked) {
        super(id, name, texturePath);
        super.setTwoLetterId(Constants.WAGON_DOOR);
        super.setObjectInventory(null);
        super.setIsSolid(true);
        this.targetId = targetId;
        this.teleport = teleport;
        this.isLocked = isLocked;
    }

    public int getTargetId() {
        return targetId;
    }

    public void setTargetId(int targetId) {
        this.targetId = targetId;
    }

    public Object getTeleport() {
        return teleport;
    }

    public void setTeleport(Object teleport) {
        this.teleport = teleport;
    }

    public void teleport(Entity entity) {
        double objectIsoX = teleport.getIsoX();
        double objectIsoY = teleport.getIsoY();
        int height = entity.getHeight();
        System.out.println("Teleporting to " + objectIsoX + " " + objectIsoY);
        entity.setPositionX((int) objectIsoX);
        entity.setPositionY((int) objectIsoY - height * 32);
        System.out.println("Teleporting to " + objectIsoX + " " + objectIsoY);
    }

    public void lock() {
        isLocked = true;
    }

    public void unlock() {
        isLocked = false;
    }

    public boolean isLocked() {
        return isLocked;
    }
}