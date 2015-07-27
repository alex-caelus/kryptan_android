package org.caelus.kryptanandroid;

import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import com.google.zxing.integration.android.*;

import org.caelus.kryptanandroid.buildingblocks.ConflictChoice;
import org.caelus.kryptanandroid.buildingblocks.TcpClient;
import org.caelus.kryptanandroid.core.CorePwd;
import org.caelus.kryptanandroid.core.CorePwdFile;
import org.caelus.kryptanandroid.core.CorePwdList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.qustom.dialog.QustomDialogBuilder;

public class SyncronizeDesktopActivity extends Activity implements TcpClient.OnTcpClientEventReciever, OnCancelListener, OnClickListener
{
	private static final String LAST_SYNC_TIME = "LAST_SYNC_TIME";
	private static final String PREFS_NAME = "SYNCRONIZATION_VALUES";
	private List<TcpClient> mTcpClients = new Vector<TcpClient>();
	private TcpClient mTcpClientResponsive;
	private ProgressDialog dialog;
	private String mEncryptionKey;
	private CorePwdFile mRemoteFile;
	private CorePwdFile mLocalPwdFile;
	private SyncAdapter mSyncAdapter; 
	
	private void SyncFailed(String message)
	{
		QustomDialogBuilder builder = new QustomDialogBuilder(this);
		builder.setMessage(getString(R.string.sync_desktop_error_message)  + message);
		builder.setTitle(getString(R.string.sync_desktop_error_title));
		builder.setNeutralButton(getString(R.string.cancel), this);
		builder.setCancelable(false);
		builder.setTitleColor(Global.THEME_ACCENT_COLOR_STRING);
		builder.setDividerColor(Global.THEME_ACCENT_COLOR_STRING);
		AlertDialog dialog = builder.create();
		dialog.show();
		
		//do some cleanup
		for (TcpClient client : mTcpClients)
		{
			client.stopClient();
		}
		mTcpClients.clear();
		if(mTcpClientResponsive != null)
		{
			mTcpClientResponsive.stopClient();
			mTcpClientResponsive = null;
		}
	}
	
