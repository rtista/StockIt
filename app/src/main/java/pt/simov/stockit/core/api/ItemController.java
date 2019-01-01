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
     * Creates an item in the given warehouse.
     *
     * @param wid          The warehouse id.
     * @param name         The item name.
     * @param description  The item description.
     * @param quantity     The item quantity.
     * @param section      The item warehouse section.
     * @param barcode      The barcode of the item.
     * @param min_quantity The item alert quantity.
     * @return Request The request object to be queued on the request queue.
     * @throws JSONException
     */
    public Request post(int wid, String name, String description, int quantity,
                        String section, String barcode, int min_quantity) throws JSONException {

        // Default quantity is 1
        if (quantity == 0) {
            quantity = 1;
        }

        // Allow empty section
        if (section == null) {
            section = "";
        }

        // Allow empty barcode
        if (barcode == null) {
            barcode = "";
        }

        // Create JSON body
        JSONObject json = new JSONObject();

        json.put("name", name);
        json.put("description", description);
        json.put("quantity", quantity);
        json.put("section", section);
        json.put("barcode", barcode);
        json.put("min_quantity", min_quantity);

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
}
