package org.caelus.kryptanandroid;

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
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Activity which displays a login screen to the user, offering registration as
 * well.
 */
public class OpenPasswordFileActivity extends Activity
{
	/**
	 * Keep track of the login task to ensure we can cancel it if requested.
	 */
	private UserLoginTask mAuthTask = null;

	// Values for email and password at the time of the login attempt.
	private String mPassword;

	// UI references.
	private EditText mPasswordView;
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
		
		//check if password file exists
		if(!mCorePwdFile.Exists())
		{
			// it doesn't so we need to create one.
			createNewPwdFile();
		}

		// Set up the login form.

		mPasswordView = (EditText) findViewById(R.id.password);
		mPasswordView
		        .setOnEditorActionListener(new TextView.OnEditorActionListener()
		        {
			        @Override
			        public boolean onEditorAction(TextView textView, int id,
			                KeyEvent keyEvent)
			        {
				        if (id == R.id.login || id == EditorInfo.IME_NULL)
				        {
					        attemptLogin();
					        return true;
				        }
				        return false;
			        }
		        });

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

	private void createNewPwdFile() {
		//TODO: ask user for a master key. For now we simply set one a test key
		CoreSecureStringHandler masterkey = mCorePwdFile.getMasterKeyHandler();
		masterkey.Clear();
		masterkey.AddChar('t');
		masterkey.AddChar('e');
		masterkey.AddChar('s');
		masterkey.AddChar('t');
		
		mCorePwdFile.CreateNew();
		mCorePwdFile.Save();
		
		if(mCorePwdFile.IsOpen())
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
		mPassword = mPasswordView.getText().toString();
		
		boolean cancel = false;
		View focusView = null;

		// Check for a valid password.
		if (TextUtils.isEmpty(mPassword))
		{
			mPasswordView.setError(getString(R.string.error_field_required));
			focusView = mPasswordView;
			cancel = true;
		} else if (mPassword.length() < 4)
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
			//TODO: Securely read password form user
			
			CoreSecureStringHandler masterkey = mCorePwdFile.getMasterKeyHandler();
			masterkey.Clear();
			int len = mPassword.length();
            for (int i = 0; i < len; i++) {
                masterkey.AddChar(mPassword.charAt(i));
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

			// TODO: register the new account here.
			return false;
		}

		@Override
		protected void onPostExecute(final Boolean success)
		{
			mAuthTask = null;
			showProgress(false);

			if (success)
			{
				//return to label list
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
