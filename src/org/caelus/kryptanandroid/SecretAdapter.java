/**
 * 
 */
package org.caelus.kryptanandroid;

import java.util.ArrayList;
import java.util.Locale;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

/**
 * @author Alexander
 * 
 */
public class SecretAdapter extends BaseAdapter implements Filterable
{
	private Context mContext;
	private ArrayList<cSecret> mFilteredSecrets = new ArrayList<cSecret>();
	private SecretFilter mFilter;

	public class cSecret
	{
		public cSecret(String n)
		{
			name = n;
		}

		public String name;
	}

	/**
	 * 
	 */
	public SecretAdapter(Context context)
	{
		mContext = context;

		mFilteredSecrets = getTestContent();
	}

	private ArrayList<cSecret> getTestContent()
	{
		ArrayList<cSecret> mFilteredSecrets = new ArrayList<cSecret>();

		// TODO Auto-generated method stub
		for (int i = 0; i < 143; i++)
		{
			mFilteredSecrets.add(new cSecret("Password " + i));
		}

		return mFilteredSecrets;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getCount()
	 */
	@Override
	public int getCount()
	{
		// TODO Auto-generated method stub
		return mFilteredSecrets.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getItem(int)
	 */
	@Override
	public Object getItem(int arg0)
	{
		// TODO Auto-generated method stub
		return mFilteredSecrets.get(arg0).name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getItemId(int)
	 */
	@Override
	public long getItemId(int arg0)
	{
		// TODO Auto-generated method stub
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getView(int, android.view.View,
	 * android.view.ViewGroup)
	 */
	@Override
	public View getView(int arg0, View convertView, ViewGroup arg2)
	{
		TextView text;

		if (convertView == null)
		{
			LayoutInflater inflater = LayoutInflater.from(mContext);
			text = (TextView) inflater.inflate(
			        android.R.layout.simple_list_item_activated_1, null);
		} else
		{
			text = (TextView) convertView;
		}

		cSecret secret = mFilteredSecrets.get(arg0);

		text.setText(secret.name);
		text.setTag(secret.name);
		return text;
	}

	@Override
	public Filter getFilter()
	{
		if (mFilter == null)
			mFilter = new SecretFilter(getTestContent());
		return mFilter;
	}

	private class SecretFilter extends Filter
	{

		private ArrayList<cSecret> mOriginalSet;

		/**
		 * @param originalSet
		 * 
		 */
		public SecretFilter(ArrayList<cSecret> originalSet)
		{
			mOriginalSet = originalSet;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.widget.Filter#performFiltering(java.lang.CharSequence)
		 */
        @Override
		protected FilterResults performFiltering(CharSequence constraint)
		{
			FilterResults results = new FilterResults();

			if (constraint == null || constraint.length() == 0)
			{
				// no constraint specified, we return the entire list
				results.values = mOriginalSet;
				results.count = mOriginalSet.size();
			} else
			{
				// now do the filter
				ArrayList<cSecret> filtered = new ArrayList<SecretAdapter.cSecret>();

				for (cSecret secret : mOriginalSet)
				{
					if (secret.name.toUpperCase(Locale.getDefault()).contains(
					        constraint.toString().toUpperCase(
					                Locale.getDefault())))
					{
						filtered.add(secret);
					}
				}

				results.values = filtered;
				results.count = filtered.size();
			}

			return results;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.widget.Filter#publishResults(java.lang.CharSequence,
		 * android.widget.Filter.FilterResults)
		 */
		@SuppressWarnings("unchecked")
		@Override
		protected void publishResults(CharSequence constraint,
		        FilterResults results)
		{
			mFilteredSecrets = (ArrayList<cSecret>) results.values;
			if (results.count == 0)
			{
				notifyDataSetInvalidated();
			} else
			{
				notifyDataSetChanged();
			}
		}
	}
}
