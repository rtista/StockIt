package pt.simov.stockit.core.domain;

import java.util.Date;

public class AuthToken {

    /**
     * The authentication method.
     */
    private String method;

    /**
     * The authentication token.
     */
    private String token;

    /**
     * The authentication token expiration date.
     */
    private Date expiration;

    /**
     * Constructor.
     *
     * @param method     The authentication method.
     * @param token      The authentication token.
     * @param expiration The authentication token expiration date as UNIX timestamp.
     */
    public AuthToken(String method, String token, long expiration) {

        this.method = method;
        this.token = token;
        this.expiration = new Date(expiration * 1000);
    }

    /**
     * Returns the authentication method.
     *
     * @return String
     */
    public String getMethod() {

        return this.method;
    }

    /**
     * Returns the authentication token.
     *
     * @return String
     */
    public String getToken() {

        return this.token;
    }

    /**
     * Returns whether the token is expired or not.
     *
     * @return boolean
     */
    public boolean isExpired() {

        return this.expiration.before(new Date());
    }
}
