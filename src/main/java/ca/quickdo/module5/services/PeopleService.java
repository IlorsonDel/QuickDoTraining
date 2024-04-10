package ca.quickdo.module5.services;

import ca.quickdo.module5.model.Person;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;

public class PeopleService {
    private static Connection connection;

    public static void shutdown() throws SQLException {
        if (connection == null || connection.isClosed()) return;
        connection.close();
    }

    /**
     * Makes sure there is always a database connection before a query is executed.
     */
    private static void assertConnection() {
        if (connection != null) return;
        try (var stream = PeopleService.class.getResourceAsStream("env.properties")) {
            Class.forName("com.mysql.cj.jdbc.Driver");
            final var properties = new Properties();
            properties.load(stream);
            final var connectionString = properties.getProperty("db.conn");
            final var username = properties.getProperty("db.user");
            final var password = properties.getProperty("db.pwd");

            connection = DriverManager.getConnection(connectionString, username, password);
        } catch (ClassNotFoundException | IOException | SQLException e) {
            System.err.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static Collection<Person> getPeople(int limit) {
        assertConnection();
        final var results = new ArrayList<Person>();
        final var sql = "SELECT * FROM people LIMIT ?;";
        try (var statement = connection.prepareStatement(sql)) {
            statement.setInt(1, limit);
            try (var resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    final var person = Person.builder()
                            .id(resultSet.getInt("id"))
                            .names(resultSet.getString("names"))
                            .phone(resultSet.getString("phone"))
                            .email(resultSet.getString("email"))
                            .city(resultSet.getString("city"))
                            .avatar(resultSet.getString("avatar"))
                            .build();
                    results.add(person);
                }
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            throw new RuntimeException(e);
        }
        return results;
    }
}