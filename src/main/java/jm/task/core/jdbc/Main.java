package jm.task.core.jdbc;

import jm.task.core.jdbc.service.UserService;
import jm.task.core.jdbc.service.UserServiceImpl;

public class Main {
    public static void main(String[] args) {
        // реализуйте алгоритм здесь
        UserService userService = new UserServiceImpl();
        //Создание таблицы User(ов)
        userService.createUsersTable();
        //Добавление 4 User(ов) в таблицу с данными на свой выбор.
        // После каждого добавления должен быть вывод в консоль ( User с именем – name добавлен в базу данных )
        userService.saveUser("Ivan", "Ivanov", (byte) 22);
        userService.saveUser("Iran", "Ivanov", (byte) 16);
        userService.saveUser("Irhan", "Ivanov", (byte) 15);
        userService.saveUser("Ayran", "Ivanov", (byte) 19);
        //Получение всех User из базы и вывод в консоль ( должен быть переопределен toString в классе User)
        userService.getAllUsers();
        //Очистка таблицы User(ов)
        userService.cleanUsersTable();
        //Удаление таблицы
        userService.dropUsersTable();
    }
}
