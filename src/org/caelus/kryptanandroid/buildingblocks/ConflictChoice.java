package org.caelus.kryptanandroid.buildingblocks;

import org.caelus.kryptanandroid.R;
import org.caelus.kryptanandroid.core.CorePwd;
import org.caelus.kryptanandroid.core.CoreSecureStringHandler;

import android.content.res.Resources;

public class ConflictChoice
{
	private static final CoreSecureStringHandler mEmptyDescriptionString = CoreSecureStringHandler.NewSecureString();
	private CorePwd mLocal;
	private CorePwd mRemote;
	private boolean mLocalPropogation;
	private boolean mRemotePropogation;
	private long mLastSyncDate;

	private String mLocalTooltip;
	private String mRemoteTooltip;
	private Resources mResources;
	
	public ConflictChoice(Resources resources, long lastSyncDate, CorePwd local, CorePwd remote)
	{
		mResources = resources;
		setEmptyText();
		mLocal = local;
		mRemote = remote;
		mLastSyncDate = lastSyncDate;
		setLocalPropagation(false);
		setRemotePropagation(false);
		makeDefaultChoice();
	}
	
	private void makeDefaultChoice()
	{
		if(mLocal == null && mRemote == null){
			throw new IllegalArgumentException("Both local and remote cannot be null");
		}
		else if(mLocal == null)
		{
			//local is either deleted or remote is new
			//we dont know which
			if(mLastSyncDate == 0)
			{
				//no prior syncronization performed
				setLocalPropagation(false);
				setRemotePropagation(true);
				mLocalTooltip = mResources.getString(R.string.conflict_choice_no_sync);
				mRemoteTooltip =  mResources.getString(R.string.conflict_choice_created) + mRemote.GetTimeCreatedString();
			}
			else if(mRemote.GetTimeCreated() < mLastSyncDate)
			{
				//this means that remote should have existed locally previously
				//but was deleted. The change should be propagated local -> remote
				setLocalPropagation(true);
				setRemotePropagation(false);
				mLocalTooltip = mResources.getString(R.string.conflict_choice_deleted);
				mRemoteTooltip = mResources.getString(R.string.conflict_choice_before_sync);
			}
			else
			{
				//this means that remote was created _After_ last sync.
				//The item is probably new and should be propagated local <- remote
				setLocalPropagation(false);
				setRemotePropagation(true);
				mLocalTooltip = mResources.getString(R.string.conflict_choice_new);
				mRemoteTooltip = mResources.getString(R.string.conflict_choice_after_sync);
			}
		}
		else if(mRemote == null)
		{
			//local is either deleted or remote is new
			//we dont know which
			if(mLastSyncDate == 0)
			{
				//no prior syncronization performed
				setLocalPropagation(true);
				setRemotePropagation(false);
				mLocalTooltip = mResources.getString(R.string.conflict_choice_created) + mLocal.GetTimeCreatedString();
				mRemoteTooltip = mResources.getString(R.string.conflict_choice_no_sync);
			}
			else if(mLocal.GetTimeCreated() < mLastSyncDate)
			{
				//this means that local should have existed previously on remote
				//but was deleted. The change should be propagated local <- remote
				setLocalPropagation(false);
				setRemotePropagation(true);
				mLocalTooltip = mResources.getString(R.string.conflict_choice_before_sync);
				mRemoteTooltip = mResources.getString(R.string.conflict_choice_deleted);
			}
			else
			{
				//this means that local was created _After_ last sync.
				//The item is probably new and should be propagated local -> remote
				setLocalPropagation(true);
				setRemotePropagation(false);
				mLocalTooltip = mResources.getString(R.string.conflict_choice_after_sync);
				mRemoteTooltip = mResources.getString(R.string.conflict_choice_new);
			}
		}
		else
		{
			if(mLocal.GetTimeCreated() != mRemote.GetTimeCreated())
			{
				throw new IllegalArgumentException("The arguments does not have the same creation date");
			}
			if(mLocal.GetTimeModified() == mRemote.GetTimeModified())
			{
				//they are identical, or should be at least
				setLocalPropagation(true);
				setRemotePropagation(true);
				mLocalTooltip = mResources.getString(R.string.conflict_choice_identical);
				mRemoteTooltip = mResources.getString(R.string.conflict_choice_identical);
			}
			else if(mLocal.GetTimeModified() < mRemote.GetTimeModified())
			{
				//remote is newest
				setLocalPropagation(false);
				setRemotePropagation(true);
				mLocalTooltip = mResources.getString(R.string.conflict_choice_modified) + mLocal.GetTimeModifiedString();
				mRemoteTooltip = mResources.getString(R.string.conflict_choice_modified) + mRemote.GetTimeModifiedString();
			}
			else
			{
				//local is newest
				setLocalPropagation(true);
				setRemotePropagation(false);
				mLocalTooltip = mResources.getString(R.string.conflict_choice_modified) + mLocal.GetTimeModifiedString();
				mRemoteTooltip = mResources.getString(R.string.conflict_choice_modified) + mRemote.GetTimeModifiedString();
			}
		}
	}
	public long getTimeCreated()
	{
		if(mLocal != null)
			return mLocal.GetTimeCreated();
		else
			return mRemote.GetTimeCreated();
	}
	public CorePwd getLocalPwd()
	{
		return mLocal;
	}
	public CorePwd getRemotePwd()
	{
		return mRemote;
	}
	public CoreSecureStringHandler getLocalPwdDescription()
	{
		if(mLocal == null)
		{
			return mEmptyDescriptionString;
		}
		return mLocal.GetDescriptionCopy();
	}
	public CoreSecureStringHandler getRemotePwdDescription()
	{
		if(mRemote == null)
		{
			return mEmptyDescriptionString;
		}
		return mRemote.GetDescriptionCopy();
	}
	public boolean shouldLocalPropagate()
	{
		return mLocalPropogation;
	}
	public void setLocalPropagation(boolean mLocal)
	{
		this.mLocalPropogation = mLocal;
	}
	public boolean shouldRemotePropagate()
	{
		return mRemotePropogation;
	}
	public void setRemotePropagation(boolean mRemote)
	{
		this.mRemotePropogation = mRemote;
	}
	public boolean isResolved()
	{
		return mLocalPropogation || mRemotePropogation;
	}

	public String getRemoteTooltip()
	{
		return mRemoteTooltip;
	}

	public String getLocalTooltip()
	{
		return mLocalTooltip;
	}

	private void setEmptyText()
	{
		if(mEmptyDescriptionString.GetLength() == 0)
		{
			String text = mResources.getString(R.string.conflict_choice_missing);
			for (char c : text.toCharArray())
			{
				mEmptyDescriptionString.AddChar(c);
			}
		}
	}
}