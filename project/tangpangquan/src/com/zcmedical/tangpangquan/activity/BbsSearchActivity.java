package com.zcmedical.tangpangquan.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.zcmedical.common.base.BaseActivity;
import com.zcmedical.common.base.TpqApplication;
import com.zcmedical.common.component.AutoLoadMoreListView;
import com.zcmedical.common.component.AutoLoadMoreListView.IAutoLoadMoreListViewListener;
import com.zcmedical.common.constant.APIKey;
import com.zcmedical.common.constant.CommonConstant;
import com.zcmedical.common.constant.InterfaceConstant;
import com.zcmedical.common.network.CommonRequest;
import com.zcmedical.common.utils.EntityUtils;
import com.zcmedical.common.utils.TypeUtil;
import com.zcmedical.tangpangquan.R;
import com.zcmedical.tangpangquan.adapter.SearchPostsAdapter;
import com.zcmedical.tangpangquan.entity.PostsEntity;

public class BbsSearchActivity extends BaseActivity implements OnClickListener {

    private int toBeContinued = 0;
    private boolean isLoadingMore = false;

    private List<PostsEntity> postsList;
    private SearchPostsAdapter postsAdapter;
    private AutoLoadMoreListView postsListView;

    private EditText searchEdt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_bbs_search);

        init();
    }

    private void init() {
        initToolbar();
        initUI();
    }

    private void initToolbar() {
        View searchView = LayoutInflater.from(getContext()).inflate(R.layout.view_bbs_search, null);
        searchEdt = findView(searchView, R.id.edt_bbs_search);

        Toolbar toolbar = findView(R.id.layout_toolbar);
        if (null != toolbar) {
            toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
            setSupportActionBar(toolbar);
        }

        ActionBar actionBar = getSupportActionBar();
        if (null != actionBar) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setHomeAsUpIndicator(getResources().getDrawable(R.drawable.selector_btn_back));
            actionBar.setDisplayShowCustomEnabled(true);
            actionBar.setCustomView(searchView);
        }
    }

    private void initUI() {
        if (null != searchEdt) {
            final ImageButton clearInputBtn = findView(R.id.imvbtn_clear_input);
            clearInputBtn.setOnClickListener(this);

            searchEdt.setOnEditorActionListener(new OnEditorActionListener() {

                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_SEND || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                        String keyword = searchEdt.getText().toString();

                        if (!TextUtils.isEmpty(keyword)) {
                            postsList = null;
                            showProgressDialog(getString(R.string.searching));
                            searchThread(0, keyword);
                        }
                        //                        return true;
                    }
                    return false;
                }

            });

            searchEdt.addTextChangedListener(new TextWatcher() {

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    String input = s.toString();
                    if (!TextUtils.isEmpty(input)) {
                        clearInputBtn.setVisibility(View.VISIBLE);
                    } else {
                        clearInputBtn.setVisibility(View.GONE);
                    }
                }
            });
        }

        postsListView = findView(R.id.almlv_search_posts);
        postsListView.setPullRefreshEnable(false);
        postsListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int clickIndex = position - ((ListView) parent).getHeaderViewsCount();
                PostsEntity post = postsAdapter.getItem(clickIndex);
                if (null != post) {
                    TpqApplication.getInstance(getContext()).setShowingPostsEntity(post);
                    Intent openPostsDetail = new Intent(getContext(), PostsDetailActivity.class);
                    startActivity(openPostsDetail);
                }
            }
        });
        postsListView.setXListViewListener(new IAutoLoadMoreListViewListener() {

            @Override
            public void onRefresh() {

            }

            @Override
            public void onLoadMore() {
                if (!isLoadingMore) {
                    isLoadingMore = true;

                    String keyword = searchEdt.getText().toString();
                    if (null != postsList && postsList.size() > 0) {
                        searchThread(postsList.size(), keyword);
                    } else {
                        searchThread(0, keyword);
                    }
                }

            }
        });
    }

    private void searchThread(int offset, String keyword) {
        List<String> searchFields = new ArrayList<String>();
        searchFields.add(APIKey.THREAD_TITLE);
        searchFields.add(APIKey.THREAD_CONTENT);

        CommonRequest searchThreadRequest = new CommonRequest();
        searchThreadRequest.setRequestApiName(InterfaceConstant.API_THREAD_SEARCH);
        searchThreadRequest.setRequestID(InterfaceConstant.REQUEST_ID_THREAD_SEARCH);
        searchThreadRequest.addRequestParam(APIKey.COMMON_KEYWORD, keyword);
        searchThreadRequest.addRequestParam(APIKey.COMMON_SEARCH_FIELDS, searchFields);
        searchThreadRequest.addRequestParam(APIKey.COMMON_OFFSET, offset);
        searchThreadRequest.addRequestParam(APIKey.COMMON_PAGE_SIZE, CommonConstant.MSG_PAGE_SIZE);

        addRequestAsyncTask(searchThreadRequest);

    }

    @Override
    protected void onResponseAsyncTaskRender(int status, String message, int toBeContinued, Map<String, Object> resultMap, String requestID, Map<String, Object> additionalArgsMap) {
        if (InterfaceConstant.REQUEST_ID_THREAD_SEARCH.equals(requestID)) {
            if (APIKey.STATUS_SUCCESSFUL == status) {
                if (null != resultMap) {
                    this.toBeContinued = toBeContinued;
                    List<Map<String, Object>> rawPostsList = TypeUtil.getList(resultMap.get(APIKey.THREADS));
                    if (null != rawPostsList && rawPostsList.size() > 0) {
                        List<PostsEntity> tmpPostsList = EntityUtils.getPostsEntityList(rawPostsList);
                        if (null != tmpPostsList) {
                            if (null == postsList) {
                                postsList = new ArrayList<PostsEntity>();
                                postsAdapter = null;
                            }
                            postsList.addAll(tmpPostsList);
                        }
                    }
                }
            } else {
                showToast(message);
            }

            showPostsList();
        }
    }

    private void showPostsList() {
        RelativeLayout noSearchResultLayout = findView(R.id.rl_no_search_result);

        if (null != postsList && postsList.size() > 0) {
            noSearchResultLayout.setVisibility(View.GONE);
            postsListView.setVisibility(View.VISIBLE);

            if (null == postsAdapter) {
                postsAdapter = new SearchPostsAdapter(getContext(), postsList);
                postsListView.setAdapter(postsAdapter);
            } else {
                postsAdapter.notifyDataSetChanged();
            }

        } else {
            noSearchResultLayout.setVisibility(View.VISIBLE);
            postsListView.setVisibility(View.GONE);

        }

        isLoadingMore = false;
        postsListView.stopLoadMore();
        setListCanLoadMore();
        
        dimissProgressDialog();
    }

    private void setListCanLoadMore() {
        switch (toBeContinued) {
        case 0:
            postsListView.setPullLoadEnable(false);
            break;
        case 1:
            postsListView.setPullLoadEnable(true);
            break;
        default:
            postsListView.setPullLoadEnable(false);
            break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_bbs_search, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
            finish();
            break;
        case R.id.menu_item_bbs_search:
            String keyword = searchEdt.getText().toString();

            if (!TextUtils.isEmpty(keyword)) {
                postsList = null;
                showProgressDialog(getString(R.string.searching));
                searchThread(0, keyword);
            }
            break;
        default:
            break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.imvbtn_clear_input:
            searchEdt.setText(null);
            break;

        default:
            break;
        }

    }

}
