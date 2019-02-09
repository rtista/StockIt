package pt.simov.stockit.warehouse;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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

import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import pt.simov.stockit.BuildConfig;
import pt.simov.stockit.LoginActivity;
import pt.simov.stockit.R;
import pt.simov.stockit.core.ApiHandler;
import pt.simov.stockit.core.domain.Warehouse;
import pt.simov.stockit.core.http.HttpClient;
import pt.simov.stockit.core.http.StockItCallback;
import pt.simov.stockit.item.ItemListActivity;

public class WarehouseListActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

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
    private ArrayList<Warehouse> feed = new ArrayList<>();

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
        setContentView(R.layout.activity_warehouse_list);
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
                Intent i = new Intent(this, WarehouseCrudActivity.class);

                i.putExtra("REQUEST_CODE", WarehouseCrudActivity.REQUEST_CODE_ADD);
                startActivityForResult(i, WarehouseCrudActivity.REQUEST_CODE_ADD);
                break;

            case R.id.wom_logout:

                // Delete user token
                this.logout();

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

                i = new Intent(getApplicationContext(), WarehouseCrudActivity.class);

                i.putExtra("NAME", feed.get(pos).get_name());
                i.putExtra("DESCRIPTION", feed.get(pos).get_description());
                i.putExtra("LATITUDE", feed.get(pos).get_lat());
                i.putExtra("LONGITUDE", feed.get(pos).get_long());

                i.putExtra("REQUEST_CODE", WarehouseCrudActivity.REQUEST_CODE_VIEW);
                startActivityForResult(i, WarehouseCrudActivity.REQUEST_CODE_VIEW);
                break;

            // On edit option
            case R.id.wcm_edit:

                i = new Intent(getApplicationContext(), WarehouseCrudActivity.class);

                i.putExtra("WAREHOUSE_ID", feed.get(pos).get_id());
                i.putExtra("NAME", feed.get(pos).get_name());
                i.putExtra("DESCRIPTION", feed.get(pos).get_description());
                i.putExtra("LATITUDE", feed.get(pos).get_lat());
                i.putExtra("LONGITUDE", feed.get(pos).get_long());

                i.putExtra("REQUEST_CODE", WarehouseCrudActivity.REQUEST_CODE_EDIT);
                startActivityForResult(i, WarehouseCrudActivity.REQUEST_CODE_EDIT);
                break;

            // On delete option
            case R.id.wcm_delete:

                new AlertDialog.Builder(this)
                        .setTitle("Delete Warehouse")
                        .setMessage("Do you want to delete this warehouse?")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {

                                WarehouseListActivity.this.deleteWarehouse(feed.get(pos).get_id());
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

        Intent i = new Intent(getApplicationContext(), ItemListActivity.class);

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
            case WarehouseCrudActivity.REQUEST_CODE_ADD:

                // If success
                if (resultCode == WarehouseCrudActivity.RESULT_CODE_SUCCESS) {

                    // Refresh warehouses list
                    this.getWarehouses();
                    Toast.makeText(this, "Warehouse created successfully.", Toast.LENGTH_SHORT).show();
                }

                break;

            // Edited warehouse
            case WarehouseCrudActivity.REQUEST_CODE_EDIT:

                // If success
                if (resultCode == WarehouseCrudActivity.RESULT_CODE_SUCCESS) {

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
        this.wAdapter = new WarehouseListAdapter(this.feed);
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

        Request req = this.apiHandler.warehouse().get();

        this.client.newCall(req).enqueue(new StockItCallback() {

            // Success
            @Override
            public void onOk(JSONObject body) {

                // Read Json Object from response body
                try {

                    JSONArray warehouses = body.getJSONArray("warehouses");

                    // Empty Array
                    WarehouseListActivity.this.feed.clear();

                    // Fill array
                    for (int i = 0; i < warehouses.length(); i++) {

                        JSONObject wh = warehouses.getJSONObject(i);
                        WarehouseListActivity.this.feed.add(
                                new Warehouse(
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
                            WarehouseListActivity.this.updateDisplay();
                        }
                    });

                } catch (JSONException e) {
                    Log.e("WAREHOUSE_LIST", "JSON Exception: " + e.getMessage());
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

        Request req = this.apiHandler.warehouse().delete(id);

        this.client.newCall(req).enqueue(new StockItCallback() {

            // Success
            @Override
            public void onOk(JSONObject body) {

                // Update Ui
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WarehouseListActivity.this, "Warehouse deleted", Toast.LENGTH_SHORT).show();
                        WarehouseListActivity.this.getWarehouses();
                    }
                });
            }
        });
    }

    /**
     * Logs the user out of the application.
     * Deletes user token from both server and shared preferences.
     */
    private void logout() {

        Request req = this.apiHandler.auth().bearerDelete();

        this.client.newCall(req).enqueue(new StockItCallback() {
            @Override
            public void onOk(JSONObject body) {

                // Get shared preferences
                SharedPreferences sharedprefs = WarehouseListActivity.this.getSharedPreferences(
                        "general_prefs_" + String.valueOf(BuildConfig.APPLICATION_ID), MODE_PRIVATE);

                // Remove token and credentials from shared preferences
                sharedprefs.edit().remove("USER_AUTH_TOKEN").commit();

                // Send user to login activity
                Intent i = new Intent(WarehouseListActivity.this, LoginActivity.class);
                startActivity(i);
            }
        });
    }
}
