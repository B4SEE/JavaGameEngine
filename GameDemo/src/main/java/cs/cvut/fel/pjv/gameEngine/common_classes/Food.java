public class Food extends Item {
    public final nourishment;

    public Food(int id, String name, String texturePath) {
        super(id, name, texturePath, "FOOD");
    }

    public Food(int id, String name, String texturePath, int nourishment) {
        super(id, name, texturePath, "FOOD");
        this.nourishment = nourishment;
    }
}