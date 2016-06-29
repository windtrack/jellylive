package com.oo58.jelly.view.listview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.oo58.jelly.R;

import java.text.SimpleDateFormat;

/**
 * @author zhongxf
 * @Description 下拉刷新和上拉加载的ListView
 * @Date 2016/6/14.
 */
public class RefreshListView extends ListView implements AbsListView.OnScrollListener{

    private static final String TAG = "RefreshListView";
    private int firstVisibleItemPosition;
    private int downY;
    private int headerViewHeight;
    private View headerView;

    private final int DOWN_PULL_REFRESH = 0;
    private final int RELEASE_REFRESH = 1;
    private final int REFRESHING = 2;
    private int currentState = DOWN_PULL_REFRESH;

    private Animation upAnimation;
    private Animation downAnimation;
    private ImageView ivArrow;
    private ProgressBar mProgressBar;
    private TextView tvState;
    private TextView tvLastUpdateTime;

    private OnRefreshListener mOnRefershListener;
    private boolean isScrollToBottom;
    private View footerView;
    private int footerViewHeight;
    private boolean isLoadingMore = false;

    public RefreshListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initHeaderView();
        initFooterView();
        this.setOnScrollListener(this);
    }

    /**
     * 初始化底部布局
     */
    private void initFooterView() {
        footerView = View.inflate(getContext(), R.layout.listview_footer, null);
        footerView.measure(0, 0);
        footerViewHeight = footerView.getMeasuredHeight();
        footerView.setPadding(0, -footerViewHeight, 0, 0);
        this.addFooterView(footerView);
    }

    /**
     * 初始化头部方法
     */
    private void initHeaderView() {
        headerView = View.inflate(getContext(), R.layout.listview_header, null);
        ivArrow = (ImageView) headerView.findViewById(R.id.iv_listview_header_arrow);
        mProgressBar = (ProgressBar) headerView.findViewById(R.id.pb_listview_header);
        tvState = (TextView) headerView.findViewById(R.id.tv_listview_header_state);
        tvLastUpdateTime = (TextView) headerView.findViewById(R.id.tv_listview_header_last_update_time);

        // 显示最后更新时间
        tvLastUpdateTime.setText("最后更新时间: " + getLastUpdateTime());

        headerView.measure(0, 0);
        headerViewHeight = headerView.getMeasuredHeight();
        headerView.setPadding(0, -headerViewHeight, 0, 0);
        this.addHeaderView(headerView); // 将头部布局添加到ListView的顶部
        initAnimation();
    }

    /**
     * 格式化时间
     */
    private String getLastUpdateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(System.currentTimeMillis());
    }

    /**
     * 初始化动画
     */
    private void initAnimation() {
        upAnimation = new RotateAnimation(0f, -180f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        upAnimation.setDuration(1500);
        upAnimation.setFillAfter(true);

        downAnimation = new RotateAnimation(-180f, -360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        downAnimation.setDuration(1500);
        downAnimation.setFillAfter(true);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downY = (int) ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                int moveY = (int) ev.getY();
                int diff = (moveY - downY) / 2;
                int paddingTop = -headerViewHeight + diff;
                if (firstVisibleItemPosition == 0 && -headerViewHeight < paddingTop) {
                    if (paddingTop > 0 && currentState == DOWN_PULL_REFRESH) {
                        currentState = RELEASE_REFRESH;
                        refreshHeaderView();
                    } else if (paddingTop < 0 && currentState == RELEASE_REFRESH) {
                        currentState = DOWN_PULL_REFRESH;
                        refreshHeaderView();
                    }
                    headerView.setPadding(0, paddingTop, 0, 0);
                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:
                if (currentState == RELEASE_REFRESH) {
                    headerView.setPadding(0, 0, 0, 0);
                    currentState = REFRESHING;
                    refreshHeaderView();
                    if (mOnRefershListener != null) {
                        mOnRefershListener.onDownPullRefresh();
                    }
                } else if (currentState == DOWN_PULL_REFRESH) {
                    headerView.setPadding(0, -headerViewHeight, 0, 0);
                }
                break;
            default:
                break;
        }
        return super.onTouchEvent(ev);
    }

    /**
     * 刷新头部
     */
    private void refreshHeaderView() {
        switch (currentState) {
            case DOWN_PULL_REFRESH:
                tvState.setText("下拉刷新");
                ivArrow.startAnimation(downAnimation);
                break;
            case RELEASE_REFRESH:
                tvState.setText("释放刷新");
                ivArrow.startAnimation(upAnimation);
                break;
            case REFRESHING:
                ivArrow.clearAnimation();
                ivArrow.setVisibility(View.GONE);
                mProgressBar.setVisibility(View.VISIBLE);
                tvState.setText("正在刷新，请稍后...");
                break;
            default:
                break;
        }
    }

    /**
     * 滚动监听
     */
    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

        if (scrollState == SCROLL_STATE_IDLE || scrollState == SCROLL_STATE_FLING) {
            if (isScrollToBottom && !isLoadingMore) {
                isLoadingMore = true;
                footerView.setPadding(0, 0, 0, 0);
                this.setSelection(this.getCount());
                if (mOnRefershListener != null) {
                    mOnRefershListener.onLoadingMore();
                }
            }
        }
    }

    /**
     * 滚动到底部自动刷新
     */
    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        firstVisibleItemPosition = firstVisibleItem;

        if (getLastVisiblePosition() == (totalItemCount - 1)) {
            isScrollToBottom = true;
        } else {
            isScrollToBottom = false;
        }
    }

    /**
     * 设置刷新和加载更多的回调用方法
     */
    public void setOnRefreshListener(OnRefreshListener listener) {
        mOnRefershListener = listener;
    }

    /**
     * 隐藏下拉刷新的布局
     */
    public void hideHeaderView() {
        headerView.setPadding(0, -headerViewHeight, 0, 0);
        ivArrow.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.GONE);
        tvState.setText("下拉刷新");
        tvLastUpdateTime.setText("最后更新时间: " + getLastUpdateTime());
        currentState = DOWN_PULL_REFRESH;
    }

    /**
     *隐藏加载更多的布局
     */
    public void hideFooterView() {
        footerView.setPadding(0, -footerViewHeight, 0, 0);
        isLoadingMore = false;
    }

}
