package org.caelus.kryptanandroid.buildingblocks;

import java.util.Arrays;

import org.caelus.kryptanandroid.buildingblocks.KryptanKeyboard.KeyboardClosedListener;
import org.caelus.kryptanandroid.buildingblocks.KryptanKeyboard.KeyboardTextChangedListener;
import org.caelus.kryptanandroid.core.CoreSecureStringHandler;

import android.content.Context;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.EditText;

public class SecureEditText extends EditText implements KeyboardClosedListener,
		KeyboardTextChangedListener, OnTouchListener
{

	private KryptanKeyboard mKeyboard;
	private CoreSecureStringHandler mCurrentText;
	private String mKeyboardTitle = null;

	public SecureEditText(Context context)
	{
		super(context);
		init();
	}

	public SecureEditText(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		init();
	}

	public SecureEditText(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		init();
	}

	private void init()
	{
		setCursorVisible(false);
		setOnTouchListener(this);
	}

	public CoreSecureStringHandler getSecureText()
	{
		return mCurrentText;
	}
	
	public void setKeyboardDialogTitle(String title)
	{
		mKeyboardTitle = title;
	}

	@Override
	public void KeyboardTextChanged(CoreSecureStringHandler text)
	{
		mCurrentText = text;
		char[] dummyArray = new char[text.GetLength()];
		Arrays.fill(dummyArray, '*');
		setText(new String(dummyArray));
	}

	@Override
	public void KeyboardClosed()
	{
		//TODO: decide if the decrypt button should be pressed when closing the keyboard
	}

	@Override
	public boolean onTouch(View arg0, MotionEvent arg1)
	{
		if (mKeyboard == null)
		{
			mKeyboard = new KryptanKeyboard(getContext(), mKeyboardTitle);
			mKeyboard.setClosedListener(this);
			mKeyboard.setTextChangedListener(this);
			mKeyboard.setHintText(getHint());
			mKeyboard
					.setInputTypePassword((this.getInputType() & InputType.TYPE_MASK_VARIATION) == InputType.TYPE_TEXT_VARIATION_PASSWORD);
		}
		
		mKeyboard.show();
		
		setError(null);
		
		return true; //listener consumed the event (prevents default keyboard from showing up)
	}
}
