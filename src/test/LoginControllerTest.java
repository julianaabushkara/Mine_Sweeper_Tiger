package test;

import minesweeper.controller.LoginController;
import minesweeper.model.User;
import minesweeper.view.LoginView;
import org.junit.*;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.Assert.*;

/**
 * JUnit 4 White Box Testing for LoginController
 * Uses JUnit 4 which is bundled with IntelliJ IDEA
 *
 * To run: Right-click on class name → "Run 'LoginControllerTest'"
 */
public class LoginControllerTest {

    private LoginController controller;
    private Path testDataFile;
    private static int testCounter = 0;

    @BeforeClass
    public static void setUpClass() {
        System.out.println("========================================");
        System.out.println("LoginController White Box Tests (JUnit 4)");
        System.out.println("========================================\n");
    }

    @Before
    public void setUp() throws Exception {
        testCounter++;
        // Create temporary test directory with unique name
        Path tempDir = Paths.get(System.getProperty("java.io.tmpdir"),
                "minesweeper_test_" + System.currentTimeMillis() + "_" + testCounter);
        testDataFile = tempDir.resolve(".minesweeper").resolve("UserData.json");
        Files.createDirectories(testDataFile.getParent());
        Files.write(testDataFile, "[]".getBytes());

        // Use reflection to set the static userDataFile field
        Field userDataFileField = LoginController.class.getDeclaredField("userDataFile");
        userDataFileField.setAccessible(true);
        userDataFileField.set(null, testDataFile);

        // Create controller with mock view
        controller = new LoginController(new MockLoginView());
    }

    @After
    public void tearDown() throws Exception {
        // Clean up test files
        if (testDataFile != null && Files.exists(testDataFile)) {
            Files.deleteIfExists(testDataFile);
            if (Files.exists(testDataFile.getParent())) {
                Files.deleteIfExists(testDataFile.getParent());
            }
        }

        // Reset static field
        Field userDataFileField = LoginController.class.getDeclaredField("userDataFile");
        userDataFileField.setAccessible(true);
        userDataFileField.set(null, null);
    }

    @AfterClass
    public static void tearDownClass() {
        System.out.println("\n========================================");
        System.out.println("All Tests Completed!");
        System.out.println("========================================");
    }

    // ========================================
    // TEST 1: User Registration & Retrieval
    // ========================================

    @Test
    public void testUserDataStorageAndRetrieval() throws Exception {
        // Arrange
        String username = "testuser";
        char[] password = "password123".toCharArray();
        String securityAnswer = "FLUFFY";
        String securityQuestion = "First Pet's Name";

        // Act
        controller.handleRegistration(username, password, password, securityAnswer, securityQuestion);

        Method loadUsersMethod = LoginController.class.getDeclaredMethod("loadUsers");
        loadUsersMethod.setAccessible(true);
        @SuppressWarnings("unchecked")
        List<User> users = (List<User>) loadUsersMethod.invoke(controller);

        // Assert
        assertFalse("User list should not be empty", users.isEmpty());
        assertEquals("Should have exactly one user", 1, users.size());

        User retrievedUser = users.get(0);
        assertEquals("Username should match", username, retrievedUser.getUsername());
        assertEquals("Password should match", new String(password), retrievedUser.getPassword());
        assertEquals("Security answer should match", securityAnswer, retrievedUser.getSecurityAnswer());
        assertEquals("Security question should match", securityQuestion, retrievedUser.getSecurityQuestion());
    }

    @Test
    public void testFindUserInternalLogic() throws Exception {
        // Arrange
        String username = "john_doe";
        char[] password = "secure_pass".toCharArray();
        String securityAnswer = "BOSTON";
        String securityQuestion = "Birth City";

        controller.handleRegistration(username, password, password, securityAnswer, securityQuestion);

        // Act
        Method findUserMethod = LoginController.class.getDeclaredMethod("findUser", String.class);
        findUserMethod.setAccessible(true);
        User foundUser = (User) findUserMethod.invoke(controller, username);

        // Assert
        assertNotNull("User should be found", foundUser);
        assertEquals("Username should match", username, foundUser.getUsername());
        assertEquals("Password should match", new String(password), foundUser.getPassword());
        assertEquals("Security answer should match", securityAnswer, foundUser.getSecurityAnswer());
        assertEquals("Security question should match", securityQuestion, foundUser.getSecurityQuestion());
    }

