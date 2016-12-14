package com.donote.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import com.wxl.donote.R;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Scroller;
import android.widget.TextView;

/**
 * 刷新控制view
 * 
 * @author doMen
 * 
 */
public class RefreshableLinearLayout extends LinearLayout {

	private Scroller scroller;
	private View refreshView;
	private ImageView refreshIndicatorView;
	private int refreshTargetTop = -60;
	// private ProgressBar bar;
	private TextView downTextView;
	private TextView timeTextView;
	private LinearLayout refereshLinearLayout;
	private RefreshListener refreshListener;
	private Long refreshTime = null;
	// private int lastX;
	private int lastY;
	private boolean isChange = false;
	// 拉动标记
	// private boolean isDragging = false;
	// 是否可刷新标记
	//private boolean isRefreshEnabled = true;
	// 在刷新中标记
	private boolean isRefreshing = false;
	private RotateAnimation shunRotate;
	// private RotateAnimation niRotate;
	private Context mContext;
	private ListView listView = null;
	private GroupListView groupListView = null;
	private SharedPreferences calendar;

	public RefreshableLinearLayout(Context context) {
		super(context);
		mContext = context;
	}

	public RefreshableLinearLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		init();

	}

	private void init() {
		// TODO Auto-generated method stub
		// 滑动对象，
		refreshTargetTop = -dipTopx(mContext, 60);
		scroller = new Scroller(mContext);
		shunRotate = new RotateAnimation(0, 180, dipTopx(mContext, 20),
				dipTopx(mContext, 20));
		shunRotate.setDuration(300);
		shunRotate.setFillAfter(true);
		calendar = mContext.getSharedPreferences("syncTime", 0);
		// 刷新视图顶端的的view
		refreshView = LayoutInflater.from(mContext).inflate(
				R.layout.refresh_top_item, null);
		/**
		 * 拉动时候的视图
		 */
		// 指示器view
		refreshIndicatorView = (ImageView) refreshView
				.findViewById(R.id.indicator);
		// 下拉显示text
		downTextView = (TextView) refreshView.findViewById(R.id.refresh_hint);

		/**
		 * 刷新时候的视图
		 */
		// 根LinearLayout
		refereshLinearLayout = (LinearLayout) refreshView
				.findViewById(R.id.referesh_linearlayout);
		// 刷新bar
		// bar = (ProgressBar) refreshView.findViewById(R.id.progress);
		// 下来显示时间

		timeTextView = (TextView) refreshView.findViewById(R.id.refresh_time);
		if (calendar.getString("time", null) != null)
		{
			timeTextView.setText(getResources().getString(R.string.sync_last_time) +" "+ calendar.getString("time", null));
		} else
		{
			timeTextView.setText(getResources().getString(R.string.never_sync));
		}
		LayoutParams lp = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, -refreshTargetTop);
		lp.topMargin = refreshTargetTop;
		lp.gravity = Gravity.CENTER;
		addView(refreshView, lp);
	}

	/**
	 * 刷新
	 * 
	 * @param time
	 */
	@SuppressLint("SimpleDateFormat")
	private void setRefreshDate(Date date) {
		SimpleDateFormat format = new SimpleDateFormat("MM-dd HH:mm");
		timeTextView.setText(getResources().getString(R.string.sync_last_time) + format.format(date));
		calendar.edit().putString("time", format.format(date)).commit();

	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		/*
		 * if (isRefreshing) { return false; }
		 */

		int y = (int) event.getRawY();

		switch (event.getAction())
		{
		case MotionEvent.ACTION_DOWN:
			// 记录下y坐标
			lastY = y;
			break;

		case MotionEvent.ACTION_MOVE:
			// y移动坐标
			int m = y - lastY;
			if (((m > 3 || m < 0)))
			{
				doMovement(m);
			}
			// 记录下此刻y坐标
			this.lastY = y;
			break;
		case MotionEvent.ACTION_UP:
			fling();
			break;
		}
		return true;
	}

	/**
	 * up事件处理
	 */
	private void fling() {
		// TODO Auto-generated method stub
		if (isRefreshing)
		{
			return;
		}
		LinearLayout.LayoutParams lp = (LayoutParams) refreshView
				.getLayoutParams();
		if (lp.topMargin > 6)
		{// 拉到了触发可刷新事件
			refresh();
		} else
		{
			returnInitState();
		}
	}

	private void returnInitState() {
		// TODO Auto-generated method stub
		LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) this.refreshView
				.getLayoutParams();
		int i = lp.topMargin;
		scroller.startScroll(0, i, 0, refreshTargetTop - i);
		invalidate();
	}

	private void refresh() {
		// TODO Auto-generated method stub
		LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) this.refreshView
				.getLayoutParams();
		int i = lp.topMargin;
		refreshIndicatorView.setVisibility(View.GONE);
		refereshLinearLayout.setVisibility(View.VISIBLE);
		// bar.setVisibility(View.VISIBLE);
		// timeTextView.setVisibility(View.VISIBLE);
		downTextView.setVisibility(View.GONE);
		scroller.startScroll(0, i, 0, 0 - i);
		invalidate();
		if (refreshListener != null)
		{
			refreshListener.onRefresh(this);
			isRefreshing = true;
		}
	}

	/** 
     *  
     */
	@Override
	public void computeScroll() {
		// TODO Auto-generated method stub
		if (scroller.computeScrollOffset())
		{
			int i = this.scroller.getCurrY();
			LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) this.refreshView
					.getLayoutParams();
			// int k = Math.max(i, refreshTargetTop);
			lp.topMargin = i;
			this.refreshView.setLayoutParams(lp);
			this.refreshView.invalidate();
			invalidate();
		}
	}

	/**
	 * 下拉move事件处理
	 * 
	 * @param moveY
	 */
	private void doMovement(int moveY) {
		// TODO Auto-generated method stub
		LinearLayout.LayoutParams lp = (LayoutParams) refreshView
				.getLayoutParams();
		// if(moveY > 0){
		// 获取view的上边距
		float f1 = lp.topMargin;
		float f2 = (moveY - 3) * 0.4F;
		int i = (int) (f1 + f2);
		// 修改上边距
		lp.topMargin = i;
		// 修改后刷新
		refreshView.setLayoutParams(lp);
		refreshView.invalidate();
		invalidate();
		// }
		if (refreshTime != null)
		{
			setRefreshTime(refreshTime);
		}
		downTextView.setVisibility(View.VISIBLE);
		refreshIndicatorView.setVisibility(View.VISIBLE);
		refereshLinearLayout.setVisibility(View.GONE);
		// timeTextView.setVisibility(View.GONE);
		// bar.setVisibility(View.GONE);
		if (lp.topMargin > 6)
		{
			downTextView.setText(getResources().getString(R.string.loose_to_refresh));
			if (!isChange)
			{
				refreshIndicatorView
						.setImageResource(R.drawable.refresh_arrow_up);
				refreshIndicatorView.startAnimation(shunRotate);
				isChange = true;
			}

		} else
		{

			downTextView.setText(getResources().getString(R.string.pull_down_to_refresh));
			if (isChange)
			{
				refreshIndicatorView
						.setImageResource(R.drawable.refresh_arrow_down);
				refreshIndicatorView.startAnimation(shunRotate);
				isChange = false;
			}
			isChange = false;
			// refreshIndicatorView.setImageResource(R.drawable.refresh_arrow_up);
		}

	}

