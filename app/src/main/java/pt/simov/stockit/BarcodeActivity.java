package pt.simov.stockit;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
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

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import pt.simov.stockit.core.ApiHandler;
import pt.simov.stockit.core.domain.Item;
import pt.simov.stockit.core.http.HttpClient;

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

    private DecoratedBarcodeView barcodeView;
    private BeepManager beepManager;
    private TextView lastValue;

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

        // lastValue = findViewById(R.id.lastValue);

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

        // Compare against last read value
        if (barcode == null || barcode.equals(lastValue)) {
            // Prevent duplicate scans
            return;
        }

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

        this.client.newCall(req).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(BarcodeActivity.this, "API connection error.", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                // Success on the request
                switch (response.code()) {
                    case 200:
                        // Read Json Object from response body
                        try {

                            JSONObject it = new JSONObject(response.body().string())
                                    .getJSONArray("items").getJSONObject(0);

                            Item item = new Item(
                                    it.getInt("id"),
                                    it.getString("name"),
                                    it.getString("description"),
                                    it.getString("barcode"),
                                    it.getInt("available"),
                                    it.getInt("allocated"),
                                    it.getInt("alert")
                            );

                            Log.e("ITEM_DEBUG", "Item Barcode: " + item.getBarcode());

                            // Take Action based on activity result
                            switch (BarcodeActivity.this.requestCode) {

                                // On Read Mode - Open View Item Activity
                                case BarcodeActivity.REQUEST_CODE_READ:

                                    Intent iViewItem = new Intent(getApplicationContext(), ItemCrudActivity.class);

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

                        break;
                    // Unauthorized
                    case 401:
                        // TODO: Refresh user token
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(BarcodeActivity.this, "Unauthorized", Toast.LENGTH_SHORT).show();
                            }
                        });
                        break;

                    // Bad Request
                    case 400:
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(BarcodeActivity.this, "Bad Request", Toast.LENGTH_SHORT).show();
                            }
                        });
                        break;

                    // Not Found
                    case 404:

                        Log.e("ITEM_DEBUG", "Request Code: " + BarcodeActivity.this.requestCode);

                        // Take Action based on activity result
                        switch (BarcodeActivity.this.requestCode) {

                            // On Read Mode - Ignore
                            case BarcodeActivity.REQUEST_CODE_READ:

                                Log.e("ITEM_DEBUG", "Nao encontrei essa cena oh mano");
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
                        break;

                    // Internal Server Error
                    case 500:
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(BarcodeActivity.this, "Internal Server Error", Toast.LENGTH_SHORT).show();
                            }
                        });
                        break;
                }

                // Resume barcode reading
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        barcodeView.resume();
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

        Request req = this.apiHandler.item().incrementAvailable(this.wid, item.getId());
        Log.e("BarcodeActivity", "WElelel");

            this.client.newCall(req).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(BarcodeActivity.this, "API connection error.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onResponse(Call call, Response response) {

                    switch (response.code()) {

                        // Success on the request
                        case 200:

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(BarcodeActivity.this, "1 Unit Added", Toast.LENGTH_SHORT).show();
                                }
                            });

                            break;

                        // Success on the request
                        case 201:

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(BarcodeActivity.this, "1 Unit Added", Toast.LENGTH_SHORT).show();
                                }
                            });

                            break;

                        // Unauthorized
                        case 401:
                            // TODO: Refresh user token
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(BarcodeActivity.this, "Unauthorized", Toast.LENGTH_SHORT).show();
                                }
                            });
                            Log.e("BarcodeActivity", "Unauthorized");
                            break;

                        // Bad Request
                        case 400:
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(BarcodeActivity.this, "Bad Request", Toast.LENGTH_SHORT).show();
                                }
                            });
                            Log.e("BarcodeActivity", "Bad Request");
                            break;

                        // Internal Server Error
                        case 500:
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(BarcodeActivity.this, "Internal Server Error", Toast.LENGTH_SHORT).show();
                                }
                            });
                            Log.e("BarcodeActivity", "Internal Server Error");
                            break;

                        default:
                            Log.e("BarcodeActivity", "Unhandled HTTP status code: " + response.code());
                    }
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
