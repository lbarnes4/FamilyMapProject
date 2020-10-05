package response;

/**
 * The response returned when a LoginService object calls the login method.
 */
public class LoginResponse extends Response {

    public LoginResponse(String authToken, String userName, String personID,boolean success,String message) {
        this.authToken = authToken;
        this.userName = userName;
        this.personID = personID;
        this.setSuccess(success);
        this.setMessage(message);
    }

    /**
     * A newly generated authToken that the server will use to identify the user on subsequent requests.
     */
    private String authToken;

    /**
     * The username of the user logging in.
     */
    private String userName;

    /**
     * The identifier of the person that represents the user.
     */
    private String personID;

    public LoginResponse() {

    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPersonID() {
        return personID;
    }

    public void setPersonID(String personID) {
        this.personID = personID;
    }
}
