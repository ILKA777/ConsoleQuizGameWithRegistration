package ru.ilka.app.controller;

import com.google.gson.Gson;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import ru.ilka.app.model.User;
import ru.ilka.app.service.UserRegistration;
import ru.ilka.app.model.Question;
import ru.ilka.app.service.UserAuthentication;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class QuizGame {
    private static String URL;
    private final Connection connection;
    private final Statement statement;
    private final UserAuthentication userAuthentication;
    private OkHttpClient client;
    private Gson gson;
    private int score;
    private String username;
    Scanner scanner = new Scanner(System.in);

    private static final Logger LOGGER = Logger.getLogger(QuizGame.class.getName());

    public QuizGame(String url, Connection connection, Statement statement, UserAuthentication userAuthentication) {
        client = new OkHttpClient();
        gson = new Gson();
        score = 0;
        URL = url;
        this.connection = connection;
        this.statement = statement;
        this.userAuthentication = userAuthentication;
    }

    public Question fetchQuestion() throws IOException {
        Request request = new Request.Builder().url(URL).build();
        Response response = client.newCall(request).execute();
        System.out.println(response.code());

        if (!response.isSuccessful()) {
            if (response.code() >= 500) {
                LOGGER.log(Level.SEVERE, "Ошибка на сервере (ошибка 500). Код ответа: " + response.code());
                System.out.println("Ошибка на сервере (ошибка 500). Повторите запрос позже.");
            } else {
                LOGGER.log(Level.SEVERE, "Не удалось получить вопрос. Код ответа: " + response.code());
                System.out.println("Не удалось получить вопрос. Попробуйте еще раз.");
            }

        }



        String json = response.body().string();
        Question[] questions = gson.fromJson(json, Question[].class);
        return questions[0];
    }

    public void startGame() throws SQLException {
        boolean playing = true;
        int answered = 0;
        int correctQuestions = 0;
        int errorCount = 0;
        final int maxErrors = 3;

        System.out.println("Добро пожаловать на матч века онлайн!");
        System.out.println("Если хотите выйти, введите 'exit'.");

        username = userAuthentication.getUsername();

        while (playing) {
            Question question;
            try {
                question = fetchQuestion();
            } catch (IOException e) {
                LOGGER.log(Level.WARNING, "Ошибка при получении вопроса.", e);
                System.out.println("Ошибка при получении вопроса." + e);
                errorCount++;
                if (errorCount >= maxErrors) {
                    LOGGER.log(Level.WARNING, "Превышено максимальное количество ошибок (" + maxErrors + ")." +
                            " Игра завершается.");
                    System.out.println("Превышено максимальное количество ошибок (" + maxErrors + ")." +
                            " Игра завершается.");
                    break;
                }
                System.out.println("Попробуйте еще раз.");
                continue;
            }

            errorCount = 0;

            System.out.println("Категория: " + question.getCategory().getTitle());
            System.out.println("Вопрос: " + question.getQuestion());
            System.out.println("правильный ответ для проверки работы программы: " + question.getAnswer());
            System.out.println("Ответ: ");
            String answer = scanner.nextLine();
            if (answer.equalsIgnoreCase("exit")) {
                playing = false;
            } else {
                answered++;
                if (answer.equalsIgnoreCase(question.getAnswer())) {
                    System.out.println("Правильно!");
                    score += question.getValue();
                    correctQuestions++;
                } else {
                    System.out.println("Неправильно! Правильный ответ: " + question.getAnswer());
                }
                System.out.println("Текущий счет: " + score);
            }
        }

        System.out.println("Игра окончена!");
        LOGGER.log(Level.INFO, "Игра завершена. Всего вопросов: " + answered +
                ", правильных ответов: " + correctQuestions + ", очков набрано: " + score);
        System.out.println("Всего вопросов: " + answered);
        System.out.println("Правильных ответов: " + correctQuestions);
        System.out.println("Очков набрано за игру: " + score);


        UserRegistration registration = new UserRegistration(connection, statement);
        registration.saveUserScore(username, score);


        UserScores userScores = new UserScores(connection, statement);
        List<User> users = userScores.getUsersByScore();
        userScores.printUserScores(users);
    }
}

