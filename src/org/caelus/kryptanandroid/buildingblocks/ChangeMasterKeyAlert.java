package org.caelus.kryptanandroid.buildingblocks;

import org.caelus.kryptanandroid.Global;
import org.caelus.kryptanandroid.R;
import org.caelus.kryptanandroid.core.CorePwdFile;
import org.caelus.kryptanandroid.core.CoreSecureStringHandler;

import android.app.Activity;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

public class ChangeMasterKeyAlert extends BaseAlert
{
	private EditText mPassword;
	private EditText mConfirm;
	private boolean mConfirmOldKey;

	private EditText mOldMasterkey;

	public ChangeMasterKeyAlert(Activity activity, CorePwdFile pwdFile,
			boolean isCancelable, boolean confirmOldKey, boolean fullscreen)
	{
		super(activity, pwdFile, R.string.action_change_master, isCancelable, fullscreen);
		mConfirmOldKey = confirmOldKey;
	}

	public ChangeMasterKeyAlert(Activity activity, CorePwdFile pwdFile)
	{
		this(activity, pwdFile, true, true, false);
	}

	protected View getView()
	{
		// create layout
		LinearLayout layout = new LinearLayout(mActivity);
		layout.setOrientation(LinearLayout.VERTICAL);

		// create inputs
		if (mConfirmOldKey)
		{
			mOldMasterkey = new EditText(mActivity);
			mOldMasterkey.setHint(R.string.confirm_old_masterkey_hint);
			mOldMasterkey.setInputType(InputType.TYPE_CLASS_TEXT
					| InputType.TYPE_TEXT_VARIATION_PASSWORD);
			layout.addView(mOldMasterkey);
		}

		mPassword = new EditText(mActivity);
		mPassword.setHint(R.string.new_masterkey_hint);
		mPassword.setInputType(InputType.TYPE_CLASS_TEXT
				| InputType.TYPE_TEXT_VARIATION_PASSWORD);
		layout.addView(mPassword);

		mConfirm = new EditText(mActivity);
		mConfirm.setHint(R.string.confirm_masterkey_hint);
		mConfirm.setInputType(InputType.TYPE_CLASS_TEXT
				| InputType.TYPE_TEXT_VARIATION_PASSWORD);
		layout.addView(mConfirm);
		return layout;
	}

	private boolean oldKeyConfirmed()
	{
		String pass = mOldMasterkey.getText().toString();
		CoreSecureStringHandler key = mPwdFile.getMasterKeyHandler();
		if (pass.length() != key.GetLength())
		{
			return false;
		}
		for (int i = 0; i < pass.length(); i++)
		{
			if (pass.charAt(i) != key.GetChar(i))
				return false;
		}
		return true;
	}

	@Override
	protected boolean onPositiveClicked()
	{
		// Save was clicked
		String pass = mPassword.getText().toString();
		String conf = mConfirm.getText().toString();

		if (mConfirmOldKey && !oldKeyConfirmed())
		{
			mOldMasterkey.setError(mActivity.getResources().getString(
					R.string.error_incorrect_password));
			mOldMasterkey.requestFocus();
		} else if (TextUtils.isEmpty(pass))
		{
			mPassword.setError(mActivity.getResources().getString(
					R.string.error_field_required));
			mPassword.requestFocus();
		} else if (pass.length() < Global.MINIMUM_MASTERKEY_LENGTH)
		{
			mPassword.setError(mActivity.getResources().getString(
					R.string.error_invalid_password));
			mPassword.requestFocus();
		} else if (!TextUtils.equals(pass, conf))
		{
			mConfirm.setError(mActivity.getResources().getString(
					R.string.error_incorrect_confirm));
			mConfirm.requestFocus();
		} else
		{
			// validated

			CoreSecureStringHandler key = mPwdFile.getMasterKeyHandler();
			key.Clear();
			for (int i = 0; i < pass.length(); i++)
			{
				key.AddChar(pass.charAt(i));
			}

			// resave password file to set the new masterkey.
			mPwdFile.Save();

			return true;
		}
		return false;
	}
}
