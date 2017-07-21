package jimihua.work;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.List;

import jimihua.circle.CircularImage;
import jimihua.user.biz.MessageBiz;
import jimihua.user.biz.impl.MessageBizImpl;
import jimihua.user.po.Message;

public class login extends AppCompatActivity {

    private CircularImage login_headpicture; // 登录界面的头像
    private Button btn_login;
    private EditText et_password;
    private ActionBar actionBar;
    private StatusBarUtils sbu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        actionBar = getSupportActionBar();
        actionBar.hide();
        sbu.setWindowStatusBarColor(this,R.color.Login_color);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
//        }
        btn_login = (Button) findViewById(R.id.btn_login);
        et_password = (EditText) findViewById(R.id.et_password);
        login_headpicture= (CircularImage) findViewById(R.id.login_headpicture);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
                R.drawable.login);
        login_headpicture.setImageBitmap(bitmap);

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                MessageBiz messageBiz=new MessageBizImpl();
                List<Message> lstMessage = messageBiz.find(login.this);
                String str1 = lstMessage.get(0).getPassword();
                Intent i=new Intent();
                if (et_password.getText().toString().equals(str1)) {
                    i.setClass(login.this,HomePage.class);
                    startActivity(i);
                    finish();
                } else {
                    Toast.makeText(login.this, "输入密码有误", Toast.LENGTH_LONG)
                            .show();
                }
            }
        });
    }
}
