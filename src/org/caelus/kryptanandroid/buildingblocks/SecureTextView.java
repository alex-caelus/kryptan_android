package org.caelus.kryptanandroid.buildingblocks;

import java.util.Vector;

import org.caelus.kryptanandroid.core.CoreSecureStringHandler;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.text.InputType;
import android.text.Layout;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnLayoutChangeListener;
import android.widget.TextView;

public class SecureTextView extends TextView implements OnLayoutChangeListener
{

	private Paint mPaint;
	private CoreSecureStringHandler mText;
	private Vector<String> mTmp;
	private char[] mTmpArr;
	private Layout mLayout;
	private Paint mHintPaint;
	private boolean mTextVisible = true;

	public SecureTextView(Context context)
	{
		super(context);
		init();
	}

	public SecureTextView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		init();
	}

	public SecureTextView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		init();
	}

	private void init()
	{
		mTmp = new Vector<String>();
		mTmpArr = new char[0];
		this.addOnLayoutChangeListener(this);
	}

	public void setSecureText(CoreSecureStringHandler text)
	{
		// store the text
		mText = text;

		// length of text
		int size = mText.GetLength();

		// make sure we have enough space in our temporary buffer
		if (size != mTmpArr.length) //BUGFIX: <= does not work
		{
			mTmpArr = new char[size];
		}

		updateShadowCopy();

		// set the shadow text
		setText(new String(mTmpArr));

		if (getLayout() != null)
		{
			updateTmpLineStrings();
		}
	}

	public CoreSecureStringHandler getSecureText()
	{
		return mText == null ? CoreSecureStringHandler.NewSecureString()
				: mText;
	}

	@Override
	public void onLayoutChange(View arg0, int arg1, int arg2, int arg3,
			int arg4, int arg5, int arg6, int arg7, int arg8)
	{
		updateTmpLineStrings();
	}

	private void updateTmpLineStrings()
	{
		// get the layout
		mLayout = getLayout();

		// we need a new String instance for each line
		// This is because the onDraw function needs to
		// use reflection and modify the internal char array
		// of the string, and we do this for each line instead
		// of the whole text in one go.
		int lines = mLayout.getLineCount();

		// clear out array
		mTmp.clear();

		// add a new string for each line
		for (int i = 0; i < lines; i++)
		{
			int start = mLayout.getLineStart(i);
			int end = mLayout.getLineEnd(i);
			int charCount = end - start;

			// note that the string only contains a shadow copy
			// of the real contents
			mTmp.add(new String(mTmpArr, start, charCount));
		}
	}

	// in order to utilize the TextView's wrapping rules we put
	// a corresponding number of characters into the regular string buffer
	// I will refer to this as shadow text from here on.
	private void updateShadowCopy()
	{
		char overwriteMe;
		int size = mText.GetLength();

		assert size <= mTmpArr.length;

		for (int i = 0; i < size; i++)
		{
			overwriteMe = mText.GetChar(i);

			// If the string contains a space
			// input a dash to shadow buffer to indicate word boundary.
			// This is needed for the wrapping to work as expected.
			if (overwriteMe == ' ')
			{
				// the test above could include more characters but for my
				// purposes
				// a space will be enough to check for.
				// TODO: Check to see if space is the only character that is to
				// be used as a word boundary.
				mTmpArr[i] = '-';
			} else
			{
				mTmpArr[i] = 'x';
			}
		}

	}
	
	public void setTextVisibility(boolean visible)
	{
		mTextVisible = visible;
	}

	@Override
	public void onDraw(Canvas canvas)
	{
		if(!mTextVisible)
		{
			return; //nothing to do
		}
		
		if (mText != null && mText.GetLength() != 0)
		{
			if (mPaint == null)
			{
				mPaint = new Paint();
				mPaint.setStyle(Paint.Style.FILL);
				mPaint.setTextSize(this.getTextSize());
				mPaint.setAntiAlias(true);
				if ((getGravity() & Gravity.HORIZONTAL_GRAVITY_MASK) == Gravity.LEFT)
					mPaint.setTextAlign(Align.LEFT);
				else
					mPaint.setTextAlign(Align.CENTER);
				mPaint.setColor(getCurrentTextColor());
			}

			// do this for each line of text
			int lineCount = getLineCount();
			for (int currentLine = 0; currentLine < lineCount; currentLine++)
			{
				mLayout = getLayout();
				int start = mLayout.getLineStart(currentLine);
				int end = mLayout.getLineEnd(currentLine);
				int size = end - start;

				assert end < mTmpArr.length;

				// we fill our temporary array
				for (int i = start; i < end; i++)
				{
					if ((this.getInputType() & InputType.TYPE_MASK_VARIATION) == InputType.TYPE_TEXT_VARIATION_PASSWORD)
						mTmpArr[i] = '•';
					else
						mTmpArr[i] = mText.GetChar(i);
				}

				// then we put the information in our temporary string instance
				CoreSecureStringHandler.overwriteStringInternalArr(
						mTmp.elementAt(currentLine), mTmpArr, start, size);

				// we draw the actual text
				int gr = getGravity();

				int x;
				if ((gr & Gravity.HORIZONTAL_GRAVITY_MASK) == Gravity.LEFT)
				{
					x = this.getTotalPaddingLeft();
				} else
				{
					x = this.getWidth() / 2;
				}

				canvas.drawText(mTmp.get(currentLine), x, this.getBaseline()
						+ (this.getTextSize() * (currentLine)), mPaint);

				// And remove the sensitive information IMMEDIATELY afterwards.
				CoreSecureStringHandler.overwriteStringInternalArr(mTmp
						.elementAt(currentLine));
			}

			// Lets remove the leftover values from our temporary array
			for (int i = 0; i < mTmpArr.length; i++)
			{
				mTmpArr[i] = 0;
			}
		} else if (getHint() != null && getHint().length() > 0)
		{
			// display hint
			if (mHintPaint == null)
			{
				mHintPaint = new Paint();
				mHintPaint.setStyle(Paint.Style.FILL);
				mHintPaint.setTextSize(this.getTextSize());
				mHintPaint.setAntiAlias(true);
				if ((getGravity() & Gravity.HORIZONTAL_GRAVITY_MASK) == Gravity.LEFT)
					mHintPaint.setTextAlign(Align.LEFT);
				else
					mHintPaint.setTextAlign(Align.CENTER);
				mHintPaint.setColor(getCurrentHintTextColor());
			}

			int gr = getGravity();
			int x;
			if ((gr & Gravity.HORIZONTAL_GRAVITY_MASK) == Gravity.LEFT)
			{
				x = this.getTotalPaddingLeft();
			} else
			{
				x = this.getWidth() / 2;
			}

			canvas.drawText(getHint().toString(), x, this.getBaseline(),
					mHintPaint);
		}
	}
}
