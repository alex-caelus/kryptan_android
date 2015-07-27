package org.caelus.kryptanandroid;

import java.util.ArrayList;

import org.caelus.kryptanandroid.buildingblocks.BaseAlert;
import org.caelus.kryptanandroid.buildingblocks.SecureEditText;
import org.caelus.kryptanandroid.buildingblocks.SecureEditText.SecureEditTextChangedListener;
import org.caelus.kryptanandroid.core.CorePwd;
import org.caelus.kryptanandroid.core.CorePwdFile;
import org.caelus.kryptanandroid.core.CoreSecureStringHandler;

import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.ToggleButton;

public class GeneratePasswordDialog extends BaseAlert
{

	private SecureEditText mPasswordEditText;
	private SecureEditText mDescriptionEditText;
	private Button mPasswordCopyButton;
	private Button mGenerateButton;
	private SeekBar mNumberOfCharctersSeekbar;
	private TextView mNumberOfCharctersTextView;
	private SecureEditText mUsernameEditText;
	private PasswordCreatedListener mCreatedListener;
	private ToggleButton mUseSpecialsToggle;
	private ToggleButton mUseNonEnglishToggle;
	private CorePwd mEditPwd;
	private boolean mEditExisting;

	interface PasswordCreatedListener
	{
		void onPasswordCreated(CorePwd pwd);
	}
	
	interface PasswordEditedListener
	{
		void onPasswordCreated(CorePwd pwd);
	}

	protected GeneratePasswordDialog(Activity activity, CorePwdFile pwdFile,
			PasswordCreatedListener finishedListener)
	{
		super(activity, pwdFile, R.string.title_generate_password,
				BaseAlert.BUTTONS_OK | BaseAlert.BUTTONS_CANCEL, false);

		mCreatedListener = finishedListener;
		mEditExisting = false;
	}

	protected GeneratePasswordDialog(Activity activity, CorePwdFile pwdFile,
			CorePwd editMe)
	{
		super(activity, pwdFile, R.string.title_edit_password,
				BaseAlert.BUTTONS_OK | BaseAlert.BUTTONS_CANCEL, false);

		mEditPwd = editMe;
		mEditExisting = true;
	}

	@Override
	protected void onInit()
	{

	}

	@Override
	protected View getView()
	{
		ViewGroup contents = (ViewGroup) mActivity.getLayoutInflater().inflate(
				R.layout.dialog_generate_password, null);

		mPasswordEditText = (SecureEditText) contents
				.findViewById(R.id.newPasswordTextEdit);

		mDescriptionEditText = (SecureEditText) contents
				.findViewById(R.id.newPasswordDescriptionEditText);
		mUsernameEditText = (SecureEditText) contents
				.findViewById(R.id.newPasswordUsernameEditText);

		mPasswordCopyButton = (Button) contents
				.findViewById(R.id.newPasswordCopyButton);

		mGenerateButton = (Button) contents
				.findViewById(R.id.newPasswordGenerateButton);

		mNumberOfCharctersTextView = (TextView) contents
				.findViewById(R.id.newPasswordNumberOfCharactersText);

		mNumberOfCharctersSeekbar = (SeekBar) contents
				.findViewById(R.id.newPasswordNumberOfCharactersSeekbar);

		mUseSpecialsToggle = (ToggleButton) contents.findViewById(R.id.newPasswordSpecialCharacterToggle);
		mUseNonEnglishToggle = (ToggleButton) contents.findViewById(R.id.newPasswordNonEnglishToggle);

		setListeners();

		mPasswordCopyButton.setEnabled(false);

		mNumberOfCharctersSeekbar.setMax(Global.GENERATE_CHARACTERS_MAX
				- Global.GENERATE_CHARACTERS_MIN);
		mNumberOfCharctersSeekbar
				.setProgress(Global.GENERATE_CHARACTERS_DEFAULT
						- Global.GENERATE_CHARACTERS_MIN);
		
		//default value
		mUseSpecialsToggle.setChecked(Global.GENERATE_CHARACTERS_USE_SPECIALS_DEFAULT);
		mUseNonEnglishToggle.setChecked(Global.GENERATE_CHARACTERS_USE_NON_ENGLISH_DEFAULT);
		
		if(mEditExisting)
		{
			mDescriptionEditText.setVisibility(View.GONE);
			mUsernameEditText.setVisibility(View.GONE);
			contents.findViewById(R.id.newPasswordDescriptionText).setVisibility(View.GONE);
			contents.findViewById(R.id.newPasswordUsernameText).setVisibility(View.GONE);
			
			mPasswordEditText.setSecureText(mEditPwd.GetPasswordCopy());
		}

		return contents;
	}

