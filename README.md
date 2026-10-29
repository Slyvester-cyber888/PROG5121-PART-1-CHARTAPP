# ChatApp (QuickChat) - PROG5121 PoE Parts 1, 2 and 3

[![TestJava](https://github.com/Slyvester-cyber888/PROG5121-PART-1-CHARTAPP/actions/workflows/TestJava.yml/badge.svg?branch=master)](https://github.com/Slyvester-cyber888/PROG5121-PART-1-CHARTAPP/actions/workflows/TestJava.yml)

**Module:** Programming 1A (PROG5121)  
**Assessment:** Portfolio of Evidence (PoE) – Part 1  
**Marks:** 100

---

## Part 2 Test Run Video

[![QuickChat PROG5121 Part 2 – Test Run](https://img.youtube.com/vi/va0b0KUe2PE/0.jpg)](https://youtu.be/va0b0KUe2PE)

Watch on YouTube: https://youtu.be/va0b0KUe2PE

Shows the JUnit tests (29 passing), login, the QuickChat menu, message validation, Send/Disregard/Store and JSON storage.

---

## Part 3 Test Run Video

[![QuickChat PROG5121 Part 3 – NetBeans Test Run](https://img.youtube.com/vi/TncDNMoJf0Q/0.jpg)](https://youtu.be/TncDNMoJf0Q)

Watch on YouTube: https://youtu.be/TncDNMoJf0Q

Shows the project running in NetBeans: 55 JUnit tests passing, failed-login protection, the 5 test messages (send, store, disregard), the Stored Messages menu (sender and recipient, longest message, search by Message ID, search by recipient, delete by Message Hash, report) and JSON persistence across restarts.

---

## GitHub Actions (Automated Unit Tests)

The `TestJava` workflow (`.github/workflows/TestJava.yml`) runs `mvn test` on JDK 25 on every push to `master`. The badge at the top of this README shows the live result of the latest run.

- All workflow runs: https://github.com/Slyvester-cyber888/PROG5121-PART-1-CHARTAPP/actions/workflows/TestJava.yml
- Latest successful run: https://github.com/Slyvester-cyber888/PROG5121-PART-1-CHARTAPP/actions/runs/40836628556
- Result: 55 tests, 0 failures (LoginTest 16, MessageTest 14, MessageManagerTest 25)

---

## Project Overview

This is **Part 1** of a console-based Chat Application.  
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
| Username correctly formatted     | `Syv_8`                | `true` / Registration successful |
| Username incorrectly formatted   | `Slyvester!!!!!!`           | `false` / Error message |
| Password meets complexity        | `Ch&seck9`         | `true` |
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
REGEX REFERENCING:
- 
The regular expression used to validate the South African cell phone number was researched from the official Oracle Java Tutorials:

Oracle. (n.d.). *Lesson: Regular Expressions*.  
https://docs.oracle.com/javase/tutorial/essential/regex/

"/**
     * South African number: +27 followed by up to ten digits.
     * The task's supplied valid test data uses exactly ten digits after +27.
     * @param cellPhoneNumber
     * @return 
     */
    public boolean checkCellPhoneNumber(String cellPhoneNumber) {
        return cellPhoneNumber != null
                && cellPhoneNumber.matches("^\\+27[0-9]{1,10}$");
    } "



VIDEO:
- https://youtu.be/3sXVltLQg6A?si=7JL5tR34OddC4D78
## Author

Student Name: [Slyvester Sibonakaliso]  
Student Number: [ST10522997]  
Module: PROG5121 – Programming 1A

---

# Part 2: QuickChat (Sending Messages)

Part 2 extends the Part 1 registration and login app. Only a user who logs in successfully can use QuickChat.

## How it works
- After login: `Welcome to QuickChat.` and the user enters how many messages they want to send.
- A **while loop** keeps the menu running until option 3:
  1) Send Messages, 2) Show recently sent messages ("Coming Soon."), 3) Quit
- A **for loop** allows only the number of messages the user chose. The loop counter is the message number.
- Each message gets a random **10-digit Message ID** (stored as a String to keep leading zeroes).
- The recipient must start with **+27** followed by 9 digits (e.g. +27718693002).
- Messages may be at most **250 characters**.
- The **message hash** = first 2 digits of the ID : message number : first + last word, in capitals (e.g. `00:0:HITONIGHT`).
- The user can **Send**, **Disregard** (press 0 to delete) or **Store** the message. Stored messages are saved to `stored_messages.json` using Gson.
- Sent messages show Message ID, Message Hash, Recipient and Message, then the total number sent.

## Classes
| Class | Purpose |
|---|---|
| `Main` | Console prompts, login flow, QuickChat menu and loops |
| `Login` | Part 1 registration and login validation |
| `Message` | Part 2 message ID, validation, hash, send/disregard/store, JSON storage |
| `LoginTest`, `MessageTest` | JUnit 5 unit tests |

## Tests
Run `mvn test` locally. GitHub Actions runs the same tests on every push (`.github/workflows/TestJava.yml`).

## References
- Google Gson: https://github.com/google/gson
- Gson User Guide: https://github.com/google/gson/blob/main/UserGuide.md

---

# Part 3: Store Data and Display Task Report

Part 3 adds to the same app (Parts 1 and 2 still work). Message data is kept in **arrays**, stored messages are **reloaded from JSON** when QuickChat starts, and a new main-menu option manages them.

## Main menu
1) Send Messages, 2) Show recently sent messages ("Coming Soon."), 3) Quit, **4) Stored Messages**

