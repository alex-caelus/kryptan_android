package com.qustom.dialog;

/**
 * This file was originally taken from https://github.com/danoz73/QustomDialog/tree/master/res
 * and modified by Alexander Nilsson
 */

import org.caelus.kryptanandroid.R;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class QustomDialogBuilder extends AlertDialog.Builder
{

	/** The custom_body layout */
	private View mDialogView;

	/** optional dialog title layout */
	private TextView mTitle;
	/** optional alert dialog image */
	private ImageView mIcon;
	/** optional message displayed below title if title exists */
	private TextView mMessage;
	/**
	 * The colored holo divider. You can set its color with the setDividerColor
	 * method
	 */
	private View mDivider;

	private View mCustomView;

	private OnClickListener mItemClickedListener;

	private ListView mList;

	public QustomDialogBuilder(Context context)
	{
		super(context);

		mDialogView = View
				.inflate(context, R.layout.qustom_dialog_layout, null);
		setView(mDialogView);

		mTitle = (TextView) mDialogView.findViewById(R.id.alertTitle);
		mMessage = (TextView) mDialogView.findViewById(R.id.message);
		mIcon = (ImageView) mDialogView.findViewById(R.id.icon);
		mDivider = mDialogView.findViewById(R.id.titleDivider);
	}

	/**
	 * Use this method to color the divider between the title and content. Will
	 * not display if no title is set.
	 * 
	 * @param colorString
	 *            for passing "#ffffff"
	 */
	public QustomDialogBuilder setDividerColor(String colorString)
	{
		mDivider.setBackgroundColor(Color.parseColor(colorString));
		return this;
	}

	@Override
	public QustomDialogBuilder setTitle(CharSequence text)
	{
		mTitle.setText(text);
		return this;
	}

	public QustomDialogBuilder setTitleColor(String colorString)
	{
		mTitle.setTextColor(Color.parseColor(colorString));
		return this;
	}

	@Override
	public QustomDialogBuilder setMessage(int textResId)
	{
		mMessage.setText(textResId);
		mDialogView.findViewById(R.id.contentPanel).setVisibility(View.VISIBLE);
		return this;
	}

	@Override
	public QustomDialogBuilder setMessage(CharSequence text)
	{
		mMessage.setText(text);
		mDialogView.findViewById(R.id.contentPanel).setVisibility(View.VISIBLE);
		return this;
	}

	@Override
	public QustomDialogBuilder setIcon(int drawableResId)
	{
		mIcon.setImageResource(drawableResId);
		return this;
	}

	@Override
	public QustomDialogBuilder setIcon(Drawable icon)
	{
		mIcon.setImageDrawable(icon);
		return this;
	}

	/**
	 * This allows you to specify a custom layout for the area below the title
	 * divider bar in the dialog. As an example you can look at
	 * example_ip_address_layout.xml and how I added it in
	 * TestDialogActivity.java
	 * 
	 * @param resId
	 *            of the layout you would like to add
	 * @param context
	 */
	public QustomDialogBuilder setCustomView(int resId, Context context)
	{
		mCustomView = View.inflate(context, resId, null);
		FrameLayout customGroup = (FrameLayout) mDialogView
				.findViewById(R.id.customPanel);
		customGroup.setVisibility(View.VISIBLE);
		customGroup.addView(mCustomView);
		return this;
	}

	public Builder setCustomView(View view)
	{
		mCustomView = view;
		FrameLayout customGroup = (FrameLayout) mDialogView
				.findViewById(R.id.customPanel);
		customGroup.setVisibility(View.VISIBLE);
		customGroup.addView(mCustomView);
		return this;
	}

	public View getCustomView()
	{
		return mCustomView;
	}
	
	@Override
	public Builder setSingleChoiceItems(ListAdapter adapter, int checkedItem,
			final OnClickListener listener)
	{
		mList = new ListView(getContext());
		
		mList.setAdapter(adapter);
		
		setCustomView(mList);
		
		mItemClickedListener = listener;
		
		return this;
	}

	@Override
	public AlertDialog create()
	{
		onCreate();
		
		AlertDialog dialog = super.create();
		
		onCreated(dialog);
		
		return dialog;
	}

	private void onCreate()
	{
		if (mTitle.getText().equals(""))
			mDialogView.findViewById(R.id.title_template).setVisibility(
					View.GONE);
		if (mMessage.getText().equals(""))
			mDialogView.findViewById(R.id.message).setVisibility(View.GONE);
	}

	private void onCreated(final AlertDialog dialog)
	{
		if(mItemClickedListener != null && mList != null)
		{
			mList.setOnItemClickListener(new OnItemClickListener()
			{

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int position, long arg3)
				{
					mItemClickedListener.onClick(dialog, position);
				}
			});
		}
	}

}
