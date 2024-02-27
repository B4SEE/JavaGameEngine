package cs.cvut.fel.pjv.gamedemo.common_classes;
public class Wagon {
    public final int id;
    public final String type;
    public final String texturePath;
    private Door doorLeft;
    private Door doorRight;
    private Entity[] entitiesArray;
    private Object[] objectsArray;

    public Wagon(int id, String type, String texturePath) {
        this.id = id;
        this.type = type;
        this.texturePath = texturePath;
    }

    public Wagon(int id, String type, String texturePath, Door doorLeft, Door doorRight, Entity[] entitiesArray, Object[] objectsArray) {
        this.id = id;
        this.type = type;
        this.texturePath = texturePath;
        this.doorLeft = doorLeft;
        this.doorRight = doorRight;
        this.entitiesArray = entitiesArray;
        this.objectsArray = objectsArray;
    }

    public void setDoorLeft(Door doorLeft) {
        this.doorLeft = doorLeft;
    }

    public void setDoorRight(Door doorRight) {
        this.doorRight = doorRight;
    }

    public void setEntitiesArray(Entity[] entitiesArray) {
        this.entitiesArray = entitiesArray;
    }

    public Entity[] getEntitiesArray() {
        return entitiesArray;
    }

    public void setObjectsArray(Object[] objectsArray) {
        this.objectsArray = objectsArray;
    }

    public Object[] getObjectsArray() {
        return objectsArray;
    }

    public void generateWagon(String seed) {
        // Generate the wagon
    }
}