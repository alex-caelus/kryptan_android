package org.caelus.kryptanandroid.core;

import org.caelus.kryptanandroid.R;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Parcel;
import android.os.Parcelable;

public class CorePwdFile implements Parcelable{
	
	static{
        //System.loadLibrary("stlport_shared"); // this is an alternative to gnustl_shared, but require recompiling of cryptopp
        System.loadLibrary("gnustl_shared");
        System.loadLibrary("cryptopp");
        System.loadLibrary("kryptan_core");
	}
	
	private long nativeHandle;
	private long nativeMasterKeyHandle;
	
	private CorePwdList passwordList = null;
	private CoreSecureStringHandler mMasterkey = null;
	
	public CorePwdFile(String filename) {
		nativeHandle = CreateInstance(filename);
		mMasterkey = CoreSecureStringHandler.NewSecureString();
		nativeMasterKeyHandle  = mMasterkey.getNativeHandle();
	}
	
	public CorePwdFile(Parcel in) {
		nativeHandle = in.readLong();
		nativeMasterKeyHandle = in.readLong();
		mMasterkey = new CoreSecureStringHandler(nativeMasterKeyHandle);
	}

	public CorePwdList getPasswordList()
	{
		if(passwordList == null)
		{
			passwordList = new CorePwdList(GetPasswordListHandle());
		}
		return passwordList;
	}
	
	public CoreSecureStringHandler getMasterKeyHandler()
	{
		if(mMasterkey == null)
		{
			mMasterkey = new CoreSecureStringHandler(nativeMasterKeyHandle);
		}
		return mMasterkey;
	}
	
	public void SaveWithDialog(Context context)
	{
		//start dialog
		SaveAsync saver = new SaveAsync(context);
		saver.execute();
	}
	
	private native long CreateInstance(String filename);
	public native void Dispose();
	
	public native void CreateNew();
	public native void TryOpenAndParse();
	
	private native void Save();

	private native long GetPasswordListHandle();
	public native String GetFilename();

	public native String SaveToString();

	public native boolean IsOpen();
	public native boolean Exists();
	
	// this is used to regenerate your object. All Parcelables must have a CREATOR that implements these two methods
    public static final Parcelable.Creator<CorePwdFile> CREATOR = new Parcelable.Creator<CorePwdFile>() {
        public CorePwdFile createFromParcel(Parcel in) {
            return new CorePwdFile(in);
        }

        public CorePwdFile[] newArray(int size) {
            return new CorePwdFile[size];
        }
    };

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(nativeHandle);
		dest.writeLong(nativeMasterKeyHandle);
	}
	private class SaveAsync extends AsyncTask<Void, Void, Void> {

	    private ProgressDialog dialog;
	    
	    private Context mContext;
	    
	    public SaveAsync(Context context)
	    {
	    	mContext = context;
	    	dialog = new ProgressDialog(context);
	    }

	    /** progress dialog to show user that the backup is processing. */
	    /** application context. */
	    @Override
	    protected void onPreExecute() {
	        this.dialog.setMessage(mContext.getResources().getString(R.string.progress_saving));
	        this.dialog.setIndeterminate(true);
	        this.dialog.setCancelable(false);
	        this.dialog.show();
	    }

	    @Override
		protected Void doInBackground(Void... params)
	    {
        	Save();
        	return null;
	    }

	    @Override
	    protected void onPostExecute(final Void arg) {

	        if (dialog.isShowing()) {
	            dialog.dismiss();
	        }

	    }
	}
	
}
