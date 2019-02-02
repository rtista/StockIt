package pt.simov.stockit.item;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import pt.simov.stockit.R;
import pt.simov.stockit.core.domain.Item;

public class ItemListAdapter extends BaseAdapter {
    private final List<Item> items;

    public ItemListAdapter(final List<Item> items) {
        this.items = items;
    }

    public int getCount() {
        return this.items.size();
    }

    public Object getItem(int arg0) {
        return this.items.get(arg0);
    }

    public long getItemId(int arg0) {
        return 0;
    }

    public View getView(int arg0, View arg1, ViewGroup arg2) {
        View itemView = null;
        final Item row = this.items.get(arg0);
        if (arg1 == null) {
            LayoutInflater inflater = (LayoutInflater) arg2.getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            itemView = inflater.inflate(R.layout.inventory_feed_item, null);
        } else {
            itemView = arg1;
        }

        TextView item_title = (TextView) itemView.findViewById(R.id.inventory_item_name);
        item_title.setText(row.getName());

        TextView item_min_temp = (TextView) itemView.findViewById(R.id.inventory_item_description);
        item_min_temp.setText(row.getDescription());

        return itemView;
    }
}