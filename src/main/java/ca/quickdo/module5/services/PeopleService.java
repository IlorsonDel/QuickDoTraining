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

    public static Collection<Person> getPeople(int limit) throws SQLException {
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
        }
        return results;
    }

    public static Collection<String> findAllCities() throws SQLException {
        final var cities = new ArrayList<String>();
        assertConnection();
        final var sql = "SELECT city FROM people;";
        try (var statement = connection.prepareStatement(sql);
             var resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                cities.add(resultSet.getString("city"));
            }
        }


        return cities;
    }

    public static Person save(Person person) throws SQLException {
        assertConnection();
        final var sql = "INSERT INTO people(`names`,`phone`,`city`, `email`) VALUES (?, ?, ?, ?);";
        try (var statement = connection.prepareStatement(sql)) {
            statement.setString(1, person.getNames());
            statement.setString(2, person.getPhone());
            statement.setString(3, person.getCity());
            statement.setString(4, person.getEmail());

            statement.execute();

            final var sql2 = "select * from people where id = (select max(id) from people);";
            try (var s2 = connection.prepareStatement(sql2);
                 var rs = s2.executeQuery()) {
                rs.next();
                return Person.builder()
                        .id(rs.getInt("id"))
                        .names(rs.getString("names"))
                        .phone(rs.getString("phone"))
                        .email(rs.getString("email"))
                        .city(rs.getString("city"))
                        .avatar(rs.getString("avatar"))
                        .build();
            }
        }
    }

    /**
     * Deletes a {@link Person} record by their database ID
     * @param id The person's database ID
     * @return success status of the operation
     * @throws SQLException
     */
    public static void deleteById(int id) throws SQLException {
        assertConnection();
        final var sql = "DELETE FROM people where id = ?;";
        try(var statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            statement.execute();
        }
    }

}