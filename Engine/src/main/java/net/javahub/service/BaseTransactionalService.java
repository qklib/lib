package net.javahub.service;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly=false, rollbackFor=Exception.class, propagation=Propagation.REQUIRED)
public class BaseTransactionalService<T> extends BaseService<T>{

}
