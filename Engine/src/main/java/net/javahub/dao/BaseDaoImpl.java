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
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import net.javahub.example.BaseExample;

public class BaseDaoImpl<T, K extends Serializable> implements IBaseDao<T, K> {

	private static SqlSessionFactory factory;
	
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
	
	public int insert(T object) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException, ClassNotFoundException {
		return (Integer) invokeMapperMethod(object.getClass(), "insert", true, new Class<?>[]{object.getClass()}, object);
	}

	public int deleteByPrimaryKey(Class<T> clazz, K key) throws ClassNotFoundException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		return (Integer) invokeMapperMethod(clazz, "deleteByPrimaryKey", true, new Class<?>[]{key.getClass()}, key);
	}

	public int updateByPrimaryKey(T object) throws ClassNotFoundException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		return (Integer) invokeMapperMethod(object.getClass(), "updateByPrimaryKey", true, new Class<?>[]{object.getClass()}, object);
	}

	public T selectByPrimaryKey(Class<T> clazz, K key) throws ClassNotFoundException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		return (T) invokeMapperMethod(clazz, "selectByPrimaryKey", false, new Class<?>[]{key.getClass()}, key);
	}

	public List<T> selectByExample(Class<T> clazz, BaseExample example) throws ClassNotFoundException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		return (List<T>) invokeMapperMethod(clazz, "selectByExample", false, new Class<?>[]{example.getClass()}, example);
	}

	private Object invokeMapperMethod(Class<?> clazz, String methodName, boolean isCommitRequired, Class<?>[] parameterTypes, Object ...args) throws ClassNotFoundException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{
		SqlSession session=getSqlSession();
		Object mapperObject=session.getMapper(Class.forName(getMapperClassName(clazz)));
		Class<?> mapperClass=mapperObject.getClass();
		Method method=mapperClass.getMethod(methodName, parameterTypes);
		Object object=method.invoke(mapperObject, args);
		if(isCommitRequired){
			session.commit();
		}
		return object;
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
	
	private SqlSession getSqlSession(){
		return factory.openSession();
	}
	
}
