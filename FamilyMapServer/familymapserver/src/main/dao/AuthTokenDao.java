package dao;

import model.AuthToken;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Provides an interface to the AuthToken database table.
 */
public class AuthTokenDao {
    private final Connection conn;

    public AuthTokenDao(Connection conn)
    {
        this.conn = conn;
    }

    /**
     * Insert an AuthToken object into the database.
     * @param authToken - the object to be inserted.
     */
    public void insert(AuthToken authToken) throws DataAccessException, IllegalArgumentException {
        if (authToken == null) {
            throw new IllegalArgumentException();
        }
        String sql = "INSERT INTO AuthTokens (Token, UserName) VALUES(?,?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            //Using the statements built-in set(type) functions we can pick the question mark we want
            //to fill in and give it a proper value. The first argument corresponds to the first
            //question mark found in our sql String
            stmt.setString(1, authToken.getToken());
            stmt.setString(2, authToken.getUserName());

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error encountered while inserting AuthToken into the database");
        }
    }

    /**
     * Get an AuthToken object from the database.
     * @param token - the unique identifier of the AuthToken.
     * @return the AuthToken object.
     */
    public AuthToken getAuthToken(String token) throws DataAccessException {
        AuthToken authToken;
        ResultSet rs = null;
        String sql = "SELECT * FROM AuthTokens WHERE Token = ?;";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, token);
            rs = stmt.executeQuery();
            if (rs.next()) {
                authToken = new AuthToken(rs.getString("Token"),rs.getString("UserName"));
                return authToken;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error encountered while finding authToken");
        } finally {
            if(rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

        }
        return null;
    }

    /**
     * Delete all AuthTokens from the database.
     */
    public void deleteAll() {

    }

    /**
     * Delete all AuthTokens that belong to one user.
     * @param userName - the userName of the user whose AuthTokens are to be deleted.
     */
    public void deleteAuthTokensOfUser(String userName) {

    }
}
