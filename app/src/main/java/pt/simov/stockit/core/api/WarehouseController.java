package pt.simov.stockit.core.api;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import pt.simov.stockit.core.ApiHandler;

public class WarehouseController {

    /**
     * The resource base url.
     */
    private String url = "/warehouse";

    /**
     * The authorization header value.
     */
    private String authorization;

    /**
     * Json media type.
     */
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    /**
     * Constructor.
     */
    public WarehouseController(String authorization) {

        this.authorization = authorization;
    }

    /**
     * Returns all the user's warehouses.
     *
     * @return Request The request object to be queued on the request queue.
     */
    public Request get() {

        // Create request
        Request request = new Request.Builder()
                .addHeader("Authorization", this.authorization)
                .url(ApiHandler.getInstance().getBaseUrl() + this.url)
                .get()
                .build();

        return request;
    }

     /**
     * Creates a warehouse in the user account.
      *
     * @param name The warehouse name
     * @param description The warehouse description
     * @param latitude The latitude of the warehouse
     * @param longitude The longitude of the warehouse
      *
     * @return Request The request object to be queued on the request queue.
     * @throws JSONException
     */
     public Request post(String name, String description,
                         float latitude, float longitude) throws JSONException {

         // Create JSON body
         JSONObject json = new JSONObject();

         json.put("name", name);
         json.put("description", description);
         json.put("latitude", latitude);
         json.put("longitude", longitude);

         // Create request
         Request request = new Request.Builder()
                 .addHeader("Authorization", this.authorization)
                 .url(ApiHandler.getInstance().getBaseUrl() + this.url)
                 .post(RequestBody.create(JSON, json.toString()))
                 .build();

         return request;
     }

    /**
     * Creates a warehouse in the user account.
     *
     * @param name        The warehouse name
     * @param description The warehouse description
     * @return Request The request object to be queued on the request queue.
     * @throws JSONException
     */
    public Request post(String name, String description) throws JSONException {

        // Create JSON body
        JSONObject json = new JSONObject();

        json.put("name", name);
        json.put("description", description);

        // Create request
        Request request = new Request.Builder()
                .addHeader("Authorization", this.authorization)
                .url(ApiHandler.getInstance().getBaseUrl() + this.url)
                .post(RequestBody.create(JSON, json.toString()))
                .build();

        return request;
    }

    /**
     * Creates a warehouse in the user account.
     *
     * @param id     The warehouse id.
     * @param values A mapping of the body parameters to its values.
     * @return Request The request object to be queued on the request queue.
     * @throws JSONException
     */
    public Request patch(int id, HashMap<String, Object> values) throws JSONException {

        // Create JSON body
        JSONObject json = new JSONObject();

        // For each value to be modified
        for (String s : values.keySet()) {

            json.put(s, values.get(s));
        }

        // Create request
        Request request = new Request.Builder()
                .addHeader("Authorization", this.authorization)
                .url(ApiHandler.getInstance().getBaseUrl() + this.url + "/" + id)
                .patch(RequestBody.create(JSON, json.toString()))
                .build();

        return request;
    }

    /**
     * Deletes a warehouse from the user account.
     *
     * @param id The warehouse id
     * @return Request The request object to be queued on the request queue.
     */
    public Request delete(int id) {

        // Create request
        Request request = new Request.Builder()
                .addHeader("Authorization", this.authorization)
                .url(ApiHandler.getInstance().getBaseUrl() + this.url + "/" + id)
                .delete()
                .build();

        return request;
    }
}