## Stored Messages submenu (option 4)
| Option | What it does |
|---|---|
| 1 | Display the sender and recipient of every stored message |
| 2 | Display the longest stored message (compares message length) |
| 3 | Search using a Message ID (shows recipient and message) |
| 4 | Search all sent and stored messages for a recipient (shows every match) |
| 5 | Delete a stored message using its Message Hash (updates the arrays and the JSON file) |
| 6 | Display the message report (status, ID, hash, sender, recipient, message, totals) |
| 7 | Return to the main menu |

## Arrays (`MessageManager`)
- The five PoE arrays: **Sent Messages**, **Disregarded Messages**, **Stored Messages**, **Message Hash**, **Message ID**.
- Supporting parallel arrays: senders, recipients, message texts and statuses. The same index in every parallel array is the same message, and they are shifted together when a message is deleted.
- Arrays start at the user's message limit and grow (copy into a bigger array) when stored messages loaded from JSON need more room.
- The sender is the logged-in username.

## JSON
- `stored_messages.json` holds stored messages only (messageId, messageNumber, messageHash, sender, recipient, message, status).
- On start-up the file is read with Gson `fromJson` into an array of records and copied into the arrays. A missing or empty file is handled.
- Deleting by hash rewrites the file, so a deleted message stays deleted after a restart.
- Sent and disregarded messages are kept in the arrays for the current run.

## Tests
`MessageManagerTest` (25 tests) uses the five official Part 3 messages: the sent array (1 and 4), stored messages reloaded from JSON (2 and 5), disregarded (3), the longest message, ID search (found and not found), recipient search (multiple and no matches), delete by hash (it stays deleted after a reload; first, middle and last; an unknown or sent hash), the report, and earlier features. In total 55 tests (LoginTest 16, MessageTest 14, MessageManagerTest 25) run locally and in GitHub Actions.

**Test data notes:** message 4 is printed as "Developer 0838884567". The tests use 0838884567 as message 4's controlled Message ID and as its recipient, exactly as printed. Message 4's text is printed as "It is dinner time !" while the expected result is "It is dinner time!", so the test fixture uses "It is dinner time!". The app itself keeps whatever the user types, and the hash ignores a lone "!" (`It is dinner time !` gives `..:3:ITTIME`).

## Classes
| Class | Purpose |
|---|---|
| `Main` | Console menus and input (Parts 1 to 3) |
| `Login` | Part 1 registration and login |
| `Message` | Part 2 message ID, validation, hash, send/disregard/store, JSON writing |
| `MessageManager` | Part 3 arrays, JSON reading, search, delete, longest message and report |
| `LoginTest`, `MessageTest`, `MessageManagerTest` | JUnit 5 tests |

## References
- Google Gson: https://github.com/google/gson
- Gson User Guide (Array and Object examples, `fromJson` and `toJson`): https://github.com/google/gson/blob/main/UserGuide.md
- IIE (2026) Programming 1A Module Outline, Learning Unit 6: Introduction to Arrays.
