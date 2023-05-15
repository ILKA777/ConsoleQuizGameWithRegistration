package ru.ilka.app.model;

public class User {
    private String username;
    private int score;
    public User(String username, int score) {
        this.username = username;
        this.score = score;
    }

    public String getUsername() {
        return username;
    }

    public int getScore() {
        return score;
    }


}
