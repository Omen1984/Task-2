package jm.task.core.jdbc.util;

import jm.task.core.jdbc.model.User;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.query.Query;
import org.postgresql.ds.PGConnectionPoolDataSource;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.sql.SQLException;
import java.util.Properties;

public class Util {
    // реализуйте настройку соеденения с БД
    private static final String DEFAULT_HOST = "localhost";
    private static final int DEFAULT_PORT = 5432;
    private static final String DEFAULT_DBNAME = "Learningdata";
    private static final String DEFAULT_USERNAME = "root";
    private static final String DEFAULT_PASSWORD = "root";
    private static final String DEFAULT_DRIVER = "org.postgresql.Driver";
    private static final String DEFAULT_DIALECT = "org.hibernate.dialect.PostgreSQLDialect";

    public PGConnectionPoolDataSource getPGConnectionPoolDataSource() throws SQLException,
            ClassNotFoundException {

        return getPGConnectionPoolDataSource(
                DEFAULT_HOST,
                DEFAULT_PORT,
                DEFAULT_DBNAME,
                DEFAULT_USERNAME,
                DEFAULT_PASSWORD);
    }

    public PGConnectionPoolDataSource getPGConnectionPoolDataSource(
            String hostName,
            int port,
            String dbName,
            String userName,
            String password) {

        PGConnectionPoolDataSource dataSource = new PGConnectionPoolDataSource();
        dataSource.setServerNames(new String[]{hostName});
        dataSource.setDatabaseName(dbName);
        dataSource.setPortNumbers(new int[]{port});
        dataSource.setUser(userName);
        dataSource.setPassword(password);

        return dataSource;
    }

    public SessionFactory getSessionFactory() {
        return getSessionFactory(
                DEFAULT_DRIVER,
                DEFAULT_DIALECT,
                DEFAULT_HOST,
                DEFAULT_PORT,
                DEFAULT_DBNAME,
                DEFAULT_USERNAME,
                DEFAULT_PASSWORD);
    }

    public SessionFactory getSessionFactory(
            String driver,
            String dialect,
            String hostName,
            int port,
            String dbName,
            String userName,
            String password) {
        SessionFactory sessionFactory = null;
        Configuration configuration = new Configuration();
        String url = String.format("jdbc:postgresql://%s:%d/%s", hostName, port, dbName);

        Properties settings = new Properties();
        settings.setProperty(Environment.DRIVER, driver);
        settings.setProperty(Environment.URL, url);
        settings.setProperty(Environment.USER, userName);
        settings.setProperty(Environment.PASS, password);
        settings.setProperty(Environment.DIALECT, dialect);
        settings.setProperty(Environment.CURRENT_SESSION_CONTEXT_CLASS, "thread");
        settings.setProperty(Environment.SHOW_SQL, "true");
        settings.setProperty(Environment.HBM2DDL_AUTO, "none");

        configuration.setProperties(settings);
        configuration.addAnnotatedClass(User.class);

        StandardServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                .applySettings(configuration.getProperties()).build();

        try {
            sessionFactory = configuration.buildSessionFactory(serviceRegistry);
        } catch (Exception e) {
            StandardServiceRegistryBuilder.destroy(serviceRegistry);
        }

        return sessionFactory;
    }
}
