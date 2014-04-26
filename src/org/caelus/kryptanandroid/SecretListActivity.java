package org.caelus.kryptanandroid;

import org.caelus.kryptanandroid.GeneratePasswordDialog.PasswordCreatedListener;
import org.caelus.kryptanandroid.buildingblocks.ChangeMasterKeyAlert;
import org.caelus.kryptanandroid.core.CorePwd;
import org.caelus.kryptanandroid.core.CorePwdFile;
import org.caelus.kryptanandroid.core.CoreSecureStringHandlerCollection;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;

/**
 * An activity representing a list of Secrets. This activity has different
 * presentations for handset and tablet-size devices. On handsets, the activity
 * presents a list of items, which when touched, lead to a
 * {@link SecretDetailActivity} representing item details. On tablets, the
 * activity presents the list of items and item details side-by-side using two
 * vertical panes.
 * <p>
 * The activity makes heavy use of fragments. The list of items is a
 * {@link SecretListFragment} and the item details (if present) is a
 * {@link SecretDetailFragment}.
 * <p>
 * This activity also implements the required
 * {@link SecretListFragment.Callbacks} interface to listen for item selections.
 */
public class SecretListActivity extends FragmentActivity implements
		SecretListFragment.Callbacks, PasswordCreatedListener
{

	/**
	 * Whether or not the activity is in two-pane mode, i.e. running on a tablet
	 * device.
	 */
	private boolean mTwoPane;
	private CorePwdFile mCorePwdFile;
	private CoreSecureStringHandlerCollection mLabelsFilter;
	private SecretListFragment mListFragment;
	private SecretAdapter mAdapter;
	private SecretDetailFragment mCurrentDetailsFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_secret_list);
		// Show the Up button in the action bar.
		getActionBar().setDisplayHomeAsUpEnabled(true);

		// get list mCurrentDetailsFragment
		mListFragment = (SecretListFragment) getSupportFragmentManager()
				.findFragmentById(R.id.secret_list);

		//

		if (findViewById(R.id.secret_detail_container) != null)
		{
			// The detail container view will be present only in the
			// large-screen layouts (res/values-large and
			// res/values-sw600dp). If this view is present, then the
			// activity should be in two-pane mode.
			mTwoPane = true;

			// In two-pane mode, list items should be given the
			// 'activated' state when touched.
			mListFragment.setActivateOnItemClick(true);
		}

		Intent intent = getIntent();
		if (intent != null)
		{
			Bundle extras = intent.getExtras();
			if (extras != null)
			{
				if (extras.containsKey(Global.EXTRA_CORE_PWD_FILE_INSTANCE))
				{
					mCorePwdFile = (CorePwdFile) extras
							.getParcelable(Global.EXTRA_CORE_PWD_FILE_INSTANCE);
				}
				if (extras.containsKey(Global.EXTRA_CORE_FILTER_COLLECTION))
				{
					mLabelsFilter = (CoreSecureStringHandlerCollection) extras
							.getParcelable(Global.EXTRA_CORE_FILTER_COLLECTION);
				}

				if (mCorePwdFile == null || mLabelsFilter == null)
				{
					throw new IllegalArgumentException(
							"instances of password file and filter collection must be provided in the intent.");
				} 

				mAdapter = new SecretAdapter(this, mCorePwdFile,
						mLabelsFilter);
				mListFragment.setListAdapter(mAdapter);
				mListFragment.setCurrentFilterString(mLabelsFilter
						.getCombinedCommaSeparatedString(
								getString(R.string.current_filter_prefix),
								getString(R.string.current_filter_none)));
				
				if (extras.containsKey(Global.EXTRA_CORE_SHOW_SEARCH))
				{
					mListFragment.showSearch(mLabelsFilter.size());
				}
				if (extras
						.containsKey(Global.EXTRA_CORE_PASSWORD_IS_NEWLY_CREATED)
						&& extras.containsKey(Global.EXTRA_CORE_PWD))
				{
					showNewlyCreatedPwd((CorePwd) extras
							.getParcelable(Global.EXTRA_CORE_PWD));
				}
			}
		}

		if (mCorePwdFile == null || mLabelsFilter == null)
		{
			throw new IllegalArgumentException(
					"instances of password file and filter collection must be provided in the intent.");
		} 
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.global_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			finish();
			return true;
		case R.id.action_search:
		{
			if (mListFragment != null)
			{
				mListFragment.showSearch(mLabelsFilter.size());
			}
			break;
		}
		case R.id.action_change_master:
		{
			ChangeMasterKeyAlert alert = new ChangeMasterKeyAlert(this,
					mCorePwdFile);
			alert.setToastMessage(R.string.masterkey_change_toast);
			alert.show();
			break;
		}
		case R.id.action_sync:
		{
			Intent intent = new Intent(this, SyncronizeDesktopActivity.class);
			startActivity(intent);
			break;
		}
		case R.id.action_new_password:
		{
			GeneratePasswordDialog dialog = new GeneratePasswordDialog(this,
					mCorePwdFile, this);
			dialog.show();
			break;
		}
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * Callback method from {@link SecretListFragment.Callbacks} indicating that
	 * the item with the given ID was selected.
	 */
	@Override
	public void onItemSelected(CorePwd pwd)
	{
		if (mTwoPane)
		{
			// In two-pane mode, show the detail view in this activity by
			// adding or replacing the detail mCurrentDetailsFragment using a
			// mCurrentDetailsFragment transaction.
			Bundle arguments = new Bundle();
			arguments.putParcelable(Global.EXTRA_CORE_PWD, pwd);
			arguments.putParcelable(Global.EXTRA_CORE_PWD_FILE_INSTANCE,
					mCorePwdFile);
			SecretDetailFragment fragment = new SecretDetailFragment();
			fragment.setArguments(arguments);
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.secret_detail_container, fragment).commit();

		} else
		{
			// In single-pane mode, simply start the detail activity
			// for the selected item ID.
			Intent detailIntent = new Intent(this, SecretDetailActivity.class);
			detailIntent.putExtra(Global.EXTRA_CORE_PWD, pwd);
			detailIntent.putExtra(Global.EXTRA_CORE_PWD_FILE_INSTANCE,
					mCorePwdFile);
			startActivityForResult(detailIntent,
					Global.ACTIVITY_REQUEST_CODE_SECRET_DETAIL);
		}
	}

	@Override
	protected void onActivityResult(int requestcode, int resultcode, Intent data)
	{
		switch (requestcode)
		{
		case Global.ACTIVITY_REQUEST_CODE_SECRET_DETAIL:
			switch(resultcode){
			case Global.ACTIVITY_REQUEST_CODE_NEW_PASSWORD:
				showNewlyCreatedPwd((CorePwd) data.getParcelableExtra(Global.EXTRA_CORE_PWD));
			}
			onDetailDone(resultcode, data);
		}
	}

	private void onDetailDone(int resultcode, Intent data)
	{
		// we must update our list of data!
		this.refreshListContents();

		// if this happens, then the search button was pressed while showing the
		// detail activity
		if (data != null && data.hasExtra(Global.EXTRA_CORE_SHOW_SEARCH))
		{
			mListFragment.showSearch(mLabelsFilter.size());
		}
	}

	public void refreshListContents()
	{
		mAdapter.refreshData();
	}

	public void deselectCurrentlySelected()
	{
		this.mListFragment.getListView().clearChoices();
		refreshListContents();
		onItemSelected(null);
	}

	private void showNewlyCreatedPwd(CorePwd pwd)
	{
		mListFragment.setSelection(-1);

		if (mTwoPane)
		{
			if (pwd == null)
			{
				if (mCurrentDetailsFragment != null)
				{
					getSupportFragmentManager().beginTransaction()
							.detach(mCurrentDetailsFragment).commit();
					
					mCurrentDetailsFragment = null;
				}
			} else
			{
				// In two-pane mode, show the detail view in this activity by
				// adding or replacing the detail mCurrentDetailsFragment using
				// a
				// mCurrentDetailsFragment transaction.
				Bundle arguments = new Bundle();
				arguments.putParcelable(Global.EXTRA_CORE_PWD, pwd);
				arguments.putParcelable(Global.EXTRA_CORE_PWD_FILE_INSTANCE,
						mCorePwdFile);
				arguments.putBoolean(
						Global.EXTRA_CORE_PASSWORD_IS_NEWLY_CREATED, true);
				mCurrentDetailsFragment = new SecretDetailFragment();
				mCurrentDetailsFragment.setArguments(arguments);
				getSupportFragmentManager()
						.beginTransaction()
						.replace(R.id.secret_detail_container,
								mCurrentDetailsFragment).commit();
			}

		} else
		{
			// In single-pane mode, simply start the detail activity
			// for the selected item ID.
			Intent detailIntent = new Intent(this, SecretDetailActivity.class);
			detailIntent.putExtra(Global.EXTRA_CORE_PWD, pwd);
			detailIntent.putExtra(Global.EXTRA_CORE_PWD_FILE_INSTANCE,
					mCorePwdFile);
			detailIntent.putExtra(Global.EXTRA_CORE_PASSWORD_IS_NEWLY_CREATED,
					true);
			startActivityForResult(detailIntent,
					Global.ACTIVITY_REQUEST_CODE_SECRET_DETAIL);
		}
	}

	@Override
	public void onPasswordCreated(CorePwd pwd)
	{
		// A new password has been created so lets view it!
		showNewlyCreatedPwd(pwd);
	}
}
