package org.caelus.kryptanandroid;

import org.caelus.kryptanandroid.buildingblocks.ChangeMasterKeyAlert;
import org.caelus.kryptanandroid.core.CorePwdFile;
import org.caelus.kryptanandroid.core.CorePwd;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;

/**
 * An activity representing a single Secret detail screen. This activity is only
 * used on handset devices. On tablet-size devices, item details are presented
 * side-by-side with a list of items in a {@link SecretListActivity}.
 * <p>
 * This activity is mostly just a 'shell' activity containing nothing more than
 * a {@link SecretDetailFragment}.
 */
public class SecretDetailActivity extends FragmentActivity
{

	private CorePwdFile mPwdFile;
	private CorePwd mPwd;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_secret_detail);

		// Show the Up button in the action bar.
		getActionBar().setDisplayHomeAsUpEnabled(true);

		// savedInstanceState is non-null when there is fragment state
		// saved from previous configurations of this activity
		// (e.g. when rotating the screen from portrait to landscape).
		// In this case, the fragment will automatically be re-added
		// to its container so we don't need to manually add it.
		// For more information, see the Fragments API guide at:
		//
		// http://developer.android.com/guide/components/fragments.html
		//
		if (savedInstanceState == null)
		{
			// Create the detail fragment and add it to the activity
			// using a fragment transaction.
			Bundle arguments = new Bundle();
			
			mPwd = (CorePwd) getIntent()
					.getParcelableExtra(Global.EXTRA_CORE_PWD);
			
			arguments.putParcelable(Global.EXTRA_CORE_PWD, mPwd);

			mPwdFile = (CorePwdFile) getIntent().getParcelableExtra(
					Global.EXTRA_CORE_PWD_FILE_INSTANCE);

			arguments.putParcelable(Global.EXTRA_CORE_PWD_FILE_INSTANCE,
					mPwdFile);
			SecretDetailFragment fragment = new SecretDetailFragment();
			fragment.setArguments(arguments);
			getSupportFragmentManager().beginTransaction()
					.add(R.id.secret_detail_container, fragment).commit();
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
			Intent intent = new Intent(this, SecretListActivity.class);
			intent.putExtra(Global.EXTRA_CORE_SHOW_SEARCH, true);
			setResult(RESULT_OK, intent);
			finish();
			break;
		}
		case R.id.action_change_master:
		{
			ChangeMasterKeyAlert alert = new ChangeMasterKeyAlert(this, mPwdFile);
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
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		switch (requestCode)
		{
		case Global.ACTIVITY_REQUEST_CODE_SETTINGS:
			onSettingsDone(resultCode, data);
			break;
		}
	}

	private void onSettingsDone(int resultCode, Intent data)
	{
		// we do nothing.
	}
}
