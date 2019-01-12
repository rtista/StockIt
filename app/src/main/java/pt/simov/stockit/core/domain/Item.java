package pt.simov.stockit.core.domain;

public class Item {

    /**
     * The item id.
     */
    private int id;

    /**
     * The item name.
     */
    private String name;

    /**
     * The item description.
     */
    private String description;

    /**
     * The item quantity.
     */
    private int quantity;

    /**
     * The item barcode value.
     */
    private String barcode;

    /**
     * The item alert/minimum quantity.
     */
    private int min_quantity;

    /**
     * The constructor.
     *
     * @param id           The item id.
     * @param name         The item name.
     * @param description  The item description.
     * @param quantity     The item quantity.
     * @param barcode      The item barcode.
     * @param min_quantity The item alert/minimum quantity.
     */
    public Item(int id, String name, String description, int quantity, String barcode, int min_quantity) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.quantity = quantity;
        this.barcode = barcode;
        this.min_quantity = min_quantity;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getQuantity() {
        return this.quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getBarcode() {
        return this.barcode == null ? "" : this.barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public int getMin_quantity() {
        return this.min_quantity;
    }

    public void setMin_quantity(int min_quantity) {
        this.min_quantity = min_quantity;
    }
}
