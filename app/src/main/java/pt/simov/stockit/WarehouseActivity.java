package pt.simov.stockit;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import pt.simov.stockit.core.ApiHandler;
import pt.simov.stockit.core.http.HttpClient;

public class WarehouseActivity extends AppCompatActivity implements View.OnClickListener {

    /**
     * Activity Result Codes
     */
    public static final int RESULT_CODE_FAILURE = 0;
    public static final int RESULT_CODE_SUCCESS = 1;

    /**
     * The request codes available for this activity.
     */
    public static final int REQUEST_CODE_VIEW = 1;
    public static final int REQUEST_CODE_EDIT = 2;
    public static final int REQUEST_CODE_ADD = 3;

    /**
     * The intent.
     */
    private Intent in;

    /**
     * The fields
     */
    private String name, description, lat, lon;

    /**
     * The text fields
     */
    private EditText name_et, description_et, lat_et, lon_et;

    /**
     * The button
     */
    private Button btn;

    /**
     * The text view.
     */
    private TextView tv;

    /**
     * Request code
     */
    private int requestCode;

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

        this.requestCode = in.getIntExtra("REQUEST_CODE", REQUEST_CODE_VIEW);
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

        // Set activity based on request code
        setActivity();
    }

    /**
     * Sets the activity components for the purpose it's being launched for.
     */
    private void setActivity() {

        switch (this.requestCode) {

            // View
            case REQUEST_CODE_VIEW:

                // Disable Text fields
                name_et.setEnabled(false);
                description_et.setEnabled(false);
                lat_et.setEnabled(false);
                lon_et.setEnabled(false);

                // Set Activity Title
                setTitle(R.string.title_view_warehouse);
                tv.setText("View Warehouse");

                // Set Text fields content
                name_et.setText(name);
                description_et.setText(description);
                lat_et.setText(lat);
                lon_et.setText(lon);

                btn.setText("Back");
                break;

            // Edit
            case REQUEST_CODE_EDIT:

                // Enable text field editing
                name_et.setEnabled(true);
                description_et.setEnabled(true);
                lat_et.setEnabled(true);
                lon_et.setEnabled(true);

                // Set activity title
                setTitle(R.string.title_edit_warehouse);
                tv.setText("Edit Warehouse");

                // Set text fields content
                name_et.setText(name);
                description_et.setText(description);
                lat_et.setText(lat);
                lon_et.setText(lon);

                btn.setText("Save");
                break;

            // Create
            case REQUEST_CODE_ADD:

                // Enable text fields
                name_et.setEnabled(true);
                description_et.setEnabled(true);
                lat_et.setEnabled(true);
                lon_et.setEnabled(true);

                // Set activity title
                tv.setText("New Warehouse");
                setTitle(R.string.title_add_warehouse);
                // Set
                /*name_et.setText("");
                description_et.setText("");
                lat_et.setText("");
                lon_et.setText("");*/

                btn.setText("Create");
                break;
        }

        // Set on click button listener
        btn.setOnClickListener(this);
    }

    /**
     * On click listener.
     *
     * @param v The view.
     */
    @Override
    public void onClick(View v) {

        // Read fields content from the UI
        String name = this.name_et.getText().toString();
        String description = this.description_et.getText().toString();
        String lat = this.lat_et.getText().toString();
        String lon = this.lon_et.getText().toString();

        switch (this.requestCode) {

            // View Warehouse
            case REQUEST_CODE_VIEW:

                // Finish activity
                finish();
                break;

            // Add Warehouse
            case REQUEST_CODE_ADD:
                addWarehouse(name, description, lat, lon);
                break;

            // Edit Warehouse
            case REQUEST_CODE_EDIT:
                int id = this.in.getIntExtra("WAREHOUSE_ID", -1);
                editWarehouse(id, name, description, lat, lon);
                break;
        }
    }

    /**
     * Creates a warehouse.
     *
     * @param name        The warehouse name.
     * @param description The warehouse description.
     * @param lat         The warehouse latitude.
     * @param lon         The warehouse longitude.
     */
    private void addWarehouse(String name, String description, String lat, String lon) {

        // Check for latitude and longitude
        if (lat.isEmpty() || lon.isEmpty()) {

            // Create request
            try {
                Request req = this.apiHandler.warehouse().post(name, description);

                this.handleRequest(req);

            } catch (JSONException e) {
                Log.e("CREATE_WAREHOUSE", "JsonException");
            }

        } else {

            float latitude = Float.parseFloat(lat);
            float longitude = Float.parseFloat(lon);

            // Create request
            try {
                Request req = this.apiHandler.warehouse().post(name, description, latitude, longitude);

                this.handleRequest(req);

            } catch (JSONException e) {
                Log.e("CREATE_WAREHOUSE", "JsonException");
            }
        }
    }

    /**
     * Modifies the warehouse.
     *
     * @param id          The warehouse id.
     * @param name        The warehouse name.
     * @param description The warehouse description.
     * @param lat         The warehouse latitude.
     * @param lon         The warehouse longitude.
     */
    private void editWarehouse(int id, String name, String description, String lat, String lon) {

        HashMap<String, Object> map = new HashMap<>();

        if (name != null && !name.isEmpty()) {

            map.put("name", name);
        }

        if (description != null && !description.isEmpty()) {

            map.put("description", description);
        }

        if (lat != null) {

            map.put("latitude", lat);
        }

        if (lon != null) {

            map.put("longitude", lon);
        }

        // If there are modified items
        if (map.size() > 0) {

            try {

                Request req = this.apiHandler.warehouse().patch(id, map);
                this.handleRequest(req);

            } catch (JSONException e) {

                Log.e("EDIT_WAREHOUSE", "JSON Exception: " + e.getMessage());
            }
        }
    }

    /**
     * Creates a warehouse via REST API.
     * @param req The request to be made.
     */
    private void handleRequest(Request req) {

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
            public void onResponse(Call call, Response response) {

                switch (response.code()) {

                    // Success on the request
                    case 200:

                        setResult(RESULT_CODE_SUCCESS, WarehouseActivity.this.getIntent());

                        // Finish activity
                        finish();

                        break;

                    // Success on the request
                    case 201:

                        setResult(RESULT_CODE_SUCCESS, WarehouseActivity.this.getIntent());

                        // Finish activity
                        finish();

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
                        Log.e("WAREHOUSE_CRUD", "Unauthorized");
                        break;

                    // Bad Request
                    case 400:
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(WarehouseActivity.this, "Bad Request", Toast.LENGTH_SHORT).show();
                            }
                        });
                        Log.e("WAREHOUSE_CRUD", "Bad Request");
                        break;

                    // Internal Server Error
                    case 500:

                        setResult(RESULT_CODE_FAILURE, WarehouseActivity.this.getIntent());

                        break;

                    default:
                        Log.e("WAREHOUSE_CRUD", "Unhandled HTTP status code: " + response.code());
                }
            }
        });
    }
}
