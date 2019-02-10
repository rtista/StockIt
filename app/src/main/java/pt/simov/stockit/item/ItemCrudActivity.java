package pt.simov.stockit.item;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import pt.simov.stockit.R;
import pt.simov.stockit.core.ApiHandler;
import pt.simov.stockit.core.http.HttpClient;
import pt.simov.stockit.core.http.StockItCallback;

public class ItemCrudActivity extends AppCompatActivity implements View.OnClickListener, NumberPicker.OnValueChangeListener {

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
     * Request code
     */
    private int requestCode;

    /**
     * The warehouse id.
     */
    private int wid;

    /**
     * The StockIt backend API handler.
     */
    private ApiHandler apiHandler = ApiHandler.getInstance();

    /**
     * HTTP Client provides request queue.
     */
    private OkHttpClient client = HttpClient.getInstance();

    /**
     * Input fields.
     */
    private EditText name_et, desc_et, barcode_et;
    private EditText availablet, allocatedt, alertt;
    private NumberPicker availablep, allocatedp, alertp;

    /**
     * Visibility Toggled Layouts
     */
    private LinearLayout pickers, ets;

    /**
     * On activity creation.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_crud);

        this.requestCode = this.getIntent().getIntExtra("REQUEST_CODE", 0);
        this.wid = this.getIntent().getIntExtra("WAREHOUSE_ID", 0);

        // Edit Texts
        this.name_et = findViewById(R.id.item_crud_et_name);
        this.desc_et = findViewById(R.id.item_crud_description);
        this.barcode_et = findViewById(R.id.item_crud_barcode);
        this.availablet = findViewById(R.id.item_crud_available_et);
        this.allocatedt = findViewById(R.id.item_crud_allocated_et);
        this.alertt = findViewById(R.id.item_crud_alert_et);

        // Number Pickers
        this.availablep = findViewById(R.id.item_crud_available_picker);
        this.allocatedp = findViewById(R.id.item_crud_allocated_picker);
        this.alertp = findViewById(R.id.item_crud_alert_picker);
        this.availablep.setWrapSelectorWheel(false);
        this.allocatedp.setWrapSelectorWheel(false);
        this.alertp.setWrapSelectorWheel(false);

        // Change max allocated value based on available value
        this.availablep.setOnValueChangedListener(this);

        // Layouts
        this.pickers = findViewById(R.id.item_crud_pickers);
        this.ets = findViewById(R.id.item_crud_ets);

        // Set activity based on request code
        setActivity();
    }

    /**
     * Sets the activity components for the purpose it's being launched for.
     */
    private void setActivity() {

        // Initialize Info
        String name, desc, barcode;
        int available, allocated, alert;

        // Button initialization
        Button btn = findViewById(R.id.item_crud_action);
        Button btnBarcode = findViewById(R.id.item_crud_barcode_button);
        buttonBarcodeListener(btnBarcode);

        switch (this.requestCode) {

            // View
            case REQUEST_CODE_VIEW:

                // Set Activity Title
                setTitle(R.string.title_view_item);

                // Set edit text layout visible
                this.pickers.setVisibility(View.GONE);
                this.ets.setVisibility(View.VISIBLE);

                // Disable input fields
                this.name_et.setEnabled(false);
                this.desc_et.setEnabled(false);
                this.barcode_et.setEnabled(false);
                this.availablet.setEnabled(false);
                this.allocatedt.setEnabled(false);
                this.alertt.setEnabled(false);

                // Get Item Info
                name = this.getIntent().getStringExtra("NAME");
                desc = this.getIntent().getStringExtra("DESCRIPTION");
                barcode = this.getIntent().getStringExtra("BARCODE");
                available = this.getIntent().getIntExtra("AVAILABLE", 0);
                allocated = this.getIntent().getIntExtra("ALLOCATED", 0);
                alert = this.getIntent().getIntExtra("ALERT", 0);

                // Set input fields content
                this.name_et.setText(name);
                this.desc_et.setText(desc);
                this.barcode_et.setText(barcode);
                this.availablet.setText(String.valueOf(available));
                this.allocatedt.setText(String.valueOf(allocated));
                this.alertt.setText(String.valueOf(alert));

                // Remove barcode read button
                btnBarcode.setVisibility(View.GONE);

                btn.setText("Back");
                break;

            // Edit
            case REQUEST_CODE_EDIT:

                // Set Activity Title
                setTitle(R.string.title_edit_item);

                // Set edit text layout visible
                this.pickers.setVisibility(View.VISIBLE);
                this.ets.setVisibility(View.GONE);

                // Enable Text fields
                this.name_et.setEnabled(true);
                this.desc_et.setEnabled(true);
                this.barcode_et.setEnabled(true);

                // Get Item Info
                name = this.getIntent().getStringExtra("NAME");
                desc = this.getIntent().getStringExtra("DESCRIPTION");
                barcode = this.getIntent().getStringExtra("BARCODE");
                available = this.getIntent().getIntExtra("AVAILABLE", 0);
                allocated = this.getIntent().getIntExtra("ALLOCATED", 0);
                alert = this.getIntent().getIntExtra("ALERT", 0);

                // Set minimum and maximum quantities for number pickers
                this.availablep.setMinValue(0);
                this.availablep.setMaxValue(Integer.MAX_VALUE);
                this.allocatedp.setMinValue(0);
                this.allocatedp.setMaxValue(available);
                this.alertp.setMinValue(0);
                this.alertp.setMaxValue(Integer.MAX_VALUE);

                // Set input fields content
                this.name_et.setText(name);
                this.desc_et.setText(desc);
                this.barcode_et.setText(barcode);
                this.availablep.setValue(available);
                this.allocatedp.setValue(allocated);
                this.alertp.setValue(alert);

                // Set barcode button visible
                btnBarcode.setVisibility(View.VISIBLE);

                btn.setText("Save");
                break;

            // Create
            case REQUEST_CODE_ADD:

                // Set activity title
                setTitle(R.string.title_add_item);

                // Set edit text layout visible
                this.pickers.setVisibility(View.VISIBLE);
                this.ets.setVisibility(View.GONE);

                // Enable Text fields
                this.name_et.setEnabled(true);
                this.desc_et.setEnabled(true);
                this.barcode_et.setEnabled(true);

                // Set minimum and maximum quantities for number pickers
                this.availablep.setMinValue(0);
                this.availablep.setMaxValue(Integer.MAX_VALUE);
                this.allocatedp.setMinValue(0);
                this.allocatedp.setMaxValue(this.availablep.getValue());
                this.alertp.setMinValue(0);
                this.alertp.setMaxValue(Integer.MAX_VALUE);

                // If launched from barcode button
                barcode = this.getIntent().getStringExtra("BARCODE");
                if (barcode != null && !barcode.isEmpty()) {

                    this.barcode_et.setText(barcode);
                    this.barcode_et.setEnabled(false);
                    btnBarcode.setVisibility(View.GONE);

                } else {

                    // Set barcode button visible
                    btnBarcode.setVisibility(View.VISIBLE);
                }

                btn.setText("Create");
                break;
        }

        // Set on click button listener
        btn.setOnClickListener(this);
    }

