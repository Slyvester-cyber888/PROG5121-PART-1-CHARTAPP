import za.ac.iie.prog5121.icetask3.chatapppart_1;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;


public class LoginTest {

    // Valid test data from the POE
    private final String validUsername = "Slyvl_1";
    private final String validPassword = "Ch&sec@ke99!";
    private final String validCellPhone = "+27838968976";

    // ========== registerUser() Tests (assertEquals) ==========

    @Test
    public void testRegisterUserUsernameCaptured() {
        Login login = new Login("Slyvester", "Sibonakaliso");
        String result = login.registerUser(validUsername, validPassword, validCellPhone);
        assertEquals("Registration successful.", result);
    }

    @Test
    public void testRegisterUserPasswordCaptured() {
        Login login = new Login("Slyvester", "Sibonakaliso");
        String result = login.registerUser(validUsername, validPassword, validCellPhone);
        assertEquals("Registration successful.", result);
    }

    @Test
    public void testRegisterUserCellPhoneAdded() {
        Login login = new Login("Slyvester", "Sibonakaliso");
        String result = login.registerUser(validUsername, validPassword, validCellPhone);
        assertEquals("Registration successful.", result);
    }

    @Test
    public void testRegisterUserUsernameIncorrectlyFormatted() {
        Login login = new Login("Slyvester", "Sibonakaliso");
        String result = login.registerUser("kyle!!!!!!", validPassword, validCellPhone);
        assertEquals("Username is not correctly formatted; please ensure that your username contains an underscore and is no more than five characters in length.", result);
    }

    @Test
    public void testRegisterUserPasswordIncorrectlyFormatted() {
        Login login = new Login("Slyvester", "Sibonakaliso");
        String result = login.registerUser(validUsername, "password", validCellPhone);
        assertEquals("Password is not correctly formatted; please ensure that the password contains at least eight characters, a capital letter, a number, and a special character.", result);
    }

    @Test
    public void testRegisterUserCellPhoneIncorrectlyFormatted() {
        Login login = new Login("Slyvester", "Sibonakaliso");
        String result = login.registerUser(validUsername, validPassword, "08966553");
        assertEquals("Cell number is incorrectly formatted or does not contain an international code; please correct the number and try again.", result);
    }

    // ========== Boolean method tests (assertTrue / assertFalse) ==========

    @Test
    public void testCheckUserNameCorrectlyFormatted() {
        Login login = new Login();
        assertTrue(login.checkUserName("Slyvl_1"));
    }

    @Test
    public void testCheckUserNameIncorrectlyFormatted() {
        Login login = new Login();
        assertFalse(login.checkUserName("Slyvester!!!!!!"));
    }

    @Test
    public void testCheckPasswordComplexityMeetsRequirements() {
        Login login = new Login();
        assertTrue(login.checkPasswordComplexity("Ch&sec@ke99!"));
    }

    @Test
    public void testCheckPasswordComplexityDoesNotMeetRequirements() {
        Login login = new Login();
        assertFalse(login.checkPasswordComplexity("password"));
    }

    @Test
    public void testCheckCellPhoneNumberCorrectlyFormatted() {
        Login login = new Login();
        assertTrue(login.checkCellPhoneNumber("+27838968976"));
    }

    @Test
    public void testCheckCellPhoneNumberIncorrectlyFormatted() {
        Login login = new Login();
        assertFalse(login.checkCellPhoneNumber("08966553"));
    }

    // ========== loginUser() Tests ==========

    @Test
    public void testLoginSuccessful() {
        Login login = new Login("Slyvester", "Sibonakaliso");
        login.registerUser(validUsername, validPassword, validCellPhone);

        boolean result = login.loginUser(validUsername, validPassword);
        assertTrue(result);
    }

    @Test
    public void testLoginFailed() {
        Login login = new Login("Slyvester", "Sibonakaliso");
        login.registerUser(validUsername, validPassword, validCellPhone);

        boolean result = login.loginUser("wrong", "wrong");
        assertFalse(result);
    }

    // ========== returnLoginStatus() Tests ==========

    @Test
    public void testReturnLoginStatusSuccessful() {
        Login login = new Login("Slyvester", "Sibonakaliso");
        login.registerUser(validUsername, validPassword, validCellPhone);

        String status = login.returnLoginStatus(true);
        assertEquals("Welcome Slyvester,Sibonakaliso it is great to see you again.", status);
    }

    @Test
    public void testReturnLoginStatusFailed() {
        Login login = new Login("Slyvester", "Siboanakaliso");
        String status = login.returnLoginStatus(false);
        assertEquals("Username or password incorrect, please try again.", status);
    }
}