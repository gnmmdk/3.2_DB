package com.kangjj.lib.subdb;

import android.util.Log;

import com.kangjj.lib.bean.User;
import com.kangjj.lib.db.dao.UserDao;
import com.kangjj.lib.db.BaseDaoFactory;

import java.io.File;

/**
 * 用来产生私有数据存放的位置
 */
public enum PrivateDatabaseEnums {
    database("");

    PrivateDatabaseEnums(String value){

    }

    public String getValue(){
        UserDao userDao = BaseDaoFactory.getInstance().getBaseDao(UserDao.class, User.class);
        if(userDao!=null){
            User curUser = userDao.getCurrentUser();
            if(curUser != null){
                File file = new File(BaseDaoFactory.SQLITE_DIR);
                if(!file.exists()){
                    file.mkdirs();
                }
                File fileDb = new File(BaseDaoFactory.SQLITE_DIR+"/u_"+curUser.getId()+"_private.db");
                if(!fileDb.exists()){
                    Log.e("kangjj",file.getAbsolutePath()+"数据库不存在");
                    return null;
                }
                return fileDb.getAbsolutePath();
            }
        }
        return null;
    }

    public String getValue(Integer userId){
        if(userId==null){
            return null;
        }
        File file = new File(BaseDaoFactory.SQLITE_DIR);
        if(!file.exists()){
            file.mkdirs();
        }
        File fileDb = new File(BaseDaoFactory.SQLITE_DIR+"/u_"+userId+"_private.db");
        if(!fileDb.exists()){
            Log.e("kangjj",file.getAbsolutePath()+"数据库不存在");
            return null;
        }
        return fileDb.getAbsolutePath();

    }
}

