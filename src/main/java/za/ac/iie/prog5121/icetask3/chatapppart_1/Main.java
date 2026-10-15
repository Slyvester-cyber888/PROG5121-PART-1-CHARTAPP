package za.ac.iie.prog5121.icetask3.chatapppart_1;

import java.util.Scanner;

/**
 * Main runs the console application: Part 1 registration and login,
 * then (only after a successful login) the Part 2 QuickChat menu.
 *
 * @author Slyvester
 */
public class Main {

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            boolean loggedIn = registerAndLogin(scanner);

            // Only a successfully logged-in user may use QuickChat.
            if (loggedIn) {
                runQuickChat(scanner);
            } else {
                System.out.println("Access to QuickChat denied.");
            }
        }
    }

    // ===================== Part 1: Registration and Login =====================

    private static boolean registerAndLogin(Scanner scanner) {
        System.out.println("=== Registration and Login ===");
        System.out.print("Enter your first name: ");
        String firstName = scanner.nextLine();
        System.out.print("Enter your last name: ");
        String lastName = scanner.nextLine();

        Login userLogin = new Login(firstName, lastName);

        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();
        System.out.print("Enter South African cell phone number (e.g. +27838968976): ");
        String cellPhone = scanner.nextLine();

        String registrationMessage = userLogin.registerUser(username, password, cellPhone);
        System.out.println(registrationMessage);

        if (!registrationMessage.equals("Registration successful.")) {
            return false;
        }

        System.out.println("\n=== Login ===");
        System.out.print("Enter username: ");
        String loginUsername = scanner.nextLine();
        System.out.print("Enter password: ");
        String loginPassword = scanner.nextLine();

        boolean loggedIn = userLogin.loginUser(loginUsername, loginPassword);
        System.out.println(userLogin.returnLoginStatus(loggedIn));
        return loggedIn;
    }

    // ===================== Part 2: QuickChat =====================

    private static void runQuickChat(Scanner scanner) {
        System.out.println("\nWelcome to QuickChat.");

        int messageLimit = readPositiveInt(scanner, "Enter the number of messages you wish to send: ");
        int messagesProcessed = 0; // counts messages entered (controls the limit)

        int choice = 0;
        // while loop: keeps the menu running until the user chooses 3 (Quit)
        while (choice != 3) {
            System.out.println("\n1) Send Messages");
            System.out.println("2) Show recently sent messages");
            System.out.println("3) Quit");
            choice = readInt(scanner, "Enter your choice: ");

            switch (choice) {
                case 1:
                    messagesProcessed = enterMessages(scanner, messagesProcessed, messageLimit);
                    break;
                case 2:
                    System.out.println("Coming Soon.");
                    break;
                case 3:
                    System.out.println("Goodbye.");
                    break;
                default:
                    System.out.println("Invalid option, please choose 1, 2 or 3.");
            }
        }
    }

    /**
     * for loop: lets the user enter only the number of messages they chose.
     * Returns the updated number of messages processed.
     */
    private static int enterMessages(Scanner scanner, int messagesProcessed, int messageLimit) {
        if (messagesProcessed >= messageLimit) {
            System.out.println("Message limit reached.");
            return messagesProcessed;
        }

        // The loop counter is the message number (starts at 0, as in the PoE hash 00:0:HITONIGHT).
        for (int messageNumber = messagesProcessed; messageNumber < messageLimit; messageNumber++) {
            System.out.println("\nCreating message " + (messageNumber + 1) + " of " + messageLimit + " ...");

            String recipient = readRecipient(scanner);
            String text = readMessageText(scanner);

            Message message = new Message(messageNumber, recipient, text);
            System.out.println(message.createMessageIdReport());
            System.out.println("Message number: " + message.getMessageNumber());
            System.out.println("Message hash: " + message.getMessageHash());

            handleMessageChoice(scanner, message);
            messagesProcessed++;
        }

        System.out.println("\nMessage limit reached.");
        System.out.println("Total number of messages sent: " + Message.returnTotalMessagess());
        return messagesProcessed;
    }

    /**
     * Asks whether to send, disregard or store the message and shows feedback.
     */
    private static void handleMessageChoice(Scanner scanner, Message message) {
        int option = 0;
        while (option < 1 || option > 3) {
            System.out.println("1) Send Message");
            System.out.println("2) Disregard Message");
            System.out.println("3) Store Message to send later");
            option = readInt(scanner, "Choose an option: ");
            if (option < 1 || option > 3) {
                System.out.println("Invalid option, please choose 1, 2 or 3.");
            }
        }

        String result = message.SentMessage(option);
        System.out.println(result);

        if (option == Message.SEND) {
            System.out.println(message.getMessageDetails());
        } else if (option == Message.DISREGARD) {
            String confirm = scanner.nextLine().trim();
            while (!confirm.equals("0")) {
                System.out.println("Press 0 to delete the message.");
                confirm = scanner.nextLine().trim();
            }
            System.out.println("Message deleted.");
        }
    }

    private static String readRecipient(Scanner scanner) {
        System.out.print("Enter the recipient's cell number (e.g. +27718693002): ");
        String recipient = scanner.nextLine().trim();
        // Keep asking until the number is correctly formatted.
        while (!Message.isValidRecipient(recipient)) {
            System.out.println(Message.checkRecipientCell(recipient));
            System.out.print("Enter the recipient's cell number: ");
            recipient = scanner.nextLine().trim();
        }
        System.out.println(Message.checkRecipientCell(recipient));
        return recipient;
    }

    private static String readMessageText(Scanner scanner) {
        System.out.print("Enter your message (max 250 characters): ");
        String text = scanner.nextLine();
        // Keep asking until the message is no longer than 250 characters.
        while (!Message.isValidMessageLength(text)) {
            if (text.trim().isEmpty()) {
                System.out.println("Message cannot be empty.");
            } else {
                System.out.println("Please enter a message of less than 250 characters.");
                System.out.println(Message.checkMessageLength(text));
            }
            System.out.print("Enter your message: ");
            text = scanner.nextLine();
        }
        System.out.println(Message.checkMessageLength(text));
        return text;
    }

    /**
     * Reads a whole number without crashing on letters or empty input.
     */
    private static int readInt(Scanner scanner, String prompt) {
        System.out.print(prompt);
        String input = scanner.nextLine().trim();
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            return -1; // treated as an invalid option by the caller
        }
    }

    private static int readPositiveInt(Scanner scanner, String prompt) {
        int value = readInt(scanner, prompt);
        while (value < 1) {
            System.out.println("Please enter a whole number greater than 0.");
            value = readInt(scanner, prompt);
        }
        return value;
    }
}
