package com.donote.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import com.baidu.mobstat.StatService;
import com.donote.imagehandler.ImageFileCache;
import com.donote.imagehandler.ImageHandle;
import com.donote.imagehandler.ImageMemoryCache;
import com.donote.util.ShowIcon;
import com.wxl.donote.R;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.Button;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class MainBackground extends Activity {
	
	GridView gridview;
	
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


	public static String pathPicture = Environment
			.getExternalStorageDirectory().getPath()
			+ "/"
			+ "DoNote"
			+ "/"
			+ "picture" + "/";

	public static int[] Imgs_back = new int[] { R.drawable.ic_back_a,
			R.drawable.ic_back_b, R.drawable.ic_back_c, R.drawable.ic_back_d,
			R.drawable.ic_back_e, R.drawable.ic_back_f };
	public static String[] Imgs_text = new String[] { "ºìÉ«", "»ÒÉ«", "À¶É«", "ÂÌÉ«",
			"»ÆÉ«", "×ÏÉ«" };
	
	Button backButton;
	

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_background);
		gridview = (GridView) this.findViewById(R.id.gridview);

		ArrayList<HashMap<String, Object>> lstImageItem = new ArrayList<HashMap<String, Object>>();
		
		for (int i = 0; i < 6; i++) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("ItemImage", Imgs_back[i]);
			lstImageItem.add(map);
		}

		SimpleAdapter saImageItems = new SimpleAdapter(this, lstImageItem,
				R.layout.background_view,
				new String[] { "ItemImage" },
				new int[] { R.id.ItemImage });
		gridview.setAdapter(saImageItems);

		gridview.setOnItemClickListener(new ItemClickListener());
		
		backButton = (Button) this.findViewById(R.id.back_button);
		backButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String status = Environment.getExternalStorageState();
				if (status.equals(Environment.MEDIA_MOUNTED))
				{
					Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
					intent.setType("image/*");
					if (intent != null)
					{
						startActivityForResult(intent, 49);
					}
				} else
				{
					Toast.makeText(MainBackground.this, getResources().getString(R.string.without_disk), Toast.LENGTH_SHORT)
							.show();
				}//±³¾°Í¼Æ¬
			}
		});

	}

	class ItemClickListener implements OnItemClickListener {
		public void onItemClick(AdapterView<?> arg0,// The AdapterView where the
													// click happened
				View arg1,// The view within the AdapterView that was clicked
				int arg2,// The position of the view in the adapter
				long arg3// The row id of the item that was clicked
		) {

			@SuppressWarnings("unchecked")
			HashMap<String, Object> item=(HashMap<String, Object>)arg0.getItemAtPosition(arg2);  
		  
			Bitmap bitmap =  BitmapFactory.decodeResource(getResources(),
	              Integer.valueOf(item.get("ItemImage").toString())).copy(Bitmap.Config.ARGB_8888, true);
			
			Bitmap temp  = ShowIcon.zoomBitmapTospe(bitmap,MainActivity.Width, MainActivity.Height);
			
			ImageMemoryCache.addBitmapToCache((long)-1, temp);//±³¾°Í¼Æ¬µÄ»º´æ
			
			ImageFileCache.deleteFile("beijing");
		
			Toast.makeText(MainBackground.this,  getResources().getString(R.string.modify_success), Toast.LENGTH_SHORT).show();
			
		}
	}
	

	/* (non-Javadoc)
	 * @see android.app.Activity#onActivityResult(int, int, android.content.Intent)
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if(requestCode == 49 && resultCode!= RESULT_CANCELED){
			
			Uri pictureUri = data.getData();
			String[] proj = { MediaStore.Images.Media.DATA };
			@SuppressWarnings("deprecation")
			Cursor cursor = managedQuery(pictureUri, proj, null, null, null);
			cursor.moveToFirst();
			Intent intent = new Intent(this, ImageHandle.class);
			intent.putExtra("filepath", cursor.getString(cursor
					.getColumnIndex(MediaStore.Images.Media.DATA)));
			startActivityForResult(intent, 50);
		}
		if(requestCode == 50 && requestCode != RESULT_CANCELED){
			
			String filePath = data.getExtras().getString("filepath");
			if (filePath.startsWith(getExternalCacheDir().getAbsolutePath()))
			{
				File oldFile = new File(filePath);
				File dir = new File(pathPicture);
				if (!dir.exists())
					dir.mkdirs();
				filePath = pathPicture + oldFile.getName();
				File newFile = new File(filePath);
				com.donote.imagehandler.ImageHandleGlobal.CopyFile(oldFile, newFile);
				filePath = newFile.getAbsolutePath();
			}
			Bitmap temp = com.donote.util.ShowIcon.readBitmapAutoSize(filePath,
					MainActivity.Width, MainActivity.Height,
					com.donote.util.ShowIcon.readPictureDegree(filePath));
			if(temp != null) {
				temp = ShowIcon.zoomBitmapTospe(temp, MainActivity.Width, MainActivity.Height);
				ImageMemoryCache.addBitmapToCache((long)-1, temp);//±³¾°Í¼Æ¬µÄ»º´æ
				if (!ImageMemoryCache.fileCache.isExitUri(filePath))
				{
					ImageMemoryCache.fileCache.saveBitmap(temp, "beijing");
				}
			}
			Toast.makeText(MainBackground.this,getResources().getString(R.string.modify_success), Toast.LENGTH_SHORT).show();
			setResult(RESULT_OK);
			finish();
		}
		
		super.onActivityResult(requestCode, resultCode, data);
	}


	/* (non-Javadoc)
	 * @see android.app.Activity#finish()
	 */
	@Override
	public void finish() {
		this.setResult(RESULT_OK);
		super.finish();
	}
	
}
