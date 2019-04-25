package com.game.sdk.dolls.dao.impl;

import com.game.sdk.dolls.constants.CommonCode;
import com.game.sdk.dolls.dao.DaoSuper;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import java.io.Serializable;
import java.util.List;

/***
 * Hibernate Dao 层默认继承该类
 * @param <T>
 * @param <PK>
 */
@Repository
public class DaoSuperImpl<T, PK extends Serializable> implements DaoSuper<T, PK> {

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    @Override
    public EntityManager getEntityManager() {
        return entityManagerFactory.createEntityManager();
    }

    @Override
    public List<T> findByHql(String sql, List params) {
        List<T> list = createQuery(sql, params).getResultList();
        return list;
    }


    @Override
    public List<Object[]> findBySql(String sql, List params) {
        List<Object[]> list = createSqlQuery(sql, params).getResultList();
        return list;
    }

    @Override
    public T findUniqueByHql(String hql, List params) {
        List<T> list =  createQuery(hql, params).getResultList();
        if(CollectionUtils.isEmpty(list)){
            return null;
        }
        if(list.size()==1){
            T t = list.get(0);
            return t;
        }
        throw new IllegalArgumentException("query result is greater 1 , this size is "+list.size());
    }

    @Override
    public long count(String sql, List params) {
        List list = createSqlQuery(sql, params).getResultList();
        if(CollectionUtils.isEmpty(list)){
            return CommonCode.NUMBER0;
        }
        Object obj = list.get(0);
        if(obj == null){
            return CommonCode.NUMBER0;
        }
        return Long.parseLong(obj.toString());
    }

    @Override
    public List<T> findBySql(String sql, List params, Class clazz) {
        return createSqlQuery(sql, params, clazz).getResultList();
    }

    private Query createQuery(String sql, List params) {
        Query query = getEntityManager().createQuery(sql);

        if (CollectionUtils.isNotEmpty(params)){
            for (int i=0; i<params.size();i++){
                query.setParameter(i, params.get(i));
            }
        }
        return query;
    }

    private Query createSqlQuery(String sql, List params) {
        Query sqlQuery = getEntityManager().createNativeQuery(sql);
        if (CollectionUtils.isNotEmpty(params)){
            for (int i=0; i<params.size();i++){
                sqlQuery.setParameter((i+1), params.get(i));
            }
        }
        return sqlQuery;
    }

    private Query createSqlQuery(String sql, List params, Class clazz) {
        Query sqlQuery = getEntityManager().createNativeQuery(sql, clazz);
        if (CollectionUtils.isNotEmpty(params)){
            for (int i=0; i<params.size();i++){
                sqlQuery.setParameter((i+1), params.get(i));
            }
        }
        return sqlQuery;
    }
}