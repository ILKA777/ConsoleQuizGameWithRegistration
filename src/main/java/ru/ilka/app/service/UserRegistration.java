package ru.ilka.app.service;


import java.sql.*;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserRegistration {
    private static final Logger LOGGER = Logger.getLogger(UserRegistration.class.getName());

    private Connection connection;
    private Statement statement;
    Scanner scanner = new Scanner(System.in);

    public UserRegistration(Connection connection, Statement statement) {
        this.connection = connection;
        this.statement = statement;
    }

    public void registerUser() throws SQLException {
        System.out.print("Введите имя пользователя: ");
        String username = scanner.nextLine();
        System.out.print("Введите пароль: ");
        String password = scanner.nextLine();
        String checkQuery = "SELECT username FROM users WHERE username = ?";
        PreparedStatement checkStmt = connection.prepareStatement(checkQuery);
        checkStmt.setString(1, username);
        ResultSet resultSet = checkStmt.executeQuery();

        if (resultSet.next()) {
            LOGGER.log(Level.INFO, "Попытка регистрации с уже существующим именем пользователя: " + username);
            System.out.println("Пользователь с таким именем уже существует. Пожалуйста, выберите другое имя.");
            return;
        }

        String hashedPassword = hashPassword(password);
        String query = "INSERT INTO users (username, password) VALUES (?, ?)";
        PreparedStatement stmt = connection.prepareStatement(query);
        stmt.setString(1, username);
        stmt.setString(2, hashedPassword);
        stmt.executeUpdate();

        LOGGER.log(Level.INFO, "Новый пользователь успешно зарегистрирован: " + username);
        System.out.println("Вы успешно зарегистрированы!");
    }

    private String hashPassword (String password) {
        return password;
    }

    public void saveUserScore(String username, int score) throws SQLException {
        String selectQuery = "SELECT username FROM user_scores WHERE username = ?";
        PreparedStatement selectStmt = connection.prepareStatement(selectQuery);
        selectStmt.setString(1, username);
        ResultSet resultSet = selectStmt.executeQuery();

        if (!resultSet.next()) {
            String insertQuery = "INSERT INTO user_scores (username, score) VALUES (?, ?)";
            PreparedStatement insertStmt = connection.prepareStatement(insertQuery);
            insertStmt.setString(1, username);
            insertStmt.setInt(2, score);
            insertStmt.executeUpdate();
            LOGGER.log(Level.INFO, "Сохранение нового счета для пользователя: " + username + " счет: " + score);
        } else {
            String updateQuery = "UPDATE user_scores SET score = score + ? WHERE username = ?";
            PreparedStatement updateStmt = connection.prepareStatement(updateQuery);
            updateStmt.setInt(1, score);
            updateStmt.setString(2, username);
            updateStmt.executeUpdate();
            LOGGER.log(Level.INFO, "Обновление счета для пользователя: " + username + " новый счет: " + score);
        }
    }


}
