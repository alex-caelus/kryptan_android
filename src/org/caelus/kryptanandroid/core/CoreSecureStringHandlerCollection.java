package org.caelus.kryptanandroid.core;

import java.util.ArrayList;

import android.os.Parcel;
import android.os.Parcelable;

public class CoreSecureStringHandlerCollection implements Parcelable
{
	ArrayList<CoreSecureStringHandler> mStrings = new ArrayList<CoreSecureStringHandler>();

	public CoreSecureStringHandlerCollection(Parcel in)
	{
		int size = in.readInt();
		for (int i = 0; i < size; i++)
		{
			mStrings.add(new CoreSecureStringHandler(in.readLong()));
		}
	}

	public CoreSecureStringHandlerCollection()
	{
		// nothing to do, we start out empty
	}

	public ArrayList<CoreSecureStringHandler> getContainer()
	{
		return mStrings;
	}

	public static final Parcelable.Creator<CoreSecureStringHandlerCollection> CREATOR = new Parcelable.Creator<CoreSecureStringHandlerCollection>()
	{
		public CoreSecureStringHandlerCollection createFromParcel(Parcel in)
		{
			return new CoreSecureStringHandlerCollection(in);
		}

		public CoreSecureStringHandlerCollection[] newArray(int size)
		{
			return new CoreSecureStringHandlerCollection[size];
		}
	};

	@Override
	public int describeContents()
	{
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags)
	{
		dest.writeInt(mStrings.size());

		for (CoreSecureStringHandler string : mStrings)
		{
			dest.writeLong(string.getNativeHandle());
		}
	}

	public int size()
	{
		// TODO Auto-generated method stub
		return mStrings.size();
	}

	public CoreSecureStringHandler getCombinedCommaSeparatedString()
	{
		return getCombinedCommaSeparatedString(null);
	}

	public CoreSecureStringHandler getCombinedCommaSeparatedString(String prefix)
	{
		CoreSecureStringHandler combined = CoreSecureStringHandler
				.NewSecureString();

		boolean first = true;
		
		if(prefix != null)
		{
			for(int i=0; i<prefix.length(); i++)
			{
				combined.AddChar(prefix.charAt(i));
			}
		}

		for (CoreSecureStringHandler string : mStrings)
		{
			if (first)
			{
				first = false;
			} else
			{
				combined.AddChar(',');
				combined.AddChar(' ');
			}
			int length = string.GetLength();
			for (int i = 0; i < length; i++)
			{
				combined.AddChar(string.GetChar(i));
			}
		}

		return combined;
	}

}
