package cs.cvut.fel.pjv.gamedemo.common_classes;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents an item in the game.
 */
public class Item {
    @JsonProperty("name")
    private final String name;
    @JsonProperty("texturePath")
    private final String texturePath;
    @JsonIgnore
    private String type;
    @JsonProperty("value")
    private int value;

    public Item(String name, String texturePath, int value) {
        this.name = name;
        this.texturePath = texturePath;
        this.value = value;
    }

    public Item(String name, String texturePath, String type) {
        this.name = name;
        this.texturePath = texturePath;
        this.type = type;
    }
    @JsonIgnore
    public String getName() {
        return name;
    }

    @JsonIgnore
    public String getTexturePath() {
        return texturePath;
    }

    @JsonIgnore
    public void setType(String type) {
        this.type = type;
    }

    @JsonIgnore
    public String getType() {
        return type;
    }

    @JsonIgnore
    public void setValue(int value) {
        this.value = value;
    }

    @JsonIgnore
    public int getValue() {
        return value;
    }
}