package cs.cvut.fel.pjv.gamedemo.common_classes;
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

    public int getGridPositionX() {
        return gridPositionX;
    }

    public void setGridPositionY(int gridPositionY) {
        this.gridPositionY = gridPositionY;
    }

    public int getGridPositionY() {
        return gridPositionY;
    }

    public void setIsSolid(boolean isSolid) {
        this.isSolid = isSolid;
    }

    public boolean isSolid() {
        return isSolid;
    }

    public void setObjectInventory(Inventory objectInventory) {
        this.objectInventory = objectInventory;
    }

    public Inventory getObjectInventory() {
        return objectInventory;
    }
}