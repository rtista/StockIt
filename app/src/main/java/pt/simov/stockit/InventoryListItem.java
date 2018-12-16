package pt.simov.stockit;

public class InventoryListItem {

    private String _name = null;
    private String _description = null;
    private String _quantity = null;
    private String _min_quantity = null;

    InventoryListItem(){}

    InventoryListItem(String name, String description, String quantity, String min_quantity){
        _name = name;
        _description = description;
        _quantity = quantity;
        _min_quantity = min_quantity;
    }


    public String get_name() {
        return _name;
    }

    public void set_name(String _name) {
        this._name = _name;
    }

    public String get_description() {
        return _description;
    }

    public void set_description(String _description) {
        this._description = _description;
    }

    public String get_quantity() {
        return _quantity;
    }

    public void set_quantity(String _quantity) {
        this._quantity = _quantity;
    }

    public String get_min_quantity() {
        return _min_quantity;
    }

    public void set_min_quantity(String _min_quantity) {
        this._min_quantity = _min_quantity;
    }
}
