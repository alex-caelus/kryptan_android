package org.caelus.kryptanandroid.core;

import android.graphics.Bitmap;

public class CoreSecureStringHandler {

	private long nativeHandle;
	
	public CoreSecureStringHandler(long nativeHandle) {
		this.nativeHandle = nativeHandle;
	}
	
	/**
	 * Copy constructor
	 * @param aString
	 */
	public CoreSecureStringHandler(CoreSecureStringHandler aString)
	{
		this.nativeHandle = NewSecureString();
		int length = aString.GetLength();
		for(int i=0; i < length; i++)
		{
			this.AddChar(aString.GetChar(i));
		}
	}
	
	public long getNativeHandle()
	{
		return nativeHandle;
	}
	
	public Bitmap getBitmapOfValue()
	{
		//TODO: generate bitmap of value
		return null;
	}
	
	protected void finalize() throws Throwable
	{
	    try
	    {
	    	Dispose();
	    }
	    finally
	    {
	        super.finalize();
	    }
	}
	
	public static native long NewSecureString();
	public native void AddChar(char c);
	public native void Clear();
	public native char GetChar(int j);
	public native int GetLength();
	private native void Dispose();
	

}
