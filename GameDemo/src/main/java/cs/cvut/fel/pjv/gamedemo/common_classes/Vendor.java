package cs.cvut.fel.pjv.gamedemo.common_classes;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import cs.cvut.fel.pjv.gamedemo.engine.EntitiesCreator;
import cs.cvut.fel.pjv.gamedemo.engine.Events;
import cs.cvut.fel.pjv.gamedemo.engine.utils.RandomHandler;
public class Vendor extends Entity {

    //region Attributes
    @JsonProperty("vendorInventory")
    private Inventory vendorInventory;
    //endregion

    //region Constructors
    public Vendor(String name, String texturePath) {
        super(name, texturePath);
        setAsDefaultVendor();
    }
    @JsonCreator
    public Vendor(@JsonProperty("name") String name, @JsonProperty("texturePath") String texturePath, @JsonProperty("inventorySize") int inventorySize) {
        super(name, texturePath);
        setAsDefaultVendor();
        this.vendorInventory = new Inventory(inventorySize);
        vendorInventory.setVendor(true);
        vendorInventory.setInventoryLabel(this.getName());
    }
    //endregion

    //region Methods
    @JsonIgnore
    public void setAsDefaultVendor() {
        initVendor();
        initInventory();
        fillInventory();
    }
    @JsonIgnore
    private void initVendor() {
        EntitiesCreator.setAsDefaultNPC(this);
        super.setType(Constants.EntityType.VENDOR);
    }
    @JsonIgnore
    private void initInventory() {
        if (vendorInventory == null) {
            int randomInventorySize = (int) (Math.random() * 20 + 1);
            this.vendorInventory = new Inventory(randomInventorySize);
            vendorInventory.setVendor(true);
            vendorInventory.setInventoryLabel(this.getName());
        }
    }
    @JsonIgnore
    private void fillInventory() {
        boolean original = Events.canSpawnTicket();
        Events.setCanSpawnTicket(true);
        RandomHandler.fillInventoryWithRandomItems(vendorInventory);
        Events.setCanSpawnTicket(original);
    }
    //endregion

    //region Getters & Setters

    //region Getters
    @JsonIgnore
    public Inventory getVendorInventory() {
        return vendorInventory;
    }
    //endregion

    //region Setters
    @JsonSetter("vendorInventory")
    public void setVendorInventory(Inventory vendorInventory) {
        this.vendorInventory = vendorInventory;
    }
    //endregion

    //endregion
}
