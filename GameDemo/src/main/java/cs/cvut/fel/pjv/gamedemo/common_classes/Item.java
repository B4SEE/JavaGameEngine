package cs.cvut.fel.pjv.gamedemo.common_classes;

import com.fasterxml.jackson.annotation.*;

/**
 * Represents an item in the game.
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "class"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = Food.class, name = "Food"),
        @JsonSubTypes.Type(value = MeleeWeapon.class, name = "MeleeWeapon"),
        @JsonSubTypes.Type(value = Firearm.class, name = "Firearm")
})
public class Item {

    //region Attributes
    @JsonProperty("name")
    private final String name;
    @JsonProperty("texturePath")
    private final String texturePath;
    @JsonIgnore
    private Constants.ItemType type;
    @JsonProperty("value")
    private int value;
    //endregion

    //region Constructors
    @JsonCreator
    public Item(@JsonProperty("name") String name, @JsonProperty("texturePath") String texturePath, @JsonProperty("value") int value) {
        this.name = name;
        this.texturePath = texturePath;
        this.value = value;
    }
    public Item(String name, String texturePath, Constants.ItemType type) {
        this.name = name;
        this.texturePath = texturePath;
        this.type = type;
    }
    //endregion

    //region Getters & Setters

    //region Getters
    @JsonIgnore
    public String getName() {
        return name;
    }
    @JsonIgnore
    public String getTexturePath() {
        return texturePath;
    }
    @JsonIgnore
    public Constants.ItemType getType() {
        return type;
    }
    @JsonIgnore
    public int getValue() {
        return value;
    }
    //endregion

    //region Setters
    @JsonIgnore
    public void setType(Constants.ItemType type) {
        this.type = type;
    }
    @JsonIgnore
    public void setValue(int value) {
        this.value = value;
    }
    //endregion

    //endregion
}