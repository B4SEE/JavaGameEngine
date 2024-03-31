package cs.cvut.fel.pjv.gamedemo.common_classes;

import cs.cvut.fel.pjv.gamedemo.engine.RandomHandler;

public class Vendor extends Entity {
    private Inventory vendorInventory;


    public Vendor(String name, String texturePath) {
        super(name, texturePath);
        setAsDefaultVendor();
    }
    public Vendor(String name, String texturePath, int inventorySize) {
        super(name, texturePath);
        setAsDefaultVendor();
        this.vendorInventory = new Inventory(inventorySize);
        vendorInventory.setVendor(true);
        vendorInventory.setInventoryLabel(this.getName());
    }
    public Inventory getVendorInventory() {
        return vendorInventory;
    }

    public void setVendorInventory(Inventory vendorInventory) {
        this.vendorInventory = vendorInventory;
    }
    public void setAsDefaultVendor() {
        super.setAsDefaultNPC();
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
