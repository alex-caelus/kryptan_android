package org.caelus.kryptanandroid;

import org.caelus.kryptanandroid.core.CorePwd;
import org.caelus.kryptanandroid.core.CorePwdFile;
import org.caelus.kryptanandroid.core.CoreSecureStringHandler;
import org.caelus.kryptanandroid.views.SecureTextView;

import android.app.AlertDialog;
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
import android.widget.EditText;
import android.widget.Toast;

/**
 * A fragment representing a single Secret detail screen. This fragment is
 * either contained in a {@link SecretListActivity} in two-pane mode (on
 * tablets) or a {@link SecretDetailActivity} on handsets.
 */
public class SecretDetailFragment extends Fragment implements OnClickListener
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

	private enum EditDestinations
	{
		Description, Username
	}

	public void promtNewStringFromUserDialog(int titleStringId,
			int messageStringId, final int toastStringId,
			final EditDestinations destination)
	{
		final EditText input = new EditText(getActivity());
		AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
		alert.setTitle(getResources().getString(titleStringId));
		alert.setMessage(getResources().getString(messageStringId));
		alert.setView(input);
		alert.setPositiveButton(getResources().getString(R.string.save),
				new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int whichButton)
					{
						// TODO: switch to safer input
						String unsafe = input.getText().toString();
						int size = unsafe.length();

						switch (destination)
						{
						case Description:
							mDescription.Clear();
							for (int i = 0; i < size; i++)
							{
								mDescription.AddChar(unsafe.charAt(i));
							}
							mPwd.SetNewDescription(mDescription);
							break;
						case Username:
							mUsername.Clear();
							for (int i = 0; i < size; i++)
							{
								mUsername.AddChar(unsafe.charAt(i));
							}
							mPwd.SetNewUsername(mUsername);
							break;
						default:
							break;

						}
						mPwdFile.Save();
						refreshContentView();

						Toast toast = Toast.makeText(getActivity(),
								getResources().getString(toastStringId),
								Toast.LENGTH_SHORT);
						toast.show();
					}
				});
		alert.setNegativeButton(getResources().getString(R.string.cancel), null);
		alert.show();
	}

	public void editDescription()
	{
		promtNewStringFromUserDialog(R.string.details_description_edit_title,
				R.string.details_description_edit_message,
				R.string.details_toast_description_edited,
				EditDestinations.Description);
	}

	public void editUsername()
	{
		promtNewStringFromUserDialog(R.string.details_username_edit_title,
				R.string.details_username_edit_message,
				R.string.details_toast_username_edited,
				EditDestinations.Username);
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
		Intent intent = new Intent(getActivity(), EditLabelsActivity.class);
		intent.putExtra(Global.EXTRA_CORE_PWD_LABELS, mPwd);
		intent.putExtra(Global.EXTRA_CORE_PWD_FILE_INSTANCE, mPwdFile);
		startActivityForResult(intent, 0);
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
}
