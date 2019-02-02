package com.zcmedical.tangpangquan.activity;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zcmedical.common.base.BaseActivity;
import com.zcmedical.common.base.TpqApplication;
import com.zcmedical.common.component.AutoLoadMoreListView;
import com.zcmedical.common.component.AutoLoadMoreListView.IAutoLoadMoreListViewListener;
import com.zcmedical.common.component.BasicDialog;
import com.zcmedical.common.constant.APIKey;
import com.zcmedical.common.constant.CommonConstant;
import com.zcmedical.common.constant.InterfaceConstant;
import com.zcmedical.common.network.CommonRequest;
import com.zcmedical.common.utils.CounterUtils;
import com.zcmedical.common.utils.DensityUtil;
import com.zcmedical.common.utils.DialogUtils;
import com.zcmedical.common.utils.EntityUtils;
import com.zcmedical.common.utils.OpenFileUtil;
import com.zcmedical.common.utils.TypeUtil;
import com.zcmedical.tangpangquan.R;
import com.zcmedical.tangpangquan.adapter.CommentsAdapter;
import com.zcmedical.tangpangquan.entity.CommentEntity;
import com.zcmedical.tangpangquan.entity.PicEntity;
import com.zcmedical.tangpangquan.entity.PostsEntity;
import com.zcmedical.tangpangquan.entity.UserEntity;
import com.zcmedical.tangpangquan.view.BackToTopBtn;
import com.zcmedical.tangpangquan.view.DealCommentPager;
import com.zcmedical.tangpangquan.view.DealCommentPager.OnItemSelectedListener;

public class PostsDetailActivity extends BaseActivity implements OnClickListener {

    public static int UPDATE_POST_LIST = 11;

    private boolean isPostsLikes = false;
    private boolean isPostsCollected = false;

    private boolean isLoadMore = false;
    private boolean isRefresh = false;
    private int toBeContinued = 0;
    private PostsEntity postsEntity;
    private CommentsAdapter commentsAdapter;
    private List<CommentEntity> commentsList;

