package springbook.user.service;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static springbook.user.service.MainUserLevelUpgradePolicy.MIN_LOGCOUNT_FOR_SILVER;
import static springbook.user.service.MainUserLevelUpgradePolicy.MIN_RECOMMEND_FOR_GOLD;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.TransientDataAccessResourceException;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.PlatformTransactionManager;

import springbook.user.TestAppContext;
import springbook.user.dao.UserDao;
import springbook.user.domain.Level;
import springbook.user.domain.User;

@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("test")
@ContextConfiguration(classes = TestAppContext.class)
public class UserServiceTest {
	@Autowired
	ApplicationContext context;
	@Autowired
	UserService userService;
	@Autowired
	UserService testUserService;
	@Autowired
	UserDao userDao;
	@Autowired
	PlatformTransactionManager transactionManager;
	@Autowired
	MailSender mailSender;
	@Autowired
	DefaultListableBeanFactory bf;
	
	List<User> users;
	
	@Before
	public void setUp() {
		users = Arrays.asList(
			new User("bumjin", "박범진", "p1", "bumjin@naver.com",
				Level.BASIC, MIN_LOGCOUNT_FOR_SILVER - 1, 0),
			new User("joytouch", "강명성", "p2", "joytouch@naver.com",
				Level.BASIC, MIN_LOGCOUNT_FOR_SILVER, 0),
			new User("erwins", "신승한", "p3", "erwins@gmail.com",
				Level.SILVER, 60, MIN_RECOMMEND_FOR_GOLD - 1),
			new User("madnite1", "이상호", "p4", "madnite1@gmail.com",
				Level.SILVER, 60, MIN_RECOMMEND_FOR_GOLD),
			new User("green", "오민규", "p5", "green@gmail.com",
				Level.GOLD, 100, Integer.MAX_VALUE)
		);
	}
	
	@Test
	public void bean() {
		assertThat(this.userService, is(notNullValue()));
	}
	
	@Test
	public void upgradeLevels() {
		UserServiceImpl userServiceImpl = new UserServiceImpl();
		
		MockUserDao mockUserDao = new MockUserDao(users);
		userServiceImpl.setUserDao(mockUserDao);
		
		MainUserLevelUpgradePolicy userLevelUpgradePolicy = new MainUserLevelUpgradePolicy();
		userLevelUpgradePolicy.setUserDao(mockUserDao);
		userServiceImpl.setUserLevelUpgradePolicy(userLevelUpgradePolicy);
		
		MockMailSender mockMailSender = new MockMailSender();
		userServiceImpl.setMailSender(mockMailSender);
		
		userServiceImpl.upgradeLevels();
		
		List<User> updated = mockUserDao.getUpdated();
		assertThat(updated.size(), is(2));
		checkUserAndLevel(updated.get(0), "joytouch", Level.SILVER);
		checkUserAndLevel(updated.get(1), "madnite1", Level.GOLD);
		
		List<String> requests = mockMailSender.getRequests();
		assertThat(requests.size(), is(2));
		assertThat(requests.get(0), is(users.get(1).getEmail()));
		assertThat(requests.get(1), is(users.get(3).getEmail()));
	}
	
	private void checkUserAndLevel(User updated, String expectedId, Level expectedLevel) {
		assertThat(updated.getId(), is(expectedId));
		assertThat(updated.getLevel(), is(expectedLevel));
	}
	
	@Test
	public void mockUpgradeLevels() {
		UserServiceImpl userServiceImpl = new UserServiceImpl();
		
		UserDao mockUserDao = mock(UserDao.class);
		when(mockUserDao.getAll()).thenReturn(users);
		userServiceImpl.setUserDao(mockUserDao);
		
		MainUserLevelUpgradePolicy userLevelUpgradePolicy = new MainUserLevelUpgradePolicy();
		userLevelUpgradePolicy.setUserDao(mockUserDao);
		userServiceImpl.setUserLevelUpgradePolicy(userLevelUpgradePolicy);
		
		MailSender mockMailSender = mock(MailSender.class);
		userServiceImpl.setMailSender(mockMailSender);
		
		userServiceImpl.upgradeLevels();
		
		verify(mockUserDao, times(2)).update(any(User.class));
		verify(mockUserDao).update(users.get(1));
		assertThat(users.get(1).getLevel(), is(Level.SILVER));
		verify(mockUserDao).update(users.get(3));
		assertThat(users.get(3).getLevel(), is(Level.GOLD));
		
		ArgumentCaptor<SimpleMailMessage> mailMessageArg = ArgumentCaptor
				.forClass(SimpleMailMessage.class);
		verify(mockMailSender, times(2)).send(mailMessageArg.capture());
		List<SimpleMailMessage> mailMessages = mailMessageArg.getAllValues();
		assertThat(mailMessages.get(0).getTo()[0], is(users.get(1).getEmail()));
		assertThat(mailMessages.get(1).getTo()[0], is(users.get(3).getEmail()));
	}
	
