package cs.cvut.fel.pjv.gamedemo.common_classes;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import cs.cvut.fel.pjv.gamedemo.engine.EntitiesCreator;
import cs.cvut.fel.pjv.gamedemo.engine.RandomHandler;
public class Vendor extends Entity {//fix json
    @JsonProperty("vendorInventory")
    private Inventory vendorInventory;
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
    @JsonIgnore
    public Inventory getVendorInventory() {
        return vendorInventory;
    }
    @JsonIgnore
    public void setVendorInventory(Inventory vendorInventory) {
        this.vendorInventory = vendorInventory;
    }
    @JsonIgnore
    public void setAsDefaultVendor() {
        EntitiesCreator.setAsDefaultNPC(this);
        super.setType(Constants.EntityType.VENDOR);
        if (vendorInventory == null) {
            int randomInventorySize = (int) (Math.random() * 20 + 1);
            this.vendorInventory = new Inventory(randomInventorySize);
            vendorInventory.setVendor(true);
            vendorInventory.setInventoryLabel(this.getName());
        }
        RandomHandler.fillInventoryWithRandomItems(vendorInventory);
    }
}
