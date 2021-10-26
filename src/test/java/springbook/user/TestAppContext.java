package springbook.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.mail.MailSender;

import springbook.user.dao.UserDao;
import springbook.user.service.DummyMailSender;
import springbook.user.service.UserLevelUpgradePolicy;
import springbook.user.service.UserService;
import springbook.user.service.UserServiceTest.TestUserLevelUpgradePolicy;
import springbook.user.service.UserServiceTest.TestUserService;

@Configuration
@Import(AppContext.class)
@Profile("test")
@PropertySource("/database.properties")
public class TestAppContext {
	@Autowired
	UserDao userDao;
	
	@Bean(name = "testUserService")
	public UserService testUserService() {
		TestUserService testService = new TestUserService();
		testService.setUserLevelUpgradePolicy(testUserLevelUpgradePolicy());
		return testService;
	}
	
	@Bean
	public MailSender mailSender() {
		return new DummyMailSender();
	}
	
	@Bean
	public UserLevelUpgradePolicy testUserLevelUpgradePolicy() {
		TestUserLevelUpgradePolicy testPolicy = new TestUserLevelUpgradePolicy();
		return testPolicy;
	}
}
