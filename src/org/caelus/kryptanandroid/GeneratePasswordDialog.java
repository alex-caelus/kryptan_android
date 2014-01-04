package org.caelus.kryptanandroid;

import org.caelus.kryptanandroid.buildingblocks.BaseAlert;
import org.caelus.kryptanandroid.buildingblocks.SecureEditText;
import org.caelus.kryptanandroid.core.CorePwdFile;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;

public class GeneratePasswordDialog extends BaseAlert
{

	protected GeneratePasswordDialog(Activity activity, CorePwdFile pwdFile)
	{
		super(activity, pwdFile, R.string.title_activity_generate_password, BaseAlert.BUTTONS_OK | BaseAlert.BUTTONS_CANCEL, false);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onInit()
	{

	}

	@Override
	protected View getView()
	{
		ViewGroup contents = (ViewGroup) mActivity.getLayoutInflater().inflate(R.layout.dialog_generate_password, null);
		
		SecureEditText mPasswordEditText = (SecureEditText) contents.findViewById(R.id.newPasswordTextEdit);
		
		
		return contents;
	}

	@Override
	protected boolean onPositiveClicked()
	{
		// TODO Auto-generated method stub
		return false;
	}
}
