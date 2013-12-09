package org.caelus.kryptanandroid.views;

import org.caelus.kryptanandroid.core.CoreSecureStringHandler;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.widget.TextView;

public class SecureTextView extends TextView
{

	private Paint mPaint;
	private CoreSecureStringHandler mText;
	private String mTmp;
	private char[] mTmpArr;

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
		mTmp = new String();
		mTmpArr = new char[0];
	}

	// @Override
	// @Deprecated
	// public void setText(CharSequence text, BufferType type)
	// {
	// if(text != "")
	// {
	// super.setText(text, type);
	// }
	// else
	// {
	// throw new
	// UnsupportedOperationException("This operation is not supported! To set a text you should use setSecureText, instead.");
	// }
	// }

	public void setSecureText(CoreSecureStringHandler text)
	{
		mText = text;

		int size = mText.GetLength();

		// try to allocate new objects as little as possible
		if (size != mTmp.length())
		{
			mTmpArr = new char[size];
			mTmp = new String(mTmpArr);
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
			int size = mText.GetLength();
			assert size == mTmp.length();
			// we fill our temprary array
			for (int i = 0; i < size; i++)
			{
				mTmpArr[i] = mText.GetChar(i);
			}
			// then we put the information in our temporary string instance
			CoreSecureStringHandler.overwriteStringInternalArr(mTmp, mTmpArr);
			// Lets destroy our temporary array
			for (int i = 0; i < size; i++)
			{
				mTmpArr[i] = 0;
			}

			// we draw the text
			canvas.drawText(mTmp, this.getWidth() / 2, this.getTotalPaddingTop()
					+ this.getTextSize(), mPaint);
			

//			// debug output while I figure out how to place the text correctly
//			// and portable between devices
//			Log.d("SecureTextView.onDraw",
//					String.format(
//							"getHeight: %d, getPaddingTop: %d, getTotalPaddingTop: %d, getTextSize: %f, getExtendedPaddingTop: %d",
//							this.getHeight(), this.getPaddingTop(),
//							this.getTotalPaddingTop(), this.getTextSize(),
//							this.getExtendedPaddingTop()));

			// And remove the sensitive information IMMEDIETLY afterwards.
			CoreSecureStringHandler.overwriteStringInternalArr(mTmp);
		}

	}
}
