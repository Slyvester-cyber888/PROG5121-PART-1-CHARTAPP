package za.ac.iie.prog5121.icetask3.chatapppart_1;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Unit tests for PoE Part 3 (MessageManager), using the five official
 * test messages.
 *
 * Interpretation of the official data (documented as the guide asks):
 * - Message 4 is printed as "Developer 0838884567". It is used here both as
 *   message 4's controlled Message ID (for the ID search test) and as the
 *   recipient field exactly as printed.
 * - Message 4 is printed as "It is dinner time !" but the expected result is
 *   "It is dinner time!", so the fixture uses "It is dinner time!".
 * - Controlled IDs are used so that hashes are predictable; the real
 *   application still generates random ten-digit IDs.
 */
public class MessageManagerTest {

    private static final String SENDER = "student_user";

    private static final String MSG1 = "Did you get the cake?";
    private static final String MSG2 = "Where are you? You are late! I have asked you to be on time.";
    private static final String MSG3 = "Yohoooo, I am at your gate.";
    private static final String MSG4 = "It is dinner time!";
    private static final String MSG5 = "Ok, I am leaving without you.";

    private static final String MSG4_ID = "0838884567";

    @TempDir
    Path tempDir;

    private Path jsonFile;
    private MessageManager manager;
    private Message message2;
    private Message message5;

    @BeforeEach
    public void setUp() {
        Message.resetSentMessages();
        jsonFile = tempDir.resolve("stored_messages.json");
        manager = new MessageManager(5, jsonFile.toString());
        loadOfficialTestData();
    }

    /**
     * Processes the five official messages the same way Main does:
     * Message.SentMessage(...) for the user's choice, then addMessage(...)
     * to record it in the arrays.
     */
    private void loadOfficialTestData() {
        process(new Message("1000000001", 0, "+27834557896", MSG1), Message.SEND);
        message2 = new Message("1000000002", 1, "+27838884567", MSG2);
        process(message2, Message.STORE);
        process(new Message("1000000003", 2, "+27834484567", MSG3), Message.DISREGARD);
        process(new Message(MSG4_ID, 3, "0838884567", MSG4), Message.SEND);
        message5 = new Message("1000000005", 4, "+27838884567", MSG5);
        process(message5, Message.STORE);
    }

    private void process(Message message, int choice) {
        message.setSender(SENDER);
        message.SentMessage(choice, jsonFile.toString());
        if (choice == Message.SEND) {
            manager.addMessage(message, MessageManager.SENT);
        } else if (choice == Message.STORE) {
            manager.addMessage(message, MessageManager.STORED);
        } else {
            manager.addMessage(message, MessageManager.DISREGARDED);
        }
    }

    private MessageManager reloadFromJson() {
        MessageManager reloaded = new MessageManager(1, jsonFile.toString());
        reloaded.loadStoredMessages();
        return reloaded;
    }

    // ===================== Arrays correctly populated =====================

    @Test
    public void testSentMessagesArray() {
        assertArrayEquals(new String[]{MSG1, MSG4}, manager.getSentMessages());
        assertEquals(2, manager.getSentCount());
        assertEquals(2, Message.returnTotalMessagess());
    }

    @Test
    public void testDisregardedMessages() {
        assertArrayEquals(new String[]{MSG3}, manager.getDisregardedMessages());
        assertEquals(1, manager.getDisregardedCount());
        assertFalse(contains(manager.getSentMessages(), MSG3));
    }

    @Test
    public void testStoredMessagesArray() {
        assertArrayEquals(new String[]{MSG2, MSG5}, manager.getStoredMessages());
        assertEquals(2, manager.getStoredCount());
    }

    @Test
    public void testMessageHashAndIdArraysMatchMessages() {
        assertEquals(5, manager.getMessageCount());
        assertArrayEquals(new String[]{"1000000001", "1000000002", "1000000003", MSG4_ID, "1000000005"},
                manager.getMessageIDs());
        assertArrayEquals(new String[]{"10:0:DIDCAKE", "10:1:WHERETIME", "10:2:YOHOOOOGATE",
            "08:3:ITTIME", "10:4:OKYOU"}, manager.getMessageHashes());
    }

    // ===================== Read JSON into an array =====================

