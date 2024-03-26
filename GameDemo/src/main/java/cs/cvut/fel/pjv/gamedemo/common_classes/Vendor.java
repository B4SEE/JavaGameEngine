package cs.cvut.fel.pjv.gamedemo.common_classes;

public class Vendor extends Entity {
    private Inventory vendorInventory;

    public Vendor(String name, String texturePath, int inventorySize) {
        super(name, texturePath);
        super.setAsDefaultNPC();
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
}
