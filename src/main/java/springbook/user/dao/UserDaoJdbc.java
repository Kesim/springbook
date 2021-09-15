package springbook.user.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import springbook.user.domain.Level;
import springbook.user.domain.User;

public class UserDaoJdbc implements UserDao {
	private JdbcTemplate jdbcTemplate;
	private String sqlAdd;
	private String sqlDeleteAll;
	private String sqlGet;
	private String sqlGetAll;
	private String sqlGetCount;
	private String sqlUpdate;
	
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
	
	public void setSqlAdd(String sqlAdd) {
		this.sqlAdd = sqlAdd;
	}
	
	public void setSqlDeleteAll(String sqlDeleteAll) {
		this.sqlDeleteAll = sqlDeleteAll;
	}
	
	public void setSqlGet(String sqlGet) {
		this.sqlGet = sqlGet;
	}
	
	public void setSqlGetAll(String sqlGetAll) {
		this.sqlGetAll = sqlGetAll;
	}
	
	public void setSqlGetCount(String sqlGetCount) {
		this.sqlGetCount = sqlGetCount;
	}
	
	public void setSqlUpdate(String sqlUpdate) {
		this.sqlUpdate = sqlUpdate;
	}
	
	@Override
	public void add(final User user) {
		this.jdbcTemplate.update(sqlAdd, user.getId(), user.getName(), user.getPassword(),
				user.getEmail(), user.getLevel().intValue(), user.getLogin(), user.getRecommend());
	}
	
	@Override
	public void deleteAll() {
		this.jdbcTemplate.update(sqlDeleteAll);
	}
	
	@Override
	public User get(String id) {
		return this.jdbcTemplate.queryForObject(sqlGet, this.userMapper, id);
	}
	
	@Override
	public List<User> getAll() {
		return this.jdbcTemplate.query(sqlGetAll, this.userMapper);
	}
	
	@Override
	public int getCount() {
		return this.jdbcTemplate.queryForInt(sqlGetCount);
	}
	
	@Override
	public void update(User user) {
		this.jdbcTemplate.update(sqlUpdate, user.getName(), user.getPassword(), user.getEmail(),
				user.getLevel().intValue(), user.getLogin(), user.getRecommend(), user.getId());
	}
}
