package jimihua.user.dao;

import android.database.sqlite.SQLiteDatabase;

import java.util.List;

import jimihua.user.po.MusicList;

/**
 * Created by Me on 2017/3/1.
 */
public interface MusicListDao {
    public abstract boolean insert(final MusicList musicList,
                                   final SQLiteDatabase sqLiteDatabase);

    public abstract boolean drop(final MusicList musicList,
                                 final SQLiteDatabase sqLiteDatabase);

    public abstract List<MusicList> select(final SQLiteDatabase sqLiteDatabase);
}
