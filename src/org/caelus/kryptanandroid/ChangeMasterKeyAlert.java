package org.caelus.kryptanandroid;

import org.caelus.kryptanandroid.core.CorePwdFile;
import org.caelus.kryptanandroid.core.CoreSecureStringHandler;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

public class ChangeMasterKeyAlert implements OnClickListener
{
	private AlertDialog mAlert;
	private EditText mPassword;
	private EditText mConfirm;
	private CorePwdFile mPwdFile;
	private Activity mActivity;
	private boolean mConfirmOldKey;
	private String mToastMessage = null;

	private OnSuccessfullSaveListener mSaveSuccessfull = new OnSuccessfullSaveListener()
	{
		@Override
		public void onSuccessfullSave()
		{
			// dummy function
		}
	};
	private EditText mOldMasterkey;

	interface OnSuccessfullSaveListener
	{
		void onSuccessfullSave();
	}

	public ChangeMasterKeyAlert(Activity activity, CorePwdFile pwdFile,
			boolean isCancelable, boolean confirmOldKey, boolean fullscreen)
	{
		mPwdFile = pwdFile;
		mActivity = activity;

		// create layout
		LinearLayout layout = new LinearLayout(mActivity);
		layout.setOrientation(LinearLayout.VERTICAL);

		// create inputs
		mConfirmOldKey = confirmOldKey;
		if (mConfirmOldKey)
		{
			mOldMasterkey = new EditText(activity);
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

		// create alert
		AlertDialog.Builder alert = new AlertDialog.Builder(mActivity);

		alert.setTitle(mActivity.getResources().getString(
				R.string.action_change_master));

		// add layout to dialog
		alert.setView(layout);

		// buttons
		alert.setPositiveButton(
				mActivity.getResources().getString(R.string.save), null);
		if (isCancelable)
		{
			alert.setNegativeButton(
					mActivity.getResources().getString(R.string.cancel), null);
		}
		else
		{
			alert.setCancelable(isCancelable);
		}

		// create instance
		mAlert = alert.create();

		// set on click listener
		mAlert.setOnShowListener(new OnShowListener()
		{
			@Override
			public void onShow(DialogInterface dialog)
			{
				Button b = mAlert.getButton(DialogInterface.BUTTON_POSITIVE);
				b.setOnClickListener(ChangeMasterKeyAlert.this);

				// set focus to the first input
				if (mConfirmOldKey)
				{
					mOldMasterkey.requestFocus();
				} else
				{
					mPassword.requestFocus();
				}
			}
		});

		// open keyboard on show
		mAlert.getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

		// set fullscreen
		if (fullscreen)
		{
			mAlert.getWindow().setFlags(LayoutParams.FLAG_FULLSCREEN,
					LayoutParams.FLAG_FULLSCREEN);
		}
	}

	public ChangeMasterKeyAlert(Activity activity, CorePwdFile pwdFile)
	{
		this(activity, pwdFile, true, true, false);
	}

	public void show()
	{
		mAlert.show();
	}

	public void setToastMessage(String text)
	{
		mToastMessage = text;
	}

	public void setToastMessage(int textId)
	{
		mToastMessage = mActivity.getResources().getString(textId);
	}

	public void setOnSuccessfullSaveListener(OnSuccessfullSaveListener listener)
	{
		mSaveSuccessfull = listener;
	}

	@Override
	public void onClick(View v)
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

			// show toast
			if (mToastMessage != null)
			{
				Toast t = Toast.makeText(mActivity, mToastMessage,
						Toast.LENGTH_SHORT);
				t.show();
			}

			// call callback
			mSaveSuccessfull.onSuccessfullSave();

			// close dialog
			mAlert.dismiss();
		}
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
}
