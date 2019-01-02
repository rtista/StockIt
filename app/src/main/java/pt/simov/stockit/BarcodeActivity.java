package pt.simov.stockit;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.ResultPoint;
import com.google.zxing.client.android.BeepManager;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.journeyapps.barcodescanner.DefaultDecoderFactory;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class BarcodeActivity extends AppCompatActivity {

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

    private DecoratedBarcodeView barcodeView;
    private BeepManager beepManager;
    private String lastText;
    private TextView lastValue;
    private ImageView imageView;

    private BarcodeCallback callback = new BarcodeCallback() {
        @Override
        public void barcodeResult(BarcodeResult result) {

            switch (requestCode) {
                case REQUEST_CODE_READ:
                    if (result.getText() == null || result.getText().equals(lastText)) {
                        // Prevent duplicate scans
                        return;
                    }

                    lastText = result.getText();
    //              barcodeView.setStatusText(result.getText());
                    lastValue.setText(lastText);

                    beepManager.playBeepSoundAndVibrate();

                    //Added preview of scanned barcode

                    imageView.setImageBitmap(result.getBitmapWithResultPoints(Color.YELLOW));

                    if(true){
                        //TODO if result.getText() exist in db
                        Intent iViewItem = new Intent(getApplicationContext(), InventoryCrudActivity.class);

//                        iViewItem.putExtra("WAREHOUSE_ID", this.wid);
                        iViewItem.putExtra("REQUEST_CODE", InventoryCrudActivity.REQUEST_CODE_VIEW);
                        startActivityForResult(iViewItem, InventoryCrudActivity.REQUEST_CODE_VIEW);
                    }
                    break;

                case REQUEST_CODE_WRITE:
                    lastText = result.getText();
                    lastValue.setText(lastText);

                    beepManager.playBeepSoundAndVibrate();

                    imageView.setImageBitmap(result.getBitmapWithResultPoints(Color.YELLOW));
                    if(true){
                        //TODO if result.getText() exist in db, quantity +1
                    }else{
                        //TODO else Add new item
                        Intent iAddItem = new Intent(BarcodeActivity.this, InventoryCrudActivity.class);

//                        iAddItem.putExtra("WAREHOUSE_ID", this.wid);
                        iAddItem.putExtra("REQUEST_CODE", InventoryCrudActivity.REQUEST_CODE_ADD);
                        startActivityForResult(iAddItem, InventoryCrudActivity.REQUEST_CODE_ADD);
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

        this.wid = this.getIntent().getIntExtra("WAREHOUSE_ID", 0);
        this.requestCode = this.getIntent().getIntExtra("REQUEST_CODE", 0);

        switch (requestCode){
            case REQUEST_CODE_READ:
                setTitle(R.string.title_activity_barcode_Read);
                break;
            case REQUEST_CODE_WRITE:
                setTitle(R.string.title_activity_barcode_Write);
        }

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

    public void triggerScan(View view) {
        barcodeView.decodeSingle(callback);
    }

}
