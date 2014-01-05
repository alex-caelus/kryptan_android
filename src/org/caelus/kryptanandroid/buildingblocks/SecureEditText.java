package org.caelus.kryptanandroid.buildingblocks;

import org.caelus.kryptanandroid.buildingblocks.KryptanKeyboard.KeyboardCloseListener;
import org.caelus.kryptanandroid.buildingblocks.KryptanKeyboard.KeyboardTextChangedListener;
import org.caelus.kryptanandroid.core.CoreSecureStringHandler;

import android.content.Context;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class SecureEditText extends SecureTextView implements
		KeyboardCloseListener, KeyboardTextChangedListener, OnTouchListener
{

	private KryptanKeyboard mKeyboard;
	private String mKeyboardTitle = null;
	private KeyboardTextChangedListener mTextChangedListener;

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

	public void setKeyboardDialogTitle(String title)
	{
		mKeyboardTitle = title;
	}
	
	public void setOnSecureTextChangedListener(KeyboardTextChangedListener listener)
	{
		mTextChangedListener = listener;
	}

	@Override
	public void KeyboardTextChanged(CoreSecureStringHandler text)
	{
		if(mTextChangedListener != null)
		{
			mTextChangedListener.KeyboardTextChanged(text);
		}
	}

	@Override
	public boolean KeyboardCloseValidate(KryptanKeyboard keyboard,
			CoreSecureStringHandler result)
	{
		setSecureText(result);
		return true;
	}

	@Override
	public boolean onTouch(View arg0, MotionEvent arg1)
	{
		// disable soft keyboard
		int inType = getInputType();
		setInputType(InputType.TYPE_NULL); // disable soft input
		//onTouchEvent(arg1); // call native handler
		setInputType(inType); // restore input type

		setError(null);

		//we remove the hint or text so that the user gets visual feedback of the click
		setTextVisibility(false);

		this.post(new Runnable()
		{

			@Override
			public void run()
			{
				if (mKeyboard == null)
				{
					mKeyboard = new KryptanKeyboard(getContext(),
							mKeyboardTitle);
					mKeyboard.setCloseValidator(SecureEditText.this);
					mKeyboard.setTextChangedListener(SecureEditText.this);
					mKeyboard.setHintText(getHint());
					mKeyboard.setInputTypePassword((SecureEditText.this
							.getInputType() & InputType.TYPE_MASK_VARIATION) == InputType.TYPE_TEXT_VARIATION_PASSWORD);
				}

				mKeyboard.setSecureText(getSecureText());
				mKeyboard.show();
			}
		});

		return false;
	}

	@Override
	public void KeyboardShowChanged(KryptanKeyboard keyboard, boolean isShowing)
	{
		if (!isShowing)
		{
			setTextVisibility(true);
		}
	}
}
