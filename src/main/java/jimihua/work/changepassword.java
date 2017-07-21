package jimihua.work;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.List;

import jimihua.user.biz.MessageBiz;
import jimihua.user.biz.impl.MessageBizImpl;
import jimihua.user.po.Message;

public class changepassword extends AppCompatActivity {

    private Button btn_changetologin;
    private EditText et_oldpassword, et_newpassword_one, et_newpassword_two;
    private StatusBarUtils sbu;
    private ActionBar actionBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_changepassword);
        actionBar = getSupportActionBar();
        actionBar.hide();
        sbu.setWindowStatusBarColor(this,R.color.white);
        btn_changetologin = (Button) findViewById(R.id.btn_changetologin);
        et_oldpassword = (EditText) findViewById(R.id.et_oldpassword);
        et_newpassword_one = (EditText) findViewById(R.id.et_newpassword_one);
        et_newpassword_two = (EditText) findViewById(R.id.et_newpassword_two);
        btn_changetologin.setOnClickListener(new ViewOCL());

    }

    private class ViewOCL implements View.OnClickListener {


        @Override
        public void onClick(View v) {
            MessageBiz messageBiz=new MessageBizImpl();

            switch (v.getId()) {
                case R.id.btn_changetologin:
                    // 判断数据库的密码是否相等
                    List<Message> lstMessage = messageBiz.find(changepassword.this);
                    Message user = lstMessage.get(0);
                    String str1 = user.getPassword();
                    String strold = et_oldpassword.getText().toString(); // 老密码
                    String strnew_one = et_newpassword_one.getText().toString();
                    String strnew_two = et_newpassword_two.getText().toString();
                    if (!str1.equals(strold)) {
					Toast.makeText(getApplicationContext(), "旧密码输入错误",
                            Toast.LENGTH_SHORT).show();
                    }
                    if (!strnew_one.equals(strnew_two)) {
					Toast.makeText(getApplicationContext(), "两次密码不一致，请确认",
							Toast.LENGTH_SHORT).show();
                    }
                    if (strnew_one.length() < 6) {
					Toast.makeText(getApplicationContext(), "新密码长度必须大于等于6位",
							Toast.LENGTH_SHORT).show();
                    }
                    if (strnew_one.equals(strnew_two) && strnew_one.length() > 5
                            && str1.equals(strold)) {
                        //修改密码
                        Message message = new Message();
                        message.setPassword(strnew_one);
                        messageBiz.alter(changepassword.this, message);
				         Toast.makeText(getApplicationContext(), "恭喜你,修改成功",
							Toast.LENGTH_SHORT).show();

                       finish();
                    }

                    break;

                default:
                    break;
            }

        }
    }


}
