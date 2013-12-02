package org.caelus.kryptanandroid.core;

import android.graphics.Bitmap;

public class CoreSecureStringHandler {

	private long nativeHandle;
	
	public CoreSecureStringHandler(long nativeHandle) {
		this.nativeHandle = nativeHandle;
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
	
	public static native long NewSecureString();
	public native void AddChar(char c);
	public native void Clear();
	public native char GetChar(int j);
	public native void Dispose();
	

}
