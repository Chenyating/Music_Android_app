package jimihua.user.biz;

import android.content.Context;

import java.util.List;

import jimihua.user.po.Message;

/**
 * Created by Me on 2017/2/22.
 */
public interface MessageBiz {

    public abstract boolean add(final Context mContext, final Message message);

    public abstract boolean alter(final Context mContext, final Message message);


    public abstract List<Message> find(final Context mContext);
}
