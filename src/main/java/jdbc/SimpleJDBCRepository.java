package jdbc;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SimpleJDBCRepository {

    private PreparedStatement ps = null;
    private Statement st = null;
    private CustomDataSource dataSource = CustomDataSource.getInstance();

    private static final String createUserSQL = """
            INSERT INTO myusers (firstname, lastname, age)
            VALUES (?, ?, ?);
            """;
    private static final String updateUserSQL = """
            UPDATE myusers
            SET firstname = ?, lastname = ?, age = ?
            WHERE id = ?;
            """;
    private static final String deleteUser = """
            DELETE FROM myusers 
            WHERE id = ?;
            """;
    private static final String findUserByIdSQL = """
            SELECT *
            FROM myusers
            WHERE id = ?;
            """;
    private static final String findUserByNameSQL = """
            SELECT *
            FROM myusers
            WHERE firstname = ?;
            """;
    private static final String findAllUserSQL = """
            SELECT *
            FROM myusers
            """;

    public Long createUser(User user) {
        try (Connection connection = CustomDataSource.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(createUserSQL, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, user.getFirstName());
            ps.setString(2, user.getLastName());
            ps.setInt(3, user.getAge());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
                throw new SQLException("User creating failed");
            }
        } catch (SQLException e) {
            e.fillInStackTrace();
        }
        return 0L;
    }

    public User findUserById(Long userId) {
        try (Connection connection = CustomDataSource.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(findUserByIdSQL)) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return userFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            e.fillInStackTrace();
        }
        return null;
    }

    public User findUserByName(String userName) {
        try (Connection connection = CustomDataSource.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(findUserByNameSQL)) {
            ps.setString(1, userName);
            ps.setString(2, userName);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return userFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            e.fillInStackTrace();
        }
        return null;
    }

    public List<User> findAllUser() {
        List<User> users = new ArrayList<>();
        try (Connection connection = CustomDataSource.getInstance().getConnection();
             Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(findAllUserSQL)) {
            while (rs.next()) {
                users.add(userFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.fillInStackTrace();
        }
        return users;
    }

    public User updateUser(User user) {
        try (Connection connection = CustomDataSource.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(updateUserSQL)) {
            ps.setString(1, user.getFirstName());
            ps.setString(2, user.getLastName());
            ps.setInt(3, user.getAge());
            ps.setLong(4, user.getId());
            int updatedRows = ps.executeUpdate();

            if (updatedRows == 0) {
                throw new SQLException("User data updating failed");
            }
            return user;
        } catch (SQLException e) {
            e.fillInStackTrace();
        }
        return null;
    }

    public void deleteUser(Long userId) {
        try (Connection connection = CustomDataSource.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(deleteUser)) {
            ps.setLong(1, userId);
            int deletedRows = ps.executeUpdate();
            if (deletedRows == 0) {
                throw new SQLException("User data deleting failed");
            }
        } catch (SQLException e) {
            e.fillInStackTrace();
        }
    }

    private User userFromResultSet(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getLong("id"));
        user.setFirstName(rs.getString("firstname"));
        user.setLastName(rs.getString("lastname"));
        user.setAge(rs.getInt("age"));
        return user;
    }
}
