package pt.simov.stockit.core.http;


import okhttp3.OkHttpClient;

public class HttpClient {

    /**
     * Singleton Instance
     */
    private static OkHttpClient instance;

    /**
     * Private constructor.
     */
    private HttpClient() {
    }

    /**
     * Returns the singleton instance.
     *
     * @return OkHttpClient
     */
    public static OkHttpClient getInstance() {

        if (instance == null) {

            instance = new OkHttpClient();
        }

        return instance;
    }
}
