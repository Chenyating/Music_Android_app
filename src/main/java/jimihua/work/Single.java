package jimihua.work;

import android.media.MediaPlayer;

/**
 * Created by Me on 2017/2/28.
 */
public class Single {

    private static MediaPlayer mp;
    public static int position=0;
    public static Boolean first=true;
    private Single(){}
    public static MediaPlayer getObject(){
        if(mp==null)
            mp=new MediaPlayer();
        return mp;
    }
}
