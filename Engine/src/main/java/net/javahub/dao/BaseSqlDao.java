package net.javahub.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.ResultMapping;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.scripting.LanguageDriver;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

@Repository
public class BaseSqlDao {

	@Autowired
	private SqlSession sqlSession;
	
	private static final String DEFAULT_MS_ID="default_ms_id";
	
	public String addMappedStatementToConfiguration(final String msId, final String defaultMsId, final Class<?> resultType, final String sql, final Object param) {
		final Configuration configuration=sqlSession.getConfiguration();
		String actualMsId=msId;
		if(StringUtils.isEmpty(actualMsId)){
			actualMsId=defaultMsId;
		}
		if(!configuration.hasStatement(actualMsId, false)){
			LanguageDriver languageDriver=configuration.getDefaultScriptingLanguageInstance();
			SqlSource sqlSource=languageDriver.createSqlSource(configuration, sql, param==null?null:param.getClass());
			List<ResultMapping> resultMappings=new ArrayList<ResultMapping>();
			List<ResultMap> resultMaps=new ArrayList<ResultMap>();
			ResultMap.Builder resultMapBuilder=new ResultMap.Builder(
					configuration, 
					actualMsId, 
					resultType, 
					resultMappings);
			resultMaps.add(resultMapBuilder.build());
			MappedStatement ms = new MappedStatement.Builder(configuration, actualMsId, sqlSource, SqlCommandType.SELECT)
		    		.resultMaps(resultMaps)
		    		.build();
		    configuration.addMappedStatement(ms);	
		}
		return actualMsId;
	}
	
	public List<Map<String, Object>> select(SqlWrapper sqlWrapper) {
		IBaseSqlMapper dao=sqlSession.getMapper(IBaseSqlMapper.class);
		return dao.select(sqlWrapper);
	}

	public <E> E selectOne(SqlWrapper sqlWrapper, Class<E> resultType){
		String msId=String.valueOf(sqlWrapper.getSqlStatement().hashCode());
		String actualMsId=addMappedStatementToConfiguration(msId, DEFAULT_MS_ID, resultType, sqlWrapper.getSqlStatement(), sqlWrapper.getParams());
		return sqlSession.selectOne(actualMsId, sqlWrapper.getParams());
	}
	
	public <E> List<E> selectList(SqlWrapper sqlWrapper, Class<E> resultType){
		String msId=String.valueOf(sqlWrapper.getSqlStatement().hashCode());
		String actualMsId=addMappedStatementToConfiguration(msId, DEFAULT_MS_ID, resultType, sqlWrapper.getSqlStatement(), sqlWrapper.getParams());
		return sqlSession.selectList(actualMsId, sqlWrapper.getParams());
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
		
		public void clearParam(){
			params.clear();
		}
	}

}
