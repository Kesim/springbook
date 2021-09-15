package springbook.user.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import springbook.user.domain.Level;
import springbook.user.domain.User;
import springbook.user.sqlservice.SqlService;

public class UserDaoJdbc implements UserDao {
	private JdbcTemplate jdbcTemplate;
	private SqlService sqlService;
	
	private RowMapper<User> userMapper = new RowMapper<User>() {
		@Override
		public User mapRow(ResultSet rs, int rowNum) throws SQLException {
			User user = new User();
			user.setId(rs.getString("id"));
			user.setName(rs.getString("name"));
			user.setPassword(rs.getString("password"));
			user.setEmail(rs.getString("email"));
			user.setLevel(Level.valueOf(rs.getInt("level")));
			user.setLogin(rs.getInt("login"));
			user.setRecommend(rs.getInt("recommend"));
			return user;
		}
	};
	
	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	public void setSqlService(SqlService sqlService) {
		this.sqlService = sqlService;
	}
	
	@Override
	public void add(final User user) {
		this.jdbcTemplate.update(sqlService.getSql("userAdd"), user.getId(), user.getName(),
				user.getPassword(), user.getEmail(), user.getLevel().intValue(), user.getLogin(),
				user.getRecommend());
	}
	
	@Override
	public void deleteAll() {
		this.jdbcTemplate.update(sqlService.getSql("userDeleteAll"));
	}
	
	@Override
	public User get(String id) {
		return this.jdbcTemplate.queryForObject(sqlService.getSql("userGet"), this.userMapper, id);
	}
	
	@Override
	public List<User> getAll() {
		return this.jdbcTemplate.query(sqlService.getSql("userGetAll"), this.userMapper);
	}
	
	@Override
	public int getCount() {
		return this.jdbcTemplate.queryForInt(sqlService.getSql("userGetCount"));
	}
	
	@Override
	public void update(User user) {
		this.jdbcTemplate.update(sqlService.getSql("userUpdate"), user.getName(), user.getPassword(),
				user.getEmail(), user.getLevel().intValue(), user.getLogin(), user.getRecommend(),
				user.getId());
	}
}
