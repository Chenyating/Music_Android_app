package jimihua.work;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import jimihua.ResideMenu.ResideMenu;
import jimihua.ResideMenu.ResideMenuItem;
import jimihua.picturePlay.MyViewPager;
import jimihua.picturePlay.PictureAdapter;

public class HomePage extends Activity implements View.OnClickListener,MyViewPager.OnImageItemClickListener{

    private ResideMenu resideMenu;
    private HomePage mContext;
    private ResideMenuItem item_model;   //模式切换
    private ResideMenuItem item_model_switch;   //修改密码
    private ResideMenuItem item_lock_lyric;   //找回密码
    private ResideMenuItem item_yaoyiyao;   //直接退出
    private LinearLayout goto_playpage;   //点击下面的，进入歌曲播放页面
    private ImageButton home_page_btnsou;
    private EditText home_page_sousuo;
    private ImageButton home_page_left;
    private ImageView bendi,woxihuan;
    private Cursor cursor;
    private MediaPlayer mp;
    private String songpath,songname,singer;
    private Boolean first;//第一次点击最底下的播放键
    private ImageView homepage_btn_bofang,homepage_btn_qian,homepage_btn_hou;//最下端的播放按钮
    private TextView homepage_songName,homepage_singer;
     //以下是轮播的
    private RelativeLayout layout;
    private List<ImageView> images;
    private MyViewPager viewPager;
    private LinearLayout dots_layout;
    private StatusBarUtils sbu;
    private model_change mc;
    private RelativeLayout up;
    private ImageView luyin;
    private RelativeLayout activity_home_page;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        initView();

        mc = new model_change();
        LinearInterpolator lin = new LinearInterpolator();

        ImageView imageView = (ImageView) findViewById(R.id.touxiang);
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.img_animation);
        animation.setInterpolator(lin);
        imageView.startAnimation(animation);//51-55头像旋转动画

        activity_home_page = (RelativeLayout) findViewById(R.id.activity_home_page);
        sbu.setWindowStatusBarColor(this,android.R.color.holo_red_dark);
        homepage_btn_bofang = (ImageView) findViewById(R.id.homepage_btn_bofang);
        homepage_btn_qian = (ImageView) findViewById(R.id.homepage_btn_qian);
        homepage_btn_hou = (ImageView) findViewById(R.id.homepage_btn_hou);
        up = (RelativeLayout) findViewById(R.id.up);
        homepage_singer= (TextView) findViewById(R.id.homepage_singer);
        homepage_songName= (TextView) findViewById(R.id.homepage_songName);
        luyin = (ImageView) findViewById(R.id.luyin);

        mp = Single.getObject();//实例化MediaPlayer
        //获得歌曲信息的数据库表
        cursor = getContentResolver().
                query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null,
                        MediaStore.Audio.Media.DEFAULT_SORT_ORDER);

        //如果有歌曲，默认显示第一首歌曲的信息
        if(cursor.getCount()!=0) {
            cursor.moveToPosition(Single.position);

            songname = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
            singer = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
            homepage_singer.setText(singer);
            homepage_songName.setText(songname);

            if (mp.isPlaying()){
                homepage_btn_bofang.setImageResource(R.drawable.playbar_btn_pause);
            }else if (mp.isPlaying()==false&&Single.first==false){
                homepage_btn_bofang.setImageResource(R.drawable.playbar_btn_play);
            }

        }

        goto_playpage = (LinearLayout) findViewById(R.id.goto_playpage);
        homepage_btn_bofang.setOnClickListener(this);
        homepage_btn_qian.setOnClickListener(this);
        homepage_btn_hou.setOnClickListener(this);
        luyin.setOnClickListener(this);
        homepage_btn_qian.setEnabled(false);
        homepage_btn_hou.setEnabled(false);
        home_page_left = (ImageButton) findViewById(R.id.home_page_left); //左侧话打开
        bendi= (ImageView) findViewById(R.id.bendi);//本地
        woxihuan= (ImageView) findViewById(R.id.woxihuan);

        mContext = this;
        setUpMenu();

        mhandler.sendEmptyMessage(0);

    }

