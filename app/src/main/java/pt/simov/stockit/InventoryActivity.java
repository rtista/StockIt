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
import pt.simov.stockit.core.domain.Inventory;
import pt.simov.stockit.core.http.HttpClient;

public class InventoryActivity extends AppCompatActivity {

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
    private ArrayList<Inventory> feed = new ArrayList<>();

    /**
     * The list view adapter.
     */
    private InventoryListAdapter wAdapter = new InventoryListAdapter(this.feed);

    /**
     * The list view which will hold the warehouse items.
     */
    private ListView lv;

    /**
     * The warehouse id.
     */
    private int wid;

    /**
     * On creation of the activity.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);
        setTitle(R.string.title_inventory);

        // Text View
        // TextView tv = findViewById(R.id.items_title);
        // tv.setText("Item of @User");

        // Update display
        this.lv = findViewById(R.id.inventory_list);

        // Set list view
        this.wid = this.getIntent().getIntExtra("WAREHOUSE_ID", 0);
        this.getItems();

        // Add Context menu
        registerForContextMenu(this.lv);
    }

    /**
     * Inflate the options menu.
     *
     * @param menu Options menu.
     * @return boolean
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.inventory_optionsmenu, menu);
        return true;
    }

    /**
     * Inflate context menu.
     *
     * @param menu     The context menu.
     * @param v        The view.
     * @param menuInfo The menu info.
     */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.inventory_contextmenu, menu);
    }

    /**
     * On Option Menu item selection.
     *
     * @param item The selected item.
     * @return boolean
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle item selection
        switch (item.getItemId()) {

            // On Add warehouse option
            case R.id.iom_add:

                // Start activity for result
                Intent i = new Intent(this, InventoryCrudActivity.class);

                i.putExtra("WAREHOUSE_ID", this.wid);

                i.putExtra("REQUEST_CODE", InventoryCrudActivity.REQUEST_CODE_ADD);
                startActivityForResult(i, InventoryCrudActivity.REQUEST_CODE_ADD);
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
            case R.id.icm_view:

                i = new Intent(getApplicationContext(), InventoryCrudActivity.class);

                i.putExtra("WAREHOUSE_ID", this.wid);
                i.putExtra("NAME", feed.get(pos).getName());
                i.putExtra("DESCRIPTION", feed.get(pos).getDescription());
                i.putExtra("QUANTITY", feed.get(pos).getQuantity());
                i.putExtra("BARCODE", feed.get(pos).getBarcode());
                i.putExtra("SECTION", feed.get(pos).getSection());
                i.putExtra("MIN_QUANTITY", feed.get(pos).getMin_quantity());

                i.putExtra("REQUEST_CODE", InventoryCrudActivity.REQUEST_CODE_VIEW);
                startActivityForResult(i, InventoryCrudActivity.REQUEST_CODE_VIEW);
                break;

            // On edit option
            case R.id.icm_edit:

                i = new Intent(getApplicationContext(), InventoryCrudActivity.class);

                i.putExtra("WAREHOUSE_ID", this.wid);
                i.putExtra("ITEM_ID", feed.get(pos).getId());
                i.putExtra("NAME", feed.get(pos).getName());
                i.putExtra("DESCRIPTION", feed.get(pos).getDescription());
                i.putExtra("QUANTITY", feed.get(pos).getQuantity());
                i.putExtra("BARCODE", feed.get(pos).getBarcode());
                i.putExtra("SECTION", feed.get(pos).getSection());
                i.putExtra("MIN_QUANTITY", feed.get(pos).getMin_quantity());

                i.putExtra("REQUEST_CODE", InventoryCrudActivity.REQUEST_CODE_EDIT);
                startActivityForResult(i, InventoryCrudActivity.REQUEST_CODE_EDIT);
                break;

            // On delete option
            case R.id.icm_delete:

                new AlertDialog.Builder(this)
                        .setTitle("Delete Item")
                        .setMessage("Do you want to delete this item?")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {

                                InventoryActivity.this.deleteItem(feed.get(pos).getId());
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
            case InventoryCrudActivity.REQUEST_CODE_ADD:

                // If success
                if (resultCode == InventoryCrudActivity.RESULT_CODE_SUCCESS) {

                    // Refresh items list
                    this.getItems();
                    Toast.makeText(this, "Item created successfully.", Toast.LENGTH_SHORT).show();
                }

                break;

            // Edited warehouse
            case InventoryCrudActivity.REQUEST_CODE_EDIT:

                // If success
                if (resultCode == InventoryCrudActivity.RESULT_CODE_SUCCESS) {

                    // Refresh items list
                    this.getItems();
                    Toast.makeText(this, "Item edited successfully.", Toast.LENGTH_SHORT).show();
                }

            default:
                break;
        }
    }

    /**
     * Update the warehouse item list view.
     */
    public void updateDisplay() {

        // Update the list view
        this.lv.setAdapter(this.wAdapter);
        lv.setSelection(0);
    }

    /**
     * Updates the activity's warehouse list.
     *
     * @return boolean
     */
    private void getItems() {

        Request req = this.apiHandler.item().get(this.wid);

        this.client.newCall(req).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(InventoryActivity.this, "Sad life :(", Toast.LENGTH_SHORT).show();
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

                            JSONArray items = new JSONObject(response.body().string()).getJSONArray("items");

                            // Empty Array
                            InventoryActivity.this.feed.clear();

                            // Fill array
                            for (int i = 0; i < items.length(); i++) {

                                JSONObject it = items.getJSONObject(i);
                                InventoryActivity.this.feed.add(
                                        new Inventory(
                                                it.getInt("id"),
                                                it.getString("name"),
                                                it.getString("description"),
                                                it.getInt("quantity"),
                                                it.getString("section"),
                                                it.getString("barcode"),
                                                it.getInt("min_quantity")
                                        )
                                );
                            }

                            // Update Ui
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    InventoryActivity.this.updateDisplay();
                                }
                            });

                        } catch (JSONException e) {
                            Log.e("ITEM_LIST", "JSON Exception: " + e.getMessage());
                        }

                        break;

                    // Unauthorized
                    case 401:
                        // TODO: Refresh user token
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(InventoryActivity.this, "Unauthorized", Toast.LENGTH_SHORT).show();
                            }
                        });
                        break;

                    // Bad Request
                    case 400:
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(InventoryActivity.this, "Bad Request", Toast.LENGTH_SHORT).show();
                            }
                        });
                        break;

                    // Internal Server Error
                    case 500:
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(InventoryActivity.this, "Internal Server Error", Toast.LENGTH_SHORT).show();
                            }
                        });
                        break;
                }
            }
        });
    }

    /**
     * Deletes an item.
     *
     * @param id The item id.
     */
    private void deleteItem(int id) {

        Request req = this.apiHandler.item().delete(this.wid, id);

        this.client.newCall(req).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(InventoryActivity.this, "Sad life :(", Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(InventoryActivity.this, "Item deleted", Toast.LENGTH_SHORT).show();
                                InventoryActivity.this.getItems();
                            }
                        });

                        break;

                    // Unauthorized
                    case 401:
                        // TODO: Refresh user token
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(InventoryActivity.this, "Unauthorized", Toast.LENGTH_SHORT).show();
                            }
                        });
                        break;

                    // Not Found
                    case 404:

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(InventoryActivity.this, "Item Not Found", Toast.LENGTH_SHORT).show();
                            }
                        });
                        break;

                    // Internal Server Error
                    case 500:
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(InventoryActivity.this, "Internal Server Error", Toast.LENGTH_SHORT).show();
                            }
                        });
                        break;
                }
            }
        });
    }
}
