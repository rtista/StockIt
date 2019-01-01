package pt.simov.stockit;

public class WarehouseListItem {
    private int _id;
        private String _name = null;
        private String _description = null;
        private String _lat = null;
        private String _long= null;

    WarehouseListItem(int id, String name, String description, String lat, String lon) {
        _id = id;
        _name = name;
        _description = description;
        _lat = lat;
        _long = lon;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
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

    public String get_lat() {
        return _lat;
    }

    public void set_lat(String _lat) {
        this._lat = _lat;
    }

    public String get_long() {
        return _long;
    }

    public void set_long(String _long) {
        this._long = _long;
    }

    public String toString(){
        return _name + ";" + _description + ";" + _lat + ";" + _long + ";";
    }

}
