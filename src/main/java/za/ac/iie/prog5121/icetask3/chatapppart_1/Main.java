/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package za.ac.iie.prog5121.icetask3.chatapppart_1;

import java.util.Scanner;

/**
 *
 * @author Student
 */
public class Main {

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.println("=== Registration and Login ===");
            System.out.print("Enter your first name: ");
            String firstName = scanner.nextLine();
            System.out.print("Enter your last name: ");
            String lastName = scanner.nextLine();

            //Login userLogin = new Login(firstName, lastName);

            System.out.print("Enter username: ");
            String username = scanner.nextLine();
            System.out.print("Enter password: ");
            String password = scanner.nextLine();
            System.out.print("Enter South African cell phone number (e.g. +27838968976): ");
            String cellPhone = scanner.nextLine();

            String registrationMessage = userLogin.registerUser(username, password, cellPhone);
            System.out.println(registrationMessage);

            if (registrationMessage.equals("Registration successful.")) {
                System.out.println("\n=== Login ===");
                System.out.print("Enter username: ");
                String loginUsername = scanner.nextLine();
                System.out.print("Enter password: ");
                String loginPassword = scanner.nextLine();

                boolean loggedIn = userLogin.loginUser(loginUsername, loginPassword);
                System.out.println(userLogin.returnLoginStatus(loggedIn));
            }
        }
    }
}