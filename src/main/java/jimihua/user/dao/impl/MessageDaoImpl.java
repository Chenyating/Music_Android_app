package jimihua.user.dao.impl;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import jimihua.user.dao.MessageDao;
import jimihua.user.datasource.SQLManager;
import jimihua.user.po.Message;

/**
 * Created by Me on 2017/2/22.
 */
public class MessageDaoImpl implements MessageDao {

    private SQLManager sqlManager = null;

    public MessageDaoImpl() {
        super();
        // TODO Auto-generated constructor stub
        this.sqlManager = new SQLManager();
    }

    @Override
    public boolean insert(Message message, SQLiteDatabase sqLiteDatabase) {

        String username = message.getUsername();
        String password = message.getPassword();
        String problem = message.getProblem();
        String answer = message.getAnswer();

        // 组织SQL语句
        String strSQL = "insert into user(username,password,problem,answer) values(?,?,?,?)";
        Object[] bindArgs = new Object[] { username, password, problem, answer };

        // 调用sqlManager对象中的方法完成数据库操作
        return this.sqlManager.execWritableBySQL(sqLiteDatabase, strSQL,
                bindArgs);


    }

    @Override
    public boolean update(Message message, SQLiteDatabase sqLiteDatabase) {

        // TODO Auto-generated method stub
        String password = message.getPassword();
        // 组织SQL语句
        String strSQL = "update user set password=? where username=? ";
        Object[] bindArgs = new Object[] { password, "ACCOUNT" };

        // 调用sqlManager对象中的方法完成数据库操作
        return this.sqlManager.execWritableBySQL(sqLiteDatabase, strSQL,
                bindArgs);
    }

    @Override
    public List<Message> select(SQLiteDatabase sqLiteDatabase) {

        // TODO Auto-generated method stub
        // 组织SQL语句
        String strSQL = "select * from user";
        String[] selectionArgs = new String[] {};

        // 调用sqlManager对象中的方法完成数据库操作
        Cursor cursor = this.sqlManager.execReadableBySQL(sqLiteDatabase,
                strSQL, selectionArgs);

        // ------ 数据库结构的转换------
        // 创建一个空集合
        List<Message> lstMessages = new ArrayList<Message>();
        // 使用循环遍历游标数据并封装成Message对象装载到List集合中
        while (cursor.moveToNext()) {
            // 创建一个空的Message对象
            Message message = new Message();
            // 为message对象的属性赋值
            message.setUsername(cursor.getString(0));
            message.setPassword(cursor.getString(1));
            message.setProblem(cursor.getString(2));
            message.setAnswer(cursor.getString(3));
            // 将封装好的message对象添加到集合中
            lstMessages.add(message);
        }

        // 返回集合
        return lstMessages;
    }
}
