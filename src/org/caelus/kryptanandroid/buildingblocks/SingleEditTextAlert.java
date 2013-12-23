package org.caelus.kryptanandroid.buildingblocks;

import org.caelus.kryptanandroid.R;
import org.caelus.kryptanandroid.core.CorePwdFile;
import org.caelus.kryptanandroid.core.CoreSecureStringHandler;

import android.app.Activity;
import android.view.View;
import android.widget.EditText;

public class SingleEditTextAlert extends BaseAlert
{

	private int mMessageId;
	private EditText mInput;
	private String mErrorMessage;
	private DialogResultListener mResultListner;
	private int mId;
	
	public interface DialogResultListener
	{
		boolean onDialogResult(SingleEditTextAlert dialog, CoreSecureStringHandler result);
	}

	public SingleEditTextAlert(Activity activity, CorePwdFile pwdFile,
			int titleId, int messageId, DialogResultListener listener, int dialogId)
	{
		super(activity, pwdFile, titleId, BaseAlert.BUTTONS_CANCEL | BaseAlert.BUTTONS_OK, false);
		mMessageId = messageId;
		setErrorMessage(R.string.invalid_text_input);
		mResultListner = listener;
		mId = dialogId;
	}
	
	public void setErrorMessage(String error)
	{
		mErrorMessage = error;
	}
	
	public void setErrorMessage(int errorId)
	{
		mErrorMessage = mActivity.getResources().getString(errorId);
	}
	
	public int getDialogId()
	{
		return mId;
	}

	@Override
	protected void onInit()
	{
		// TODO Auto-generated method stub
		mAlert.setMessage(mActivity.getResources().getString(mMessageId));
	}

	@Override
	protected View getView()
	{
		mInput = new EditText(mActivity);
		return mInput;
	}

	@Override
	protected boolean onPositiveClicked()
	{
		// TODO: switch to safer input
		String unsafe = mInput.getText().toString();
		int size = unsafe.length();
		CoreSecureStringHandler newLabel = CoreSecureStringHandler
				.NewSecureString();
		for (int i = 0; i < size; i++)
		{
			newLabel.AddChar(unsafe.charAt(i));
		}
		
		boolean validationPassed = mResultListner.onDialogResult(this, newLabel.trim());
		if(!validationPassed){
			mInput.setError(mErrorMessage);
		}
		else
		{
			mInput.setError(null);
		}
		
		return validationPassed;
	}

}
