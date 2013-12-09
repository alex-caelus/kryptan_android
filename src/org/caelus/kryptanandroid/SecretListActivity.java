package org.caelus.kryptanandroid;

import org.caelus.kryptanandroid.core.CorePwd;
import org.caelus.kryptanandroid.core.CorePwdFile;
import org.caelus.kryptanandroid.core.CoreSecureStringHandlerCollection;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
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
        SecretListFragment.Callbacks
{

	/**
	 * Whether or not the activity is in two-pane mode, i.e. running on a tablet
	 * device.
	 */
	private boolean mTwoPane;
	private CorePwdFile mCorePwdFile;
	private CoreSecureStringHandlerCollection mLabelsFilter;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_secret_list);
		// Show the Up button in the action bar.
		getActionBar().setDisplayHomeAsUpEnabled(true);

		// get list fragment
		SecretListFragment listFragment = (SecretListFragment) getSupportFragmentManager()
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
			listFragment.setActivateOnItemClick(true);
		}

		Intent intent = getIntent();
		if(intent != null)
		{
			Bundle extras = intent.getExtras();
			if(extras != null)
			{
				if(extras.containsKey("org.caelus.SecretListActivity.showSearch"))
				{
					listFragment.showSearch();
				}
				if(extras.containsKey(Global.EXTRA_CORE_PWD_FILE_INSTANCE))
				{
					mCorePwdFile = (CorePwdFile) extras.getParcelable(Global.EXTRA_CORE_PWD_FILE_INSTANCE);
				}
				if(extras.containsKey(Global.EXTRA_CORE_FILTER_COLLECTION))
				{
					mLabelsFilter = (CoreSecureStringHandlerCollection) extras.getParcelable(Global.EXTRA_CORE_FILTER_COLLECTION);
				}
				if(mCorePwdFile == null || mLabelsFilter == null)
				{
					throw new IllegalArgumentException("instances of password file and filter collection must be provided in the intent.");
				}
				else
				{
					SecretAdapter adapter = new SecretAdapter(this, mCorePwdFile, mLabelsFilter);
					listFragment.setListAdapter(adapter);
				}
			}
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
				NavUtils.navigateUpFromSameTask(this);
				return true;
			case R.id.action_search:
			{
				SecretListFragment listFragment = (SecretListFragment) getSupportFragmentManager()
				        .findFragmentById(R.id.secret_list);
				if (listFragment != null)
				{
					listFragment.showSearch();
				}
				break;
			}
			case R.id.action_settings:
			{
				Intent intent = new Intent(this, SettingsActivity.class);
				startActivity(intent);
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
			// adding or replacing the detail fragment using a
			// fragment transaction.
			Bundle arguments = new Bundle();
			arguments.putParcelable(Global.EXTRA_CORE_PWD, pwd);
			arguments.putParcelable(Global.EXTRA_CORE_PWD_FILE_INSTANCE, mCorePwdFile);
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
			detailIntent.putExtra(Global.EXTRA_CORE_PWD_FILE_INSTANCE, mCorePwdFile);
			startActivity(detailIntent);
		}
	}
}
