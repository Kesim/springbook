package springbook.user.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import springbook.user.domain.User;

public class UserDao {
	private JdbcTemplate jdbcTemplate;
	
	private RowMapper<User> userMapper =
		new RowMapper<User>() {
			@Override
			public User mapRow(ResultSet rs, int rowNum) throws SQLException {
				User user = new User();
				user.setid(rs.getString("id"));
				user.setName(rs.getString("name"));
				user.setPassword(rs.getString("password"));
				return user;
			}
		};

	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	public void add(final User user) throws SQLException{
		this.jdbcTemplate.update("insert into users(id, name, password) values(?, ?, ?)",
			user.getId(), user.getName(), user.getPassword());
	}

	public User get(String id) throws SQLException{
		return this.jdbcTemplate.queryForObject("select * from users where id = ?",
			this.userMapper, id);
	}
	
	public void deleteAll() throws SQLException {
		this.jdbcTemplate.update("delete from users");
	}
	
	public int getCount() throws SQLException{
		return this.jdbcTemplate.queryForInt("select count(*) from users");
	}
	
	public List<User> getAll() throws SQLException {
		return this.jdbcTemplate.query("select * from users order by id", this.userMapper);
	}
}
