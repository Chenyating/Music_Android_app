package jimihua.work;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import java.util.List;

import jimihua.user.biz.MessageBiz;
import jimihua.user.biz.impl.MessageBizImpl;
import jimihua.user.po.Message;

/**
 * Created by Administrator on 2017/1/12.
 */
public class tiaozhuan extends Activity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tiaozhuan);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

             /*   Intent intent = new Intent(tiaozhuan.this,HomePage.class);
                startActivity(intent);
                tiaozhuan.this.finish();

                         */

                MessageBiz messageBiz = new MessageBizImpl();
                List<Message> lstMessage = messageBiz.find(tiaozhuan.this);
                Intent in = new Intent(Intent.ACTION_MAIN);
                in.addCategory(Intent.CATEGORY_HOME);
                int str = lstMessage.size();
                if (str == 0) {
                    in.setClass(tiaozhuan.this, register.class);
                    startActivity(in);
                    finish();
                } else {
                    in.setClass(tiaozhuan.this, login.class);
                    startActivity(in);
                    finish();
                }


            }
        }, 3000); //跳转时间
    }
}

