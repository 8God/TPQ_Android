package com.zcmedical.tangpangquan.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.zcmedical.common.base.BaseActivity;
import com.zcmedical.common.base.TpqApplication;
import com.zcmedical.common.constant.APIKey;
import com.zcmedical.common.constant.InterfaceConstant;
import com.zcmedical.common.network.CommonRequest;
import com.zcmedical.common.utils.DialogUtils;
import com.zcmedical.tangpangquan.R;
import com.zcmedical.tangpangquan.adapter.MyPostsCollectionAdapter;
import com.zcmedical.tangpangquan.entity.PostCollectionEntity;
import com.zcmedical.tangpangquan.entity.PostsEntity;
import com.zcmedical.tangpangquan.fragment.MyPostsCollectionFragment;

public class MyPostsCollectionActivity extends BaseActivity {

    private MyPostsCollectionFragment myPostsCollectionFragment;
    private MenuItem menuItem;
    private MyPostsCollectionAdapter myPostsCollectionAdapter;

    private List<String> deletePostCollectionIdList; //删除帖子收藏记录对应的帖子ID列表

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_my_posts_collection);

        init();
    }

    private void init() {
        initToolbar("我的收藏");
        initUI();
    }

    private void initUI() {
        myPostsCollectionFragment = new MyPostsCollectionFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, myPostsCollectionFragment).commit();

        initDeleteTabbar();
    }

    private void initDeleteTabbar() {
        findView(R.id.rl_delete_bottom_tabbar).setVisibility(View.GONE);

        CheckBox selectAllCb = findView(R.id.cb_select_all);
        selectAllCb.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                List<PostCollectionEntity> postCollectionEntities = myPostsCollectionFragment.getPostCollectionEntityList();
                if (null != postCollectionEntities && postCollectionEntities.size() > 0) {
                    for (PostCollectionEntity entity : postCollectionEntities) {
                        entity.setSelectToDelete(isChecked);
                    }
                    if (null == myPostsCollectionAdapter) {
                        myPostsCollectionAdapter = myPostsCollectionFragment.getMyPostsCollectionAdapter();
                    }
                    myPostsCollectionAdapter.notifyDataSetChanged();
                }
            }
        });

        TextView deleteCollectionTv = findView(R.id.tv_delete_collection);
        deleteCollectionTv.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                final List<String> deleteCollectionIdList = getDeleteCollectionIdList();
                if (null != deleteCollectionIdList && deleteCollectionIdList.size() > 0) {
                    DialogUtils.showAlertDialog(getContext(), "确认删除所选的收藏，删除后将不可恢复", getString(R.string.yes), new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            deletePostsCollection(deleteCollectionIdList);
                            dialog.dismiss();
                        }
                    }, getString(R.string.no), null);

                } else {
                    DialogUtils.showAlertDialog(getContext(), "请选择收藏记录");
                }

            }

        });
    }

    private List<String> getDeleteCollectionIdList() {
        List<String> deleteCollectionIdList = new ArrayList<String>();
        deletePostCollectionIdList = new ArrayList<String>();

        List<PostCollectionEntity> myPostCollectionEntities = myPostsCollectionFragment.getPostCollectionEntityList();
        if (null != myPostCollectionEntities && myPostCollectionEntities.size() > 0) {
            for (PostCollectionEntity entity : myPostCollectionEntities) {
                if (entity.isSelectToDelete()) {
                    deleteCollectionIdList.add(entity.getId());

                    PostsEntity post = entity.getPost();
                    if (null != post) {
                        deletePostCollectionIdList.add(post.getId());
                    }
                }
            }
        }

        return deleteCollectionIdList;
    }

    private void deletePostsCollection(List<String> deleteCollectionIdList) {
        String userId = TpqApplication.getInstance().getUserId();
        CommonRequest deletePostCollection = new CommonRequest();
        deletePostCollection.setRequestApiName(InterfaceConstant.API_THREAD_COLLECTITON_REMOVE);
        deletePostCollection.setRequestID(InterfaceConstant.REQUEST_ID_THREAD_COLLECTITON_REMOVE);
        deletePostCollection.addRequestParam(APIKey.USER_ID, userId);
        deletePostCollection.addRequestParam(APIKey.COMMON_ID, deleteCollectionIdList);

        addRequestAsyncTask(deletePostCollection);

        showProgressDialog("删除中...");
    }

    @Override
    protected void onResponseAsyncTaskRender(int status, String message, int toBeContinued, Map<String, Object> resultMap, String requestID, Map<String, Object> additionalArgsMap) {
        super.onResponseAsyncTaskRender(status, message, toBeContinued, resultMap, requestID, additionalArgsMap);

        if (InterfaceConstant.REQUEST_ID_THREAD_COLLECTITON_REMOVE.equals(requestID)) {
            if (status == APIKey.STATUS_SUCCESSFUL) {
                showToast("删除成功");

                cancelDeleteCollection();

                if (null != deletePostCollectionIdList && deletePostCollectionIdList.size() > 0) {
                    TpqApplication tpqApplication = TpqApplication.getInstance();
                    for (String postId : deletePostCollectionIdList) {
                        tpqApplication.setPostsCollected(postId, false);
                    }
                }
                myPostsCollectionAdapter = null;
                myPostsCollectionFragment.reloadList();
            } else {
                showToast(message);
            }

            dimissProgressDialog();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_delete_post_collection, menu);
        menuItem = menu.getItem(0);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.menu_item_delete_post_collection:
            if (null == myPostsCollectionAdapter) {
                myPostsCollectionAdapter = myPostsCollectionFragment.getMyPostsCollectionAdapter();
            }
            if (myPostsCollectionAdapter.isDeleteMode()) {
                cancelDeleteCollection();
            } else {
                deleteCollection();
            }
            break;

        default:
            break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void deleteCollection() {
        menuItem.setTitle(getString(R.string.menu_item_title_cancel_delete_collection));

        if (null == myPostsCollectionAdapter) {
            myPostsCollectionAdapter = myPostsCollectionFragment.getMyPostsCollectionAdapter();
        }
        if (null != myPostsCollectionAdapter) {
            myPostsCollectionAdapter.setDeleteMode(true);
            myPostsCollectionAdapter.notifyDataSetChanged();
        }

        findView(R.id.rl_delete_bottom_tabbar).setVisibility(View.VISIBLE);
    }

    private void cancelDeleteCollection() {
        menuItem.setTitle(getString(R.string.menu_item_title_delete_collection));

        if (null == myPostsCollectionAdapter) {
            myPostsCollectionAdapter = myPostsCollectionFragment.getMyPostsCollectionAdapter();
        }
        if (null != myPostsCollectionAdapter) {
            myPostsCollectionAdapter.setDeleteMode(false);
            myPostsCollectionAdapter.notifyDataSetChanged();
        }

        findView(R.id.rl_delete_bottom_tabbar).setVisibility(View.GONE);
    }

}
