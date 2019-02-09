package pt.simov.stockit.item;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.ResultPoint;
import com.google.zxing.client.android.BeepManager;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.journeyapps.barcodescanner.DefaultDecoderFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import pt.simov.stockit.R;
import pt.simov.stockit.core.ApiHandler;
import pt.simov.stockit.core.domain.Item;
import pt.simov.stockit.core.http.HttpClient;
import pt.simov.stockit.core.http.StockItCallback;

public class BarcodeActivity extends AppCompatActivity implements BarcodeCallback {

    /**
     * The StockIt backend API handler.
     */
    private ApiHandler apiHandler = ApiHandler.getInstance();

    /**
     * HTTP Client provides request queue.
     */
    private OkHttpClient client = HttpClient.getInstance();

    /**
     * The request codes available for this activity.
     */
    public static final int REQUEST_CODE_READ = 1;
    public static final int REQUEST_CODE_WRITE = 2;

    /**
     * Request code
     */
    private int requestCode;

    /**
     * The warehouse id.
     */
    private int wid;

    /**
     * The barcode reader view.
     */
    private DecoratedBarcodeView barcodeView;

    /**
     * The beep manager allows playing beeps on events.
     */
    private BeepManager beepManager;

    /**
     * On activity creation.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode);

        // Get warehouse id and request code from intent
        wid = this.getIntent().getIntExtra("WAREHOUSE_ID", 0);
        requestCode = this.getIntent().getIntExtra("REQUEST_CODE", 0);

        // Set title by request code
        switch (requestCode){
            case REQUEST_CODE_READ:
                setTitle(R.string.title_activity_barcode_Read);
                break;
            case REQUEST_CODE_WRITE:
                setTitle(R.string.title_activity_barcode_Write);
        }

        barcodeView = findViewById(R.id.barcode_scanner);
        Collection<BarcodeFormat> formats = Arrays.asList(BarcodeFormat.CODE_39);
        barcodeView.getBarcodeView().setDecoderFactory(new DefaultDecoderFactory(formats));
        barcodeView.initializeFromIntent(this.getIntent());
        barcodeView.decodeContinuous(this);

        beepManager = new BeepManager(this);
    }

    /**
     * On activity resume.
     */
    @Override
    protected void onResume() {

        super.onResume();
        barcodeView.resume();
    }

    /**
     * On activity pause.
     */
    @Override
    protected void onPause() {

        super.onPause();
        barcodeView.pause();
    }

    /**
     * On barcode detection.
     *
     * @param result The barcode result.
     */
    @Override
    public void barcodeResult(BarcodeResult result) {

        // Alert user that the barcode has been read
        beepManager.playBeepSoundAndVibrate();

        // Pause barcode reading while processing
        barcodeView.pause();

        // Read barcode
        String barcode = result.getText();

        // Find item by barcode
        this.getItemByBarcode(barcode);

        // Sleep
        SystemClock.sleep(750);
    }

    @Override
    public void possibleResultPoints(List<ResultPoint> resultPoints) {
    }

    /**
     * Verify if an item exists in the database.
     *
     * @return boolean
     */
    private void getItemByBarcode(final String barcode) {

        Request req = this.apiHandler.item().get(this.wid, barcode);

        this.client.newCall(req).enqueue(new StockItCallback() {
            @Override
            public void onOk(JSONObject body) {

                // Read Json Object from response body
                try {

                    JSONObject it = body.getJSONArray("items").getJSONObject(0);

                    Item item = new Item(
                            it.getInt("id"),
                            it.getString("name"),
                            it.getString("description"),
                            it.getString("barcode"),
                            it.getInt("available"),
                            it.getInt("allocated"),
                            it.getInt("alert")
                    );

                    // Take Action based on activity result
                    switch (BarcodeActivity.this.requestCode) {

                        // On Read Mode - Open View Item Activity
                        case BarcodeActivity.REQUEST_CODE_READ:

                            Intent iViewItem = new Intent(BarcodeActivity.this, ItemCrudActivity.class);

                            iViewItem.putExtra("WAREHOUSE_ID", wid);
                            iViewItem.putExtra("NAME", item.getName());
                            iViewItem.putExtra("DESCRIPTION", item.getDescription());
                            iViewItem.putExtra("BARCODE", item.getBarcode());
                            iViewItem.putExtra("AVAILABLE", item.getAvailable());
                            iViewItem.putExtra("ALLOCATED", item.getAllocated());
                            iViewItem.putExtra("ALERT", item.getAlert());

                            iViewItem.putExtra("REQUEST_CODE", ItemCrudActivity.REQUEST_CODE_VIEW);
                            startActivityForResult(iViewItem, ItemCrudActivity.REQUEST_CODE_VIEW);

                            break;

                        // On Write Mode - Increment Item Quantity
                        case BarcodeActivity.REQUEST_CODE_WRITE:
                            increment(item);
                            break;

                        default:
                            break;
                    }

                } catch (JSONException e) {
                    Log.e("Find_Item", "JSON Exception: " + e.getMessage());
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        BarcodeActivity.this.barcodeView.resume();
                    }
                });
            }

            @Override
            public void onUnauthorized(JSONObject body) {

                // TODO: Refresh user token
            }

            @Override
            public void onNotFound(JSONObject body) {

                // Take Action based on activity result
                switch (BarcodeActivity.this.requestCode) {

                    // On Read Mode - Ignore
                    case BarcodeActivity.REQUEST_CODE_READ:

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(BarcodeActivity.this, "Barcode Not Found.", Toast.LENGTH_SHORT).show();
                            }
                        });
                        break;

                    // On Write Mode - Create Item
                    case BarcodeActivity.REQUEST_CODE_WRITE:

                        Intent iAddItem = new Intent(BarcodeActivity.this, ItemCrudActivity.class);

                        iAddItem.putExtra("WAREHOUSE_ID", wid);
                        iAddItem.putExtra("BARCODE", barcode);
                        iAddItem.putExtra("REQUEST_CODE", ItemCrudActivity.REQUEST_CODE_ADD);

                        startActivityForResult(iAddItem, ItemCrudActivity.REQUEST_CODE_ADD);
                        break;

                    default:
                        break;
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        BarcodeActivity.this.barcodeView.resume();
                    }
                });
            }
        });
    }

    /**
     * Increments an item's quantity by one.
     *
     * @param item The item which quantity should be incremented.
     */
    private void increment(Item item) {

        HashMap<String, Object> params = new HashMap<>();

        // Increment count
        params.put("available", item.getAvailable() + 1);

        Request req = null;
        try {
            req = this.apiHandler.item().patch(this.wid, item.getId(), params);

        } catch (JSONException e) {
            Log.e("JSON_EXCEPTION", e.getMessage());
        }

        this.client.newCall(req).enqueue(new StockItCallback() {
            @Override
            public void onOk(JSONObject body) {

                // Toast user with success
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(BarcodeActivity.this, "1 Unit Added", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onUnauthorized(JSONObject body) {

                // TODO: Refresh user token
            }
        });
    }

    /**
     * On ItemCrudActivity finish.
     *
     * @param requestCode The activity request code.
     * @param resultCode The activity result code.
     * @param data The intent.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        switch (requestCode) {

            // Added warehouse
            case ItemCrudActivity.REQUEST_CODE_ADD:

                // If success
                if (resultCode == ItemCrudActivity.RESULT_CODE_SUCCESS) {

                    Toast.makeText(this, "Item created successfully.", Toast.LENGTH_SHORT).show();
                }

                break;

            default:
                break;
        }
    }
}
