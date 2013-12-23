package org.caelus.kryptanandroid.buildingblocks;

import org.caelus.kryptanandroid.R;
import org.caelus.kryptanandroid.core.CorePwdFile;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.DialogInterface.OnShowListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.Toast;

public abstract class BaseAlert implements OnClickListener, OnDismissListener
{
	public static int BUTTONS_CANCEL = 1;
	public static int BUTTONS_OK = 2;
	protected AlertDialog mAlert;
	protected CorePwdFile mPwdFile;
	protected Activity mActivity;
	private String mToastMessage = null;
	private int mButtons;
	private boolean mFullscreen;


	private OnDismissListener mDissmiss = new OnDismissListener()
	{	
		@Override
		public void onDismiss(DialogInterface dialog)
		{
			//dummy function
		}
	};
	
	private int mTitleid;
	protected View mContentRootView;
	private boolean mIsInited = false;


	protected BaseAlert(Activity activity, CorePwdFile pwdFile, int titleId,
			int buttons, boolean fullscreen)
	{
		mPwdFile = pwdFile;
		mActivity = activity;
		mTitleid = titleId;
		this.mButtons = buttons;
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
		if ((mButtons & BUTTONS_OK) > 0)
		{
			alert.setPositiveButton(
					mActivity.getResources().getString(R.string.save), null);
		}

		if ((mButtons & BUTTONS_CANCEL) > 0)
		{
			alert.setNegativeButton(
					mActivity.getResources().getString(R.string.cancel), null);
		} else
		{
			alert.setCancelable(false);
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
				if (b != null)
				{
					b.setOnClickListener(BaseAlert.this);
				}

				mContentRootView.requestFocus();
			}
		});
		
		// set mFullscreen
		if (mFullscreen)
		{
			mAlert.getWindow().setFlags(LayoutParams.FLAG_FULLSCREEN,
					LayoutParams.FLAG_FULLSCREEN);
		}
		
		mAlert.setOnDismissListener(this);

		onInit();

	}

	protected abstract void onInit();

	protected abstract View getView();

	public final void show()
	{
		if (!mIsInited)
			init();
		mIsInited = true;
		mAlert.show();
	}

	public final void hide()
	{
		mAlert.hide();
	}

	public final void setToastMessage(String text)
	{
		mToastMessage = text;
	}

	public final void setToastMessage(int textId)
	{
		mToastMessage = mActivity.getResources().getString(textId);
	}

	public final void setOnDismissListener(
			OnDismissListener listener)
	{
		mDissmiss = listener;
	}

	@Override
	public final void onClick(View v)
	{
		if (onPositiveClicked())
		{
			// show toast
			if (mToastMessage != null)
			{
				Toast t = Toast.makeText(mActivity, mToastMessage,
						Toast.LENGTH_SHORT);
				t.show();
			}

			// close dialog
			mAlert.dismiss();
		}
	}

	/**
	 * 
	 * @return if true the alert dialog will close
	 */
	abstract protected boolean onPositiveClicked();
	
	@Override
	public void onDismiss(DialogInterface dialog)
	{
		mDissmiss.onDismiss(dialog);
	}

}