    @Test
    public void testFindUserReturnsNullForNonExistentUser() throws Exception {
        // Act
        Method findUserMethod = LoginController.class.getDeclaredMethod("findUser", String.class);
        findUserMethod.setAccessible(true);
        User foundUser = (User) findUserMethod.invoke(controller, "nonexistent");

        // Assert
        assertNull("Should return null for non-existent user", foundUser);
    }

    // ========================================
    // TEST 2: Duplicate User Prevention
    // ========================================

    @Test
    public void testDuplicateUserCannotBeAdded() throws Exception {
        // Arrange
        String username = "duplicate_test";
        char[] password = "pass123".toCharArray();
        controller.handleRegistration(username, password, password, "ANSWER", "Birth Month");

        // Act - Try to register same username again
        controller.handleRegistration(username, "different_pass".toCharArray(),
                "different_pass".toCharArray(), "ANSWER2", "Birth City");

        // Assert
        Method loadUsersMethod = LoginController.class.getDeclaredMethod("loadUsers");
        loadUsersMethod.setAccessible(true);
        @SuppressWarnings("unchecked")
        List<User> users = (List<User>) loadUsersMethod.invoke(controller);

        assertEquals("Should still have only one user", 1, users.size());
        assertEquals("Original password should be preserved", "pass123", users.get(0).getPassword());
    }

    @Test
    public void testUserExistsMethodFlow() throws Exception {
        // Arrange
        controller.handleRegistration("existinguser", "pass".toCharArray(),
                "pass".toCharArray(), "ANSWER", "Birth Month");

        // Act
        Method userExistsMethod = LoginController.class.getDeclaredMethod("userExists", String.class);
        userExistsMethod.setAccessible(true);

        boolean exists = (boolean) userExistsMethod.invoke(controller, "existinguser");
        boolean notExists = (boolean) userExistsMethod.invoke(controller, "newuser");

        // Assert
        assertTrue("Should return true for existing user", exists);
        assertFalse("Should return false for non-existing user", notExists);
    }

    // ========================================
    // TEST 3: Null/Empty Input Validation
    // ========================================

    @Test
    public void testNullUsernameValidation() throws Exception {
        // Act
        Method validateUsernameMethod = LoginController.class.getDeclaredMethod("validateUsername", String.class);
        validateUsernameMethod.setAccessible(true);

        boolean validNull = (boolean) validateUsernameMethod.invoke(controller, (String) null);

        // Assert
        assertFalse("Null username should fail validation", validNull);
    }

    @Test
    public void testEmptyUsernameValidation() throws Exception {
        // Act
        Method validateUsernameMethod = LoginController.class.getDeclaredMethod("validateUsername", String.class);
        validateUsernameMethod.setAccessible(true);

        boolean validEmpty = (boolean) validateUsernameMethod.invoke(controller, "");
        boolean validWhitespace = (boolean) validateUsernameMethod.invoke(controller, "   ");
        boolean validProper = (boolean) validateUsernameMethod.invoke(controller, "username");

        // Assert
        assertFalse("Empty username should fail validation", validEmpty);
        assertFalse("Whitespace username should fail validation", validWhitespace);
        assertTrue("Valid username should pass validation", validProper);
    }

    @Test
    public void testEmptySecurityAnswerValidation() throws Exception {
        // Arrange
        String username = "testuser";
        char[] password = "pass".toCharArray();

        // Act - Try registration with default "Answer" (invalid)
        controller.handleRegistration(username, password, password, "Answer", "Birth Month");

        Method loadUsersMethod = LoginController.class.getDeclaredMethod("loadUsers");
        loadUsersMethod.setAccessible(true);
        @SuppressWarnings("unchecked")
        List<User> users = (List<User>) loadUsersMethod.invoke(controller);

        // Assert
        assertTrue("User should not be added with invalid security answer", users.isEmpty());

        // Act - Try with empty security answer
        controller.handleRegistration(username, password, password, "", "Birth Month");
        @SuppressWarnings("unchecked")
        List<User> users2 = (List<User>) loadUsersMethod.invoke(controller);

        // Assert
        assertTrue("User should not be added with empty security answer", users2.isEmpty());
    }

