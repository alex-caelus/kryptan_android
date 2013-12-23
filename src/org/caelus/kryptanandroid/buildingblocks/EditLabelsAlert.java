/**
 * 
 */
package org.caelus.kryptanandroid.buildingblocks;

import org.caelus.kryptanandroid.EditLabelAdapter;
import org.caelus.kryptanandroid.R;
import org.caelus.kryptanandroid.buildingblocks.SingleEditTextAlert.DialogResultListener;
import org.caelus.kryptanandroid.core.CorePwd;
import org.caelus.kryptanandroid.core.CorePwdFile;
import org.caelus.kryptanandroid.core.CoreSecureStringHandler;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * @author Alexander
 * 
 */
public class EditLabelsAlert extends BaseAlert implements DialogResultListener
{

	private CorePwd mPwd;
	private Button mExistingLabelButton;
	private Button mNewLabelButton;
	private EditLabelAdapter mAdapter;
	private LinearLayout mItemlistView;

	public EditLabelsAlert(Activity activity, CorePwdFile pwdFile, CorePwd pwd)
	{
		super(activity, pwdFile, R.string.action_edit_labels, BaseAlert.BUTTONS_CANCEL, false);
		mPwd = pwd;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.caelus.kryptanandroid.buildingblocks.BaseAlert#getView()
	 */
	@Override
	protected View getView()
	{
		LinearLayout root = (LinearLayout) mActivity.getLayoutInflater()
				.inflate(R.layout.edit_labels_dialog, null);

		mItemlistView = (LinearLayout) root
				.findViewById(R.id.edit_label_current_labels_layout);

		mExistingLabelButton = (Button) root
				.findViewById(R.id.edit_label_select_label_button);
		mNewLabelButton = (Button) root
				.findViewById(R.id.create_new_label_button);

		setButtonClickListeners();

		addLabelLists();

		return root;
	}

	private void setButtonClickListeners()
	{
		mExistingLabelButton.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				ShowLabelSpinner();
			}
		});

		mNewLabelButton.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				CreateNewLabelClicked();
			}
		});
	}

	protected void CreateNewLabelClicked()
	{
		SingleEditTextAlert alert = new SingleEditTextAlert(mActivity, mPwdFile, R.string.create_new_label, R.string.create_new_label_help, this, 0);
		alert.setOnDismissListener(new OnDismissListener()
		{	
			@Override
			public void onDismiss(DialogInterface arg0)
			{
				//we must show 'this' dialog again 
				EditLabelsAlert.this.show();
			}
		});
		this.hide();
		alert.show();
	}

	protected void ShowLabelSpinner()
	{
		AlertDialog.Builder b = new Builder(mActivity);
		b.setTitle(mActivity.getResources().getString(
				R.string.select_label_text));
		b.setSingleChoiceItems(mAdapter, -1,
				new DialogInterface.OnClickListener()
				{

					@Override
					public void onClick(DialogInterface dialog, int which)
					{
						CoreSecureStringHandler label = (CoreSecureStringHandler) mAdapter
								.getItem(which);
						addLabelToPassword(label);
						dialog.dismiss();
					}
				});
		b.show();
	}

	protected void addLabelToPassword(CoreSecureStringHandler label)
	{
		mPwdFile.getPasswordList().addPwdToLabel(mPwd, label);
		mPwdFile.Save();
		mAdapter.updateAvailableLabels();
		if (mAdapter.getCount() == 0)
		{
			mExistingLabelButton.setEnabled(false);
		}

		// add to list
		appendSelectedLabel(label);
	}

	private void addLabelLists()
	{
		mAdapter = new EditLabelAdapter(mActivity, mPwdFile.getPasswordList(),
				mPwd);

		CoreSecureStringHandler[] selectedLabels = mAdapter.getSelectedLabels();

		for (CoreSecureStringHandler label : selectedLabels)
		{
			appendSelectedLabel(label);
		}

		if (mAdapter.getCount() == 0)
		{
			mExistingLabelButton.setEnabled(false);
		}
	}

	private void appendSelectedLabel(CoreSecureStringHandler label)
	{
		LinearLayout item = (LinearLayout) mActivity.getLayoutInflater()
				.inflate(R.layout.edit_label_list_item, null);
		mItemlistView.addView(item);

		SecureTextView LabelNameView = (SecureTextView) item
				.findViewById(R.id.EditLabelItemName);

		TextView LabelPwdCountView = (TextView) item
				.findViewById(R.id.EditLabelItemPwdCount);

		Button removeButton = (Button) item
				.findViewById(R.id.EditLabelRemoveButton);
		removeButton.setTag(label);
		removeButton.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				removeLabel(v);
			}
		});

		LabelNameView.setSecureText(label);
		LabelPwdCountView.setText(String.format(mActivity.getResources()
				.getString(R.string.label_nr_of_passwords_format), mPwdFile
				.getPasswordList().CountPwds(label)));
	}

	protected void removeLabel(View v)
	{
		CoreSecureStringHandler label = (CoreSecureStringHandler) v.getTag();
		mPwdFile.getPasswordList().removePwdFromLabel(mPwd, label);
		mPwdFile.Save();

		mAdapter.updateAvailableLabels();
		if (mAdapter.getCount() == 0)
		{
			mExistingLabelButton.setEnabled(false);
		}
		else
		{
			mExistingLabelButton.setEnabled(true);
		}

		// remove in gui also
		ViewGroup labelLayout = (ViewGroup) v.getParent();
		((ViewGroup) labelLayout.getParent()).removeView(labelLayout);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.caelus.kryptanandroid.buildingblocks.BaseAlert#onPositiveClicked()
	 */
	@Override
	protected boolean onPositiveClicked()
	{
		// Let the user exit
		return true;
	}

	@Override
	protected void onInit()
	{
	}

	@Override
	public boolean onDialogResult(SingleEditTextAlert dialog, CoreSecureStringHandler result)
	{
		if(result.GetLength() == 0)
		{
			dialog.setErrorMessage(R.string.error_field_required);
			return false;
		}
		//see if label already exists
		CoreSecureStringHandler[] searchResult = mPwdFile.getPasswordList().FilterLabels(result.getNativeHandle());
		boolean exists = false;
		for(CoreSecureStringHandler res : searchResult)
		{
			if(res.GetLength() == result.GetLength())
			{
				exists = true;
			}
		}
		
		if(exists)
		{
			//if the label already exists we fail validation
			dialog.setErrorMessage(R.string.error_label_already_exists);
			return false;
		}
		
		//validation passed!
		addLabelToPassword(result);
		return true;
	}

}
