/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package za.ac.iie.prog5121.icetask3.chartapppart_1;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Student
 */
public class Login {

    private String username;
    private String password;
    private String cellPhoneNumber;
    private String firstName;
    private String lastName;

    public Login() {
    }

    public Login(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public boolean checkUserName(String username) {
        return username != null
                && username.contains("_")
                && username.length() <= 5;
    }

    public boolean checkPasswordComplexity(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }

        boolean hasCapital = false;
        boolean hasNumber = false;
        boolean hasSpecialCharacter = false;

        for (char character : password.toCharArray()) {
            if (Character.isUpperCase(character)) {
                hasCapital = true;
            } else if (Character.isDigit(character)) {
                hasNumber = true;
            } else if (!Character.isLetterOrDigit(character)) {
                hasSpecialCharacter = true;
            }
        }

        return hasCapital && hasNumber && hasSpecialCharacter;
    }

    /**
     * South African number: +27 followed by up to ten digits.
     * The task's supplied valid test data uses exactly ten digits after +27.
     * @param cellPhoneNumber
     * @return
     *Regex syntax researched from Oracle Java Tutorials
     *https://docs.oracle.com/javase/tutorial/essential/regex/
     */
    public boolean checkCellPhoneNumber(String cellPhoneNumber) {
        Pattern pattern = Pattern.compile("^\\+27\\d{9}$");
        Matcher matcher;
        matcher = pattern.matcher(cellPhoneNumber);
        return matcher.matches();
    }

    public String registerUser(String username, String password, String cellPhoneNumber) {
        if (!checkUserName(username)) {
            return username != null && username.contains("_")&&username.length() <=5;
        }

        if (!checkPasswordComplexity(password)) {
            return "Password is not correctly formatted; please ensure that the password contains at least eight characters, a capital letter, a number and a special character.";
        }

        if (!checkCellPhoneNumber(cellPhoneNumber)) {
            return "Cell number is incorrectly formatted or does not contain an international code; please correct the number and try again.";
        }

        this.username = username;
        this.password = password;
        this.cellPhoneNumber = cellPhoneNumber;
        return "Registration successful.";
    }

    public boolean loginUser(String username, String password) {
        return this.username != null
                && this.password != null
                && this.username.equals(username)
                && this.password.equals(password);
    }

    public String returnLoginStatus(boolean loginSuccessful) {
        if (loginSuccessful) {
            return "Welcome " + firstName + " " + lastName +"," + " it is great to see you again.";
        }
        return "Username or password incorrect, please try again.";
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getCellPhoneNumber() {
        return cellPhoneNumber;
    }
}