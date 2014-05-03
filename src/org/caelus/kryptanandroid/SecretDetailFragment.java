package org.caelus.kryptanandroid;

import java.util.ArrayList;

import org.caelus.kryptanandroid.buildingblocks.BaseAlert;
import org.caelus.kryptanandroid.buildingblocks.EditLabelsAlert;
import org.caelus.kryptanandroid.buildingblocks.KryptanKeyboard;
import org.caelus.kryptanandroid.buildingblocks.KryptanKeyboard.KeyboardCloseListener;
import org.caelus.kryptanandroid.buildingblocks.SecureTextView;
import org.caelus.kryptanandroid.core.CorePwd;
import org.caelus.kryptanandroid.core.CorePwdFile;
import org.caelus.kryptanandroid.core.CoreSecureStringHandler;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * A fragment representing a single Secret detail screen. This fragment is
 * either contained in a {@link SecretListActivity} in two-pane mode (on
 * tablets) or a {@link SecretDetailActivity} on handsets.
 */
public class SecretDetailFragment extends Fragment implements OnClickListener,
		DialogInterface.OnDismissListener, KeyboardCloseListener
{
	private CorePwd mPwd;
	private CoreSecureStringHandler mDescription;
	private CoreSecureStringHandler mUsername;
	private CoreSecureStringHandler mPassword;
	private View mRootView;
	private CorePwdFile mPwdFile;

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public SecretDetailFragment()
	{
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		if (!getArguments().containsKey(Global.EXTRA_CORE_PWD)
				|| !getArguments().containsKey(
						Global.EXTRA_CORE_PWD_FILE_INSTANCE))
		{
			throw new IllegalArgumentException(
					"Arguments must include both EXTRA_CORE_PWD and EXTRA_CORE_PWD_FILE_INSTANCE instances.");
		} else
		{
			mPwd = (CorePwd) getArguments()
					.getParcelable(Global.EXTRA_CORE_PWD);
			mPwdFile = (CorePwdFile) getArguments().getParcelable(
					Global.EXTRA_CORE_PWD_FILE_INSTANCE);

			if (getArguments().containsKey(
					Global.EXTRA_CORE_PASSWORD_IS_NEWLY_CREATED))
			{
				// lets show the editLabel dialog for this newly created password
				editLabels();

				Toast.makeText(getActivity(),
						getString(R.string.new_password_added_toast),
						Toast.LENGTH_SHORT).show();
			}
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		mRootView = inflater.inflate(R.layout.fragment_secret_detail,
				container, false);

		refreshContentView();

		addOnClickListeners();

		return mRootView;
	}

	private void refreshContentView()
	{
		// Show the dummy content as text in a TextView.
		SecureTextView description = (SecureTextView) mRootView
				.findViewById(R.id.descriptionText);
		SecureTextView username = (SecureTextView) mRootView
				.findViewById(R.id.usernameText);
		SecureTextView password = (SecureTextView) mRootView
				.findViewById(R.id.passwordText);
		SecureTextView labels = (SecureTextView) mRootView
				.findViewById(R.id.labelsText);

		if (mPwd != null)
		{
			mDescription = mPwd.GetDescriptionCopy();
			mUsername = mPwd.GetUsernameCopy();
			mPassword = mPwd.GetPasswordCopy();

			description.setSecureText(mDescription);
			username.setSecureText(mUsername);
			password.setSecureText(mPassword);

			CoreSecureStringHandler[] labelHandlers = mPwd.GetLabels();

			CoreSecureStringHandler labelText = CoreSecureStringHandler
					.NewSecureString();
			for (int j = 0; j < labelHandlers.length; j++)
			{
				int length = labelHandlers[j].GetLength();
				for (int i = 0; i < length; i++)
				{
					labelText.AddChar(labelHandlers[j].GetChar(i));
				}
				if (j < labelHandlers.length - 1)
				{
					labelText.AddChar(',');
					labelText.AddChar(' ');
				}
			}

			labels.setSecureText(labelText);
		}

		// if we are in two pane mode
		if (getActivity() instanceof SecretListActivity)
		{
			SecretListActivity activity = (SecretListActivity) getActivity();
			// we should let the list know that the content may hav been
			// updated.
			activity.refreshListContents();
		} else
		{
			// Apparently not in two pane mode, lets just ignore this
		}
	}

	private void addOnClickListeners()
	{
		// listen to clicks
		Button button;
		button = ((Button) mRootView.findViewById(R.id.copyDescription));
		if (button != null)
			button.setOnClickListener(this);

		button = ((Button) mRootView.findViewById(R.id.copyUsername));
		if (button != null)
			button.setOnClickListener(this);

		button = ((Button) mRootView.findViewById(R.id.copyPassword));
		if (button != null)
			button.setOnClickListener(this);

		button = ((Button) mRootView.findViewById(R.id.editDescription));
		if (button != null)
			button.setOnClickListener(this);

		button = ((Button) mRootView.findViewById(R.id.editUsername));
		if (button != null)
			button.setOnClickListener(this);

		button = ((Button) mRootView.findViewById(R.id.editPassword));
		if (button != null)
			button.setOnClickListener(this);

		button = ((Button) mRootView.findViewById(R.id.editLabels));
		if (button != null)
			button.setOnClickListener(this);

		button = ((Button) mRootView.findViewById(R.id.deletePassword));
		if (button != null)
			button.setOnClickListener(this);
	}

	public static void copyToClipboard(Activity activity, String name,
			String message, CoreSecureStringHandler src)
	{
		// Copy to clipboard
		ClipboardManager clipboard = (ClipboardManager) activity
				.getSystemService(Context.CLIPBOARD_SERVICE);

		int size = src.GetLength();
		char[] arr = new char[size];
		for (int i = 0; i < size; i++)
		{
			arr[i] = src.GetChar(i);
		}

		String string = new String(arr);

		ClipData clip = ClipData.newPlainText(name, string);
		clipboard.setPrimaryClip(clip);

		// lets destroy this evil unsecure string with voodo reflection
		CoreSecureStringHandler.overwriteStringInternalArr(string);

		// Lets also destroy our temporary array
		for (int i = 0; i < size; i++)
		{
			arr[i] = 0;
		}

		Toast toast = Toast.makeText(activity, message, Toast.LENGTH_SHORT);
		toast.show();
	}

	public void copyDescription()
	{
		copyToClipboard(getActivity(), "kryptan password description",
				getString(R.string.details_toast_description_copied),
				mDescription);
	}

	public void copyUsername()
	{
		copyToClipboard(getActivity(), "kryptan password username",
				getString(R.string.details_toast_username_copied), mUsername);
	}

	public void copyPassword()
	{
		copyToClipboard(getActivity(), "kryptan password description",
				getString(R.string.details_toast_password_copied), mPassword);
	}

	private final int Description = 1;
	private final int Username = 2;

	public void editDescription()
	{
		KryptanKeyboard keyboard = new KryptanKeyboard(getActivity(),
				getString(R.string.details_description_edit_title));
		keyboard.setId(Description);
		keyboard.setHintText(getString(R.string.details_description_edit_message));
		keyboard.setCloseValidator(this);
		keyboard.setSecureText(mDescription);
		keyboard.show();
	}

	public void editUsername()
	{
		KryptanKeyboard keyboard = new KryptanKeyboard(getActivity(),
				getString(R.string.details_username_edit_title));
		keyboard.setId(Username);
		keyboard.setHintText(getString(R.string.details_username_edit_message));
		keyboard.setCloseValidator(this);
		keyboard.setSecureText(mUsername);
		keyboard.show();
	}

	public void editPassword()
	{
		GeneratePasswordDialog dialog = new GeneratePasswordDialog(getActivity(), mPwdFile, mPwd);
		dialog.setToastMessage(R.string.details_toast_password_edited);
		dialog.setOnDismissListener(this);
		dialog.show();
	}

	public void editLabels()
	{
		EditLabelsAlert alert = new EditLabelsAlert(getActivity(), mPwdFile,
				mPwd);
		alert.setOnDismissListener(this);
		alert.show();
	}

	public void deletePassword()
	{
		BaseAlert confirm = new BaseAlert(getActivity(), mPwdFile, R.string.details_delete_password_button, BaseAlert.BUTTONS_OK | BaseAlert.BUTTONS_CANCEL, false)
		{
			
			@Override
			protected boolean onPositiveClicked()
			{
				mPwdFile.getPasswordList().deletePwd(mPwd);
				mPwdFile.SaveWithDialog(mActivity);
				if (getActivity() instanceof SecretListActivity)
				{
					SecretListActivity activity = (SecretListActivity) getActivity();
					// we should let the list know that the content no longer exists
					activity.deselectCurrentlySelected();
				}
				else if(getActivity() instanceof SecretDetailActivity)
				{
					getActivity().finish();
				}
				return true;
			}
			
			@Override
			protected void onInit()
			{
			}
			
			@Override
			protected View getView()
			{
				TextView message = new TextView(mActivity);
				message.setTextAppearance(mActivity, android.R.style.TextAppearance_Medium);
				message.setText(R.string.details_delete_password_confirm);
				message.setPadding(
						(int) TypedValue
						.applyDimension(
								TypedValue.COMPLEX_UNIT_DIP, 
								14, 
								getResources()
								.getDisplayMetrics()
						), 0, 0, 0);
				return message;
			}
		};
		confirm.show();
	}

	@Override
	public void onClick(View v)
	{
		switch(v.getId())
		{
		case R.id.copyDescription:
			copyDescription();
			break;
		case R.id.copyUsername:
			copyUsername();
			break;
		case R.id.copyPassword:
			copyPassword();
			break;
		case R.id.editDescription:
			editDescription();
			break;
		case R.id.editUsername:
			editUsername();
			break;
		case R.id.editPassword:
			editPassword();
			break;
		case R.id.editLabels:
			editLabels();
			break;
		case R.id.deletePassword:
			deletePassword();
			break;
		}
	}

	@Override
	public void onDismiss(DialogInterface dialog)
	{
		// We end up here when the edit labels/password dialog has been closed
		refreshContentView();
	}

	@Override
	public boolean KeyboardCloseValidate(KryptanKeyboard keyboard,
			CoreSecureStringHandler result)
	{
		switch (keyboard.getId())
		{
		case Description:
			// validate input
			if (result.GetLength() == 0)
			{
				keyboard.setError(getString(R.string.error_field_required));
				return false;
			}
			ArrayList<CorePwd> filterResults = mPwdFile.getPasswordList()
					.filter(result);
			boolean exactMatch = false;
			for (CorePwd filtered : filterResults)
			{
				if (filtered.GetDescriptionCopy().GetLength() == result
						.GetLength())
				{
					exactMatch = true;
				}
			}
			if (!exactMatch)
			{
				mDescription = result;
				mPwd.SetNewDescription(mDescription);
				Toast.makeText(getActivity(),
						getString(R.string.details_toast_description_edited),
						Toast.LENGTH_SHORT).show();
			} else
			{
				keyboard.setError(getString(R.string.error_pwd_already_exists));
				return false;
			}

			break;
		case Username:
			// no validation is needed, username can be empty or whatever else
			mUsername = result;
			mPwd.SetNewUsername(mUsername);
			Toast.makeText(getActivity(),
					getString(R.string.details_toast_username_edited),
					Toast.LENGTH_SHORT).show();
			break;
		default:
			break;

		}
		mPwdFile.SaveWithDialog(getActivity());

		// no need, it will be called in the onDissmiss metod
		refreshContentView();

		return true;
	}

	@Override
	public void KeyboardShowChanged(KryptanKeyboard keyboard, boolean isShowing)
	{
	}
}
