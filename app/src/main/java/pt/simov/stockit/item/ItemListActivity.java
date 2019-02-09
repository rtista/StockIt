package pt.simov.stockit.item;

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

import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import pt.simov.stockit.R;
import pt.simov.stockit.core.ApiHandler;
import pt.simov.stockit.core.domain.Item;
import pt.simov.stockit.core.http.HttpClient;
import pt.simov.stockit.core.http.StockItCallback;

public class ItemListActivity extends AppCompatActivity {

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
    private ArrayList<Item> feed = new ArrayList<>();

    /**
     * The list view adapter.
     */
    private ItemListAdapter wAdapter = new ItemListAdapter(this.feed);

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
        setContentView(R.layout.activity_item_list);
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
     * On resume update the display.
     */
    @Override
    protected void onResume() {
        super.onResume();

        // Update list
        this.getItems();
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

            // On Add item option
            case R.id.iom_add:

                // Start activity for result
                Intent i = new Intent(this, ItemCrudActivity.class);

                i.putExtra("WAREHOUSE_ID", this.wid);

                i.putExtra("REQUEST_CODE", ItemCrudActivity.REQUEST_CODE_ADD);
                startActivityForResult(i, ItemCrudActivity.REQUEST_CODE_ADD);
                break;

            case R.id.iom_barcode_read:

                Intent iBarcodeRead = new Intent(this, BarcodeActivity.class);

                iBarcodeRead.putExtra("WAREHOUSE_ID", this.wid);

                iBarcodeRead.putExtra("REQUEST_CODE", BarcodeActivity.REQUEST_CODE_READ);
                startActivity(iBarcodeRead);
                break;

            case R.id.iom_barcode_write:

                Intent iBarcodeWrite = new Intent(this, BarcodeActivity.class);

                iBarcodeWrite.putExtra("WAREHOUSE_ID", this.wid);

                iBarcodeWrite.putExtra("REQUEST_CODE", BarcodeActivity.REQUEST_CODE_WRITE);
                startActivity(iBarcodeWrite);
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

                i = new Intent(getApplicationContext(), ItemCrudActivity.class);

                i.putExtra("WAREHOUSE_ID", this.wid);
                i.putExtra("NAME", feed.get(pos).getName());
                i.putExtra("DESCRIPTION", feed.get(pos).getDescription());
                i.putExtra("BARCODE", feed.get(pos).getBarcode());
                i.putExtra("AVAILABLE", feed.get(pos).getAvailable());
                i.putExtra("ALLOCATED", feed.get(pos).getAllocated());
                i.putExtra("ALERT", feed.get(pos).getAlert());

                i.putExtra("REQUEST_CODE", ItemCrudActivity.REQUEST_CODE_VIEW);
                startActivityForResult(i, ItemCrudActivity.REQUEST_CODE_VIEW);
                break;

            // On edit option
            case R.id.icm_edit:

                i = new Intent(getApplicationContext(), ItemCrudActivity.class);

                i.putExtra("WAREHOUSE_ID", this.wid);
                i.putExtra("ITEM_ID", feed.get(pos).getId());
                i.putExtra("NAME", feed.get(pos).getName());
                i.putExtra("DESCRIPTION", feed.get(pos).getDescription());
                i.putExtra("BARCODE", feed.get(pos).getBarcode());
                i.putExtra("AVAILABLE", feed.get(pos).getAvailable());
                i.putExtra("ALLOCATED", feed.get(pos).getAllocated());
                i.putExtra("ALERT", feed.get(pos).getAlert());

                i.putExtra("REQUEST_CODE", ItemCrudActivity.REQUEST_CODE_EDIT);
                startActivityForResult(i, ItemCrudActivity.REQUEST_CODE_EDIT);
                break;

            // On delete option
            case R.id.icm_delete:

                new AlertDialog.Builder(this)
                        .setTitle("Delete Item")
                        .setMessage("Do you want to delete this item?")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {

                                ItemListActivity.this.deleteItem(feed.get(pos).getId());
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
            case ItemCrudActivity.REQUEST_CODE_ADD:

                // If success
                if (resultCode == ItemCrudActivity.RESULT_CODE_SUCCESS) {

                    // Refresh items list
                    this.getItems();
                    Toast.makeText(this, "Item created successfully.", Toast.LENGTH_SHORT).show();
                }

                break;

            // Edited warehouse
            case ItemCrudActivity.REQUEST_CODE_EDIT:

                // If success
                if (resultCode == ItemCrudActivity.RESULT_CODE_SUCCESS) {

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
        this.wAdapter = new ItemListAdapter(this.feed);
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

        this.client.newCall(req).enqueue(new StockItCallback() {
            @Override
            public void onOk(JSONObject body) {


                // Read Json Object from response body
                try {

                    JSONArray items = body.getJSONArray("items");

                    // Empty Array
                    ItemListActivity.this.feed.clear();

                    // Fill array
                    for (int i = 0; i < items.length(); i++) {

                        JSONObject it = items.getJSONObject(i);
                        ItemListActivity.this.feed.add(
                                new Item(
                                        it.getInt("id"),
                                        it.getString("name"),
                                        it.getString("description"),
                                        it.getString("barcode"),
                                        it.getInt("available"),
                                        it.getInt("allocated"),
                                        it.getInt("alert")
                                )
                        );
                    }

                    // Update Ui
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ItemListActivity.this.updateDisplay();
                        }
                    });

                } catch (JSONException e) {
                    Log.e("ITEM_LIST", "JSON Exception: " + e.getMessage());
                }
            }

            @Override
            public void onUnauthorized(JSONObject body) {

                // TODO: Refresh user token
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

        this.client.newCall(req).enqueue(new StockItCallback() {
            @Override
            public void onOk(JSONObject body) {

                // Update Ui
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ItemListActivity.this, "Item deleted", Toast.LENGTH_SHORT).show();
                        ItemListActivity.this.getItems();
                    }
                });
            }

            @Override
            public void onUnauthorized(JSONObject body) {

                // TODO: Refresh user token
            }
        });
    }
}
