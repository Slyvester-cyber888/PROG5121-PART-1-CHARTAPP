# ChatApp - Part 1: Registration and Login Feature

Module: Programming 1A (PROG5121)  
Assessment: Portfolio of Evidence (PoE) – Part 1  
Marks: 100

---

## Project Overview

This is Part 1 of a console-based Chat Application.  
The goal of this part is to create a secure **Registration and Login** system that validates user details before allowing access to the application.

The system checks:
- Username format
- Password complexity
- South African cell phone number format

Once the user successfully registers, they can log in using the same credentials.

---

## Purpose of Part 1

The main purpose of this section is to:

- Practice creating classes and methods (OOP)
- Use decision-making (if statements)
- Accept user input and return appropriate messages
- Write clean, testable code
- Create unit tests using JUnit to verify that methods work correctly

This part forms the foundation of the full Chat Application that will be built in later parts.

---

## Features Implemented

### 1. User Registration
The system asks the user for:
- First Name
- Last Name
- Username
- Password
- South African Cell Phone Number

### 2. Validation Rules

| Field              | Rule                                                                 | Success Message                          | Failure Message |
|--------------------|----------------------------------------------------------------------|------------------------------------------|-----------------|
| **Username**       | Must contain an underscore (`_`) and be no more than 5 characters   | Username successfully captured.          | Username is not correctly formatted; please ensure that your username contains an underscore and is no more than five characters in length. |
| **Password**       | At least 8 characters, 1 capital letter, 1 number, 1 special character | Password successfully captured.       | Password is not correctly formatted; please ensure that the password contains at least eight characters, a capital letter, a number, and a special character. |
| **Cell Phone**     | Must start with `+27` followed by 9 digits                          | Cell number successfully captured.       | Cell number is incorrectly formatted or does not contain an international code; please correct the number and try again. |

### 3. Login
After successful registration, the user can log in.  
The system returns:

- **Successful login:**  
  `Welcome <FirstName>, <LastName> it is great to see you again.`

- **Failed login:**  
  `Username or password incorrect, please try again.`

---

## Classes and Methods

### `Login` Class

| Method                        | Return Type | Description |
|-------------------------------|-------------|-------------|
| `checkUserName()`             | `boolean`   | Checks if username contains `_` and is ≤ 5 characters |
| `checkPasswordComplexity()`   | `boolean`   | Checks password complexity rules |
| `checkCellPhoneNumber()`      | `boolean`   | Uses regex to validate South African phone number |
| `registerUser()`              | `String`    | Registers the user and returns the appropriate message |
| `loginUser()`                 | `boolean`   | Checks if login credentials match |
| `returnLoginStatus()`         | `String`    | Returns the welcome or error message |

### `Main` Class
- Handles all user input using `Scanner`
- Creates a `Login` object
- Calls the registration and login methods
- Displays messages to the user

---

## How to Run the Application

1. Open the project in **NetBeans**.
2. Make sure the main class is set to `Main`.
3. Run the project (`F6` or right-click → Run).
4. Follow the on-screen prompts to register and then log in.

---

## Unit Testing

Unit tests were created using **JUnit 5** to verify that all methods work correctly.

### Test Data Used (as required by the PoE):

| Test Case                        | Test Data              | Expected Result |
|----------------------------------|------------------------|-----------------|
| Username correctly formatted     | `kyl_1`                | `true` / Registration successful |
| Username incorrectly formatted   | `kyle!!!!!!`           | `false` / Error message |
| Password meets complexity        | `Ch&sec@ke99!`         | `true` |
| Password does not meet complexity| `password`             | `false` |
| Cell phone correctly formatted   | `+27838968976`         | `true` |
| Cell phone incorrectly formatted | `08966553`             | `false` |
| Login Successful                 | Valid credentials      | `true` |
| Login Failed                     | Invalid credentials    | `false` |

To run the tests:
1. Right-click the `LoginTest` class
2. Select **Test File**

---

## Technologies Used

- Java
- NetBeans IDE
- JUnit 5
- Maven
- Git & GitHub

---

## Author

Student Name: [Your Full Name]  
Student Number: [Your Student Number]  
Module: PROG5121 – Programming 1A