    /**
     * On number picker value change.
     *
     * @param picker The picker which value's changed.
     * @param oldVal The old value.
     * @param newVal The new value.
     */
    @Override
    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {

        // Change max value
        this.allocatedp.setMaxValue(newVal);
    }

    /**
     * On click listener.
     *
     * @param v The view.
     */
    @Override
    public void onClick(View v) {

        // Initialize variables
        String name, desc, barcode;
        int available, allocated, alert;

        switch (this.requestCode) {

            // View Warehouse
            case REQUEST_CODE_VIEW:

                // Finish activity
                setResult(ItemCrudActivity.RESULT_CODE_SUCCESS);
                finish();
                break;

            // Add Warehouse
            case REQUEST_CODE_ADD:

                // Read input from UI
                name = this.name_et.getText().toString();
                desc = this.desc_et.getText().toString();
                barcode = this.barcode_et.getText().toString();
                available = this.availablep.getValue();
                allocated = this.allocatedp.getValue();
                alert = this.alertp.getValue();

                addItem(name, desc, barcode, available, allocated, alert);
                break;

            // Edit Warehouse
            case REQUEST_CODE_EDIT:

                // Read input from UI
                name = this.name_et.getText().toString();
                desc = this.desc_et.getText().toString();
                barcode = this.barcode_et.getText().toString();
                available = this.availablep.getValue();
                allocated = this.allocatedp.getValue();
                alert = this.alertp.getValue();

                int id = this.getIntent().getIntExtra("ITEM_ID", -1);
                editItem(id, name, desc, barcode, available, allocated, alert);
                break;
        }
    }

