package pt.simov.stockit.core.api;


import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import pt.simov.stockit.core.ApiHandler;
import pt.simov.stockit.core.domain.AuthToken;


public class AuthController {

    /**
     * The resource base url.
     */
    private String url = "/auth/token";

    /**
     * The authentication token.
     */
    private AuthToken authToken;

    /**
     * Json media type.
     */
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    /**
     * Constructor.
     */
    public AuthController() {

        this.authToken = null;
    }

    /**
     * Set the authentication token.
     *
     * @param authToken The authentication token.
     */
    public void setAuthToken(AuthToken authToken) {

        this.authToken = authToken;
    }

    /**
     * The Authorization HTTP header value.
     *
     * @return String
     */
    public String getAuthorization() {

        return this.authToken == null ? null
                : this.authToken.getMethod() + " " + this.authToken.getToken();
    }

    /**
     * Returns true when the token is expired.
     *
     * @return boolean
     */
    public boolean isTokenExpired() {

        if (this.authToken == null) {

            return true;
        }

        return this.authToken.isExpired();
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
