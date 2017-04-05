package net.javahub.dao;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class BaseSqlDao {

	@Autowired
	private SqlSession sqlSession;
	
	public List<Map<String, Object>> select(SqlWrapper sqlWrapper) {
		IBaseSqlMapper dao=sqlSession.getMapper(IBaseSqlMapper.class);
		return dao.select(sqlWrapper);
	}

	public static class SqlWrapper{
		private Map<String, Object> params;
		private String sqlStatement;
		public SqlWrapper(){
			params=new ConcurrentHashMap<String, Object>();
			sqlStatement="";
		}
		public SqlWrapper(String sqlStatement){
			this.sqlStatement=sqlStatement;
		}
		public Map<String, Object> getParams() {
			return params;
		}
		public void setParams(Map<String, Object> params) {
			this.params = params;
		}
		public String getSqlStatement() {
			return sqlStatement;
		}
		public void setSqlStatement(String sqlStatement) {
			this.sqlStatement = sqlStatement;
		}
		public Object get(String key){
			return params.get(key);
		}
		public SqlWrapper addParam(String key, Object value){
			params.put(key, value);
			return this;
		}
		public Object removeParam(String key){
			return params.remove(key);
		}
	}
}
