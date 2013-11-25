package org.caelus.kryptanandroid;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.GridView;
import android.widget.TextView;

public class SelectLabelActivity extends Activity implements OnCheckedChangeListener
{
	
	private ArrayList<CharSequence> mSelectedLabels = new ArrayList<CharSequence>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_label);

		GridView labelLayout = (GridView) findViewById(R.id.LabelLayout);
		LabelAdapter adapter = new LabelAdapter(this);
		adapter.setOnCheckedChangeListener(this);
		
		if(labelLayout != null){
			labelLayout.setAdapter(adapter);
		}

		// set number of matching passwords
		setMatchingPasswordsText();
		
		//if needed we ask the user to decrypt the password file first
		if(!isDecrypted() )
		{
			Intent intent = new Intent(this, DecryptActivity.class);
			startActivityForResult(intent, 0);
		}
	}
	
	private boolean isDecrypted()
    {
	    // TODO Add real check for decrypted password file or-not
	    return false;
    }

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if(requestCode == 0)
		{
			if(resultCode == RESULT_OK)
			{
				//Decryption successfull!
				
			}
			else
			{
				//This should never happen, but if we end up here and 
				//the decryption was aborted for some reason
				//We simply exit the app
				//finish();
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

		if (mSelectedLabels.size() == 0)
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
		int nrOfMatches = 143;
		
		button.setText(String.format(buttonFormat, nrOfMatches));
		text.setText(String.format(textFormat, mSelectedLabels.size()));
		
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
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
    {
		if(isChecked)
		{
			mSelectedLabels.add(buttonView.getText());
		}
		else
		{
			mSelectedLabels.remove(buttonView.getText());
		}
		setMatchingPasswordsText();
    }
	
	public void onButtonClick(View view)
	{
		Intent intent = new Intent(this, SecretListActivity.class);
		CharSequence[] filter =  mSelectedLabels.toArray(new CharSequence[0]);
		intent.putExtra("org.caelus.SecretListActivity.filter", filter);
		startActivity(intent);
	}

}