    @Test
    public void testStoredArrayReadFromJson() {
        MessageManager reloaded = new MessageManager(1, jsonFile.toString());
        assertEquals(2, reloaded.loadStoredMessages());
        assertArrayEquals(new String[]{MSG2, MSG5}, reloaded.getStoredMessages());
        assertArrayEquals(new String[]{"10:1:WHERETIME", "10:4:OKYOU"}, reloaded.getMessageHashes());
        assertEquals(0, reloaded.getSentCount());
    }

    @Test
    public void testDisplayStoredSendersAndRecipients() {
        String output = reloadFromJson().displayStoredSendersAndRecipients();
        assertTrue(output.contains("STORED MESSAGE 1\nSender: student_user\nRecipient: +27838884567"));
        assertTrue(output.contains("STORED MESSAGE 2\nSender: student_user\nRecipient: +27838884567"));
        assertFalse(output.contains("+27834557896")); // sent message 1 is not a stored message
    }

    @Test
    public void testMissingJsonFileLoadsNothing() {
        MessageManager empty = new MessageManager(2, tempDir.resolve("missing.json").toString());
        assertEquals(0, empty.loadStoredMessages());
        assertEquals(0, empty.getStoredCount());
    }

    @Test
    public void testEmptyJsonFileLoadsNothing() throws Exception {
        Path emptyFile = tempDir.resolve("empty.json");
        Files.writeString(emptyFile, "");
        MessageManager empty = new MessageManager(2, emptyFile.toString());
        assertEquals(0, empty.loadStoredMessages());
    }

    @Test
    public void testPunctuationSurvivesJsonReload() {
        Path file = tempDir.resolve("punctuation.json");
        String text = "He said \"wait, please\" - are you sure?";
        Message message = new Message("2000000001", 0, "+27831112222", text);
        message.storeMessage(file.toString());

        MessageManager reloaded = new MessageManager(1, file.toString());
        reloaded.loadStoredMessages();
        assertArrayEquals(new String[]{text}, reloaded.getStoredMessages());
    }

    // ===================== Longest message =====================

    @Test
    public void testLongestStoredMessage() {
        assertEquals(MSG2, manager.findLongestStoredMessage());
        assertTrue(manager.displayLongestStoredMessage().contains("Message ID: 1000000002"));
    }

    @Test
    public void testLongestWithNoStoredMessages() {
        MessageManager empty = new MessageManager(1, tempDir.resolve("none.json").toString());
        assertEquals("No stored messages found.", empty.findLongestStoredMessage());
        assertEquals("No stored messages found.", empty.displayStoredSendersAndRecipients());
    }

    // ===================== Search by message ID =====================

    @Test
    public void testFindMessageById() {
        assertEquals("Recipient: 0838884567\nMessage: It is dinner time!", manager.findMessageByID(MSG4_ID));
    }

    @Test
    public void testUnknownMessageId() {
        assertEquals("Message ID not found.", manager.findMessageByID("9999999999"));
    }

    // ===================== Search by recipient =====================

    @Test
    public void testSearchRecipientMultipleMatches() {
        assertArrayEquals(new String[]{MSG2, MSG5}, manager.searchByRecipient("+27838884567"));
        String output = manager.displayMessagesForRecipient("+27838884567");
        assertTrue(output.contains(MSG2) && output.contains(MSG5));
    }

    @Test
    public void testSearchRecipientNoMatches() {
        assertEquals(0, manager.searchByRecipient("+27820000000").length);
        assertEquals("No messages for this recipient.", manager.displayMessagesForRecipient("+27820000000"));
    }

    @Test
    public void testSearchRecipientIgnoresDisregarded() {
        assertEquals(0, manager.searchByRecipient("+27834484567").length); // message 3 was disregarded
    }

    // ===================== Delete by message hash =====================

    @Test
    public void testDeleteByHash() {
        String result = manager.deleteMessageByHash(message2.getMessageHash());
        assertEquals("Message \"" + MSG2 + "\" successfully deleted.", result);
        assertArrayEquals(new String[]{MSG5}, manager.getStoredMessages());
        assertEquals(4, manager.getMessageCount());
        assertEquals("Message ID not found.", manager.findMessageByID("1000000002"));
        // Arrays stay aligned: the record after the deleted one still matches.
        assertEquals("Recipient: 0838884567\nMessage: It is dinner time!", manager.findMessageByID(MSG4_ID));
    }

