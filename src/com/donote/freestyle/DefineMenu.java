package com.donote.freestyle;

import com.baidu.mobstat.StatService;
import com.wxl.donote.R;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class DefineMenu extends Activity {

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	
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

	boolean isdelete = false;
	boolean isedit = false;
	boolean isshadow = false;
	
	TextView delete;
	TextView edit;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.define_menu);
		
		delete = (TextView) this.findViewById(R.id.define_delete);
		edit = (TextView) this.findViewById(R.id.define_fix);
		
		
		delete.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				isdelete = true;
				DefineMenu.this.finish();
			}
		});
		
		edit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				isedit = true;
				DefineMenu.this.finish();
			}
		});
		
		super.onCreate(savedInstanceState);
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#finish()
	 */
	@Override
	public void finish() {
		Bundle bundle = new Bundle();
		bundle.putBoolean("isdelete", isdelete);
		bundle.putBoolean("isedit", isedit);
		bundle.putBoolean("isshadow", isshadow);
		DefineMenu.this.setResult(56, DefineMenu.this.getIntent()
				.putExtras(bundle));
		super.finish();
	}
	
	

}
