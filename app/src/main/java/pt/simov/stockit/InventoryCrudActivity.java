package pt.simov.stockit;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

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

public class InventoryCrudActivity extends AppCompatActivity implements View.OnClickListener {

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
     * Text fields.
     */
    private EditText name_et, desc_et, quant_et, barcode_et, section_et, min_quant_et;

    /**
     * On activity creation.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory_crud);

        this.requestCode = this.getIntent().getIntExtra("REQUEST_CODE", 0);
        this.wid = this.getIntent().getIntExtra("WAREHOUSE_ID", 0);

        this.name_et = findViewById(R.id.item_crud_et_name);
        this.desc_et = findViewById(R.id.item_crud_description);
        this.quant_et = findViewById(R.id.item_crud_et_quantity);
        this.barcode_et = findViewById(R.id.item_crud_barcode);
        this.section_et = findViewById(R.id.item_crud_section);
        this.min_quant_et = findViewById(R.id.item_crud_alert);

        // Set activity based on request code
        setActivity();
    }

    /**
     * Sets the activity components for the purpose it's being launched for.
     */
    private void setActivity() {

        // Initialize Info
        String name, desc, barcode, section;
        int quant, min_quant;

        Button btn = findViewById(R.id.item_crud_action);
        Button btnBarcode = findViewById(R.id.item_crud_barcode_button);
        buttonBarcodeListener(btnBarcode);

        switch (this.requestCode) {

            // View
            case REQUEST_CODE_VIEW:

                // Disable Text fields
                this.name_et.setEnabled(false);
                this.desc_et.setEnabled(false);
                this.quant_et.setEnabled(false);
                this.barcode_et.setEnabled(false);
                this.section_et.setEnabled(false);
                this.min_quant_et.setEnabled(false);

                // Set Activity Title
                setTitle(R.string.title_view_item);

                // Get Info
                name = this.getIntent().getStringExtra("NAME");
                desc = this.getIntent().getStringExtra("DESCRIPTION");
                quant = this.getIntent().getIntExtra("QUANTITY", 0);
                barcode = this.getIntent().getStringExtra("BARCODE");
                section = this.getIntent().getStringExtra("SECTION");
                min_quant = this.getIntent().getIntExtra("MIN_QUANTITY", 0);

                // Set Text fields content
                this.name_et.setText(name);
                this.desc_et.setText(desc);
                this.quant_et.setText(String.valueOf(quant));
                this.barcode_et.setText(barcode);
                this.section_et.setText(section);
                this.min_quant_et.setText(String.valueOf(min_quant));

                btnBarcode.setVisibility(View.INVISIBLE);
                btn.setText("Back");
                break;

            // Edit
            case REQUEST_CODE_EDIT:

                // Enable Text fields
                this.name_et.setEnabled(true);
                this.desc_et.setEnabled(true);
                this.quant_et.setEnabled(true);
                this.barcode_et.setEnabled(true);
                this.section_et.setEnabled(true);
                this.min_quant_et.setEnabled(true);

                // Set Activity Title
                setTitle(R.string.title_edit_item);

                // Get Info
                name = this.getIntent().getStringExtra("NAME");
                desc = this.getIntent().getStringExtra("DESCRIPTION");
                quant = this.getIntent().getIntExtra("QUANTITY", 0);
                barcode = this.getIntent().getStringExtra("BARCODE");
                section = this.getIntent().getStringExtra("SECTION");
                min_quant = this.getIntent().getIntExtra("MIN_QUANTITY", 0);

                // Set Text fields content
                this.name_et.setText(name);
                this.desc_et.setText(desc);
                this.quant_et.setText(String.valueOf(quant));
                this.barcode_et.setText(barcode);
                this.section_et.setText(section);
                this.min_quant_et.setText(String.valueOf(min_quant));

                btn.setText("Save");
                break;

            // Create
            case REQUEST_CODE_ADD:

                // Enable Text fields
                this.name_et.setEnabled(true);
                this.desc_et.setEnabled(true);
                this.quant_et.setEnabled(true);
                this.barcode_et.setEnabled(true);
                this.section_et.setEnabled(true);
                this.min_quant_et.setEnabled(true);

                //Get Info
                barcode = this.getIntent().getStringExtra("BARCODE");
                if (barcode != null && !barcode.isEmpty()) {
                    this.barcode_et.setText(barcode);
                    this.barcode_et.setEnabled(false);
                }

                // Set default text field content
                this.quant_et.setText("0");
                this.min_quant_et.setText("0");

                // Set activity title
                setTitle(R.string.title_add_item);

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
        String desc = this.desc_et.getText().toString();
        int quant = Integer.parseInt(this.quant_et.getText().toString());
        String barcode = this.barcode_et.getText().toString();
        String section = this.section_et.getText().toString();
        int min_quant = Integer.parseInt(this.min_quant_et.getText().toString());

        switch (this.requestCode) {

            // View Warehouse
            case REQUEST_CODE_VIEW:

                // Finish activity
                finish();
                break;

            // Add Warehouse
            case REQUEST_CODE_ADD:
                addItem(name, desc, quant, barcode, section, min_quant);
                break;

            // Edit Warehouse
            case REQUEST_CODE_EDIT:
                int id = this.getIntent().getIntExtra("ITEM_ID", -1);
                editItem(id, name, desc, quant, barcode, section, min_quant);
                break;
        }
    }

    public void buttonBarcodeListener(Button btn){
        btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                new IntentIntegrator(InventoryCrudActivity.this).initiateScan();
            }
        });

    }

    /**
     * Creates an item in the warehouse.
     *
     * @param name         The item name.
     * @param description  The item description.
     * @param quantity     The item quantity.
     * @param barcode      The item barcode.
     * @param section      The item warehouse section.
     * @param min_quantity The item alert quantity.
     */
    private void addItem(String name, String description, int quantity, String barcode,
                         String section, int min_quantity) {

        // Create request
        try {
            Request req = this.apiHandler.item().post(this.wid, name, description,
                    quantity, section, barcode, min_quantity);

            this.handleRequest(req);

        } catch (JSONException e) {

            Log.e("CREATE_ITEM", "JsonException");
        }
    }

    /**
     * Modifies the warehouse.
     *
     * @param id          The warehouse id.
     * @param name        The warehouse name.
     * @param description The warehouse description.
     */
    private void editItem(int id, String name, String description, int quantity,
                          String barcode, String section, int min_quantity) {

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

        if (section != null) {

            map.put("section", section);
        }

        map.put("quantity", quantity);
        map.put("min_quantity", min_quantity);

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
     * Creates a warehouse via REST API.
     *
     * @param req The request to be made.
     */
    private void handleRequest(Request req) {

        this.client.newCall(req).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(InventoryCrudActivity.this, "Sad life :(", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) {

                switch (response.code()) {

                    // Success on the request
                    case 200:

                        setResult(RESULT_CODE_SUCCESS, InventoryCrudActivity.this.getIntent());

                        // Finish activity
                        finish();

                        break;

                    // Success on the request
                    case 201:

                        setResult(RESULT_CODE_SUCCESS, InventoryCrudActivity.this.getIntent());

                        // Finish activity
                        finish();

                        break;

                    // Unauthorized
                    case 401:
                        // TODO: Refresh user token
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(InventoryCrudActivity.this, "Unauthorized", Toast.LENGTH_SHORT).show();
                            }
                        });
                        Log.e("ITEM_CRUD", "Unauthorized");
                        break;

                    // Bad Request
                    case 400:
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(InventoryCrudActivity.this, "Bad Request", Toast.LENGTH_SHORT).show();
                            }
                        });
                        Log.e("ITEM_CRUD", "Bad Request");
                        break;

                    // Internal Server Error
                    case 500:

                        setResult(RESULT_CODE_FAILURE, InventoryCrudActivity.this.getIntent());
                        break;

                    default:
                        Log.e("ITEM_CRUD", "Unhandled HTTP status code: " + response.code());
                        break;
                }
            }
        });
    }

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
