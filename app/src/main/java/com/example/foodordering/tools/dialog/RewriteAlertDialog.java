package com.example.foodordering.tools.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;

import com.example.foodordering.R;


public class RewriteAlertDialog extends Dialog implements DialogInterface {

	private RewriteAlertController mAlert;

	protected RewriteAlertDialog(Context context) {
		this(context, R.style.Rewrite_Dialog_Alert);
	}

	protected RewriteAlertDialog(Context context, int theme) {
		super(context, theme);
		mAlert = new RewriteAlertController(context, this, getWindow());
	}

	protected RewriteAlertDialog(Context context, boolean cancelable,
			OnCancelListener cancelListener) {
		super(context, R.style.Rewrite_Dialog_Alert);
		setCancelable(cancelable);
		setOnCancelListener(cancelListener);
		mAlert = new RewriteAlertController(context, this, getWindow());
	}

	@Override
	public void setTitle(CharSequence title) {
		super.setTitle(title);
		mAlert.setTitle(title);
	}

	public void setCustomTitle(View customTitleView) {
		mAlert.setCustomTitle(customTitleView);
	}

	public void setMessage(CharSequence message) {
		mAlert.setMessage(message);
	}

	public void setView(View view) {
		mAlert.setView(view);
	}

	public void setButton(int whichButton, CharSequence text, Message msg) {
		mAlert.setButton(whichButton, text, null, msg);
	}

	public void setButton(int whichButton, CharSequence text,
			OnClickListener listener) {
		mAlert.setButton(whichButton, text, listener, null);
	}

	public void setIcon(int resId) {
		mAlert.setIcon(resId);
	}

	public void setIcon(Drawable icon) {
		mAlert.setIcon(icon);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mAlert.installContent();
	}
	
	public static class Builder {
		
		private final RewriteAlertController.AlertParams P;

		public Builder(Context context) {
			P = new RewriteAlertController.AlertParams(context);
		}

		public Builder setTitle(int titleId) {
			P.mTitle = P.mContext.getText(titleId);
			return this;
		}

		public Builder setTitle(CharSequence title) {
			P.mTitle = title;
			return this;
		}

		public Builder setCustomTitle(View customTitleView) {
			P.mCustomTitleView = customTitleView;
			return this;
		}

		public Builder setMessage(int messageId) {
			P.mMessage = P.mContext.getText(messageId);
			return this;
		}

		public Builder setMessage(CharSequence message) {
			P.mMessage = message;
			return this;
		}

		public Builder setIcon(int iconId) {
			P.mIconId = iconId;
			return this;
		}

		public Builder setIcon(Drawable icon) {
			P.mIcon = icon;
			return this;
		}

		public Builder setPositiveButton(int textId,
				final OnClickListener listener) {
			P.mPositiveButtonText = P.mContext.getText(textId);
			P.mPositiveButtonListener = listener;
			return this;
		}

		public Builder setPositiveButton(CharSequence text,
				final OnClickListener listener) {
			P.mPositiveButtonText = text;
			P.mPositiveButtonListener = listener;
			return this;
		}

		public Builder setNegativeButton(int textId,
				final OnClickListener listener) {
			P.mNegativeButtonText = P.mContext.getText(textId);
			P.mNegativeButtonListener = listener;
			return this;
		}

		public Builder setNegativeButton(CharSequence text,
				final OnClickListener listener) {
			P.mNegativeButtonText = text;
			P.mNegativeButtonListener = listener;
			return this;
		}

		public Builder setNeutralButton(int textId,
				final OnClickListener listener) {
			P.mNeutralButtonText = P.mContext.getText(textId);
			P.mNeutralButtonListener = listener;
			return this;
		}

		public Builder setNeutralButton(CharSequence text,
				final OnClickListener listener) {
			P.mNeutralButtonText = text;
			P.mNeutralButtonListener = listener;
			return this;
		}

		public Builder setItems(int itemsId, final OnClickListener listener) {
			P.mItems = P.mContext.getResources().getTextArray(itemsId);
			P.mOnClickListener = listener;
			P.isAdapter=false;
			return this;
		}

		public Builder setItems(CharSequence[] items,
				final OnClickListener listener) {
			P.mItems = items;
			P.mOnClickListener = listener;
			P.isAdapter=false;
			return this;
		}

		public Builder setAdapter(final ListAdapter adapter,
				final OnClickListener listener) {
			P.mAdapter = adapter;
			P.mOnClickListener = listener;
			P.isAdapter=true;
			return this;
		}

		public Builder setOnItemSelectedListener(
				final AdapterView.OnItemSelectedListener listener) {
			P.mOnItemSelectedListener = listener;
			return this;
		}

		public Builder setView(View view) {
			P.mView = view;
			return this;
		}

		public RewriteAlertDialog create() {
			final RewriteAlertDialog dialog = new RewriteAlertDialog(P.mContext);
			P.apply(dialog.mAlert);
			if (P.mOnKeyListener != null) {
				dialog.setOnKeyListener(P.mOnKeyListener);
			}
			return dialog;
		}

		public RewriteAlertDialog show() {
			RewriteAlertDialog dialog = create();
			dialog.show();
			return dialog;
		}
	}

}
