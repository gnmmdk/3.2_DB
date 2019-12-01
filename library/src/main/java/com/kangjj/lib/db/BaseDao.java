package com.kangjj.lib.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import com.kangjj.lib.annotation.DbField;
import com.kangjj.lib.annotation.DbTable;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class BaseDao<T> implements IBseDao<T> {

    private SQLiteDatabase sqLiteDatabase;

    private String tableName;
    //操作数据库所对应的java类型
    private Class<T> entityClass;
    //标识，用来标识是否已经做过初始化
    private boolean isInit = false;
    //定义一个缓存控件（key 字段名 value成员变量）
    private HashMap<String, Field> cacheMap;

    public boolean init(SQLiteDatabase sqLiteDatabase,Class<T> entityClass){
        this.sqLiteDatabase = sqLiteDatabase;
        this.entityClass = entityClass;
        if(!isInit){
            DbTable dt = entityClass.getAnnotation(DbTable.class);
            if(dt!=null && !TextUtils.isEmpty(dt.value())){
                tableName = dt.value();
            }else{
                tableName = entityClass.getName();
            }
            if(!sqLiteDatabase.isOpen()){
                return false;
            }
            String creteTableSql = getCreateTableSql();
            sqLiteDatabase.execSQL(creteTableSql);
            cacheMap = new HashMap<>();
            initCacheMap();
            isInit=true;
        }
        return isInit;
    }

    private String getCreateTableSql() {
        StringBuilder sb = new StringBuilder();
        sb.append("create table if not exists ");
        sb.append(tableName+"(");
        Field[] fields = entityClass.getDeclaredFields();
        for (Field field : fields) {
            Class<?> type = field.getType();
            DbField dbField = field.getAnnotation(DbField.class);
            if(dbField != null && !TextUtils.isEmpty(dbField.value())){
                if(type == String.class){
                    sb.append(dbField.value()+" TEXT,");
                }else if(type == Integer.class){
                    sb.append(dbField.value()+" INTEGER,");
                }else if(type== Long.class){
                    sb.append(dbField.value()+" BIGINT,");
                }else if(type== Double.class){
                    sb.append(dbField.value()+" DOUBLE,");
                }else if(type==byte[].class){
                    sb.append(dbField.value()+" BLOB,");
                }else{
                    //不支持的类型号
                    continue;
                }
            }else{
                if(type == String.class){
                    sb.append(field.getName()+" TEXT,");
                }else if(type == Integer.class){
                    sb.append(field.getName()+" INTEGER,");
                }else if(type== Long.class){
                    sb.append(field.getName()+" BIGINT,");
                }else if(type== Double.class){
                    sb.append(field.getName()+" DOUBLE,");
                }else if(type==byte[].class){
                    sb.append(field.getName()+" BLOB,");
                }else{
                    //不支持的类型号
                    continue;
                }
            }
        }
        if(sb.charAt(sb.length()-1) == ','){
            sb.deleteCharAt(sb.length()-1);
        }
        sb.append(")");
        return sb.toString();
    }

    private void initCacheMap() {
        //取得所有的列名
        String sql = "select * from "+tableName+" limit 1,0";
        Cursor cursor = sqLiteDatabase.rawQuery(sql,null);
        String[] columnNames = cursor.getColumnNames();
        //获取所有成员变量 和columnName进行匹配
        Field[] columnFields = entityClass.getDeclaredFields();
        for (Field columnField : columnFields) {
            columnField.setAccessible(true);
        }
        for (String columnName : columnNames) {
            Field columnField = null;
            for (Field field : columnFields) {
                String fileName = null;
                if(field.getAnnotation(DbField.class)!=null){
                    fileName = field.getAnnotation(DbField.class).value();
                }else{
                    fileName = field.getName();
                }
                if(columnName.equals(fileName)){
                    columnField = field;
                    break;
                }
            }
            if(columnField!=null){
                cacheMap.put(columnName,columnField);
            }
        }
    }

    @Override
    public long insert(T entity) {
        //user对象转为contentvalues
        Map<String,String> map = getValues(entity);
        ContentValues values = getContentValues(map);
        long ret = sqLiteDatabase.insert(tableName,null,values);
        return ret;
    }

    @Override
    public long update(T entity, T where) {
        Map map = getValues(entity);
        ContentValues values = getContentValues(map);
        Map whereMap = getValues(where);
        Condition condition = new Condition(whereMap);
        return sqLiteDatabase.update(tableName,values,condition.whereCause,condition.whereArgs);
    }

    @Override
    public int delete(T where) {
        Map<String, String> whereMap = getValues(where);
        Condition condition = new Condition(whereMap);
        return sqLiteDatabase.delete(tableName,condition.whereCause,condition.whereArgs);
    }

    @Override
    public List<T> query(T where) {
        return query(where,null,null,null);
    }

    @Override
    public List<T> query(T where, String orderBy, Integer startIndex, Integer limit) {

        Map map = getValues(where);
        String limitString = null;
        if(startIndex !=null && limit != null){
            limitString = startIndex+" , "+limit;
        }
        Condition condition = new Condition(map);
        Cursor cursor = sqLiteDatabase.query(tableName,null,condition.whereCause,condition.whereArgs,null,null,orderBy,limitString);
        //解析游标
        List<T> result = getResult(cursor,where);
        return result;
    }

    private List<T> getResult(Cursor cursor, T obj) {
        ArrayList list = new ArrayList();
        Object item = null;
        while (cursor.moveToNext()) {//查询的结果是多条的 ，所以要进行遍历
            try {
                item = obj.getClass().newInstance();
                Iterator iterator = cacheMap.entrySet().iterator();
                String[] columnNames = cursor.getColumnNames();
//                for (String columnName : columnNames) { //for test updatedb
//                    Log.e("kangjj","columnName="+columnName+"\n");
//                }

                while (iterator.hasNext()) {
                    Map.Entry entry = (Map.Entry) iterator.next();
                    //获取列名
                    String columnName = (String) entry.getKey();
                    //以列名拿到列名在游标中的位置
                    int columnIndex = cursor.getColumnIndex(columnName);
//                    Log.e("kangjj","columnName:"+columnName+" columnIndex:"+columnIndex);
                    Field field = (Field) entry.getValue();
                    Class type = field.getType();
                    if(columnIndex != -1){
                        if(type == String.class){
                            field.set(item,cursor.getString(columnIndex));
                        }else if(type == Double.class){
                            field.set(item,cursor.getDouble(columnIndex));
                        }else if(type == Integer.class){
                            field.set(item,cursor.getInt(columnIndex));
                        }else if(type == Long.class){
                            field.set(item,cursor.getLong(columnIndex));
                        }else if(type == byte[].class){
                            field.set(item,cursor.getBlob(columnIndex));
                        }else{
                            continue;
                        }
                    }
                }
                list.add(item);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        cursor.close();
        return list;
    }

    private Map<String, String> getValues(T entity) {
        HashMap<String,String> map = new HashMap<>();
        if(entity==null){
            return map;
        }
        Iterator<Field> fieldIterator = cacheMap.values().iterator();
        while (fieldIterator.hasNext()) {
            Field field = fieldIterator.next();
            field.setAccessible(true);
            try {

                Object obj = field.get(entity);
                if(obj==null){
                    continue;
                }
                String value = obj.toString();
                //获取列名
                String key = null;
                DbField dbField = field.getAnnotation(DbField.class);
                if(dbField!=null && !"".equals(dbField.value())){
                    key = dbField.value();
                }else{
                    key = field.getName();
                }
                if(!TextUtils.isEmpty(key) && !TextUtils.isEmpty(value)){
                    map.put(key,value);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return map;
    }

    private ContentValues getContentValues(Map<String, String> map) {
        ContentValues contentValues = new ContentValues();
        Set keys = map.keySet();
        Iterator<String> iterator = keys.iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            String value = map.get(key);
            if(value!=null){//内部是HashMap<String, Object> mValues; HasMap的value不能为空
                contentValues.put(key,value);
            }
        }
        return contentValues;
    }

    private class Condition{
        private String whereCause;
        private String[] whereArgs;

        public Condition(Map<String,String> whereMap){
            ArrayList list = new ArrayList();
            StringBuilder sb = new StringBuilder();
            sb.append("1=1");
            //获取所有的字段名
            Set keys = whereMap.keySet();
            Iterator<String> iterator = keys.iterator();
            while (iterator.hasNext()) {
                String key = iterator.next();
                String value = whereMap.get(key);
                if(value != null){
                    sb.append(" and "+key+" =?");
                    list.add(value);
                }
            }
            this.whereCause = sb.toString();
            this.whereArgs = (String[]) list.toArray(new String[list.size()]);
        }
    }
}
