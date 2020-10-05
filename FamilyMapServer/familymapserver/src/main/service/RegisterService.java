package service;

import dao.*;
import model.*;
import request.RegisterRequest;
import response.RegisterResponse;
import java.io.IOException;
import java.util.*;

/**
 * An object used to register a new user.
 */
public class RegisterService extends AncestorGenerator {


    public RegisterService() throws IOException {
    }

    /**
     * Register a new user.
     * @param request - an object containing the new user's information.
     * @return a response including the user's information and/or a status of the request.
     */
    public RegisterResponse register(RegisterRequest request) throws IOException, DataAccessException {
        RegisterResponse registerResponse = new RegisterResponse();
        //lets create a new database
        db = new Database();
        try {
            conn = db.getConnection();
            eventDao = new EventDao(conn);
            personDao = new PersonDao(conn);


            user = new User(request.getUserName(),request.getPassword(),request.getEmail(),request.getFirstName()
                    ,request.getLastName(), request.getGender(), UUID.randomUUID().toString());
            UserDao userDao = new UserDao(conn);
            userDao.insert(user);

            Random random = new Random();
            int childBirthYear = 2010 - random.nextInt(40);

            Person userPerson = generateUserPersonAndBirthEvent(childBirthYear);
            generateAncestors(userPerson,childBirthYear,4);

            AuthTokenDao authTokenDao = new AuthTokenDao(conn);
            AuthToken authToken = new AuthToken(UUID.randomUUID().toString(),user.getUserName());
            authTokenDao.insert(authToken);

            db.closeConnection(true);
            registerResponse = new RegisterResponse(authToken.getToken(),user.getUserName(),user.getPersonID(),true);
        }
        catch (DataAccessException e) {
            registerResponse.setMessage(e.getMessage());
            registerResponse.setSuccess(false);
            db.closeConnection(false);
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return registerResponse;
    }
}