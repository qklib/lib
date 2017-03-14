package net.javahub.dao;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.apache.ibatis.session.RowBounds;

public interface IBaseDaoWithBlobs<T, K extends Serializable, E> {
	
	public int insert(T object) throws ClassNotFoundException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException;
	
	public List<T> selectByExampleWithBLOBsWithRowbounds(Class<?> objectClass, E example, RowBounds rowBounds) throws ClassNotFoundException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException;
		
	public List<T> selectByExampleWithBLOBs(Class<?> objectClass, E example) throws ClassNotFoundException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException;
	
	public T selectByPrimaryKey(Class<?> objectClass, K id) throws ClassNotFoundException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException;
	
	public int updateByPrimaryKeyWithBLOBs(T object) throws ClassNotFoundException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException;
}
