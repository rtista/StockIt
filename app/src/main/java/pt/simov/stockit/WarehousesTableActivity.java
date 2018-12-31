package pt.simov.stockit;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
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

            // On Add warehouse option
            case R.id.wom_add:

                // Start activity for result
                Intent i = new Intent(this, WarehouseActivity.class);

                i.putExtra("REQUEST_CODE", WarehouseActivity.REQUEST_CODE_ADD);
                startActivityForResult(i, WarehouseActivity.REQUEST_CODE_ADD);
                break;

            case R.id.wom_logout:
                i = new Intent(this, LoginActivity.class);
                //TODO Logout operation
                startActivity(i);
                finish();
                break;

            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }

    /**
     * On context menu item selection.
     *
     * @param item The selected item.
     * @return boolean
     */
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        final int pos = info.position;

        Intent i;

        // Get item id
        switch (item.getItemId()) {

            // On edit option
            case R.id.wcm_view:

                i = new Intent(getApplicationContext(), WarehouseActivity.class);

                i.putExtra("NAME", feed.get(pos).get_name());
                i.putExtra("DESCRIPTION", feed.get(pos).get_description());
                i.putExtra("LATITUDE", feed.get(pos).get_lat());
                i.putExtra("LONGITUDE", feed.get(pos).get_long());

                i.putExtra("REQUEST_CODE", WarehouseActivity.REQUEST_CODE_VIEW);
                startActivityForResult(i, WarehouseActivity.REQUEST_CODE_VIEW);
                break;

            // On edit option
            case R.id.wcm_edit:

                i = new Intent(getApplicationContext(), WarehouseActivity.class);

                i.putExtra("WAREHOUSE_ID", feed.get(pos).get_id());
                i.putExtra("NAME", feed.get(pos).get_name());
                i.putExtra("DESCRIPTION", feed.get(pos).get_description());
                i.putExtra("LATITUDE", feed.get(pos).get_lat());
                i.putExtra("LONGITUDE", feed.get(pos).get_long());

                i.putExtra("REQUEST_CODE", WarehouseActivity.REQUEST_CODE_EDIT);
                startActivityForResult(i, WarehouseActivity.REQUEST_CODE_EDIT);
                break;

            // On delete option
            case R.id.wcm_delete:

                new AlertDialog.Builder(this)
                        .setTitle("Delete Warehouse")
                        .setMessage("Do you want to delete this warehouse?")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {

                                WarehousesTableActivity.this.deleteWarehouse(feed.get(pos).get_id());
                            }
                        })
                        .setNegativeButton(android.R.string.no, null).show();
                break;

            default:
                return super.onContextItemSelected(item);
        }

        return true;
    }

    /**
     * On warehouse list view item click go into list of items.
     *
     * @param parent   The parent view.
     * @param view     The view.
     * @param position The position of the item.
     * @param id       The id of the item.
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Intent i = new Intent(getApplicationContext(), InventoryActivity.class);

        i.putExtra("WAREHOUSE_ID", feed.get(position).get_id());

        startActivity(i);
    }

    /**
     * On activity finish.
     *
     * @param requestCode The activity request code.
     * @param resultCode  The activity result code.
     * @param data        The intent.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        switch (requestCode) {

            // Added warehouse
            case WarehouseActivity.REQUEST_CODE_ADD:

                // If success
                if (resultCode == WarehouseActivity.RESULT_CODE_SUCCESS) {

                    // Refresh warehouses list
                    this.getWarehouses();
                    Toast.makeText(this, "Warehouse created successfully.", Toast.LENGTH_SHORT).show();
                }

                break;

            // Edited warehouse
            case WarehouseActivity.REQUEST_CODE_EDIT:

                // If success
                if (resultCode == WarehouseActivity.RESULT_CODE_SUCCESS) {

                    // Refresh warehouses list
                    this.getWarehouses();
                    Toast.makeText(this, "Warehouse edited successfully.", Toast.LENGTH_SHORT).show();
                }

            default:
                break;
        }
    }

    /**
     * Update the warehouses list view.
     */
    public void updateDisplay() {

        // Update the list view
        this.lv.setAdapter(this.wAdapter);
        this.lv.setOnItemClickListener(this);
        lv.setSelection(0);
    }

    /**
     * Updates the activity's warehouse list.
     *
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
                                                wh.getInt("id"),
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
     * Deletes a warehouse.
     *
     * @param id The warehouse id.
     */
    private void deleteWarehouse(int id) {

        Request req = this.apiHandler.deleteWarehouse(id);

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

                        // Update Ui
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(WarehousesTableActivity.this, "Warehouse deleted", Toast.LENGTH_SHORT).show();
                                WarehousesTableActivity.this.getWarehouses();
                            }
                        });

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

                    // Not Found
                    case 404:

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(WarehousesTableActivity.this, "Warehouse Not Found", Toast.LENGTH_SHORT).show();
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
}
