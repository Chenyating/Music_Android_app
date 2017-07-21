package jimihua.user.dao.impl;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import jimihua.user.dao.MusicListDao;
import jimihua.user.datasource.SQLManager;
import jimihua.user.po.MusicList;

/**
 * Created by Me on 2017/3/1.
 */
public class MusicListDaoImpl implements MusicListDao {

    private SQLManager sqlManager = null;

    public MusicListDaoImpl() {
        super();
        // TODO Auto-generated constructor stub
        this.sqlManager = new SQLManager();
    }

    @Override
    public boolean insert(MusicList musicList, SQLiteDatabase sqLiteDatabase) {

        int position = musicList.getPosition();
        String songname = musicList.getSongname();

        // 组织SQL语句
        String strSQL = "insert into music( position , songname ) values(?,?)";
        Object[] bindArgs = new Object[] { position,songname };

        // 调用sqlManager对象中的方法完成数据库操作
        return this.sqlManager.execWritableBySQL(sqLiteDatabase, strSQL,
                bindArgs);
    }

    @Override
    public boolean drop(MusicList musicList, SQLiteDatabase sqLiteDatabase) {

        // TODO Auto-generated method stub
        int positon = musicList.getPosition();
        // 组织SQL语句
        String strSQL = "delete from music where position=? ";
        Object[] bindArgs = new Object[] { positon };

        // 调用sqlManager对象中的方法完成数据库操作
        return this.sqlManager.execWritableBySQL(sqLiteDatabase, strSQL,
                bindArgs);
    }

    @Override
    public List<MusicList> select(SQLiteDatabase sqLiteDatabase) {
        // TODO Auto-generated method stub
        // 组织SQL语句
        String strSQL = "select * from music";
        String[] selectionArgs = new String[] {};

        // 调用sqlManager对象中的方法完成数据库操作
        Cursor cursor = this.sqlManager.execReadableBySQL(sqLiteDatabase,
                strSQL, selectionArgs);

        // ------ 数据库结构的转换------
        // 创建一个空集合
        List<MusicList> lstMessages = new ArrayList<MusicList>();
        // 使用循环遍历游标数据并封装成Message对象装载到List集合中
        while (cursor.moveToNext()) {
            // 创建一个空的Message对象
            MusicList musiclist = new MusicList();
            // 为message对象的属性赋值
            musiclist.setPosition(cursor.getInt(1));
            musiclist.setSongname(cursor.getString(2));
            // 将封装好的message对象添加到集合中
            lstMessages.add(musiclist);
        }

        // 返回集合
        return lstMessages;
    }
}
