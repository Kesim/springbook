package springbook;

import java.sql.SQLException;

import springbook.user.dao.UserDao;
import springbook.user.domain.User;

public class main {

	public static void main(String[] args) throws ClassNotFoundException, SQLException {
		UserDao dao = new UserDao();
		
		User user = new User();
		user.setid("whiteship");
		user.setName("��⼱");
		user.setPassword("married");
		
		dao.add(user);
		
		System.out.println(user.getId() + " ��� ����");
		
		User user2 = dao.get(user.getId());
		System.out.println(user.getName());
		System.out.println(user.getPassword());
		
		System.out.println(user2.getId() + " ��ȸ ����");
	}

}
