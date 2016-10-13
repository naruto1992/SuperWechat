package cn.ucai.superwechat.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import cn.ucai.superwechat.R;
import cn.ucai.superwechat.SWApplication;
import cn.ucai.superwechat.utils.ToastUtil;

public class LoginActivity extends BaseActivity {

    Context mContext;

    @ViewInject(R.id.et_usertel)
    EditText et_usertel;
    @ViewInject(R.id.et_password)
    EditText et_password;
    @ViewInject(R.id.btn_login)
    Button btn_login;

    String[] more_menu = new String[]{"切换帐号", "注册", "安全中心(无用)"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ViewUtils.inject(LoginActivity.this);
        mContext = this;
        initView();
    }

    private void initView() {
        actionBar.setTitle("登录超级微信");
        et_password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                //手机号和密码均不能为空
                if (et_usertel.getEditableText().length() > 0 && editable.length() > 0) {
                    btn_login.setEnabled(true);
                } else {
                    btn_login.setEnabled(false);
                }
            }
        });
    }

    @OnClick(R.id.btn_login)
    private void login(View v) {
        SWApplication.setLogined(true);
        final String userTel = et_usertel.getEditableText().toString().trim();
        ToastUtil.show(mContext, userTel + "登录成功");
        Intent intent = new Intent(mContext, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @OnClick(R.id.login_more)
    private void login_more(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setItems(more_menu, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        ToastUtil.show(mContext, "切换帐号");
                        break;
                    case 1:
                        ToastUtil.show(mContext, "注册");
                        break;
                    case 2:
                        ToastUtil.show(mContext, "安全中心");
                        break;
                }
            }
        });
        builder.create().show();
    }
}
