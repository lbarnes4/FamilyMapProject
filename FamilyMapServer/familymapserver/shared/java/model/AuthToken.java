package model;

import java.util.Objects;

/**
 * A unique authorization token generated when a user logs in.
 * Subsequent requests sent from the client to the server
 * include the auth token so the server can determine which user is making the request.
 */
public class AuthToken {
    /**
     * The value of the AuthToken.
     */
    private String token;

    public AuthToken(String token, String userName) {
        this.token = token;
        this.userName = userName;
    }

    /**
     * The userName of the User to which the AuthToken belongs
     */
    private String userName;

    public AuthToken() {

    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AuthToken authToken = (AuthToken) o;
        return Objects.equals(token, authToken.token) &&
                Objects.equals(userName, authToken.userName);
    }
}
