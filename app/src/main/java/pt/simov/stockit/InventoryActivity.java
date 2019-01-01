package pt.simov.stockit;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class InventoryActivity extends AppCompatActivity {

    ArrayList<InventoryListItem> feed;
    ListView lv;
    InventoryListAdapter iAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);
        setTitle(R.string.title_inventory);

        lv = findViewById(R.id.inventory_list);
        feed = new ArrayList<>();
        feed.add(new InventoryListItem("name","description","quantity","min_quantity"));
        iAdapter = new InventoryListAdapter(feed);
        registerForContextMenu(lv);
        UpdateDisplay();
    }

    public void UpdateDisplay() {
        if (feed == null){ Toast.makeText(
                InventoryActivity.this,
                "No Warehouses Found",
                Toast.LENGTH_SHORT).show(); return; }
        lv.setAdapter(iAdapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(getApplicationContext(), ItemActivity.class);
                i.putExtra("NAME", feed.get(position).get_name());
                i.putExtra("DESCRIPTION", feed.get(position).get_description());
                i.putExtra("QUANTITY", feed.get(position).get_quantity());
                i.putExtra("MIN_QUANTITY", feed.get(position).get_min_quantity());
                i.putExtra("REQUEST_CODE",1);
                startActivity(i);
            }
        });
        lv.setSelection(0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.inventory_optionsmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {// Handle item selection
        switch (item.getItemId()) {
            case R.id.iom_add:
                Intent i = new Intent(this,ItemActivity.class);
                i.putExtra("NAME", "");
                i.putExtra("DESCRIPTION", "");
                i.putExtra("QUANTITY", "");
                i.putExtra("MIN_QUANTITY", "");
                i.putExtra("REQUEST_CODE",3);
                startActivity(i);
                return true;
            case R.id.iom_barcode:
                Intent iBarcode = new Intent(this,BarcodeActivity.class);
                startActivity(iBarcode);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.warehouses_contextmenu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int pos = info.position;
        Intent i;
        switch (item.getItemId()) {
            case R.id.wcm_view:
                Toast.makeText(InventoryActivity.this, "View",Toast.LENGTH_LONG).show();
                i = new Intent(getApplicationContext(), ItemActivity.class);
                i.putExtra("NAME", feed.get(pos).get_name());
                i.putExtra("DESCRIPTION", feed.get(pos).get_description());
                i.putExtra("QUANTITY", feed.get(pos).get_quantity());
                i.putExtra("MIN_QUANTITY", feed.get(pos).get_min_quantity());
                i.putExtra("REQUEST_CODE",1);
                startActivity(i);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }
}
