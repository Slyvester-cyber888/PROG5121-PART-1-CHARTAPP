package za.ac.iie.prog5121.icetask3.chatapppart_1;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Message holds the information for one QuickChat message and the
 * message-related behaviour required by PoE Part 2: ID generation,
 * recipient validation, length validation, hash creation and the
 * send / disregard / store outcome.
 *
 * JSON storage uses Google Gson (https://github.com/google/gson).
 * Approach adapted from the Gson User Guide:
 * https://github.com/google/gson/blob/main/UserGuide.md
 * (reading a file into a JsonArray with JsonParser and writing it back
 * with Gson's pretty-printing toJson method).
 *
 * @author Slyvester
 */
public class Message {

    // ===== Constants =====
    public static final int MAX_MESSAGE_LENGTH = 250;
    public static final int MESSAGE_ID_LENGTH = 10;
    public static final String DEFAULT_JSON_FILE = "stored_messages.json";

    // Menu choices for what happens to a message
    public static final int SEND = 1;
    public static final int DISREGARD = 2;
    public static final int STORE = 3;

    // ===== Shared state for the current program run =====
    private static final List<Message> sentMessages = new ArrayList<>();
    private static int totalMessagesSent = 0;
    private static final Random random = new Random();

    // ===== Fields for one message =====
    private final String messageId;
    private final int messageNumber;
    private final String recipient;
    private final String messageText;
    private final String messageHash;
    private String sender = "Unknown"; // Part 3: the logged-in user who created the message

    /**
     * Creates a message with a randomly generated ten-digit ID.
     */
    public Message(int messageNumber, String recipient, String messageText) {
        this(generateMessageId(), messageNumber, recipient, messageText);
    }

    /**
     * Part 3: creates a message with a random ID and records who sent it.
     */
    public Message(int messageNumber, String sender, String recipient, String messageText) {
        this(generateMessageId(), messageNumber, recipient, messageText);
        setSender(sender);
    }

    /**
     * Creates a message with a known ID. Used by the unit tests so that
     * the hash is predictable (for example an ID starting with "00").
     */
    public Message(String messageId, int messageNumber, String recipient, String messageText) {
        this.messageId = messageId;
        this.messageNumber = messageNumber;
        this.recipient = recipient;
        this.messageText = messageText;
        this.messageHash = createMessageHash();
    }

    /**
     * Generates a random ten-digit message ID. It is kept as a String so
     * that leading zeroes (e.g. "0012345678") are not lost.
     */
    public static String generateMessageId() {
        StringBuilder id = new StringBuilder();
        for (int i = 0; i < MESSAGE_ID_LENGTH; i++) {
            id.append(random.nextInt(10));
        }
        return id.toString();
    }

    /**
     * Checks that the message ID is no more than ten characters and only digits.
     */
    public boolean checkMessageID() {
        return messageId != null
                && messageId.length() <= MESSAGE_ID_LENGTH
                && messageId.matches("\\d+");
    }

    /**
     * Returns the text reported once an ID has been created.
     */
    public String createMessageIdReport() {
        return "Message ID generated: " + messageId;
    }

    /**
     * Validates the recipient's cell number. It must start with the +27
     * international code followed by nine digits (no more than ten digits
     * after the code), e.g. +27718693002 from the PoE test data.
     */
    public static boolean isValidRecipient(String cellNumber) {
        return cellNumber != null && cellNumber.matches("^\\+27\\d{9}$");
    }

    /**
     * Returns the feedback message for this message's recipient number.
     */
    public String checkRecipientCell() {
        return checkRecipientCell(recipient);
    }

    public static String checkRecipientCell(String cellNumber) {
        if (isValidRecipient(cellNumber)) {
            return "Cell phone number successfully captured.";
        }
        return "Cell phone number is incorrectly formatted or does not contain an international code. "
                + "Please correct the number and try again.";
    }

    /**
     * Checks the message length (maximum 250 characters, spaces included).
     */
    public static String checkMessageLength(String text) {
        int length = (text == null) ? 0 : text.length();
        if (length <= MAX_MESSAGE_LENGTH) {
            return "Message ready to send.";
        }
        int excess = length - MAX_MESSAGE_LENGTH;
        return "Message exceeds 250 characters by " + excess + "; please reduce the size.";
    }

    public static boolean isValidMessageLength(String text) {
        return text != null && !text.trim().isEmpty() && text.length() <= MAX_MESSAGE_LENGTH;
    }

    /**
     * Builds the message hash: first two digits of the ID, a colon, the
     * message number, a colon, then the first and last words of the
     * message joined together, all in capitals. Example: 00:0:HITONIGHT
     */
    public final String createMessageHash() {
        String idPart = messageId.substring(0, 2);

        // Remove punctuation first so a lone "!" or "?" at the end
        // (e.g. "It is dinner time !") is not treated as the last word.
        String[] words = removePunctuationKeepSpaces(messageText).trim().split("\\s+");
        String firstWord = words[0];
        String lastWord = words[words.length - 1];

        String hash = idPart + ":" + messageNumber + ":" + firstWord + lastWord;
        return hash.toUpperCase();
    }

    /**
     * Removes punctuation but keeps the spaces between words
     * (e.g. "Hi Mike, can you join us?" -> "Hi Mike can you join us").
     */
    private static String removePunctuationKeepSpaces(String text) {
        return text.replaceAll("[^A-Za-z0-9\\s]", "");
    }

    /**
     * Applies the user's choice: send, disregard or store the message,
     * and returns the matching feedback.
     */
    public String SentMessage(int choice) {
        return SentMessage(choice, DEFAULT_JSON_FILE);
    }

    public String SentMessage(int choice, String jsonFilePath) {
        switch (choice) {
            case SEND:
                sentMessages.add(this);
                totalMessagesSent++;
                return "Message successfully sent.";
            case DISREGARD:
                // Not added to the sent list and the sent total is not increased.
                return "Press 0 to delete the message.";
            case STORE:
                if (storeMessage(jsonFilePath)) {
                    return "Message successfully stored.";
                }
                return "Message could not be stored.";
            default:
                return "Invalid option.";
        }
    }

    /**
     * Returns the details of this message in the required order:
     * Message ID, Message Hash, Recipient, Message.
     */
    public String getMessageDetails() {
        return "Message ID: " + messageId
                + "\nMessage Hash: " + messageHash
                + "\nRecipient: " + recipient
                + "\nMessage: " + messageText;
    }

    /**
     * Returns all messages sent during the current program run.
     */
    public static String printMessages() {
        if (sentMessages.isEmpty()) {
            return "No messages have been sent.";
        }
        StringBuilder output = new StringBuilder();
        for (Message message : sentMessages) {
            output.append(message.getMessageDetails()).append("\n\n");
        }
        return output.toString().trim();
    }

    /**
     * Returns the number of messages successfully sent.
     */
    public static int returnTotalMessagess() {
        return totalMessagesSent;
    }

    /**
     * Saves this message to a JSON file as part of a JSON array, so it can
     * be sent later. Existing stored messages in the file are kept.
     *
     * Attribution: adapted from the Gson User Guide examples
     * (https://github.com/google/gson/blob/main/UserGuide.md).
     */
    public boolean storeMessage(String jsonFilePath) {
        Path path = Paths.get(jsonFilePath);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try {
            JsonArray storedMessages = new JsonArray();
            if (Files.exists(path)) {
                String existing = Files.readString(path, StandardCharsets.UTF_8);
                if (!existing.isBlank()) {
                    storedMessages = JsonParser.parseString(existing).getAsJsonArray();
                }
            }

            JsonObject json = new JsonObject();
            json.addProperty("messageId", messageId);
            json.addProperty("messageNumber", messageNumber);
            json.addProperty("messageHash", messageHash);
            json.addProperty("sender", sender);
            json.addProperty("recipient", recipient);
            json.addProperty("message", messageText);
            json.addProperty("status", "STORED");
            storedMessages.add(json);

            Files.writeString(path, gson.toJson(storedMessages), StandardCharsets.UTF_8);
            return true;
        } catch (IOException | RuntimeException e) {
            System.out.println("Error storing message: " + e.getMessage());
            return false;
        }
    }

    /**
     * Clears the sent messages. Used by the unit tests so each test starts fresh.
     */
    public static void resetSentMessages() {
        sentMessages.clear();
        totalMessagesSent = 0;
    }

    // ===== Getters =====
    public String getMessageId() {
        return messageId;
    }

    public int getMessageNumber() {
        return messageNumber;
    }

    public String getRecipient() {
        return recipient;
    }

    public String getMessageText() {
        return messageText;
    }

    public String getMessageHash() {
        return messageHash;
    }

    public String getSender() {
        return sender;
    }

    /**
     * Sets the sender (the logged-in username). Blank values become "Unknown".
     */
    public final void setSender(String sender) {
        this.sender = (sender == null || sender.isBlank()) ? "Unknown" : sender;
    }
}
