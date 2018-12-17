package pt.simov.stockit.core.api;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import pt.simov.stockit.core.ApiHandler;

public class UserController {

    /**
     * The resource base url.
     */
    private String url = "/user";

    /**
     * Json media type.
     */
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    /**
     * Constructor.
     */
    public UserController() {
    }

    /**
     * User account creation.
     *
     * @return Request The request object to be queued on the request queue.
     */
    public Request newUser(String username, String password, String email) throws JSONException {

        // Create JSON body
        JSONObject json = new JSONObject();

        json.put("username", username);
        json.put("password", password);
        json.put("email", email);

        // Create request
        Request request = new Request.Builder()
                .url(ApiHandler.getInstance().getBaseUrl() + this.url)
                .post(RequestBody.create(JSON, json.toString()))
                .build();

        return request;
    }
}
