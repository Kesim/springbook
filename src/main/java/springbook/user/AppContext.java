package springbook.user;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.mysql.cj.jdbc.Driver;

import springbook.user.service.UserLevelUpgradePolicy;
import springbook.user.service.UserService;
import springbook.user.service.UserServiceImpl;

@Configuration
@EnableTransactionManagement
@ComponentScan(basePackages = "springbook.user")
@Import({SqlServiceContext.class, ProductionAppContext.class})
public class AppContext {
	@Bean
	public DataSource dataSource() {
		SimpleDriverDataSource dataSource = new SimpleDriverDataSource();
		
		dataSource.setDriverClass(Driver.class);
		dataSource.setUrl("jdbc:mysql://localhost/testdb");
		dataSource.setUsername("spring");
		dataSource.setPassword("book");
		
		return dataSource;
	}
	
	@Bean
	public PlatformTransactionManager transactionManager() {
		DataSourceTransactionManager tm = new DataSourceTransactionManager();
		tm.setDataSource(dataSource());
		return tm;
	}
	
	/*
	 * 局撇府纳捞记 肺流 
	 */
	
	@Autowired
	UserLevelUpgradePolicy userLevelUpgradePolicy;
	
	@Bean
	public UserService userService() {
		UserServiceImpl userService = new UserServiceImpl();
		userService.setUserLevelUpgradePolicy(this.userLevelUpgradePolicy);
		return userService;
	}
}
