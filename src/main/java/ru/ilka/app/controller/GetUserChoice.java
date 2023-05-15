package ru.ilka.app.controller;

import java.util.Scanner;

public class GetUserChoice {
    static Scanner scanner = new Scanner(System.in);
    public static int getUserChoice() {
        System.out.println("1 - Регистрация\n2 - Авторизация\nВыберите действие (1 или 2): ");
        return scanner.nextInt();
    }
}
