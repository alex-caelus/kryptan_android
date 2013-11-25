/**
 * 
 */
package org.caelus.kryptanandroid;

import java.util.Vector;

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
	private Vector<cLabel> mLabels = new Vector<cLabel>();
	private OnCheckedChangeListener mListener;

	private class cLabel
	{
		public cLabel(CharSequence n)
		{
			name = n;
		}

		public CharSequence name;
		public boolean isChecked;
	}

	/**
	 * 
	 */
	public LabelAdapter(Context c)
	{
		mContext = c;
		addTestData();
	}

	private void addTestData()
	{
		for (int i = 0; i < 40; i++)
		{
			mLabels.add(new cLabel("Label " + i));
		}
		mLabels.add(new cLabel("One last really long label"));
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
		int nrOfPasswords = 1; // TODO: get real number
		cLabel label = mLabels.elementAt(arg0);

		checkBox.setTag(label);
		checkBox.setText(label.name);
		checkBox.setChecked(label.isChecked);
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
		cLabel label = (cLabel) buttonView.getTag();
		if (label.isChecked != isChecked)
		{
			label.isChecked = isChecked;
			if (mListener != null)
			{
				mListener.onCheckedChanged(buttonView, isChecked);
			}
		}
		// else nothing has changed
	}

}
