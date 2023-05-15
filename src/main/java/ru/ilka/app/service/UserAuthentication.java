package ru.ilka.app.service;


import java.sql.*;

import java.util.Scanner;
public class UserAuthentication {
    private Statement statement;
    private String username;
    Scanner scanner = new Scanner(System.in);

    public UserAuthentication(Statement statement) {
        this.statement = statement;
    }

    public boolean authenticateUser() throws SQLException {
        System.out.print("Введите имя пользователя: ");
        username = scanner.nextLine();
        System.out.print("Введите пароль: ");
        String password = scanner.nextLine();
        String hashedPassword = hashPassword(password);
        String query = "SELECT * FROM users WHERE username = '" + username + "' AND password = '" + hashedPassword + "'";
        ResultSet resultSet = statement.executeQuery(query);
        return resultSet.next();
    }


    // При необходимости можно добавить алгоритм хэширования.
    private String hashPassword(String password) {
        return password;
    }
    public String getUsername() {
        return username;
    }
}