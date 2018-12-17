package pt.simov.stockit.core;

import pt.simov.stockit.core.api.AuthController;

import org.json.JSONException;

import okhttp3.Request;
import pt.simov.stockit.core.api.UserController;

public class ApiHandler {

    /**
     * Singleton Instance
     */
    private static ApiHandler instance;

    /**
     * The API URL location.
     * Todo: Make this config changeable
     */
    private final String url = "http://192.168.1.92:8000/api";

    /**
     * The user authentication token.
     */
    private String authToken = null;

    /**
     * Private constructor.
     */
    private ApiHandler() {
    }

    /**
     * Returns the singleton instance.
     * @return ApiHandler
     */
    public static ApiHandler getInstance() {

        if (instance == null) {

            instance = new ApiHandler();
        }

        return instance;
    }

    /**
     * Returns the singleton instance.
     * @return ApiHandler
     */
    public String getBaseUrl() {

        return instance.url;
    }

    /**
     * Authenticates the user against the API, returns the bearer token.
     * @param username The username for the user at hand.
     * @param password The password for the given username.
     * @return boolean
     */
    public Request authenticate(String username, String password) throws JSONException {

        AuthController controller = new AuthController();

        return controller.bearer(username, password);
    }

    public Request createAccount(String username, String password, String email) throws JSONException {

        UserController controller = new UserController();

        return controller.newUser(username, password, email);
    }

    /**
     * Set the user authentication token.
     *
     * @param authToken The user authentication token.
     */
    public void setAuthToken(String authToken) {

        instance.authToken = authToken;
    }

    /**
     * Get the user authentication token.
     *
     * @return The user authenticaton token.
     */
    public String getAuthToken() {

        return instance.authToken;
    }
}
