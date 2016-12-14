package com.donote.freestyle;

import com.baidu.mobstat.StatService;
import com.wxl.donote.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class GraphText extends Activity {

	/*
	 * (non-Javadoc)
	 * 
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

	boolean iswrite = false;
	EditText editText;

	ImageButton save;
	ImageButton cancel;

	String lastText = null;
	String content = null;
	boolean crea_cloud = false;
	
	// Ä¬ÈÏÖµ
	int id;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.define_text);

		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		lastText = bundle.getString("text");

		String temp = bundle.getString("cloud");
		if(temp!=null && temp.equals("create")){
			crea_cloud = true;
		}else{
			id = bundle.getInt("id");
		}

		if (lastText == null) {
			iswrite = false;
		}
		if (lastText != null) {
			iswrite = true;
		}

		editText = (EditText) this.findViewById(R.id.note_text);
		save = (ImageButton) this.findViewById(R.id.text_save);
		cancel = (ImageButton) this.findViewById(R.id.text_cancel);


		editText.setText(lastText);
		editText.setSelection(editText.length());

		save.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				textFinish();
			}
		});

		cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				crea_cloud = false;
				iswrite = false;
				editText.setText(lastText);
				textFinish();

			}
		});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onKeyDown(int, android.view.KeyEvent)
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK
				|| keyCode == KeyEvent.KEYCODE_HOME) {
			crea_cloud = false;
			iswrite = false;
			textFinish();
		}

		return super.onKeyDown(keyCode, event);
	}

	private void textFinish() {
		content = editText.getText().toString();
		Bundle bundle = new Bundle();
		bundle.putBoolean("iswrite", iswrite);
		bundle.putString("note", content);
		if(crea_cloud == true){
			bundle.putBoolean("crea_cloud", true);
		}
		else{
			bundle.putInt("id", id);
		}
		GraphText.this.setResult(14,
				GraphText.this.getIntent().putExtras(bundle));
		GraphText.this.finish();
	}

}
