package cs.cvut.fel.pjv.gamedemo.common_classes;
public class Door extends Object {
    private int[] targetId_teleportX_teleportY;
    private boolean isLocked;
    public Door(int id, String name, String texturePath, int[] targetId_teleportX_teleportY, boolean isLocked) {
        super(id, name, texturePath);
        super.setObjectInventory(null);
        this.targetId_teleportX_teleportY = targetId_teleportX_teleportY;
        this.isLocked = isLocked;
    }

    public int[] getTargetId_teleportX_teleportY() {
        return targetId_teleportX_teleportY;
    }

    public void setTargetId_teleportX_teleportY(int[] targetId_teleportX_teleportY) {
        this.targetId_teleportX_teleportY = targetId_teleportX_teleportY;
    }

    public void teleport(Entity entity) {
        //teleport entity to targetId
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