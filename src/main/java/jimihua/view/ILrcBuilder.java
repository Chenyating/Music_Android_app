package jimihua.view;


import java.util.List;

import jimihua.view.impl.LrcRow;

/**
 * 解析歌词，得到LrcRow的集合
 */
public interface ILrcBuilder {
    List<LrcRow> getLrcRows(String rawLrc);
}
