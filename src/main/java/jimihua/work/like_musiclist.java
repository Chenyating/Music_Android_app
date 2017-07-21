package jimihua.work;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import jimihua.picturePlay.MyViewPager;
import jimihua.picturePlay.PictureAdapter;
import jimihua.user.biz.MusicListBiz;
import jimihua.user.biz.impl.MusicListBizImpl;
import jimihua.user.po.MusicList;

public class like_musiclist extends AppCompatActivity {

    private  ListView  music_list_listview;
    private ArrayList<HashMap<String, Object>> songmessage;
    private ArrayList<String> songList;
    private MusicListBiz musiclistBiz1;
    private List<MusicList> lstMessage;
    private Cursor cursor;
    private ActionBar actionBar;
    private StatusBarUtils sbu;
    private RelativeLayout like_list;
//以下是轮播的
    private RelativeLayout layout;
    private List<ImageView> images;
    private MyViewPager viewPager;
    private LinearLayout dots_layout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_list_like);
        initView();
        actionBar = getSupportActionBar();
        actionBar.hide();
        sbu.setWindowStatusBarColor(this,android.R.color.white);

        music_list_listview= (ListView) findViewById(R.id.music_list_listview_like);

        like_list = (RelativeLayout) findViewById(R.id.like_list);
        songmessage = new ArrayList<HashMap<String, Object>>();
        songList=new ArrayList<String>();

        //获得歌曲信息的数据库表
        cursor = getContentResolver().
                query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,null, null, null,
                        MediaStore.Audio.Media.DEFAULT_SORT_ORDER);

        while (cursor.moveToNext()) {
            //歌曲的名称 ：MediaStore.Audio.Media.TITLE
            String tilte = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
            //歌曲文件的路径 ：MediaStore.Audio.Media.DATA
            String url = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
            //歌手名称
            String name = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
            //歌曲的总播放时长 ：MediaStore.Audio.Media.DURATION
            int duration = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));
            duration=duration/1000;
            HashMap<String, Object> temp = new HashMap<String, Object>();
            temp.put("songname", tilte);
            temp.put("singername", name);
            temp.put("songduration", "时长" + duration / 60 + ":" + duration % 60);
            temp.put("position_single",cursor.getPosition());

            musiclistBiz1=new MusicListBizImpl();
            List<MusicList> lstMessage=musiclistBiz1.find(like_musiclist.this);

            for(int i=0;i<lstMessage.size();i++){

                if (lstMessage.get(i).getPosition()==cursor.getPosition()) {
                    songmessage.add(temp);
                    songList.add(url);
                    break;
                }
            }

        }



        SimpleAdapter adapter = new SimpleAdapter(this, songmessage, R.layout.activity_music_list_item_like,
                new String[]{"songname", "singername","songduration"},
                new int[]{R.id.music_list_songname_like, R.id.music_list_singername_like,R.id.music_list_songduration_like});

        music_list_listview.setAdapter(adapter);

        music_list_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                Single.position = (int) songmessage.get(position).get("position_single");

                tiaozhuan();
            }
        });

    }

    private void tiaozhuan(){

        Intent in = new Intent(Intent.ACTION_MAIN);
        in.setClass(like_musiclist.this, PlayPage.class);
        startActivity(in);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_like_musiclist, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onResume(){
        super.onResume();
        //随时更新曲目

        songmessage = new ArrayList<HashMap<String, Object>>();
        songList=new ArrayList<String>();

        //获得歌曲信息的数据库表
        cursor = getContentResolver().
                query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,null, null, null,
                        MediaStore.Audio.Media.DEFAULT_SORT_ORDER);

        while (cursor.moveToNext()) {
            //歌曲的名称 ：MediaStore.Audio.Media.TITLE
            String tilte = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
            //歌曲文件的路径 ：MediaStore.Audio.Media.DATA
            String url = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
            //歌手名称
            String name = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
            //歌曲的总播放时长 ：MediaStore.Audio.Media.DURATION
            int duration = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));
            duration=duration/1000;
            HashMap<String, Object> temp = new HashMap<String, Object>();
            temp.put("songname", tilte);
            temp.put("singername", name);
            temp.put("songduration", "时长" + duration / 60 + ":" + duration % 60);
            temp.put("position_single",cursor.getPosition());

            musiclistBiz1=new MusicListBizImpl();
            List<MusicList> lstMessage=musiclistBiz1.find(like_musiclist.this);

            for(int i=0;i<lstMessage.size();i++){

                if (lstMessage.get(i).getPosition()==cursor.getPosition()) {
                    songmessage.add(temp);
                    songList.add(url);
                    break;
                }
            }

        }

    }
    private void initView() {
        layout = (RelativeLayout) findViewById(R.id.PicturePlay);
        getImages();
        addHeader();
    }

    private void addHeader() {
        View headerView = LayoutInflater.from(this).inflate(R.layout.viewpager_round_layout, null);
        LinearLayout images_layout = (LinearLayout) headerView.findViewById(R.id.carousel_image_layout);
        dots_layout = (LinearLayout) headerView.findViewById(R.id.image_round_layout);
        int width = (int) getWindowInfo(this);
        ViewGroup.LayoutParams params = images_layout.getLayoutParams();
        params.width = width;
        params.height = (int) (width * 0.6746);
        images_layout.setLayoutParams(params);

        viewPager = new MyViewPager(this);
        initImageRounds();
        viewPager.setImages(images);
        viewPager.setAdapter(new PictureAdapter(images));
        viewPager.setCurrentItem(Integer.MAX_VALUE / 2);
        images_layout.addView(viewPager);
        //viewPager.setOnImageItemClickListener(this);
        layout.addView(headerView);
    }

    private void getImages() {
        images = new ArrayList<>();
        ImageView img1 = new ImageView(this);
        img1.setImageResource(R.drawable.ic_guide_1);
        img1.setScaleType(ImageView.ScaleType.CENTER_CROP);
        images.add(img1);

        ImageView img2 = new ImageView(this);
        img2.setImageResource(R.drawable.ic_guide_2);
        img2.setScaleType(ImageView.ScaleType.CENTER_CROP);
        images.add(img2);

        ImageView img3 = new ImageView(this);
        img3.setImageResource(R.drawable.ic_guide_3);
        img3.setScaleType(ImageView.ScaleType.CENTER_CROP);
        images.add(img3);

        ImageView img4 = new ImageView(this);
        img4.setImageResource(R.drawable.ic_guide_2);
        img4.setScaleType(ImageView.ScaleType.CENTER_CROP);
        images.add(img4);
    }

    private void initImageRounds() {
        List<ImageView> dots = new ArrayList<>();
        dots_layout.removeAllViews();

        /**
         *当轮播图大于1张时小圆点显示
         * */
        if (images.size() > 1) {
            dots_layout.setVisibility(View.VISIBLE);
        } else {
            dots_layout.setVisibility(View.INVISIBLE);
        }
        for (int i = 0; i < images.size(); i++) {
            ImageView round = new ImageView(this);
            /**
             * 默认让第一张图片显示深颜色的圆点
             * */
            if (i == 0) {
                round.setImageResource(R.drawable.dot_focus);
            } else {
                round.setImageResource(R.drawable.dot_normal);
            }
            dots.add(round);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, -2);
            params.leftMargin = 20;
            dots_layout.addView(round, params);
        }
        viewPager.setDots(dots);
    }


    private static float getWindowInfo(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.widthPixels;
    }



    public void onItemClick(int itemPosition) {
        Toast.makeText(this, "当前是第" + (itemPosition + 1) + "页图片", Toast.LENGTH_SHORT).show();
    }

}
