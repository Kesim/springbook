package springbook.user.service;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static springbook.user.service.MainUserLevelUpgradePolicy.MIN_LOGCOUNT_FOR_SILVER;
import static springbook.user.service.MainUserLevelUpgradePolicy.MIN_RECOMMEND_FOR_GOLD;

import java.util.Arrays;
import java.util.List;

import javax.sql.DataSource;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.PlatformTransactionManager;

import springbook.user.dao.UserDao;
import springbook.user.domain.Level;
import springbook.user.domain.User;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/test-applicationContext.xml")
public class UserServiceTest {
	@Autowired
	UserService userService;
	@Autowired
	UserDao userDao;
	@Autowired
	PlatformTransactionManager transactionManager;
	
	List<User> users;
	
	static class TestUserLevelUpgradePolicy extends MainUserLevelUpgradePolicy {
		private String id;
		
		public TestUserLevelUpgradePolicy(String id) {
			this.id = id;
		}

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
		userDao.deleteAll();
		for(User user : users) {
			userDao.add(user);
		}
		
		userService.upgradeLevels();
		
		checkLevelUpgraded(users.get(0), false);
		checkLevelUpgraded(users.get(1), true);
		checkLevelUpgraded(users.get(2), false);
		checkLevelUpgraded(users.get(3), true);
		checkLevelUpgraded(users.get(4), false);
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
	public void upgradeAllOrNothing() {
		TestUserLevelUpgradePolicy testPolicy =
			new TestUserLevelUpgradePolicy(users.get(3).getId());
		testPolicy.setUserDao(userDao);
		UserService testUserService = new UserService();
		testUserService.setUserDao(userDao);
		testUserService.setUserLevelUpgradePolicy(testPolicy);
		testUserService.setTransactionManager(transactionManager);
		
		userDao.deleteAll();
		for(User user : users) {
			userDao.add(user);
		}
		
		try {
			testUserService.upgradeLevels();
			fail("TestUserLevelException expected");
		} catch(TestUserLevelException e) {
		}
		
		checkLevelUpgraded(users.get(1), false);
	}
}
