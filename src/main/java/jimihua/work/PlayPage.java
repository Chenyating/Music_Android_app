package jimihua.work;

import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import jimihua.view.ILrcBuilder;
import jimihua.view.ILrcView;
import jimihua.view.ILrcViewListener;
import jimihua.view.impl.DefaultLrcBuilder;
import jimihua.view.impl.LrcRow;

public class PlayPage extends AppCompatActivity {
    private Timer timer = new Timer();
    private Timer timer2 = new Timer();
    private SeekBar sb;
    private TextView play_page_songName,play_page_singerName;
    private ListView lv;
    private ImageView  homepage_hou_music,homepage_qian_music,homepage_bofang_music;
    public MediaPlayer mp;
    private String  songname,singername,songpath;
    private Cursor cursor,cursor2;
    private ActionBar actionBar;
    private StatusBarUtils sbu;


    public final static String TAG = "PlayPage";
    ILrcView mLrcView;   //自定义LrcView，用来展示歌词
    private int mPalyTimerDuration = 1000;//更新歌词的频率，每秒更新一次
    private Timer mTimer;//更新歌词的定时器
    private TimerTask mTask;//更新歌词的定时任务

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_page);
        actionBar = getSupportActionBar();
        actionBar.hide();
        ImageView imageView = (ImageView) findViewById(R.id.play_page_picture);
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.img_animation);
        LinearInterpolator lin = new LinearInterpolator();
        animation.setInterpolator(lin);
        imageView.startAnimation(animation);//40-44播放旋转动画

        sbu.setWindowStatusBarColor(this,R.color.Bofang_color);

        sb = (SeekBar) findViewById(R.id.play_page_seekbar);
        lv = (ListView) findViewById(R.id.play_listview);
        play_page_songName= (TextView) findViewById(R.id.play_page_songName);
        play_page_singerName= (TextView) findViewById(R.id.play_page_singerName);


        //获得歌曲信息的数据库表
        cursor = getContentResolver().
                query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null,
                        MediaStore.Audio.Media.DEFAULT_SORT_ORDER);

        SimpleAdapter adapter = new SimpleAdapter(this,getData(),R.layout.play_page_leftlist,
                new String[]{"songname","singername"},
                new int[]{R.id.list_songName,R.id.list_singerName});
        lv.setAdapter(adapter);

        homepage_hou_music= (ImageView) findViewById(R.id.homepage_hou_music);
        homepage_bofang_music= (ImageView) findViewById(R.id.homepage_bofang_music);
        homepage_qian_music= (ImageView) findViewById(R.id.homepage_qian_music);


        mp=Single.getObject();

        mLrcView=(ILrcView)findViewById(R.id.geci);

        //自动播放刚点进来的那首歌
        if(cursor.getCount()!=0) {

            cursor.moveToPosition(Single.position);

            songname = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
            singername = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
            songpath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));

            play_page_songName.setText(songname);
            play_page_singerName.setText(singername);
            songpath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));

            mp.reset();
            try {
                mp.setDataSource(songpath);
                mp.prepare();
            } catch (Exception e) {
                e.printStackTrace();
            }

            mp.start();

           if(Single.position==0)
            {Geci("wl_one");    } //封装的类方法 ， 加载歌词
            else if (Single.position==1){Geci("wl_two"); }
            else if (Single.position==2){Geci("wl_three"); }

            //控制音乐自动播放下一首音乐
             mp.setOnCompletionListener(new CompletionListener());
        }

        homepage_bofang_music.setOnClickListener(new ViewCLO());
        homepage_hou_music.setOnClickListener(new ViewCLO());
        homepage_qian_music.setOnClickListener(new ViewCLO());


        sb.setMax(100);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                int mCurrentPosition = mp.getCurrentPosition();
                int total = mp.getDuration();
                sb.setProgress(100 * mCurrentPosition / total);
            }
        }, 10, 1000);

        mTask = new LrcTask();
        timer2.scheduleAtFixedRate(mTask, 0, mPalyTimerDuration);

        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
