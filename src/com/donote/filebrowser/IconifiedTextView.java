package com.donote.filebrowser;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class IconifiedTextView extends LinearLayout
{
	//һ���ļ������ļ�����ͼ��
	//����һ����ֱ���Բ���
	private TextView	mText	= null;
	private ImageView	mIcon	= null;
	Drawable drawable;
	String name;
	public IconifiedTextView(Context context, IconifiedText aIconifiedText) 
	{
		super(context);
		//���ò��ַ�ʽ
		this.setOrientation(HORIZONTAL);
		mIcon = new ImageView(context);
		name = aIconifiedText.getText();
		//����ImageViewΪ�ļ���ͼ��
		drawable = aIconifiedText.getIcon();
		mIcon.setImageDrawable(aIconifiedText.getIcon());
		//����ͼ���ڸò����е����λ��
		mIcon.setPadding(6, 0, 6, 0); 
		//��ImageView��ͼ����ӵ��ò�����
		addView(mIcon,  new LinearLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		//�����ļ�������䷽ʽ�������С
		mText = new TextView(context);
		mText.setText(aIconifiedText.getText());
		mText.setPadding(4, 10, 4, 6); 
		mText.setTextSize(18);
		mText.setWidth(LayoutParams.WRAP_CONTENT);
		mText.setHeight(LayoutParams.WRAP_CONTENT);
		mText.setSingleLine(true);
		//���ļ�����ӵ�������
		addView(mText, new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
	}
	//�����ļ���
	public void setText(String words)
	{
		mText.setText(words);
	}
	//����ͼ��
	public void setIcon(Drawable bullet)
	{
		mIcon.setImageDrawable(bullet);
	}
	
	public Drawable getIcon()
	{
		return drawable ;
	}
	
	public String getName()
	{
		return name ;
	}
}

