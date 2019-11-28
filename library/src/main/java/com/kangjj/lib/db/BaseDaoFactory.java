package com.kangjj.lib.db;

import android.database.sqlite.SQLiteDatabase;

public class BaseDaoFactory {
    private static final BaseDaoFactory instance = new BaseDaoFactory();
    public static BaseDaoFactory getInstance(){
        return instance;
    }
    private SQLiteDatabase sqLiteDatabase;
    private String sqlitePath;
    private BaseDaoFactory(){
//        sqlitePath = "data/data/com.kangjj.db/kangjj.db";//注意 applicationID
        sqlitePath = "sdcard/kangjj.db";//注意 applicationID
        sqLiteDatabase = SQLiteDatabase.openOrCreateDatabase(sqlitePath,null);
    }

    public <T> BaseDao<T> getBaseDao(Class<T> entityClass){
        try {
            BaseDao baseDao = BaseDao.class.newInstance();
            baseDao.init(sqLiteDatabase,entityClass);
            return baseDao;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void close(){
        if(sqLiteDatabase!=null && sqLiteDatabase.isOpen()){
            sqLiteDatabase.close();
        }
        sqLiteDatabase = null;
    }
}
