package com.donote.activity;


import com.baidu.mobstat.StatService;
import com.wxl.donote.R;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

public class TableText extends Activity {

	
	
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
	EditText table_text;

	Bundle extrasBundle = null;

	String text = "";
	int id;
	
	

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		
		
		setContentView(R.layout.table_text);
		
		table_text = (EditText) this.findViewById(R.id.table_text);
		confirm = (ImageButton) this.findViewById(R.id.table_save);
		cancel = (ImageButton) this.findViewById(R.id.table_cancel);

		confirm.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Bundle bundle = new Bundle();

				bundle.putString("text", table_text.getText().toString());
				bundle.putInt("id", id);

				TableText.this.setResult(14, TableText.this.getIntent()
						.putExtras(bundle));
				
				TableText.this.finish();

			}
		});

		cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Bundle bundle = new Bundle();

				bundle.putString("text", text);
				bundle.putInt("id", id);

				TableText.this.setResult(14, TableText.this.getIntent()
						.putExtras(bundle));
				
				TableText.this.finish();
				
			}
		});

		extrasBundle = getIntent().getExtras();

		if (extrasBundle != null) {
			text = extrasBundle.getString("text");
			id = extrasBundle.getInt("id");
			table_text.setText(text);
			table_text.setSelection(text.length());
		}

		
		super.onCreate(savedInstanceState);
	}

}
