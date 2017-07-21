package jimihua.work;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import jimihua.utils.MediaPlayUtil;
import jimihua.utils.StringUtil;

public class Record_Page extends AppCompatActivity {

    //titlebar
    private ActionBar actionBar;
    private StatusBarUtils sbu;
    private model_change mc;
    private RelativeLayout record_layout;

    private Button songhua;
    private TextView mTvTime,mTvNotice,mTvTimeLengh;
    private ImageView mIvRecord,mIvVoice,mIvVoiceAnim,anim_right,anim_left;
    private LinearLayout mSoundLengthLayout;
    private RelativeLayout mRlVoiceLayout;
    // 语音相关
    private ScaleAnimation mScaleBigAnimation;
    private ScaleAnimation mScaleLittleAnimation;
    private String mSoundData = "";//语音数据
    private String dataPath;
    private boolean isStop;  // 录音是否结束的标志 超过两分钟停止
    private boolean isCanceled = false; // 是否取消录音
    private float downY;
    private MediaRecorder mRecorder;
    private MediaPlayUtil mMediaPlayUtil;
    private long mStartTime;
    private long mEndTime;
    private int mTime;
    private String mVoiceData;
    private AnimationDrawable mImageAnim,mLeftAnim,mRightAnim;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };
    //悬浮的变量1是黄色花的。
    private WindowManager mWindowManager;
    private WindowManager.LayoutParams mLayout;
    private DesktopLayout mDesktopLayout;
    private WindowManager mWindowManager1;
    private WindowManager.LayoutParams mLayout1;
    private DesktopLayout1 mDesktopLayout1;
    private long startTime;
    // 声明屏幕的宽高
    float x, y;
    int top;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.record);

        actionBar = getSupportActionBar();
        actionBar.hide();
        sbu.setWindowStatusBarColor(this,android.R.color.white);
        mc = new model_change();
        record_layout = (RelativeLayout) findViewById(R.id.record_layout);
        int judge = mc.getI();
        if(judge == 1){
            record_layout.setBackgroundColor(Color.parseColor("#ffffff"));
        }else if(judge == 0){
            record_layout.setBackgroundColor(Color.parseColor("#000000"));
        }

        initSoundData();
        initView();
        createWindowManager();
        createDesktopLayout();
        createWindowManager1();
        createDesktopLayout1();
        ImageButton honghua = (ImageButton) findViewById(R.id.honghua);
        honghua.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showDesk();
            }
        });
        ImageButton huanghua = (ImageButton) findViewById(R.id.huanghua);
        huanghua.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showDesk1();
            }
        });
    }
    public void initView(){
        //计时
        mTvTime = (TextView) findViewById(R.id.chat_tv_sound_length);
        //提示信息
        mTvNotice = (TextView) findViewById(R.id.chat_tv_sound_notice);
        //图片按钮
        mIvRecord = (ImageView) findViewById(R.id.chat_record);
        mSoundLengthLayout = (LinearLayout) findViewById(R.id.chat_tv_sound_length_layout);
        //播放时录音模块部分
        mRlVoiceLayout = (RelativeLayout) findViewById(R.id.voice_layout);
        mIvVoice = (ImageView) findViewById(R.id.iv_voice_image);
        mIvVoiceAnim = (ImageView) findViewById(R.id.iv_voice_image_anim);
        mTvTimeLengh = (TextView) findViewById(R.id.chat_tv_voice_len);
        //计时两侧的图像
        anim_right=(ImageView)findViewById(R.id.anim_right);
        anim_left=(ImageView)findViewById(R.id.anim_left);


        mIvRecord.setOnTouchListener(new VoiceTouch());

        // TODO 播放录音
        mRlVoiceLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mMediaPlayUtil.isPlaying()) {
                    mMediaPlayUtil.stop();
                    mImageAnim.stop();
                    mIvVoice.setVisibility(View.VISIBLE);
                    mIvVoiceAnim.setVisibility(View.GONE);
                } else {
                    startAnim();
                    mMediaPlayUtil.play(StringUtil.decoderBase64File(mVoiceData));
                }
            }
        });
    }
    public void startAnim() {

        mImageAnim = (AnimationDrawable) mIvVoiceAnim.getBackground();

        mIvVoiceAnim.setVisibility(View.VISIBLE);
        mIvVoice.setVisibility(View.GONE);
        mImageAnim.start();
        mMediaPlayUtil.setPlayOnCompleteListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                mIvVoice.setVisibility(View.VISIBLE);
                mIvVoiceAnim.setVisibility(View.GONE);
            }
        });
    }  //语音播放效果
    public void initSoundData() {
        dataPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/AsRecrod/Sounds/";
        File folder = new File(dataPath);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        mMediaPlayUtil = MediaPlayUtil.getInstance();
    }//录音存放路径
    class VoiceTouch implements View.OnTouchListener {

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {

            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    downY = motionEvent.getY();
                    mIvRecord.setImageDrawable(getResources().getDrawable(R.drawable.record_pressed));
                    mTvNotice.setText("向上滑动取消发送");
                    mSoundData = dataPath + getRandomFileName() + ".amr";//语音数据 组合名

                    // TODO 防止开权限后崩溃
                    if (mRecorder != null) {
                        mRecorder.reset();
                    } else {
                        mRecorder = new MediaRecorder();
                    }
                    mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                    mRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
                    mRecorder.setOutputFile(mSoundData);
                    mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                    try {
                        mRecorder.prepare();
                    } catch (IOException e) {
                        Log.i("recoder", "prepare() failed-Exception-" + e.toString());
                    }
                    try {
                        mRecorder.start();
                        mStartTime = System.currentTimeMillis();
                        mSoundLengthLayout.setVisibility(View.VISIBLE);
                        mTvTime.setText("0" + '"');
                        // TODO 开启定时器 每次一秒 到了延迟时间就执行该方法
                        mHandler.postDelayed(runnable, 1000);
                    } catch (Exception e) {
                        Log.i("recoder", "prepare() failed-Exception-"+e.toString());
                    }
                    mLeftAnim=(AnimationDrawable)anim_left.getBackground();
                    mRightAnim=(AnimationDrawable)anim_right.getBackground();
                    initScaleAnim();
                    // TODO 录音过程重复动画 即一大一小放大缩小
                    mScaleBigAnimation.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            if (mScaleLittleAnimation != null) {
                                mIvRecord.startAnimation(mScaleLittleAnimation);
                            }
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                    mScaleLittleAnimation.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            if (mScaleBigAnimation != null) {
                                mIvRecord.startAnimation(mScaleBigAnimation);
                            }
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                    mIvRecord.startAnimation(mScaleBigAnimation);
                    mLeftAnim.start();
                    mRightAnim.start();

                    break;
                case MotionEvent.ACTION_UP:
                    if (!isStop) {
                        mEndTime = System.currentTimeMillis();
                        mTime = (int) ((mEndTime - mStartTime) / 1000);
                        stopRecord();
                        mIvRecord.setVisibility(View.VISIBLE);
                        try {
                            mVoiceData = StringUtil.encodeBase64File(mSoundData);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        if (isCanceled) {
                            deleteSoundFileUnSend();
                            mTvTime.setText("0" +'"' );
                            mTvNotice.setText("录音取消");
                            mRlVoiceLayout.setVisibility(View.GONE);
                        } else {
                            mIvRecord.setImageDrawable(getResources().getDrawable(R.drawable.record));
                            mRlVoiceLayout.setVisibility(View.VISIBLE);
                            mTvTimeLengh.setText(mTime + "" +'"' );
                        }
                    }else{
                        mTvTime.setText("0");
                        mIvRecord.setImageDrawable(getResources().getDrawable(R.drawable.record));
                        mTvNotice.setText("重新录音");
                    }
                    break;
                case MotionEvent.ACTION_CANCEL: // 首次开权限时会走这里，录音取消
                    Log.i("record_test","权限影响录音录音");
                    mHandler.removeCallbacks(runnable);
                    mSoundLengthLayout.setVisibility(View.GONE);

                    // TODO 这里一定注意，先release，还要置为null，否则录音会发生错误，还有可能崩溃
                    if (mRecorder != null) {
                        mRecorder.release();
                        mRecorder = null;
                        System.gc();
                    }
                    mIvRecord.setImageDrawable(getResources().getDrawable(R.drawable.record));
                    mIvRecord.clearAnimation();
                    mTvNotice.setText("按住说话");
                    isCanceled = true;
                    mScaleBigAnimation = null;
                    mScaleLittleAnimation = null;

                    break;

                case MotionEvent.ACTION_MOVE: // 滑动手指
                    float moveY = motionEvent.getY();
                    if (downY - moveY > 100) {
                        isCanceled = true;
                        mTvNotice.setText("松开手指可取消录音");
                        mIvRecord.setImageDrawable(getResources().getDrawable(R.drawable.record));
                    }
                    if (downY - moveY < 20) {
                        isCanceled = false;
                        mIvRecord.setImageDrawable(getResources().getDrawable(R.drawable.record_pressed));
                        mTvNotice.setText("向上滑动取消发送");
                    }
                    break;

            }
            return true;
        }

    }//录音的触摸监听
    public void stopRecord() {
        mIvRecord.clearAnimation();
        mLeftAnim.stop();
        mRightAnim.stop();

        mScaleBigAnimation = null;
        mScaleLittleAnimation = null;
        if (mTime < 1) {
            deleteSoundFileUnSend();
            isCanceled = true;
            Toast.makeText(Record_Page.this, "录音时间太短，长按开始录音", Toast.LENGTH_SHORT).show();
        } else {
            mTvNotice.setText("录音成功");
            // 不加  "" 空串 会出  Resources$NotFoundException 错误
            mTvTime.setText(mTime + "" + '"');
            Log.i("record_test","录音成功");

        }
        //mRecorder.setOnErrorListener(null);
        try {
            mRecorder.stop();
            mRecorder.reset();
            mRecorder.release();
        } catch (Exception e) {
            Log.i("recoder", "stop() failed");
            isCanceled = true;
            mIvRecord.setVisibility(View.VISIBLE);
            mTvTime.setText("");
            Toast.makeText(Record_Page.this, "录音发生错误,请重新录音", Toast.LENGTH_LONG).show();
            Log.i("record_test","录音发生错误");
        }
        mHandler.removeCallbacks(runnable);
        if (mRecorder != null) {
            mRecorder = null;
            System.gc();
        }

    }//结束录音
    public String getRandomFileName() {
        String rel = "";
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        Date curDate = new Date(System.currentTimeMillis());
        rel = formatter.format(curDate);
        rel = rel + new Random().nextInt(1000);
        return rel;
    }//生成一个随机的文件名
    public void initScaleAnim() {

        // TODO 放大
        mScaleBigAnimation = new ScaleAnimation(1.0f, 1.3f, 1.0f, 1.3f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        mScaleBigAnimation.setDuration(700);

        // TODO 缩小
        mScaleLittleAnimation = new ScaleAnimation(1.3f, 1.0f, 1.3f, 1.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        mScaleLittleAnimation.setDuration(700);


    }//初始化录音动画
    public void deleteSoundFileUnSend() {
        mTime = 0;
        if (!"".equals(mSoundData)) {
            try {
                File file = new File(mSoundData);
                file.delete();
                mSoundData = "";
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }//录音完毕后，若不发送，则删除文件
    public class DesktopLayout extends LinearLayout {
        public DesktopLayout(Context context) {
            super(context);
            setOrientation(LinearLayout.VERTICAL);// 水平排列
            //设置宽高
            this.setLayoutParams( new LayoutParams(LayoutParams.WRAP_CONTENT,
                    LayoutParams.WRAP_CONTENT));
            View view = LayoutInflater.from(context).inflate(
                    R.layout.desklayout1, null);
            this.addView(view);
        }

    } //悬浮等
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        Rect rect = new Rect();
        // /取得整个视图部分,注意，如果你要设置标题样式，这个必须出现在标题样式之后，否则会出错
        getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
        top = rect.top;//状态栏的高度，所以rect.height,rect.width分别是系统的高度的宽度

        Log.i("top", "" + top);
    } //创建悬浮
    private void showDesk() {
        if (Flowers.second==false){
            mWindowManager.addView(mDesktopLayout, mLayout);
            Flowers.second=true;}

    }//显示DesktopLayout
    private void closeDesk() {
        mWindowManager.removeView(mDesktopLayout);
        Flowers.second=false;
    }//关闭DesktopLayout
    private void createWindowManager() {
        // 取得系统窗体
        mWindowManager = (WindowManager) getApplicationContext()
                .getSystemService(WINDOW_SERVICE);

        // 窗体的布局样式
        mLayout = new WindowManager.LayoutParams();

        // 设置窗体显示类型——TYPE_SYSTEM_ALERT(系统提示)
        mLayout.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;

        // 设置窗体焦点及触摸：
        // FLAG_NOT_FOCUSABLE(不能获得按键输入焦点)
        mLayout.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;

        // 设置显示的模式
        mLayout.format = PixelFormat.RGBA_8888;

        // 设置对齐的方法
        mLayout.gravity = Gravity.TOP | Gravity.LEFT;

        // 设置窗体宽度和高度
        mLayout.width = WindowManager.LayoutParams.WRAP_CONTENT;
        mLayout.height = WindowManager.LayoutParams.WRAP_CONTENT;

    }
    private void createDesktopLayout() {
        mDesktopLayout = new DesktopLayout(this);
        mDesktopLayout.setOnTouchListener(new View.OnTouchListener() {
            float mTouchStartX;
            float mTouchStartY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // 获取相对屏幕的坐标，即以屏幕左上角为原点
                x = event.getRawX();
                y = event.getRawY() - top; // 25是系统状态栏的高度
                Log.i("startP", "startX" + mTouchStartX + "====startY"
                        + mTouchStartY);
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // 获取相对View的坐标，即以此View左上角为原点
                        mTouchStartX = event.getX();
                        mTouchStartY = event.getY();
                        Log.i("startP", "startX" + mTouchStartX + "====startY"
                                + mTouchStartY);
                        long end = System.currentTimeMillis() - startTime;
                        // 双击的间隔在 300ms以下
                        if (end < 300) {
                            closeDesk();
                        }
                        startTime = System.currentTimeMillis();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        // 更新浮动窗口位置参数
                        mLayout.x = (int) (x - mTouchStartX);
                        mLayout.y = (int) (y - mTouchStartY);
                        mWindowManager.updateViewLayout(v, mLayout);
                        break;
                    case MotionEvent.ACTION_UP:

                        // 更新浮动窗口位置参数
                        mLayout.x = (int) (x - mTouchStartX);
                        mLayout.y = (int) (y - mTouchStartY);
                        mWindowManager.updateViewLayout(v, mLayout);

                        // 可以在此记录最后一次的位置

                        mTouchStartX = mTouchStartY = 0;
                        break;
                }
                return true;
            }
        });
    }//设置WindowManager

    //下面全部是黄色小花的！
    public class DesktopLayout1 extends LinearLayout {
        public DesktopLayout1(Context context) {
            super(context);
            setOrientation(LinearLayout.VERTICAL);// 水平排列
            //设置宽高
            this.setLayoutParams( new LayoutParams(LayoutParams.WRAP_CONTENT,
                    LayoutParams.WRAP_CONTENT));
            View view = LayoutInflater.from(context).inflate(
                    R.layout.desklayout2, null);
            this.addView(view);
        }

    }
    private void createDesktopLayout1() {
        mDesktopLayout1 = new DesktopLayout1(this);
        mDesktopLayout1.setOnTouchListener(new View.OnTouchListener() {
            float mTouchStartX;
            float mTouchStartY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // 获取相对屏幕的坐标，即以屏幕左上角为原点
                x = event.getRawX();
                y = event.getRawY() - top; // 25是系统状态栏的高度
                Log.i("startP", "startX" + mTouchStartX + "====startY"
                        + mTouchStartY);
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // 获取相对View的坐标，即以此View左上角为原点
                        mTouchStartX = event.getX();
                        mTouchStartY = event.getY();
                        Log.i("startP", "startX" + mTouchStartX + "====startY"
                                + mTouchStartY);
                        long end = System.currentTimeMillis() - startTime;
                        // 双击的间隔在 300ms以下
                        if (end < 300) {
                            closeDesk1();
                        }
                        startTime = System.currentTimeMillis();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        // 更新浮动窗口位置参数
                        mLayout1.x = (int) (x - mTouchStartX);
                        mLayout1.y = (int) (y - mTouchStartY);
                        mWindowManager1.updateViewLayout(v, mLayout1);
                        break;
                    case MotionEvent.ACTION_UP:

                        // 更新浮动窗口位置参数
                        mLayout1.x = (int) (x - mTouchStartX);
                        mLayout1.y = (int) (y - mTouchStartY);
                        mWindowManager1.updateViewLayout(v, mLayout1);

                        // 可以在此记录最后一次的位置

                        mTouchStartX = mTouchStartY = 0;
                        break;
                }
                return true;
            }
        });
    }
    public void onWindowFocusChanged1(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        Rect rect = new Rect();
        // /取得整个视图部分,注意，如果你要设置标题样式，这个必须出现在标题样式之后，否则会出错
        getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
        top = rect.top;//状态栏的高度，所以rect.height,rect.width分别是系统的高度的宽度

        Log.i("top", "" + top);
    }

    private void showDesk1() {
        if (Flowers.first==false){
            mWindowManager1.addView(mDesktopLayout1, mLayout1);
            Flowers.first=true;
        }

    }  //显示DesktopLayout
    private void closeDesk1() {

        mWindowManager1.removeView(mDesktopLayout1);
        Flowers.first=false;

    }  //关闭desk
    private void createWindowManager1() {
        // 取得系统窗体
        mWindowManager1 = (WindowManager) getApplicationContext()
                .getSystemService(WINDOW_SERVICE);

        // 窗体的布局样式
        mLayout1= new WindowManager.LayoutParams();

        // 设置窗体显示类型——TYPE_SYSTEM_ALERT(系统提示)
        mLayout1.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;

        // 设置窗体焦点及触摸：
        // FLAG_NOT_FOCUSABLE(不能获得按键输入焦点)
        mLayout1.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;

        // 设置显示的模式
        mLayout1.format = PixelFormat.RGBA_8888;

        // 设置对齐的方法
        mLayout1.gravity = Gravity.TOP | Gravity.LEFT;

        // 设置窗体宽度和高度
        mLayout1.width = WindowManager.LayoutParams.WRAP_CONTENT;
        mLayout1.height = WindowManager.LayoutParams.WRAP_CONTENT;

    } //设置windowmanager





    // 定时器
    Runnable runnable = new Runnable() {

        @Override
        public void run() {
            // handler自带方法实现定时器
            try {
                long endTime = System.currentTimeMillis();
                int time = (int) ((endTime - mStartTime) / 1000);
                //mRlSoundLengthLayout.setVisibility(View.VISIBLE);
                mTvTime.setText(time + "" + '"');
                // 限制录音时间不长于两分钟 可更改录音限制
                if (time > 119) {
                    isStop = true;
                    mTime = time;
                    stopRecord();
                    Toast.makeText(Record_Page.this, "时间过长", Toast.LENGTH_SHORT).show();
                } else {
                    mHandler.postDelayed(this, 1000);
                    isStop = false;
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    };
}
