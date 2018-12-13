package pt.simov.stockit;

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
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
                setTitle(R.string.title_view_warehouse);
                //TODO get info
                tv.setText("View Warehouse");
                name_et.setText(name);
                description_et.setText(description);
                lat_et.setText(lat);
                lon_et.setText(lon);
                break;
            case 2:     //EDIT
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
            case 3:     //NEW
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.warehouse_optionsmenu, menu);
        return true;
    }

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
}
