package org.caelus.kryptanandroid;

import org.caelus.kryptanandroid.core.CorePwdFile;
import org.caelus.kryptanandroid.core.CoreSecureStringHandler;
import org.caelus.kryptanandroid.core.CoreSecureStringHandlerCollection;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

public class SelectLabelActivity extends Activity implements LabelAdapter.OnLabelSelectionChangedListener
{
	
	private CoreSecureStringHandlerCollection mSelectedLabels = new CoreSecureStringHandlerCollection();
	private CorePwdFile mCorePwdFile = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_label);
		
		//if needed, we ask the user to decrypt the password file first
		if( mCorePwdFile == null || !mCorePwdFile.IsOpen() )
		{
			Intent intent = getIntent();
			if(intent.hasExtra(Global.EXTRA_CORE_PWD_FILE_INSTANCE))
			{
				onActivityResult(0, RESULT_OK, intent);
			}
			else
			{
				intent = new Intent(this, OpenPasswordFileActivity.class);
				startActivityForResult(intent, 0);
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if(requestCode == 0)
		{
			if(resultCode == RESULT_OK)
			{
				//Decryption successfull!
				mCorePwdFile = (CorePwdFile) data.getExtras().getParcelable(Global.EXTRA_CORE_PWD_FILE_INSTANCE);

				GridView labelLayout = (GridView) findViewById(R.id.LabelLayout);
				LabelAdapter adapter = new LabelAdapter(this, mCorePwdFile.getPasswordList());
				adapter.setOnLabelSelectionChangedListener(this);
				
				if(labelLayout != null){
					labelLayout.setAdapter(adapter);
				}

				// set number of matching passwords
				setMatchingPasswordsText();
			}
			else
			{
				//This should never happen, but if we end up here and 
				//the decryption was aborted for some reason
				//We simply exit the app
				finish();
			}
		}
	}

	/**
	 * sets number of matching passwords
	 * 
	 * @param value
	 */
	private void setMatchingPasswordsText()
	{
		Button button = (Button) findViewById(R.id.browseButton);
		TextView text = (TextView) findViewById(R.id.filterText);
		
		int buttonFormatIdentifier = 0;
		int textFormatIdentifier = 0;

		if (mSelectedLabels.getContainer().size() == 0)
		{
			buttonFormatIdentifier = R.string.button_browse_all_passwords;
			textFormatIdentifier = R.string.text_filter_none;
		} else
		{
			buttonFormatIdentifier = R.string.button_browse_filtered_passwords_format;
			textFormatIdentifier = R.string.text_filter_format;
		}

		String buttonFormat = getResources().getString(buttonFormatIdentifier);
		String textFormat = getResources().getString(textFormatIdentifier);
		
		//TODO: Get real number of matches
		int nrOfMatches = mCorePwdFile.getPasswordList().filter(mSelectedLabels).size();
		
		//TODO: if number of matches is zero, disable the button and change the text.
		
		button.setText(String.format(buttonFormat, nrOfMatches));
		text.setText(String.format(textFormat, mSelectedLabels.getContainer().size()));
		
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
				Intent intent = new Intent(this, SecretListActivity.class);
				intent.putExtra("org.caelus.SecretListActivity.showSearch", true);
				startActivity(intent);
				break;
			}
			case R.id.action_settings:
			{
				Intent intent = new Intent(this, SettingsActivity.class);
				startActivity(intent);
				break;
			}
			case R.id.action_change_master:
			{
				Intent intent = new Intent(this, ChangeMasterKeyActivity.class);
				startActivity(intent);
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

	/**
	 * This is called whenever a checkbox representing a label is checked
	 * @param buttonView
	 * @param isChecked
	 */
	@Override
	public void OnLabelSelectionChanged(CoreSecureStringHandler label,
			boolean isSelected)
    {
		if(isSelected)
		{
			mSelectedLabels.getContainer().add(label);
		}
		else
		{
			mSelectedLabels.getContainer().remove(label);
		}
		setMatchingPasswordsText();
    }
	
	public void onButtonClick(View view)
	{
		Intent intent = new Intent(this, SecretListActivity.class);
		intent.putExtra(Global.EXTRA_CORE_PWD_FILE_INSTANCE, mCorePwdFile);
		intent.putExtra(Global.EXTRA_CORE_FILTER_COLLECTION, mSelectedLabels);
		startActivity(intent);
	}

}
