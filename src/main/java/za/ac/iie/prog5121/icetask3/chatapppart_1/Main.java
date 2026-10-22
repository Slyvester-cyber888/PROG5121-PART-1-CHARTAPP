package za.ac.iie.prog5121.icetask3.chatapppart_1;

import java.util.Scanner;

/**
 * Main runs the console application: Part 1 registration and login,
 * then (only after a successful login) the QuickChat menu: Part 2 message
 * sending and Part 3 Stored Messages (search, delete and report).
 *
 * @author Slyvester
 */
public class Main {

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            String loggedInUser = registerAndLogin(scanner);

            // Only a successfully logged-in user may use QuickChat.
            if (loggedInUser != null) {
                runQuickChat(scanner, loggedInUser);
            } else {
                System.out.println("Access to QuickChat denied.");
            }
        }
    }

    // ===================== Part 1: Registration and Login =====================

    /**
     * Registers and logs in the user.
     * Returns the logged-in username, or null if registration or login failed.
     */
    private static String registerAndLogin(Scanner scanner) {
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
            return null;
        }

        System.out.println("\n=== Login ===");
        System.out.print("Enter username: ");
        String loginUsername = scanner.nextLine();
        System.out.print("Enter password: ");
        String loginPassword = scanner.nextLine();

        boolean loggedIn = userLogin.loginUser(loginUsername, loginPassword);
        System.out.println(userLogin.returnLoginStatus(loggedIn));
        return loggedIn ? loginUsername : null;
    }

    // ===================== Part 2: QuickChat =====================

    private static void runQuickChat(Scanner scanner, String loggedInUser) {
        System.out.println("\nWelcome to QuickChat.");

        int messageLimit = readPositiveInt(scanner, "Enter the number of messages you wish to send: ");
        int messagesProcessed = 0; // counts messages entered (controls the limit)

        // Part 3: arrays sized from the message limit; stored messages are reloaded from JSON.
        MessageManager manager = new MessageManager(messageLimit, Message.DEFAULT_JSON_FILE);
        int loaded = manager.loadStoredMessages();
        System.out.println(loaded + " stored message(s) loaded from " + Message.DEFAULT_JSON_FILE + ".");

        int choice = 0;
        // while loop: keeps the menu running until the user chooses 3 (Quit)
        while (choice != 3) {
            System.out.println("\n1) Send Messages");
            System.out.println("2) Show recently sent messages");
            System.out.println("3) Quit");
            System.out.println("4) Stored Messages");
            choice = readInt(scanner, "Enter your choice: ");

            switch (choice) {
                case 1:
                    messagesProcessed = enterMessages(scanner, messagesProcessed, messageLimit,
                            loggedInUser, manager);
                    break;
                case 2:
                    System.out.println("Coming Soon.");
                    break;
                case 3:
                    System.out.println("Goodbye.");
                    break;
                case 4:
                    runStoredMessagesMenu(scanner, manager);
                    break;
                default:
                    System.out.println("Invalid option, please choose 1, 2, 3 or 4.");
            }
        }
    }

    // ===================== Part 3: Stored Messages =====================

    /**
     * Submenu for option 4. Repeats until the user chooses 7 (return).
     */
    private static void runStoredMessagesMenu(Scanner scanner, MessageManager manager) {
        int option = 0;
        while (option != 7) {
            System.out.println("\n=== Stored Messages ===");
            System.out.println("1) Display sender and recipient of stored messages");
            System.out.println("2) Display the longest stored message");
            System.out.println("3) Search using a Message ID");
            System.out.println("4) Search all messages for a recipient");
            System.out.println("5) Delete a message using a Message Hash");
            System.out.println("6) Display message report");
            System.out.println("7) Return to main menu");
            option = readInt(scanner, "Enter your choice: ");

            switch (option) {
                case 1:
                    System.out.println(manager.displayStoredSendersAndRecipients());
                    break;
                case 2:
                    System.out.println(manager.displayLongestStoredMessage());
                    break;
                case 3:
                    System.out.print("Enter the Message ID: ");
                    System.out.println(manager.findMessageByID(scanner.nextLine()));
                    break;
                case 4:
                    System.out.print("Enter the recipient's cell number: ");
                    System.out.println(manager.displayMessagesForRecipient(scanner.nextLine()));
                    break;
                case 5:
                    System.out.print("Enter the Message Hash: ");
                    System.out.println(manager.deleteMessageByHash(scanner.nextLine()));
                    break;
                case 6:
                    System.out.println(manager.generateReport());
                    break;
                case 7:
                    System.out.println("Returning to main menu.");
                    break;
                default:
                    System.out.println("Invalid option, please choose 1 to 7.");
            }
        }
    }

    /**
     * for loop: lets the user enter only the number of messages they chose.
     * Returns the updated number of messages processed.
     */
    private static int enterMessages(Scanner scanner, int messagesProcessed, int messageLimit,
            String loggedInUser, MessageManager manager) {
        if (messagesProcessed >= messageLimit) {
            System.out.println("Message limit reached.");
            return messagesProcessed;
        }

        // The loop counter is the message number (starts at 0, as in the PoE hash 00:0:HITONIGHT).
        for (int messageNumber = messagesProcessed; messageNumber < messageLimit; messageNumber++) {
            System.out.println("\nCreating message " + (messageNumber + 1) + " of " + messageLimit + " ...");

            String recipient = readRecipient(scanner);
            String text = readMessageText(scanner);

            Message message = new Message(messageNumber, loggedInUser, recipient, text);
            System.out.println(message.createMessageIdReport());
            System.out.println("Message number: " + message.getMessageNumber());
            System.out.println("Message hash: " + message.getMessageHash());

            handleMessageChoice(scanner, message, manager);
            messagesProcessed++;
        }

        System.out.println("\nMessage limit reached.");
        System.out.println("Total number of messages sent: " + Message.returnTotalMessagess());
        return messagesProcessed;
    }

    /**
     * Asks whether to send, disregard or store the message and shows feedback.
     */
    private static void handleMessageChoice(Scanner scanner, Message message, MessageManager manager) {
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

        // Part 3: record the message in the arrays with its status.
        if (option == Message.SEND) {
            System.out.println(message.getMessageDetails());
            manager.addMessage(message, MessageManager.SENT);
        } else if (option == Message.DISREGARD) {
            String confirm = scanner.nextLine().trim();
            while (!confirm.equals("0")) {
                System.out.println("Press 0 to delete the message.");
                confirm = scanner.nextLine().trim();
            }
            System.out.println("Message deleted.");
            manager.addMessage(message, MessageManager.DISREGARDED);
        } else if (result.equals("Message successfully stored.")) {
            manager.addMessage(message, MessageManager.STORED);
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
