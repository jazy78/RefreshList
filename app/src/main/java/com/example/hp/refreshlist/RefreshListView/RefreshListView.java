package com.example.hp.refreshlist.RefreshListView;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.hp.refreshlist.R;

/**
 * Created by hp on 2016/3/29.
 */
public class RefreshListView extends ListView implements AbsListView.OnScrollListener {

    private OnRefreshListener mOnRefreshListener;
    private OnLoadMoreListener mOnLoadMoreListener;
    private LinearLayout headerView;
    private LinearLayout footerView;
    private TextView tv_load;
    private RefreshBgView bg_view;
    private AnimationDrawable bg_viewAnimation;
    private RefreshAnimView refreshAnimView;
    private RefreshLoadingView refreshLoadingView;
    private AnimationDrawable loadAnimation;
    private int headviewHeight;
    private int footviewHeight;


    private int totalcount;//item总数量
    private boolean isScrollFirst;//是否滑动到顶部
    private boolean isScrollLast;//是否滑动到底部

    private boolean isRefreshable;//是否启用下拉刷新
    private int refreshstate;//下拉刷新状态
    private int loadstate;//上拉加载状态
    private boolean isLoadable;//是否启用上拉加载

    private static final float REFRESH_RATIO = 2.0f;//下拉系数
    private static final float LOAD_RATIO = 3;//上拉系数

    private static final int REFRESH_DONE = 0;//下拉刷新完成
    private static final int PULL_TO_REFRESH = 1;//下拉中（下拉高度未超出headview高度）
    private static final int RELEASE_TO_REFRESH = 2;//下拉中（下拉高度超出headview高度）
    private static final int REFRESHING = 3;//刷新中

    private static final int LOAD_DONE = 4;//上拉加载完成
    private static final int PULL_TO_LOAD = 5;//上拉中（上拉高度未超出footerview高度）
    private static final int RELEASE_TO_LOAD = 6;//上拉中（上拉高度超出footerview高度）
    private static final int LOADING = 7;//加载中

    private float startY,//手指落点
            offsetY;//手指滑动的距离

    public RefreshListView(Context context) {

        super(context);
        init(context);
    }

