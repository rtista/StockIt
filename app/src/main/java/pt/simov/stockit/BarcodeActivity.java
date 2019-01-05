package pt.simov.stockit;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.ResultPoint;
import com.google.zxing.client.android.BeepManager;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.journeyapps.barcodescanner.DefaultDecoderFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import pt.simov.stockit.core.ApiHandler;
import pt.simov.stockit.core.domain.Inventory;
import pt.simov.stockit.core.http.HttpClient;

public class BarcodeActivity extends AppCompatActivity {

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
    public static final int REQUEST_CODE_READ= 1;
    public static final int REQUEST_CODE_WRITE = 2;

    /**
     * Request code
     */
    private int requestCode;

    /**
     * The warehouse id.
     */
    private int wid;

    private Inventory itemFound;

    private DecoratedBarcodeView barcodeView;
    private BeepManager beepManager;
    private String barcodeValue;
    private TextView lastValue;
    private TextView labelLastValue;
    private ImageView imageView;

    private BarcodeCallback callback = new BarcodeCallback() {
        @Override
        public void barcodeResult(BarcodeResult result) {

            switch (requestCode) {

                case REQUEST_CODE_READ:
                    if (result.getText() == null || result.getText().equals(barcodeValue)) {
                        // Prevent duplicate scans
                        return;
                    }

                    barcodeValue = result.getText();

                    beepManager.playBeepSoundAndVibrate();

                    verifyValue();
                    SystemClock.sleep(750);

                    if(itemFound != null){

                        Intent iViewItem = new Intent(getApplicationContext(), InventoryCrudActivity.class);

                        iViewItem.putExtra("WAREHOUSE_ID", wid);
                        iViewItem.putExtra("NAME", itemFound.getName());
                        iViewItem.putExtra("DESCRIPTION", itemFound.getDescription());
                        iViewItem.putExtra("QUANTITY", itemFound.getQuantity());
                        iViewItem.putExtra("BARCODE", itemFound.getBarcode());
                        iViewItem.putExtra("SECTION", itemFound.getSection());
                        iViewItem.putExtra("MIN_QUANTITY", itemFound.getMin_quantity());

                        iViewItem.putExtra("REQUEST_CODE", InventoryCrudActivity.REQUEST_CODE_VIEW);
                        startActivityForResult(iViewItem, InventoryCrudActivity.REQUEST_CODE_VIEW);

                        labelLastValue.setText("Last Value:");
                        lastValue.setText(barcodeValue);

                        //Added preview of scanned barcode
                        imageView.setImageBitmap(result.getBitmapWithResultPoints(Color.YELLOW));

                    }else{
                        labelLastValue.setText("Unknown Barcode:");
                        lastValue.setText(barcodeValue);

                        //Added preview of scanned barcode
                        imageView.setImageBitmap(result.getBitmapWithResultPoints(Color.YELLOW));
                    }
                    break;

                case REQUEST_CODE_WRITE:

                    barcodeValue = result.getText();

                    beepManager.playBeepSoundAndVibrate();

                    verifyValue();
                    SystemClock.sleep(750);

                    if(itemFound != null){

                        addQuantity();
                        SystemClock.sleep(500);

                        labelLastValue.setText("Last Value:");
                        lastValue.setText(barcodeValue);
                        //Added preview of scanned barcode
                        imageView.setImageBitmap(result.getBitmapWithResultPoints(Color.YELLOW));

                    }else{

                        Intent iAddItem = new Intent(BarcodeActivity.this, InventoryCrudActivity.class);

                        iAddItem.putExtra("WAREHOUSE_ID", wid);
                        iAddItem.putExtra("BARCODE", barcodeValue);
                        iAddItem.putExtra("REQUEST_CODE", InventoryCrudActivity.REQUEST_CODE_ADD);
                        startActivityForResult(iAddItem, InventoryCrudActivity.REQUEST_CODE_ADD);

                        labelLastValue.setText("Last Value:");
                        lastValue.setText(barcodeValue);

                        //Added preview of scanned barcode
                        imageView.setImageBitmap(result.getBitmapWithResultPoints(Color.YELLOW));

                    }
                    break;
            }
        }
        @Override
        public void possibleResultPoints(List<ResultPoint> resultPoints) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode);

        wid = this.getIntent().getIntExtra("WAREHOUSE_ID", 0);
        requestCode = this.getIntent().getIntExtra("REQUEST_CODE", 0);

        switch (requestCode){
            case REQUEST_CODE_READ:
                setTitle(R.string.title_activity_barcode_Read);
                break;
            case REQUEST_CODE_WRITE:
                setTitle(R.string.title_activity_barcode_Write);
        }

        labelLastValue = findViewById(R.id.labelLastValue);
        lastValue = findViewById(R.id.lastValue);

        barcodeView = findViewById(R.id.barcode_scanner);
        Collection<BarcodeFormat> formats = Arrays.asList(BarcodeFormat.CODE_39);
        barcodeView.getBarcodeView().setDecoderFactory(new DefaultDecoderFactory(formats));
        barcodeView.initializeFromIntent(this.getIntent());
        barcodeView.decodeContinuous(callback);

        beepManager = new BeepManager(this);

        imageView = findViewById(R.id.barcodePreview);
    }

    @Override
    protected void onResume() {
        super.onResume();

        barcodeView.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();

        barcodeView.pause();
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

                    Toast.makeText(this, "Item created successfully.", Toast.LENGTH_SHORT).show();
                }

                break;

            default:
                break;
        }
    }

    /**
     * Verify if a item exists in the database.
     *
     * @return boolean
     */
    private void verifyValue() {

        Request req = this.apiHandler.item().get(this.wid);

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
                boolean found;

                // Success on the request
                switch (response.code()) {
                    case 200:
                        // Read Json Object from response body
                        try {

                            JSONArray items = new JSONObject(response.body().string()).getJSONArray("items");
                            found = false;
                            for (int i = 0; i < items.length(); i++) {
                                JSONObject it = items.getJSONObject(i);
                                if (it.getString("barcode").equals(barcodeValue)){
                                    found = true;
                                    itemFound = new Inventory(it.getInt("id"), it.getString("name"), it.getString("description"), it.getInt("quantity"), it.getString("section"), it.getString("barcode"), it.getInt("min_quantity"));
                                }
                            }
                            if(!found){
                                itemFound = null;
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
            }

        });
    }

    private void addQuantity() {

        HashMap<String, Object> map = new HashMap<>();
        int itemQuantity = itemFound.getQuantity()+1;

        map.put("quantity", (itemQuantity));

        try {
            Request req = this.apiHandler.item().patch(this.wid, itemFound.getId(), map);

            this.handleRequest(req);

        } catch (JSONException e) {

            Log.e("Add_Quantity", "JSON Exception: " + e.getMessage());
        }

    }

    private void handleRequest(Request req) {

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
                        break;

                    default:
                        Log.e("BarcodeActivity", "Unhandled HTTP status code: " + response.code());
                }
            }
        });
    }

}
