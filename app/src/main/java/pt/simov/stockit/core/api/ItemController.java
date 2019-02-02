package pt.simov.stockit.core.api;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import pt.simov.stockit.core.ApiHandler;

public class ItemController {

    /**
     * The resource base url.
     */
    private String url = "/warehouse/{wid}/item";

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
    public ItemController(String authorization) {

        this.authorization = authorization;
    }

    /**
     * Returns all the user's warehouse id item.
     *
     * @param wid The warehouse id.
     * @return Request The request object to be queued on the request queue.
     */
    public Request get(int wid) {

        // Create request
        Request request = new Request.Builder()
                .addHeader("Authorization", this.authorization)
                .url(ApiHandler.getInstance().getBaseUrl() + this.url.replace("{wid}", String.valueOf(wid)))
                .get()
                .build();

        return request;
    }

    /**
     * Returns all the user's warehouse id item.
     *
     * @param wid The warehouse id.
     * @param barcode The item's barcode.
     * @return Request The request object to be queued on the request queue.
     */
    public Request get(int wid, String barcode) {

        String url = new StringBuilder()
                .append(ApiHandler.getInstance().getBaseUrl())
                .append(this.url.replace("{wid}", String.valueOf(wid)))
                .append("?barcode=").append(barcode).toString();

        // Create request
        Request request = new Request.Builder()
                .addHeader("Authorization", this.authorization)
                .url(url)
                .get()
                .build();

        return request;
    }

    /**
     * Creates an item in the given warehouse.
     *
     * @param wid          The warehouse id.
     * @param name         The item name.
     * @param description  The item description.
     * @param barcode      The barcode of the item.
     * @param available    The available quantity.
     * @param allocated    The allocated quantity.
     * @param alert        The alert quantity.
     * @return Request The request object to be queued on the request queue.
     * @throws JSONException
     */
    public Request post(int wid, String name, String description, String barcode,
                        int available, int allocated, int alert) throws JSONException {

        // Allow empty barcode
        if (barcode == null) {
            barcode = "";
        }

        // Create JSON body
        JSONObject json = new JSONObject();

        json.put("name", name);
        json.put("description", description);
        json.put("barcode", barcode);
        json.put("available", available);
        json.put("allocated", allocated);
        json.put("alert", alert);

        // Create request
        Request request = new Request.Builder()
                .addHeader("Authorization", this.authorization)
                .url(ApiHandler.getInstance().getBaseUrl() + this.url.replace("{wid}", String.valueOf(wid)))
                .post(RequestBody.create(JSON, json.toString()))
                .build();

        return request;
    }

    /**
     * Updates an item in a user warehouse.
     *
     * @param wid    The warehouse id.
     * @param id     The item id.
     * @param values A mapping of the body parameters to its values.
     * @return Request The request object to be queued on the request queue.
     * @throws JSONException
     */
    public Request patch(int wid, int id, HashMap<String, Object> values) throws JSONException {

        // Create JSON body
        JSONObject json = new JSONObject();

        // For each value to be modified
        for (String s : values.keySet()) {

            json.put(s, values.get(s));
        }

        // Create request
        Request request = new Request.Builder()
                .addHeader("Authorization", this.authorization)
                .url(ApiHandler.getInstance().getBaseUrl() + this.url.replace("{wid}", String.valueOf(wid)) + "/" + id)
                .patch(RequestBody.create(JSON, json.toString()))
                .build();

        return request;
    }

    /**
     * Deletes an item from a user warehouse.
     *
     * @param wid The warehouse id.
     * @param id  The item id.
     * @return Request The request object to be queued on the request queue.
     */
    public Request delete(int wid, int id) {

        // Create request
        Request request = new Request.Builder()
                .addHeader("Authorization", this.authorization)
                .url(ApiHandler.getInstance().getBaseUrl() + this.url.replace("{wid}", String.valueOf(wid)) + "/" + id)
                .delete()
                .build();

        return request;
    }

    /**
     * Increments an item's available units quantity.
     *
     * @param wid The warehouse id.
     * @param id  The item id.
     * @return Request The request object to be queued on the request queue.
     */
    public Request incrementAvailable(int wid, int id) {

        // Create JSON body
        JSONObject json = new JSONObject();



        String url = new StringBuilder()
                .append(ApiHandler.getInstance().getBaseUrl())
                .append(this.url.replace("{wid}", String.valueOf(wid)))
                .append("/").append(id).append("/units?unit=available").toString();

        // Create request
        Request request = new Request.Builder()
                .addHeader("Authorization", this.authorization)
                .url(url)
                .post(RequestBody.create(JSON, json.toString()))
                .build();

        return request;
    }
}
