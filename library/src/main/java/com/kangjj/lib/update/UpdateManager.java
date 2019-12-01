package com.kangjj.lib.update;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import com.kangjj.lib.bean.User;
import com.kangjj.lib.db.BaseDaoFactory;
import com.kangjj.lib.db.dao.UserDao;
import com.kangjj.lib.subdb.PrivateDatabaseEnums;

import org.w3c.dom.Document;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class UpdateManager {
    private List<User> userList;

    public void startUpdateDb(Context context){
        UserDao userDao = BaseDaoFactory.getInstance().getBaseDao(UserDao.class,User.class);
        userList = userDao.query(new User());
        //解析XML文件
        UpdateXml updateXml = readDbXml(context);
        //拿到当前版本信息
        UpdateStep updateStep = analyseUpdateStep(updateXml);
        if(updateStep==null){
            return;
        }
        //获取更新用的对象
        List<UpdateDb> updateDbs = updateStep.getUpdateDbs();
        for (User user : userList) {
            //得到每个用户的数据库对象  因为做了分库，假如没做分库，可以不需要做分库处理
            String dbPath = PrivateDatabaseEnums.database.getValue(user.getId());
            if(dbPath==null){
                continue;
            }
            SQLiteDatabase database = SQLiteDatabase.openOrCreateDatabase(dbPath,null);
            for (UpdateDb updateDb : updateDbs) {
                String sql_rename = updateDb.getSql_rename();
                String sql_create = updateDb.getSql_create();
                String sql_insert = updateDb.getSql_insert();
                String sql_delete = updateDb.getSql_delete();
                String[] sqls = new String[]{sql_rename,sql_create,sql_insert,sql_delete};
                executeSql(database,sqls);
                Log.i("kangjj",user.getId()+"用户数据库升级成功");
            }
        }
    }

    private void executeSql(SQLiteDatabase database, String[] sqls) {
        if(sqls == null || sqls.length == 0){
            return;
        }
        database.beginTransaction();
        for (String sql : sqls) {
            sql = sql.replace("\r\n"," ");
            sql = sql.replace("\n"," ");
            if(!"".equalsIgnoreCase(sql.trim())){
                database.execSQL(sql);
            }
        }
        database.setTransactionSuccessful();
        database.endTransaction();
    }


    private UpdateStep analyseUpdateStep(UpdateXml updateXml) {
        if(updateXml == null){
            return null;
        }
        UpdateStep thisStep = null;
        List<UpdateStep> steps = updateXml.getUpdateSteps();
        if(steps == null || steps.size()==0){
            return null;
        }
        for (UpdateStep step : steps) {
            if(TextUtils.isEmpty(step.getVersionFrom()) || TextUtils.isEmpty(step.getVersionTo())){

            }else{
                String[] versionArray = step.getVersionFrom().split(",");
                if(versionArray!=null && versionArray.length>0){
                    for (int i = 0; i < versionArray.length; i++) {
                        //用sqlite的服务器版本会不会比较好？ TODO Test
                        if("V001".equalsIgnoreCase(versionArray[i]) &&
                        step.getVersionTo().equalsIgnoreCase("V003")){
                            thisStep = step;
                        }
                    }
                }
            }
        }
        return thisStep;
    }

    private UpdateXml readDbXml(Context context) {
        InputStream is =  null;
        Document document = null;
        try {
            //真实项目是需要从服务器下载到sdcard
            is=context.getAssets().open("updateXml.xml");
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            document = builder.parse(is);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if(is != null){
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(document == null){
                return null;
            }
        }
        UpdateXml updateXml = new UpdateXml(document);
        return updateXml;
    }

}
