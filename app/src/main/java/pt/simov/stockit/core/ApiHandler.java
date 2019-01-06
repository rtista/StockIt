package pt.simov.stockit.core;

import pt.simov.stockit.core.api.AuthController;
import pt.simov.stockit.core.api.ItemController;
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
    private final String url = "http://192.168.1.72:8000/api";

    /**
     * The handler's item controller.
     */
    private AuthController authController;

    /**
     * The handler's item controller.
     */
    private UserController userController;

    /**
     * The handler's item controller.
     */
    private WarehouseController warehouseController;

    /**
     * The handler's item controller.
     */
    private ItemController itemController;

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
     * Private constructor.
     */
    private ApiHandler() {

        this.authController = new AuthController();
        this.userController = new UserController();
    }

    /**
     * Returns the singleton instance.
     * @return ApiHandler
     */
    public String getBaseUrl() {

        return instance.url;
    }

    /**
     * Returns the Authentication Controller.
     *
     * @return AuthController
     */
    public AuthController auth() {

        return this.authController;
    }

    /**
     * Returns the User Controller.
     *
     * @return UserController
     */
    public UserController user() {

        return this.userController;
    }

    /**
     * Returns the Warehouse Controller.
     *
     * @return WarehouseController
     */
    public WarehouseController warehouse() {

        return new WarehouseController(this.authController.getAuthorization());
    }

    /**
     * Returns the Item Controller.
     *
     * @return ItemController
     */
    public ItemController item() {

        return new ItemController(this.authController.getAuthorization());
    }
}
