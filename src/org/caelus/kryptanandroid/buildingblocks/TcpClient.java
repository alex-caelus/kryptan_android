package org.caelus.kryptanandroid.buildingblocks;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

import org.caelus.kryptanandroid.core.CorePwdFile;
import org.caelus.kryptanandroid.core.CoreSecureStringHandler;

import android.content.Context;
import android.os.AsyncTask;

public class TcpClient extends AsyncTask<Void, Void, Void>{

	// your computer IP address
    public String mServerIp; 
    // your server port
    public int mServerPort;
    // sends message received notifications
    private OnTcpClientEventReciever mMessageListener = null;
    // while this is true, the server will continue running
    private boolean mRun = false;
    // used to send messages
    private PrintWriter mBufferOut;
    // used to read messages from the server
    private BufferedReader mBufferIn;
    // used for waiting for other thread
    private final Object mLock = new Object();
    //the message to send
    private CorePwdFile mFileToSend = null;
    // the recieved content
    private String recievedContent;
    // used for error
    private String mError;
    //current state
    private STATES mState = STATES.INIT;
	private Context mContext;
	private CorePwdFile mNewFile;
	private String mEncryptionKey;
    
    private enum STATES{
    	INIT,
    	READING,
    	DECRYPTING,
    	WAITING,
    	SENDING,
    	FINISHED
    };

    /**
     * Constructor of the class. OnMessagedReceived listens for the messages
     * received from server
     */
    public TcpClient(Context context, String ip, int port, String encryptionkey, OnTcpClientEventReciever listener) {
        mMessageListener = listener;
        mServerIp = ip;
        mServerPort = port;
        mContext = context;
        mEncryptionKey = encryptionkey;
    }

    /**
     * Sends the message entered by client to the server
     * 
     * @param message
     *            text entered by client
     */
    public void sendMergedPwdFile(CorePwdFile toSend) {
    	synchronized (mLock)
		{
    		mFileToSend = toSend;
        	mLock.notifyAll();
		}
    }
    
    private void _sendMessage() {
        if (mBufferOut != null && !mBufferOut.checkError()) {
        	String messageToSend = mFileToSend.SaveToString();
            mBufferOut.print(messageToSend + '#');
            mBufferOut.flush();
        }
    }

    /**
     * Close the connection and release the members
     */
    public void stopClient() {
        mRun = false;
        synchronized (mLock)
		{
            mLock.notifyAll();
		}

        if (mBufferOut != null) {
            mBufferOut.flush();
            mBufferOut.close();
        }
        
        this.cancel(true);

        mMessageListener = null;
        mBufferIn = null;
        mBufferOut = null;
    }
    
    @Override 
    protected void onPreExecute() {};
    
    @Override 
    protected void onPostExecute(Void result) 
    {
    	if(mMessageListener != null)
    	{
	    	if(mError != null && mError.length() > 0)
	    	{
	    		mMessageListener.tcpClientFailed(this, mError);
	    	}
	    	else
			{
	    		mMessageListener.tcpClientFinished(this);
			}
    	}
	};
    
    @Override
    protected void onProgressUpdate(Void... values) 
    {
    	if(mMessageListener != null)
    	{
    		switch (mState)
			{
			case INIT:
				mMessageListener.tcpClientProgressChanged(this, "TCP connection initialized...");
				break;
				
			case READING:
				mMessageListener.tcpClientProgressChanged(this, "TCP connection connected and reading data...");
				break;
				
			case DECRYPTING:
				mMessageListener.tcpClientProgressChanged(this, "TCP connection decrypting received data...");
				break;
				
			case WAITING:
				//tell ui thread that data is received
				mMessageListener.tcpClientPwdFileReceived(this, mNewFile);
				break;
				
			case SENDING:
				mMessageListener.tcpClientProgressChanged(this, "TCP connection is sending data...");
				break;
			
			case FINISHED:
				mMessageListener.tcpClientProgressChanged(this, "TCP connection is finished!");
				break;

			default:
				break;
			}
    	}
    };