    public RefreshListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public RefreshListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }


    private void init(Context context) {
        /**
         * android  2.3 ListView引入   overscroll 的相关api 之后
         * 添加了一个自带的效果 也就是 当滑动到边界的时候，如果再滑动，
         * 就会有一个边界就会有一个发光效果
         * 如何去掉这个效果呢？
         * 只需要在布局的 xml中  添加android:fadingEdge="none"
         * 或者在代码中添加setOverScrollMode(OVER_SCROLL_NEVER);
         */

        setOverScrollMode(View.OVER_SCROLL_NEVER);
        setOnScrollListener(this);
        headerView=(LinearLayout) View.inflate(context, R.layout.listview_head_view,null);
        footerView=(LinearLayout)View.inflate(context,R.layout.listview_foot_view,null);
        tv_load = (TextView) footerView.findViewById(R.id.tv_load);

        bg_view= (RefreshBgView) headerView.findViewById(R.id.bg_view);
        bg_view.setBackgroundResource(R.drawable.loading_bg1);
        bg_viewAnimation = (AnimationDrawable) bg_view.getBackground();
        bg_viewAnimation.start();

        refreshAnimView = (RefreshAnimView) headerView.findViewById(R.id.first_step_view);
        refreshLoadingView= (RefreshLoadingView) headerView.findViewById(R.id.second_step_view);
        refreshLoadingView.setBackgroundResource(R.drawable.anim_refresh);
        loadAnimation = (AnimationDrawable) refreshLoadingView.getBackground();

        meaSureView(headerView);
        meaSureView(footerView);
        //添加两个view
        addHeaderView(headerView);
        addFooterView(footerView);

        headviewHeight=headerView.getMeasuredHeight();
        footviewHeight=footerView.getMeasuredHeight();

        headerView.setPadding(0,-headviewHeight,0,0);
        footerView.setPadding(0,0,0,-footviewHeight);

        refreshstate = REFRESH_DONE;//开始下拉完成
        loadstate = LOAD_DONE;//上拉完成

        isRefreshable = true; //可以下拉
        isLoadable = true; //可以上拉

    }


    /**
     * 计算控件宽高
     *
     * @param child
     */

    private void meaSureView(View child){
        ViewGroup.LayoutParams p=child.getLayoutParams();
        if(p==null){
            p=new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        //通过父View给定参数 来获得子view的MeasureSpec;
        int childWidthSpec=ViewGroup.getChildMeasureSpec(0, 0 + 0, p.width);
        int childHeightSpec;
        int childhight=p.height;
        if(childhight>0){

            childHeightSpec=MeasureSpec.makeMeasureSpec(childhight,MeasureSpec.EXACTLY);
        }else {

            childHeightSpec=MeasureSpec.makeMeasureSpec(childhight,MeasureSpec.UNSPECIFIED);
        }
        child.measure(childWidthSpec,childHeightSpec);
    }


    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
           totalcount=totalItemCount;

        //判断是不是滑到最顶端或者最低端
        if(firstVisibleItem==0){
            isScrollFirst=true;
        }else {

            isScrollFirst=false;
        }
        if(firstVisibleItem+visibleItemCount==totalcount){
            isScrollLast=true;

        }else {

            isScrollLast=false;
        }

    }


    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                startY=ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                offsetY=ev.getY()-startY;

                /**
                 * 下拉刷新
                 */
                if(isRefreshable && refreshstate!=REFRESHING && isScrollFirst && offsetY>0 && loadstate==LOAD_DONE){
                    float headViewShowHeigt=offsetY/REFRESH_RATIO;//headview露出的高度
                    float currentProgress = headViewShowHeigt /headviewHeight;//根据此比例，在滑动时改变动图大小
                    Log.d("AAA","currentProgress="+currentProgress);
                    if(currentProgress>=1){

                        currentProgress=1;
                    }

                    switch (refreshstate){
                        case REFRESH_DONE:
                            refreshstate = PULL_TO_REFRESH;
                            break;
                        case PULL_TO_REFRESH:
                              setSelection(0);
                            //当state=PULL_TO_REFRESH时，如果headerViewShowHeight超过了headerViewHeight，那么此时已经达到可刷新状态了，
                            //意思是准备刷新中，如果此时用户松手，则执行刷新操作
                            if(headViewShowHeigt-headviewHeight>0) {
                                refreshstate = RELEASE_TO_REFRESH;
                                changeHeaderByState(refreshstate);
                            }
                            break;
                        case RELEASE_TO_REFRESH:
                            setSelection(0);
                            if(headViewShowHeigt-headviewHeight<0){

                                refreshstate=PULL_TO_REFRESH;
                                changeHeaderByState(refreshstate);
                            }

                            break;

                    }

                    if(refreshstate==RELEASE_TO_REFRESH||refreshstate==PULL_TO_REFRESH){
                         //设置离父view的距离
                        headerView.setPadding(0,(int)(headViewShowHeigt-headviewHeight),0,0);
                        refreshAnimView.setCurrentProgress(currentProgress);//绘制headview中的动画
                        refreshAnimView.postInvalidate();
                    }

                }

                if(isLoadable && isScrollLast && offsetY<0 && loadstate!=LOADING && refreshstate==REFRESH_DONE){

                    float footershowViewHieght=-offsetY/LOAD_RATIO;
                    switch (loadstate){
                        case  LOAD_DONE:

                            loadstate=PULL_TO_LOAD;

                            break;
                        case PULL_TO_LOAD:
                            setSelection(totalcount);
                            if(footershowViewHieght-footviewHeight>0) {
                                loadstate = RELEASE_TO_LOAD;
                                changeFooterByState(loadstate);
                            }
                            break;
                        case RELEASE_TO_LOAD:
                            setSelection(totalcount);
                            if(footershowViewHieght-footviewHeight<0){

                                loadstate=PULL_TO_REFRESH;
                                changeFooterByState(loadstate);
                            }

                            break;

                     }
                         if(loadstate==PULL_TO_LOAD||loadstate==RELEASE_TO_LOAD){

                             footerView.setPadding(0,0,0,(int)(footershowViewHieght-footviewHeight));
                         }

                }


                break;
            case MotionEvent.ACTION_UP:
                /**
                 * 下拉刷新
                 */
                if(isRefreshable) {
                    if (refreshstate == PULL_TO_REFRESH) {

                        refreshstate = REFRESH_DONE;
                        changeHeaderByState(refreshstate);
                    }
                    if (refreshstate == RELEASE_TO_REFRESH) {
                        refreshstate = REFRESHING;
                        changeHeaderByState(refreshstate);
                        mOnRefreshListener.onRefresh();
                    }
                }

                /**
                 * 上拉加载
                 */
                if (isLoadable) {//只有当启用上拉加载时触发
                    if (loadstate == PULL_TO_LOAD) {
                        loadstate = LOAD_DONE;
                        changeFooterByState(loadstate);
                    }
                    if (loadstate == RELEASE_TO_LOAD) {
                        loadstate = LOADING;
                        changeFooterByState(loadstate);
                        mOnLoadMoreListener.OnLoadMore();
                    }
                }
                break;



        }


        return super.onTouchEvent(ev);
    }

    public void setmOnLoadMoreListener(OnLoadMoreListener mOnLoadMoreListener) {
        this.mOnLoadMoreListener = mOnLoadMoreListener;
    }

    public void setmOnRefreshListener(OnRefreshListener mOnRefreshListener) {
        this.mOnRefreshListener = mOnRefreshListener;
    }
    /**
     * 下拉刷新完成
     */
    public void setOnRefreshComplete() {
        refreshstate = REFRESH_DONE;
        changeHeaderByState(refreshstate);
    }

    /**
     * 加载更多完成
     */
    public void setOnLoadMoreComplete() {
        loadstate = LOAD_DONE;
        changeFooterByState(loadstate);
    }

    private void changeHeaderByState(int state) {
        switch (state) {
            case REFRESH_DONE:
                headerView.setPadding(0, -headviewHeight, 0, 0);
                refreshAnimView.setVisibility(View.VISIBLE);
                refreshLoadingView.setVisibility(View.GONE);
                loadAnimation.stop();
                break;
            case RELEASE_TO_REFRESH:
                Log.e("jj", "RELEASE_TO_REFRESH");
                break;
            case PULL_TO_REFRESH:
                Log.e("jj", "PULL_TO_REFRESH");
                break;
            case REFRESHING:
                refreshAnimView.setVisibility(View.GONE);
                refreshLoadingView.setVisibility(View.VISIBLE);
                loadAnimation.start();
                headerView.setPadding(0, 0, 0, 0);
                break;
            default:
                break;
        }
    }

    private void changeFooterByState(int loadstate){

        switch (loadstate){
            case LOAD_DONE:
                footerView.setPadding(0,0,0,-footviewHeight);
                tv_load.setText("上拉加载更多");
                break;
            case PULL_TO_LOAD:
                tv_load.setText("上拉加载更多");
                break;
            case RELEASE_TO_LOAD:
                tv_load.setText("松开加载更多");
                break;
            case LOADING:
                tv_load.setText("正在加载...");
                footerView.setPadding(0, 0, 0, 0);
                break;
            default:
                break;
        }

    }

    //定义刷新监听器
    public interface OnRefreshListener {

        void onRefresh();
    }

    //设置下载更多监听器
    public interface OnLoadMoreListener {


        void OnLoadMore();
    }
}
