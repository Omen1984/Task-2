package jm.task.core.jdbc.dao;

import jm.task.core.jdbc.model.User;
import jm.task.core.jdbc.util.Util;
import org.postgresql.ds.PGConnectionPoolDataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDaoJDBCImpl implements UserDao {

    private final Util util;
    private PGConnectionPoolDataSource dataSource;
    private static final String TABLE_NAME = "user_data";

    public UserDaoJDBCImpl() {
        this.util = new Util();
        try {
            this.dataSource = util.getPGConnectionPoolDataSource();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void createUsersTable() {
        if (!isPresentInDatabase(TABLE_NAME)) {
            String sql = "CREATE TABLE " +
                    TABLE_NAME + " (id bigint NOT NULL GENERATED ALWAYS AS IDENTITY, " +
                    "name text, " +
                    "lastname text, " +
                    "age numeric, " +
                    "PRIMARY KEY (id));";

            try (PreparedStatement pst = dataSource.getConnection().prepareStatement(sql)) {
                pst.executeUpdate();
                System.out.println("TABLE CREATE " + TABLE_NAME + " COMPLETE");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void dropUsersTable() {
        if (isPresentInDatabase(TABLE_NAME)) {
            String sql = "DROP TABLE " + TABLE_NAME + ";";

            try (PreparedStatement pst = dataSource.getConnection().prepareStatement(sql)) {
                pst.executeUpdate();
                System.out.println("DROP TABLE " + TABLE_NAME + " COMPLETE");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void saveUser(String name, String lastName, byte age) {
        if (isPresentInDatabase(TABLE_NAME)) {
            String sql = "INSERT INTO " + TABLE_NAME +
                    "(name, lastname, age) " +
                    "VALUES" +
                    "(?, ?, ?)" +
                    "returning name;";

            try (PreparedStatement pst = dataSource.getConnection().prepareStatement(sql)) {
                pst.setString(1, name);
                pst.setString(2, lastName);
                pst.setInt(3, age);
                ResultSet rs = pst.executeQuery();

                while (rs.next()) {
                    System.out.println("User с именем – " + rs.getString(1) + " добавлен в базу данных");
                }
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void removeUserById(long id) {
        List<User> users = getAllUsers();

        if (users != null) {
            String sql = "DELETE FROM " + TABLE_NAME +
                    " WHERE id = ?;";

            users.stream().forEach((u) -> {
                if (u.getId() == id) {
                    try (PreparedStatement pst = dataSource.getConnection().prepareStatement(sql)) {
                        pst.setLong(1, id);
                        pst.executeUpdate();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    /**
     * При отсутствии таблици в BD
     *
     * @return null
     */
    public List<User> getAllUsers() {
        if (isPresentInDatabase(TABLE_NAME)) {
            String sql = "Select * FROM " + TABLE_NAME + " ORDER BY id;";
            List<User> users = new ArrayList<>();

            try (PreparedStatement pst = dataSource.getConnection().prepareStatement(sql)) {
                ResultSet rs = pst.executeQuery();

                while (rs.next()) {
                    User user = new User();

                    long id = rs.getLong(1);
                    String name = rs.getString(2);
                    String lastName = rs.getString(3);
                    byte age = rs.getByte(4);

                    user.setId(id);
                    user.setName(name);
                    user.setLastName(lastName);
                    user.setAge(age);

                    users.add(user);
                }
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            users.stream().forEach(System.out::println);
            return users;
        }
        return null;
    }

    public void cleanUsersTable() {
        if (isPresentInDatabase(TABLE_NAME)) {
            String sql = "TRUNCATE TABLE " + TABLE_NAME + ";";

            try (PreparedStatement pst = dataSource.getConnection().prepareStatement(sql)) {
                pst.executeUpdate();
                System.out.println("TRUNCATE TABLE " + TABLE_NAME + " COMPLETE");
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }
    }

    private boolean isPresentInDatabase(String tableName) {
        String sql = "SELECT * FROM pg_tables;";
        boolean isPresent = false;

        try (PreparedStatement pst = dataSource.getConnection().prepareStatement(sql)) {
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                String table = rs.getString(2);
                if (table.equals(TABLE_NAME)) {
                    isPresent = true;
                }
            }

            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return isPresent;
    }
}
