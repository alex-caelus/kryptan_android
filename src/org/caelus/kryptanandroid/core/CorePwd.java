package org.caelus.kryptanandroid.core;

public class CorePwd {

	private long nativeHandle;

	public CorePwd(long nativeHandle)
	{
		this.nativeHandle = nativeHandle;
	}

	public native CoreSecureStringHandler GetDescriptionHandler();
	public native CoreSecureStringHandler GetUsernameHandler();
	public native CoreSecureStringHandler GetPasswordHandler();

	public long getNativeHandle()
	{
		return nativeHandle;
	}
	
}
