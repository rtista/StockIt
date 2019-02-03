package pt.simov.stockit;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import pt.simov.stockit.core.ApiHandler;
import pt.simov.stockit.core.http.HttpClient;
import pt.simov.stockit.core.http.StockItCallback;

public class SignupActivity extends AppCompatActivity implements View.OnClickListener {

    /**
     * HTTP client allows network requests for data.
     */
    private OkHttpClient client = HttpClient.getInstance();

    /**
     * API Handler allows StockIt backend API communication.
     */
    private final ApiHandler apiHandler = ApiHandler.getInstance();

    /**
     * Main method runs on activity start.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        setTitle(R.string.title_signup);

        TextView login = findViewById(R.id.link_login);

        login.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                try {
                    finish();
                } catch (ActivityNotFoundException ex) {
                    Toast.makeText(SignupActivity.this, "Error in Login", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Button signupBtn = findViewById(R.id.btn_signup);
        signupBtn.setOnClickListener(this);
    }

    /**
     * Create account button handling.
     *
     * @param v -The View.
     */
    @Override
    public void onClick(View v) {

        String username = ((EditText) findViewById(R.id.input_username)).getText().toString();
        String password = ((EditText) findViewById(R.id.input_password)).getText().toString();
        String email = ((EditText) findViewById(R.id.input_email)).getText().toString();

        try {
            Request req = this.apiHandler.user().post(username, password, email);

            // Execute the request
            client.newCall(req).enqueue(new StockItCallback() {

                // Account Creation Success
                @Override
                public void onCreated(JSONObject body) {

                    // Toast user with welcome
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(SignupActivity.this, "Welcome to StockIt", Toast.LENGTH_SHORT).show();
                        }
                    });

                    // Create Login Activity
                    try {
                        Intent i = new Intent(SignupActivity.this, LoginActivity.class);
                        startActivity(i);
                        finish();
                    } catch (ActivityNotFoundException ex) {
                        Toast.makeText(SignupActivity.this, "Error in Login", Toast.LENGTH_SHORT).show();
                    }
                }

                // Internal Server Error
                @Override
                public void onInternalServerError(JSONObject body) {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(SignupActivity.this, "Server communication error. Please report at github.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });

        } catch (JSONException e) {
            Toast.makeText(SignupActivity.this, "Missing fields.", Toast.LENGTH_SHORT).show();
        }
    }
}
