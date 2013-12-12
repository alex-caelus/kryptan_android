package org.caelus.kryptanandroid.views;

import java.util.Vector;

import org.caelus.kryptanandroid.core.CoreSecureStringHandler;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.text.Layout;
import android.util.AttributeSet;
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
		if (size != mTmp.size())
		{
			mTmpArr = new char[size];
		}

		updateShadowCopy();

		// set the shadow text
		setText(new String(mTmpArr));

		if(getLayout() != null)
		{
			updateTmpLineStrings();
		}
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

	@Override
	public void onDraw(Canvas canvas)
	{
		if (mPaint == null)
		{
			mPaint = new Paint();
			mPaint.setStyle(Paint.Style.FILL);
			mPaint.setTextSize(this.getTextSize());
			mPaint.setAntiAlias(true);
			mPaint.setTextAlign(Align.CENTER);
			mPaint.setColor(Color.BLACK);
		}

		if (mText != null)
		{
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
					mTmpArr[i] = mText.GetChar(i);
				}

				// then we put the information in our temporary string instance
				CoreSecureStringHandler.overwriteStringInternalArr(
						mTmp.elementAt(currentLine), mTmpArr, start, size);

				// we draw the actual text

				canvas.drawText(mTmp.get(currentLine), this.getWidth() / 2,
						this.getTotalPaddingTop()
								+ (this.getTextSize() * (currentLine + 1)),
						mPaint);

				// And remove the sensitive information IMMEDIATELY afterwards.
				CoreSecureStringHandler.overwriteStringInternalArr(mTmp.elementAt(currentLine));
			}

			// Lets remove the leftover values from our temporary array
			for (int i = 0; i < mTmpArr.length; i++)
			{
				mTmpArr[i] = 0;
			}
		}
	}
}
