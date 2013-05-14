package ru.timtish.bridge.services.mailstorage;

import java.util.Collection;
import java.util.Collections;

import org.apache.james.mailbox.MailboxSession;
import org.apache.james.mailbox.SubscriptionException;
import org.apache.james.mailbox.SubscriptionManager;

/**
 * @author Timofey Tishin (ttishin@luxoft.com)
 */
public class MemoryMailSubscriptionManager implements SubscriptionManager {

	@Override
	public void subscribe(MailboxSession session, String mailbox) throws SubscriptionException {
	}

	@Override
	public Collection<String> subscriptions(MailboxSession session) throws SubscriptionException {
		return Collections.emptyList();
	}

	@Override
	public void unsubscribe(MailboxSession session, String mailbox) throws SubscriptionException {
	}

	@Override
	public void startProcessingRequest(MailboxSession session) {
	}

	@Override
	public void endProcessingRequest(MailboxSession session) {
	}
}
