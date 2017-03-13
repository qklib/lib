package net.javahub.dao;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import net.javahub.example.BaseExample;

@Repository
public class BaseDaoImpl<T, K extends Serializable> implements IBaseDao<T, K> {

	private static SqlSessionFactory factory;
	
	@Autowired
	private SqlSession injectedSqlSession;
	
	public static void init(String pathToConfigFile) throws IOException{
		File configFile=new File(pathToConfigFile);
		InputStream inputStream=null;
		if(configFile.exists()){
			inputStream=new FileInputStream(configFile);
		} else {
			inputStream=Resources.getResourceAsStream(pathToConfigFile);
		}
		factory=new SqlSessionFactoryBuilder().build(inputStream);
	}
	
	public long countByExample(Class<T> clazz, BaseExample example) throws ClassNotFoundException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{
		return (Long) invokeMapperMethod(clazz, "countByExample", false, new Class<?>[]{example.getClass()}, example);
	}
	
	public int insert(T object) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException, ClassNotFoundException {
		return (Integer) invokeMapperMethod(object.getClass(), "insert", true, new Class<?>[]{object.getClass()}, object);
	}

	public int deleteByPrimaryKey(Class<T> clazz, K key) throws ClassNotFoundException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		return (Integer) invokeMapperMethod(clazz, "deleteByPrimaryKey", true, new Class<?>[]{key.getClass()}, key);
	}

	public int updateByPrimaryKey(T object) throws ClassNotFoundException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		return (Integer) invokeMapperMethod(object.getClass(), "updateByPrimaryKey", true, new Class<?>[]{object.getClass()}, object);
	}

	@SuppressWarnings("unchecked")
	public T selectByPrimaryKey(Class<T> clazz, K key) throws ClassNotFoundException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		return (T) invokeMapperMethod(clazz, "selectByPrimaryKey", false, new Class<?>[]{key.getClass()}, key);
	}

	@SuppressWarnings("unchecked")
	public List<T> selectByExample(Class<T> clazz, BaseExample example) throws ClassNotFoundException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		return (List<T>) invokeMapperMethod(clazz, "selectByExample", false, new Class<?>[]{example.getClass()}, example);
	}

	@SuppressWarnings("unchecked")
	public List<T> selectByExampleWithRowbounds(Class<T> clazz, BaseExample example, int offset, int limit) throws ClassNotFoundException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		RowBounds rowBounds=new RowBounds(offset, limit);
		return (List<T>) invokeMapperMethod(clazz, "selectByExampleWithRowbounds", false, new Class<?>[]{example.getClass(),rowBounds.getClass()},example,rowBounds);
	}

	public void batchInsert(Class<T> clazz, Collection<T> objectCollection) throws ClassNotFoundException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		SqlSession session=null;
		try{
			session=getSqlSession(true);
			Object mapperObject=getMapperObject(clazz, session);
			Method method=getMapperMethod(clazz, mapperObject, "insert", new Class<?>[]{clazz});
			for(T object: objectCollection){
				method.invoke(mapperObject, object);
			}
			if(!session.equals(injectedSqlSession)){
				session.commit();
			}
		}finally{
			if(session!=null && (!session.equals(injectedSqlSession))){
				session.close();
			}
		}
	}

	public void batchDeleteByPrimaryKey(Class<T> objectClass, Class<K> keyClass, Collection<K> keyCollection) throws ClassNotFoundException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		SqlSession session=null;
		try{
			session=getSqlSession(true);
			Object mapperObject=getMapperObject(objectClass, session);
			Method method=getMapperMethod(objectClass, mapperObject, "deleteByPrimaryKey", new Class<?>[]{keyClass});
			for(K key: keyCollection){
				method.invoke(mapperObject, key);
			}
			if(!session.equals(injectedSqlSession)){
				session.commit();				
			}
		}finally{
			if(session!=null &&(!session.equals(injectedSqlSession))){
				session.close();
			}
		}
	}
	
	public void batchUpdateByPrimaryKey(Class<T> clazz, Collection<T> objectCollection) throws ClassNotFoundException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		SqlSession session=null;
		try{
			session=getSqlSession(true);
			Object mapperObject=getMapperObject(clazz, session);
			Method method=getMapperMethod(clazz, mapperObject, "updateByPrimaryKey", new Class<?>[]{clazz});
			for(T object: objectCollection){
				method.invoke(mapperObject, object);
			}
			if(!session.equals(injectedSqlSession)){
				session.commit();				
			}
		}finally{
			if(session!=null &&(!session.equals(injectedSqlSession))){
				session.close();
			}
		}
	}
	
	private Object invokeMapperMethod(Class<?> clazz, String methodName, boolean isCommitRequired, Class<?>[] parameterTypes, Object ...args) throws ClassNotFoundException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{
		Object object=null;
		SqlSession session=null;
		try{
			session=getSqlSession(false);
			Object mapperObject=getMapperObject(clazz, session);
			Method method=getMapperMethod(clazz, mapperObject, methodName, parameterTypes);
			object=method.invoke(mapperObject, args);
			if((!session.equals(injectedSqlSession) && isCommitRequired)){
				session.commit();
			}
		}finally{
			if(session!=null && (!session.equals(injectedSqlSession))){
				session.close();
			}
		}
		return object;
	}
	
	private Object getMapperObject(Class<?> clazz, SqlSession session) throws ClassNotFoundException{
		return session.getMapper(Class.forName(getMapperClassName(clazz)));
	}
	
	private Method getMapperMethod(Class<?> clazz, Object mapperObject, String methodName, Class<?>[] parameterTypes) throws ClassNotFoundException, NoSuchMethodException, SecurityException{
		Class<?> mapperClass=mapperObject.getClass();
		Method method=mapperClass.getMethod(methodName, parameterTypes);
		return method;
	}
	
	private String getMapperClassName(Class<?> objectClass){
		String objectClassName=objectClass.getName();
		int indexOfLastSeparator=objectClassName.lastIndexOf(".");
		if(indexOfLastSeparator<0){
			return new StringBuilder("mapper.").append(objectClassName).append("Mapper").toString();
		}
		String objectClassPackage=objectClassName.substring(0, indexOfLastSeparator);
		String objectClassSimpleName=objectClassName.substring(indexOfLastSeparator+1);
		return new StringBuilder(objectClassPackage).append(".mapper.").append(objectClassSimpleName).append("Mapper").toString();
		
	}
	
	private SqlSession getSqlSession(boolean isBatched){
		if(injectedSqlSession!=null){
			return injectedSqlSession;
		} else {
			if(isBatched){
				return factory.openSession(ExecutorType.BATCH);
			} else {
				return factory.openSession();
			}
		}
	}
	
	public SqlSession getInjectedSqlSession() {
		return injectedSqlSession;
	}

	public void setInjectedSqlSession(SqlSession injectedSqlSession) {
		this.injectedSqlSession = injectedSqlSession;
	}

}
