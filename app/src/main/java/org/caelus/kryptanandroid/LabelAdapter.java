/**
 * 
 */
package org.caelus.kryptanandroid;

import java.util.Vector;

import org.caelus.kryptanandroid.core.CorePwdList;
import org.caelus.kryptanandroid.core.CoreSecureStringHandler;
import org.caelus.kryptanandroid.buildingblocks.SecureTextView;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * @author Alexander
 * 
 */
public class LabelAdapter extends BaseAdapter implements
		OnCheckedChangeListener, OnClickListener
{
	private Context mContext;
	private CoreSecureStringHandler[] mLabels;
	private Vector<Boolean> mChecked = new Vector<Boolean>();
	private OnLabelSelectionChangedListener mListener;
	private CorePwdList mSource;

	interface OnLabelSelectionChangedListener
	{
		void OnLabelSelectionChanged(CoreSecureStringHandler label,
				boolean isSelected);
	}

	/**
	 * 
	 */
	public LabelAdapter(Context c, CorePwdList src)
	{
		mContext = c;
		mSource = src;
		refreshData();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getCount()
	 */
	@Override
	public int getCount()
	{
		return mLabels.length;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getItem(int)
	 */
	@Override
	public Object getItem(int arg0)
	{
		return mLabels[arg0];
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getItemId(int)
	 */
	@Override
	public long getItemId(int arg0)
	{
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getView(int, android.view.View,
	 * android.view.ViewGroup)
	 */
	@Override
	public View getView(int arg0, View convertView, ViewGroup parent)
	{
		LinearLayout group;
		CheckBox checkBox;
		SecureTextView mainText;
		TextView passwordCountText;

		if (convertView == null)
		{
			group = new LinearLayout(mContext);
			LinearLayout checkLayout = new LinearLayout(mContext);
			checkBox = new CheckBox(mContext);
			mainText = new SecureTextView(mContext);
			passwordCountText = new TextView(mContext);

			// settings
			group.setOrientation(LinearLayout.VERTICAL);
			checkLayout.setOrientation(LinearLayout.HORIZONTAL);
			mainText.setLayoutParams(new LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

			checkBox.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);

			mainText.setTextAppearance(mContext,
					android.R.style.TextAppearance_Medium);
			LinearLayout.LayoutParams lparam = new LinearLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			mainText.setLayoutParams(lparam);
			mainText.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);

			passwordCountText.setTextAppearance(mContext,
					android.R.style.TextAppearance_Small);
			passwordCountText.setPadding(checkBox.getCompoundPaddingLeft(),
					passwordCountText.getPaddingTop(),
					passwordCountText.getPaddingRight(),
					passwordCountText.getPaddingBottom());

			// add listener
			checkBox.setOnCheckedChangeListener(this);
			mainText.setOnClickListener(this);
			passwordCountText.setOnClickListener(this);

			// add views to group
			checkLayout.addView(checkBox);
			checkLayout.addView(mainText);
			group.addView(checkLayout);
			group.addView(passwordCountText);
		} else
		{
			group = (LinearLayout) convertView;
			checkBox = (CheckBox) ((LinearLayout) group.getChildAt(0))
					.getChildAt(0);
			mainText = (SecureTextView) ((LinearLayout) group.getChildAt(0))
					.getChildAt(1);
			passwordCountText = (TextView) group.getChildAt(1);
		}

		// make text
		String textFormat = mContext.getResources().getString(
				R.string.label_nr_of_passwords_format);

		CoreSecureStringHandler label = mLabels[arg0];

		int nrOfPasswords = mSource.CountPwds(label);

		checkBox.setTag(Integer.valueOf(arg0));
		checkBox.setChecked(mChecked.get(arg0).booleanValue());
		mainText.setSecureText(label);
		mainText.setTag(checkBox);
		passwordCountText.setTag(checkBox);
		passwordCountText.setText(String.format(textFormat, nrOfPasswords));

		return group;
	}

	public void setOnLabelSelectionChangedListener(
			OnLabelSelectionChangedListener listener)
	{
		mListener = listener;
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
	{
		int label = (Integer) buttonView.getTag();
		if (mChecked.get(label).booleanValue() != isChecked)
		{
			mChecked.set(label, Boolean.valueOf(isChecked));
			if (mListener != null)
			{
				mListener.OnLabelSelectionChanged(mLabels[label], isChecked);
			}
		}
		// else nothing has changed
	}

	public void refreshData()
	{
		mLabels = mSource.AllLabels();
		mChecked.clear();
		for (int i = 0; i < mLabels.length; i++)
		{
			mChecked.add(Boolean.valueOf(false));
		}
		notifyDataSetChanged();
	}

	@Override
	public void onClick(View arg0)
	{
		Object obj = (CheckBox) arg0.getTag();
		if (obj instanceof View)
		{
			View checkbox = (View) obj;
			checkbox.performClick();
		}
	}

}
