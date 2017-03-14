package net.javahub.dao;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class BaseDaoWithBlobsImpl<T, K extends Serializable, E> implements IBaseDaoWithBlobs<T, K, E> {

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
	
	public int insert(T object) throws ClassNotFoundException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		return (Integer) invokeMapperMethod(object.getClass(), "insert", true, new Class<?>[]{object.getClass()}, object);
	}

	@SuppressWarnings("unchecked")
	public List<T> selectByExampleWithBLOBsWithRowbounds(Class<?> objectClass, E example, RowBounds rowBounds) throws ClassNotFoundException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		return (List<T>) invokeMapperMethod(objectClass, "selectByExampleWithBLOBsWithRowbounds", false, new Class<?>[]{example.getClass(),rowBounds.getClass()},example,rowBounds);
	}

	@SuppressWarnings("unchecked")
	public List<T> selectByExampleWithBLOBs(Class<?> objectClass, E example) throws ClassNotFoundException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		return (List<T>) invokeMapperMethod(objectClass, "selectByExampleWithBLOBs", false, new Class<?>[]{example.getClass()}, example);
	}

	@SuppressWarnings("unchecked")
	public T selectByPrimaryKey(Class<?> objectClass, K id) throws ClassNotFoundException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		return (T) invokeMapperMethod(objectClass, "selectByPrimaryKey", false, new Class<?>[]{id.getClass()}, id);
	}

	public int updateByPrimaryKeyWithBLOBs(T object) throws ClassNotFoundException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		return (Integer) invokeMapperMethod(object.getClass(), "updateByPrimaryKeyWithBLOBs", true, new Class<?>[]{object.getClass()}, object);
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
		Class<?> baseClass=objectClass;
		if(objectClass.getName().toUpperCase().endsWith("WITHBLOBS")){
			baseClass=objectClass.getSuperclass();
		}
		String baseClassName=baseClass.getName();
		int indexOfLastSeparator=baseClassName.lastIndexOf(".");
		if(indexOfLastSeparator<0){
			return new StringBuilder("mapper.").append(baseClassName).append("Mapper").toString();
		}
		String baseClassPackage=baseClassName.substring(0, indexOfLastSeparator);
		String baseClassSimpleName=baseClassName.substring(indexOfLastSeparator+1);
		return new StringBuilder(baseClassPackage).append(".mapper.").append(baseClassSimpleName).append("Mapper").toString();
		
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
