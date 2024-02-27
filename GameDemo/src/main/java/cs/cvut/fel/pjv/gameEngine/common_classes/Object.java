public class Object {
    public final int id;
    public final String name;
    public final String texturePath;
    private int gridPositionX;
    private int gridPositionY;
    private boolean isSolid;
    private Inventory objectInventory;

    public Object(int id, String name, String texturePath) {
        this.id = id;
        this.name = name;
        this.texturePath = texturePath;
    }

    public Object(int id, String name, String texturePath, int gridPositionX, int gridPositionY, boolean isSolid, Inventory objectInventory) {
        this.id = id;
        this.name = name;
        this.texturePath = texturePath;
        this.gridPositionX = gridPositionX;
        this.gridPositionY = gridPositionY;
        this.isSolid = isSolid;
        this.objectInventory = objectInventory;
    }

    public void setGridPositionX(int gridPositionX) {
        this.gridPositionX = gridPositionX;
    }

    public void getGridPositionX() {
        return gridPositionX;
    }

    public void setGridPositionY(int gridPositionY) {
        this.gridPositionY = gridPositionY;
    }

    public void getGridPositionY() {
        return gridPositionY;
    }

    public void setIsSolid(boolean isSolid) {
        this.isSolid = isSolid;
    }

    public void isSolid() {
        return isSolid;
    }

    public void setObjectInventory(Inventory objectInventory) {
        this.objectInventory = objectInventory;
    }

    public void getObjectInventory() {
        return objectInventory;
    }
}