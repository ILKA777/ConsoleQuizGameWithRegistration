package org.example;

import ru.ilka.app.controller.GetUserChoice;
import ru.ilka.app.service.UserAuthentication;
import ru.ilka.app.service.UserRegistration;
import ru.ilka.app.controller.QuizGame;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

public class App {
    public static final String DB_URL = "jdbc:postgresql://localhost:5432/spring";
    public static final String DB_USER = "suser";
    public static final String DB_PASSWORD = "password";
    private static final Logger LOGGER = Logger.getLogger(App.class.getName());

    public static void main(String[] args) {
        Connection conn = null;
        Statement stmt = null;
        int maxAttempts = 3;

        for(int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                Class.forName("org.postgresql.Driver");
                conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                stmt = conn.createStatement();

                stmt.executeUpdate("CREATE TABLE IF NOT EXISTS users (id SERIAL PRIMARY KEY, username VARCHAR(255) UNIQUE NOT NULL, password VARCHAR(255) NOT NULL)");
                stmt.executeUpdate("CREATE TABLE IF NOT EXISTS user_scores (id SERIAL PRIMARY KEY, username VARCHAR(255) UNIQUE NOT NULL, score INTEGER NOT NULL)");

                LOGGER.info("Успешное подключение к базе данных");
                break;
            } catch (Exception e) {
                if(attempt == maxAttempts) {
                    LOGGER.log(Level.SEVERE, "Не удалось подключиться к базе данных после " + maxAttempts + " попыток. Проверьте параметры подключения.", e);
                    System.out.println("Не удалось подключиться к базе данных после " + maxAttempts + " попыток. Проверьте параметры подключения." + e);
                    return;
                }
                LOGGER.log(Level.WARNING, "Не удалось подключиться к базе данных. Повторная попытка через 5 секунд...", e);
                System.out.println("Не удалось подключиться к базе данных. Повторная попытка через 5 секунд..." + e);
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException ie) {
                    LOGGER.log(Level.SEVERE, "Поток прерван во время ожидания повторного подключения к базе данных", ie);
                }
            }
        }

        try {
            int choice = GetUserChoice.getUserChoice();
            LOGGER.info("Выбор пользователя: " + choice);

            if (choice == 1) {
                UserRegistration registration = new UserRegistration(conn, stmt);
                registration.registerUser();
            } else if (choice == 2) {
                UserAuthentication authentication = new UserAuthentication(stmt);
                boolean isAuthenticated = authentication.authenticateUser();
                if (isAuthenticated) {
                    LOGGER.info("Пользователь успешно вошел в систему");
                    QuizGame game = new QuizGame("http://jservice.io/api/random", conn, stmt,authentication);
                    game.startGame();
                } else {
                    LOGGER.warning("Ошибка аутентификации. Неверное имя пользователя или пароль.");
                    System.out.println("Ошибка аутентификации. Неверное имя пользователя или пароль.");

                }
            }

            stmt.close();
            conn.close();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Произошла ошибка", e);
        }
    }
}