private android.os.Handler mhandler=new android.os.Handler(){

    public  void handleMessage(android.os.Message esg){

        mhandler.sendEmptyMessageDelayed(0, 300);
    };

};


    private void setUpMenu(){
        resideMenu = new ResideMenu(this);
        resideMenu.setUse3D(true);
        resideMenu.setBackground(R.drawable.bg); //  更改左侧滑的背景

        resideMenu.attachToActivity(this);
        resideMenu.setScaleValue(0.8f);

        //实例化侧滑栏按钮
        item_model = new ResideMenuItem(this,R.drawable.msqh,"模式切换");
        item_model_switch = new ResideMenuItem(this,R.drawable.xgmm,"修改密码");
        item_lock_lyric = new ResideMenuItem(this,R.drawable.zhmm,"找回密码");
        item_yaoyiyao = new ResideMenuItem(this,R.drawable.zjtc,"直接退出");

        item_model.setOnClickListener(this);
        item_model_switch.setOnClickListener(this);
        item_lock_lyric.setOnClickListener(this);
        item_yaoyiyao.setOnClickListener(this);

        home_page_left.setOnClickListener(this);
        goto_playpage.setOnClickListener(this);    //去播放界面的触发事件

        bendi.setOnClickListener(this);
        woxihuan.setOnClickListener(this);

        //向左侧滑栏添加按钮
        resideMenu.addMenuItem(item_model,ResideMenu.DIRECTION_LEFT);
        resideMenu.addMenuItem(item_model_switch,ResideMenu.DIRECTION_LEFT);
        resideMenu.addMenuItem(item_lock_lyric,ResideMenu.DIRECTION_LEFT);
        resideMenu.addMenuItem(item_yaoyiyao, ResideMenu.DIRECTION_LEFT);

        //关闭右边的侧滑栏
        resideMenu.setSwipeDirectionDisable(ResideMenu.DIRECTION_RIGHT);

        //左上方打开侧滑栏的按钮
        home_page_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resideMenu.openMenu(ResideMenu.DIRECTION_LEFT);
            }
        });

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return resideMenu.dispatchTouchEvent(ev);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){

            //播放
            case R.id.homepage_btn_bofang:

                if(cursor.getCount()==0){
                    Toast.makeText(HomePage.this, "手机中没有歌曲哦", Toast.LENGTH_LONG)
                            .show();
                }
                else{
                  if( mp.isPlaying()) {//是现在是播放状态，想要暂停
                     homepage_btn_bofang.setImageResource(R.drawable.playbar_btn_play);
                      mp.pause();

                   }else{//现在是暂停状态，想要播放
                      homepage_btn_bofang.setImageResource(R.drawable.playbar_btn_pause);
                      if (Single.first) {
                          //是否是第一次点击播放
                          Single.first=false;
                          songpath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
                          mp.reset();
                          try {
                              mp.setDataSource(songpath);
                              mp.prepare();
                          } catch (Exception e) {
                              e.printStackTrace();
                          }
                          mp.start();
                          homepage_btn_qian.setEnabled(true);
                          homepage_btn_hou.setEnabled(true);
                      }else {
                          mp.start();
                      }
                }
                }break;
            //前一首
            case R.id.homepage_btn_qian:

                Single.position=(Single.position+cursor.getCount()-1)%(cursor.getCount());
                cursor.moveToPosition(Single.position);

                songpath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
                songname=cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
                singer=cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));

                homepage_singer.setText(singer);
                homepage_songName.setText(songname);

                homepage_btn_bofang.setImageResource(R.drawable.playbar_btn_pause);
                mp.reset();
                try {
                    mp.setDataSource(songpath);
                    mp.prepare();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                mp.start();

                break;

            case R.id.homepage_btn_hou:
                Single.position=(Single.position+1)%(cursor.getCount());
                cursor.moveToPosition(Single.position);
                songpath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
                songname=cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
                singer=cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));

                homepage_singer.setText(singer);
                homepage_songName.setText(songname);

                homepage_btn_bofang.setImageResource(R.drawable.playbar_btn_pause);
                mp.reset();
                try {
                    mp.setDataSource(songpath);
                    mp.prepare();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                mp.start();

                break;

            case R.id.goto_playpage:

                Intent i1 = new Intent(HomePage.this,PlayPage.class);
                startActivity(i1);
                break;

            case R.id.bendi :

                Intent i2 = new Intent(HomePage.this,music_list.class);
                startActivity(i2);
                break;

            case R.id.woxihuan :

                Intent i3 = new Intent(HomePage.this,like_musiclist.class);
                startActivity(i3);
                break;

            case R.id.luyin:
                Intent i4 = new Intent(HomePage.this,Record_Page.class);
                startActivity(i4);
                break;

        }
        if (view==item_model_switch){
            Intent i2 = new Intent(HomePage.this,changepassword.class);
            startActivity(i2);
        }else if (view== item_lock_lyric){
            Intent i2 = new Intent(HomePage.this,searchpassword.class);
            startActivity(i2);
        }else if (view== item_yaoyiyao){
          finish();
        }else if(view == item_model){
            Model();
            resideMenu.closeMenu();
        }

    }

    public void Model(){
        int q = mc.getI();
        if(q == 1){
            sbu.setWindowStatusBarColor(this,android.R.color.black);
            //黑
            up.setBackgroundColor(Color.BLACK);
            resideMenu.setBackground(R.drawable.timg);
//            homepage_songName.setTextColor(getResources().getColor(R.color.white));
//            homepage_singer.setTextColor(getResources().getColor(R.color.white));
//            activity_home_page.setBackground(getResources().getDrawable(R.drawable.ttimg));
//            Toast.makeText(this,"", Toast.LENGTH_SHORT).show();

            mc.setI(2);
        }else if (q == 3){
            sbu.setWindowStatusBarColor(this,android.R.color.holo_red_dark);
            //白天模式加这里
            up.setBackgroundColor(Color.parseColor("#CC0000"));
            homepage_songName.setTextColor(getResources().getColor(R.color.black));
            homepage_singer.setTextColor(getResources().getColor(R.color.black));
            resideMenu.setBackground(R.drawable.bg);
            activity_home_page.setBackgroundColor(Color.parseColor("#ffffff"));
//            Toast.makeText(this,"白天模式", Toast.LENGTH_SHORT).show();
            mc.setI(1);
        }else if(q == 2){
            sbu.setWindowStatusBarColor(this,R.color.blue);
            up.setBackgroundColor(getResources().getColor(R.color.blue));
            resideMenu.setBackground(R.drawable.timg3);
            mc.setI(3);
        }
    }


   public boolean onTouchEvent() {
        boolean bo = true;
        return bo;
    }
    public ResideMenu getResideMenu(){
        return resideMenu;
    }

//    private void changeFragment(
//            Fragment targetFragment){
//        resideMenu.clearIgnoredViewList();
//        getSupportFragmentManager()
//                .beginTransaction()
//                .replace(R.id.main_fragment, targetFragment, "fragment")
//                .setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
//                .commit();
//    }
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
        viewPager.setOnImageItemClickListener(this);
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

    @Override
    public void onItemClick(int itemPosition) {
        Toast.makeText(this, "当前是第" + (itemPosition + 1) + "页图片", Toast.LENGTH_SHORT).show();
    }

    public void onResume(){
        super.onResume();
        //如果有歌曲，默认显示第一首歌曲的信息
        if(cursor.getCount()!=0) {
            cursor.moveToPosition(Single.position);

            songname = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
            singer = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
            homepage_singer.setText(singer);
            homepage_songName.setText(songname);

            if (mp.isPlaying()){
                homepage_btn_bofang.setImageResource(R.drawable.playbar_btn_pause);
            }else if (mp.isPlaying()==false&&Single.first==false){
                homepage_btn_bofang.setImageResource(R.drawable.playbar_btn_play);
            }

        }

    }
}
