package cs.cvut.fel.pjv.gamedemo.common_classes;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents food in the game, which can be eaten by the player.
 */
public class Food extends Item {

    //region Attributes
    @JsonProperty("nourishment")
    private final int nourishment;
    //endregion

    //region Constructors
    @JsonCreator
    public Food(@JsonProperty("name") String name, @JsonProperty("texturePath") String texturePath, @JsonProperty("nourishment") int nourishment) {
        super(name, texturePath, Constants.ItemType.FOOD);
        this.nourishment = nourishment;
    }
    //endregion

    //region Getters & Setters

    //region Getters
    public int getNourishment() {
        return nourishment;
    }
    //endregion

    //endregion





}