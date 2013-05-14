package ru.timtish.bridge.services.mailstorage;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.apache.james.mailbox.MailboxSession;

/**
 * @author Timofey Tishin (ttishin@luxoft.com)
 */
public class MailboxUser implements MailboxSession.User {

	private String userName;
	private String password;

	public MailboxUser(String userName, String password) {
		this.userName = userName;
		this.password = password;
	}

	@Override
	public String getUserName() {
		return userName;
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public List<Locale> getLocalePreferences() {
		return Collections.singletonList(Locale.getDefault());
	}
}
