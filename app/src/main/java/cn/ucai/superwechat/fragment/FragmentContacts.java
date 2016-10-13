package cn.ucai.superwechat.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.Map;

import cn.ucai.superwechat.R;
import cn.ucai.superwechat.bean.Result;
import cn.ucai.superwechat.bean.UserAvatar;
import cn.ucai.superwechat.I;
import cn.ucai.superwechat.utils.ImageLoader;
import cn.ucai.superwechat.utils.OkHttpUtils;
import cn.ucai.superwechat.utils.ToastUtil;

/**
 * Created by Administrator on 2016/10/10.
 */

public class FragmentContacts extends Fragment implements ContactsClickListener {

    Context mContext;
    @ViewInject(R.id.mSrl)
    SwipeRefreshLayout mSrl;
    @ViewInject(R.id.mRvContact)
    RecyclerView mRvContact;

    static final String USER_NAME = "zhangsan";
    static final int ACTION_DOWNLOAD = 0;
    static final int ACTION_PULL_DOWN = 1;
    static final int ACTION_PULL_UP = 2;

    int mPageId = 1;//下载的页号
    static final int PAGE_SIZE = 6;

    ContactAdapter mAdapter;
    ArrayList<UserAvatar> mContactList;
    LinearLayoutManager mLayoutManager;

