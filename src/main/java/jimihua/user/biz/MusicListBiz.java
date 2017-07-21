package jimihua.user.biz;

import android.content.Context;

import java.util.List;

import jimihua.user.po.MusicList;

/**
 * Created by Me on 2017/3/1.
 */
public interface MusicListBiz {

    public abstract boolean add(final Context mContext, final MusicList musiclist);

    public abstract boolean remove(final Context mContext, final MusicList musiclist);

    public abstract List<MusicList> find(final Context mContext);
}
