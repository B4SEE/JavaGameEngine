package cs.cvut.fel.pjv.gamedemo.common_classes;

import com.fasterxml.jackson.annotation.*;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Polygon;

/**
 * Represents an object in the game.
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "class"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = Door.class, name = "Door")
})
public class Object {
    @JsonProperty("id")
    private final int id;
    @JsonProperty("name")
    private final String name;
    @JsonProperty("texturePath")
    private String texturePath;
    @JsonIgnore
    private ImageView texture;
    @JsonProperty("twoLetterId")
    private String twoLetterId;
    @JsonProperty("height")
    private int height;
    @JsonIgnore
    private int cartX;
    @JsonIgnore
    private int cartY;
    @JsonProperty("isoX")
    private double isoX;
    @JsonProperty("isoY")
    private double isoY;
    @JsonProperty("isSolid")
    private boolean isSolid;
    @JsonProperty("objectInventory")
    private Inventory objectInventory;
    @JsonIgnore
    private Polygon objectHitbox;
    public Object(int id, String name, String texturePath, String twoLetterId, int height, int cartX, int cartY, boolean isSolid) {
        this.id = id;
        this.name = name;
        this.texturePath = texturePath;
        this.twoLetterId = twoLetterId;
        this.height = height;
        this.cartX = cartX;
        this.cartY = cartY;
        this.isSolid = isSolid;
    }
    @JsonCreator
    public Object(@JsonProperty("id") int id, @JsonProperty("name") String name, @JsonProperty("texturePath") String texturePath) {
        this.id = id;
        this.name = name;
        this.texturePath = texturePath;
    }
    @JsonIgnore
    public int getId() {
        return id;
    }
    @JsonIgnore
    public String getName() {
        return name;
    }
    @JsonIgnore
    public void setTexturePath(String texturePath) {
        this.texturePath = texturePath;
    }
    @JsonIgnore
    public String getTexturePath() {
        return texturePath;
    }
    @JsonIgnore
    public void setTexture(ImageView texture) {
        this.texture = texture;
    }
    @JsonIgnore
    public ImageView getTexture() {
        return texture;
    }
    @JsonSetter("twoLetterId")
    public void setTwoLetterId(String twoLetterId) {
        this.twoLetterId = twoLetterId;
    }
    @JsonIgnore
    public String getTwoLetterId() {
        return twoLetterId;
    }
    @JsonSetter("height")
    public void setHeight(int height) {
        this.height = height;
    }
    @JsonIgnore
    public int getHeight() {
        return height;
    }
    @JsonIgnore
    public void setCartX(int cartX) {
        this.cartX = cartX;
    }
    @JsonIgnore
    public int getCartX() {
        return cartX;
    }
    @JsonIgnore
    public void setCartY(int cartY) {
        this.cartY = cartY;
    }
    @JsonSetter("isoX")
    public void setIsoX(double isoX) {
        this.isoX = isoX;
    }
    @JsonIgnore
    public double getIsoX() {
        return isoX;
    }
    @JsonSetter("isoY")
    public void setIsoY(double isoY) {
        this.isoY = isoY;
    }
    @JsonIgnore
    public double getIsoY() {
        return isoY;
    }
    @JsonIgnore
    public int getCartY() {
        return cartY;
    }
    @JsonSetter("isSolid")
    public void setIsSolid(boolean isSolid) {
        this.isSolid = isSolid;
    }
    @JsonIgnore
    public boolean isSolid() {
        return isSolid;
    }
    @JsonSetter("objectInventory")
    public void setObjectInventory(Inventory objectInventory) {
        this.objectInventory = objectInventory;
    }
    @JsonIgnore
    public Inventory getObjectInventory() {
        return objectInventory;
    }
    @JsonIgnore
    public void setObjectHitbox(Polygon objectHitbox) {
        this.objectHitbox = objectHitbox;
    }
    @JsonIgnore
    public Polygon getObjectHitbox() {
        return objectHitbox;
    }
}