package ru.ilka.app.controller;
import ru.ilka.app.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
public class UserScores {
    private Statement statement;

    public UserScores(Connection connection, Statement statement) {
        this.statement = statement;
    }

    public List<User> getUsersByScore() throws SQLException {
        List<User> users = new ArrayList<>();
        String query = "SELECT username, SUM(score) AS total_score FROM user_scores GROUP BY username ORDER BY total_score DESC";
        ResultSet resultSet = statement.executeQuery(query);

        while (resultSet.next()) {
            String username = resultSet.getString("username");
            int totalScore = resultSet.getInt("total_score");
            User user = new User(username, totalScore);
            users.add(user);
        }

        return users;
    }


    public void printUserScores(List<User> users) {
        System.out.println("Список пользователей и их счетов:");
        for (User user : users) {
            System.out.println(user.getUsername() + ": " + user.getScore());
        }
    }

}
