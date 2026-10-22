package za.ac.iie.prog5121.icetask3.chatapppart_1;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * MessageManager keeps the QuickChat message data for PoE Part 3 in arrays
 * and provides the Stored Messages features: display sender and recipient,
 * longest stored message, search by message ID, search by recipient,
 * delete by message hash and the message report.
 *
 * Parallel arrays: the same index in messageIDs, messageHashes, senders,
 * recipients, messageTexts and statuses always describes the same message.
 *
 * The five arrays named in the PoE are:
 *   sentMessages, disregardedMessages, storedMessages, messageHashes, messageIDs
 *
 * Reading JSON into Java objects uses Google Gson (https://github.com/google/gson).
 * Approach adapted from the Gson User Guide, "Array Examples" and
 * "Object Examples" (gson.fromJson(reader, Type[].class) and gson.toJson):
 * https://github.com/google/gson/blob/main/UserGuide.md
 *
 * @author Slyvester
 */
public class MessageManager {

    // ===== Status values =====
    public static final String SENT = "SENT";
    public static final String STORED = "STORED";
    public static final String DISREGARDED = "DISREGARDED";

    public static final String NO_STORED_MESSAGES = "No stored messages found.";
    public static final String ID_NOT_FOUND = "Message ID not found.";
    public static final String HASH_NOT_FOUND = "Message hash not found.";

    // ===== Parallel arrays: one complete record per index =====
    private String[] messageIDs;
    private String[] messageHashes;
    private String[] senders;
    private String[] recipients;
    private String[] messageTexts;
    private String[] statuses;

    // ===== Category arrays (message text only) =====
    private String[] sentMessages;
    private String[] disregardedMessages;
    private String[] storedMessages;

    // ===== Counters =====
    private int messageCount = 0;      // all records in the parallel arrays
    private int sentCount = 0;         // only SENT
    private int storedCount = 0;       // only STORED (including records loaded from JSON)
    private int disregardedCount = 0;  // only DISREGARDED

    // JSON file used for stored messages
    private final String jsonFilePath;

    /**
     * @param capacity     starting array size (the user's message limit)
     * @param jsonFilePath file that stored messages are read from and written to
     */
    public MessageManager(int capacity, String jsonFilePath) {
        int size = Math.max(capacity, 1);
        messageIDs = new String[size];
        messageHashes = new String[size];
        senders = new String[size];
        recipients = new String[size];
        messageTexts = new String[size];
        statuses = new String[size];
        sentMessages = new String[size];
        disregardedMessages = new String[size];
        storedMessages = new String[size];
        this.jsonFilePath = jsonFilePath;
    }

    // ===================== Adding records =====================

    /**
     * Adds a message after the user chose Send, Store or Disregard.
     */
    public void addMessage(Message message, String status) {
        addRecord(message.getMessageId(), message.getMessageHash(), message.getSender(),
                message.getRecipient(), message.getMessageText(), status);
    }

    /**
     * Stores one complete record at the same index of every parallel array,
     * then updates the matching category array.
     */
    public void addRecord(String id, String hash, String sender, String recipient,
            String text, String status) {
        ensureCapacity(messageCount + 1);

        messageIDs[messageCount] = id;
        messageHashes[messageCount] = hash;
        senders[messageCount] = sender;
        recipients[messageCount] = recipient;
        messageTexts[messageCount] = text;
        statuses[messageCount] = status;
        messageCount++;

        addToCategory(text, status);
    }

    private void addToCategory(String text, String status) {
        if (SENT.equals(status)) {
            sentMessages[sentCount] = text;
            sentCount++;
        } else if (STORED.equals(status)) {
            storedMessages[storedCount] = text;
            storedCount++;
        } else if (DISREGARDED.equals(status)) {
            disregardedMessages[disregardedCount] = text;
            disregardedCount++;
        }
    }

    /**
     * Makes every array bigger (double the size) when it is full, so that
     * messages reloaded from JSON always fit. Copies the old values across.
     */
    private void ensureCapacity(int needed) {
        if (needed <= messageIDs.length) {
            return;
        }
        int newSize = Math.max(needed, messageIDs.length * 2);
        messageIDs = grow(messageIDs, newSize);
        messageHashes = grow(messageHashes, newSize);
        senders = grow(senders, newSize);
        recipients = grow(recipients, newSize);
        messageTexts = grow(messageTexts, newSize);
        statuses = grow(statuses, newSize);
        sentMessages = grow(sentMessages, newSize);
        disregardedMessages = grow(disregardedMessages, newSize);
        storedMessages = grow(storedMessages, newSize);
    }

    private static String[] grow(String[] oldArray, int newSize) {
        String[] newArray = new String[newSize];
        for (int i = 0; i < oldArray.length; i++) {
            newArray[i] = oldArray[i];
        }
        return newArray;
    }

    // ===================== JSON: read and write =====================

    /**
     * Shape of one stored message in the JSON file. Field names match the
     * JSON properties written by Message.storeMessage().
     */
    private static class StoredMessageRecord {
        String messageId;
        int messageNumber;
        String messageHash;
        String sender;
        String recipient;
        String message;
        String status;
    }

    /**
     * Reads the stored messages from the JSON file into the arrays.
     * A missing or empty file simply leaves the stored arrays empty.
     *
     * Attribution: adapted from the Gson User Guide
     * (https://github.com/google/gson/blob/main/UserGuide.md).
     *
     * @return the number of stored messages loaded
     */
    public int loadStoredMessages() {
        Path path = Paths.get(jsonFilePath);
        if (!Files.exists(path)) {
            return 0;
        }
        try (Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            StoredMessageRecord[] saved = new Gson().fromJson(reader, StoredMessageRecord[].class);
            if (saved == null) {
                return 0; // empty file
            }
            int loaded = 0;
            for (int i = 0; i < saved.length; i++) {
                StoredMessageRecord record = saved[i];
                if (record == null || record.messageId == null || record.message == null) {
                    continue; // skip incomplete records
                }
                String sender = (record.sender == null || record.sender.isBlank())
                        ? "Unknown" : record.sender;
                addRecord(record.messageId, record.messageHash, sender,
                        record.recipient, record.message, STORED);
                loaded++;
            }
            return loaded;
        } catch (IOException | JsonParseException e) {
            System.out.println("Could not read stored messages: " + e.getMessage());
            return 0;
        }
    }

    /**
     * Writes every STORED record back to the JSON file, for example after a
     * deletion, so the file and the arrays agree after a restart.
     */
    public boolean saveStoredMessages() {
        StoredMessageRecord[] records = new StoredMessageRecord[storedCount];
        int next = 0;
        for (int i = 0; i < messageCount; i++) {
            if (STORED.equals(statuses[i])) {
                StoredMessageRecord record = new StoredMessageRecord();
                record.messageId = messageIDs[i];
                record.messageNumber = readMessageNumber(messageHashes[i]);
                record.messageHash = messageHashes[i];
                record.sender = senders[i];
                record.recipient = recipients[i];
                record.message = messageTexts[i];
                record.status = STORED;
                records[next] = record;
                next++;
            }
        }
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (Writer writer = Files.newBufferedWriter(Paths.get(jsonFilePath), StandardCharsets.UTF_8)) {
            gson.toJson(records, writer);
            return true;
        } catch (IOException e) {
            System.out.println("Could not save stored messages: " + e.getMessage());
            return false;
        }
    }

    /**
     * The message number is the part between the two colons of the hash (e.g. 00:1:WHERETIME -> 1).
     */
    private static int readMessageNumber(String hash) {
        try {
            String[] parts = hash.split(":");
            return Integer.parseInt(parts[1]);
        } catch (RuntimeException e) {
            return 0;
        }
    }

    // ===================== Stored Messages features =====================

    /**
     * Returns the sender and recipient of every stored message.
     */
    public String displayStoredSendersAndRecipients() {
        if (storedCount == 0) {
            return NO_STORED_MESSAGES;
        }
        StringBuilder output = new StringBuilder();
        int number = 1;
        for (int i = 0; i < messageCount; i++) {
            if (STORED.equals(statuses[i])) {
                output.append("STORED MESSAGE ").append(number)
                        .append("\nSender: ").append(senders[i])
                        .append("\nRecipient: ").append(recipients[i])
                        .append("\n\n");
                number++;
            }
        }
        return output.toString().trim();
    }

    /**
     * Returns the text of the longest stored message (compares message length only).
     */
    public String findLongestStoredMessage() {
        if (storedCount == 0) {
            return NO_STORED_MESSAGES;
        }
        String longest = storedMessages[0];
        for (int i = 1; i < storedCount; i++) {
            if (storedMessages[i].length() > longest.length()) {
                longest = storedMessages[i];
            }
        }
        return longest;
    }

    /**
     * Returns the full details of the longest stored message (ID, recipient and text).
     */
    public String displayLongestStoredMessage() {
        int index = indexOfLongestStored();
        if (index == -1) {
            return NO_STORED_MESSAGES;
        }
        return "Longest stored message:"
                + "\nMessage ID: " + messageIDs[index]
                + "\nRecipient: " + recipients[index]
                + "\nMessage: " + messageTexts[index];
    }

    private int indexOfLongestStored() {
        int longestIndex = -1;
        for (int i = 0; i < messageCount; i++) {
            if (STORED.equals(statuses[i])
                    && (longestIndex == -1 || messageTexts[i].length() > messageTexts[longestIndex].length())) {
                longestIndex = i;
            }
        }
        return longestIndex;
    }

    /**
     * Searches every message for a message ID and returns its recipient and text.
     */
    public String findMessageByID(String id) {
        String wanted = (id == null) ? "" : id.trim();
        for (int i = 0; i < messageCount; i++) {
            if (messageIDs[i].equals(wanted)) {
                return "Recipient: " + recipients[i] + "\nMessage: " + messageTexts[i];
            }
        }
        return ID_NOT_FOUND;
    }

    /**
     * Returns every sent or stored message for a recipient. Unlike the ID
     * search this does not stop at the first match.
     */
    public String[] searchByRecipient(String recipient) {
        String wanted = (recipient == null) ? "" : recipient.trim();

        // First count the matches so the result array is the right size.
        int matches = 0;
        for (int i = 0; i < messageCount; i++) {
            if (isSentOrStored(i) && recipients[i].equals(wanted)) {
                matches++;
            }
        }

        String[] results = new String[matches];
        int next = 0;
        for (int i = 0; i < messageCount; i++) {
            if (isSentOrStored(i) && recipients[i].equals(wanted)) {
                results[next] = messageTexts[i];
                next++;
            }
        }
        return results;
    }

    /**
     * Console-friendly version of the recipient search.
     */
    public String displayMessagesForRecipient(String recipient) {
        String[] results = searchByRecipient(recipient);
        if (results.length == 0) {
            return "No messages for this recipient.";
        }
        StringBuilder output = new StringBuilder("Messages for " + recipient.trim() + ":");
        for (int i = 0; i < results.length; i++) {
            output.append("\n").append(results[i]);
        }
        return output.toString();
    }

    private boolean isSentOrStored(int index) {
        return SENT.equals(statuses[index]) || STORED.equals(statuses[index]);
    }

    /**
     * Deletes a stored message using its message hash. All parallel arrays
     * are shifted together, the category arrays are rebuilt and the JSON
     * file is rewritten so the deletion lasts after a restart.
     */
    public String deleteMessageByHash(String hash) {
        String wanted = (hash == null) ? "" : hash.trim().toUpperCase();

        int deleteIndex = -1;
        for (int i = 0; i < messageCount; i++) {
            if (STORED.equals(statuses[i]) && messageHashes[i].equals(wanted)) {
                deleteIndex = i;
                break;
            }
        }
        if (deleteIndex == -1) {
            return HASH_NOT_FOUND;
        }

        String deletedText = messageTexts[deleteIndex];

        // Shift every later record one place left so the indexes stay aligned.
        for (int j = deleteIndex; j < messageCount - 1; j++) {
            messageIDs[j] = messageIDs[j + 1];
            messageHashes[j] = messageHashes[j + 1];
            senders[j] = senders[j + 1];
            recipients[j] = recipients[j + 1];
            messageTexts[j] = messageTexts[j + 1];
            statuses[j] = statuses[j + 1];
        }
        messageCount--;
        messageIDs[messageCount] = null;
        messageHashes[messageCount] = null;
        senders[messageCount] = null;
        recipients[messageCount] = null;
        messageTexts[messageCount] = null;
        statuses[messageCount] = null;

        rebuildCategoryArrays();
        saveStoredMessages();

        return "Message \"" + deletedText + "\" successfully deleted.";
    }

    /**
     * Rebuilds the sent, stored and disregarded arrays and counters from the
     * parallel arrays (used after a deletion).
     */
    private void rebuildCategoryArrays() {
        for (int i = 0; i < sentMessages.length; i++) {
            sentMessages[i] = null;
            storedMessages[i] = null;
            disregardedMessages[i] = null;
        }
        sentCount = 0;
        storedCount = 0;
        disregardedCount = 0;
        for (int i = 0; i < messageCount; i++) {
            addToCategory(messageTexts[i], statuses[i]);
        }
    }

    /**
     * Returns the full report: every stored and sent message with its
     * status, ID, hash, sender, recipient and text, plus the totals.
     */
    public String generateReport() {
        String line = "-----------------------------------------------";
        StringBuilder report = new StringBuilder("QUICKCHAT MESSAGE REPORT\n").append(line).append("\n");

        if (sentCount + storedCount == 0) {
            report.append("No sent or stored messages to report.\n").append(line).append("\n");
        }
        appendSection(report, STORED, line);
        appendSection(report, SENT, line);

        report.append("Total sent: ").append(sentCount)
                .append("\nTotal stored: ").append(storedCount)
                .append("\nDisregarded: ").append(disregardedCount);
        return report.toString();
    }

    private void appendSection(StringBuilder report, String status, String line) {
        for (int i = 0; i < messageCount; i++) {
            if (status.equals(statuses[i])) {
                report.append("STATUS: ").append(statuses[i])
                        .append("\nMessage ID: ").append(messageIDs[i])
                        .append("\nMessage Hash: ").append(messageHashes[i])
                        .append("\nSender: ").append(senders[i])
                        .append("\nRecipient: ").append(recipients[i])
                        .append("\nMessage: ").append(messageTexts[i])
                        .append("\n").append(line).append("\n");
            }
        }
    }

    // ===================== Getters (copies, without empty cells) =====================

    public String[] getSentMessages() {
        return copyOf(sentMessages, sentCount);
    }

    public String[] getStoredMessages() {
        return copyOf(storedMessages, storedCount);
    }

    public String[] getDisregardedMessages() {
        return copyOf(disregardedMessages, disregardedCount);
    }

    public String[] getMessageHashes() {
        return copyOf(messageHashes, messageCount);
    }

    public String[] getMessageIDs() {
        return copyOf(messageIDs, messageCount);
    }

    private static String[] copyOf(String[] source, int count) {
        String[] copy = new String[count];
        for (int i = 0; i < count; i++) {
            copy[i] = source[i];
        }
        return copy;
    }

    public int getMessageCount() {
        return messageCount;
    }

    public int getSentCount() {
        return sentCount;
    }

    public int getStoredCount() {
        return storedCount;
    }

    public int getDisregardedCount() {
        return disregardedCount;
    }
}
