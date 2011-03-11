import org.junit.*;
import java.util.*;

import play.db.jpa.GenericModel;
import play.test.*;
import models.*;
import models.User.DuplicateLoginException;
import models.User.PasswordsDontMatchException;

public class UserTest extends UnitTest {

	private static final String TEST_EMAIL = "user@example.com";
    private static final String TEST_PASSWORD = "secret";
    private static final String TEST_FULLNAME = "Example User";
    private static final String TEST_LOGIN = "exus";
	

    @Test
    public void createAndRetrieveUser() throws PasswordsDontMatchException, DuplicateLoginException {
        forceUserCreate();
    	User user = User.find("byEmail", TEST_EMAIL).first();
    	assertNotNull(user);
    	assertEquals(TEST_FULLNAME, user.fullname);
    	cleanup();
    }
    
    @Test
    public void shoudRejectDuplicateLogins() throws PasswordsDontMatchException, DuplicateLoginException {
    	forceUserCreate();
    	try {
    		userCreate();
    		assertTrue("A user with duplicate user login shouldn't be created", false);
    	} catch (DuplicateLoginException e) {
    		// discard - this is expected
    	} finally {
    		cleanup();
    	}
    }
    
    @Test
    public void shouldConnectValidUser() throws PasswordsDontMatchException, DuplicateLoginException {
    	forceUserCreate();
    	User user = User.connect(TEST_LOGIN, TEST_PASSWORD);
    	assertNotNull(user);
    	assertEquals(TEST_FULLNAME, user.fullname);
    	user = User.connect("fakelogin", TEST_PASSWORD);
    	assertNull(user);
    	user = User.connect(TEST_LOGIN, "fakepass");
    	assertNull(user);
    	cleanup();
    }

    private void userCreate() throws PasswordsDontMatchException, DuplicateLoginException {
    	new User(TEST_LOGIN, TEST_FULLNAME, TEST_EMAIL, TEST_PASSWORD, TEST_PASSWORD).save();
    }
    private void forceUserCreate() throws PasswordsDontMatchException, DuplicateLoginException {
        try {
            userCreate();
        } catch (DuplicateLoginException e) {
            cleanup();
            userCreate();
        }
    }
    private void cleanup() {
        try {
            ((User) User.find("byLogin", TEST_LOGIN ).first()).delete();
        } catch (NullPointerException e) {
            //discard
        }
    }
}
