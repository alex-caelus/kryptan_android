package org.caelus.kryptanandroid;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.SyncFailedException;
import java.text.ChoiceFormat;
import java.util.List;
import java.util.Vector;

import net.sourceforge.zbar.Symbol;

import org.caelus.kryptanandroid.buildingblocks.ConflictChoice;
import org.caelus.kryptanandroid.buildingblocks.TcpClient;
import org.caelus.kryptanandroid.core.CorePwd;
import org.caelus.kryptanandroid.core.CorePwdFile;
import org.caelus.kryptanandroid.core.CorePwdList;
import org.caelus.kryptanandroid.core.CoreSecureStringHandler;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.MergeCursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.dm.zbar.android.scanner.ZBarConstants;
import com.dm.zbar.android.scanner.ZBarScannerActivity;
import com.qustom.dialog.QustomDialogBuilder;

public class SyncronizeDesktopActivity extends Activity implements TcpClient.OnTcpClientEventReciever, OnCancelListener, OnClickListener
{
	private static final String LAST_SYNC_TIME = "LAST_SYNC_TIME";
	private static final String PREFS_NAME = "SYNCRONIZATION_VALUES";
	private List<TcpClient> mTcpClients = new Vector<TcpClient>();
	private TcpClient mTcpClientResponsive;
	private ProgressDialog dialog;
	private String mDebugReceieved;
	private String mEncryptionKey;
	private CorePwdFile mRemoteFile;
	private CorePwdFile mLocalPwdFile;
	private SyncAdapter mSyncAdapter; 
	
	private void SyncFailed(String message)
	{
		QustomDialogBuilder builder = new QustomDialogBuilder(this);
		builder.setMessage(message);
		builder.setTitle("Error");
		builder.setNeutralButton("Close", this);
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
		builder.setTitle("Warning");
		builder.setNeutralButton("Close", new OnClickListener()
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
	    if (resultCode == RESULT_OK) 
	    {
	    	this.dialog = new ProgressDialog(this);
	        this.dialog.setMessage("Trying to connect...");
	        this.dialog.setIndeterminate(true);
	        this.dialog.setOnCancelListener(this);
	        this.dialog.show();
	        
	    	tryConnectToServers(data.getStringExtra(ZBarConstants.SCAN_RESULT));
	    	
	        // The value of type indicates one of the symbols listed in Advanced Options below.
	    } else if(resultCode == RESULT_CANCELED) {
	        Toast.makeText(this, "Camera unavailable", Toast.LENGTH_SHORT).show();
	    }
	}
	
	public void scanQrCode(View v)
	{
		//tryConnectToServers("192.168.1.64:4321:temporarykey");
		//return;
	    Intent intent = new Intent(this, ZBarScannerActivity.class);
	    intent.putExtra(ZBarConstants.SCAN_MODES, new int[]{Symbol.QRCODE});
	    startActivityForResult(intent, Global.ACTIVITY_REQUEST_CODE_QR_SCAN);
	}
	
	private void tryConnectToServers(String urls)
	{
		try{
			String[] parts = urls.split(":");
			if(parts.length != 3)
			{
				SyncFailed(getResources().getString(R.string.sync_error_malformed_qr_content));
				return;
			}
			//this key is only valid for a very brief period of time, 
			//so we do not bother to protect it by using SecureStringHandler
			//it's already to late for that anyways
			mEncryptionKey = parts[2];
			int port = Integer.parseInt(parts[1]);
			String[] ips = parts[0].split(",");
			
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

			this.dialog.setMessage("Sending our response...");
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
			//do nothing, we already have whats needed
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
			//simply import from remote
			list.ImportPwd(remote);
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
		//save changes
		mLocalPwdFile.SaveWithDialog(this);
		
		this.dialog.dismiss();
		QustomDialogBuilder builder = new QustomDialogBuilder(this);
		builder.setMessage("Passwords sucessfully syncronized!");
		builder.setTitle("Success");
		builder.setNeutralButton("Close", new OnClickListener()
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
	}

	@Override
	public void onCancel(DialogInterface arg0)
	{
		this.dialog.dismiss();
		
		SyncFailed("Cancelled by the user!");
	}
	
	private void listDifferences(CorePwdFile local, CorePwdFile remote)
	{
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		long lastSyncTime = settings.getLong(LAST_SYNC_TIME, 0);
		
		ListView list = (ListView) findViewById(R.id.listSyncItems);
		mSyncAdapter = new SyncAdapter(this, lastSyncTime, local, remote);
		list.setAdapter(mSyncAdapter);
	}
}