    private AutoLoadMoreListView commentListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posts_detail);

        init();
    }

    private void init() {
        initData();
        initToolbar();
        if (null != postsEntity) {
            initUI();

            fetchCommentList(0);
        }
    }

    private void initData() {
        postsEntity = TpqApplication.getInstance(getContext()).getShowingPostsEntity();
        if (null != postsEntity) {
            isPostsLikes = TpqApplication.getInstance(getContext()).isPostsLikes(postsEntity.getId());
            isPostsCollected = TpqApplication.getInstance(getContext()).isPostsCollected(postsEntity.getId());
        }
    }

    private void initToolbar() {
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
            actionBar.setTitle("话题");
        }
    }

    private void initUI() {
        commentListView = findView(R.id.almlv_comments);
        commentListView.setPullRefreshEnable(true);
        commentListView.setPullLoadEnable(false);
        commentListView.setXListViewListener(new ListViewUpdateListener());
        commentListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int clickIndex = position - ((ListView) parent).getHeaderViewsCount();
                if (null != commentsAdapter) {
                    CommentEntity comment = commentsAdapter.getItem(clickIndex);

                    DealCommentPager dealCommentPager = new DealCommentPager(getContext(), comment);
                    final BasicDialog dialog = DialogUtils.showContentDialog(getContext(), getString(R.string.deal_comment_dialog_title), dealCommentPager);
                    dealCommentPager.setOnItemSelectedListener(new DealCommentEventListener(dialog));
                }

            }
        });

        final BackToTopBtn backToTopButton = findView(R.id.btn_back_to_top);
        backToTopButton.bindListView(commentListView);
        commentListView.setOnScrollListener(new OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                backToTopButton.onVisibilityChanged(firstVisibleItem, visibleItemCount, totalItemCount);
            }
        });

        View headview = initHeadView();
        commentListView.addHeaderView(headview);

        commentsList = new ArrayList<CommentEntity>();
        commentsAdapter = new CommentsAdapter(getContext(), commentsList);
        commentListView.setAdapter(commentsAdapter);

        RelativeLayout postsLikesRl = findView(R.id.rl_posts_likes);
        RelativeLayout postsCollectRl = findView(R.id.rl_posts_collect);
        RelativeLayout postsCommentRl = findView(R.id.rl_posts_comment);
        postsLikesRl.setOnClickListener(this);
        postsCollectRl.setOnClickListener(this);
        postsCommentRl.setOnClickListener(this);
        initTabbarButton();
    }

    private void initTabbarButton() {

        ImageView postsLikesImv = findView(R.id.imv_posts_likes_icon);
        ImageView postsCollectImv = findView(R.id.imv_posts_collect_icon);

        TextView likesTv = findView(R.id.tv_likes);
        TextView collectTv = findView(R.id.tv_collect);

        if (isPostsLikes) {
            postsLikesImv.setEnabled(false);
            likesTv.setTextColor(getResources().getColor(R.color.colorPrimary));
        } else {
            postsLikesImv.setEnabled(true);
            likesTv.setTextColor(getResources().getColor(R.color.middle_gray));
        }

        if (isPostsCollected) {
            postsCollectImv.setEnabled(false);
            collectTv.setTextColor(getResources().getColor(R.color.colorPrimary));
        } else {
            postsCollectImv.setEnabled(true);
            collectTv.setTextColor(getResources().getColor(R.color.middle_gray));
        }
    }

    private View initHeadView() {
        View headView = LayoutInflater.from(getContext()).inflate(R.layout.headview_posts, null);
        //        layout = View.inflate(getContext(), R.layout.headview_posts, null);

        TextView reportTv = findView(headView, R.id.tv_report);
        reportTv.setOnClickListener(this);

        if (null != postsEntity) {
            String createdAt = postsEntity.getCreatedAt();
            if (null != createdAt) {
                Date createdDate = null;
                try {
                    createdDate = CommonConstant.serverTimeFormat.parse(createdAt);
                } catch (ParseException e) {
                    e.printStackTrace();
                    createdDate = new Date();
                }
                createdAt = CommonConstant.refreshTimeFormat.format(createdDate);
            }
            setTextView(headView, R.id.tv_posts_title, postsEntity.getTitle());
            setTextView(headView, R.id.tv_posts_content, postsEntity.getContent());
            setTextView(headView, R.id.tv_posts_likes_count, CounterUtils.format(postsEntity.getLikesCount()));
            setTextView(headView, R.id.tv_posts_date, createdAt);

            final UserEntity user = postsEntity.getUser();
            if (null != user) {
                setTextView(headView, R.id.tv_user_name, user.getNickname());
                setImageView(headView, R.id.cimv_user_head, user.getHeadPic(), R.drawable.common_icon_default_user_head);

                findView(headView, R.id.cimv_user_head).setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        TpqApplication.getInstance(getContext()).setShowingBbsUserEntity(user);
                        Intent openBbsUserInfo = new Intent(getContext(), BbsUserInfoActivity.class);
                        startActivity(openBbsUserInfo);
                    }
                });
            }

            initPostsPics(headView);
        }

        return headView;
    }

    private void initPostsPics(View view) {
        List<PicEntity> postsPicUrls = postsEntity.getPostsPicUrls();
        if (null != postsPicUrls && postsPicUrls.size() > 0) {
            LinearLayout postsPicsLl = findView(view, R.id.ll_posts_pics);

            //            LinearLayout.LayoutParams imvParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            LinearLayout.LayoutParams imvParams = new LinearLayout.LayoutParams(DensityUtil.dip2px(getContext(), 120), DensityUtil.dip2px(getContext(), 120));
            imvParams.topMargin = DensityUtil.dip2px(getContext(), 12);

            for (int i = 0; i < postsPicUrls.size(); i++) {
                ImageView imv = new ImageView(getContext());
                imv.setScaleType(ScaleType.CENTER_CROP);
                imv.setLayoutParams(imvParams);
                imv.setId(1000 + i);

                postsPicsLl.addView(imv, i);

                final PicEntity pic = postsPicUrls.get(i);
                if (null != pic) {
                    ImageView imv1 = findView(view, 1000 + i);
                    setImageView(view, 1000 + i, pic.getPicUrl(), R.drawable.common_btn_back2top);
                }

                imv.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        OpenFileUtil.openFile(getContext(), pic.getPicUrl());
                    }
                });
            }
        }

    }

    class ListViewUpdateListener implements IAutoLoadMoreListViewListener {

        @Override
        public void onRefresh() {
            if (!isRefresh && !isLoadMore) {
                isRefresh = true;
                commentsList = null;

                fetchCommentList(0, CommonConstant.MSG_PAGE_SIZE);
            }
        }

        @Override
        public void onLoadMore() {
            if (!isRefresh && !isLoadMore) {
                isLoadMore = true;
                if (null != commentsList && commentsList.size() > 0) {
                    fetchCommentList(commentsList.size(), CommonConstant.MSG_PAGE_SIZE);
                } else {
                    fetchCommentList(0);
                }
            }
        }

    }

    private void fetchCommentList(int offset) {
        fetchCommentList(offset, CommonConstant.MSG_PAGE_SIZE);
    }

    private void fetchCommentList(int offset, int page_sizes) {
        CommonRequest fetchCommentList = new CommonRequest();
        fetchCommentList.setRequestApiName(InterfaceConstant.API_THREAD_COMMENT_FETCH);
        fetchCommentList.setRequestID(InterfaceConstant.REQUEST_ID_THREAD_COMMENT_FETCH);
        fetchCommentList.addRequestParam(APIKey.COMMON_OFFSET, offset);
        fetchCommentList.addRequestParam(APIKey.COMMON_PAGE_SIZE, page_sizes);
        fetchCommentList.addRequestParam(APIKey.THREAD_ID, postsEntity.getId());
        fetchCommentList.addRequestParam(APIKey.COMMON_SORT_TYPES, APIKey.SORT_DESC);
        fetchCommentList.addRequestParam(APIKey.COMMON_SORT_FIELDS, APIKey.COMMON_CREATED_AT);
        fetchCommentList.addRequestParam(APIKey.COMMENT_STATUS, 1);

        addRequestAsyncTask(fetchCommentList);
    }

    @Override
    protected void onResponseAsyncTaskRender(Map<String, Object> result, String requestID, Map<String, Object> additionalArgsMap) {
        if (InterfaceConstant.REQUEST_ID_THREAD_COMMENT_FETCH.equals(requestID)) {
            if (null != result) {
                int status = TypeUtil.getInteger(result.get(APIKey.COMMON_STATUS), -1);
                String message = TypeUtil.getString(result.get(APIKey.COMMON_RESULT));
                if (status == APIKey.STATUS_SUCCESSFUL) {
                    Map<String, Object> resultMap = TypeUtil.getMap(result.get(APIKey.COMMON_RESULT));
                    if (null != resultMap) {
                        toBeContinued = TypeUtil.getInteger(resultMap.get(APIKey.COMMON_TO_BE_CONTINUED), 0);
                        List<Map<String, Object>> rawCommentList = TypeUtil.getList(resultMap.get(APIKey.THREAD_COMMENTS));
                        if (null != rawCommentList && rawCommentList.size() > 0) {
                            List<CommentEntity> tmpCommentList = EntityUtils.getCommentEntityList(rawCommentList);
                            if (null != tmpCommentList) {
                                if (null == commentsList || commentsList.size() == 0) {
                                    commentsList = new ArrayList<CommentEntity>();
                                    commentsAdapter = null;
                                }
                                commentsList.addAll(tmpCommentList);
                            }
                        }
                    }

                } else {
                    showToast(message);
                }
            }
            showCommentList();
        } else if (InterfaceConstant.REQUEST_ID_THREAD_LIKE_CREATE.equals(requestID)) {
            if (null != result) {
                int status = TypeUtil.getInteger(result.get(APIKey.COMMON_STATUS), -1);
                String message = TypeUtil.getString(result.get(APIKey.COMMON_RESULT));
                if (status == APIKey.STATUS_SUCCESSFUL) {
                    isPostsLikes = true;
                    TpqApplication.getInstance(getContext()).setPostsLikes(postsEntity.getId(), isPostsLikes);
                    postsEntity.setLikesCount(postsEntity.getLikesCount() + 1);
                    TextView postsLikecCountTv = findView(R.id.tv_posts_likes_count);
                    postsLikecCountTv.setText(postsEntity.getLikesCount() + "");

                    initTabbarButton();
                } else {
                    showToast(message);
                }
            }
        } else if (InterfaceConstant.REQUEST_ID_THREAD_LIKE_REMOVE.equals(requestID)) {
            if (null != result) {
                int status = TypeUtil.getInteger(result.get(APIKey.COMMON_STATUS), -1);
                String message = TypeUtil.getString(result.get(APIKey.COMMON_RESULT));
                if (status == APIKey.STATUS_SUCCESSFUL) {
                    isPostsLikes = false;
                    TpqApplication.getInstance(getContext()).setPostsLikes(postsEntity.getId(), isPostsLikes);
                    postsEntity.setLikesCount(postsEntity.getLikesCount() - 1);
                    TextView postsLikecCountTv = findView(R.id.tv_posts_likes_count);
                    postsLikecCountTv.setText(postsEntity.getLikesCount() + "");

                    initTabbarButton();
                } else {
                    showToast(message);
                }
            }
        } else if (InterfaceConstant.REQUEST_ID_THREAD_COLLECTION_CREATE.equals(requestID)) {
            if (null != result) {
                int status = TypeUtil.getInteger(result.get(APIKey.COMMON_STATUS), -1);
                String message = TypeUtil.getString(result.get(APIKey.COMMON_RESULT));
                if (status == APIKey.STATUS_SUCCESSFUL) {
                    isPostsCollected = true;
                    TpqApplication.getInstance(getContext()).setPostsCollected(postsEntity.getId(), isPostsCollected);
                    initTabbarButton();
                } else {
                    showToast(message);
                }
            }
        } else if (InterfaceConstant.REQUEST_ID_THREAD_COLLECTITON_REMOVE.equals(requestID)) {
            if (null != result) {
                int status = TypeUtil.getInteger(result.get(APIKey.COMMON_STATUS), -1);
                String message = TypeUtil.getString(result.get(APIKey.COMMON_RESULT));
                if (status == APIKey.STATUS_SUCCESSFUL) {
                    isPostsCollected = false;
                    TpqApplication.getInstance(getContext()).setPostsCollected(postsEntity.getId(), isPostsCollected);
                    initTabbarButton();
                } else {
                    showToast(message);
                }
            }
        }
    }

    private void showCommentList() {
        if (null == commentsAdapter) {
            commentsAdapter = new CommentsAdapter(getContext(), commentsList);
            commentListView.setAdapter(commentsAdapter);
        } else {
            commentsAdapter.notifyDataSetChanged();
        }

        if (toBeContinued == 1) {
            commentListView.setPullLoadEnable(true);
        } else {
            commentListView.setPullLoadEnable(false);
        }

        isRefresh = false;
        isLoadMore = false;
        commentListView.stopRefresh();
        commentListView.stopLoadMore();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
            finish();
            break;

        default:
            break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.rl_posts_likes:
            likeEvent();
            break;
        case R.id.rl_posts_collect:
            collectEvent();
            break;
        case R.id.rl_posts_comment:
            Intent openComment = new Intent(getContext(), PostCommentActivity.class);
            openComment.putExtra(PostCommentActivity.COMMENT_TYPE, PostCommentActivity.COMMENT_TYPE_REPLY);
            openComment.putExtra(PostCommentActivity.POST_ID, postsEntity.getId());

            startActivityForResult(openComment, UPDATE_POST_LIST);
            break;
        case R.id.tv_report:
            Intent openReportPost = new Intent(getContext(), BbsReportActivity.class);
            openReportPost.putExtra(BbsReportActivity.KEY_REPORT_TYPE, BbsReportActivity.REPORT_POST);
            openReportPost.putExtra(BbsReportActivity.KEY_REPORT_POST_ID, postsEntity.getId());
            openReportPost.putExtra(BbsReportActivity.KEY_REPORT_POST_TITLE, postsEntity.getTitle());
            startActivity(openReportPost);
            break;
        default:
            break;
        }
    }

    private void likeEvent() {
        CommonRequest likeRequest = new CommonRequest();
        if (!isPostsLikes) {
            likeRequest.setRequestApiName(InterfaceConstant.API_THREAD_LIKE_CREATE);
            likeRequest.setRequestID(InterfaceConstant.REQUEST_ID_THREAD_LIKE_CREATE);
        } else {
            likeRequest.setRequestApiName(InterfaceConstant.API_THREAD_LIKE_REMOVE);
            likeRequest.setRequestID(InterfaceConstant.REQUEST_ID_THREAD_LIKE_REMOVE);
        }
        if (null != postsEntity) {
            String postsId = postsEntity.getId();
            likeRequest.addRequestParam(APIKey.THREAD_ID, postsId);
        }
        String userId = TpqApplication.getInstance(getContext()).getUserId();
        likeRequest.addRequestParam(APIKey.USER_ID, userId);

        addRequestAsyncTask(likeRequest);
    }

    private void collectEvent() {
        CommonRequest collectRequest = new CommonRequest();
        if (!isPostsCollected) {
            collectRequest.setRequestApiName(InterfaceConstant.API_THREAD_COLLECTION_CREATE);
            collectRequest.setRequestID(InterfaceConstant.REQUEST_ID_THREAD_COLLECTION_CREATE);
        } else {
            collectRequest.setRequestApiName(InterfaceConstant.API_THREAD_COLLECTITON_REMOVE);
            collectRequest.setRequestID(InterfaceConstant.REQUEST_ID_THREAD_COLLECTITON_REMOVE);
        }
        if (null != postsEntity) {
            String postsId = postsEntity.getId();
            collectRequest.addRequestParam(APIKey.THREAD_ID, postsId);
        }
        String userId = TpqApplication.getInstance(getContext()).getUserId();
        collectRequest.addRequestParam(APIKey.USER_ID, userId);

        addRequestAsyncTask(collectRequest);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == UPDATE_POST_LIST && resultCode == RESULT_OK) {
            commentsList = null;
            fetchCommentList(0);
        }
    }

    class DealCommentEventListener implements OnItemSelectedListener {

        private BasicDialog dialog;

        public DealCommentEventListener(BasicDialog dialog) {
            this.dialog = dialog;
        }

        @Override
        public void onLikeComment(CommentEntity comment, boolean isLike) {
            if (null != commentsAdapter) {
                commentsAdapter.notifyDataSetChanged();
            }
        }

        @Override
        public void onDeleteComent(CommentEntity comment) {
            if (null != commentsList && commentsList.size() > 0) {
                commentsList.remove(comment);
                if (null != commentsAdapter) {
                    commentsAdapter.notifyDataSetChanged();
                }
            }
        }

        @Override
        public void onHasSelected(int selectedItemId) {
            if (null != dialog && dialog.isShowing()) {
                dialog.dismiss();
            }
        }
    }

}
