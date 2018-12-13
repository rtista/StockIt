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
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class WarehousesTableActivity extends AppCompatActivity {

    ArrayList<WarehouseListItem> feed;
    ListView lv;
    WarehouseListAdapter wAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_warehouses);
        lv = findViewById(R.id.warehouses_list);
        TextView tv = findViewById(R.id.warehouses_title);
        tv.setText("Warehouses of @User");
        feed = new ArrayList<>();
        feed.add(new WarehouseListItem("name","description","latitude","longitude"));
        wAdapter = new WarehouseListAdapter(feed);
        registerForContextMenu(lv);
        UpdateDisplay();
    }

    public void UpdateDisplay() {
        if (feed == null){ Toast.makeText(
                WarehousesTableActivity.this,
                "No Warehouses Found",
                Toast.LENGTH_SHORT).show(); return; }
        lv.setAdapter(wAdapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(getApplicationContext(), InventoryActivity.class);
                i.putExtra("NAME", feed.get(position).get_name());
                i.putExtra("DESCRIPTION", feed.get(position).get_description());
                i.putExtra("LATITUDE", feed.get(position).get_lat());
                i.putExtra("LONGITUDE", feed.get(position).get_long());
                i.putExtra("REQUEST_CODE",1);
                startActivity(i);
            }
        });
        lv.setSelection(0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.warehouses_optionsmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {// Handle item selection
        switch (item.getItemId()) {
            case R.id.wom_add:
                Intent i=new Intent(this,WarehouseActivity.class);
                i.putExtra("NAME", "");
                i.putExtra("DESCRIPTION", "");
                i.putExtra("LATITUDE", "");
                i.putExtra("LONGITUDE", "");
                i.putExtra("REQUEST_CODE",3);
                startActivity(i);
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
        WarehouseListItem p = (WarehouseListItem) wAdapter.getItem(pos);
        switch (item.getItemId()) {
            case R.id.wcm_view:
                Toast.makeText(WarehousesTableActivity.this, "View",Toast.LENGTH_LONG).show();
                i = new Intent(getApplicationContext(), WarehouseActivity.class);
                i.putExtra("NAME", feed.get(pos).get_name());
                i.putExtra("DESCRIPTION", feed.get(pos).get_description());
                i.putExtra("LATITUDE", feed.get(pos).get_lat());
                i.putExtra("LONGITUDE", feed.get(pos).get_long());
                i.putExtra("REQUEST_CODE",1);
                startActivity(i);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }
}
