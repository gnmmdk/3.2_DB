package com.kangjj.lib.db;

import java.util.List;

public interface IBseDao<T> {
    long insert(T entity);

    long update(T entity,T where);

    int delete(T where);

    List<T> query(T where);

    List<T> query(T where,String orderBy,Integer startIndex,Integer limit);
}
