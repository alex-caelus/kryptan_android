package org.caelus.kryptanandroid;

import org.caelus.kryptanandroid.buildingblocks.SecureTextView;
import org.caelus.kryptanandroid.core.CorePwd;
import org.caelus.kryptanandroid.core.CoreSecureStringHandler;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ListView;

import com.qustom.dialog.QustomDialogBuilder;

/**
 * A list fragment representing a list of Secrets. This fragment also supports
 * tablet devices by allowing list items to be given an 'activated' state upon
 * selection. This helps indicate which item is currently being viewed in a
 * {@link SecretDetailFragment}.
 * <p>
 * Activities containing this fragment MUST implement the {@link Callbacks}
 * interface.
 */
public class SecretListFragment extends ListFragment
{

	/**
	 * The serialization (saved instance state) Bundle key representing the
	 * activated item position. Only used on tablets.
	 */
	private static final String STATE_ACTIVATED_POSITION = "activated_position";

	/**
	 * The fragment's current callback object, which is notified of list item
	 * clicks.
	 */
	private Callbacks mCallbacks = sDummyCallbacks;

	/**
	 * The current activated item position. Only used on tablets.
	 */
	private int mActivatedPosition = ListView.INVALID_POSITION;

	/**
	 * The current filter of the listView
	 */
	private CharSequence mCurrentFilter;

	private SecureTextView mCurrentLabelFilterTextView;

	/**
	 * A callback interface that all activities containing this fragment must
	 * implement. This mechanism allows activities to be notified of item
	 * selections.
	 */
	public interface Callbacks
	{
		/**
		 * Callback for when an item has been selected.
		 */
		public void onItemSelected(CorePwd selectedPwd);
	}

	/**
	 * A dummy implementation of the {@link Callbacks} interface that does
	 * nothing. Used only when this fragment is not attached to an activity.
	 */
	private static Callbacks sDummyCallbacks = new Callbacks()
	{
		@Override
		public void onItemSelected(CorePwd id)
		{
		}
	};

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public SecretListFragment()
	{
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		View content = inflater.inflate(R.layout.fragment_secret_list, container);
		
		mCurrentLabelFilterTextView = (SecureTextView) content.findViewById(R.id.listCurrentFilter);
		
		return content;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState)
	{
		super.onViewCreated(view, savedInstanceState);

		// Restore the previously serialized activated item position.
		if (savedInstanceState != null
				&& savedInstanceState.containsKey(STATE_ACTIVATED_POSITION))
		{
			setActivatedPosition(savedInstanceState
					.getInt(STATE_ACTIVATED_POSITION));
		}
	}
	
	public void setCurrentFilterString(CoreSecureStringHandler text)
	{
		mCurrentLabelFilterTextView.setSecureText(text);
	}

	@Override
	public void onAttach(Activity activity)
	{
		super.onAttach(activity);

		// Activities containing this fragment must implement its callbacks.
		if (!(activity instanceof Callbacks))
		{
			throw new IllegalStateException(
					"Activity must implement fragment's callbacks.");
		}

		mCallbacks = (Callbacks) activity;
	}

	@Override
	public void onDetach()
	{
		super.onDetach();

		// Reset the active callbacks interface to the dummy implementation.
		mCallbacks = sDummyCallbacks;
	}

	@Override
	public void onListItemClick(ListView listView, View view, int position,
			long id)
	{
		super.onListItemClick(listView, view, position, id);

		// Notify the active callbacks interface (the activity, if the
		// fragment is attached to one) that an item has been selected.
		mCallbacks.onItemSelected((CorePwd) view.getTag());
	}

	@Override
	public void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);
		if (mActivatedPosition != ListView.INVALID_POSITION)
		{
			// Serialize and persist the activated item position.
			outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
		}
	}

	/**
	 * Turns on activate-on-click mode. When this mode is on, list items will be
	 * given the 'activated' state when touched.
	 */
	public void setActivateOnItemClick(boolean activateOnItemClick)
	{
		// When setting CHOICE_MODE_SINGLE, ListView will automatically
		// give items the 'activated' state when touched.
		getListView().setChoiceMode(
				activateOnItemClick ? ListView.CHOICE_MODE_SINGLE
						: ListView.CHOICE_MODE_NONE);
	}

	private void setActivatedPosition(int position)
	{
		if (position == ListView.INVALID_POSITION)
		{
			getListView().setItemChecked(mActivatedPosition, false);
		} else
		{
			getListView().setItemChecked(position, true);
		}

		mActivatedPosition = position;
	}

	public void showSearch(int numberOfLabels)
	{
		final EditText input = new EditText(getActivity());
		input.setText(mCurrentFilter);
		input.setSelectAllOnFocus(true);
		input.setHint(getResources().getString(R.string.search_filter_hint));

		input.addTextChangedListener(new TextWatcher()
		{
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count)
			{
				ListView listView = getListView();
				if (listView != null)
				{
					SecretAdapter adapter = (SecretAdapter) listView
							.getAdapter();
					if (adapter != null)
					{
						adapter.getFilter().filter(s);
					}
				}
				mCurrentFilter = s;
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after)
			{
			}

			@Override
			public void afterTextChanged(Editable s)
			{
			}
		});

		QustomDialogBuilder builder = new QustomDialogBuilder(getActivity());
		builder.setTitle(getResources().getString(R.string.search_filter_title));
		builder.setTitleColor(org.caelus.kryptanandroid.Global.THEME_ACCENT_COLOR_STRING);
		builder.setDividerColor(org.caelus.kryptanandroid.Global.THEME_ACCENT_COLOR_STRING);

		if (numberOfLabels > 0)
			builder.setMessage(String.format(
					getResources().getString(
							R.string.search_filter_message_labels),
					numberOfLabels));
		else
			builder.setMessage(getResources().getString(
					R.string.search_filter_message_nolabels));

		builder.setCustomView(input);
		builder.setPositiveButton(getResources().getString(R.string.show), null);
		builder.setNegativeButton(getResources().getString(R.string.clear),
				new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int whichButton)
					{
						input.setText("");
					}
				});
		AlertDialog a = builder.create();
		a.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
		a.show();
		input.requestFocus();
	}
}
