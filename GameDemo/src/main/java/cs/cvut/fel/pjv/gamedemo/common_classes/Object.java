package cs.cvut.fel.pjv.gamedemo.common_classes;

import javafx.scene.image.ImageView;
import javafx.scene.shape.Polygon;

/**
 * Represents an object in the game.
 */
public class Object {
    private final int id;

    private final String name;
    private String texturePath;
    private ImageView texture;
    private String twoLetterId;
    private int height;
    private int cartX;
    private int cartY;
    private double isoX;
    private double isoY;
    private int gridPositionX;
    private int gridPositionY;
    private boolean isSolid;
    private Inventory objectInventory;

    private Polygon objectHitbox;

    public Object(int id, String name, String texturePath) {
        this.id = id;
        this.name = name;
        this.texturePath = texturePath;
    }

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

    public Object(int id, String name, String texturePath, int gridPositionX, int gridPositionY, boolean isSolid, Inventory objectInventory) {
        this.id = id;
        this.name = name;
        this.texturePath = texturePath;
        this.gridPositionX = gridPositionX;
        this.gridPositionY = gridPositionY;
        this.isSolid = isSolid;
        this.objectInventory = objectInventory;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setTexturePath(String texturePath) {
        this.texturePath = texturePath;
    }
    public String getTexturePath() {
        return texturePath;
    }

    public void setTexture(ImageView texture) {
        this.texture = texture;
    }

    public ImageView getTexture() {
        return texture;
    }

    public void setTwoLetterId(String twoLetterId) {
        this.twoLetterId = twoLetterId;
    }

    public String getTwoLetterId() {
        return twoLetterId;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getHeight() {
        return height;
    }

    public void setCartX(int cartX) {
        this.cartX = cartX;
    }

    public int getCartX() {
        return cartX;
    }

    public void setCartY(int cartY) {
        this.cartY = cartY;
    }

    public void setIsoX(double isoX) {
        this.isoX = isoX;
    }

    public double getIsoX() {
        return isoX;
    }

    public void setIsoY(double isoY) {
        this.isoY = isoY;
    }

    public double getIsoY() {
        return isoY;
    }

    public int getCartY() {
        return cartY;
    }

    public void setGridPositionX(int gridPositionX) {
        this.gridPositionX = gridPositionX;
    }

    public int getGridPositionX() {
        return gridPositionX;
    }

    public void setGridPositionY(int gridPositionY) {
        this.gridPositionY = gridPositionY;
    }

    public int getGridPositionY() {
        return gridPositionY;
    }

    public void setIsSolid(boolean isSolid) {
        this.isSolid = isSolid;
    }

    public boolean isSolid() {
        return isSolid;
    }

    public void setObjectInventory(Inventory objectInventory) {
        this.objectInventory = objectInventory;
    }

    public Inventory getObjectInventory() {
        return objectInventory;
    }

    public void setObjectHitbox(Polygon objectHitbox) {
        this.objectHitbox = objectHitbox;
    }

    public Polygon getObjectHitbox() {
        return objectHitbox;
    }
}