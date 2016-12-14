package com.donote.freestyle;

import com.baidu.mobstat.StatService;
import com.wxl.donote.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class DefineShape extends Activity {

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
		// TODO Auto-generated method stub
		StatService.onResume(this);
		super.onResume();
	}


	ImageButton confirm;
	ImageButton cancel;
	
	RelativeLayout relativelayout;
	GraphMove touchView;
	boolean isOK;
	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.define_shape);
		relativelayout = (RelativeLayout) this.findViewById(R.id.text_shape);
	
		touchView = new GraphMove(this);
		relativelayout.addView(touchView);
		
		confirm = (ImageButton) this.findViewById(R.id.shape_confirm);
		cancel = (ImageButton) this.findViewById(R.id.shape_cancel);
		
		cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				isOK = false;
				DefineShape.this.finish();
			}
		});
		
		confirm.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				isOK = true;
				DefineShape.this.finish();
			}
		});
	}
	
	

	/* (non-Javadoc)
	 * @see android.app.Activity#onKeyDown(int, android.view.KeyEvent)
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		 if(keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_HOME){
	            isOK = false;
	            DefineShape.this.finish();
	        }
		return super.onKeyDown(keyCode, event);
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#finish()
	 */
	@Override
	public void finish() {
		
		touchView.Definefigure();
		Bundle bundle = new Bundle();
		bundle.putBoolean("isOK", isOK );
		bundle.putBoolean("isCircle", touchView.isCircle());
		bundle.putBoolean("isRect", touchView.isRect());
		bundle.putBoolean("isTable", touchView.isTable());
		bundle.putInt("rows", touchView.getRows());
		bundle.putInt("columns", touchView.getColumns());
		bundle.putFloat("Radius", touchView.radius);
		bundle.putFloat("Width", touchView.width);
		bundle.putFloat("Height", touchView.height);
		DefineShape.this.setResult(14,
				DefineShape.this.getIntent().putExtras(bundle));
		super.finish();
	}

}
