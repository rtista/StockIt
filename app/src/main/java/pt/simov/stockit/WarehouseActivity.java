package pt.simov.stockit;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class WarehouseActivity extends AppCompatActivity {

    Intent in;
    String name, description, lat, lon;
    EditText name_et, description_et, lat_et, lon_et;
    Button btn;
    TextView tv;
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
        //IF VIEWING
        setActivity(request);
    }

    private void setActivity(int request){
        switch(request){
            case 1:     //VIEW
                name_et.setEnabled(false);
                description_et.setEnabled(false);
                lat_et.setEnabled(false);
                lon_et.setEnabled(false);
                btn.setVisibility(View.GONE);
                //TODO get info
                tv.setText("View Warehouse");
                name_et.setText(name);
                description_et.setText(description);
                lat_et.setText(lat);
                lon_et.setText(lon);
                btn.setText("Edit");
                break;
            case 2:     //EDIT
                name_et.setEnabled(true);
                description_et.setEnabled(true);
                lat_et.setEnabled(true);
                lon_et.setEnabled(true);
                btn.setVisibility(View.VISIBLE);
                //TODO get info
                tv.setText("Edit Warehouse");
                name_et.setText(name);
                description_et.setText(description);
                lat_et.setText(lat);
                lon_et.setText(lon);
                btn.setText("Edit");
                break;
            case 3:     //NEW
                tv.setText("New Warehouse");
                name_et.setEnabled(true);
                description_et.setEnabled(true);
                lat_et.setEnabled(true);
                lon_et.setEnabled(true);
                btn.setVisibility(View.VISIBLE);
                //TODO get info
                name_et.setText("");
                description_et.setText("");
                lat_et.setText("");
                lon_et.setText("");
                btn.setText("Edit");
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.warehouse_optionsmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {// Handle item selection
        switch (item.getItemId()) {
            case R.id.wcm_view_map:
                Toast.makeText(this, "View in Map",Toast.LENGTH_LONG).show();
                return true;
            case R.id.wcm_edit:
                setActivity(2);
                btn.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View arg0) {
                        Toast.makeText(WarehouseActivity.this, "Save",Toast.LENGTH_LONG).show();
                        setActivity(1);
                    }
                });
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
