package ru.timtish.bridge.services.mailstorage;

import java.io.InputStream;
import java.util.*;
import java.util.logging.Logger;

import javax.mail.Flags;

import org.apache.commons.logging.Log;
import org.apache.james.mailbox.*;
import org.springframework.beans.factory.annotation.Autowired;
import ru.timtish.bridge.box.BoxUtil;
import ru.timtish.bridge.box.StreamsBox;
import ru.timtish.bridge.pipeline.AbstractStream;
import ru.timtish.bridge.pipeline.cache.AutoClosableInputStream;
import ru.timtish.bridge.pipeline.cache.CacheInitializer;
import ru.timtish.bridge.pipeline.cache.CachedInMemoryInputStream;

/**
 * @author Timofey Tishin (ttishin@luxoft.com)
 */
public class MemoryMailboxManager implements MailboxManager {

	private static final Logger LOG = Logger.getLogger(MemoryMailboxManager.class.getName());

	private static final Random RANDOM = new Random();

	@Autowired
	private StreamsBox streamsBox;

	private MessageManager messageManager = new MessageManager() {
		@Override
		public long getMessageCount(MailboxSession mailboxSession) throws MailboxException {
			LOG.info("get count for " + mailboxSession.toString() + " " + mailboxSession.getUser().getUserName());
			return 0;
		}

		@Override
		public boolean isWriteable(MailboxSession mailboxSession) {
			return true;
		}

		@Override
		public Iterator<Long> search(SearchQuery searchQuery, MailboxSession mailboxSession) throws MailboxException {
			return Collections.<Long>emptyList().iterator();
		}

		@Override
		public Iterator<Long> expunge(MessageRange messageRange, MailboxSession mailboxSession) throws MailboxException {
			return Collections.<Long>emptyList().iterator();
		}

		@Override
		public Map<Long, Flags> setFlags(Flags flags, boolean b, boolean b1, MessageRange messageRange, MailboxSession mailboxSession) throws MailboxException {
			return Collections.emptyMap();
		}

		@Override
		public long appendMessage(InputStream inputStream, Date date, MailboxSession mailboxSession, boolean b, Flags flags) throws MailboxException {
			CachedInMemoryInputStream cache = new CachedInMemoryInputStream(new AutoClosableInputStream(inputStream, null), null);
			cache.init();
			String key = UUID.randomUUID().toString();
			AbstractStream stream = new AbstractStream(inputStream, (Long) null, BoxUtil.safeFileName("mail_" + mailboxSession.getUser().getUserName()), mailboxSession.getUser().getUserName(), null);
			stream.setRepeatable(true);
			//stream.setContentType("");
			streamsBox.addStreams(key, stream);
			new Thread(new CacheInitializer(stream)).start();
			return RANDOM.nextLong();
		}

		@Override
		public Iterator<MessageResult> getMessages(MessageRange messageRange, MessageResult.FetchGroup fetchGroup, MailboxSession mailboxSession) throws MailboxException {
			return Collections.<MessageResult>emptyList().iterator();
		}

		@Override
		public MetaData getMetaData(boolean resetRecent, MailboxSession mailboxSession, MetaData.FetchGroup fetchGroup) throws MailboxException {
			return new MetaData() {
				@Override
				public List<Long> getRecent() {
					return Collections.emptyList();
				}

				@Override
				public long countRecent() {
					LOG.info("get count recent");
					return 0;
				}

				@Override
				public Flags getPermanentFlags() {
					return new Flags();
				}

				@Override
				public long getUidValidity() {
					return 0;
				}

				@Override
				public long getUidNext() {
					return 0;
				}

				@Override
				public long getMessageCount() {
					return 0;
				}

				@Override
				public long getUnseenCount() {
					return 0;
				}

				@Override
				public Long getFirstUnseen() {
					return null;
				}

				@Override
				public boolean isWriteable() {
					return true;
				}
			};
		}
	};

	@Override
	public char getDelimiter() {
		return MailboxConstants.DEFAULT_DELIMITER;
	}

	@Override
	public MessageManager getMailbox(MailboxPath mailboxPath, MailboxSession mailboxSession) throws MailboxException {
		return messageManager;
	}

	@Override
	public void createMailbox(MailboxPath mailboxPath, MailboxSession mailboxSession) throws MailboxException {
	}

	@Override
	public void deleteMailbox(MailboxPath mailboxPath, MailboxSession mailboxSession) throws MailboxException {
	}

	@Override
	public void renameMailbox(MailboxPath mailboxPath, MailboxPath mailboxPath1, MailboxSession mailboxSession) throws MailboxException {
	}

	@Override
	public void copyMessages(MessageRange messageRange, MailboxPath mailboxPath, MailboxPath mailboxPath1, MailboxSession mailboxSession) throws MailboxException {
	}

	@Override
	public List<MailboxMetaData> search(MailboxQuery mailboxQuery, MailboxSession mailboxSession) throws MailboxException {
		return Collections.emptyList();
	}

	@Override
	public boolean mailboxExists(MailboxPath mailboxPath, MailboxSession mailboxSession) throws MailboxException {
		LOG.info("get mailboxExists " + mailboxPath);
		return true;
	}

	@Override
	public MailboxSession createSystemSession(String userName, Log log) throws BadCredentialsException, MailboxException {
		// todo: check userName
		MailboxSession.User user = new MailboxUser(userName, null);
		return new MemoryMailboxSession(RANDOM.nextLong(), user, log);
	}

	@Override
	public MailboxSession login(String userName, String password, Log log) throws BadCredentialsException, MailboxException {
		// todo: check password
		MailboxSession.User user = new MailboxUser(userName, password);
		return new MemoryMailboxSession(RANDOM.nextLong(), user, log);
	}

	@Override
	public void logout(MailboxSession mailboxSession, boolean force) throws MailboxException {
		mailboxSession.close();
	}

	@Override
	public void addListener(MailboxPath mailboxPath, MailboxListener mailboxListener, MailboxSession mailboxSession) throws MailboxException {
	}

	@Override
	public void startProcessingRequest(MailboxSession mailboxSession) {
	}

	@Override
	public void endProcessingRequest(MailboxSession mailboxSession) {
	}
}
