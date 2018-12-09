package pt.simov.stockit.core;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;

import java.io.File;

/**
 * Singleton Volley Request Queue
 */
public class AppRequestQueue {

    /**
     * Singleton Instance
     */
    private static AppRequestQueue instance;

    /**
     * Volley request queue for HTTP requests.
     */
    private final com.android.volley.RequestQueue requestQueue;

    /**
     * Private constructor.
     */
    private AppRequestQueue() {

        // Instantiate the cache
        Cache cache = new DiskBasedCache(new File("/tmp/"), 1024 * 1024); // 1MB cap

        // Set up the network to use HttpURLConnection as the HTTP client.
        Network network = new BasicNetwork(new HurlStack());

        // Instantiate the AppRequestQueue with the cache and network.
        this.requestQueue = new com.android.volley.RequestQueue(cache, network);

        // Start the queue
        this.requestQueue.start();
    }

    /**
     * Returns the singleton instance.
     * @return AppRequestQueue
     */
    public static AppRequestQueue getInstance() {

        if (instance == null) {

            instance = new AppRequestQueue();
        }

        return instance;
    }

    /**
     * Returns the Volley Request queue for adding requests.
     * @return com.android.volley.AppRequestQueue
     */
    public com.android.volley.RequestQueue getQueue() {

        return this.requestQueue;
    }
}
