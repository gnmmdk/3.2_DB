package com.kangjj.lib.db;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class BaseDaoFactory {
    public static final String SQLITE_DIR = "sdcard/";
//    public static final String SQLITE_DIR = "data/data/com.kangjj.db/";
    private static final BaseDaoFactory instance = new BaseDaoFactory();
    public static BaseDaoFactory getInstance(){
        return instance;
    }
    private SQLiteDatabase sqLiteDatabase;
    private String sqlitePath;
    //设计要给数据连接池，new容器，只要new个一次，下一次就不会创建了。考虑多线程的问题
    protected Map<String,BaseDao> map = Collections.synchronizedMap(new HashMap<String, BaseDao>());
    protected BaseDaoFactory(){
        Log.e("kangjj","BaseDaoFactory");//TODO  test
//        sqlitePath = "data/data/com.kangjj.db/kangjj.db";//注意 applicationID
        sqlitePath = SQLITE_DIR+"kangjj.db";//注意 applicationID
        sqLiteDatabase = SQLiteDatabase.openOrCreateDatabase(sqlitePath,null);
    }

    public <T extends BaseDao<M>,M> T getBaseDao(Class<T> daoClass,Class<M> entityClass){
        try {
            if(map.get(daoClass.getSimpleName())!=null){
                return (T) map.get(daoClass.getSimpleName());
            }
            BaseDao baseDao = daoClass.newInstance();
            baseDao.init(sqLiteDatabase,entityClass);
            map.put(daoClass.getSimpleName(),baseDao);
            return (T)baseDao;
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
