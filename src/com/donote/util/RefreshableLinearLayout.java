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
 * ˢ�¿���view
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
	// �������
	// private boolean isDragging = false;
	// �Ƿ��ˢ�±��
	//private boolean isRefreshEnabled = true;
	// ��ˢ���б��
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
		// ��������
		refreshTargetTop = -dipTopx(mContext, 60);
		scroller = new Scroller(mContext);
		shunRotate = new RotateAnimation(0, 180, dipTopx(mContext, 20),
				dipTopx(mContext, 20));
		shunRotate.setDuration(300);
		shunRotate.setFillAfter(true);
		calendar = mContext.getSharedPreferences("syncTime", 0);
		// ˢ����ͼ���˵ĵ�view
		refreshView = LayoutInflater.from(mContext).inflate(
				R.layout.refresh_top_item, null);
		/**
		 * ����ʱ�����ͼ
		 */
		// ָʾ��view
		refreshIndicatorView = (ImageView) refreshView
				.findViewById(R.id.indicator);
		// ������ʾtext
		downTextView = (TextView) refreshView.findViewById(R.id.refresh_hint);

		/**
		 * ˢ��ʱ�����ͼ
		 */
		// ��LinearLayout
		refereshLinearLayout = (LinearLayout) refreshView
				.findViewById(R.id.referesh_linearlayout);
		// ˢ��bar
		// bar = (ProgressBar) refreshView.findViewById(R.id.progress);
		// ������ʾʱ��

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
	 * ˢ��
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
			// ��¼��y����
			lastY = y;
			break;

		case MotionEvent.ACTION_MOVE:
			// y�ƶ�����
			int m = y - lastY;
			if (((m > 3 || m < 0)))
			{
				doMovement(m);
			}
			// ��¼�´˿�y����
			this.lastY = y;
			break;
		case MotionEvent.ACTION_UP:
			fling();
			break;
		}
		return true;
	}

	/**
	 * up�¼�����
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
		{// �����˴�����ˢ���¼�
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
	 * ����move�¼�����
	 * 
	 * @param moveY
	 */
	private void doMovement(int moveY) {
		// TODO Auto-generated method stub
		LinearLayout.LayoutParams lp = (LayoutParams) refreshView
				.getLayoutParams();
		// if(moveY > 0){
		// ��ȡview���ϱ߾�
		float f1 = lp.topMargin;
		float f2 = (moveY - 3) * 0.4F;
		int i = (int) (f1 + f2);
		// �޸��ϱ߾�
		lp.topMargin = i;
		// �޸ĺ�ˢ��
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
	 * ˢ��ʱ��
	 * 
	 * @param refreshTime2
	 */
	private void setRefreshTime(Long time) {
		// TODO Auto-generated method stub

	}

	/**
	 * ����ˢ���¼�
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
	 * �÷���һ���ontouchEvent һ���� (non-Javadoc)
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
			// y�ƶ�����
			int m = y - lastY;
			// ��¼�´˿�y����
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
			// ���Զ�Ӹ�else if��֧��GridView
		}
		return false;
	}

	/**
	 * ˢ�¼����ӿ�
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