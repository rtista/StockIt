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

public class SignupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        EditText name = findViewById(R.id.input_username);


        Button signupBtn = findViewById(R.id.btn_signup);
        signupBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                EditText username = (EditText) findViewById(R.id.input_username);
                EditText password = (EditText) findViewById(R.id.input_password);
                EditText email = (EditText) findViewById(R.id.input_email);

                //TODO authenticate

                try {
                    Intent i=new Intent(SignupActivity.this,LoginActivity.class);
                    startActivity(i);
                    finish();
                } catch (ActivityNotFoundException ex) {
                    Toast.makeText(SignupActivity.this, "Error in Login", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