	@Override
	protected Void doInBackground(Void... params)
	{

        mRun = true;

        try {
            // here you must put your computer's IP address.
            InetAddress serverAddr = InetAddress.getByName(mServerIp);

            //MyLog.e("TCP Client", "C: Connecting...");

            // create a socket to make the connection with the server
            Socket socket = new Socket(serverAddr, mServerPort);

            try {

                // sends the message to the server
                mBufferOut = new PrintWriter(new BufferedWriter(
                        new OutputStreamWriter(socket.getOutputStream())), true);

                // receives the message which the server sends back
                mBufferIn = new BufferedReader(new InputStreamReader(
                        socket.getInputStream()));
                //sendMessage(Constants.LOGIN_NAME + "id: " + uid);

                //make room in buffer (and '#' stopsign)
                StringBuilder serverMessage = new StringBuilder();

                mState = STATES.READING;
                publishProgress();
                
                // in this while the client listens for the messages sent by the
                // server
                while (mRun) {
                	//socket.setSoTimeout(100);
                    int read = mBufferIn.read();
                    if(read == -1)
                    {
                    	mError = "Failed read";
                    	return null;
                    }
                    char ch = (char) read;
                    if(ch == '#')
                    {
                    	break;
                    }
                    serverMessage.append(ch);
                }
                
                //Handle message and send response
                if(serverMessage.length() > 0)
                {
                	recievedContent = serverMessage.toString();

                    mState = STATES.DECRYPTING;
                    publishProgress();

                	HandleRead(recievedContent);
                	if(mError != null && mError.length() > 0)
                	{
                		return null;
                	}

                    mState = STATES.WAITING;
                    publishProgress();
                	
                	synchronized (mLock)
					{
                		if(mFileToSend == null)
                		{
	                		//wait for a response to send to server
							mLock.wait();
                		}
					}
                	if(mRun)
                	{
                        mState = STATES.SENDING;
                        publishProgress();
                		_sendMessage();
                	}
                }
                

                //MyLog.e("RESPONSE FROM SERVER", "S: Received Message: '"
                //        + mServerMessage + "'");

            } finally {
                // the socket must be closed. It is not possible to reconnect to
                // this socket
                // after it is closed, which means a new socket instance has to
                // be created.
                socket.close();
            }

        } catch (Exception e) {

        	mError = "Failed: " + e.getMessage();

        }

        mState = STATES.FINISHED;
        publishProgress();

		// TODO Auto-generated method stub
		return null;
	}
    
    private void HandleRead(String data)
    {
    	try{
        	File outputFile = File.createTempFile("prefix", "extension", mContext.getCacheDir());
    		FileWriter writer = new FileWriter(outputFile);
    		writer.write(data);
    		writer.close();
    		
    		mNewFile = new CorePwdFile(outputFile.getCanonicalPath());
			CoreSecureStringHandler masterkey = mNewFile
					.getMasterKeyHandler();
			masterkey.Clear();
			int len = mEncryptionKey.length();
			for (int i = 0; i < len; i++)
			{
				masterkey.AddChar(mEncryptionKey.charAt(i));
			}

			mNewFile.TryOpenAndParse();

			if (!mNewFile.IsOpen())
			{
				mError = "Failed to decrypt data";
			} 		
    	}
    	catch(Exception e)
    	{
        	mError = "Failed read";
    	}
    }

    // Declare the interface. The method messageReceived(String message) will
    // must be implemented in the MyActivity
    // class at on asynckTask doInBackground
    public interface OnTcpClientEventReciever {
        public void tcpClientPwdFileReceived(TcpClient client, CorePwdFile newFile);
        public void tcpClientFailed(TcpClient client, String message);
        public void tcpClientProgressChanged(TcpClient client, String status);
        public void tcpClientFinished(TcpClient client);
    }
}