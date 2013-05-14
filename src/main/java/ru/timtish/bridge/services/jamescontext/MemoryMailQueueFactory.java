package ru.timtish.bridge.services.jamescontext;

import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

import org.apache.james.queue.api.MailQueue;
import org.apache.james.queue.api.MailQueueFactory;
import org.apache.mailet.Mail;

/**
 * @author Timofey Tishin (ttishin@luxoft.com)
 */
public class MemoryMailQueueFactory implements MailQueueFactory {

	private static class SimpleMailQueueItem implements MailQueue.MailQueueItem {

		private Mail mail;

		private Boolean done;

		private Long timeout;

		private SimpleMailQueueItem(Mail mail, Long timeout) {
			this.mail = mail;
			this.timeout = timeout;
		}

		@Override
		public Mail getMail() {
			return mail;
		}

		@Override
		public void done(boolean success) throws MailQueue.MailQueueException {
			done = success;
		}

		public boolean isActual() {
			return done == null && (timeout == null || System.currentTimeMillis() < timeout);
		}
	}

	private static final MailQueue spool = new MailQueue() {

		private LinkedList<SimpleMailQueueItem> mails = new LinkedList<SimpleMailQueueItem>();

		@Override
		public void enQueue(Mail mail, long l, TimeUnit timeUnit) throws MailQueueException {
			mails.add(new SimpleMailQueueItem(mail, System.currentTimeMillis() + timeUnit.toMillis(l)));
		}

		@Override
		public void enQueue(Mail mail) throws MailQueueException {
			mails.add(new SimpleMailQueueItem(mail, null));
		}

		@Override
		public MailQueueItem deQueue() throws MailQueueException {
			SimpleMailQueueItem mail;
			do {
				mail = mails.poll();
			} while (mail != null && !mail.isActual());
			return mail;
		}
	};

	@Override
	public MailQueue getQueue(String s) {
		if (MailQueueFactory.SPOOL.equals(s)) return spool;
		return null;
	}
}
