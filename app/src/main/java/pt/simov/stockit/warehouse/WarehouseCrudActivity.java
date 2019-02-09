package pt.simov.stockit.warehouse;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import pt.simov.stockit.R;
import pt.simov.stockit.core.ApiHandler;
import pt.simov.stockit.core.http.HttpClient;
import pt.simov.stockit.core.http.StockItCallback;

public class WarehouseCrudActivity extends AppCompatActivity implements View.OnClickListener {

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
     * the google maps for the fragment
     */
    private GoogleMap mMap;

    /**
     * On activity creation.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_warehouse_crud);

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

        SupportMapFragment mapFragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.warehouse_map);
        ViewGroup.LayoutParams params = mapFragment.getView().getLayoutParams();
        params.height = 900;
        mapFragment.getView().setLayoutParams(params);
        mapFragment.getMapAsync(mapReadyCallback);


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

                setEditTextListenetrs();
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
                setEditTextListenetrs();
                break;
        }

        // Set on click button listener
        btn.setOnClickListener(this);
    }

    /**
     * Sets listeners for lat and lon, to change marker on map
     */
    private void setEditTextListenetrs(){
        lat_et.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                try{
                    mMap.clear();
                    lat = lat_et.getText().toString();
                    LatLng warehouse =new LatLng(Double.parseDouble(lat), Double.parseDouble(lon));
                    mMap.addMarker(new MarkerOptions().position(warehouse).title("Warehouse " + name));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(warehouse));
                    // Zoom in, animating the camera.
                    mMap.animateCamera(CameraUpdateFactory.zoomIn());
                    // Zoom out to zoom level 10, animating with a duration of 2 seconds.
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(14), 2000, null);
                }catch (NumberFormatException e) {
                    Toast.makeText(getApplicationContext(), "Waiting for acceptable coordinates", Toast.LENGTH_SHORT).show();
                }catch (NullPointerException e){

                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        lon_et.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                try{
                    mMap.clear();
                    lon = lon_et.getText().toString();
                    LatLng warehouse =new LatLng(Double.parseDouble(lat), Double.parseDouble(lon));
                    mMap.addMarker(new MarkerOptions().position(warehouse).title("Warehouse " + name));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(warehouse));
                    // Zoom in, animating the camera.
                    mMap.animateCamera(CameraUpdateFactory.zoomIn());
                    // Zoom out to zoom level 10, animating with a duration of 2 seconds.
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(14), 2000, null);
                }catch (NumberFormatException e) {
                    Toast.makeText(getApplicationContext(), "Waiting for acceptable coordinates", Toast.LENGTH_SHORT).show();
                }catch (NullPointerException e){

                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });
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

    OnMapReadyCallback mapReadyCallback = new OnMapReadyCallback() {
        @Override
        public void onMapReady(GoogleMap googleMap) {
            LatLng warehouse;
            mMap = googleMap;
            switch(requestCode){
                case REQUEST_CODE_VIEW:
                    warehouse =new LatLng(Double.parseDouble(lat), Double.parseDouble(lon));
                    //mMap.getUiSettings().setZoomGesturesEnabled(true);
                    mMap.getUiSettings().setZoomControlsEnabled(true);
                    //mMap.getUiSettings().setMyLocationButtonEnabled(true);
                    //mMap.getUiSettings().setCompassEnabled(true);
                    mMap.getUiSettings().setAllGesturesEnabled(false);
                    //mMap.getUiSettings().setRotateGesturesEnabled(true);
                    mMap.addMarker(new MarkerOptions().position(warehouse).title("Warehouse " + name));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(warehouse));
                    // Zoom in, animating the camera.
                    mMap.animateCamera(CameraUpdateFactory.zoomIn());
                    // Zoom out to zoom level 10, animating with a duration of 2 seconds.
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(14), 2000, null);
                    break;

                // Add Warehouse
                case REQUEST_CODE_ADD:
                    //mMap.getUiSettings().setZoomGesturesEnabled(true);
                    mMap.getUiSettings().setZoomControlsEnabled(true);
                    //mMap.getUiSettings().setMyLocationButtonEnabled(true);
                    //mMap.getUiSettings().setCompassEnabled(true);
                    //mMap.getUiSettings().setAllGesturesEnabled(false);
                    //mMap.getUiSettings().setRotateGesturesEnabled(true);

                    mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                        @Override
                        public void onMapClick(LatLng point) {
                            mMap.clear();
                            mMap.addMarker(new MarkerOptions().position(point));
                            lat = String.valueOf(point.latitude);
                            lon = String.valueOf(point.longitude);
                            lat_et.setText(lat);
                            lon_et.setText(lon);
                        }
                    });
                    break;

                // Edit Warehouse
                case REQUEST_CODE_EDIT:
                    mMap.getUiSettings().setZoomControlsEnabled(true);
                    warehouse =new LatLng(Double.parseDouble(lat), Double.parseDouble(lon));
                    mMap.addMarker(new MarkerOptions().position(warehouse));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(warehouse));
                    // Zoom in, animating the camera.
                    mMap.animateCamera(CameraUpdateFactory.zoomIn());
                    // Zoom out to zoom level 10, animating with a duration of 2 seconds.
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(14), 2000, null);
                    mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                        @Override
                        public void onMapClick(LatLng point) {
                            mMap.clear();
                            mMap.addMarker(new MarkerOptions().position(point));
                            lat = String.valueOf(point.latitude);
                            lon = String.valueOf(point.longitude);
                            lat_et.setText(lat);
                            lon_et.setText(lon);
                        }
                    });
                    break;
            }
            mMap.setBuildingsEnabled(true);
        }
    };

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
     * Handles al the requests to the REST API.
     *
     * @param req The request to be made.
     */
    private void handleRequest(Request req) {

        this.client.newCall(req).enqueue(new StockItCallback() {

            // On edit success
            @Override
            public void onOk(JSONObject body) {
                setResult(RESULT_CODE_SUCCESS, WarehouseCrudActivity.this.getIntent());
                finish();
            }

            // On add success
            @Override
            public void onCreated(JSONObject body) {
                setResult(RESULT_CODE_SUCCESS, WarehouseCrudActivity.this.getIntent());
                finish();
            }

            // On failure
            @Override
            public void onInternalServerError(JSONObject body) {
                setResult(RESULT_CODE_FAILURE, WarehouseCrudActivity.this.getIntent());
                finish();
            }
        });
    }
}
