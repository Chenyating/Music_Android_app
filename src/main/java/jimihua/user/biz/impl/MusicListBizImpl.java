package jimihua.user.biz.impl;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.util.List;

import jimihua.user.biz.MusicListBiz;
import jimihua.user.dao.MusicListDao;
import jimihua.user.dao.impl.MusicListDaoImpl;
import jimihua.user.datasource.ConnectionManager;
import jimihua.user.datasource.TransactionManager;
import jimihua.user.po.MusicList;

/**
 * Created by Me on 2017/3/1.
 */
public class MusicListBizImpl implements MusicListBiz {

    private MusicListDao musiclistDao;

    public MusicListBizImpl() {
        super();
        // TODO Auto-generated constructor stub
        this.musiclistDao = new MusicListDaoImpl();
    }
    @Override
    public boolean add(Context mContext, MusicList musiclist) {
        // TODO Auto-generated method stub
        // 步骤1：获取数据库连接对象
        ConnectionManager connectionManager = new ConnectionManager();
        SQLiteDatabase sqLiteDatabase = connectionManager.openConnection(
                mContext, "write");

        // 步骤2：开启事务处理
        TransactionManager transactionManager = new TransactionManager();
        transactionManager.beginTransaction(sqLiteDatabase);

        // 步骤3：调用DAO实现添加操作 // 步骤3：调用DAO实现添加操作
        this.musiclistDao.insert(musiclist, sqLiteDatabase);


        // 步骤4：提交事务
        transactionManager.commitTransaction(sqLiteDatabase);

        // 步骤5：关闭事务
        transactionManager.closeTransaction(sqLiteDatabase);

        // 步骤6：关闭数据库连接
        connectionManager.closeConnection(sqLiteDatabase);

        return true;
    }

    @Override
    public boolean remove(Context mContext, MusicList musiclist) {
        // TODO Auto-generated method stub
        // 步骤1：获取数据库连接对象
        ConnectionManager connectionManager = new ConnectionManager();
        SQLiteDatabase sqLiteDatabase = connectionManager.openConnection(
                mContext, "write");

        // 步骤2：开启事务处理
        TransactionManager transactionManager = new TransactionManager();
        transactionManager.beginTransaction(sqLiteDatabase);

        // 步骤3：调用DAO实现添加操作

        this.musiclistDao.drop(musiclist, sqLiteDatabase);

        // 步骤4：提交事务
        transactionManager.commitTransaction(sqLiteDatabase);

        // 步骤5：关闭事务
        transactionManager.closeTransaction(sqLiteDatabase);

        // 步骤6：关闭数据库连接
        connectionManager.closeConnection(sqLiteDatabase);

        return true;
    }

    @Override
    public List<MusicList> find(Context mContext) {
        // TODO Auto-generated method stub
        // 步骤1：获取数据库连接对象
        ConnectionManager connectionManager = new ConnectionManager();
        SQLiteDatabase sqLiteDatabase = connectionManager.openConnection(
                mContext, "read");

        // 步骤2：调用Dao层方法完成数据库操作
        List<MusicList> lstMessage = this.musiclistDao.select(sqLiteDatabase);

        // 步骤3：关闭数据库连接
        connectionManager.closeConnection(sqLiteDatabase);

        // 返回结果
        return lstMessage;
    }
}
