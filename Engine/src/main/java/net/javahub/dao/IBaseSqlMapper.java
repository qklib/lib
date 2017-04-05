package net.javahub.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import net.javahub.dao.BaseSqlDao.SqlWrapper;

@Repository
public interface IBaseSqlMapper {

	public List<Map<String, Object>> select(SqlWrapper sql);
}
