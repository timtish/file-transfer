package ru.timtish.bridge.services.mailstorage;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.james.user.api.User;
import org.apache.james.user.api.UsersRepository;

/**
 * @author Timofey Tishin (ttishin@luxoft.com)
 */
public class MemoryMailUsersRepository implements UsersRepository {

	private Map<String, User> users = new HashMap<String, User>();

	public MemoryMailUsersRepository() {
		addUser("user1", "user1");
	}

	@Override
	public boolean addUser(User user) {
		users.put(user.getUserName(), user);
		return false;
	}

	@Override
	public void addUser(String name, Object attributes) {
		addUser(name, null);
	}

	@Override
	public boolean addUser(String userName, String password) {
		return addUser(new ExternalUser(userName, password));
	}

	@Override
	public User getUserByName(String s) {
		return users.get(s);
	}

	@Override
	public User getUserByNameCaseInsensitive(String s) {
		if (s == null) return null;
		for (User user : users.values()) {
			if (s.equalsIgnoreCase(user.getUserName())) return user;
		}
		return null;
	}

	@Override
	public String getRealName(String s) {
		return s;
	}

	@Override
	public boolean updateUser(User user) {
		removeUser(user.getUserName());
		addUser(user);
		return true;
	}

	@Override
	public void removeUser(String s) {
		users.remove(s);
	}

	@Override
	public boolean contains(String s) {
		return users.containsKey(s);
	}

	@Override
	public boolean containsCaseInsensitive(String s) {
		return getUserByNameCaseInsensitive(s) != null;
	}

	@Override
	public boolean test(String s, String s1) {
		User user = getUserByName(s);
		return user != null && user.verifyPassword(s1);
	}

	@Override
	public int countUsers() {
		return users.size();
	}

	@Override
	public Iterator<String> list() {
		return users.keySet().iterator();
	}
}
