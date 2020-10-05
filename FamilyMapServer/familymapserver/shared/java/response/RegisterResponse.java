package response;

/**
 * The response returned when a RegisterService object calls the register method.
 */
public class RegisterResponse extends Response {
    /**
     * A newly generated authToken that the server will use to identify the user on subsequent requests.
     */
    private String authToken;

    /**
     * The username of the user that is registering.
     */
    private String userName;

    /**
     * the identifier of the person that represents the user.
     */
    private String personID;

    public RegisterResponse(String authToken, String userName, String personID, boolean success) {
        this.authToken = authToken;
        this.userName = userName;
        this.personID = personID;
        this.setSuccess(success);
    }

    public RegisterResponse() {

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
