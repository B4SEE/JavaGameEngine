public class Item {
    public final int id;
    public final String name;
    public final String texturePath;
    private int String type;

    public Item(int id, String name, String texturePath) {
        this.id = id;
        this.name = name;
        this.texturePath = texturePath;
    }

    public Item(int id, String name, String texturePath, String type) {
        this.id = id;
        this.name = name;
        this.texturePath = texturePath;
        this.type = type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}