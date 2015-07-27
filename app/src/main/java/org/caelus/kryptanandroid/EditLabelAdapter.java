/**
 * 
 */
package org.caelus.kryptanandroid;

import java.util.Vector;

import org.caelus.kryptanandroid.buildingblocks.SecureTextView;
import org.caelus.kryptanandroid.core.CorePwd;
import org.caelus.kryptanandroid.core.CorePwdList;
import org.caelus.kryptanandroid.core.CoreSecureStringHandler;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * @author Alexander
 * 
 */
public class EditLabelAdapter extends BaseAdapter implements OnClickListener
{
	private Context mContext;
	private CoreSecureStringHandler[] mSelectedLabels;
	private CoreSecureStringHandler[] mAllLabels;
	private Vector<CoreSecureStringHandler> mAvailableLabels;
	private CorePwdList mSource;
	private CorePwd mPwd;

	interface OnLabelSelectionChangedListener
	{
		void OnLabelSelectionChanged(CoreSecureStringHandler label,
				boolean isSelected);
	}

	/**
	 * 
	 */

	public EditLabelAdapter(Activity c, CorePwdList src, CorePwd pwd)
	{
		mContext = c;
		mSource = src;
		mPwd = pwd;
		mAvailableLabels = new Vector<CoreSecureStringHandler>();
		updateAvailableLabels();
	}

	public void updateAvailableLabels()
	{
		mSelectedLabels = mPwd.GetLabels();
		mAllLabels = mSource.AllLabels();
		mAvailableLabels.clear();
		for (CoreSecureStringHandler label : mAllLabels)
		{
			boolean add = true;
			for (CoreSecureStringHandler selected : mSelectedLabels)
			{
				if (label.equals(selected))
				{
					add = false;
					break;
				}
			}

			if (add)
			{
				mAvailableLabels.add(label);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getCount()
	 */
	@Override
	public int getCount()
	{
		return mAvailableLabels.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getItem(int)
	 */
	@Override
	public Object getItem(int index)
	{
		return mAvailableLabels.elementAt(index);
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

	public CoreSecureStringHandler[] getSelectedLabels()
	{
		return mSelectedLabels;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getView(int, android.view.View,
	 * android.view.ViewGroup)
	 */
	@Override
	public View getView(int labelIndex, View convertView, ViewGroup parent)
	{
		LinearLayout group;
		SecureTextView mainText;
		TextView passwordCountText;

		if (convertView == null)
		{
			group = new LinearLayout(mContext);
			mainText = new SecureTextView(mContext);
			passwordCountText = new TextView(mContext);

			// settings
			group.setOrientation(LinearLayout.VERTICAL);

			mainText.setLayoutParams(new LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

			mainText.setTextAppearance(mContext,
					android.R.style.TextAppearance_Medium);
			LinearLayout.LayoutParams lparam = new LinearLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			mainText.setLayoutParams(lparam);
			mainText.setGravity(Gravity.CENTER);

			passwordCountText.setTextAppearance(mContext,
					android.R.style.TextAppearance_Small);
			passwordCountText.setGravity(Gravity.CENTER);

			// add listeners
			// mainText.setOnClickListener(this);
			// passwordCountText.setOnClickListener(this);

			// add views to group
			group.addView(mainText);
			group.addView(passwordCountText);
		} else
		{
			group = (LinearLayout) convertView;
			mainText = (SecureTextView) group.getChildAt(0);
			passwordCountText = (TextView) group.getChildAt(1);
		}

		// make text
		String textFormat = mContext.getResources().getString(
				R.string.label_nr_of_passwords_format);

		CoreSecureStringHandler label = (CoreSecureStringHandler) getItem(labelIndex);

		int nrOfPasswords = mSource.CountPwds(label);

		mainText.setSecureText(label);
		passwordCountText.setText(String.format(textFormat, nrOfPasswords));

		return group;
	}

	@Override
	public void onClick(View v)
	{
		// if a label has been clicked
	}

}
