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
	private CoreSecureStringHandler[] mLabels;
	private Vector<Boolean> mChecked = new Vector<Boolean>();
	private OnLabelSelectionChangedListener mListener;
	private CorePwdList mSource;

	interface OnLabelSelectionChangedListener
	{
		void OnLabelSelectionChanged(CoreSecureStringHandler label, boolean isSelected);
	}

	/**
	 * 
	 */
	public LabelAdapter(Context c, CorePwdList src)
	{
		mContext = c;
		mSource = src;
		mLabels = mSource.AllLabels();
		for(int i=0; i<mLabels.length; i++)
		{
			mChecked.add(Boolean.valueOf(false));
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
		CoreSecureStringHandler label = mLabels[arg0];
		//TODO: fix secure viewing of labels
		String labelname = "";
		int length = label.GetLength();
		for(int i=0; i<length; i++)
		{
			labelname += label.GetChar(i);
		}
		int nrOfPasswords = mSource.CountPwds(label); // TODO: get real number
		
		checkBox.setTag(Integer.valueOf(arg0));
		checkBox.setText(labelname);
		checkBox.setChecked(mChecked.get(arg0).booleanValue());
		text.setText(String.format(textFormat, nrOfPasswords));

		return group;
	}

	public void setOnLabelSelectionChangedListener(OnLabelSelectionChangedListener listener)
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

}
