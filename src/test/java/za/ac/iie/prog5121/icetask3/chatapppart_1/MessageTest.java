package za.ac.iie.prog5121.icetask3.chatapppart_1;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Unit tests for the Part 2 Message class, using the PoE test data.
 */
public class MessageTest {

    // PoE test data
    private final String validRecipient = "+27718693002";
    private final String message1 = "Hi Mike, can you join us for dinner tonight?";
    private final String invalidRecipient = "08575975889";
    private final String message2 = "Hi Keegan, did you receive the payment?";

    @TempDir
    Path tempDir;

    @BeforeEach
    public void setUp() {
        Message.resetSentMessages();
    }

    // ===== Test 1: main message is sent and the hash is correct =====

    @Test
    public void testMessageHashMatchesPoeExample() {
        Message message = new Message("0012345678", 0, validRecipient, message1);
        assertEquals("00:0:HITONIGHT", message.createMessageHash());
    }

    @Test
    public void testSendMessage() {
        Message message = new Message("0012345678", 0, validRecipient, message1);
        assertEquals("Message successfully sent.", message.SentMessage(Message.SEND));
        assertEquals(1, Message.returnTotalMessagess());
    }

    // ===== Test 2: second message has an invalid number and is disregarded =====

    @Test
    public void testDisregardDoesNotCountAsSent() {
        Message message = new Message(1, invalidRecipient, message2);
        assertEquals("Press 0 to delete the message.", message.SentMessage(Message.DISREGARD));
        assertEquals(0, Message.returnTotalMessagess());
    }

    // ===== Test 3 and 4: message length =====

    @Test
    public void testMessageOf250CharactersIsAccepted() {
        String text = "a".repeat(250);
        assertEquals("Message ready to send.", Message.checkMessageLength(text));
        assertTrue(Message.isValidMessageLength(text));
    }

    @Test
    public void testMessageTooLong() {
        String text = "a".repeat(257);
        assertEquals("Message exceeds 250 characters by 7; please reduce the size.",
                Message.checkMessageLength(text));
        assertFalse(Message.isValidMessageLength(text));
    }

    // ===== Test 5: recipient format =====

    @Test
    public void testRecipientCorrectlyFormatted() {
        assertEquals("Cell phone number successfully captured.",
                Message.checkRecipientCell(validRecipient));
        assertTrue(Message.isValidRecipient(validRecipient));
    }

    @Test
    public void testRecipientIncorrectlyFormatted() {
        assertEquals("Cell phone number is incorrectly formatted or does not contain an international code. "
                + "Please correct the number and try again.",
                Message.checkRecipientCell(invalidRecipient));
        assertFalse(Message.isValidRecipient(invalidRecipient));
    }

    // ===== Test 6: message ID creation =====

    @Test
    public void testMessageIdIsTenDigits() {
        Message message = new Message(0, validRecipient, message1);
        assertTrue(message.checkMessageID());
        assertEquals(10, message.getMessageId().length());
        assertTrue(message.getMessageId().matches("\\d{10}"));
        assertEquals("Message ID generated: " + message.getMessageId(), message.createMessageIdReport());
    }

    @Test
    public void testMessageIdTooLongFails() {
        Message message = new Message("12345678901", 0, validRecipient, message1);
        assertFalse(message.checkMessageID());
    }

    // ===== Test 7: send choices =====

    @Test
    public void testStoreMessageWritesJsonAndDoesNotCountAsSent() throws Exception {
        Path file = tempDir.resolve("stored_messages.json");
        Message message = new Message("0012345678", 0, validRecipient, message1);

        assertEquals("Message successfully stored.", message.SentMessage(Message.STORE, file.toString()));
        assertEquals(0, Message.returnTotalMessagess());

        JsonArray stored = JsonParser.parseString(Files.readString(file)).getAsJsonArray();
        assertEquals(1, stored.size());
        assertEquals("0012345678", stored.get(0).getAsJsonObject().get("messageId").getAsString());
        assertEquals("00:0:HITONIGHT", stored.get(0).getAsJsonObject().get("messageHash").getAsString());
    }

    @Test
    public void testSendDisregardStoreTotals() {
        Path file = tempDir.resolve("stored.json");
        new Message(0, validRecipient, message1).SentMessage(Message.SEND);
        new Message(1, validRecipient, message2).SentMessage(Message.DISREGARD);
        new Message(2, validRecipient, "Did you get the cake?").SentMessage(Message.STORE, file.toString());
        assertEquals(1, Message.returnTotalMessagess());
    }

    @Test
    public void testPrintMessagesShowsDetailsInOrder() {
        Message message = new Message("0012345678", 0, validRecipient, message1);
        message.SentMessage(Message.SEND);
        String expected = "Message ID: 0012345678"
                + "\nMessage Hash: 00:0:HITONIGHT"
                + "\nRecipient: +27718693002"
                + "\nMessage: " + message1;
        assertEquals(expected, Message.printMessages());
    }

    // ===== Test 8: several messages in a loop =====

    @Test
    public void testHashAndMessageNumberInLoop() {
        String[] texts = {
            "Hi Mike, can you join us for dinner tonight?",
            "Hi Keegan, did you receive the payment?",
            "Good morning team!"
        };
        String[] expected = {"00:0:HITONIGHT", "00:1:HIPAYMENT", "00:2:GOODTEAM"};

        for (int i = 0; i < texts.length; i++) {
            Message message = new Message("0012345678", i, validRecipient, texts[i]);
            assertEquals(i, message.getMessageNumber());
            assertEquals(expected[i], message.createMessageHash());
        }
    }
}
