package com.kangjj.lib.db;

public interface IBseDao<T> {
    long insert(T entity);
}
