package springbook.user.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import springbook.user.domain.Level;
import springbook.user.domain.User;

public class UserDaoJdbc implements UserDao {
	private JdbcTemplate jdbcTemplate;
	private Map<String, String> sqlMap;
	
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
	
	public void setSqlMap(Map<String, String> sqlMap) {
		this.sqlMap = sqlMap;
	}
	
	@Override
	public void add(final User user) {
		this.jdbcTemplate.update(sqlMap.get("add"), user.getId(), user.getName(),
				user.getPassword(), user.getEmail(), user.getLevel().intValue(), user.getLogin(),
				user.getRecommend());
	}
	
	@Override
	public void deleteAll() {
		this.jdbcTemplate.update(sqlMap.get("deleteAll"));
	}
	
	@Override
	public User get(String id) {
		return this.jdbcTemplate.queryForObject(sqlMap.get("get"), this.userMapper, id);
	}
	
	@Override
	public List<User> getAll() {
		return this.jdbcTemplate.query(sqlMap.get("getAll"), this.userMapper);
	}
	
	@Override
	public int getCount() {
		return this.jdbcTemplate.queryForInt(sqlMap.get("getCount"));
	}
	
	@Override
	public void update(User user) {
		this.jdbcTemplate.update(sqlMap.get("update"), user.getName(), user.getPassword(),
				user.getEmail(), user.getLevel().intValue(), user.getLogin(), user.getRecommend(),
				user.getId());
	}
}
