package cs.cvut.fel.pjv.gamedemo.common_classes;

/**
 * Represents food in the game, which can be eaten by the player.
 */
public class Food extends Item {
    public final int nourishment;

    public Food(String name, String texturePath, int nourishment) {
        super(name, texturePath, "FOOD");
        this.nourishment = nourishment;
    }
}