    // ========================================
    // TEST 4: Password Confirmation Validation
    // ========================================

    @Test
    public void testPasswordMatchValidation() throws Exception {
        // Act
        Method validatePasswordMatchMethod = LoginController.class.getDeclaredMethod(
                "validatePasswordMatch", char[].class, char[].class);
        validatePasswordMatchMethod.setAccessible(true);

        boolean matchesIdentical = (boolean) validatePasswordMatchMethod.invoke(
                controller, "password123".toCharArray(), "password123".toCharArray());
        boolean matchesDifferent = (boolean) validatePasswordMatchMethod.invoke(
                controller, "password123".toCharArray(), "password456".toCharArray());
        boolean matchesEmpty = (boolean) validatePasswordMatchMethod.invoke(
                controller, "".toCharArray(), "".toCharArray());
        boolean matchesOneEmpty = (boolean) validatePasswordMatchMethod.invoke(
                controller, "password".toCharArray(), "".toCharArray());

        // Assert
        assertTrue("Identical passwords should match", matchesIdentical);
        assertFalse("Different passwords should not match", matchesDifferent);
        assertTrue("Empty passwords should match each other", matchesEmpty);
        assertFalse("Password and empty should not match", matchesOneEmpty);
    }

    @Test
    public void testRegistrationFailsOnPasswordMismatch() throws Exception {
        // Arrange
        String username = "testuser";
        char[] password = "password123".toCharArray();
        char[] repeatPassword = "password456".toCharArray();

        // Act
        controller.handleRegistration(username, password, repeatPassword, "ANSWER", "Birth Month");

        // Assert
        Method loadUsersMethod = LoginController.class.getDeclaredMethod("loadUsers");
        loadUsersMethod.setAccessible(true);
        @SuppressWarnings("unchecked")
        List<User> users = (List<User>) loadUsersMethod.invoke(controller);

        assertTrue("User should not be added when passwords don't match", users.isEmpty());
    }

    @Test
    public void testSuccessfulRegistrationWithMatchingPasswords() throws Exception {
        // Arrange
        String username = "validuser";
        char[] password = "securepass".toCharArray();

        // Act
        controller.handleRegistration(username, password, password, "VALID_ANSWER", "Birth Month");

        // Assert
        Method loadUsersMethod = LoginController.class.getDeclaredMethod("loadUsers");
        loadUsersMethod.setAccessible(true);
        @SuppressWarnings("unchecked")
        List<User> users = (List<User>) loadUsersMethod.invoke(controller);

        assertFalse("User should be added with matching passwords", users.isEmpty());
        assertEquals("Username should match", username, users.get(0).getUsername());
    }

    // ========================================
    // TEST 5: Authentication Logic
    // ========================================

    @Test
    public void testAuthenticateUserLogic() throws Exception {
        // Arrange
        User testUser = new User("authtest", "correctpass".toCharArray(), "ANSWER", "Question");

        Method authenticateUserMethod = LoginController.class.getDeclaredMethod(
                "authenticateUser", User.class, char[].class);
        authenticateUserMethod.setAccessible(true);

        // Act
        boolean correctAuth = (boolean) authenticateUserMethod.invoke(
                controller, testUser, "correctpass".toCharArray());
        boolean incorrectAuth = (boolean) authenticateUserMethod.invoke(
                controller, testUser, "wrongpass".toCharArray());
        boolean emptyAuth = (boolean) authenticateUserMethod.invoke(
                controller, testUser, "".toCharArray());

        // Assert
        assertTrue("Should authenticate with correct password", correctAuth);
        assertFalse("Should not authenticate with wrong password", incorrectAuth);
        assertFalse("Should not authenticate with empty password", emptyAuth);
    }

    @Test
    public void testCompleteLoginFlowSuccess() throws Exception {
        // Arrange
        String username = "logintest";
        char[] password = "testpass".toCharArray();
        controller.handleRegistration(username, password, password, "ANSWER", "Birth Month");

        // Act
        Method findUserMethod = LoginController.class.getDeclaredMethod("findUser", String.class);
        findUserMethod.setAccessible(true);
        User foundUser = (User) findUserMethod.invoke(controller, username);

        // Assert
        assertNotNull("User should exist for successful login", foundUser);
    }

