package cn.ucai.superwechat.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import cn.ucai.superwechat.R;
import cn.ucai.superwechat.SWApplication;
import cn.ucai.superwechat.bean.Result;
import cn.ucai.superwechat.bean.UserAvatar;
import cn.ucai.superwechat.I;
import cn.ucai.superwechat.utils.BitmapUtils;
import cn.ucai.superwechat.utils.OkHttpUtils;
import cn.ucai.superwechat.utils.ToastUtil;
import cn.ucai.superwechat.view.SimpleListDialog;

public class LoginActivity extends BaseActivity {

    Context mContext;

    @ViewInject(R.id.default_avatar)
    ImageView default_avatar;
    @ViewInject(R.id.et_usertel)
    EditText et_usertel;
    @ViewInject(R.id.et_password)
    EditText et_password;
    @ViewInject(R.id.btn_login)
    Button btn_login;

    Bitmap avatarBitmap;
    String[] more_menu = new String[]{"切换帐号", "注册", "安全中心"};

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
        initAvatar();//设置头像
        if (SWApplication.getUserName() != null) {
            et_usertel.setText(SWApplication.getUserName());
            et_usertel.setEnabled(false);
            et_password.requestFocus();
        }
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

    private void initAvatar() {
        String avatarPath = SWApplication.getAavatarPath();
        if (avatarPath == null) {
            default_avatar.setImageResource(R.drawable.default_face);
        } else {
            avatarBitmap = BitmapUtils.getBitmap(avatarPath);
            default_avatar.setImageBitmap(avatarBitmap);
        }
    }

    @OnClick(R.id.btn_login)
    private void btn_login(View v) {
        login();
    }

    private void login() {
        String userName = et_usertel.getText().toString().trim();
        String password = et_password.getText().toString();
        final OkHttpUtils<Result> utils = new OkHttpUtils<>();
        utils.url(I.SERVER_URL)
                .addParam(I.KEY_REQUEST, I.REQUEST_LOGIN)
                .post()
                .addParam(I.User.USER_NAME, userName)
                .addParam(I.User.PASSWORD, password)
                .targetClass(Result.class)
                .execute(new OkHttpUtils.OnCompleteListener<Result>() {
                    @Override
                    public void onSuccess(Result result) {
                        if (result != null) {
                            UserAvatar user = utils.parseJson(result.getRetData().toString(), UserAvatar.class);
                            if (user == null) {
                                ToastUtil.show(mContext, "登录失败");
                            } else {
                                SWApplication.saveUserData(user);
                                SWApplication.setLogined(true);
                                Intent intent = new Intent(mContext, MainActivity.class);
                                startActivity(intent);
                                finish();
                                Log.i("main", user.toString());
                            }
                        }
                    }

                    @Override
                    public void onError(final String error) {
                        ToastUtil.show(mContext, error);
                    }
                });
    }

    @OnClick(R.id.login_more)
    private void login_more(View v) {
        SimpleListDialog.Builder builder = new SimpleListDialog.Builder(this);
        builder.setItems(more_menu, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        initDefault(); //切换帐号
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

    private void initDefault() {
        SWApplication.saveAvatarPath(null);
        initAvatar();
        et_usertel.setText("");
        et_usertel.setEnabled(true);
        et_usertel.requestFocus();
    }

    @Override
    protected void onDestroy() {
        if (avatarBitmap != null) {
            avatarBitmap.recycle();
            Log.i("main", "avatarBitmap is recycled");
        }
        super.onDestroy();
    }
}