    /**
     * On barcode read button click.
     *
     * @param btn The barcode read button.
     */
    public void buttonBarcodeListener(Button btn){
        btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                new IntentIntegrator(ItemCrudActivity.this).initiateScan();
            }
        });
    }

    /**
     * Creates an item in the warehouse.
     *
     * @param name         The item name.
     * @param description  The item description.
     * @param barcode      The item barcode.
     * @param available    The available quantity.
     * @param allocated    The allocated quantity.
     * @param alert        The alert quantity.
     */
    private void addItem(String name, String description, String barcode,
                         int available, int allocated, int alert) {

        // Create request
        try {
            Request req = this.apiHandler.item().post(this.wid, name, description,
                    barcode, available, allocated, alert);

            this.handleRequest(req);

        } catch (JSONException e) {

            Log.e("CREATE_ITEM", "JsonException");
        }
    }

    /**
     * Modifies an item in the warehouse.
     *
     * @param id          The warehouse id.
     *
     * @param name         The item name.
     * @param description  The item description.
     * @param barcode      The item barcode.
     * @param available    The available quantity.
     * @param allocated    The allocated quantity.
     * @param alert        The alert quantity.
     */
    private void editItem(int id, String name, String description, String barcode,
                          int available, int allocated, int alert) {

        HashMap<String, Object> map = new HashMap<>();

        if (name != null && !name.isEmpty()) {

            map.put("name", name);
        }

        if (description != null && !description.isEmpty()) {

            map.put("description", description);
        }

        if (barcode != null) {

            map.put("barcode", barcode);
        }

        map.put("available", available);
        map.put("allocated", allocated);
        map.put("alert", alert);

        // If there are modified items
        if (map.size() > 0) {

            try {

                Request req = this.apiHandler.item().patch(this.wid, id, map);
                this.handleRequest(req);

            } catch (JSONException e) {

                Log.e("EDIT_ITEM", "JSON Exception: " + e.getMessage());
            }
        }
    }

    /**
     * Creates an item via REST API.
     *
     * @param req The request to be made.
     */
    private void handleRequest(Request req) {

        this.client.newCall(req).enqueue(new StockItCallback() {
            @Override
            public void onOk(JSONObject body) {

                // Set successful result
                setResult(RESULT_CODE_SUCCESS, ItemCrudActivity.this.getIntent());

                // Finish activity
                finish();
            }

            @Override
            public void onCreated(JSONObject body) {

                // Set successful result
                setResult(RESULT_CODE_SUCCESS, ItemCrudActivity.this.getIntent());

                // Finish activity
                finish();
            }

            @Override
            public void onUnauthorized(JSONObject body) {

                // TODO: Refresh user token
                finish();
            }

            @Override
            public void onInternalServerError(JSONObject body) {

                // Set error result code
                setResult(RESULT_CODE_FAILURE, ItemCrudActivity.this.getIntent());

                // Finish activity
                finish();
            }
        });
    }

    /**
     * On result of the barcode reading activity.
     *
     * @param requestCode The activity's request code.
     * @param resultCode  The activity's result code.
     * @param data        The intent data.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if(result != null) {

            if(result.getContents() == null) {

                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();

            } else {

                this.barcode_et.setText(result.getContents());
            }

        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
