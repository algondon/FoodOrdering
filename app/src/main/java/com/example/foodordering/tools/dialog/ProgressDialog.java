package com.example.foodordering.tools.dialog;

import java.text.NumberFormat;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.foodordering.R;


@SuppressLint("HandlerLeak")
public class ProgressDialog extends RewriteAlertDialog {

	private ProgressBar mProgress;
	private TextView mMessageView;
	private NumberFormat mProgressPercentFormat;
	private CharSequence mMessage;
	private Context mContext;
	private View view;

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public ProgressDialog(Context context) {
		super(context);
		mContext = context;
		initFormats();
	}

	public ProgressDialog(Context context, int theme) {
		super(context, theme);
		mContext = context;
		initFormats();
	}

	private void initFormats() {
		mProgressPercentFormat = NumberFormat.getPercentInstance();
		mProgressPercentFormat.setMaximumFractionDigits(0);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		LayoutInflater inflater = LayoutInflater.from(mContext);
		view = inflater.inflate(R.layout.progress_dialog, null);
		mProgress = (ProgressBar) view.findViewById(R.id.progress);
		mMessageView = (TextView) view.findViewById(R.id.message);
		setView(view);
		if (mMessage != null) {
			setMessage(mMessage);
		}
		super.onCreate(savedInstanceState);
	}

	@Override
	public void setMessage(CharSequence message) {
		if (mProgress != null) {
			mMessageView.setText(message);
		} else {
			mMessage = message;
		}
	}
	
	public static ProgressDialog show(Context context, CharSequence title,
			CharSequence message) {
		return show(context, title, message, false);
	}

	public static ProgressDialog show(Context context, CharSequence title,
			CharSequence message, boolean indeterminate) {
		return show(context, title, message, indeterminate, false, null);
	}

	public static ProgressDialog show(Context context, CharSequence title,
			CharSequence message, boolean indeterminate, boolean cancelable) {
		return show(context, title, message, indeterminate, cancelable, null);
	}

	public static ProgressDialog show(Context context, CharSequence title,
			CharSequence message, boolean indeterminate, boolean cancelable,
			OnCancelListener cancelListener) {
		ProgressDialog dialog = new ProgressDialog(context);
		dialog.setTitle(title);
		dialog.setMessage(message);
		dialog.setCancelable(cancelable);
		dialog.setOnCancelListener(cancelListener);
		dialog.show();
		return dialog;
	}
}