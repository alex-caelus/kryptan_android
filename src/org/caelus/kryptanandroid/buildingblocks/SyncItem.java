package org.caelus.kryptanandroid.buildingblocks;


import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SyncItem extends LinearLayout
{
	ConflictChoice mChoice;
	
	LinearLayout mLocalGroup;
	LinearLayout mRemoteGroup;

	SecureTextView mLocalDescription;
	TextView mLocalTooltip;
	SecureTextView mRemoteDescription;
	TextView mRemoteTooltip;

	Button mChooseLocalButton;
	Button mChooseRemoteButton;
	
	public SyncItem(Context context)
	{
		super(context);
		this.setOrientation(LinearLayout.HORIZONTAL);
		addViews();
	}

	public SyncItem(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		this.setOrientation(LinearLayout.HORIZONTAL);
		addViews();
	}

	public SyncItem(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		this.setOrientation(LinearLayout.HORIZONTAL);
		addViews();
	}
	
	public void updateViews(ConflictChoice choice)
	{
		mChoice = choice;
		
		if(mChoice == null)
			throw new IllegalArgumentException("Choice cannot be null");
		
		updateViews();
	}
	
	public ConflictChoice GetChoice()
	{
		return mChoice;
	}

	private void addViews()
	{
		//LOCAL TEXTS
		if(mLocalDescription == null)
		{
			mLocalDescription = new SecureTextView(getContext());
			mLocalDescription.setTextAppearance(getContext(), android.R.style.TextAppearance_Medium);
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			mLocalDescription.setGravity(Gravity.LEFT);
			mLocalDescription.setLayoutParams(lp);
		}
		if(mLocalTooltip == null)
		{
			mLocalTooltip = new TextView(getContext());
			mLocalTooltip.setTextAppearance(getContext(), android.R.style.TextAppearance_Small);
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			lp.gravity = Gravity.LEFT;
			mLocalTooltip.setLayoutParams(lp);
		}
		
		//REMOTE TEXTS
		if(mRemoteDescription == null)
		{
			mRemoteDescription = new SecureTextView(getContext());
			mRemoteDescription.setTextAppearance(getContext(), android.R.style.TextAppearance_Medium);
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			mRemoteDescription.setGravity(Gravity.RIGHT);
			mRemoteDescription.setLayoutParams(lp);
		}
		if(mRemoteTooltip == null)
		{
			mRemoteTooltip = new TextView(getContext());
			mRemoteTooltip.setTextAppearance(getContext(), android.R.style.TextAppearance_Small);
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			lp.gravity = Gravity.RIGHT;
			mRemoteTooltip.setLayoutParams(lp);
		}
		
		//BUTTONS
		if(mChooseLocalButton == null)
		{
			mChooseLocalButton = new Button(getContext());
			mChooseLocalButton.setText("Choose");
			mChooseLocalButton.setOnClickListener(new OnClickListener()
			{	
				@Override
				public void onClick(View v)
				{
					ChooseLocalOverrideClicked();
				}
			});
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			lp.gravity = Gravity.RIGHT;
			mChooseLocalButton.setLayoutParams(lp);
		}
		if(mChooseRemoteButton == null)
		{
			mChooseRemoteButton = new Button(getContext());
			mChooseRemoteButton.setText("Choose");
			mChooseRemoteButton.setOnClickListener(new OnClickListener()
			{	
				@Override
				public void onClick(View v)
				{
					ChooseRemoteOverrideClicked();
				}
			});
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			lp.gravity = Gravity.LEFT;
			mChooseRemoteButton.setLayoutParams(lp);
		}
		
		//LOCAL GROUP
		if(mLocalGroup == null)
		{
			mLocalGroup = new LinearLayout(getContext());
			mLocalGroup.setOrientation(LinearLayout.VERTICAL);
			mLocalGroup.setPadding(0, 0, 10, 0);
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			lp.weight = 1;
			mLocalGroup.setLayoutParams(lp);
		}
		mLocalGroup.removeAllViews();
		mLocalGroup.addView(mLocalDescription);
		mLocalGroup.addView(mLocalTooltip);
		mLocalGroup.addView(mChooseLocalButton);
		
		//REMOTE GROUP
		if(mRemoteGroup == null)
		{
			mRemoteGroup = new LinearLayout(getContext());
			mRemoteGroup.setOrientation(LinearLayout.VERTICAL);
			mRemoteGroup.setPadding(10, 0, 0, 0);
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			lp.weight = 1;
			mRemoteGroup.setLayoutParams(lp);
		}
		mRemoteGroup.removeAllViews();
		mRemoteGroup.addView(mRemoteDescription);
		mRemoteGroup.addView(mRemoteTooltip);
		mRemoteGroup.addView(mChooseRemoteButton);
		
		//ROOT LAYOUT
		this.removeAllViews();
		this.addView(mLocalGroup);
		this.addView(mRemoteGroup);
	}

	private void updateViews()
	{
		mLocalDescription.setSecureText(mChoice.getLocalPwdDescription());
		mRemoteDescription.setSecureText(mChoice.getRemotePwdDescription());
		mLocalTooltip.setText(mChoice.getLocalTooltip());
		mRemoteTooltip.setText(mChoice.getRemoteTooltip());
		mChooseLocalButton.setEnabled(!mChoice.shouldLocalPropagate());
		mChooseRemoteButton.setEnabled(!mChoice.shouldRemotePropagate());
		
		//color
		int localColor = mChoice.shouldLocalPropagate() ? Color.GREEN : Color.WHITE;
		int remoteColor = mChoice.shouldRemotePropagate() ? Color.GREEN : Color.WHITE;

		mLocalDescription.setTextColor(localColor);
		mLocalDescription.invalidatePaint();
		mChooseLocalButton.setTextColor(localColor);
		mRemoteDescription.setTextColor(remoteColor);
		mRemoteDescription.invalidatePaint();
		mChooseRemoteButton.setTextColor(remoteColor);
	}

	protected void ChooseRemoteOverrideClicked()
	{
		mChoice.setLocalPropagation(false);
		mChoice.setRemotePropagation(true);
		updateViews();
	}

	protected void ChooseLocalOverrideClicked()
	{
		mChoice.setLocalPropagation(true);
		mChoice.setRemotePropagation(false);
		updateViews();
	}
	

}
