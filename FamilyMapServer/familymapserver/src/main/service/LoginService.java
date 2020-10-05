package service;

import dao.*;
import model.AuthToken;
import model.User;
import request.LoginRequest;
import response.LoginResponse;

import java.sql.Connection;
import java.util.UUID;

/**
 * An object used to log a user in to the program.
 */
public class LoginService {

    /**
     * Log a user into the program.
     * @param request - an object containing a user's username and password.
     * @return a response including a user's information and/or a status of the request.
     */
    public LoginResponse login(LoginRequest request) throws DataAccessException {
        LoginResponse loginResponse = new LoginResponse();
        Database db = new Database();
        try {
            Connection conn = db.openConnection();
            UserDao userDao = new UserDao(conn);
            User user = userDao.getUser(request.getUserName());
            if (user != null && user.getPassword().equals(request.getPassWord())) {
                AuthTokenDao authTokenDao = new AuthTokenDao(conn);
                AuthToken authToken = new AuthToken(UUID.randomUUID().toString(),user.getUserName());
                authTokenDao.insert(authToken);

                loginResponse = new LoginResponse(authToken.getToken(),user.getUserName(),user.getPersonID(),
                        true,null);
                db.closeConnection(true);
            }
            else {
                if (user != null && request.getPassWord() == null) {
                    throw new DataAccessException("Error: Password is null");
                }
                loginResponse.setMessage("Error: There is no account with that username and password");
                loginResponse.setSuccess(false);
                db.closeConnection(false);
            }
        } catch (DataAccessException e) {
            loginResponse.setMessage(e.getMessage());
            loginResponse.setSuccess(false);
            db.closeConnection(false);
        }
        return loginResponse;
    }
}
