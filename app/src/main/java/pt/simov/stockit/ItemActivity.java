package pt.simov.stockit;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

public class ItemActivity extends AppCompatActivity {

    private Intent in;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);
        in = getIntent();
        int request =  in.getIntExtra("REQUEST_CODE",0);

        EditText description = findViewById(R.id.item_description);
        EditText quantity = findViewById(R.id.item_quantity);
        EditText min_quantity = findViewById(R.id.item_min_quantity);
        Button btn = findViewById(R.id.btn_action);
        //IF VIEWING
        switch(request){
            case 1:     //VIEW
                description.setEnabled(false);
                quantity.setEnabled(false);
                min_quantity.setEnabled(false);
                //TODO get info
                description.setText("Description");
                quantity.setText("1000");
                min_quantity.setText("20");
                btn.setText("Edit");
                break;
            case 2:     //EDIT
                description.setEnabled(true);
                quantity.setEnabled(true);
                min_quantity.setEnabled(true);
                //TODO get info
                description.setText("Description");
                quantity.setText("1000");
                min_quantity.setText("20");
                btn.setText("Confirm");
                break;
            case 3:     //NEW
                description.setEnabled(true);
                quantity.setEnabled(true);
                min_quantity.setEnabled(true);
                description.setText("");
                quantity.setText("");
                min_quantity.setText("");
                btn.setText("Delete");
                break;
        }
    }
}
