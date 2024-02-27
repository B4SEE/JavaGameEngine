package cs.cvut.fel.pjv.gamedemo.common_classes;
public class Food extends Item {
    public final int nourishment;

    public Food(int id, String name, String texturePath, int nourishment) {
        super(id, name, texturePath, "FOOD");
        this.nourishment = nourishment;
    }
}