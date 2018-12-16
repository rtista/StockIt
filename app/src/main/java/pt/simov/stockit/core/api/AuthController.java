package pt.simov.stockit.core.api;


import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import pt.simov.stockit.core.ApiHandler;


public class AuthController {

    /**
     * The resource base url.
     */
    private String url = "/auth/token";

    /**
     * Json media type.
     */
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    /**
     * Constructor.
     */
    public AuthController() {
    }

    /**
     * Bearer token authentication.
     *
     * @return String The authentication token or Null.
     */
    public Request bearer(String username, String password) throws JSONException {

        // Create JSON body
        JSONObject json = new JSONObject();

        json.put("username", username);
        json.put("password", password);

        // Create request
        Request request = new Request.Builder()
                .url(ApiHandler.getInstance().getBaseUrl() + this.url)
                .post(RequestBody.create(JSON, json.toString()))
                .build();

        return request;
    }
}
