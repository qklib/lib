package net.javahub.service;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.javahub.dao.IBaseDao;

@Service
public class BaseService<T, K extends Serializable, E> {

	@Autowired
	private IBaseDao<T,K,E> baseDao;
	
	public BaseService(){
	}
	
	public List<T> selectListByExample(Class<T> objectClass, E example) throws ClassNotFoundException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{
		List<T> list=null;
		list=baseDao.selectByExample(objectClass, example);
		return list;
	}
	
	public List<T> selectListByExampleWithRowBounds(Class<T> objectClass, E example, int offset, int limit) throws ClassNotFoundException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{
		List<T> list=null;
		list=baseDao.selectByExampleWithRowbounds(objectClass, example, offset, limit);
		return list;
	}
}
