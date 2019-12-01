package com.kangjj.db;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.kangjj.lib.bean.Photo;
import com.kangjj.lib.bean.User;
import com.kangjj.lib.db.dao.UserDao;
import com.kangjj.lib.db.BaseDao;
import com.kangjj.lib.db.BaseDaoFactory;
import com.kangjj.lib.subdb.BaseDaoSubFactory;
import com.kangjj.lib.subdb.dao.PhotoDao;
import com.kangjj.lib.update.UpdateManager;

import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    int i = 0;
    UserDao userDao;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        userDao = BaseDaoFactory.getInstance().getBaseDao(UserDao.class,User.class);
        findViewById(R.id.insert).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userDao.insert(new User(1,"netease1","111"));
                userDao.insert(new User(2,"netease2","111"));
                userDao.insert(new User(3,"netease3","111"));
                userDao.insert(new User(4,"netease4","111"));
                userDao.insert(new User(5,"netease5","111"));
                userDao.insert(new User(6,"netease6","111"));
                Toast.makeText(MainActivity.this,"执行成功!",Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.select).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BaseDao baseDao = BaseDaoFactory.getInstance().getBaseDao(UserDao.class,User.class);
                User where = new User();
//                where.setName("netease");
                where.setPassword("111");
                List<User> list = baseDao.query(where);
                Log.e("neteas database: "," list size is "+list.size());
                for(int i=0;i<list.size();i++){
                    System.out.println(list.get(i).toString());
                }
                Toast.makeText(MainActivity.this,"执行成功!",Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.update).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                UserDao userDao = BaseDaoFactory.getInstance().getBaseDao(UserDao.class,User.class);
//                User user = new User();
//                user.setId(2);
//                user.setName("netease111111");
//                user.setPassword("111");
//                User where = new User();
//                where.setId(2);
//                userDao.update(user,where);
//                Toast.makeText(MainActivity.this,"执行成功!",Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                UserDao userDao = BaseDaoFactory.getInstance().getBaseDao(UserDao.class,User.class);
//                User where = new User();
//                where.setName("netease111111");
//                userDao.delete(where);
            }
        });
//
//        findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                BaseDaoFactory.getInstance().close();
//            }
//        });
//
        findViewById(R.id.login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 服务器返回的信息
              User user = new User();
              user.setName("netease"+(i++));
                user.setPassword("154657567");
                user.setId(i);
                long ret = userDao.insert(user);
                Toast.makeText(MainActivity.this,"执行成功:"+ret,Toast.LENGTH_LONG).show();
            }
        });

        findViewById(R.id.subInsert).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Photo photo = new Photo();
                photo.setPath("/sdcard/xxx.jpg");
                photo.setTime(new Date().toString());
                PhotoDao photoDao = BaseDaoSubFactory.getInstance().getBaseDao(PhotoDao.class, Photo.class);
                photoDao.insert(photo);
            }
        });
        findViewById(R.id.subSelect).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Photo photo = new Photo();
                photo.setPath("/sdcard/xxx.jpg");
                PhotoDao photoDao = BaseDaoSubFactory.getInstance().getBaseDao(PhotoDao.class, Photo.class);

                List<Photo> list = photoDao.query(photo);
                Log.e("neteas database: "," list size is "+list.size());
                for(int i=0;i<list.size();i++){
                    System.out.println(list.get(i).toString());
                }
            }
        });
        findViewById(R.id.newVersion).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateManager updateManager = new UpdateManager();
                updateManager.startUpdateDb(getApplicationContext());
            }
        });
    }
}
