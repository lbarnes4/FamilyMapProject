package request;

/**
 * An object containing information that is used to log a user in.
 */
public class LoginRequest {
    /**
     * The userName of the user.
     */
    private String userName;

    /**
     * The password of the user.
     */
    private String password;

    public LoginRequest(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }

    public String getUserName() { return userName; }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassWord() {
        return password;
    }

    public void setPassWord(String passWord) {
        this.password = passWord;
    }
}