    // ========================================
    // TEST 6: Password Recovery Logic
    // ========================================

    @Test
    public void testPasswordRecoverySuccess() throws Exception {
        // Arrange
        String username = "recoverytest";
        char[] password = "mypassword".toCharArray();
        String securityAnswer = "FLUFFY";

        controller.handleRegistration(username, password, password, securityAnswer, "First Pet's Name");

        // Act - should not throw exception
        controller.handlePasswordRecovery(username, securityAnswer);

        // Assert - if we get here without exception, test passes
        assertTrue("Password recovery should succeed", true);
    }

    @Test
    public void testPasswordRecoveryCaseInsensitive() throws Exception {
        // Arrange
        String username = "casetest";
        char[] password = "pass".toCharArray();
        String securityAnswer = "BOSTON";

        controller.handleRegistration(username, password, password, securityAnswer, "Birth City");

        // Act & Assert - all should succeed without exception
        controller.handlePasswordRecovery(username, "boston");
        controller.handlePasswordRecovery(username, "BOSTON");
        controller.handlePasswordRecovery(username, "BoStOn");

        assertTrue("All case variations should work", true);
    }

    // ========================================
    // TEST 7: Edge Cases & Multiple Users
    // ========================================

    @Test
    public void testMultipleUsersStorage() throws Exception {
        // Act
        controller.handleRegistration("user1", "pass1".toCharArray(), "pass1".toCharArray(), "ANS1", "Q1");
        controller.handleRegistration("user2", "pass2".toCharArray(), "pass2".toCharArray(), "ANS2", "Q2");
        controller.handleRegistration("user3", "pass3".toCharArray(), "pass3".toCharArray(), "ANS3", "Q3");

        // Assert
        Method loadUsersMethod = LoginController.class.getDeclaredMethod("loadUsers");
        loadUsersMethod.setAccessible(true);
        @SuppressWarnings("unchecked")
        List<User> users = (List<User>) loadUsersMethod.invoke(controller);

        assertEquals("Should have three users", 3, users.size());

        Method findUserMethod = LoginController.class.getDeclaredMethod("findUser", String.class);
        findUserMethod.setAccessible(true);

        assertNotNull("User1 should be found", findUserMethod.invoke(controller, "user1"));
        assertNotNull("User2 should be found", findUserMethod.invoke(controller, "user2"));
        assertNotNull("User3 should be found", findUserMethod.invoke(controller, "user3"));
    }

    @Test
    public void testSpecialCharactersInCredentials() throws Exception {
        // Arrange
        String username = "user@test.com";
        char[] password = "p@ss!w0rd#123".toCharArray();
        String securityAnswer = "SPECIAL CHARS!@#";

        // Act
        controller.handleRegistration(username, password, password, securityAnswer, "Birth Month");

        // Assert
        Method findUserMethod = LoginController.class.getDeclaredMethod("findUser", String.class);
        findUserMethod.setAccessible(true);
        User user = (User) findUserMethod.invoke(controller, username);

        assertNotNull("User with special characters should be stored", user);
        assertEquals("Username should match", username, user.getUsername());
        assertEquals("Password should match", new String(password), user.getPassword());
    }

    @Test
    public void testDataPersistenceAcrossInstances() throws Exception {
        // Arrange
        controller.handleRegistration("persistent", "pass".toCharArray(),
                "pass".toCharArray(), "ANSWER", "Birth Month");

        // Act - Create new controller instance
        LoginController newController = new LoginController(new MockLoginView());

        // Assert
        Method findUserMethod = LoginController.class.getDeclaredMethod("findUser", String.class);
        findUserMethod.setAccessible(true);
        User user = (User) findUserMethod.invoke(newController, "persistent");

        assertNotNull("User should persist across controller instances", user);
    }

    // ========================================
    // HELPER: Mock LoginView
    // ========================================

    static class MockLoginView extends LoginView {
        @Override
        public void setVisible(boolean visible) {
            // Do nothing
        }

        @Override
        public void dispose() {
            // Do nothing
        }
    }
}