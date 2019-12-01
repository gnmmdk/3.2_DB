package com.kangjj.lib.db.dao;

import android.util.Log;

import com.kangjj.lib.bean.User;
import com.kangjj.lib.db.BaseDao;

import java.util.List;

public class UserDao extends BaseDao<User> {
    @Override
    public long insert(User entity) {
        List<User> list = query(new User());
        User where = null;
        for (User user : list) {
            where = new User();
            where.setId(user.getId());
            user.setStatus(0);
            update(user,where);
            Log.e("neteasedb","用户"+user.getName()+"更改为未登录状态");
        }
        entity.setStatus(1);
        Log.e("neteasedb","用户"+entity.getName()+"登录");
        return super.insert(entity);
    }

    public User getCurrentUser(){
        User user = new User();
        user.setStatus(1);
        List<User> list = query(user);
        if(list!=null  && list.size()>0){
            return list.get(0);
        }
        return null;
    }
}
