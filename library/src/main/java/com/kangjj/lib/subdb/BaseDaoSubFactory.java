package com.kangjj.lib.subdb;

import android.database.sqlite.SQLiteDatabase;

import com.kangjj.lib.db.BaseDao;
import com.kangjj.lib.db.BaseDaoFactory;

public class BaseDaoSubFactory extends BaseDaoFactory {

    private static final BaseDaoSubFactory instance = new BaseDaoSubFactory();
    public static BaseDaoSubFactory getInstance(){
        return instance;
    }

    //定义一个用于实现分库的数据库对象
    protected SQLiteDatabase subSqliteDatabase;

    //生产basedao对象
    public <T extends BaseDao<M>,M> T getBaseDao(Class<T> daoClass, Class<M> entityClass){
        if(map.get(PrivateDatabaseEnums.database.getValue())!=null){
            return (T) map.get(PrivateDatabaseEnums.database.getValue());
        }
        subSqliteDatabase = SQLiteDatabase.openOrCreateDatabase(PrivateDatabaseEnums.database.getValue(),null);
        try {
            BaseDao baseDao = daoClass.newInstance();
            baseDao.init(subSqliteDatabase,entityClass);//创建表
            map.put(PrivateDatabaseEnums.database.getValue(),baseDao);
            return (T)baseDao;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        return null;
    }
}
