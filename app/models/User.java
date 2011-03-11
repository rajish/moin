package models;

import java.util.Date;

import javax.persistence.Entity;

import org.apache.commons.codec.digest.DigestUtils;

import play.db.jpa.Model;


@Entity
public class User extends Model {
	public class DuplicateLoginException extends Exception {

		public DuplicateLoginException() {
			super("A user with the login already exists.");
		}

	}

	public class PasswordsDontMatchException extends Exception {

	}

	public String login;
	public String fullname;
	public String email;
	public String password;
	public String salt;
	
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
		return password.equals(encrypt(submittedPassword));
	}

	public static User connect(String login, String password) {
		User user = find("byLogin", login).first();
		if (user != null && user.hasPassword(password))
			return user;
		else
			return null;
	}
	
	private void makeSalt() {
		salt = secureHash((new Date()).toString());
	}

	private String encrypt(String arg) {
		if(!isPersistent())
			makeSalt();
		return secureHash(salt + "::" + arg);
	}

	private String secureHash(String arg) {
		return DigestUtils.sha512Hex(arg);
	}
	
}