	private void setListeners()
	{

		mPasswordEditText
				.setOnSecureTextChangedListener(new SecureEditTextChangedListener()
				{
					@Override
					public void SecureEditTextChanged(CoreSecureStringHandler text)
					{
						passwordChanged(text);
					}
				});
		mPasswordCopyButton.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				GeneratePasswordDialog.this.copyCurrentPasswordClick();
			}
		});
		mGenerateButton.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				generatePasswordClicked();
			}
		});
		mNumberOfCharctersSeekbar
				.setOnSeekBarChangeListener(new OnSeekBarChangeListener()
				{

					@Override
					public void onStopTrackingTouch(SeekBar seekBar)
					{
					}

					@Override
					public void onStartTrackingTouch(SeekBar seekBar)
					{
					}

					@Override
					public void onProgressChanged(SeekBar seekBar,
							int progress, boolean fromUser)
					{
						GeneratePasswordDialog.this
								.onNumberOfCharactersChange(progress);
					}
				});
	}

	protected void generatePasswordClicked()
	{
		int numberofcharacters = mNumberOfCharctersSeekbar.getProgress()
				+ Global.GENERATE_CHARACTERS_MIN;
		boolean useSpecials = mUseSpecialsToggle.isChecked();
		boolean useNonEnglish = mUseNonEnglishToggle.isChecked();
		
		final String alphaNumericals = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
		final String specials = ".,-_+!?\"'`#@*§&%:;^<=>$~(/){|}[\\]";
		final String nonEnglish = "¤£¥ßÜüµÆæØøÅåÄäÖö"; 
		
		String characterPool = alphaNumericals;
		if(useSpecials)
			characterPool += specials;
		if(useNonEnglish)
			characterPool += nonEnglish;
		
		CoreSecureStringHandler password = CoreSecureStringHandler.NewSecureString();
		
		for(int i=0; i<numberofcharacters; i++)
		{
			int randomnumber = (int) (Math.random() * characterPool.length());
			password.Append("" + characterPool.charAt(randomnumber));
		}
		
		mPasswordEditText.setSecureText(password);
		
		passwordChanged(password);
	}

	protected void passwordChanged(CoreSecureStringHandler string)
	{
		boolean enabled = mPasswordCopyButton.isEnabled();
		if (enabled == (string.GetLength() == 0))
		{
			mPasswordCopyButton.setEnabled(!enabled);
		}
	}

	protected void onNumberOfCharactersChange(int number)
	{
		number += Global.GENERATE_CHARACTERS_MIN; // set minimum number of
													// characters
		mNumberOfCharctersTextView.setText(String.format(
				mActivity.getString(R.string.number_of_characters_to_generate),
				number));
	}

	protected void copyCurrentPasswordClick()
	{
		CoreSecureStringHandler src = mPasswordEditText.getSecureText();

		if (src.GetLength() > 0)
		{
			SecretDetailFragment
					.copyToClipboard(
							mActivity,
							"kryptan generated password",
							mActivity
									.getString(R.string.details_toast_password_copied),
							src);
		}
	}

	@Override
	protected boolean onPositiveClicked()
	{
		if(mEditExisting)
			return editExistingPasswordClicked();
		else
			return createNewPasswordClicked();
	}

	private boolean editExistingPasswordClicked()
	{
		CoreSecureStringHandler password = mPasswordEditText.getSecureText();
		if (password.GetLength() == 0)
		{
			mPasswordEditText.setError(mActivity
					.getString(R.string.error_field_required));
			mPasswordEditText.requestFocus();
			return false;
		}
		
		mEditPwd.SetNewPassword(password);
		
		return true;
	}

	private boolean createNewPasswordClicked()
	{
		CoreSecureStringHandler password = mPasswordEditText.getSecureText();
		CoreSecureStringHandler description = mDescriptionEditText
				.getSecureText();
		CoreSecureStringHandler username = mUsernameEditText.getSecureText();

		if(!validateNewPasswordAndDescription(password, description))
			return false;

		// create new password
		CorePwd pwd;

		if (username.GetLength() > 0)
		{
			pwd = mPwdFile.getPasswordList().createPwd(description, username,
					password);
		} else
		{
			pwd = mPwdFile.getPasswordList().createPwd(description, password);
		}

		mPwdFile.SaveWithDialog(mActivity);

		if (pwd == null)
		{
			return false;
		}

		mCreatedListener.onPasswordCreated(pwd);

		return true;
	}

	private boolean validateNewPasswordAndDescription(
			CoreSecureStringHandler password,
			CoreSecureStringHandler description)
	{
		// Validate inputs
		if (password.GetLength() == 0)
		{
			mPasswordEditText.setError(mActivity
					.getString(R.string.error_field_required));
			mPasswordEditText.requestFocus();
			return false;
		}
		if (description.GetLength() == 0)
		{
			mDescriptionEditText.setError(mActivity
					.getString(R.string.error_field_required));
			mDescriptionEditText.requestFocus();
			return false;
		}

		// validate description
		{
			ArrayList<CorePwd> filterResults = mPwdFile.getPasswordList()
					.filter(description);
			boolean exactMatch = false;

			// check if there already exists a password with the same
			// description
			for (CorePwd filtered : filterResults)
			{
				if (filtered.GetDescriptionCopy().GetLength() == description
						.GetLength())
				{
					exactMatch = true;
				}
			}
			if (exactMatch)
			{
				mDescriptionEditText.setError(mActivity
						.getString(R.string.error_pwd_already_exists));
				mDescriptionEditText.requestFocus();
				return false;
			}
		}
		
		return true;
	}
}
