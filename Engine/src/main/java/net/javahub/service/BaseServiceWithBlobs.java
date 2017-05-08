package net.javahub.service;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;

import net.javahub.dao.IBaseDaoWithBlobs;

public class BaseServiceWithBlobs<T, K extends Serializable, E> extends BaseService<T, K, E> {
	
	@Autowired 
	protected IBaseDaoWithBlobs<T,K,E> baseDaoWithBlobs;
	
	protected T selectByPrimaryKeyWithBlobs(Class<T> objectClass, K id) throws ClassNotFoundException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{
		return baseDaoWithBlobs.selectByPrimaryKey(objectClass, id);
	}
	
	protected int updateByPrimaryKeyWithBLOBs(T object) throws ClassNotFoundException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{
		return baseDaoWithBlobs.updateByPrimaryKeyWithBLOBs(object);
	}
	
	protected List<T> selectListByExampleWithBlobs(Class<T> objectClass, E example) throws ClassNotFoundException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{
		List<T> list=null;
		list=baseDaoWithBlobs.selectByExampleWithBLOBs(objectClass, example);
		return list;
	}
	
	protected List<T> selectListByExampleWithRowBoundsWithBlobs(Class<T> objectClass, E example, int offset, int limit) throws ClassNotFoundException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{
		List<T> list=null;
		list=baseDaoWithBlobs.selectByExampleWithBLOBsWithRowbounds(objectClass, example, new RowBounds(offset, limit));
		return list;
	}

}
