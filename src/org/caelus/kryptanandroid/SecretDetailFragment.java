package org.caelus.kryptanandroid;

import java.util.ArrayList;

import org.caelus.kryptanandroid.buildingblocks.EditLabelsAlert;
import org.caelus.kryptanandroid.buildingblocks.SecureTextView;
import org.caelus.kryptanandroid.buildingblocks.SingleEditTextAlert;
import org.caelus.kryptanandroid.buildingblocks.SingleEditTextAlert.DialogResultListener;
import org.caelus.kryptanandroid.core.CorePwd;
import org.caelus.kryptanandroid.core.CorePwdFile;
import org.caelus.kryptanandroid.core.CoreSecureStringHandler;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

/**
 * A fragment representing a single Secret detail screen. This fragment is
 * either contained in a {@link SecretListActivity} in two-pane mode (on
 * tablets) or a {@link SecretDetailActivity} on handsets.
 */
public class SecretDetailFragment extends Fragment implements OnClickListener,
		DialogInterface.OnDismissListener, DialogResultListener
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

		if (getArguments().containsKey(Global.EXTRA_CORE_PWD)
				&& getArguments().containsKey(
						Global.EXTRA_CORE_PWD_FILE_INSTANCE))
		{
			mPwd = (CorePwd) getArguments()
					.getParcelable(Global.EXTRA_CORE_PWD);
			mPwdFile = (CorePwdFile) getArguments().getParcelable(
					Global.EXTRA_CORE_PWD_FILE_INSTANCE);
		} else
		{
			throw new IllegalArgumentException(
					"Arguments must include both EXTRA_CORE_PWD and EXTRA_CORE_PWD_FILE_INSTANCE instances.");
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
			//we should let the list know that the content may hav been updated.
			activity.refreshListContents();
		} else
		{
			// apperently not in two pane mode, lets just ignore this
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

	private void copyToClipboard(String name, String message,
			CoreSecureStringHandler src)
	{
		// Copy to clipboard
		ClipboardManager clipboard = (ClipboardManager) getActivity()
				.getSystemService(Context.CLIPBOARD_SERVICE);

		int size = src.GetLength();
		char[] arr = new char[size];
		for (int i = 0; i < size; i++)
		{
			arr[i] = src.GetChar(i);
		}

		String string = new String(arr);

		ClipData clip = ClipData.newPlainText("Password username", string);
		clipboard.setPrimaryClip(clip);

		// lets destroy this evil unsecure string with voodo reflection
		CoreSecureStringHandler.overwriteStringInternalArr(string);

		// Lets also destroy our temporary array
		for (int i = 0; i < size; i++)
		{
			arr[i] = 0;
		}

		Toast toast = Toast
				.makeText(getActivity(), message, Toast.LENGTH_SHORT);
		toast.show();
	}

	public void copyDescription()
	{
		copyToClipboard("kryptan password description",
				getString(R.string.details_toast_description_copied),
				mDescription);
	}

	public void copyUsername()
	{
		copyToClipboard("kryptan password username",
				getString(R.string.details_toast_username_copied), mUsername);
	}

	public void copyPassword()
	{
		copyToClipboard("kryptan password description",
				getString(R.string.details_toast_password_copied), mPassword);
	}

	private final int Description = 1;
	private final int Username = 2;

	public void editDescription()
	{
		SingleEditTextAlert alert = new SingleEditTextAlert(getActivity(),
				mPwdFile, R.string.details_description_edit_title,
				R.string.details_description_edit_message, this, Description);

		alert.setOnDismissListener(this);
		alert.setToastMessage(R.string.details_toast_description_edited);

		alert.show();
	}

	public void editUsername()
	{
		SingleEditTextAlert alert = new SingleEditTextAlert(getActivity(),
				mPwdFile, R.string.details_username_edit_title,
				R.string.details_username_edit_message, this, Username);

		alert.setOnDismissListener(this);
		alert.setToastMessage(R.string.details_toast_username_edited);

		alert.show();
	}

	public void editPassword()
	{
		Intent intent = new Intent(getActivity(),
				GeneratePasswordActivity.class);
		intent.putExtra(Global.EXTRA_CORE_PWD_LABELS, mPwd);
		intent.putExtra(Global.EXTRA_CORE_PWD_FILE_INSTANCE, mPwdFile);
		startActivityForResult(intent, 0);
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
		// TODO: implement this
		Toast toast = Toast.makeText(getActivity(),
				"Action not implemented yet!", Toast.LENGTH_SHORT);
		toast.show();
	}

	@Override
	public void onClick(View v)
	{
		if (v == getActivity().findViewById(R.id.copyDescription))
		{
			copyDescription();
		} else if (v == getActivity().findViewById(R.id.copyUsername))
		{
			copyUsername();
		} else if (v == getActivity().findViewById(R.id.copyPassword))
		{
			copyPassword();
		} else if (v == getActivity().findViewById(R.id.editDescription))
		{
			editDescription();
		} else if (v == getActivity().findViewById(R.id.editUsername))
		{
			editUsername();
		} else if (v == getActivity().findViewById(R.id.editPassword))
		{
			editPassword();
		} else if (v == getActivity().findViewById(R.id.editLabels))
		{
			editLabels();
		} else if (v == getActivity().findViewById(R.id.deletePassword))
		{
			deletePassword();
		}
	}

	@Override
	public void onDismiss(DialogInterface dialog)
	{
		// We end up here when the edit labels dialog has been closed
		refreshContentView();
	}

	@Override
	public boolean onDialogResult(SingleEditTextAlert dialog,
			CoreSecureStringHandler result)
	{
		switch (dialog.getDialogId())
		{
		case Description:
			// validate input
			if (result.GetLength() == 0)
			{
				dialog.setErrorMessage(R.string.error_field_required);
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
			} else
			{
				dialog.setErrorMessage(R.string.error_pwd_already_exists);
				return false;
			}

			break;
		case Username:
			// no validation is needed, username can be empty or whatever else
			mUsername = result;
			mPwd.SetNewUsername(mUsername);
			break;
		default:
			break;

		}
		mPwdFile.Save();
		
		//no need, it will be called in the onDissmiss metod
		//refreshContentView();

		return true;
	}
}
