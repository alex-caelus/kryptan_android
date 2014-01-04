package org.caelus.kryptanandroid.buildingblocks;

import java.util.Arrays;

import org.caelus.kryptanandroid.buildingblocks.KryptanKeyboard.KeyboardCloseValidator;
import org.caelus.kryptanandroid.buildingblocks.KryptanKeyboard.KeyboardTextChangedListener;
import org.caelus.kryptanandroid.core.CoreSecureStringHandler;

import android.content.Context;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.EditText;

public class SecureEditText extends EditText implements KeyboardCloseValidator,
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
		clearFocus();
	}

	public CoreSecureStringHandler getSecureText()
	{
		return mCurrentText == null ? CoreSecureStringHandler.NewSecureString() : mCurrentText;
	}
	
	public void setKeyboardDialogTitle(String title)
	{
		mKeyboardTitle = title;
	}

	@Override
	public void KeyboardTextChanged(CoreSecureStringHandler text)
	{
	}

	@Override
	public boolean KeyboardCloseValidate(KryptanKeyboard keyboard,
			CoreSecureStringHandler result)
	{
		mCurrentText = result;
		char[] dummyArray = new char[result.GetLength()];
		Arrays.fill(dummyArray, '*');
		String dummyString = new String(dummyArray);
		setText(dummyString);
		return true;
	}

	@Override
	public boolean onTouch(View arg0, MotionEvent arg1)
	{
		//disable soft keyboard
		int inType = getInputType();
		setInputType(InputType.TYPE_NULL); // disable soft input
//		onTouchEvent(arg1); // call native handler
		setInputType(inType); // restore input type
		
		clearFocus();
		
		if (mKeyboard == null)
		{
			mKeyboard = new KryptanKeyboard(getContext(), mKeyboardTitle);
			mKeyboard.setCloseValidator(this);
			mKeyboard.setTextChangedListener(this);
			mKeyboard.setHintText(getHint());
			mKeyboard
					.setInputTypePassword((this.getInputType() & InputType.TYPE_MASK_VARIATION) == InputType.TYPE_TEXT_VARIATION_PASSWORD);
		}
		
		mKeyboard.show();
		
		setError(null);
		
		
		
		return false; 
	}
}
