package cs.cvut.fel.pjv.gamedemo.common_classes;
public class Item {
    private final String name;
    private final String texturePath;
    private String type;

    public Item(String name, String texturePath) {
        this.name = name;
        this.texturePath = texturePath;
    }

    public Item(String name, String texturePath, String type) {
        this.name = name;
        this.texturePath = texturePath;
        this.type = type;
    }
    public String getName() {
        return name;
    }

    public String getTexturePath() {
        return texturePath;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}