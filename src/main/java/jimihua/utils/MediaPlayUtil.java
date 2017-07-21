package jimihua.utils;

import android.media.MediaPlayer;

/**
 * Created by hfx on 14-10-15.
 */
public class MediaPlayUtil {
    private static MediaPlayUtil mMediaPlayUtil;
    private MediaPlayer mMediaPlayer;


    public void setPlayOnCompleteListener(MediaPlayer.OnCompletionListener playOnCompleteListener){
        if(mMediaPlayer != null){
            mMediaPlayer.setOnCompletionListener(playOnCompleteListener);
        }
    }

    public static MediaPlayUtil getInstance(){
        if(mMediaPlayUtil == null){
            mMediaPlayUtil = new MediaPlayUtil();
        }
        return  mMediaPlayUtil;
    }

    private MediaPlayUtil(){
        mMediaPlayer = new MediaPlayer();
    }

    public void play(String soundFilePath){
        if(mMediaPlayer == null){
            return;
        }
        try {
            mMediaPlayer.reset();
            mMediaPlayer.setDataSource(soundFilePath);
            mMediaPlayer.prepare();
            mMediaPlayer.start();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void pause(){
        if(mMediaPlayer != null){
            mMediaPlayer.pause();
        }
    }

    public void stop(){
        if(mMediaPlayer != null && mMediaPlayer.isPlaying()){
            mMediaPlayer.stop();
        }
    }

    public int getCurrentPosition(){
        if(mMediaPlayer != null && mMediaPlayer.isPlaying()){
            return mMediaPlayer.getCurrentPosition();
        }else{
            return 0;
        }
    }

    public int getDutation(){
        if(mMediaPlayer!= null && mMediaPlayer.isPlaying()){
            return mMediaPlayer.getDuration();
        }else{
            return 0;
        }
    }

    public boolean isPlaying(){
        if(mMediaPlayer != null){
            return mMediaPlayer.isPlaying();
        }else{
            return false;
        }
    }
}
