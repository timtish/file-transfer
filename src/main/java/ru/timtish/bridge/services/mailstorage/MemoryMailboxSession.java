package ru.timtish.bridge.services.mailstorage;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.james.mailbox.MailboxSession;

/**
 * @author Timofey Tishin (ttishin@luxoft.com)
 */
public class MemoryMailboxSession implements MailboxSession {

	private static final Log LOG = LogFactory.getLog(MemoryMailboxSession. class);

	private long sessionId;
	private User user;
	private Log log;

	public MemoryMailboxSession(long sessionId, User user, Log log) {
		this.sessionId = sessionId;
		this.user = user;
		this.log = (log != null) ? log : LOG;
	}

	@Override
	public long getSessionId() {
		return sessionId;
	}

	@Override
	public boolean isOpen() {
		return user != null;
	}

	@Override
	public void close() {
		user = null;
	}

	@Override
	public Log getLog() {
		return log;
	}

	@Override
	public User getUser() {
		return null;
	}

	@Override
	public String getPersonalSpace() {
		return null;
	}

	@Override
	public String getOtherUsersSpace() {
		return null;
	}

	@Override
	public Collection<String> getSharedSpaces() {
		return Collections.emptyList();
	}

	@Override
	public Map<Object, Object> getAttributes() {
		return Collections.emptyMap();
	}
}