	private void SyncWarning(String message)
	{
		QustomDialogBuilder builder = new QustomDialogBuilder(this);
		builder.setMessage(message);
		builder.setTitle(getString(R.string.sync_desktop_warning_title));
		builder.setNeutralButton(getString(R.string.cancel), new OnClickListener()
		{	
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				dialog.dismiss();
			}
		});
		builder.setCancelable(true);
		builder.setTitleColor(Global.THEME_ACCENT_COLOR_STRING);
		builder.setDividerColor(Global.THEME_ACCENT_COLOR_STRING);
		AlertDialog dialog = builder.create();
		dialog.show();
	}
	
	private void SyncFinished()
	{
		long now = System.currentTimeMillis() / 1000l;
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		settings.edit().putLong(LAST_SYNC_TIME, now).commit();
		finish();
	}

	@Override
	public void onClick(DialogInterface arg0, int arg1)
	{
		//the error dialog has been closed/dismissed
		finish();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_syncronize_desktop);

	    getActionBar().setDisplayHomeAsUpEnabled(true);

		Intent intent = getIntent();
	    if (intent != null)
		{
			Bundle extras = intent.getExtras();
			if (extras != null)
			{
				if (extras.containsKey(Global.EXTRA_CORE_PWD_FILE_INSTANCE))
				{
					mLocalPwdFile = (CorePwdFile) extras
							.getParcelable(Global.EXTRA_CORE_PWD_FILE_INSTANCE);
				}
			}
		}
	    
	    if(mLocalPwdFile == null)
	    {
			throw new IllegalArgumentException(
					"instances of password file must be provided in the intent.");
	    }
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.syncronize_desktop, menu);
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
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
		if (scanResult != null) {
	    	this.dialog = new ProgressDialog(this);
	        this.dialog.setMessage(getString(R.string.tcp_client_progress_init));
	        this.dialog.setIndeterminate(true);
	        this.dialog.setOnCancelListener(this);
	        this.dialog.show();
	        
	    	tryConnectToServers(scanResult.getContents());
	    	
	        // The value of type indicates one of the symbols listed in Advanced Options below.
	    } else {
	        Toast.makeText(this, "Camera unavailable", Toast.LENGTH_SHORT).show();
	    }
	}
	
	public void scanQrCode(View v)
	{
		//tryConnectToServers("192.168.1.64:4321:temporarykey");
		//return;
		IntentIntegrator integrator = new IntentIntegrator(this);
		integrator.initiateScan();
	}
	
	private void tryConnectToServers(String urls)
	{
		try{
			String[] parts = urls.split(",");
			if (parts.length < 3) {
				SyncFailed(getResources().getString(R.string.sync_error_malformed_qr_content));
				return;
			}
			//this key is only valid for a very brief period of time, 
			//so we do not bother to protect it by using SecureStringHandler
			//it's already to late for that anyways
			
			//encryption key is last in message
			mEncryptionKey = parts[parts.length-1];
			
			//port number is second to last in message
			int port = Integer.parseInt(parts[parts.length-2]);
			
			//ips are the other parts of the message
			String[] ips = Arrays.copyOfRange(parts, 0, parts.length-2);
			
			for (String ip : ips)
			{
				TcpClient client = new TcpClient(this, ip, port, mEncryptionKey, this);
				client.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
				mTcpClients.add(client);
			}
		}
		catch(Exception e)
		{
			SyncFailed(e.getMessage());
		}
	}
	
	public void sendResponseButtonClicked(View v)
	{
		try
		{
			int nPwds = mSyncAdapter.getCount();
			
			for(int i=0; i < nPwds; i++)
			{
				applyChoice(mSyncAdapter.getItem(i));
			}

			this.dialog.setMessage(getString(R.string.tcp_client_progress_sending));
			this.dialog.show();
			mTcpClientResponsive.sendMergedPwdFile(mLocalPwdFile);
		} catch (Exception e)
		{
			// TODO Auto-generated catch block
			SyncFailed(e.getMessage());
		}
	}

	private void applyChoice(ConflictChoice choice)
	{
		CorePwdList list = mLocalPwdFile.getPasswordList();
		if(choice.shouldLocalPropagate())
		{
			//do nothing, we already have what's needed
		}
		else if(choice.shouldRemotePropagate())
		{
			CorePwd local = choice.getLocalPwd();
			CorePwd remote = choice.getRemotePwd();
			if(local != null)
			{
				//we first remove the local copy
				list.deletePwd(local);
			}
			if(remote != null)
			{
				//simply import from remote
				list.ImportPwd(remote);
			}
		}
		else
		{
			//should never happen
			SyncWarning(getString(R.string.sync_desktop_warning_unresolved_items));
		}
	}

	//this is run on TcpClient thread (in the background)
	@Override
	public void tcpClientPwdFileReceived(TcpClient client, CorePwdFile newFile)
	{
		try
		{
			mTcpClientResponsive = client;
			mTcpClients.remove(client);
			
			//stop and delete all other clients
			for (TcpClient element : mTcpClients)
			{
				element.stopClient();
			}
			mTcpClients.clear();
			
			//show results view
	    	ViewFlipper vf = (ViewFlipper) findViewById(R.id.viewFlipper);
	    	vf.showNext();
	    	
	    	mRemoteFile = newFile;
	    	
	    	listDifferences(mLocalPwdFile, mRemoteFile);
			
			//hide progress window
			this.dialog.hide();
		} catch (Exception e)
		{
			// TODO Auto-generated catch block
			SyncFailed(e.getMessage());
		}
	}

	@Override
	public void tcpClientFailed(TcpClient client, String message)
	{
		this.dialog.dismiss();
		if(client != null)
		{
			client.stopClient();
			if(mTcpClients.size() > 0)
			{
				mTcpClients.remove(client);
			}
			
			//if there are no more chances of success, we should notify the user
			if(mTcpClients.size() == 0)
			{
				mTcpClients.remove(client);
				
				SyncFailed(message);
			}
		}
	}

	@Override
	public void tcpClientProgressChanged(TcpClient client, String status)
	{
		// TODO Auto-generated method stub
        this.dialog.setMessage(status);
	}

	@Override
	public void tcpClientFinished(TcpClient client)
	{	
		this.dialog.dismiss();
		QustomDialogBuilder builder = new QustomDialogBuilder(this);
		builder.setMessage(getString(R.string.sync_desktop_success_message));
		builder.setTitle(getString(R.string.sync_desktop_success_title));
		builder.setNeutralButton(getString(R.string.cancel), new OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				dialog.dismiss();
				SyncFinished();
			}
		});
		builder.setCancelable(false);
		builder.setTitleColor(Global.THEME_ACCENT_COLOR_STRING);
		builder.setDividerColor(Global.THEME_ACCENT_COLOR_STRING);
		AlertDialog dialog = builder.create();
		dialog.show();

		//save changes
		mLocalPwdFile.SaveWithDialog(this);
	}

	@Override
	public void onCancel(DialogInterface arg0)
	{
		this.dialog.dismiss();
		
		SyncFailed(getString(R.string.sync_desktop_cancelled));
	}
	
	private void listDifferences(CorePwdFile local, CorePwdFile remote)
	{
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		long lastSyncTime = settings.getLong(LAST_SYNC_TIME, 0);
		
		ListView list = (ListView) findViewById(R.id.listSyncItems);
		mSyncAdapter = new SyncAdapter(this, lastSyncTime, local, remote);
		list.setAdapter(mSyncAdapter);
	}
	
	//fix window leaks
	@Override
	protected void onPause()
	{
		// TODO Auto-generated method stub
		super.onPause();
	}
	
	@Override
	protected void onResume()
	{
		// TODO Auto-generated method stub
		super.onResume();
	}
}
