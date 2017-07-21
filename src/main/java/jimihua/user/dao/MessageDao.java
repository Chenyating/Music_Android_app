package jimihua.user.dao;

import android.database.sqlite.SQLiteDatabase;

import java.util.List;

import jimihua.user.po.Message;

/**
 * Created by Me on 2017/2/22.
 */
public interface MessageDao {

    public abstract boolean insert(final Message message,
                                   final SQLiteDatabase sqLiteDatabase);

    public abstract boolean update(final Message message,
                                   final SQLiteDatabase sqLiteDatabase);
    
    public abstract List<Message> select(final SQLiteDatabase sqLiteDatabase);
}