package ru.ilka.app.model;

import com.google.gson.annotations.SerializedName;

public class Question {
    private String answer;
    private String question;
    private int value;

    @SerializedName("category")
    private Category category;


    public String getAnswer() {
        return answer;
    }

    public String getQuestion() {
        return question;
    }

    public int getValue() {
        return value;
    }

    public Category getCategory() {
        return category;
    }

    public static class Category {
        private String title;

        public String getTitle() {
            return title;
        }
    }
}
