package pt.simov.stockit;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ItemActivity extends AppCompatActivity {

    private Intent in;
    String description, quantity, min_quantity, name;
    EditText name_et, description_et, quantity_et, min_quantity_et;
    Button btn;
    int request;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);
        in = getIntent();
        request =  in.getIntExtra("REQUEST_CODE",1);

        name_et = findViewById(R.id.item_name);
        description_et = findViewById(R.id.item_description);
        quantity_et = findViewById(R.id.item_quantity);
        min_quantity_et = findViewById(R.id.item_min_quantity);
        btn = findViewById(R.id.btn_action);

        name = in.getStringExtra("NAME");
        description = in.getStringExtra("DESCRIPTION");
        quantity = in.getStringExtra("QUANTITY");
        min_quantity = in.getStringExtra("MIN_QUANTITY");
        //IF VIEWING
        setActivity(request);
    }

    private void setActivity(int request){
        switch(request){
            case 1:     //VIEW
                name_et.setEnabled(false);
                description_et.setEnabled(false);
                quantity_et.setEnabled(false);
                min_quantity_et.setEnabled(false);
                setTitle(R.string.title_view_item);
                btn.setVisibility(View.GONE);
                //TODO get info
                name_et.setText(name);
                description_et.setText(description);
                quantity_et.setText(quantity);
                min_quantity_et.setText(min_quantity);
                break;
            case 2:     //EDIT
                name_et.setEnabled(true);
                description_et.setEnabled(true);
                quantity_et.setEnabled(true);
                min_quantity_et.setEnabled(true);
                setTitle(R.string.title_edit_item);
                btn.setVisibility(View.VISIBLE);
                //TODO get info
                name_et.setText(name);
                description_et.setText(description);
                quantity_et.setText(quantity);
                min_quantity_et.setText(min_quantity);
                btn.setText("Confirm");
                buttonListener(2);
                break;
            case 3:     //NEW
                name_et.setEnabled(true);
                description_et.setEnabled(true);
                quantity_et.setEnabled(true);
                min_quantity_et.setEnabled(true);
                btn.setVisibility(View.VISIBLE);
                description_et.setText("");
                setTitle(R.string.title_add_item);
                quantity_et.setText("");
                name_et.setText("");
                min_quantity_et.setText("");
                btn.setText("Add Item");
                buttonListener(3);
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.item_optionsmenu, menu);
        MenuInflater menuInflater = getMenuInflater();
        switch (request){
            case 1:
                menuInflater.inflate(R.menu.item_optionsmenu, menu);
                break;
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {// Handle item selection
        switch (item.getItemId()) {
            case R.id.wom_view_map:
                Toast.makeText(this, "View in Map",Toast.LENGTH_LONG).show();
                return true;
            case R.id.iom_edit:

                // Em vez de setActivity a activity é recreada pelo intent
                Intent i=new Intent(this,ItemActivity.class);
                i.putExtra("NAME", name);
                i.putExtra("DESCRIPTION", description);
                i.putExtra("QUANTITY", quantity);
                i.putExtra("MIN_QUANTITY", min_quantity);
                i.putExtra("REQUEST_CODE",2);
                startActivity(i);
                finish();

                /*setActivity(2);
                btn.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View arg0) {
                        Toast.makeText(ItemActivity.this, "Save",Toast.LENGTH_LONG).show();
                        name = name_et.getText().toString();
                        description = description_et.getText().toString();
                        quantity = quantity_et.getText().toString();
                        min_quantity = min_quantity_et.getText().toString();
                        //TODO save edited information
                        setActivity(1);
                    }
                });*/
                return true;
            case R.id.iom_delete:
                new AlertDialog.Builder(this)
                        .setTitle("Delete Warehouse")
                        .setMessage("Do you want to remove this item from this warehouse?")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                Intent i = new Intent(ItemActivity.this, InventoryActivity.class);
                                //TODO remove warehouse from database
                                Toast.makeText(ItemActivity.this, "Item removed from Warehouse", Toast.LENGTH_SHORT).show();
                                startActivity(i);
                                finish();
                            }})
                        .setNegativeButton(android.R.string.no, null).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void buttonListener(int request){
        switch(request) {
            case 2:
                btn.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View arg0) {
                        Toast.makeText(ItemActivity.this, "Save", Toast.LENGTH_LONG).show();
                        name = name_et.getText().toString();
                        description = description_et.getText().toString();
                        quantity = quantity_et.getText().toString();
                        min_quantity = min_quantity_et.getText().toString();
                        //TODO save edited information
    //                Em vez de setActivity, esta é recriada por intent
    //                setActivity(1);
                        Intent i = new Intent(ItemActivity.this, ItemActivity.class);
                        i.putExtra("NAME", name);
                        i.putExtra("DESCRIPTION", description);
                        i.putExtra("QUANTITY", quantity);
                        i.putExtra("MIN_QUANTITY", min_quantity);
                        i.putExtra("REQUEST_CODE", 1);
                        startActivity(i);
                        finish();
                    }
                });
                break;
            case 3:
                btn.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View arg0) {
                        Toast.makeText(ItemActivity.this, "Save", Toast.LENGTH_LONG).show();
                        name = name_et.getText().toString();
                        description = description_et.getText().toString();
                        quantity = quantity_et.getText().toString();
                        min_quantity = min_quantity_et.getText().toString();
                        //TODO save edited information
                    }
                });
                break;
        }
    }
}
