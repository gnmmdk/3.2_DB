package com.kangjj.db;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.kangjj.db.bean.User;
import com.kangjj.lib.db.BaseDao;
import com.kangjj.lib.db.BaseDaoFactory;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.insert).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BaseDao baseDao = BaseDaoFactory.getInstance().getBaseDao(User.class);
                User user = new User(1,"kangjj","111111");
                baseDao.insert(user);
                baseDao.insert(new User(2,"kangjj2","111"));
                baseDao.insert(new User(3,"kangjj3","111"));
                baseDao.insert(new User(4,"kangjj4","111"));
                baseDao.insert(new User(5,"kangjj5","111"));
                baseDao.insert(new User(6,"kangjj6","111"));
                Toast.makeText(MainActivity.this,"执行成功!",Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.select).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BaseDao<User> baseDao = BaseDaoFactory.getInstance().getBaseDao(User.class);
//                User where = new User();
//                where.setName("kangjj2");
//                where.setPassword("111");
                List<User> list = baseDao.query(new User());
                Log.e("kangjj database: "," list size is "+list.size());
                for(int i=0;i<list.size();i++){
                    System.out.println(list.get(i).toString());
                }
                Toast.makeText(MainActivity.this,"执行成功!",Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.update).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BaseDao<User> baseDao = BaseDaoFactory.getInstance().getBaseDao(User.class);
                User user = new User();
                user.setId(2);
                user.setName("netease111111");
                user.setPassword("111");
                User where = new User();
                where.setId(2);
                baseDao.update(user,where);
                Toast.makeText(MainActivity.this,"执行成功!",Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BaseDao<User> baseDao = BaseDaoFactory.getInstance().getBaseDao(User.class);
                User where = new User();
                where.setName("netease111111");
                baseDao.delete(where);
            }
        });

        findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BaseDaoFactory.getInstance().close();
            }
        });


    }
}
