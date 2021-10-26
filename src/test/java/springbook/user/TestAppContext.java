package springbook.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.MailSender;

import springbook.user.dao.UserDao;
import springbook.user.service.DummyMailSender;
import springbook.user.service.UserLevelUpgradePolicy;
import springbook.user.service.UserService;
import springbook.user.service.UserServiceTest.TestUserLevelUpgradePolicy;
import springbook.user.service.UserServiceTest.TestUserService;

@Configuration
public class TestAppContext {
	@Autowired
	UserDao userDao;
	
	@Bean
	public UserService testUserService() {
		TestUserService testService = new TestUserService();
		testService.setUserDao(this.userDao);
		testService.setUserLevelUpgradePolicy(testUserLevelUpgradePolicy());
		testService.setMailSender(mailSender());
		return testService;
	}
	
	@Bean
	public MailSender mailSender() {
		return new DummyMailSender();
	}
	
	@Bean
	public UserLevelUpgradePolicy testUserLevelUpgradePolicy() {
		TestUserLevelUpgradePolicy testPolicy = new TestUserLevelUpgradePolicy();
		testPolicy.setUserDao(this.userDao);
		return testPolicy;
	}
}
