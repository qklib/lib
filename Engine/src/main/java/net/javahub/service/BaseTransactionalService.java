package net.javahub.service;

import java.io.Serializable;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly=false, rollbackFor=Exception.class, propagation=Propagation.REQUIRED)
public abstract class BaseTransactionalService<T, K extends Serializable, E> extends BaseService<T, K, E>{

}
