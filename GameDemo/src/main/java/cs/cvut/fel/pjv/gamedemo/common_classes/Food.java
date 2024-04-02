package cs.cvut.fel.pjv.gamedemo.common_classes;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents food in the game, which can be eaten by the player.
 */
public class Food extends Item {
    @JsonProperty("nourishment")
    public final int nourishment;

    public Food(String name, String texturePath, int nourishment) {
        super(name, texturePath, Constants.ItemType.FOOD);
        this.nourishment = nourishment;
    }
}