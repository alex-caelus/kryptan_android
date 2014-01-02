package org.caelus.kryptanandroid;

import org.caelus.kryptanandroid.buildingblocks.BaseAlert;
import org.caelus.kryptanandroid.buildingblocks.ChangeMasterKeyAlert;
import org.caelus.kryptanandroid.buildingblocks.SecureEditText;
import org.caelus.kryptanandroid.core.CorePwdFile;
import org.caelus.kryptanandroid.core.CoreSecureStringHandler;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

/**
 * Activity which displays a login screen to the user, offering registration as
 * well.
 */
public class OpenPasswordFileActivity extends Activity implements
		ChangeMasterKeyAlert.OnSuccessfullListener
{
	/**
	 * Keep track of the login task to ensure we can cancel it if requested.
	 */
	private UserLoginTask mAuthTask = null;

	// Values for email and password at the time of the login attempt.
	private CoreSecureStringHandler mPassword;

	// UI references.
	private SecureEditText mPasswordView;
	private View mLoginFormView;
	private View mLoginStatusView;
	private TextView mLoginStatusMessageView;

	private CorePwdFile mCorePwdFile;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_decrypt);

		String filename = getFilesDir().getAbsolutePath();
		filename += "/secret.pwd";
		mCorePwdFile = new CorePwdFile(filename);

		// check if password file exists
		if (!mCorePwdFile.Exists())
		{
			// it doesn't so we need to create one.
			createNewPwdFile();
		}

		// Set up the login form.

		mPasswordView = (SecureEditText) findViewById(R.id.password);
		mPasswordView.setKeyboardDialogTitle(getResources().getString(R.string.title_activity_decrypt));

		mLoginFormView = findViewById(R.id.login_form);
		mLoginStatusView = findViewById(R.id.login_status);
		mLoginStatusMessageView = (TextView) findViewById(R.id.login_status_message);

		findViewById(R.id.sign_in_button).setOnClickListener(
				new View.OnClickListener()
				{
					@Override
					public void onClick(View view)
					{
						attemptLogin();
					}
				});

	}

	private void createNewPwdFile()
	{
		//create the new file in memory
		mCorePwdFile.CreateNew();
		
		//display dialog for new master and let it save the file on disk
		ChangeMasterKeyAlert alert = new ChangeMasterKeyAlert(this,
				mCorePwdFile, BaseAlert.BUTTONS_OK, false, true);
		alert.setOnSuccessfullListener(this);
		alert.setToastMessage(R.string.new_password_file_created);
		alert.show();
	}

	/**
	 * This is called when user has created a new password file
	 */
	@Override
	public void onSuccessfull()
	{
		if (mCorePwdFile.IsOpen())
		{
			Intent data = new Intent();
			data.putExtra(Global.EXTRA_CORE_PWD_FILE_INSTANCE, mCorePwdFile);
			setResult(RESULT_OK, data);
			finish();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.decrypt, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * Attempts to sign in or register the account specified by the login form.
	 * If there are form errors (invalid email, missing fields, etc.), the
	 * errors are presented and no actual login attempt is made.
	 */
	public void attemptLogin()
	{
		if (mAuthTask != null)
		{
			return;
		}

		// Reset errors.
		mPasswordView.setError(null);

		// TODO: securely read password from user
		mPassword = mPasswordView.getSecureText();

		boolean cancel = false;
		View focusView = null;

		// Check for a valid password.
		if (mPassword.GetLength() == 0)
		{
			mPasswordView.setError(getString(R.string.error_field_required));
			focusView = mPasswordView;
			cancel = true;
		} else if (mPassword.GetLength() < Global.MINIMUM_MASTERKEY_LENGTH)
		{
			mPasswordView.setError(getString(R.string.error_invalid_password));
			focusView = mPasswordView;
			cancel = true;
		}

		if (cancel)
		{
			// There was an error; don't attempt login and focus the first
			// form field with an error.
			focusView.requestFocus();
		} else
		{
			// Show a progress spinner, and kick off a background task to
			// perform the user login attempt.
			mLoginStatusMessageView.setText(R.string.login_progress_signing_in);
			showProgress(true);
			mAuthTask = new UserLoginTask();
			mAuthTask.execute((Void) null);
		}
	}

	/**
	 * Shows the progress UI and hides the login form.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private void showProgress(final boolean show)
	{
		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		// for very easy animations. If available, use these APIs to fade-in
		// the progress spinner.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2)
		{
			int shortAnimTime = getResources().getInteger(
					android.R.integer.config_shortAnimTime);

			mLoginStatusView.setVisibility(View.VISIBLE);
			mLoginStatusView.animate().setDuration(shortAnimTime)
					.alpha(show ? 1 : 0)
					.setListener(new AnimatorListenerAdapter()
					{
						@Override
						public void onAnimationEnd(Animator animation)
						{
							mLoginStatusView.setVisibility(show ? View.VISIBLE
									: View.GONE);
						}
					});

			mLoginFormView.setVisibility(View.VISIBLE);
			mLoginFormView.animate().setDuration(shortAnimTime)
					.alpha(show ? 0 : 1)
					.setListener(new AnimatorListenerAdapter()
					{
						@Override
						public void onAnimationEnd(Animator animation)
						{
							mLoginFormView.setVisibility(show ? View.GONE
									: View.VISIBLE);
						}
					});
		} else
		{
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
			mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}

	/**
	 * Represents an asynchronous login/registration task used to authenticate
	 * the user.
	 */
	public class UserLoginTask extends AsyncTask<Void, Void, Boolean>
	{
		@Override
		protected Boolean doInBackground(Void... params)
		{
			// TODO: Securely read password form user

			CoreSecureStringHandler masterkey = mCorePwdFile
					.getMasterKeyHandler();
			masterkey.Clear();
			int len = mPassword.GetLength();
			for (int i = 0; i < len; i++)
			{
				masterkey.AddChar(mPassword.GetChar(i));
			}

			mCorePwdFile.TryOpenAndParse();

			if (mCorePwdFile.IsOpen())
			{
				Intent data = new Intent();
				data.putExtra(Global.EXTRA_CORE_PWD_FILE_INSTANCE, mCorePwdFile);
				setResult(RESULT_OK, data);
				return true;
			}

			try
			{
				// Stop brute force attacks.
				Thread.sleep(2000);
			} catch (InterruptedException e)
			{
				return false;
			}

			return false;
		}

		@Override
		protected void onPostExecute(final Boolean success)
		{
			mAuthTask = null;
			showProgress(false);

			if (success)
			{
				// return to label list
				finish();
			} else
			{
				mPasswordView
						.setError(getString(R.string.error_incorrect_password));
				mPasswordView.requestFocus();
			}
		}

		@Override
		protected void onCancelled()
		{
			mAuthTask = null;
			showProgress(false);
		}
	}
}
