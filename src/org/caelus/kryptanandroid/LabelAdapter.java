/**
 * 
 */
package org.caelus.kryptanandroid;

import java.util.Vector;

import org.caelus.kryptanandroid.core.*;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
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
        OnCheckedChangeListener
{
	private Context mContext;
	private Vector<CoreSecureStringHandler> mLabels = new Vector<CoreSecureStringHandler>();
	private Vector<Boolean> mChecked = new Vector<Boolean>();
	private OnCheckedChangeListener mListener;
	private CorePwdList mSource;



	/**
	 * 
	 */
	public LabelAdapter(Context c, CorePwdList src)
	{
		mContext = c;
		mSource = src;
		mLabels = mSource.allLabels();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getCount()
	 */
	@Override
	public int getCount()
	{
		return mLabels.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getItem(int)
	 */
	@Override
	public Object getItem(int arg0)
	{
		return mLabels.elementAt(arg0);
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
		TextView text;

		if (convertView == null)
		{
			group = new LinearLayout(mContext);
			checkBox = new CheckBox(mContext);
			text = new TextView(mContext);

			// settings
			group.setOrientation(LinearLayout.VERTICAL);
			text.setTextAppearance(mContext,
			        android.R.style.TextAppearance_Small);
			text.setPadding(checkBox.getCompoundPaddingLeft(), text.getPaddingTop(), text.getPaddingRight(), text.getPaddingBottom());
			
			

			// add listener
			checkBox.setOnCheckedChangeListener(this);

			// add views to group
			group.addView(checkBox);
			group.addView(text);
		} else
		{
			group = (LinearLayout) convertView;
			checkBox = (CheckBox) group.getChildAt(0);
			text = (TextView) group.getChildAt(1);
		}

		// make text
		String textFormat = mContext.getResources().getString(
		        R.string.label_nr_of_passwords_format);
		int nrOfPasswords = mSource.CountPwds(mLabels.get(arg0)); // TODO: get real number
		
		checkBox.setTag(Integer.valueOf(arg0));
		checkBox.setText(mLabels.get(arg0).GetChar(0));
		checkBox.setChecked(mChecked.get(arg0).booleanValue());
		text.setText(String.format(textFormat, nrOfPasswords));

		return group;
	}

	public void setOnCheckedChangeListener(OnCheckedChangeListener listener)
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
				mListener.onCheckedChanged(buttonView, isChecked);
			}
		}
		// else nothing has changed
	}

}
