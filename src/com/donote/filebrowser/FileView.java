package com.donote.filebrowser;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.donote.activity.MainActivity;
import com.wxl.donote.R;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.Toast;

@SuppressLint("HandlerLeak")
public class FileView extends ListActivity {

	private List<IconifiedText> directoryEntries = new ArrayList<IconifiedText>();
	private List<IconifiedText> otherDirectoryEntries = new ArrayList<IconifiedText>();
	private ArrayList<HashMap<String, String>> imageFileList = new ArrayList<HashMap<String, String>>();
	private File currentDirectory = new File("/");
	private int location = 0;
	private IconifiedTextListAdapter itla;
	private Thread imageThread;

	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what)
			{
			case 1:
				int currentPathStringLenght = currentDirectory
						.getAbsolutePath().length();

				if (IconifiedTextListAdapter.itemGroup[msg.arg1] == null)
				{
					otherDirectoryEntries.set(msg.arg1,
							new IconifiedText(msg.getData().getString("path")
									.substring(currentPathStringLenght),
									(Drawable) msg.obj));
				}
				else
				{
					IconifiedTextView view = (IconifiedTextView) IconifiedTextListAdapter.itemGroup[msg.arg1];
					view.setIcon((Drawable) msg.obj);
					otherDirectoryEntries.set(msg.arg1,
							new IconifiedText(msg.getData().getString("path")
									.substring(currentPathStringLenght),
									(Drawable) msg.obj));

				}
				break;
			case 2:
				handler.removeCallbacks(imageThread);
				break;
			}
		}
	};

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		browseToRoot();
		getContentResolver();
		this.setSelection(0);

	}

	// 浏览文件系统的根目录
	private void browseToRoot() {
		browseTo(new File(Environment.getExternalStorageDirectory().getPath()));
		handler.removeCallbacks(imageThread);
	}

	// 返回上一级目录
	private void upOneLevel() {
		if (this.currentDirectory.getParent() != null)
			this.browseTo(this.currentDirectory.getParentFile());
		handler.removeCallbacks(imageThread);
	}

	// 浏览指定的目录,如果是文件则进行打开操作
	private void browseTo(final File file) {
		this.setTitle(file.getAbsolutePath());
		if (file.isDirectory())
		{
			this.currentDirectory = file;
			fill(file.listFiles());
		} else
		{
			fileOptMenu(file);
		}
	}

	// 这里可以理解为设置ListActivity的源
	@SuppressLint("UseSparseArrays")
	private void fill(File[] files) {

		if (files == null)
		{
			this.browseTo(this.currentDirectory.getParentFile());
			return;
		}
		// 清空列表
		this.directoryEntries.clear();
		otherDirectoryEntries.clear();

		// 如果不是根目录则添加上一级目录项
		if (this.currentDirectory.getParent() != null)
			otherDirectoryEntries.add(new IconifiedText(
					getString(R.string.up_one_level), getResources()
							.getDrawable(R.drawable.uponelevel7)));

		Drawable currentIcon = null;
		for (File currentFile : files)
		{
			// 判断是一个文件夹还是一个文件
			if (currentFile.isDirectory())
			{
				currentIcon = getResources().getDrawable(
						R.drawable.ic_file_icon7);
				otherdirectoryEntriesAdd(currentFile, currentIcon,
						otherDirectoryEntries);
			} else
			{
				// 取得文件名
				String fileName = currentFile.getName();
				// 根据文件名来判断文件类型，设置不同的图标
				if (checkEndsWithInStringArray(fileName, getResources()
						.getStringArray(R.array.fileEndingImage)))
				{
					HashMap<String, String> tempMap = new HashMap<String, String>();
					tempMap.put("location", String.valueOf(location));
					tempMap.put("path", currentFile.getAbsolutePath());
					tempMap.put("name", currentFile.getName());
					imageFileList.add(tempMap);
					currentIcon = getResources().getDrawable(R.drawable.image7);
					//isnoDrawable = currentIcon;
					directoryEntriesAdd(currentFile, currentIcon);

				} else if (checkEndsWithInStringArray(fileName, getResources()
						.getStringArray(R.array.fileEndingWebText)))
				{
					currentIcon = getResources()
							.getDrawable(R.drawable.webtext);
					otherdirectoryEntriesAdd(currentFile, currentIcon,
							otherDirectoryEntries);

				} else if (checkEndsWithInStringArray(fileName, getResources()
						.getStringArray(R.array.fileEndingPackage)))
				{
					currentIcon = getResources().getDrawable(R.drawable.packed7);
					otherdirectoryEntriesAdd(currentFile, currentIcon,
							otherDirectoryEntries);
				} else if (checkEndsWithInStringArray(fileName, getResources()
						.getStringArray(R.array.fileEndingAudio)))
				{
					currentIcon = getResources().getDrawable(R.drawable.audio7);
					otherdirectoryEntriesAdd(currentFile, currentIcon,
							otherDirectoryEntries);
				} else if (checkEndsWithInStringArray(fileName, getResources()
						.getStringArray(R.array.fileEndingVideo)))
				{
					currentIcon = getResources().getDrawable(R.drawable.video7);
					otherdirectoryEntriesAdd(currentFile, currentIcon,
							otherDirectoryEntries);
				}  else if (checkEndsWithInStringArray(fileName, getResources()
						.getStringArray(R.array.fileEndingPdf)))
				{
					currentIcon = getResources().getDrawable(R.drawable.ic_icon_pdf);
					otherdirectoryEntriesAdd(currentFile, currentIcon,
							otherDirectoryEntries);
				}  else if (checkEndsWithInStringArray(fileName, getResources()
						.getStringArray(R.array.fileEndingPPT)))
				{
					currentIcon = getResources().getDrawable(R.drawable.ic_icon_ppt);
					otherdirectoryEntriesAdd(currentFile, currentIcon,
							otherDirectoryEntries);
				}  else if (checkEndsWithInStringArray(fileName, getResources()
						.getStringArray(R.array.fileEndingWord)))
				{
					currentIcon = getResources().getDrawable(R.drawable.ic_icon_word);
					otherdirectoryEntriesAdd(currentFile, currentIcon,
							otherDirectoryEntries);
				}  else if (checkEndsWithInStringArray(fileName, getResources()
						.getStringArray(R.array.fileEndingExcel)))
				{
					currentIcon = getResources().getDrawable(R.drawable.ic_icon_excel);
					otherdirectoryEntriesAdd(currentFile, currentIcon,
							otherDirectoryEntries);
				} 
				else
				{
					currentIcon = getResources().getDrawable(R.drawable.ic_unknown);
					otherdirectoryEntriesAdd(currentFile, currentIcon,
							otherDirectoryEntries);
				}
			}

		}
		itla = new IconifiedTextListAdapter(this);
		// 将表设置到ListAdapter中
		location = 0;
		otherDirectoryEntries.size();
		otherDirectoryEntries.addAll(directoryEntries);
		itla.setListItems(otherDirectoryEntries);
		// 为ListActivity添加一个ListAdapter
		// imageThread = new Thread(new imageLoadThread());
		// imageThread.start();// 启动线程
		this.setListAdapter(itla);
	}

	public void directoryEntriesAdd(File currentFile, Drawable currentIcon) {
		int currentPathStringLenght = this.currentDirectory.getAbsolutePath()
				.length();
		directoryEntries.add(location, new IconifiedText(currentFile
				.getAbsolutePath().substring(currentPathStringLenght+1),
				currentIcon));
		location++;
	}

	public void otherdirectoryEntriesAdd(File currentFile,
			Drawable currentIcon, List<IconifiedText> otherDirectoryEntries) {
		int currentPathStringLenght = this.currentDirectory.getAbsolutePath()
				.length();
		otherDirectoryEntries.add(new IconifiedText(currentFile
				.getAbsolutePath().substring(currentPathStringLenght+1),
				currentIcon));
	}

	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		// 取得选中的一项的文件名
		String selectedFileString = otherDirectoryEntries.get(position)
				.getText();

		if (selectedFileString.equals(getString(R.string.current_dir)))
		{
			// 如果选中的是刷新
			this.browseTo(this.currentDirectory);
		} else if (selectedFileString.equals(getString(R.string.up_one_level)))
		{
			// 返回上一级目录
			this.upOneLevel();
		} else
		{
			File clickedFile = null;
			clickedFile = new File(this.currentDirectory.getAbsolutePath()
					+"/"+ otherDirectoryEntries.get(position).getText());
			if (clickedFile != null)
				this.browseTo(clickedFile);
		}
	}

	// 通过文件名判断是什么类型的文件
	private boolean checkEndsWithInStringArray(String checkItsEnd,
			String[] fileEndings) {
		for (String aEnd : fileEndings)
		{
			if (checkItsEnd.endsWith(aEnd))
				return true;
		}
		return false;
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(0, 3, 0, getResources().getString(R.string.root_dir)).setIcon(R.drawable.goroot);
		menu.add(0, 4, 0, getResources().getString(R.string.up_one_level)).setIcon(R.drawable.uponelevel7);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		switch (item.getItemId())
		{
		case 3:
			this.browseToRoot();
			break;
		case 4:
			this.upOneLevel();
			break;
		}
		return false;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		return super.onPrepareOptionsMenu(menu);
	}

	// 处理文件
	public void fileOptMenu(final File file) {

		new AlertDialog.Builder(FileView.this)
				.setTitle(getResources().getString(R.string.choose_file))
				.setMessage(getResources().getString(R.string.choose_file_confirm))
				.setPositiveButton(getResources().getString(R.string.confirm), new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						Toast.makeText(FileView.this,
								getResources().getString(R.string.choose_file2) + file.getAbsolutePath(),
								Toast.LENGTH_SHORT).show();

						Bundle bundle = new Bundle();
						bundle.putString("file", file.getAbsolutePath());
						Intent intent = new Intent();
						intent.putExtras(bundle);
						setResult(RESULT_OK, intent);
						FileView.this.finish();

					}
				})
				.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						Toast.makeText(FileView.this, getResources().getString(R.string.cancel_choose),
								Toast.LENGTH_SHORT).show();

					}
				}).show();
	}

	// 得到当前目录的绝对路劲
	public String GetCurDirectory() {
		return this.currentDirectory.getAbsolutePath();
	}

	// 移动文件
	public void moveFile(String source, String destination) {
		new File(source).renameTo(new File(destination));
	}

	/*// 得到图像文件缩略图
	public static Bitmap getBitmap(ContentResolver cr, String fileName) {
		Bitmap bitmap = null;
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inDither = false;
		options.inPreferredConfig = Bitmap.Config.ARGB_8888;
		// select condition.
		String whereClause = MediaStore.Images.Media.DATA + " = '" + fileName
				+ "'";

		// colection of results.
		Cursor cursor = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
				new String[] { MediaStore.Images.Media._ID }, whereClause,
				null, null);
		if (cursor == null || cursor.getCount() == 0)
		{
			if (cursor != null)
				cursor.close();
			return null;
		}
		cursor.moveToFirst();
		// image id in image table.
		String videoId = cursor.getString(cursor
				.getColumnIndex(MediaStore.Images.Media._ID));
		cursor.close();
		if (videoId == null)
		{
			return null;
		}
		long videoIdLong = Long.parseLong(videoId);
		// via imageid get the bimap type thumbnail in thumbnail table.
		bitmap = MediaStore.Images.Thumbnails.getThumbnail(cr, videoIdLong,
				Images.Thumbnails.MINI_KIND, options);
		return bitmap;
	}

	// 图片刷新线程
	class imageLoadThread implements Runnable {
		public void run() {
			// 判断Photo

			for (int i = 0; i < imageFileList.size(); i++)
			{
				int Location = Integer.valueOf(imageFileList.get(i).get(
						"location"))
						+ Count;
				String filePath = imageFileList.get(i).get("path");

				@SuppressWarnings("deprecation")
				Drawable currentIcon = new BitmapDrawable(
						showIcon.readBitmapAutoSize(filePath, Edit.Width / 5,
								Edit.Height / 5));
				Message message = new Message();
				message.what = 1;

				message.arg1 = Location;
				Bundle bundle = new Bundle();
				bundle.putString("path", filePath);
				message.setData(bundle);
				message.obj = currentIcon;
				handler.sendMessage(message);
				System.gc();
			}
			imageFileList.clear();
			Message message = new Message();
			message.what = 2;
			handler.sendMessage(message);
		}*/
		/*
		 * try { Thread.sleep(500); } catch (InterruptedException e) {
		 * Thread.currentThread().interrupt(); } for (int i = 0; i <
		 * IconifiedTextListAdapter.itemGroup.length &&
		 * itla.getchidView(i).getIcon().equals(isnoDrawable); i++) {
		 * Log.i("image", "fuckenter"); File clickedFile = null; clickedFile =
		 * new File(currentDirectory.getAbsolutePath() +
		 * itla.getchidView(i).getName());
		 * 
		 * String filePath = clickedFile.getAbsolutePath(); Bitmap bitmap =
		 * showIcon.readBitmapAutoSize(filePath, Edit.Width / 5, Edit.Height /
		 * 5); Drawable currentIcon = new BitmapDrawable(bitmap); Message
		 * message = new Message(); message.what = 1; // Bundle bundle = new
		 * Bundle(); // bundle.putString("path", filePath); //
		 * message.setData(bundle); message.arg1 = i; message.obj = currentIcon;
		 * handler.sendMessage(message); } Message message = new Message();
		 * message.what = 2; handler.sendMessage(message);
		 */

	//}

	// 判断Photo
	/*
	 * for (int i = 0; i < imageFileList.size() && i < itla.getchidViewCount();
	 * i++) { int Location = Integer.valueOf(imageFileList.get(i).get(
	 * "location")); String filePath = imageFileList.get(i).get("path");
	 * 
	 * Bitmap bitmap = showIcon.readBitmapAutoSize(filePath, Edit.Width / 5,
	 * Edit.Height / 5); Drawable currentIcon = new BitmapDrawable(bitmap);
	 * Message message = new Message(); message.what = 1; message.arg1 =
	 * Location; Bundle bundle = new Bundle(); bundle.putString("path",
	 * filePath); message.setData(bundle); message.obj = currentIcon;
	 * handler.sendMessage(message); } Message message = new Message();
	 * message.what = 2; handler.sendMessage(message); }
	 */

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#finish()
	 */
	@Override
	public void finish() {
		// TODO Auto-generated method stub
		handler.removeCallbacks(imageThread);
		super.finish();
	}

}
