package cn.ucai.superwechat.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import cn.ucai.superwechat.R;
import cn.ucai.superwechat.SWApplication;
import cn.ucai.superwechat.activity.LoginActivity;
import cn.ucai.superwechat.I;
import cn.ucai.superwechat.utils.BitmapUtils;
import cn.ucai.superwechat.utils.ImageLoader;

/**
 * Created by Administrator on 2016/10/10.
 */

public class FragmentProfile extends Fragment {

    Context mContext;

    @ViewInject(R.id.iv_avatar)
    ImageView iv_avatar;
    @ViewInject(R.id.tv_username)
    TextView tv_username;
    @ViewInject(R.id.tv_nickname)
    TextView tv_nickname;

    ViewGroup parent;

    String userName;
    Bitmap avatarBitmap;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_profile, container, false);
        this.parent = (ViewGroup) layout;
        return layout;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ViewUtils.inject(this, getView());
        mContext = getActivity();
        userName = SWApplication.getUserName();
        initView();
    }

    private void initView() {
        String avatarPath = SWApplication.getAavatarPath();
        if (SWApplication.getAavatarPath() == null) {
            //加载头像
            avatarPath = ImageLoader.build()
                    .url(I.SERVER_URL)
                    .addParam(I.KEY_REQUEST, I.REQUEST_DOWNLOAD_AVATAR)
                    .addParam(I.NAME_OR_HXID, userName)
                    .addParam(I.AVATAR_TYPE, "user_avatar")
                    .saveFileName(userName + ".jpg")
                    .width(64)
                    .height(64)
                    .defaultPicture(R.drawable.default_face)
                    .imageView(iv_avatar)
                    .listener(parent)
                    .showImage(mContext);
            //保存头像的本地路径
            Log.i("main",avatarPath);
            SWApplication.saveAvatarPath(avatarPath);
        } else {
            Log.i("main", "avatarPath is not null");
            avatarBitmap = BitmapUtils.getBitmap(avatarPath);
            iv_avatar.setImageBitmap(avatarBitmap);
        }
        //设置名字和昵称
        tv_username.setText(SWApplication.getUserName());
        tv_nickname.setText(SWApplication.getNickName());

    }

    @OnClick(R.id.re_setting)
    private void logout(View v) {
        SWApplication.setLogined(false);
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivity(intent);
        getActivity().finish();
    }

    @Override
    public void onDestroy() {
        if (avatarBitmap != null) {
            avatarBitmap.recycle();
        }
        super.onDestroy();
    }
}
