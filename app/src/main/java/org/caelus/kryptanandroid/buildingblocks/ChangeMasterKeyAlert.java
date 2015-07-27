package org.caelus.kryptanandroid.buildingblocks;

import org.caelus.kryptanandroid.Global;
import org.caelus.kryptanandroid.R;
import org.caelus.kryptanandroid.core.CorePwdFile;
import org.caelus.kryptanandroid.core.CorePwdFile.FinishListener;
import org.caelus.kryptanandroid.core.CoreSecureStringHandler;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;

public class ChangeMasterKeyAlert extends BaseAlert
{
	private SecureEditText mPassword;
	private SecureEditText mConfirm;
	private boolean mConfirmOldKey;

	private SecureEditText mOldMasterkey;
	
	private OnSuccessfullListener mSuccessfull = new OnSuccessfullListener()
	{
		@Override
		public void onSuccessfull()
		{
			// dummy function
		}
	};

	public interface OnSuccessfullListener
	{
		void onSuccessfull();
	}

	public ChangeMasterKeyAlert(Activity activity, CorePwdFile pwdFile,
			int buttons, boolean confirmOldKey, boolean fullscreen)
	{
		super(activity, pwdFile, R.string.action_change_master, buttons, fullscreen);
		mConfirmOldKey = confirmOldKey;
	}

	public ChangeMasterKeyAlert(Activity activity, CorePwdFile pwdFile)
	{
		this(activity, pwdFile, BaseAlert.BUTTONS_CANCEL | BaseAlert.BUTTONS_OK, true, false);
	}

	public final void setOnSuccessfullListener(
			OnSuccessfullListener listener)
	{
		mSuccessfull = listener;
	}

	protected View getView()
	{
		ViewGroup layout = (ViewGroup) mActivity.getLayoutInflater().inflate(R.layout.dialog_change_master_key, null);

		mOldMasterkey = (SecureEditText) layout.findViewById(R.id.oldMasterKey);
		mPassword = (SecureEditText) layout.findViewById(R.id.newMasterKey);
		mConfirm = (SecureEditText) layout.findViewById(R.id.confirmMasterKey);
		if(!mConfirmOldKey)
		{
			mOldMasterkey.setVisibility(View.GONE);
		}
		
		return layout;
	}
	
	
	protected void onInit()
	{
	}

	private boolean oldKeyConfirmed()
	{
		CoreSecureStringHandler pass = mOldMasterkey.getSecureText();
		CoreSecureStringHandler key = mPwdFile.getMasterKeyHandler();
		if (pass.GetLength() != key.GetLength())
		{
			return false;
		}
		for (int i = 0; i < pass.GetLength(); i++)
		{
			if (pass.GetByte(i) != key.GetByte(i))
				return false;
		}
		return true;
	}

	@Override
	protected boolean onPositiveClicked()
	{
		// Save was clicked
		CoreSecureStringHandler pass = mPassword.getSecureText();
		CoreSecureStringHandler conf = mConfirm.getSecureText();

		if (mConfirmOldKey && !oldKeyConfirmed())
		{
			mOldMasterkey.setError(mActivity.getResources().getString(
					R.string.error_incorrect_password));
			mOldMasterkey.requestFocus();
		} else if (pass.GetLength() == 0)
		{
			mPassword.setError(mActivity.getResources().getString(
					R.string.error_field_required));
			mPassword.requestFocus();
		} else if (pass.GetLength() < Global.MINIMUM_MASTERKEY_LENGTH)
		{
			mPassword.setError(mActivity.getResources().getString(
					R.string.error_invalid_password));
			mPassword.requestFocus();
		} else if (!pass.equals(conf))
		{
			mConfirm.setError(mActivity.getResources().getString(
					R.string.error_incorrect_confirm));
			mConfirm.requestFocus();
		} else
		{
			// validated

			CoreSecureStringHandler key = mPwdFile.getMasterKeyHandler();
			key.Clear();
			for (int i = 0; i < pass.GetLength(); i++)
			{
				key.AddByte(pass.GetByte(i));
			}

			// resave password file to set the new masterkey.
			mPwdFile.SaveWithDialog(mActivity, new FinishListener()
			{
				@Override
				public void OnFinish()
				{
					// call callback
					mSuccessfull.onSuccessfull();
				}
			});

			return true;
		}
		return false;
	}
}