    int mScrollState; //当前滑动的状态

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_contacts, container, false);
        return layout;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ViewUtils.inject(this, getView());
        mContext = getActivity();
        init();
        downloadContactList(ACTION_DOWNLOAD, mPageId);
        setListener();
    }

    private void init() {
        mContactList = new ArrayList<UserAvatar>();
        mAdapter = new ContactAdapter(mContext, mContactList);
        mAdapter.setClickListener(this);
        mRvContact.setAdapter(mAdapter);
        mLayoutManager = new LinearLayoutManager(mContext);
        mRvContact.setLayoutManager(mLayoutManager);
    }

    private void setListener() {
        //上拉加载监听
        mRvContact.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                mScrollState = newState;
                //获取最后一个列表项的索引
                int lastPostion = mLayoutManager.findLastVisibleItemPosition();
                if (lastPostion == mAdapter.getItemCount() - 1 && newState == RecyclerView.SCROLL_STATE_IDLE
                        && mAdapter.isMore()) {
                    mPageId++;//设置准备加载下一页数据
                    downloadContactList(ACTION_PULL_UP, mPageId);
                }
            }
        });
        //下拉刷新监听
        mSrl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPageId = 1;//设置下载第一页数据
                downloadContactList(ACTION_PULL_DOWN, mPageId);
                mSrl.setEnabled(true);
                mSrl.setRefreshing(true);
            }
        });
    }

    private void downloadContactList(final int action, int pageId) {
        final OkHttpUtils<Result> utils = new OkHttpUtils<>();
        utils.url(I.SERVER_URL)
                .addParam(I.KEY_REQUEST, I.REQUEST_DOWNLOAD_CONTACT_PAGE_LIST)
                .addParam(I.Contact.USER_NAME, USER_NAME)
                .addParam(I.PAGE_ID, "" + pageId)
                .addParam(I.PAGE_SIZE, PAGE_SIZE + "")
                .targetClass(Result.class)
                .execute(new OkHttpUtils.OnCompleteListener<Result>() {
                    @Override
                    public void onSuccess(Result result) {
                        if (result.getRetCode() == 0) {
                            String json = result.getRetData().toString();
                            Gson gson = new Gson();
                            Map map = gson.fromJson(json, Map.class);
                            json = map.get("pageData").toString();
                            UserAvatar[] users = gson.fromJson(json, UserAvatar[].class);
                            mAdapter.setMore(users != null && users.length > 0);
                            if (!mAdapter.isMore()) {
                                if (action == ACTION_PULL_UP) {
                                    mAdapter.setFooter("没有更多数据");
                                }
                                return;
                            }
                            ArrayList<UserAvatar> userList = utils.array2List(users);
                            mAdapter.setFooter("加载更多数据");
                            switch (action) {
                                case ACTION_DOWNLOAD:
                                    mAdapter.initContactList(userList);
                                    break;
                                case ACTION_PULL_DOWN:
                                    mAdapter.initContactList(userList);
                                    mSrl.setRefreshing(false);
                                    break;
                                case ACTION_PULL_UP:
                                    mAdapter.addContactList(userList);
                                    break;
                            }
                        }
                    }

                    @Override
                    public void onError(String error) {
                        mSrl.setRefreshing(false);
                        ToastUtil.show(mContext, error);
                    }
                });
    }

    /////////////////////////////////////我是纯洁的分割线////////////////////////////////////////
    class ContactAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener, View.OnLongClickListener {

        final static int TYPE_ITEM = 0;//代表联系人item类型的布局
        final static int TYPE_FOOTER = 1;//代表页脚item类型的布局

        Context context;
        ArrayList<UserAvatar> contactList;
        String footerText;
        RecyclerView parent;
        boolean isMore;//是否有更多的数据可加载
        ContactsClickListener mClickListener;

        public boolean isMore() {
            return isMore;
        }

        public void setMore(boolean more) {
            isMore = more;
        }

        public ArrayList<UserAvatar> getContactList() {
            return contactList;
        }

        public ContactAdapter(Context context, ArrayList<UserAvatar> contactList) {
            this.context = context;
            this.contactList = contactList;
        }

        public void setClickListener(ContactsClickListener clickListener) {
            this.mClickListener = clickListener;
        }

        //刷新
        public void initContactList(ArrayList<UserAvatar> contactList) {
            this.contactList.clear();
            this.contactList.addAll(contactList);
            notifyDataSetChanged();
        }

        //加载更多
        public void addContactList(ArrayList<UserAvatar> contactList) {
            this.contactList.addAll(contactList);
            notifyDataSetChanged();
        }

        public void setFooter(String footerText) {
            this.footerText = footerText;
            notifyDataSetChanged();
        }

        public void deleteContact(int position) {
            contactList.remove(position);
            notifyDataSetChanged();
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            this.parent = (RecyclerView) parent;
            RecyclerView.ViewHolder holder = null;
            LayoutInflater inflater = LayoutInflater.from(context);
            View layout = null;
            switch (viewType) {
                case TYPE_FOOTER:
                    layout = inflater.inflate(R.layout.item_footer, parent, false);
                    holder = new FooterViewHolder(layout);
                    break;
                case TYPE_ITEM:
                    layout = inflater.inflate(R.layout.item_contact, parent, false);
                    //设置事件监听
                    layout.setOnClickListener(this);
                    layout.setOnLongClickListener(this);
                    holder = new ContactViewHolder(layout);
                    break;
            }
            return holder;
        }

        @Override
        public void onClick(View view) {
            int position = (int) view.getTag();
            if (mClickListener != null) {
                mClickListener.onItemClick(position);
            }
        }

        @Override
        public boolean onLongClick(View view) {
            int position = (int) view.getTag();
            if (mClickListener != null) {
                mClickListener.onItemLongClick(position);
            }
            return false;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (position == getItemCount() - 1) {
                FooterViewHolder footerViewHolder = (FooterViewHolder) holder;
                footerViewHolder.tvFooter.setText(footerText);
                return;
            }
            ContactViewHolder contactViewHolder = (ContactViewHolder) holder;
            UserAvatar user = contactList.get(position);
            contactViewHolder.tvUserName.setText(user.getMUserName());
            contactViewHolder.tvNick.setText(user.getMUserNick());
            //加载头像
            ImageLoader.build()
                    .url(I.SERVER_URL)
                    .addParam(I.KEY_REQUEST, I.REQUEST_DOWNLOAD_AVATAR)
                    .addParam(I.NAME_OR_HXID, user.getMUserName())
                    .addParam(I.AVATAR_TYPE, "user_avatar")
                    //.saveFileName(user.getMUserNick()+".jpg")
                    .width(80)
                    .height(80)
                    .defaultPicture(R.drawable.default_face)
                    .imageView(contactViewHolder.ivAvatar)
                    .listener(parent)
                    .setDragging(mScrollState != RecyclerView.SCROLL_STATE_DRAGGING)
                    .showImage(context);
            contactViewHolder.itemView.setTag(position);
        }

        @Override
        public int getItemCount() {
            return contactList == null ? 0 : contactList.size() + 1;
        }

        @Override
        public int getItemViewType(int position) {
            if (position == getItemCount() - 1) {
                return TYPE_FOOTER;
            } else {
                return TYPE_ITEM;
            }
        }
    }

    class ContactViewHolder extends RecyclerView.ViewHolder {
        ImageView ivAvatar;
        TextView tvUserName, tvNick;

        public ContactViewHolder(View itemView) {
            super(itemView);
            ivAvatar = (ImageView) itemView.findViewById(R.id.ivAvatar);
            tvUserName = (TextView) itemView.findViewById(R.id.tvUserName);
            tvNick = (TextView) itemView.findViewById(R.id.tvNick);
        }
    }

    class FooterViewHolder extends RecyclerView.ViewHolder {
        TextView tvFooter;

        public FooterViewHolder(View itemView) {
            super(itemView);
            tvFooter = (TextView) itemView.findViewById(R.id.tvFooter);
        }
    }

    @Override
    public void onItemClick(int position) {
    }

    @Override
    public void onItemLongClick(final int position) {
        UserAvatar user = mAdapter.getContactList().get(position);
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("删除好友");
        builder.setMessage("你确定要删除好友" + " \"" + user.getMUserName() + "\" " + "吗？");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mAdapter.deleteContact(position);
            }
        });
        builder.setNegativeButton("取消", null);
        builder.create().show();
    }
}
