package pt.simov.stockit;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import pt.simov.stockit.core.ApiHandler;
import pt.simov.stockit.core.http.HttpClient;
import pt.simov.stockit.domain.Warehouse;

public class WarehousesTableActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    /**
     * The StockIt backend API handler.
     */
    private ApiHandler apiHandler = ApiHandler.getInstance();

    /**
     * HTTP Client provides request queue.
     */
    private OkHttpClient client = HttpClient.getInstance();

    /**
     * The list which contains the warehouse entities.
     */
    private ArrayList<WarehouseListItem> feed = new ArrayList<>();

    /**
     * The list view adapter.
     */
    private WarehouseListAdapter wAdapter = new WarehouseListAdapter(this.feed);

    /**
     * The list view which will hold the warehouse items.
     */
    ListView lv;

    /**
     * On creation of the activity.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_warehouses);
        setTitle(R.string.title_warehouses);

        // Text View
        TextView tv = findViewById(R.id.warehouses_title);
        tv.setText("Warehouses of @User");

        // Update display
        this.lv = findViewById(R.id.warehouses_list);

        // Set list view
        this.getWarehouses();

        // Add Context menu
        registerForContextMenu(this.lv);
    }

    /**
     * Inflate the options menu.
     * @param menu Options menu.
     * @return boolean
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.warehouses_optionsmenu, menu);
        return true;
    }

    /**
     * Inflate context menu.
     * @param menu The context menu.
     * @param v The view.
     * @param menuInfo The menu info.
     */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.warehouses_contextmenu, menu);
    }

    /**
     * On Option Menu item selection.
     * @param item The selected item.
     * @return boolean
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle item selection
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

            case R.id.wom_logout:
                i = new Intent(this,LoginActivity.class);
                //TODO Logout operation
                startActivity(i);
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * On context menu item selection.
     * @param item The selected item.
     * @return boolean
     */
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

    /**
     * Update the warehouses list.
     */
    public void updateDisplay() {

        // Update the list view
        this.lv.setAdapter(this.wAdapter);
        this.lv.setOnItemClickListener(this);
        lv.setSelection(0);
    }

    /**
     * Updates the activity's warehouse list.
     * @return boolean
     */
    private void getWarehouses() {

        Request req = this.apiHandler.getWarehouses();

        this.client.newCall(req).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WarehousesTableActivity.this, "Sad life :(", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                switch (response.code()) {

                    // Success on the request
                    case 200:

                        // Read Json Object from response body
                        try {

                            JSONArray warehouses = new JSONObject(response.body().string()).getJSONArray("warehouses");

                            // Empty Array
                            WarehousesTableActivity.this.feed.clear();

                            // Fill array
                            for (int i = 0; i < warehouses.length(); i++) {

                                JSONObject wh = warehouses.getJSONObject(i);
                                WarehousesTableActivity.this.feed.add(
                                        new WarehouseListItem(
                                                wh.getString("name"),
                                                wh.getString("description"),
                                                wh.getString("latitude"),
                                                wh.getString("longitude")
                                        )
                                );
                            }

                            // Update Ui
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    WarehousesTableActivity.this.updateDisplay();
                                }
                            });

                        } catch (JSONException e) {
                            Log.e("WAREHOUSE_LIST", "JSON Exception: " + e.getMessage());
                        }

                        break;

                    // Unauthorized
                    case 401:
                        // TODO: Refresh user token
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(WarehousesTableActivity.this, "Unauthorized", Toast.LENGTH_SHORT).show();
                            }
                        });
                        break;

                    // Bad Request
                    case 400:
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(WarehousesTableActivity.this, "Bad Request", Toast.LENGTH_SHORT).show();
                            }
                        });
                        break;

                    // Internal Server Error
                    case 500:
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(WarehousesTableActivity.this, "Internal Server Error", Toast.LENGTH_SHORT).show();
                            }
                        });
                        break;
                }
            }
        });
    }

    /**
     * On warehouse list view item click.
     * @param parent The parent view.
     * @param view The view.
     * @param position The position of the item.
     * @param id The id of the item.
     */
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
}
