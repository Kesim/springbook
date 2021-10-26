package springbook.user.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

import springbook.user.dao.UserDao;
import springbook.user.domain.Level;
import springbook.user.domain.User;

@Service("userService")
public class UserServiceImpl implements UserService {
	@Autowired
	private UserDao userDao;
	private UserLevelUpgradePolicy userLevelUpgradePolicy;
	@Autowired
	private MailSender mailSender;
	
	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}
	
	public void setUserLevelUpgradePolicy(UserLevelUpgradePolicy userLevelUpgradePolicy) {
		this.userLevelUpgradePolicy = userLevelUpgradePolicy;
	}
	
	public void setMailSender(MailSender mailSender) {
		this.mailSender = mailSender;
	}
	
	@Override
	public void add(User user) {
		if (user.getLevel() == null) {
			user.setLevel(Level.BASIC);
		}
		userDao.add(user);
	}
	
	@Override
	public User get(String id) {
		return userDao.get(id);
	}
	
	@Override
	public List<User> getAll() {
		return userDao.getAll();
	}
	
	@Override
	public void update(User user) {
		userDao.update(user);
	}
	
	@Override
	public void deleteAll() {
		userDao.deleteAll();
	}
	
	@Override
	public void upgradeLevels() {
		List<User> users = userDao.getAll();
		for (User user : users) {
			if (userLevelUpgradePolicy.canUpgradeLevel(user)) {
				upgradeLevel(user);
			}
		}
	}
	
	private void upgradeLevel(User user) {
		userLevelUpgradePolicy.upgradeLevel(user);
		sendUpgradEMail(user);
	}
	
	private void sendUpgradEMail(User user) {
		SimpleMailMessage mailMessage = new SimpleMailMessage();
		mailMessage.setTo(user.getEmail());
		mailMessage.setFrom("useradmin@ksug.org");
		mailMessage.setSubject("Upgrade 안내");
		mailMessage.setText("사용자님의 등급이 " + user.getLevel().name() + "로 업그레이드되었습니다");
		
		mailSender.send(mailMessage);
	}
}
