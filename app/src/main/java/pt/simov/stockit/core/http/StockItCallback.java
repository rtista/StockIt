package pt.simov.stockit.core.http;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public abstract class StockItCallback implements Callback {

    /**
     * On connection to service failure.
     *
     * @param call The call in place.
     * @param e    The exception raised.
     */
    @Override
    public void onFailure(Call call, IOException e) {

        Log.e("AUTH_FAIL", "Exception: " + e.getMessage());
    }

    /**
     * On connection to service success.
     *
     * @param call     The call in place.
     * @param response The response obtained.
     */
    @Override
    public void onResponse(Call call, Response response) {

        // Get HTTP response code
        int code = response.code();
        JSONObject body = null;

        // Get response body
        try {
            body = new JSONObject(response.body().string());

        } catch (JSONException | IOException e) {

            Log.e("STOCK_IT_CALLBACK_EXCPT", "Exception: " + e.getMessage());
        }

        // Switch response code
        switch (code) {

            // OK
            case 200:
                onOk(body);
                break;

            // Created
            case 201:
                onCreated(body);
                break;

            // Bad Request
            case 400:
                onBadRequest(body);
                break;

            // Unauthorized
            case 401:
                onUnauthorized(body);
                break;

            // Forbidden
            case 403:
                onForbidden(body);
                break;

            // Not Found
            case 404:
                onNotFound(body);
                break;

            // Internal Server Error
            case 500:
                onInternalServerError(body);
                break;

            // Unmapped Response Code
            default:
                onUnmappedResponseCode(code, body);
                break;
        }
    }

    /**
     * On 200 OK response code.
     *
     * @param body The response body.
     */
    public void onOk(JSONObject body) {

        Log.e("STOCK_IT_CALLBACK_200", "Body: " + body.toString());
    }

    /**
     * On 201 Created response code.
     *
     * @param body The response body.
     */
    public void onCreated(JSONObject body) {

        Log.e("STOCK_IT_CALLBACK_201", "Body: " + body.toString());
    }

    /**
     * On 400 bad request response code.
     *
     * @param body The response body.
     */
    public void onBadRequest(JSONObject body) {

        Log.e("STOCK_IT_CALLBACK_400", "Body: " + body.toString());
    }

    /**
     * On 401 Unauthorized response code.
     *
     * @param body The response body.
     */
    public void onUnauthorized(JSONObject body) {

        Log.e("STOCK_IT_CALLBACK_401", "Body: " + body.toString());
    }

    /**
     * On 403 Forbidden response code.
     *
     * @param body The response body.
     */
    public void onForbidden(JSONObject body) {

        Log.e("STOCK_IT_CALLBACK_403", "Body: " + body.toString());
    }

    /**
     * On 404 Not Found response code.
     *
     * @param body The response body.
     */
    public void onNotFound(JSONObject body) {

        Log.e("STOCK_IT_CALLBACK_404", "Body: " + body.toString());
    }

    /**
     * On 500 Internal Server Error response code.
     *
     * @param body The response body.
     */
    public void onInternalServerError(JSONObject body) {

        Log.e("STOCK_IT_CALLBACK_500", "Body: " + body.toString());
    }

    /**
     * On unmapped response codes.
     *
     * @param code The response code.
     * @param body The response body.
     */
    public void onUnmappedResponseCode(int code, JSONObject body) {

        Log.e("STOCK_IT_CALLBACK", "Response Code: " + code + " Body: " + body.toString());
    }
}
