/**
 * 
 */
package org.caelus.kryptanandroid;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Locale;

import org.caelus.kryptanandroid.buildingblocks.SecureTextView;
import org.caelus.kryptanandroid.core.CorePwd;
import org.caelus.kryptanandroid.core.CorePwdFile;
import org.caelus.kryptanandroid.core.CoreSecureStringHandler;
import org.caelus.kryptanandroid.core.CoreSecureStringHandlerCollection;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;

/**
 * @author Alexander
 * 
 */
public class SecretAdapter extends BaseAdapter implements Filterable
{
	private Context mContext;
	private ArrayList<CorePwd> mFilteredSecrets = new ArrayList<CorePwd>();
	private SecretFilter mFilter;
	private CoreSecureStringHandlerCollection mLabelsFilter;
	private CorePwdFile mCorePwdFile;

	/**
	 * @param mLabelsFilter 
	 * @param mCorePwdFile 
	 * 
	 */
	public SecretAdapter(Context context, CorePwdFile corePwdFile, CoreSecureStringHandlerCollection labelsFilter)
	{
		mContext = context;
		mCorePwdFile = corePwdFile;
		mLabelsFilter = labelsFilter;
		refreshData();
	}
	
	public void refreshData()
	{
		mFilteredSecrets = mCorePwdFile.getPasswordList().filter(mLabelsFilter);
		notifyDataSetChanged();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getCount()
	 */
	@Override
	public int getCount()
	{
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
		return mFilteredSecrets.get(arg0).GetDescriptionCopy().GetByte(0);
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
	public View getView(int arg0, View convertView, ViewGroup arg2)
	{
		SecureTextView text;

		if (convertView == null)
		{
			LayoutInflater inflater = LayoutInflater.from(mContext);
			text = (SecureTextView) inflater.inflate(
			        R.layout.simple_list_item_activated_secure, null);
		} else
		{
			text = (SecureTextView) convertView;
		}

		CorePwd secret = mFilteredSecrets.get(arg0);
		CoreSecureStringHandler descHandler = secret.GetDescriptionCopy(); 
		text.setSecureText(descHandler);
		text.setTag(secret);
		return text;
	}

	@Override
	public Filter getFilter()
	{
		if (mFilter == null)
			mFilter = new SecretFilter(mFilteredSecrets);
		return mFilter;
	}

	private class SecretFilter extends Filter
	{

		private ArrayList<CorePwd> mOriginalSet;

		/**
		 * @param originalSet
		 * 
		 */
		public SecretFilter(ArrayList<CorePwd> originalSet)
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
				ArrayList<CorePwd> filtered = new ArrayList<CorePwd>();

				for (CorePwd secret : mOriginalSet)
				{
					//TODO: make secure text comparison
					String desc = "";
					String descUpper = "";
					CoreSecureStringHandler descHandler = secret.GetDescriptionCopy(); 
					int l = descHandler.GetLength();
					byte[] encoded = new byte[l];
					for(int i=0; i < l; i++)
					{
						encoded[i] = descHandler.GetByte(i);
					}
					
					try
					{
						desc = new String(encoded, "UTF-8");
					} catch (UnsupportedEncodingException e)
					{
						desc = "";
					}
					
					descUpper = desc.toUpperCase(Locale.getDefault());
					
					if (descUpper.contains(
					        constraint.toString().toUpperCase(
					                Locale.getDefault())))
					{
						filtered.add(secret);
					}

					CoreSecureStringHandler.overwriteStringInternalArr(desc);
					CoreSecureStringHandler.overwriteStringInternalArr(descUpper);
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
			mFilteredSecrets = (ArrayList<CorePwd>) results.values;
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
