package ru.timtish.bridge.services.mailstorage;

import java.util.logging.Logger;

import org.apache.james.user.api.User;

/**
 * @author Timofey Tishin (ttishin@luxoft.com)
 */
public class ExternalUser implements User {

	private static final Logger LOG	= Logger.getLogger(ExternalUser.class.getName());

	private String userName;
	private String password;

	public ExternalUser() {
	}

	public ExternalUser(String userName, String password) {
		this.userName = userName;
		this.password = password;
	}

	@Override
	public String getUserName() {
		return userName;
	}

	@Override
	public boolean verifyPassword(String pass) {
		LOG.fine("verifyPassword '" + pass + "' for user " + userName +  " pass: '" + password + "'");
		return String.valueOf(pass).equals(String.valueOf(password));
	}

	@Override
	public boolean setPassword(String newPass) {
		password = newPass;
		return true;
	}
}
