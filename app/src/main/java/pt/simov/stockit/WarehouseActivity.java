package pt.simov.stockit;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import pt.simov.stockit.core.ApiHandler;
import pt.simov.stockit.core.http.HttpClient;
import pt.simov.stockit.domain.Warehouse;

public class WarehouseActivity extends AppCompatActivity implements View.OnClickListener {

    Intent in;
    String name, description, lat, lon;
    EditText name_et, description_et, lat_et, lon_et;
    Button btn;
    TextView tv;

    /**
     * The StockIt backend API handler.
     */
    private ApiHandler apiHandler = ApiHandler.getInstance();

    /**
     * HTTP Client provides request queue.
     */
    private OkHttpClient client = HttpClient.getInstance();

    /**
     * On activity creation.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_warehouse);

        in = getIntent();
        int request =  in.getIntExtra("REQUEST_CODE",1);
        name = in.getStringExtra("NAME");
        description = in.getStringExtra("DESCRIPTION");
        lat = in.getStringExtra("LATITUDE");
        lon = in.getStringExtra("LONGITUDE");

        name_et = findViewById(R.id.warehouse_name);
        description_et = findViewById(R.id.warehouse_description);
        lat_et = findViewById(R.id.warehouse_lat);
        lon_et = findViewById(R.id.warehouse_long);
        btn = findViewById(R.id.btn_action);
        tv = findViewById(R.id.warehouse_title);
        // IF VIEWING
        setActivity(request);
    }

    /**
     * Options menu creation.
     * @param menu The menu to be inflated.
     * @return boolean
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.warehouse_optionsmenu, menu);
        return true;
    }

    /**
     * Options menu item selection handler.
     * @param item The selected item
     * @return boolean
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {// Handle item selection
        switch (item.getItemId()) {
            case R.id.wom_view_map:
                Toast.makeText(this, "View in Map",Toast.LENGTH_LONG).show();
                return true;
            case R.id.wom_edit:
                setActivity(2);
                btn.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View arg0) {
                        Toast.makeText(WarehouseActivity.this, "Save",Toast.LENGTH_LONG).show();
                        name = name_et.getText().toString();
                        description = description_et.getText().toString();
                        lat = lat_et.getText().toString();
                        lon = lon_et.getText().toString();
                        //TODO save edited information
                        setActivity(1);
                    }
                });
                return true;
            case R.id.wom_delete:
                new AlertDialog.Builder(this)
                        .setTitle("Delete Warehouse")
                        .setMessage("Do you want to delete this warehouse?")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                Intent i = new Intent(WarehouseActivity.this, WarehousesTableActivity.class);
                                startActivity(i);
                                //TODO remove warehouse from database
                                Toast.makeText(WarehouseActivity.this, "Warehouse deleted from database", Toast.LENGTH_SHORT).show();
                            }})
                        .setNegativeButton(android.R.string.no, null).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Sets the activity components for the purpose it's being launched for.
     * @param request The intent's request code.
     */
    private void setActivity(int request){

        switch(request) {

            // View
            case 1:
                name_et.setEnabled(false);
                description_et.setEnabled(false);
                lat_et.setEnabled(false);
                lon_et.setEnabled(false);
                btn.setVisibility(View.GONE);
                setTitle(R.string.title_view_warehouse);
                //TODO get info
                tv.setText("View Warehouse");
                name_et.setText(name);
                description_et.setText(description);
                lat_et.setText(lat);
                lon_et.setText(lon);
                break;

            // Edit
            case 2:
                name_et.setEnabled(true);
                description_et.setEnabled(true);
                lat_et.setEnabled(true);
                lon_et.setEnabled(true);
                btn.setVisibility(View.VISIBLE);
                setTitle(R.string.title_edit_warehouse);
                //TODO get info
                tv.setText("Edit Warehouse");
                name_et.setText(name);
                description_et.setText(description);
                lat_et.setText(lat);
                lon_et.setText(lon);
                btn.setText("Confirm");
                break;

            // Create
            case 3:
                tv.setText("New Warehouse");
                name_et.setEnabled(true);
                description_et.setEnabled(true);
                lat_et.setEnabled(true);
                lon_et.setEnabled(true);
                btn.setVisibility(View.VISIBLE);
                setTitle(R.string.title_add_warehouse);
                //TODO get info
                name_et.setText("");
                description_et.setText("");
                lat_et.setText("");
                lon_et.setText("");
                btn.setText("Add Warehouse");
                break;
        }
    }

    /**
     * Creates a warehouse via REST API.
     * @param req
     */
    private void createWarehouse(Request req) {

        this.client.newCall(req).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WarehouseActivity.this, "Sad life :(", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                switch (response.code()) {

                    // Success on the request
                    case 201:

                        // Warehouse Successfully created
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(WarehouseActivity.this, "Warehouse Successfully Created", Toast.LENGTH_SHORT).show();
                            }
                        });

                        break;

                    // Unauthorized
                    case 401:
                        // TODO: Refresh user token
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(WarehouseActivity.this, "Unauthorized", Toast.LENGTH_SHORT).show();
                            }
                        });
                        break;

                    // Bad Request
                    case 400:
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(WarehouseActivity.this, "Bad Request", Toast.LENGTH_SHORT).show();
                            }
                        });
                        break;

                    // Internal Server Error
                    case 500:

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(WarehouseActivity.this, "Internal Server Error", Toast.LENGTH_SHORT).show();
                            }
                        });

                        break;

                    default:
                        Log.e("CREATE_WAREHOUSE", "Unhandled HTTP status code: " + response.code());
                }
            }
        });
    }

    /**
     * On click listener.
     * @param v The view.
     */
    @Override
    public void onClick(View v) {

        // Read fields content from the UI
        String name = this.name_et.getText().toString();
        String description = this.description_et.getText().toString();
        String lat = this.lat_et.getText().toString();
        String lon = this.lon_et.getText().toString();

        // Check for latitude and longitude
        if (lat.isEmpty() || lon.isEmpty()) {

            // Create request
            try {
                Request req = this.apiHandler.createWarehouse(name, description);

                this.createWarehouse(req);

            } catch (JSONException e) {
                Log.e("CREATE_WAREHOUSE", "JsonException");
            }

        } else {

            float latitude = Float.parseFloat(lat);
            float longitude = Float.parseFloat(lon);

            // Create request
            try {
                Request req = this.apiHandler.createWarehouse(name, description, latitude, longitude);

                this.createWarehouse(req);

            } catch (JSONException e) {
                Log.e("CREATE_WAREHOUSE", "JsonException");
            }
        }
    }
}
