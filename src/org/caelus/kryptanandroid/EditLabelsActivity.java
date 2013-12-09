package org.caelus.kryptanandroid;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class EditLabelsActivity extends Activity
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_labels);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.edit_labels, menu);
		return true;
	}
	
	//TODO: implement this
}