/*	public void setRefreshEnabled(boolean b) {
		this.isRefreshEnabled = b;
	}*/

	public void setRefreshListener(RefreshListener listener) {
		this.refreshListener = listener;
	}

	/**
	 * 刷新时间
	 * 
	 * @param refreshTime2
	 */
	private void setRefreshTime(Long time) {
		// TODO Auto-generated method stub

	}

	/**
	 * 结束刷新事件
	 */
	public void finishRefresh() {
		LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) this.refreshView
				.getLayoutParams();
		int i = lp.topMargin;
		scroller.startScroll(0, i, 0, refreshTargetTop);
		invalidate();
		// refreshIndicatorView.setVisibility(View.VISIBLE);
		// timeTextView.setVisibility(View.VISIBLE);
		isRefreshing = false;
		setRefreshDate(Calendar.getInstance().getTime());
	}

	public void setListView(ListView l) {
		listView = l;

	}

	public void setGroupListView(GroupListView l) {
		groupListView = l;
	}

	/*
	 * 该方法一般和ontouchEvent 一起用 (non-Javadoc)
	 * 
	 * @see
	 * Android.view.ViewGroup#onInterceptTouchEvent(android.view.MotionEvent)
	 */
	@Override
	public boolean onInterceptTouchEvent(MotionEvent e) {
		// TODO Auto-generated method stub
		int action = e.getAction();
		int y = (int) e.getRawY();
		switch (action)
		{
		case MotionEvent.ACTION_DOWN:
			lastY = y;
			break;

		case MotionEvent.ACTION_MOVE:
			if ((listView != null && listView.getFirstVisiblePosition() != 0)
					|| (groupListView != null && groupListView
							.getFirstVisiblePosition() != 0) || isRefreshing == true)
			{
				return false;
			}
			// y移动坐标
			int m = y - lastY;
			// 记录下此刻y坐标
			this.lastY = y;
			if (canScroll(m))
			{
				return true;
			}
			break;
		case MotionEvent.ACTION_UP:

			break;

		case MotionEvent.ACTION_CANCEL:

			break;
		}
		return false;
	}

	private boolean canScroll(int m) {
		View childView;
		if (m < 6)
		{
			return false;
		}
		if (getChildCount() > 1)
		{
			childView = this.getChildAt(1);
			if (childView instanceof ListView)
			{
				int top = ((ListView) childView).getChildAt(0).getTop();
				int pad = ((ListView) childView).getListPaddingTop();
				if ((Math.abs(top - pad)) < 3
						&& ((ListView) childView).getFirstVisiblePosition() == 0)
				{
					return true;
				} else
				{
					return false;
				}
			} else if (childView instanceof ScrollView)
			{
				if (((ScrollView) childView).getScrollY() == 0)
				{
					return true;
				} else
				{
					return false;
				}
			}
			// 可以多加个else if来支持GridView
		}
		return false;
	}

	/**
	 * 刷新监听接口
	 * 
	 * @author Nono
	 * 
	 */
	public interface RefreshListener {
		public void onRefresh(RefreshableLinearLayout view);
	}

	public static int dipTopx(Context context, float dipValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}
}