package org.caelus.kryptanandroid;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

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
		public void onItemSelected(String id);
	}

	/**
	 * A dummy implementation of the {@link Callbacks} interface that does
	 * nothing. Used only when this fragment is not attached to an activity.
	 */
	private static Callbacks sDummyCallbacks = new Callbacks()
	{
		@Override
		public void onItemSelected(String id)
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

		setListAdapter(new SecretAdapter(getActivity()));
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

		// add searchbar
		getListView().setTextFilterEnabled(true);
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
		mCallbacks.onItemSelected((String) view.getTag());
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

	public void showSearch()
	{
		final EditText input = new EditText(getActivity());
		input.setText(mCurrentFilter);
		input.setSelectAllOnFocus(true);
		input.setHint(getResources().getString(R.string.search_filter_hint));
		
		input.addTextChangedListener(new TextWatcher()
		{
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count)
			{
				ListView listView = getListView();
				if(listView != null)
				{
					SecretAdapter adapter = (SecretAdapter) listView.getAdapter();
					if(adapter != null)
					{
						adapter.getFilter().filter(s);
					}
				}
				mCurrentFilter = s;
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after){ }
			
			@Override
			public void afterTextChanged(Editable s){}
		});

		AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
		alert.setTitle(getResources().getString(R.string.search_filter_title));
		alert.setMessage(getResources().getString(
		        R.string.search_filter_message));
		alert.setView(input);
		alert.setPositiveButton(getResources().getString(R.string.show), null);
		alert.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int whichButton)
			{
				input.setText("");
			}
		});
		alert.show();
	}
}
