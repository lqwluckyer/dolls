package com.game.sdk.dolls.dao;

import javax.persistence.EntityManager;
import java.io.Serializable;
import java.util.List;

public interface DaoSuper<T, PK extends Serializable> {
    EntityManager getEntityManager();
    List<T> findByHql(String hql, List params);
    T findUniqueByHql(String sql, List params);
    List<Object[]> findBySql(String sql, List params);
    List<T> findBySql(String sql, List params, Class clazz);
    long count(String sql, List params);
}