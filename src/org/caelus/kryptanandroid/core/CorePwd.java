package org.caelus.kryptanandroid.core;

import android.os.Parcel;
import android.os.Parcelable;

public class CorePwd implements Parcelable{

	private long nativeHandle;

	public CorePwd(long nativeHandle)
	{
		this.nativeHandle = nativeHandle;
		
	}
	
	public CorePwd(Parcel in)
	{
		nativeHandle = in.readLong();
	}
	
	public long getNativeHandle()
	{
		return nativeHandle;
	}
	

	public void SetNewDescription(CoreSecureStringHandler desc)
	{
		SetNewDescription(desc.getNativeHandle());
	}
	
	public void SetNewUsername(CoreSecureStringHandler user)
	{
		SetNewUsername(user.getNativeHandle());
	}
	
	public void SetNewPassword(CoreSecureStringHandler password)
	{
		SetNewPassword(password.getNativeHandle());
	}

	//natives
	public native CoreSecureStringHandler GetDescriptionCopy();
	public native CoreSecureStringHandler GetUsernameCopy();
	public native CoreSecureStringHandler GetPasswordCopy();
	public native CoreSecureStringHandler[] GetLabels();
	private native void SetNewDescription(long nativeHandle);
	private native void SetNewUsername(long nativeHandle);
	private native void SetNewPassword(long nativeHandle);

	//parcelable
	public static final Parcelable.Creator<CorePwd> CREATOR = new Parcelable.Creator<CorePwd>() {
        public CorePwd createFromParcel(Parcel in) {
            return new CorePwd(in); 
        }

        public CorePwd[] newArray(int size) {
            return new CorePwd[size];
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
		dest.writeLong(nativeHandle);
	}

	
}
