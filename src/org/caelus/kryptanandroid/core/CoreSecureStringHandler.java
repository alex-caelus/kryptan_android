package org.caelus.kryptanandroid.core;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;

public class CoreSecureStringHandler
{

	private long nativeHandle;

	/**
	 * This uses reflection voodo to overwrite the string instance with null
	 * chars
	 * 
	 * @param toOverwrite
	 */
	public static void overwriteStringInternalArr(String toOverwrite)
	{
		try
		{
			Field fVal = String.class.getDeclaredField("value");
			fVal.setAccessible(true);
			char[] value = (char[]) fVal.get(toOverwrite);
			for (int i = 0; i < value.length; i++)
			{
				value[i] = '\0';
			}
			// All done, there should now, hopefully be no traces of this string
			// left in memory now.
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
	 * 
	 * @param toOverwrite
	 */
	public static void overwriteStringInternalArr(String toOverwrite,
			char[] arr, int start, int charCount)
	{
		try
		{
			Field fVal = String.class.getDeclaredField("value");
			fVal.setAccessible(true);
			char[] value = (char[]) fVal.get(toOverwrite);
			int length = Math
					.min(Math.min(value.length, arr.length), charCount);
			for (int i = 0; i < length; i++)
			{
				value[i] = arr[start + i];
			}
			// All done, there should now, hopefully be no traces of this string
			// left in memory now.
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
	 * 
	 * @param toOverwrite
	 */
	public static void overwriteStringInternalArr(String toOverwrite, char[] arr)
	{
		overwriteStringInternalArr(toOverwrite, arr, 0, arr.length);
	}

	/**
	 * This should never be called from Java
	 * 
	 * @param nativeHandle
	 */
	public CoreSecureStringHandler(long nativeHandle)
	{
		this.nativeHandle = nativeHandle;
		IncrementReference();
	}

	/**
	 * Copy constructor
	 * 
	 * @param aString
	 */
	public CoreSecureStringHandler(CoreSecureStringHandler aString)
	{
		CoreSecureStringHandler newString = NewSecureString();
		this.nativeHandle = newString.getNativeHandle();
		IncrementReference();
		int length = aString.GetLength();
		for (int i = 0; i < length; i++)
		{
			this.AddByte(aString.GetByte(i));
		}
	}

	public long getNativeHandle()
	{
		return nativeHandle;
	}

	protected void finalize() throws Throwable
	{
		try
		{
			DecrementReference();
		} finally
		{
			super.finalize();
		}
	}

	@Override
	public boolean equals(Object o)
	{
		if(o == null)
			return false;
		try
		{
			CoreSecureStringHandler obj = (CoreSecureStringHandler) o;
			if(obj.nativeHandle == this.nativeHandle)
			{
				return true; //well it's really the same object
			}
			return Equals(obj);
		} catch (ClassCastException e)
		{
			//not even the right class
			return false;
		}
	}
	
	public void Append(String toAppend)
	{
		try
		{
			byte[] encoded = toAppend.getBytes("UTF-8");
			for (byte b : encoded)
			{
				AddByte(b);
			}
		} catch (UnsupportedEncodingException e)
		{
			//suppress this error, should never happen with UTF-8
		}
	}
	
	public void Append(CoreSecureStringHandler toAppend)
	{
		int l=toAppend.GetLength();
		for(int i=0; i < l; i++)
		{
			AddByte(toAppend.GetByte(i));
		}
	}

	public static native CoreSecureStringHandler NewSecureString();

	public native void AddByte(byte c);

	public native void Clear();

	public native byte GetByte(int j);

	public native int GetLength();

	private native void IncrementReference();

	private native void DecrementReference();
	
	private native boolean Equals(CoreSecureStringHandler str);

	/**
	 * Returns a trimmed version of this string where
	 * whitespace from the beginning and end of the string
	 * has been removed
	 */
	public CoreSecureStringHandler trim()
	{
		int length = GetLength();
		if(length == 0)
		{
			return this;
		}
		boolean hasNonWhitespace = false;
		int firstNonWhitespace = 0;
		int lastNonWhitespace = 0;
		for(int i=0; i<length; i++)
		{
			byte c = GetByte(i);
			if(!Character.isWhitespace(c))
			{
				if(!hasNonWhitespace)
				{
					firstNonWhitespace = i;
					hasNonWhitespace = true;
				}
				lastNonWhitespace = i;
			}
		}
		
		if(!hasNonWhitespace)
		{
			return NewSecureString();
		}
		
		if(firstNonWhitespace == 0 && lastNonWhitespace == length-1)
		{
			return this;
		}

		CoreSecureStringHandler trimmed = NewSecureString();
		
		for(int i=firstNonWhitespace; i <= lastNonWhitespace; i++)
		{
			trimmed.AddByte(GetByte(i));
		}
		
		return trimmed;
	}

}
