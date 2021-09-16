package springbook.user.sqlservice;

import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.springframework.oxm.Unmarshaller;

import springbook.user.dao.UserDao;
import springbook.user.sqlservice.jaxb.SqlType;
import springbook.user.sqlservice.jaxb.Sqlmap;

public class OxmSqlService implements SqlService {
	private final BaseSqlService baseSqlService = new BaseSqlService();
	private final OxmSqlReader oxmSqlReader = new OxmSqlReader();
	
	private SqlRegistry sqlRegistry = new HashMapSqlRegistry();
	
	public void setSqlRegistry(SqlRegistry sqlRegistry) {
		this.sqlRegistry = sqlRegistry;
	}
	
	public void setUnmarshaller(Unmarshaller unmarshaller) {
		oxmSqlReader.setUnmarshaller(unmarshaller);
	}
	
	public void setSqlmapFile(String sqlmapFile) {
		oxmSqlReader.setSqlmapFile(sqlmapFile);
	}
	
	@PostConstruct
	public void loadSql() {
		baseSqlService.setSqlReader(oxmSqlReader);
		baseSqlService.setSqlRegistry(sqlRegistry);
		
		baseSqlService.loadSql();
	}
	
	@Override
	public String getSql(String key) throws SqlRetrievalFailureException {
		return baseSqlService.getSql(key);
	}
	
	private class OxmSqlReader implements SqlReader {
		private final static String DEFAULT_SQLMAP_FILE = "sqlmap.xml";
		
		private Unmarshaller unmarshaller;
		private String sqlmapFile = DEFAULT_SQLMAP_FILE;
		
		public void setUnmarshaller(Unmarshaller unmarshaller) {
			this.unmarshaller = unmarshaller;
		}
		
		public void setSqlmapFile(String sqlmapFile) {
			this.sqlmapFile = sqlmapFile;
		}
		
		@Override
		public void read(SqlRegistry sqlRegistry) {
			try {
				Source source = new StreamSource(UserDao.class.getResourceAsStream(sqlmapFile));
				Sqlmap sqlmap = (Sqlmap) unmarshaller.unmarshal(source);
				
				for (SqlType sql : sqlmap.getSql()) {
					sqlRegistry.registerSql(sql.getKey(), sql.getValue());
				}
			} catch (IOException e) {
				throw new IllegalArgumentException(sqlmapFile + "을 가져올 수 없습니다", e);
			}
		}
	}
}
