package org.caelus.kryptanandroid.buildingblocks;

import java.util.Arrays;
import java.util.Locale;

import org.caelus.kryptanandroid.Global;
import org.caelus.kryptanandroid.R;
import org.caelus.kryptanandroid.core.CoreSecureStringHandler;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.qustom.dialog.QustomDialogBuilder;

public class KryptanKeyboard implements OnClickListener, OnDismissListener
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

	public KeyboardCloseValidator mCloseValidator = null;

	private Dialog mDialog;

	private boolean mShiftPressed;

	private long mShiftPressedTimestamp;

	private boolean mCapsOn;

	private TextView mPasswordDottedView;

	private QustomDialogBuilder mBuilder;

	private int mId;

	private Context mContext;

	public interface KeyboardCloseValidator
	{
		boolean KeyboardCloseValidate(KryptanKeyboard keyboard,
				CoreSecureStringHandler result);
	}

	public KryptanKeyboard(Context context, String Title)
	{
		mBuilder = new QustomDialogBuilder(context);
		
		mContext = context;

		if (Title != null)
		{
			mBuilder.setTitle(Title);
		}
		mBuilder.setTitleColor(Global.THEME_ACCENT_COLOR_STRING);
		mBuilder.setDividerColor(Global.THEME_ACCENT_COLOR_STRING);

		mBuilder.setCustomView(R.layout.kryptan_keyboard, context);

		mDialog = mBuilder.create();

		mDialog.setOnDismissListener(this);

		mKeyboardView = (ViewGroup) mBuilder.getCustomView();

		mTextView = (SecureTextView) mKeyboardView
				.findViewById(R.id.keyboardText);
		mPasswordDottedView = (TextView) mKeyboardView
				.findViewById(R.id.keyboardPasswordDottedText);
		mHintView = (TextView) mKeyboardView.findViewById(R.id.keyboardHint);

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

	public void setId(int id)
	{
		mId = id;
	}

	public int getId()
	{
		return mId;
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

	public void setCloseValidator(KeyboardCloseValidator listener)
	{
		mCloseValidator = listener;
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

		case R.id.keyCancel:
			cancelPressed();
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

	public void setError(String message)
	{
		mTextView.setError(message);
		mPasswordDottedView.setError(message);
	}

	@Override
	public void onDismiss(DialogInterface arg0)
	{
		clearText();
	}

	protected void clearText()
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

	private void donePressed()
	{
		if (mCloseValidator != null)
		{
			CoreSecureStringHandler currentText = mTextView.getSecureText();
			if (currentText == null)
			{
				currentText = CoreSecureStringHandler.NewSecureString();
			}
			if (mCloseValidator.KeyboardCloseValidate(this, currentText))
			{
				mDialog.hide();
			}
		} else
		{
			mDialog.hide();
		}
	}

	private void cancelPressed()
	{
		clearText();
		mDialog.hide();
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
		if (mPasswordDottedView.getVisibility() == View.VISIBLE)
		{
			char[] dummyArray = new char[currentText.GetLength()];
			Arrays.fill(dummyArray, '•');
			mPasswordDottedView.setText(new String(dummyArray));
		}

		// Remove error messages
		if (mTextView.getError() != null && mTextView.getError().length() > 0)
		{
			mTextView.setError(null);
			mPasswordDottedView.setError(null);
		}

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

		CoreSecureStringHandler newText = CoreSecureStringHandler
				.NewSecureString();

		for (int i = 0; i < length - 1; i++)
		{
			newText.AddChar(currentText.GetChar(i));
		}

		currentText = newText;

		mTextView.setSecureText(currentText);

		onTextChanged(currentText);

		if (currentText.GetLength() == 0
				&& mHintView.getVisibility() == View.INVISIBLE)
		{
			mHintView.setVisibility(View.VISIBLE);
		}
	}

	@SuppressWarnings("deprecation")
	private void shiftPressed(Button shiftButton)
	{
		mShiftPressed = !mShiftPressed;
		if (!mShiftPressed
				&& ((System.currentTimeMillis() - mShiftPressedTimestamp) < Global.SHIFT_DOUBLE_CLICK_TIME_MILLIS))
		{
			mShiftPressed = true;
			mCapsOn = true;
			shiftButton.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.kryptantheme_btn_default_focused_holo_dark));
		}

		if (mShiftPressed)
		{
			mShiftPressedTimestamp = System.currentTimeMillis();
		} else
		{
			mCapsOn = false;
			shiftButton.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.kryptantheme_btn_default_normal_holo_dark));
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
		case R.id.keyCancel:
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
