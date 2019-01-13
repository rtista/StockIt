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
     * The item barcode value.
     */
    private String barcode;

    /**
     * The item available units.
     */
    private int available;

    /**
     * The item allocated units.
     */
    private int allocated;

    /**
     * The item alert/minimum quantity.
     */
    private int alert;

    /**
     * The constructor.
     *
     * @param id           The item id.
     * @param name         The item name.
     * @param description  The item description.
     * @param barcode      The item barcode.
     * @param available    The item available units.
     * @param allocated    The item allocated units.
     * @param alert        The item alert/minimum quantity.
     */
    public Item(int id, String name, String description, String barcode,
                int available, int allocated, int alert) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.barcode = barcode;
        this.available = available;
        this.allocated = allocated;
        this.alert = alert;
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

    public String getDescription() {
        return this.description;
    }

    public String getBarcode() {
        return this.barcode == null ? "" : this.barcode;
    }

    public int getAvailable() {
        return this.available;
    }

    public int getAllocated() {
        return this.allocated;
    }

    public int getAlert() {
        return this.alert;
    }
}
