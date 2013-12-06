package org.caelus.kryptanandroid.core;

import android.graphics.Bitmap;

public class CoreSecureStringHandler {

	private long nativeHandle;
	
	/**
	 * This should never be called from Java
	 * @param nativeHandle
	 */
	public CoreSecureStringHandler(long nativeHandle) {
		this.nativeHandle = nativeHandle;
		IncrementReference();
	}
	
	/**
	 * Copy constructor
	 * @param aString
	 */
	public CoreSecureStringHandler(CoreSecureStringHandler aString)
	{
		CoreSecureStringHandler newString = NewSecureString(); 
		this.nativeHandle = newString.getNativeHandle();
		IncrementReference();
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
	
	@Deprecated
	public Bitmap getBitmapOfValue()
	{
		//TODO: generate bitmap of value
		return null;
	}
	
	protected void finalize() throws Throwable
	{
	    try
	    {
	    	DecrementReference();
	    }
	    finally
	    {
	        super.finalize();
	    }
	}
	
	public static native CoreSecureStringHandler NewSecureString();
	public native void AddChar(char c);
	public native void Clear();
	public native char GetChar(int j);
	public native int GetLength();
	private native void IncrementReference();
	private native void DecrementReference();
	

}
