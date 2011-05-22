package models;

import java.sql.Timestamp;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.apache.commons.codec.digest.DigestUtils;

import play.data.validation.Email;
import play.data.validation.Equals;
import play.data.validation.MinSize;
import play.data.validation.Required;
import play.db.jpa.Model;


@Entity
@Table( name="User",
        uniqueConstraints=
            @UniqueConstraint(columnNames={"id", "login"})
)
public class User extends TemporalModel {
	public class DuplicateLoginException extends Exception {

		public DuplicateLoginException() {
			super("A user with the login already exists.");
		}

	}

	public class PasswordsDontMatchException extends Exception {

	}

    public boolean isAdmin;
    @Required
	public String login;
    @Required
	public String fullname;
	@Email
	public String email;
	@Required
	@MinSize(6)
	public String password;
	public String salt;

	public String toString() {
	    return "User[" + login + ", " + fullname + ", " + salt + "]"; 
	}

	public User(String login, String name, String email, String password,
			String repeatPass) throws PasswordsDontMatchException, DuplicateLoginException {
		User user = find("byLogin", login).first(); 
		if(user != null && user.login.equals(login))
			throw new DuplicateLoginException();
		this.login = login;
		this.fullname = name;
		this.email = email;
		if(password.equals(repeatPass))
			this.password = encrypt(password);
		else
			throw new PasswordsDontMatchException();
	}
	
	public boolean hasPassword(String submittedPassword) {
	    System.out.println("User.hasPassword()\n" + password + "\n" + encrypt(submittedPassword) );
	    return password.equals(encrypt(submittedPassword));
	}

	public static User connect(String login, String password) {
		User user = find("byLogin", login).first();
		System.out.println("User.connect() user = " + user);
		if (user != null && user.hasPassword(password))
			return user;
		else
			return null;
	}
	
	private void makeSalt() {
		salt = secureHash((new Date()).toString());
	}

	@PrePersist
	public void encryptPassword() {
	    String pass = password;
	    password = encrypt(pass);
	}

	public String encrypt(String arg) {
		if(!isPersistent())
			makeSalt();
		return secureHash(salt + "::" + arg);
	}

	private String secureHash(String arg) {
		return DigestUtils.sha512Hex(arg);
	}
	
}
