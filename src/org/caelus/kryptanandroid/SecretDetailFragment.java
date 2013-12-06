package org.caelus.kryptanandroid;

import java.lang.reflect.Field;
import java.util.ArrayList;

import org.caelus.kryptanandroid.core.CorePwd;
import org.caelus.kryptanandroid.core.CoreSecureStringHandler;

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
import android.widget.TextView;
import android.widget.Toast;

/**
 * A fragment representing a single Secret detail screen. This fragment is
 * either contained in a {@link SecretListActivity} in two-pane mode (on
 * tablets) or a {@link SecretDetailActivity} on handsets.
 */
public class SecretDetailFragment extends Fragment implements OnClickListener
{
	/**
	 * The fragment argument representing the item ID that this fragment
	 * represents.
	 */
	public static final String ARG_ITEM = "org.caelus.kryptanandroid.detail_pwd_arg";
	private ArrayList<CharSequence> mLabels = new ArrayList<CharSequence>();
	private CorePwd mPwd;
	private CoreSecureStringHandler mDescription;
	private CoreSecureStringHandler mUsername;
	private CoreSecureStringHandler mPassword;
	private View mRootView;

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

		if (getArguments().containsKey(ARG_ITEM))
		{
			mPwd = (CorePwd) getArguments().getParcelable(ARG_ITEM);
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
		TextView description = (TextView) mRootView
				.findViewById(R.id.descriptionText);
		TextView username = (TextView) mRootView.findViewById(R.id.usernameText);
		TextView password = (TextView) mRootView.findViewById(R.id.passwordText);
		TextView labels = (TextView) mRootView.findViewById(R.id.labelsText);

		if (mPwd != null)
		{
			mDescription = mPwd.GetDescriptionCopy();
			mUsername = mPwd.GetUsernameCopy();
			mPassword = mPwd.GetPasswordCopy();

			// TODO: Change to more secure text output
			String desc = "";
			String user = "";
			String pass = "";
			int size = mDescription.GetLength();
			for (int i = 0; i < size; i++)
			{
				desc += mDescription.GetChar(i);
			}
			size = mUsername.GetLength();
			for (int i = 0; i < size; i++)
			{
				user += mUsername.GetChar(i);
			}
			size = mPassword.GetLength();
			for (int i = 0; i < size; i++)
			{
				pass += mPassword.GetChar(i);
			}

			description.setText(desc);
			username.setText(user);
			password.setText(pass);

			mLabels.clear();
			mLabels.add("Dymmy label 1");
			mLabels.add("Dymmy label 2");
			mLabels.add("Dymmy label 3");

			String tmp = mLabels.get(0).toString();
			for (int i = 1; i < mLabels.size(); i++)
			{
				tmp += ", " + mLabels.get(i).toString();
			}
			labels.setText(tmp);
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

	private void overwriteStringInternalArr(String toOverwrite)
	{
		try
		{
			Field fVal = String.class.getDeclaredField("value");
			fVal.setAccessible(true);
			char[] value = (char[]) fVal.get(toOverwrite);
			for(int i=0; i<value.length; i++)
			{
				value[i] = '\0';
			}
			//All done, there should now, hopefully be no traces of this string left in memory now.
		} catch (NoSuchFieldException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
		overwriteStringInternalArr(string);

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
		copyToClipboard("kryptan password description", "Description copied!",
				mDescription);
	}

	public void copyUsername()
	{
		copyToClipboard("kryptan password username", "Password copied!",
				mDescription);
	}

	public void copyPassword()
	{
		copyToClipboard("kryptan password description", "Description copied!",
				mDescription);
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
		startActivityForResult(intent, 0);
	}

	public void editLabels()
	{
		Intent intent = new Intent(getActivity(), EditLabelsActivity.class);
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
