package pt.simov.stockit;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        setTitle(R.string.title_login);

        TextView tv = findViewById(R.id.link_signup);
        tv.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                try {
                    Intent i=new Intent(LoginActivity.this,SignupActivity.class);
                    startActivity(i);
                } catch (ActivityNotFoundException ex) {
                    Toast.makeText(LoginActivity.this, "Error in Login", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Button loginBtn = findViewById(R.id.btn_login);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                EditText username = (EditText) findViewById(R.id.input_username);
                EditText password = (EditText) findViewById(R.id.input_password);

                //TODO authenticate

                try {
                    Intent i=new Intent(LoginActivity.this,WarehousesTableActivity.class);
                    startActivity(i);
                    finish();
                } catch (ActivityNotFoundException ex) {
                    Toast.makeText(LoginActivity.this, "Error in Login", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
