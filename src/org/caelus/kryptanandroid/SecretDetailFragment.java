package org.caelus.kryptanandroid;

import java.util.ArrayList;

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
	public static final String ARG_ITEM_ID = "item_id";
	private CharSequence mDescription;
	private String mUsername;
	private String mPassword;
	private ArrayList<CharSequence> mLabels = new ArrayList<CharSequence>();

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

		if (getArguments().containsKey(ARG_ITEM_ID))
		{
			// TODO: Get real password from id
			// getArguments().getString(ARG_ITEM_ID);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	        Bundle savedInstanceState)
	{
		View rootView = inflater.inflate(R.layout.fragment_secret_detail,
		        container, false);

		// Show the dummy content as text in a TextView.
		TextView description = (TextView) rootView
		        .findViewById(R.id.descriptionText);
		TextView username = (TextView) rootView.findViewById(R.id.usernameText);
		TextView password = (TextView) rootView.findViewById(R.id.passwordText);
		TextView labels = (TextView) rootView.findViewById(R.id.labelsText);

		if (getArguments().containsKey(ARG_ITEM_ID))
		{
			mDescription = getArguments().getString(ARG_ITEM_ID);
			mUsername = "Username for " + getArguments().getString(ARG_ITEM_ID);
			mPassword = getArguments().getString(ARG_ITEM_ID);

			description.setText(mDescription);
			username.setText(mUsername);
			password.setText(mPassword);

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

		// listen to clicks
		Button button;
		button = ((Button) rootView.findViewById(R.id.copyDescription));
		if (button != null)
			button.setOnClickListener(this);

		button = ((Button) rootView.findViewById(R.id.copyUsername));
		if (button != null)
			button.setOnClickListener(this);

		button = ((Button) rootView.findViewById(R.id.copyPassword));
		if (button != null)
			button.setOnClickListener(this);

		button = ((Button) rootView.findViewById(R.id.editDescription));
		if (button != null)
			button.setOnClickListener(this);

		button = ((Button) rootView.findViewById(R.id.editUsername));
		if (button != null)
			button.setOnClickListener(this);

		button = ((Button) rootView.findViewById(R.id.editPassword));
		if (button != null)
			button.setOnClickListener(this);

		button = ((Button) rootView.findViewById(R.id.editLabels));
		if (button != null)
			button.setOnClickListener(this);

		button = ((Button) rootView.findViewById(R.id.deletePassword));
		if (button != null)
			button.setOnClickListener(this);

		return rootView;
	}

	public void copyDescription()
	{
		// Copy to clipboard
		ClipboardManager clipboard = (ClipboardManager) getActivity()
		        .getSystemService(Context.CLIPBOARD_SERVICE);
		ClipData clip = ClipData.newPlainText("Password Description",
		        mDescription);
		clipboard.setPrimaryClip(clip);

		Toast toast = Toast.makeText(getActivity(), "Description copied!",
		        Toast.LENGTH_SHORT);
		toast.show();
	}

	public void copyUsername()
	{
		// Copy to clipboard
		ClipboardManager clipboard = (ClipboardManager) getActivity()
		        .getSystemService(Context.CLIPBOARD_SERVICE);
		ClipData clip = ClipData.newPlainText("Password username", mUsername);
		clipboard.setPrimaryClip(clip);

		Toast toast = Toast.makeText(getActivity(), "Username copied!",
		        Toast.LENGTH_SHORT);
		toast.show();
	}

	public void copyPassword()
	{
		// Copy to clipboard
		ClipboardManager clipboard = (ClipboardManager) getActivity()
		        .getSystemService(Context.CLIPBOARD_SERVICE);
		ClipData clip = ClipData.newPlainText("Password value", mPassword);
		clipboard.setPrimaryClip(clip);

		Toast toast = Toast.makeText(getActivity(), "Password copied!",
		        Toast.LENGTH_SHORT);
		toast.show();
	}

	public void editDescription()
	{
		final EditText input = new EditText(getActivity());
		AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
		alert.setTitle(getResources().getString(
		        R.string.details_description_edit_title));
		alert.setMessage(getResources().getString(
		        R.string.details_description_edit_message));
		alert.setView(input);
		alert.setPositiveButton(getResources().getString(R.string.save),
		        new DialogInterface.OnClickListener()
		        {
			        public void onClick(DialogInterface dialog, int whichButton)
			        {
				        mDescription = input.getText().toString();
				        Toast toast = Toast
				                .makeText(
				                        getActivity(),
				                        getResources()
				                                .getString(
				                                        R.string.details_toast_description_edited),
				                        Toast.LENGTH_SHORT);
				        toast.show();
			        }
		        });
		alert.setNegativeButton(getResources().getString(R.string.cancel), null);
		alert.show();
	}

	public void editUsername()
	{
		final EditText input = new EditText(getActivity());
		AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
		alert.setTitle(getResources().getString(
		        R.string.details_username_edit_title));
		alert.setMessage(getResources().getString(
		        R.string.details_username_edit_message));
		alert.setView(input);
		alert.setPositiveButton(getResources().getString(R.string.save),
		        new DialogInterface.OnClickListener()
		        {
			        public void onClick(DialogInterface dialog, int whichButton)
			        {
				        mUsername = input.getText().toString();
				        Toast toast = Toast
				                .makeText(
				                        getActivity(),
				                        getResources()
				                                .getString(
				                                        R.string.details_toast_username_edited),
				                        Toast.LENGTH_SHORT);
				        toast.show();
			        }
		        });
		alert.setNegativeButton(getResources().getString(R.string.cancel), null);
		alert.show();
	}

	public void editPassword()
	{
		Intent intent = new Intent(getActivity(), GeneratePasswordActivity.class);
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
