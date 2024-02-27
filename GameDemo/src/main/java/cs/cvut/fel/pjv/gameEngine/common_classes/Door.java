public class Door {
    private int gridPositionX;
    private int gridPositionY;
    private int targetId;
    private boolean isLocked;

    public Door() {
    }

    public Door(int gridPositionX, int gridPositionY, int targetId, boolean isLocked) {
        this.gridPositionX = gridPositionX;
        this.gridPositionY = gridPositionY;
        this.targetId = targetId;
        this.isLocked = isLocked;
    }

    public void setGridPositionX(int gridPositionX) {
        this.gridPositionX = gridPositionX;
    }

    public int getGridPositionX() {
        return gridPositionX;
    }

    public void setGridPositionY(int gridPositionY) {
        this.gridPositionY = gridPositionY;
    }

    public int getGridPositionY() {
        return gridPositionY;
    }

    public void setTargetId(int targetId) {
        this.targetId = targetId;
    }

    public int getTargetId() {
        return targetId;
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