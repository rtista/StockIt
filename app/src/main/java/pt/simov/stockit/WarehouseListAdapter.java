package pt.simov.stockit;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class WarehouseListAdapter extends BaseAdapter {
    private final List<WarehouseListItem> items;

    public WarehouseListAdapter(final List<WarehouseListItem> items) {
        this.items = items;
    }

    public int getCount() { return this.items.size(); }

    public Object getItem(int arg0) { return this.items.get(arg0); }

    public long getItemId(int arg0) { return 0;	}

    public View getView(int arg0, View arg1, ViewGroup arg2) {
        View itemView = null;
        final WarehouseListItem row = this.items.get(arg0);
        if (arg1 == null) {
            LayoutInflater inflater = (LayoutInflater) arg2.getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            itemView = inflater.inflate(R.layout.warehouse_feed_item, null);
        } else {
            itemView = arg1;
        }

        TextView item_title = (TextView) itemView.findViewById(R.id.warehouse_item_name);
        item_title.setText(row.get_name());

        TextView item_min_temp = (TextView) itemView.findViewById(R.id.warehouse_item_description);
        item_min_temp.setText(row.get_description());

        TextView item_lat = (TextView) itemView.findViewById(R.id.warehouse_item_lat);
        item_lat.setText(row.get_lat());

        TextView item_long = (TextView) itemView.findViewById(R.id.warehouse_item_long);
        item_long.setText(row.get_long());

        return itemView;
    }

}