	@Test
	public void add() {
		userDao.deleteAll();
		
		User userWithLevel = users.get(4);
		User userWithoutLevel = users.get(0);
		userWithoutLevel.setLevel(null);
		
		userService.add(userWithLevel);
		userService.add(userWithoutLevel);
		
		User userWithLevelRead = userDao.get(userWithLevel.getId());
		User userWithoutLevelRead = userDao.get(userWithoutLevel.getId());
		
		assertThat(userWithLevelRead.getLevel(), is(userWithLevel.getLevel()));
		assertThat(userWithoutLevelRead.getLevel(), is(Level.BASIC));
	}
	
	@Test
	public void upgradeAllOrNothing() throws Exception {
		userDao.deleteAll();
		for (User user : users) {
			userDao.add(user);
		}
		
		try {
			testUserService.upgradeLevels();
			fail("TestUserLevelException expected");
		} catch (TestUserLevelException e) {
		}
		
		checkLevelUpgraded(users.get(1), false);
	}
	
	private void checkLevelUpgraded(User user, boolean upgraded) {
		User userUpdate = userDao.get(user.getId());
		if(upgraded) {
			assertThat(userUpdate.getLevel(), is(user.getLevel().nextLevel()));
		} else {
			assertThat(userUpdate.getLevel(), is(user.getLevel()));
		}
	}
	
	@Test
	public void advisorAutoProxyCreator() {
		assertThat(testUserService, is(java.lang.reflect.Proxy.class));
	}
	
	@Test(expected = TransientDataAccessResourceException.class)
	public void readOnlyTransactionAttribute() {
		testUserService.getAll();
	}
	
	@Test
	public void beans() {
		for(String n : bf.getBeanDefinitionNames()) {
			System.out.println(n + "\t" + bf.getBean(n).getClass().getName());
		}
	}
	
	public static class TestUserService extends UserServiceImpl {
		@Override
		public List<User> getAll() {
			for(User user : super.getAll()) {
				super.update(user);
			}
			return null;
		}
	}
	
	public static class TestUserLevelUpgradePolicy extends MainUserLevelUpgradePolicy {
		private String id = "madnite1";
		
		@Override
		public void upgradeLevel(User user) {
			if(user.getId().equals(id)) {
				throw new TestUserLevelException();
			}
			super.upgradeLevel(user);
		}
	}
	
	static class TestUserLevelException extends RuntimeException{
	}
	
	static class MockMailSender implements MailSender {
		private List<String> requests = new ArrayList<>();
		
		public List<String> getRequests() {
			return requests;
		}
		
		@Override
		public void send(SimpleMailMessage simpleMessage) throws MailException {
			this.requests.add(simpleMessage.getTo()[0]);
		}
		
		@Override
		public void send(SimpleMailMessage[] simpleMessages) throws MailException {
		}
	}
	
	static class MockUserDao implements UserDao {
		private List<User> users;
		private List<User> updated = new ArrayList<>();
		
		public MockUserDao(List<User> users) {
			this.users = users;
		}
		
		public List<User> getUpdated() {
			return updated;
		}
		
		@Override
		public List<User> getAll() {
			return users;
		}
		
		@Override
		public void update(User user) {
			updated.add(user);
		}

		@Override
		public void add(User user) {
			throw new UnsupportedOperationException();
		}

		@Override
		public User get(String id) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void deleteAll() {
			throw new UnsupportedOperationException();
		}

		@Override
		public int getCount() {
			throw new UnsupportedOperationException();
		}
	}
}
