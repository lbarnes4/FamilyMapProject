package service;

import dao.DataAccessException;
import dao.Database;
import response.ClearResponse;

import java.sql.Connection;

/**
 * An object used to clear the database.
 */
public class ClearService {
    private Database db;
    Connection conn;
    /**
     * Clear the database.
     * @return a response regarding the status of the request.
     */
    public ClearResponse clear() throws DataAccessException {
        ClearResponse response = new ClearResponse();

        db = new Database();
        try {
            conn = db.openConnection();

            db.clearTables();

            db.closeConnection(true);
            response.setSuccess(true);
            response.setMessage("Clear succeeded.");
        } catch (DataAccessException e) {
            response.setMessage(e.getMessage());
            response.setSuccess(false);
            db.closeConnection(false);

        }
        return response;
    }
}
