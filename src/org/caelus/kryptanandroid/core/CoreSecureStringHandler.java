package org.caelus.kryptanandroid.core;

import java.lang.reflect.Field;

import android.graphics.Bitmap;

public class CoreSecureStringHandler {

	private long nativeHandle;

	/**
	 * This uses reflection voodo to overwrite the string instance with null chars
	 * @param toOverwrite
	 */
	public static void overwriteStringInternalArr(String toOverwrite)
	{
		try
		{
			Field fVal = String.class.getDeclaredField("value");
			fVal.setAccessible(true);
			char[] value = (char[]) fVal.get(toOverwrite);
			for(int i=0; i<value.length; i++)
			{
				value[i] = '\0';
			}
			//All done, there should now, hopefully be no traces of this string left in memory now.
		} catch (NoSuchFieldException e)
		{
			e.printStackTrace();
		} catch (IllegalAccessException e)
		{
			e.printStackTrace();
		} catch (IllegalArgumentException e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * This uses reflection voodo to overwrite the string instance
	 * @param toOverwrite
	 */
	public static void overwriteStringInternalArr(String toOverwrite, char[] arr)
	{
		try
		{
			Field fVal = String.class.getDeclaredField("value");
			fVal.setAccessible(true);
			char[] value = (char[]) fVal.get(toOverwrite);
			int length = Math.min(value.length, arr.length);
			for(int i=0; i < length; i++)
			{
				value[i] = arr[i];
			}
			//All done, there should now, hopefully be no traces of this string left in memory now.
		} catch (NoSuchFieldException e)
		{
			e.printStackTrace();
		} catch (IllegalAccessException e)
		{
			e.printStackTrace();
		} catch (IllegalArgumentException e)
		{
			e.printStackTrace();
		}
	}
	
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
