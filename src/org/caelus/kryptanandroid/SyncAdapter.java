package org.caelus.kryptanandroid;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

import org.caelus.kryptanandroid.buildingblocks.ConflictChoice;
import org.caelus.kryptanandroid.buildingblocks.SyncItem;
import org.caelus.kryptanandroid.core.CorePwd;
import org.caelus.kryptanandroid.core.CorePwdFile;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;

public class SyncAdapter extends BaseAdapter
{
	private CorePwdFile mLocalFile;
	private CorePwdFile mRemoteFile;
	private ArrayList<ConflictChoice> mSyncItems;
	private Context mContext;
	private long mLastSync;

	public SyncAdapter(Context context, long lastSync, CorePwdFile localFile, CorePwdFile remoteFile)
	{
		mContext = context;
		mLastSync = lastSync;
		mLocalFile = localFile;
		mRemoteFile = remoteFile;
		createSyncItemList();
	}

	private void createSyncItemList()
	{
		mSyncItems = new ArrayList<ConflictChoice>();
		ArrayList<CorePwd> localArray = mLocalFile.getPasswordList().all();
		ArrayList<CorePwd> remoteArray = mRemoteFile.getPasswordList().all(); 

		for (CorePwd currRemote : remoteArray)
		{
			CorePwd localMatch = null;
			for (CorePwd currLocal : localArray)
			{
				if(currRemote.GetTimeCreated() == currLocal.GetTimeCreated())
				{
					localMatch = currLocal;
					localArray.remove(localMatch);
					break;
				}
			}
			
			mSyncItems.add(new ConflictChoice(mLastSync, localMatch, currRemote));
		}
		//and add the locals that are left and have not been paired up yet
		for (CorePwd currLocal : localArray)
		{
			mSyncItems.add(new ConflictChoice(mLastSync, currLocal, null));
		}
		
		SortItems();
	}

	private void SortItems()
	{
		Collections.sort(mSyncItems, new Comparator<ConflictChoice>()
		{
			@Override
			public int compare(ConflictChoice lhs, ConflictChoice rhs)
			{
				int nWeightsL = (lhs.shouldLocalPropagate() ? 1:0) + (lhs.shouldRemotePropagate() ? 1:0);
				int nWeightsR = (rhs.shouldLocalPropagate() ? 1:0) + (rhs.shouldRemotePropagate() ? 1:0);
				
				return nWeightsL - nWeightsR;
			}
		});
	}

	@Override
	public int getCount()
	{
		return mSyncItems.size();
	}

	@Override
	public ConflictChoice getItem(int arg0)
	{
		return mSyncItems.get(arg0);
	}

	@Override
	public long getItemId(int arg0)
	{
		return mSyncItems.get(arg0).getTimeCreated();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		SyncItem view;
		if(convertView == null)
		{
			view = new SyncItem(mContext);
		}
		else
		{
			view = (SyncItem) convertView;
		}
		view.updateViews(getItem(position));
		return view;
	}

}
