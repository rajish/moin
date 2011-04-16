import models.User;
import models.User.DuplicateLoginException;
import models.User.PasswordsDontMatchException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import play.test.Fixtures;
import play.test.UnitTest;

public class UserTest extends UnitTest {

	private static final String TEST_EMAIL = "user@example.com";
    private static final String TEST_PASSWORD = "secret";
    private static final String TEST_FULLNAME = "Example User";
    private static final String TEST_LOGIN = "exus";
    
    @Before
    public void setUp() {
        Fixtures.deleteAll();
        Fixtures.load("data.yml");
    }

    @Test
    public void createAndRetrieveUser() throws PasswordsDontMatchException, DuplicateLoginException {
        System.out.println("+++UserTest.createAndRetrieveUser()");
    	User user = User.find("byEmail", TEST_EMAIL).first();
    	assertNotNull(user);
    	assertEquals(TEST_FULLNAME, user.fullname);
    	System.out.println("---UserTest.createAndRetrieveUser()");
    }
    
    @Test
    public void shoudRejectDuplicateLogins() throws PasswordsDontMatchException, DuplicateLoginException {
        System.out.println("+++UserTest.shoudRejectDuplicateLogins()");
    	try {
    		userCreate();
    		assertTrue("A user with duplicate user login shouldn't be created", false);
    	} catch (DuplicateLoginException e) {
    		// discard - this is expected
    	}
    	System.out.println("---UserTest.shoudRejectDuplicateLogins()");
    }
    
    @Test
    public void shouldConnectValidUser() throws PasswordsDontMatchException, DuplicateLoginException {
        System.out.println("+++UserTest.shouldConnectValidUser()");
    	User user = User.connect(TEST_LOGIN, TEST_PASSWORD);
    	assertNotNull(user);
    	assertEquals(TEST_FULLNAME, user.fullname);
    	user = User.connect("fakelogin", TEST_PASSWORD);
    	assertNull(user);
    	user = User.connect(TEST_LOGIN, "fakepass");
    	assertNull(user);
    	System.out.println("---UserTest.shouldConnectValidUser()");
    }

    private void userCreate() throws PasswordsDontMatchException, DuplicateLoginException {
    	new User(TEST_LOGIN, TEST_FULLNAME, TEST_EMAIL, TEST_PASSWORD, TEST_PASSWORD).save();
    }

    @After
    public void cleanup() {
        try {
            ((User) User.find("byLogin", TEST_LOGIN ).first()).delete();
        } catch (NullPointerException e) {
            //discard
        }
    }
}
