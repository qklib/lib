package net.javahub.service;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.javahub.dao.IBaseDao;
import net.javahub.dao.IBaseDaoWithBlobs;

@Service
public class BaseService<T, K extends Serializable, E> {

	@Autowired
	protected IBaseDao<T,K,E> baseDao;
	
	@Autowired 
	protected IBaseDaoWithBlobs<T,K,E> baseDaoWithBlogs;
	
	public BaseService(){
	}
	
	protected List<T> selectListByExample(Class<T> objectClass, E example) throws ClassNotFoundException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{
		List<T> list=null;
		list=baseDao.selectByExample(objectClass, example);
		return list;
	}
	
	protected List<T> selectListByExampleWithRowBounds(Class<T> objectClass, E example, int offset, int limit) throws ClassNotFoundException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{
		List<T> list=null;
		list=baseDao.selectByExampleWithRowbounds(objectClass, example, offset, limit);
		return list;
	}
	
	protected List<T> selectListByExampleWithBlobs(Class<T> objectClass, E example) throws ClassNotFoundException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{
		List<T> list=null;
		list=baseDaoWithBlogs.selectByExampleWithBLOBs(objectClass, example);
		return list;
	}
	
	protected List<T> selectListByExampleWithRowBoundsWithBlobs(Class<T> objectClass, E example, int offset, int limit) throws ClassNotFoundException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{
		List<T> list=null;
		list=baseDaoWithBlogs.selectByExampleWithBLOBsWithRowbounds(objectClass, example, new RowBounds(offset, limit));
		return list;
	}
}
