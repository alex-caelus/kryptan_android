package org.caelus.kryptanandroid.buildingblocks;

import java.util.Arrays;
import java.util.Locale;

import org.caelus.kryptanandroid.Global;
import org.caelus.kryptanandroid.R;
import org.caelus.kryptanandroid.core.CoreSecureStringHandler;

import android.R.color;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class KryptanKeyboard implements OnClickListener
{
	boolean mIsUpperCase = false;

	private ViewGroup mKeyboardView;

	private SecureTextView mTextView;

	private TextView mHintView;

	public KeyboardTextChangedListener mTextChangedListener = null;

	public interface KeyboardTextChangedListener
	{
		void KeyboardTextChanged(CoreSecureStringHandler text);
	}

	public KeyboardClosedListener mClosedListener = null;

	private Dialog mDialog;

	private boolean mShiftPressed;

	private long mShiftPressedTimestamp;

	private boolean mCapsOn;

	private TextView mPasswordDottedView;

	public interface KeyboardClosedListener
	{
		void KeyboardClosed();
	}

	public KryptanKeyboard(Context context, String Title)
	{
		LayoutInflater mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		mKeyboardView = (ViewGroup) mInflater.inflate(
				R.layout.kryptan_keyboard, null);

		mTextView = (SecureTextView) mKeyboardView
				.findViewById(R.id.keyboardText);
		mPasswordDottedView = (TextView) mKeyboardView
				.findViewById(R.id.keyboardPasswordDottedText);
		mHintView = (TextView) mKeyboardView.findViewById(R.id.keyboardHint);

		if (Title == null)
		{
			mDialog = new Dialog(context,
					android.R.style.Theme_Holo_Dialog_NoActionBar);
		} else
		{
			mDialog = new Dialog(context, android.R.style.Theme_Dialog);
			mDialog.setTitle(Title);
		}

		mDialog.setContentView(mKeyboardView);

		int rowCount = mKeyboardView.getChildCount();
		for (int i = 0; i < rowCount; i++)
		{
			ViewGroup row = (ViewGroup) mKeyboardView.getChildAt(i);
			int columnCount = row.getChildCount();
			for (int j = 0; j < columnCount; j++)
			{
				View view = row.getChildAt(j);
				if (view instanceof Button)
				{
					initButtonView((Button) view);
				}
			}
		}

		// default state
		setInputTypePassword(false);
	}

	void initButtonView(Button button)
	{
		updateButtonCase(button);
		button.setOnClickListener(this);
	}

	public void show()
	{
		mDialog.show();
	}

	public void setInputTypePassword(boolean password)
	{
		if (password)
		{
			mTextView.setVisibility(View.INVISIBLE);
			mPasswordDottedView.setVisibility(View.VISIBLE);
		} else
		{
			mTextView.setVisibility(View.VISIBLE);
			mPasswordDottedView.setVisibility(View.INVISIBLE);
		}
	}

	public void setTextChangedListener(KeyboardTextChangedListener listener)
	{
		mTextChangedListener = listener;
	}

	public void setClosedListener(KeyboardClosedListener listener)
	{
		mClosedListener = listener;
	}

	public void setHintText(CharSequence charSequence)
	{
		mHintView.setText(charSequence);
	}

	public void onClick(View arg0)
	{
		// We end up here when any key has been pressed
		switch (arg0.getId())
		{
		case R.id.keySHIFT:
			shiftPressed((Button) arg0);
			break;

		case R.id.keyDEL:
			deletePressed();
			break;

		case R.id.keyClear:
			clearPressed();
			break;

		case R.id.keySpace:
			addToText(" ");
			break;

		case R.id.keyDone:
			donePressed();
			break;

		default:
			// The pressed button was obviously a normal key so lets add it to
			// the text
			if (arg0 instanceof Button)
			{
				Button button = (Button) arg0;
				String key = button.getText().toString();
				addToText(key);
			}
			break;
		}
	}

	private void addToText(String s)
	{
		if (mHintView.getVisibility() == View.VISIBLE)
		{
			mHintView.setVisibility(View.INVISIBLE);
		}

		CoreSecureStringHandler currentText = mTextView.getSecureText();
		if (currentText == null)
		{
			currentText = CoreSecureStringHandler.NewSecureString();
		}

		for (int i = 0; i < s.length(); i++)
		{
			char c = s.charAt(i);
			// Process char
			currentText.AddChar(c);
		}

		mTextView.setSecureText(currentText);

		onTextChanged(currentText);
	}

	private void donePressed()
	{
		mDialog.hide();
	}

	private void clearPressed()
	{
		CoreSecureStringHandler currentText = mTextView.getSecureText();

		if (currentText == null)
		{
			currentText = CoreSecureStringHandler.NewSecureString();
		}
		currentText.Clear();

		mTextView.setSecureText(currentText);

		onTextChanged(currentText);

		mHintView.setVisibility(View.VISIBLE);
	}

	protected void onTextChanged(CoreSecureStringHandler currentText)
	{
		// SHIFT state
		if (mShiftPressed && !mCapsOn)
		{
			mShiftPressed = false;
			updateAllButtonsCase();
		}
		mShiftPressedTimestamp = 0;

		// Update passwordDottedText
		char[] dummyArray = new char[currentText.GetLength()];
		Arrays.fill(dummyArray, '•');
		mPasswordDottedView.setText(new String(dummyArray));

		// LISTENER
		if (mTextChangedListener != null)
		{
			mTextChangedListener.KeyboardTextChanged(currentText);
		}
	}

	private void deletePressed()
	{
		CoreSecureStringHandler currentText = mTextView.getSecureText();
		
		if (currentText == null)
		{
			return;
		}
		
		int length = currentText.GetLength();
		
		if (currentText.GetLength() == 0)
		{
			return;
		}

		CoreSecureStringHandler newText = CoreSecureStringHandler.NewSecureString();
		
		for (int i = 0; i < length-1; i++)
		{
			newText.AddChar(currentText.GetChar(i));
		}
		
		currentText = newText;

		mTextView.setSecureText(currentText);

		onTextChanged(currentText);

		if (currentText.GetLength() == 0 && mHintView.getVisibility() == View.INVISIBLE)
		{
			mHintView.setVisibility(View.VISIBLE);
		}
	}

	private void shiftPressed(Button shiftButton)
	{
		mShiftPressed = !mShiftPressed;
		if (!mShiftPressed
				&& ((System.currentTimeMillis() - mShiftPressedTimestamp) < Global.SHIFT_DOUBLE_CLICK_TIME_MILLIS))
		{
			mShiftPressed = true;
			mCapsOn = true;
			shiftButton.requestFocus();
		}

		if (mShiftPressed)
		{
			mShiftPressedTimestamp = System.currentTimeMillis();
		} else
		{
			mCapsOn = false;
			shiftButton.clearFocus();
		}

		updateAllButtonsCase();
	}

	protected void updateAllButtonsCase()
	{
		int rowCount = mKeyboardView.getChildCount();
		for (int i = 0; i < rowCount; i++)
		{
			ViewGroup row = (ViewGroup) mKeyboardView.getChildAt(i);
			int columnCount = row.getChildCount();
			for (int j = 0; j < columnCount; j++)
			{
				View view = row.getChildAt(j);
				if (view instanceof Button)
				{
					Button button = (Button) view;
					updateButtonCase(button);
				}
			}
		}
	}

	protected void updateButtonCase(Button button)
	{
		// There are exceptions every rule
		switch (button.getId())
		{
		case R.id.keyCapitalSharpS:
		case R.id.keyClear:
		case R.id.keySpace:
		case R.id.keyDone:
		case R.id.keySHIFT:
		case R.id.keyDEL:
			return;
		default:
		}

		// this is not a excepted button
		if (mShiftPressed)
		{
			button.setText(button.getText().toString()
					.toUpperCase(Locale.ENGLISH));
		} else
		{
			button.setText(button.getText().toString()
					.toLowerCase(Locale.ENGLISH));
		}
	}
}
