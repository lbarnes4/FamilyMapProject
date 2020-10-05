package dao;

import model.User;

import java.sql.*;

/**
 * Provides an interface to the User database table.
 */
public class UserDao {
    private final Connection conn;

    public UserDao(Connection conn) {
        this.conn = conn;
    }
    /**
     * Insert a User object into the database.
     * @param user - the User to be inserted.
     */
    public void insert(User user) throws DataAccessException, IllegalArgumentException {
        if (user == null) {
            throw new IllegalArgumentException();
        }
        String sql = "INSERT INTO Users (UserName,Password,Email,FirstName,LastName,Gender,PersonID) " +
                "VALUES(?,?,?,?,?,?,?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1,user.getUserName());
            stmt.setString(2,user.getPassword());
            stmt.setString(3,user.getEmail());
            stmt.setString(4,user.getFirstName());
            stmt.setString(5,user.getLastName());
            stmt.setString(6,user.getGender());
            stmt.setString(7,user.getPersonID());

            stmt.executeUpdate();
        }
        catch (SQLException e) {
            throw new DataAccessException("Error encountered while inserting User into the database");
        }
    }

    /**
     * Delete all Users from the database.
     */
    public void deleteAll() throws DataAccessException {
        try (Statement stmt = conn.createStatement()) {
            String sql = "DELETE FROM Users";
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            throw new DataAccessException("SQL Error encountered while clearing Users");
        }
    }

    /**
     * Get one User from the database.
     * @param userName - the userName of the User to be returned
     * @return a User object.
     */
    public User getUser(String userName) throws DataAccessException{
        User user;
        ResultSet rs = null;
        String sql = "SELECT * FROM Users WHERE UserName = ?;";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1,userName);
            rs = stmt.executeQuery();
            if (rs.next()) {
                user = new User(userName,rs.getString("Password"),rs.getString("Email"),rs.getString("FirstName"),
                    rs.getString("LastName"),rs.getString("Gender"), rs.getString("PersonID"));
                return user;
            }
        } catch(SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error encountered while finding user");
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
}
