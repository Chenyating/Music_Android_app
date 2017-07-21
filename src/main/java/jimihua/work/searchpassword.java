package jimihua.work;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.List;

import jimihua.user.biz.MessageBiz;
import jimihua.user.biz.impl.MessageBizImpl;
import jimihua.user.po.Message;

public class searchpassword extends AppCompatActivity {


    private Button btn_search_submit; // 提交按钮
    private EditText et_search_repsw1;// 密码
    private EditText et_search_repsw2;// 重复密码
    private EditText et_search_answer;// 问题答案
    private String info;
    private Spinner spin;
    private StatusBarUtils sbu;
    private ActionBar actionBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_searchpassword);

        actionBar = getSupportActionBar();
        actionBar.hide();
        sbu.setWindowStatusBarColor(this,R.color.white);
        btn_search_submit = (Button) findViewById(R.id.btn_search_submit);
        et_search_answer = (EditText) findViewById(R.id.et_serach_answer);
        et_search_repsw1 = (EditText) findViewById(R.id.et_search_repsw1);
        et_search_repsw2 = (EditText) findViewById(R.id.et_search_repsw2);
        info = "我的姓名是？";
        spin = (Spinner) findViewById(R.id.sp_search_problem);
        spin.setOnItemSelectedListener(new ProvOnItemSelectedListener());
        btn_search_submit.setOnClickListener(new ViewOCL());
    }


    private class ViewOCL implements View.OnClickListener {
        MessageBiz messageBiz = new MessageBizImpl();

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_search_submit:
                    //  从数据库中读取问题和答案，判断条件，修改其中的密码,并存放到application中
                    List<Message> lstMessage = messageBiz.find(searchpassword.this);
                    Message user = lstMessage.get(0);
                    String str1 = user.getProblem();
                    String str2 = user.getAnswer();
                    String password1 = et_search_repsw1.getText().toString();
                    String password2 = et_search_repsw2.getText().toString();
                    String answer = et_search_answer.getText().toString();

                    if (!str1.equals(info)) {
                        Toast.makeText(searchpassword.this,
                                "您选择的问题与您注册时设定的问题不一致", Toast.LENGTH_SHORT).show();
                    }
                    if (!str2.equals(answer)) {
                        Toast.makeText(searchpassword.this, "您填写的答案不正确",
                                Toast.LENGTH_SHORT).show();

                    }
                    if (password1.length() < 6) {
                        Toast.makeText(searchpassword.this, "密码必须大于六位",
                                Toast.LENGTH_SHORT).show();
                    }
                    if (!password1.equals(password2)) {
                        Toast.makeText(searchpassword.this, "两次输入的密码不一致",
                                Toast.LENGTH_SHORT).show();
                    }
                    if (str1.equals(info) && str2.equals(answer)
                            && password1.length() >= 6
                            && password1.equals(password2)) {
                        Message message = new Message();
                        message.setPassword(password1);
                        messageBiz.alter(searchpassword.this, message);
                        Toast.makeText(searchpassword.this, "恭喜您，操作成功",
                                Toast.LENGTH_SHORT).show();
                        finish();

                    }
                    break;

                default:
                    break;
            }
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_searchpassword, menu);
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

    private class ProvOnItemSelectedListener implements AdapterView.OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> adapter, View view,
                                   int position, long id) {
            info = adapter.getItemAtPosition(position).toString();
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
            // TODO Auto-generated method stub

        }

    }
}
