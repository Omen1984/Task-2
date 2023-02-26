package jm.task.core.jdbc.dao;

import jm.task.core.jdbc.model.User;
import jm.task.core.jdbc.util.Util;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.NativeQuery;
import org.hibernate.query.Query;

import java.util.List;

public class UserDaoHibernateImpl implements UserDao {

    private final Util util;
    private final SessionFactory sessionFactory;
    private static final String TABLE_NAME = "user_data";

    public UserDaoHibernateImpl() {
        this.util = new Util();
        this.sessionFactory = util.getSessionFactory();
    }

    @Override
    public void createUsersTable() {
        if (!isPresentInDatabase(TABLE_NAME)) {
            String sql = "CREATE TABLE " +
                    TABLE_NAME + " (id bigint NOT NULL GENERATED ALWAYS AS IDENTITY, " +
                    "name text, " +
                    "lastname text, " +
                    "age numeric, " +
                    "PRIMARY KEY (id));";

            try (Session session = sessionFactory.getCurrentSession()) {
                session.beginTransaction();
                NativeQuery query = session.createSQLQuery(sql);
                query.executeUpdate();
                session.getTransaction().commit();
            }
        }
    }

    @Override
    public void dropUsersTable() {
        if (isPresentInDatabase(TABLE_NAME)) {
            String sql = "DROP TABLE " + TABLE_NAME + ";";

            try (Session session = sessionFactory.getCurrentSession()) {
                session.beginTransaction();
                NativeQuery query = session.createNativeQuery(sql);
                query.executeUpdate();
                session.getTransaction().commit();
            }
        }
    }

    @Override
    public void saveUser(String name, String lastName, byte age) {
        if (isPresentInDatabase(TABLE_NAME)) {
            User user = new User(name, lastName, age);

            try (Session session = sessionFactory.getCurrentSession()) {
                session.beginTransaction();
                session.save(user);
                long id = user.getId();
                if (session.get(User.class, id) != null) {
                    System.out.println("User с именем – " + user.getName() + " добавлен в базу данных");
                }
                session.getTransaction().commit();
            }
        }
    }

    @Override
    public void removeUserById(long id) {
        if (isPresentInDatabase(TABLE_NAME)) {
            String hql = "delete User where id = :id";

            try (Session session = sessionFactory.getCurrentSession()) {
                session.beginTransaction();
                Query query = session.createQuery(hql);
                query.setParameter("id", id)
                        .executeUpdate();
                session.getTransaction().commit();
            }
        }

    }

    @Override
    public List<User> getAllUsers() {
        List<User> users = null;

        if (isPresentInDatabase(TABLE_NAME)) {
            String hql = "from User";

            try (Session session = sessionFactory.getCurrentSession()) {
                session.beginTransaction();
                Query query = session.createQuery(hql);
                users = query.getResultList();
                session.getTransaction().commit();
            }

            users.stream()
                    .forEach(System.out::println);
        }

        return users;
    }

    @Override
    public void cleanUsersTable() {
        if (isPresentInDatabase(TABLE_NAME)) {
            String hql = "from User";

            try (Session session = sessionFactory.getCurrentSession()) {
                session.beginTransaction();
                List<User> users;
                Query query = session.createQuery(hql);
                users = query.getResultList();
                if (users != null) {
                    users.stream().forEach((user) -> session.delete(user));
                }
                session.getTransaction().commit();
            }
        }
    }

    private boolean isPresentInDatabase(String tableName) {
        String sql = "SELECT tablename FROM pg_tables;";
        boolean isPresent = false;

        try (Session session = sessionFactory.getCurrentSession()) {
            session.beginTransaction();
            NativeQuery query = session.createSQLQuery(sql);
            List<String> tables = query.getResultList();
            session.getTransaction().commit();

            for (String t : tables) {
                if (t.equals(tableName)) {
                    isPresent = true;
                }
            }

        }

        return isPresent;
    }
}
