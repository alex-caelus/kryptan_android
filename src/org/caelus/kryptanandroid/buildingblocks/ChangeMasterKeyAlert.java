package org.caelus.kryptanandroid.buildingblocks;

import org.caelus.kryptanandroid.Global;
import org.caelus.kryptanandroid.R;
import org.caelus.kryptanandroid.core.CorePwdFile;
import org.caelus.kryptanandroid.core.CoreSecureStringHandler;

import android.app.Activity;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;

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
		// create layout
		LinearLayout layout = new LinearLayout(mActivity);
		layout.setOrientation(LinearLayout.VERTICAL);

		// create inputs
		if (mConfirmOldKey)
		{
			mOldMasterkey = new SecureEditText(mActivity);
			mOldMasterkey.setHint(R.string.confirm_old_masterkey_hint);
			mOldMasterkey.setInputType(InputType.TYPE_CLASS_TEXT
					| InputType.TYPE_TEXT_VARIATION_PASSWORD);
			layout.addView(mOldMasterkey);
		}

		mPassword = new SecureEditText(mActivity);
		mPassword.setHint(R.string.new_masterkey_hint);
		mPassword.setInputType(InputType.TYPE_CLASS_TEXT
				| InputType.TYPE_TEXT_VARIATION_PASSWORD);
		layout.addView(mPassword);

		mConfirm = new SecureEditText(mActivity);
		mConfirm.setHint(R.string.confirm_masterkey_hint);
		mConfirm.setInputType(InputType.TYPE_CLASS_TEXT
				| InputType.TYPE_TEXT_VARIATION_PASSWORD);
		layout.addView(mConfirm);
		return layout;
	}
	
	
	protected void onInit()
	{
		// disable default keyboard on show
		mAlert.getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
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
			if (pass.GetChar(i) != key.GetChar(i))
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
				key.AddChar(pass.GetChar(i));
			}

			// resave password file to set the new masterkey.
			mPwdFile.Save();

			// call callback
			mSuccessfull.onSuccessfull();

			return true;
		}
		return false;
	}
}
