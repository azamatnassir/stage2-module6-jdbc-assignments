package jdbc;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SimpleJDBCRepository {

    private Connection connection = null;
    private PreparedStatement ps = null;
    private Statement st = null;

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
        try (Connection con = CustomDataSource.getInstance().getConnection();
             PreparedStatement preparedStatement = con.prepareStatement(createUserSQL, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, user.getFirstName());
            preparedStatement.setString(2, user.getLastName());
            preparedStatement.setInt(3, user.getAge());

            preparedStatement.executeUpdate();
            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            Long userId = null;
            if (generatedKeys.next()) {
                userId = generatedKeys.getLong("id");
                user.setId(userId);
            }
            return userId;
        } catch (SQLException | IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public User findUserById(Long userId) {
        try (Connection con = CustomDataSource.getInstance().getConnection();
             PreparedStatement preparedStatement = con.prepareStatement(findUserByIdSQL)) {
            preparedStatement.setLong(1, userId);

            ResultSet resultSet = preparedStatement.executeQuery();
            User user = null;
            if (resultSet.next()) {
                user = User.builder()
                        .id(resultSet.getLong("id"))
                        .firstName(resultSet.getString("firstname"))
                        .lastName(resultSet.getString("lastname"))
                        .age(resultSet.getInt("age"))
                        .build();
            }
            return user;
        } catch (SQLException | IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public User findUserByName(String userName) {
        try (Connection con = CustomDataSource.getInstance().getConnection();
             PreparedStatement preparedStatement = con.prepareStatement(findUserByNameSQL)) {
            preparedStatement.setString(1, userName);

            ResultSet resultSet = preparedStatement.executeQuery();
            User user = null;
            if (resultSet.next()) {
                user = User.builder()
                        .id(resultSet.getLong("id"))
                        .firstName(resultSet.getString("firstname"))
                        .lastName(resultSet.getString("lastname"))
                        .age(resultSet.getInt("age"))
                        .build();
            }
            return user;
        } catch (SQLException | IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public List<User> findAllUser() {
        List<User> users = new ArrayList<>();
        try (Connection con = CustomDataSource.getInstance().getConnection();
             PreparedStatement preparedStatement = con.prepareStatement(findAllUserSQL)) {

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                users.add(User.builder()
                        .id(resultSet.getLong("id"))
                        .firstName(resultSet.getString("firstname"))
                        .lastName(resultSet.getString("lastname"))
                        .age(resultSet.getInt("age"))
                        .build());
            }
            return users;
        } catch (SQLException | IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public User updateUser(User user) {
        try (Connection con = CustomDataSource.getInstance().getConnection();
             PreparedStatement preparedStatement = con.prepareStatement(updateUserSQL)) {
            preparedStatement.setString(1, user.getFirstName());
            preparedStatement.setString(2, user.getLastName());
            preparedStatement.setInt(3, user.getAge());
            preparedStatement.setLong(4, user.getId());

            User updatedUser = null;
            int updatedRows = preparedStatement.executeUpdate();
            if (updatedRows == 1) {
                updatedUser = findUserById(user.getId());
            }
            return updatedUser;
        } catch (SQLException | IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteUser(Long userId) {
        try (Connection con = CustomDataSource.getInstance().getConnection();
             PreparedStatement preparedStatement = con.prepareStatement(deleteUser)) {
            preparedStatement.setLong(1, userId);

            preparedStatement.executeUpdate();
        } catch (SQLException | IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