    @Test
    public void testDeletedMessageStaysDeleted() {
        manager.deleteMessageByHash("10:1:WHERETIME");
        MessageManager reloaded = reloadFromJson();
        assertArrayEquals(new String[]{MSG5}, reloaded.getStoredMessages());
        assertEquals("Message hash not found.", reloaded.deleteMessageByHash("10:1:WHERETIME"));
    }

    @Test
    public void testDeleteHashIsCaseInsensitive() {
        assertTrue(manager.deleteMessageByHash("10:4:okyou").contains("successfully deleted"));
        assertArrayEquals(new String[]{MSG2}, manager.getStoredMessages());
    }

    @Test
    public void testDeleteFirstMiddleAndLastStored() {
        MessageManager three = new MessageManager(3, tempDir.resolve("three.json").toString());
        three.addRecord("3000000001", "30:0:AA", SENDER, "+27831110001", "A first a", MessageManager.STORED);
        three.addRecord("3000000002", "30:1:BB", SENDER, "+27831110002", "B middle b", MessageManager.STORED);
        three.addRecord("3000000003", "30:2:CC", SENDER, "+27831110003", "C last c", MessageManager.STORED);

        three.deleteMessageByHash("30:1:BB"); // middle
        assertArrayEquals(new String[]{"A first a", "C last c"}, three.getStoredMessages());
        three.deleteMessageByHash("30:2:CC"); // last
        assertArrayEquals(new String[]{"A first a"}, three.getStoredMessages());
        three.deleteMessageByHash("30:0:AA"); // first
        assertEquals(0, three.getStoredCount());
        assertEquals(0, three.getMessageCount());
    }

    @Test
    public void testCannotDeleteSentMessageOrUnknownHash() {
        assertEquals("Message hash not found.", manager.deleteMessageByHash("10:0:DIDCAKE")); // sent
        assertEquals("Message hash not found.", manager.deleteMessageByHash("99:9:NOTHING"));
        assertEquals(2, manager.getSentCount());
        assertEquals(2, manager.getStoredCount());
    }

    // ===================== Report =====================

    @Test
    public void testReport() {
        String report = manager.generateReport();
        assertTrue(report.contains("STATUS: STORED\nMessage ID: 1000000002\nMessage Hash: 10:1:WHERETIME"
                + "\nSender: student_user\nRecipient: +27838884567\nMessage: " + MSG2));
        assertTrue(report.contains("STATUS: SENT\nMessage ID: 1000000001\nMessage Hash: 10:0:DIDCAKE"));
        assertTrue(report.contains("Message Hash: 08:3:ITTIME"));
        assertTrue(report.contains("Total sent: 2\nTotal stored: 2\nDisregarded: 1"));
        assertFalse(report.contains(MSG3)); // disregarded message is not listed
        assertFalse(report.contains("null"));
    }

    @Test
    public void testReportUpdatesAfterDelete() {
        manager.deleteMessageByHash("10:4:OKYOU");
        String report = manager.generateReport();
        assertFalse(report.contains(MSG5));
        assertTrue(report.contains("Total stored: 1"));
    }

    // ===================== Arrays grow when needed =====================

    @Test
    public void testArraysGrowBeyondStartingCapacity() {
        MessageManager small = new MessageManager(1, tempDir.resolve("grow.json").toString());
        for (int i = 0; i < 6; i++) {
            Message message = new Message("400000000" + i, i, "+27831234567", "Message number " + i);
            small.addMessage(message, MessageManager.SENT);
        }
        assertEquals(6, small.getSentCount());
        assertEquals("Message number 5", small.getSentMessages()[5]);
    }

    // ===================== Earlier parts still work =====================

    @Test
    public void testPreviousFeaturesStillWork() {
        Login login = new Login("Kyle", "Smith");
        assertEquals("Registration successful.", login.registerUser("kyl_1", "Ch&&sec@ke99!", "+27838968976"));
        assertTrue(login.loginUser("kyl_1", "Ch&&sec@ke99!"));

        Message message = new Message("0012345678", 0, "+27718693002",
                "Hi Mike, can you join us for dinner tonight?");
        assertEquals("00:0:HITONIGHT", message.getMessageHash());
        assertTrue(new Message(0, "+27718693002", "Hello").checkMessageID());
        assertEquals("Cell phone number successfully captured.", Message.checkRecipientCell("+27718693002"));
    }

    private static boolean contains(String[] array, String value) {
        for (int i = 0; i < array.length; i++) {
            if (array[i].equals(value)) {
                return true;
            }
        }
        return false;
    }
}
