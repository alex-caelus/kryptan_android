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
		return mStrings.size();
	}

	public CoreSecureStringHandler getCombinedCommaSeparatedString(String prefix, String ifEmpty)
	{
		CoreSecureStringHandler combined = CoreSecureStringHandler
				.NewSecureString();

		boolean first = true;
		
		if (!mStrings.isEmpty())
		{
			if (prefix != null)
			{ 
				combined.Append(prefix);
			}
			for (CoreSecureStringHandler string : mStrings)
			{
				if (first)
				{
					first = false;
				} else
				{
					combined.Append(", ");
				}
				int length = string.GetLength();
				for (int i = 0; i < length; i++)
				{
					combined.AddByte(string.GetByte(i));
				}
			}
		}
		else
		{
			if (ifEmpty != null)
			{
				for (int i = 0; i < ifEmpty.length(); i++)
				{
					combined.Append(ifEmpty);
				}
			}
		}
		return combined;
	}

}
