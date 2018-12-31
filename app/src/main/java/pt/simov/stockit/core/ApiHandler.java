package pt.simov.stockit.core;

import org.json.JSONException;

import java.util.HashMap;

import okhttp3.Request;
import pt.simov.stockit.core.api.AuthController;
import pt.simov.stockit.core.api.UserController;
import pt.simov.stockit.core.api.WarehouseController;

public class ApiHandler {

    /**
     * Singleton Instance
     */
    private static ApiHandler instance;

    /**
     * The API URL location.
     * Todo: Make this config changeable
     */
    // private final String url = "http://172.18.158.14:8000/api";
    private final String url = "http://192.168.1.5:8000/api";

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
     * @throws JSONException
     */
    public Request authenticate(String username, String password) throws JSONException {

        AuthController controller = new AuthController();

        return controller.bearer(username, password);
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

    /**
     * Create a user account.
     * @param username The username.
     * @param password The password.
     * @param email The email.
     * @return Request to be added to the queue.
     * @throws JSONException
     */
    public Request createAccount(String username, String password, String email) throws JSONException {

        UserController controller = new UserController();

        return controller.newUser(username, password, email);
    }

    /**
     * Returns the user warehouses.
     * @return Request to be added to the queue.
     */
    public Request getWarehouses() {

        WarehouseController controller = new WarehouseController(this.getAuthToken());

        return controller.get();
    }

    /**
     * Creates a warehouse in the user account.
     * @param name The warehouse name
     * @param description The warehouse description
     * @param latitude The latitude of the warehouse
     * @param longitude The longitude of the warehouse
     * @return Request The request object to be queued on the request queue.
     * @throws JSONException
     */
    public Request createWarehouse(String name, String description, float latitude,
                                   float longitude) throws JSONException {

        WarehouseController controller = new WarehouseController(this.getAuthToken());

        return controller.post(name, description, latitude, longitude);
    }

    /**
     * Creates a warehouse in the user account.
     *
     * @param name        The warehouse name
     * @param description The warehouse description
     * @return Request The request object to be queued on the request queue.
     * @throws JSONException
     */
    public Request createWarehouse(String name, String description) throws JSONException {

        WarehouseController controller = new WarehouseController(this.getAuthToken());

        return controller.post(name, description);
    }

    /**
     * Creates a warehouse in the user account.
     *
     * @param id     The warehouse id
     * @param values The parameters to be modified.
     * @return Request The request object to be queued on the request queue.
     * @throws JSONException
     */
    public Request editWarehouse(int id, HashMap<String, Object> values) throws JSONException {

        WarehouseController controller = new WarehouseController(this.getAuthToken());

        return controller.patch(id, values);
    }

    /**
     * Creates a warehouse in the user account.
     *
     * @param id The warehouse id
     * @return Request The request object to be queued on the request queue.
     */
    public Request deleteWarehouse(int id) {

        WarehouseController controller = new WarehouseController(this.getAuthToken());

        return controller.delete(id);
    }
}
