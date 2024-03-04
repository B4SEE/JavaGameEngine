package cs.cvut.fel.pjv.gamedemo.common_classes;
public class Door {
    public final String name;
    private String twoLetterId;
    public String texturePath;
    private int gridPositionX;
    private int gridPositionY;
    private int targetId;
    private boolean isLocked;

    public Door(String name) {
        this.name = name;
    }
    public Door(String name, String twoLetterId, String texturePath, int gridPositionX, int gridPositionY, int targetId, boolean isLocked) {
        this.name = name;
        this.twoLetterId = twoLetterId;
        this.texturePath = texturePath;
        this.gridPositionX = gridPositionX;
        this.gridPositionY = gridPositionY;
        this.targetId = targetId;
        this.isLocked = isLocked;
    }

    public String getName() {
        return name;
    }

    public String getTwoLetterId() {
        return twoLetterId;
    }

    public void setTwoLetterId(String twoLetterId) {
        this.twoLetterId = twoLetterId;
    }

    public String getTexturePath() {
        return texturePath;
    }

    public void setTexturePath(String texturePath) {
        this.texturePath = texturePath;
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