//                timer.cancel();

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int sb_current = sb.getProgress();
                mp.seekTo(mp.getDuration() * sb_current / 100);

            }
        });

    //    mLrcView=(ILrcView)findViewById(R.id.geci);
   //    Geci("wl_one");     //封装的类方法 ， 加载歌词
        //设置自定义的LrcView上下拖动歌词时监听
        mLrcView.setListener(new ILrcViewListener() {
            //当歌词被用户上下拖动的时候回调该方法,从高亮的那一句歌词开始播放
            public void onLrcSeeked(int newPosition, LrcRow row) {
                if (mp != null) {
                    Log.d(TAG, "onLrcSeeked:" + row.time);
                    mp.seekTo((int) row.time);
                }
            }
        });
    }

    private void Geci(String s){
        //从assets目录下读取歌词文件内容
        String lrc = getfromAssets(s);
        //解析歌词构造器
        ILrcBuilder builder = new DefaultLrcBuilder();
        //解析歌词返回LrcRow集合
        List<LrcRow> rows = builder.getLrcRows(lrc);
        //将得到的歌词集合传给mLrcView用来展示
        mLrcView.setLrc(rows);
    }

    private String getfromAssets(String fileName) {
        try {
            InputStreamReader inputReader = new InputStreamReader( getResources().getAssets().open(fileName) );
            BufferedReader bufReader = new BufferedReader(inputReader);
            String line="";
            String result="";
            while((line = bufReader.readLine()) != null){
                if(line.trim().equals(""))
                    continue;
                result += line + "\r\n";
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    class LrcTask extends TimerTask{
        @Override
        public void run() {
            //获取歌曲播放的位置
            final long timePassed = mp.getCurrentPosition();
            PlayPage.this.runOnUiThread(new Runnable() {
                public void run() {
                    //滚动歌词
                    mLrcView.seekLrcToTime(timePassed);
                }
            });

        }
    };

    private class ViewCLO implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            switch (v.getId())
            {
                case R.id.homepage_bofang_music:

                    if(!mp.isPlaying()) {
                        mp.start();
                        homepage_bofang_music.setImageDrawable(getResources().getDrawable(R.drawable.pause));
                    }else {
                        mp.pause();
                        homepage_bofang_music.setImageDrawable(getResources().getDrawable(R.drawable.play));
                    }
                break;
                case R.id.homepage_qian_music:
              //      Geci("wl_one");
                    Single.position=(Single.position+cursor.getCount()-1)%(cursor.getCount());
                    cursor.moveToPosition(Single.position);

                    if(Single.position==0)
                    {Geci("wl_one");    } //封装的类方法 ， 加载歌词
                    else if (Single.position==1){Geci("wl_two"); }
                    else if (Single.position==2){Geci("wl_three"); }

                    songpath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
                    songname=cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
                    singername=cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));


                    play_page_songName.setText(songname);
                    play_page_singerName.setText(singername);

                    homepage_bofang_music.setImageDrawable(getResources().getDrawable(R.drawable.pause));
                    mp.reset();
                    try {
                        mp.setDataSource(songpath);
                        mp.prepare();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    mp.start();
                break;
                case R.id.homepage_hou_music:
                 //  Geci("wl_two");

                    Single.position=(Single.position+1)%(cursor.getCount());
                    cursor.moveToPosition(Single.position);

                    if(Single.position==0)
                    {Geci("wl_one");    } //封装的类方法 ， 加载歌词
                    else if (Single.position==1){Geci("wl_two"); }
                    else if (Single.position==2){Geci("wl_three"); }

                    songpath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
                    songname=cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
                    singername=cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));

                    play_page_songName.setText(songname);
                    play_page_singerName.setText(singername);


                    homepage_bofang_music.setImageDrawable(getResources().getDrawable(R.drawable.pause));
                    mp.reset();
                    try {
                        mp.setDataSource(songpath);
                        mp.prepare();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    mp.start();
                break;
            }

        }
    }

    //控制音乐自动播放下一首音乐的类

    private final class CompletionListener implements MediaPlayer.OnCompletionListener {

        @Override
        public void onCompletion(MediaPlayer mp) {
            Single.position = (Single.position + 1)%(cursor.getCount());

            cursor.moveToPosition(Single.position);
            songpath=cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));

            songname=cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
            singername=cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));


            play_page_songName.setText(songname);
            play_page_singerName.setText(singername);

            mp.reset();
            try {
                mp.setDataSource(songpath);
                mp.prepare();
            } catch (Exception e) {
                e.printStackTrace();
            }
            mp.start();
        }

    }


    private ArrayList<HashMap<String, Object>> getData() {
        ArrayList<HashMap<String, Object>> arrayList = new ArrayList<HashMap<String, Object>>();

        //获得歌曲信息的数据库表
        cursor2 = getContentResolver().
                query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,null, null, null,
                        MediaStore.Audio.Media.DEFAULT_SORT_ORDER);

        while (cursor2.moveToNext()) {
            //歌曲的名称 ：MediaStore.Audio.Media.TITLE
            String tilte = cursor2.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
            //歌曲文件的路径 ：MediaStore.Audio.Media.DATA
            String url = cursor2.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
            //歌手名称
            String name = cursor2.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
            //歌曲的总播放时长 ：MediaStore.Audio.Media.DURATION
            int duration = cursor2.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));
            //duration=duration/1000;
            HashMap<String, Object> temp = new HashMap<String, Object>();
            temp.put("songname", tilte);
            temp.put("singername", name);
            arrayList.add(temp);

        }
        return arrayList;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(sb.isFocusable()) {
            return false;
        }else{
            return true;
        }
    }



}
