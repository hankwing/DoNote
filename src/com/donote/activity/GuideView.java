package com.donote.activity;

import java.util.ArrayList;
import java.util.List;

import com.baidu.mobstat.StatService;
import com.wxl.donote.R;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class GuideView extends Activity implements OnClickListener,
		OnPageChangeListener {
	private ViewPager viewPager;
	private ArrayList<View> pageViews;
	private ViewGroup main, group;
	private ImageView[] dots;
	private ImageButton imgbtn;
	private int currentIndex;
	private ViewPagerAdapter vpAdapter;
	private ImageView imageView;
	private static final int GO_BTN = 10;

	private static final int[] help_pics = { R.drawable.guide0,
			R.drawable.guide1, R.drawable.guide2, R.drawable.guide_cooperate};

	/* (non-Javadoc)
	 * @see android.app.Activity#onPause()
	 */
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		StatService.onPause(this);
		super.onPause();
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {
		StatService.onResume(this);
		// TODO Auto-generated method stub
		super.onResume();
	}

	/**
	 * (非 Javadoc) Title: onCreate Description:
	 * 
	 * @param savedInstanceState
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		pageViews = new ArrayList<View>();
		LayoutInflater inflater = getLayoutInflater(); 
		main = (ViewGroup)inflater.inflate(R.layout.guide, null); 
		LinearLayout.LayoutParams mParams = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		for (int i = 0; i < help_pics.length; i++) {
			RelativeLayout rl  = new RelativeLayout(this);
			rl.setLayoutParams(mParams);
			rl.setBackgroundResource(help_pics[i]);
			if(i == (help_pics.length -1)){
				imgbtn = new ImageButton(this);
				imgbtn.setBackgroundResource(R.drawable.help_go_btn_selector);
				imgbtn.setOnClickListener(this);
				imgbtn.setTag(GO_BTN);
				imgbtn.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						SharedPreferences account = getSharedPreferences("account", 0);
						if( account.getString("account_name", null) == null ) {
							Intent intent = new Intent(GuideView.this, Login.class);
							startActivity(intent);
							finish();
							overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
						}
						else {
							Intent mainIntent = new Intent(GuideView.this, MainActivity.class);
							startActivity(mainIntent);
							finish();
							overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
						}
					}
				});
				RelativeLayout .LayoutParams lp1 =
						new RelativeLayout .LayoutParams(
						RelativeLayout.LayoutParams.WRAP_CONTENT,
						RelativeLayout.LayoutParams.WRAP_CONTENT);
				lp1.bottomMargin = 80;
				lp1.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
				lp1.addRule(RelativeLayout.CENTER_HORIZONTAL,RelativeLayout.TRUE);
				rl.addView(imgbtn,lp1);
				
			}
			pageViews.add(rl);
			
		}
		/*RelativeLayout rl  = new RelativeLayout(this);
		rl.setLayoutParams(mParams);
		pageViews.add(rl);*/
		group = (ViewGroup) main.findViewById(R.id.viewGroup);
		viewPager = (ViewPager) main.findViewById(R.id.guidePages);
		vpAdapter = new ViewPagerAdapter(pageViews);
		viewPager.setAdapter(vpAdapter);
		// 绑定回调
		viewPager.setOnPageChangeListener(this);
		// 初始化底部小点
		initDots();
		setContentView(main); 
		

	}

	/**
	 * @Title: initDots
	 * @Description: TODO(这里用一句话描述这个方法的作用) 设定文件
	 * @return void 返回类型
	 * @throws
	 */
	private void initDots() {
		// TODO Auto-generated method stub
		dots = new ImageView[help_pics.length];  
		for (int i = 0; i < help_pics.length; i++) {
			imageView = new ImageView(GuideView.this);
			imageView.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
			imageView.setMaxHeight(20);
			imageView.setMaxWidth(40);
			imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
			imageView.setAdjustViewBounds(true);
			imageView.setPadding(10, 0, 10, 0);
			imageView.setImageResource(R.drawable.dot);
			dots[i] = imageView;
			dots[i].setEnabled(false);
			dots[i].setTag(i);
			dots[i].setOnClickListener(this); 
			group.addView(dots[i]); 
		}
		currentIndex = 0;
		dots[currentIndex].setEnabled(true);
	}

	private void setCurView(int position) {
		if (position < 0 || position >= help_pics.length) {
			return;
		}
		viewPager.setCurrentItem(position);
	}

	private void setCurDot(int positon) {
		if (positon < 0 || positon > help_pics.length - 1
				|| currentIndex == positon) {
			return;
		}
		dots[positon].setEnabled(true);
		dots[currentIndex].setEnabled(false);
		currentIndex = positon;
	}

	public class ViewPagerAdapter extends PagerAdapter {

		// 界面列表
		private List<View> views;

		public ViewPagerAdapter(List<View> views) {
			this.views = views;
		}

		// 销毁arg1位置的界面
		@Override
		public void destroyItem(View arg0, int arg1, Object arg2) {
			((ViewPager) arg0).removeView(views.get(arg1));
		}

		@Override
		public void finishUpdate(View arg0) {
			// TODO Auto-generated method stub

		}

		// 获得当前界面数
		public int getCount() {
			if (views != null) {
				return views.size();
			}

			return 0;
		}

		// 初始化arg1位置的界面
		public Object instantiateItem(View arg0, int arg1) {

			((ViewPager) arg0).addView(views.get(arg1));

			return views.get(arg1);
		}

		// 判断是否由对象生成界面
		public boolean isViewFromObject(View arg0, Object arg1) {
			return (arg0 == arg1);
		}

		public void restoreState(Parcelable arg0, ClassLoader arg1) {
			// TODO Auto-generated method stub

		}

		public Parcelable saveState() {
			// TODO Auto-generated method stub
			return null;
		}

		public void startUpdate(View arg0) {
			// TODO Auto-generated method stub

		}

	}

	/**
	 * (非 Javadoc) Title: onPageScrollStateChanged Description:
	 * 
	 * @param arg0
	 * @see android.support.v4.view.ViewPager.OnPageChangeListener#onPageScrollStateChanged(int)
	 */
	public void onPageScrollStateChanged(int arg0) {
		// TODO Auto-generated method stub
	}

	/**
	 * (非 Javadoc) Title: onPageScrolled Description:
	 * 
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 * @see android.support.v4.view.ViewPager.OnPageChangeListener#onPageScrolled(int,
	 *      float, int)
	 */
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		// TODO Auto-generated method stub
	}

	/**
	 * (非 Javadoc) Title: onPageSelected Description:
	 * 
	 * @param arg0
	 * @see android.support.v4.view.ViewPager.OnPageChangeListener#onPageSelected(int)
	 */
	public void onPageSelected(int arg0) {
		// TODO Auto-generated method stub
		/*if(arg0 == pageViews.size()-1){
			viewPager.setCurrentItem (arg0 - 1);
			ToHome();
			return;
		}*/
		
		setCurDot(arg0);
	}

	/**
	 * (非 Javadoc) Title: onClick Description:
	 * 
	 * @param v
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int position = (Integer)v.getTag();
		if(position == GO_BTN){
			ToHome();
		}else{
			setCurView(position);  
			setCurDot(position); 
		}
	}
	
	public void ToHome(){
		/*Intent intentHome = new Intent();
		intentHome.setClass(HelpActivity.this, HomeActivity.class);
        startActivity(intentHome); 
        finish();*/
	}
	
	/** (非 Javadoc) 
	 * Title: onKeyDown
	 * Description:
	 * @param keyCode
	 * @param event
	 * @return 
	 * @see android.app.Activity#onKeyDown(int, android.view.KeyEvent) 
	 */
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		finish();
		return super.onKeyDown(keyCode, event);
	}
}

