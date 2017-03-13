package net.javahub.dao;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.List;

import net.javahub.example.BaseExample;

public interface IBaseDao<T, K extends Serializable> {
	
	public long countByExample(Class<T> clazz, BaseExample example) throws ClassNotFoundException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException;
	
	public int insert(T object) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException, ClassNotFoundException;
	
	public int deleteByPrimaryKey(Class<T> clazz, K key) throws ClassNotFoundException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException;
	
	public int updateByPrimaryKey(T object) throws ClassNotFoundException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException;
	
	public T selectByPrimaryKey(Class<T> clazz, K key) throws ClassNotFoundException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException;
	
	public List<T> selectByExample(Class<T> clazz, BaseExample example) throws ClassNotFoundException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException;

	List<T> selectByExampleWithRowbounds(Class<T> clazz, BaseExample example, int offset, int limit) throws ClassNotFoundException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException;
	
	public void batchInsert(Class<T> clazz, Collection<T> objectCollection) throws ClassNotFoundException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException;
	
	public void batchDeleteByPrimaryKey(Class<T> objectClass, Class<K> keyClass, Collection<K> keyCollection) throws ClassNotFoundException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException;

	public void batchUpdateByPrimaryKey(Class<T> clazz, Collection<T> objectCollection) throws ClassNotFoundException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException;
}
