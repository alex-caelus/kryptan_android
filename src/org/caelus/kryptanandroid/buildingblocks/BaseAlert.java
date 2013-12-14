package org.caelus.kryptanandroid.buildingblocks;

import org.caelus.kryptanandroid.R;
import org.caelus.kryptanandroid.core.CorePwdFile;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.Toast;

public abstract class BaseAlert implements OnClickListener
{

	private AlertDialog mAlert;
	protected CorePwdFile mPwdFile;
	protected Activity mActivity;
	private String mToastMessage = null;
	private boolean mIsCancelable;
	private boolean mFullscreen;
	
	private OnSuccessfullSaveListener mSaveSuccessfull = new OnSuccessfullSaveListener()
	{
		@Override
		public void onSuccessfullSave()
		{
			// dummy function
		}
	};
	private int mTitleid;
	protected View mContentRootView;

	public interface OnSuccessfullSaveListener
	{
		void onSuccessfullSave();
	}

	protected BaseAlert(Activity activity, CorePwdFile pwdFile, int titleId,
			boolean isCancelable, boolean fullscreen)
	{
		mPwdFile = pwdFile;
		mActivity = activity;
		mTitleid = titleId;
		this.mIsCancelable = isCancelable;
		this.mFullscreen = fullscreen;
	}

	private final void init()
	{
		mContentRootView = getView();

		// create alert
		AlertDialog.Builder alert = new AlertDialog.Builder(mActivity);

		alert.setTitle(mActivity.getResources().getString(mTitleid));

		// add layout to dialog
		alert.setView(mContentRootView);

		// buttons
		alert.setPositiveButton(
				mActivity.getResources().getString(R.string.save), null);
		if (mIsCancelable)
		{
			alert.setNegativeButton(
					mActivity.getResources().getString(R.string.cancel), null);
		}
		else
		{
			alert.setCancelable(mIsCancelable);
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
				b.setOnClickListener(BaseAlert.this);

				mContentRootView.requestFocus();
			}
		});

		// open keyboard on show
		mAlert.getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

		// set mFullscreen
		if (mFullscreen)
		{
			mAlert.getWindow().setFlags(LayoutParams.FLAG_FULLSCREEN,
					LayoutParams.FLAG_FULLSCREEN);
		}
	}

	protected abstract View getView();

	public final void show()
	{
		init();
		mAlert.show();
	}

	public final void setToastMessage(String text)
	{
		mToastMessage = text;
	}

	public final void setToastMessage(int textId)
	{
		mToastMessage = mActivity.getResources().getString(textId);
	}

	public final void setOnSuccessfullSaveListener(OnSuccessfullSaveListener listener)
	{
		mSaveSuccessfull = listener;
	}

	@Override
	public final void onClick(View v)
	{
		if(onPositiveClicked())
		{
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

	/**
	 * 
	 * @return if true the alert dialog will close
	 */
	abstract protected boolean onPositiveClicked();

}
