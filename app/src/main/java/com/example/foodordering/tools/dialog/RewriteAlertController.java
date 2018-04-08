package com.example.foodordering.tools.dialog;

import java.lang.ref.WeakReference;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.foodordering.R;

public class RewriteAlertController {

	private DialogInterface mDI;
	private Window mWindow;
	private View mView;
	private int mAlertDialogLayout; // mAlertDialogLayout：AlertDialog基础布局
	private int mListLayout; // mListLayout：List布局
	private int mSingleChoiceItemLayout; // mSingleChoiceItemLayout：单选布局
	private int mListItemLayout; // mListItemLayout：listItem布局
	private Handler mHandler;
	private View mCustomTitleView;
	private ImageView mImageIcon;
	private TextView mTextTitle, mTextMessage;
	private CharSequence mTitle, mPositiveTitle, mNegativeTitle, mNeutralTitle,
			message;
	private int iconID = 0;
	private Drawable icon;
	private Button Button_Positive, Button_Negative, Button_Neutral;
	private Message mPositiveMessage, mNegativeMessage, mNeutralMessage;
	private ScrollView mScrollView;
	private ListView mListView;
	private ListAdapter mAdapter;

	public RewriteAlertController(Context context, DialogInterface di,
			Window window) {
		mDI = di;
		mWindow = window;
		mHandler = new ButtonHandler(di);

		mAlertDialogLayout = R.layout.rewrite_alert_dialog;
		mListLayout = R.layout.select_dialog;
		mSingleChoiceItemLayout = R.layout.select_dialog_singlechoice;
		mListItemLayout = R.layout.select_dialog_item;
	}

	public void installContent() {
		mWindow.requestFeature(Window.FEATURE_NO_TITLE);
		if (mView == null || canTextInput(mView)) {
			mWindow.setFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM,
					WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
			mWindow.setContentView(mAlertDialogLayout);
			setupView();
		}else{
			mWindow.setContentView(mView);
		}
	}

	private boolean canTextInput(View view) {
		if (view.onCheckIsTextEditor()) {
			return true;
		}
		if (!(view instanceof ViewGroup)) {
			return false;
		}
		ViewGroup VG = (ViewGroup) view;
		int count = VG.getChildCount();
		while (count > 0) {
			count--;
			view = VG.getChildAt(count);
			if (canTextInput(view)) {
				return true;
			}
		}
		return false;
	}

	private void setupView() {
		LinearLayout topPanel = (LinearLayout) mWindow
				.findViewById(R.id.topPanel);
		LinearLayout contentPanel = (LinearLayout) mWindow
				.findViewById(R.id.contentPanel);
		LinearLayout buttonPanel = (LinearLayout) mWindow
				.findViewById(R.id.buttonPanel);

		setupContent(contentPanel);
		boolean hasTitle = setupTitle(topPanel);
		boolean hasButton = setupButton(buttonPanel);

		if (!hasTitle) {
			topPanel.setVisibility(View.GONE);
		}

		if (!hasButton) {
			buttonPanel.setVisibility(View.GONE);
		}
		
		if(mListView!=null && mAdapter!=null){
			mListView.setAdapter(mAdapter);
		}
	}

	private void setupContent(LinearLayout contentPanel) {
		mScrollView = (ScrollView) mWindow.findViewById(R.id.scrollView);
		mScrollView.setFocusable(false);
		mTextMessage = (TextView) mWindow.findViewById(R.id.message);

		if (mTextMessage == null) {
			return;
		} else if (!TextUtils.isEmpty(message)) {
			mTextMessage.setText(message);
		} else {
			mTextMessage.setVisibility(View.GONE);
			mScrollView.removeView(mTextMessage);
			if (mListView != null) {
				contentPanel.removeView(mScrollView);
				contentPanel.addView(mListView, new LinearLayout.LayoutParams(
						LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
				contentPanel.setLayoutParams(new LinearLayout.LayoutParams(
						LayoutParams.MATCH_PARENT, 0, 1.0f));
			} else {
				contentPanel.setVisibility(View.GONE);
			}
		}
	}

	private boolean setupTitle(LinearLayout layout) {
		boolean hasTitle = false;
		if (mCustomTitleView != null) {
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.MATCH_PARENT,
					LinearLayout.LayoutParams.WRAP_CONTENT);
			layout.addView(mCustomTitleView, 0, params);

			hasTitle = true;
		} else {
			final boolean hasTextTitle = !TextUtils.isEmpty(mTitle);
			mImageIcon = (ImageView) mWindow.findViewById(R.id.icon);
			if (hasTextTitle) {
				mTextTitle = (TextView) mWindow.findViewById(R.id.title);
				mTextTitle.setText(mTitle);
				hasTitle = true;
			}
			if (iconID > 0) {
				mImageIcon.setImageResource(iconID);
				hasTitle = true;
			} else if (icon != null) {
				mImageIcon.setImageDrawable(icon);
				hasTitle = true;
			} else {
				mImageIcon.setVisibility(View.GONE);
			}
		}
		return hasTitle;
	}

	private boolean setupButton(LinearLayout layout) {
		boolean hasButton = false;
		Button_Positive = (Button) mWindow.findViewById(R.id.positive);
		Button_Negative = (Button) mWindow.findViewById(R.id.negative);
		Button_Neutral = (Button) mWindow.findViewById(R.id.neutral);

		Button_Positive.setOnClickListener(clickListener);
		Button_Negative.setOnClickListener(clickListener);
		Button_Neutral.setOnClickListener(clickListener);

		if (TextUtils.isEmpty(mPositiveTitle)) {
			Button_Positive.setVisibility(View.GONE);
		} else {
			Button_Positive.setVisibility(View.VISIBLE);
			Button_Positive.setText(mPositiveTitle);
			hasButton = true;
		}
		if (TextUtils.isEmpty(mNegativeTitle)) {
			Button_Negative.setVisibility(View.GONE);
		} else {
			Button_Negative.setVisibility(View.VISIBLE);
			Button_Negative.setText(mNegativeTitle);
			hasButton = true;
		}
		if (TextUtils.isEmpty(mNeutralTitle)) {
			Button_Neutral.setVisibility(View.GONE);
		} else {
			Button_Negative.setVisibility(View.VISIBLE);
			Button_Neutral.setText(mNeutralTitle);
			hasButton = true;
		}
		return hasButton;
	}

	private View.OnClickListener clickListener = new View.OnClickListener() {

		@Override
		public void onClick(View view) {
			Message message = null;
			if (view == Button_Positive && mPositiveMessage != null) {
				message = Message.obtain(mPositiveMessage);
			}
			if (view == Button_Negative && mNegativeMessage != null) {
				message = Message.obtain(mNegativeMessage);
			}
			if (view == Button_Neutral && mNeutralMessage != null) {
				message = Message.obtain(mNeutralMessage);
			}
			if (message != null) {
				message.sendToTarget();
			}
			mHandler.obtainMessage(ButtonHandler.MSG_DISMISS_DIALOG, mDI)
					.sendToTarget();
		}
	};

	public void setIcon(int id) {
		iconID = id;
	}

	public void setIcon(Drawable icon) {
		this.icon = icon;
	}

	public void setTitle(CharSequence title) {
		mTitle = title;
	}

	public void setCustomTitle(View customTitle) {
		mCustomTitleView = customTitle;
	}

	public void setMessage(CharSequence message) {
		this.message = message;
	}

	public void setView(View view) {
		mView = view;
	}

	public void setButton(int whichButton, CharSequence text,
			DialogInterface.OnClickListener listener, Message msg) {
		if (msg == null && listener != null) {
			msg = mHandler.obtainMessage(whichButton, listener);
		}

		switch (whichButton) {
		case DialogInterface.BUTTON_POSITIVE:
			mPositiveTitle = text;
			mPositiveMessage = msg;
			break;

		case DialogInterface.BUTTON_NEGATIVE:
			mNegativeTitle = text;
			mNegativeMessage = msg;
			break;

		case DialogInterface.BUTTON_NEUTRAL:
			mNeutralTitle = text;
			mNeutralMessage = msg;
			break;

		default:
			throw new IllegalArgumentException("Button does not exist");
		}
	}

	private static final class ButtonHandler extends Handler {
		// 用于处理Button按钮的点击事件
		private static final int MSG_DISMISS_DIALOG = 1;
		private WeakReference<DialogInterface> dialog;

		public ButtonHandler(DialogInterface dialog) {
			this.dialog = new WeakReference<DialogInterface>(dialog);
		}

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case DialogInterface.BUTTON_POSITIVE:
			case DialogInterface.BUTTON_NEGATIVE:
			case DialogInterface.BUTTON_NEUTRAL: {
				((DialogInterface.OnClickListener) msg.obj).onClick(
						dialog.get(), msg.what);
			}
				break;
			case MSG_DISMISS_DIALOG: {
				((DialogInterface) msg.obj).dismiss();
			}
				break;
			}
		}
	}

	public static class AlertParams {

		public Context mContext;
		public View mView;
		public LayoutInflater mInflater;
		public View mCustomTitleView;
		public CharSequence mTitle, mMessage, mPositiveButtonText,
				mNegativeButtonText, mNeutralButtonText;
		public DialogInterface.OnClickListener mPositiveButtonListener,
				mNegativeButtonListener, mNeutralButtonListener,
				mOnClickListener;
		public int mIconId = 0;
		public Drawable mIcon;
		public CharSequence[] mItems;
		public ListAdapter mAdapter;
		public AdapterView.OnItemSelectedListener mOnItemSelectedListener;
		public String mLabelColumn;
		public DialogInterface.OnKeyListener mOnKeyListener;
		public boolean isAdapter;

		public AlertParams(Context context) {
			mContext = context;
			mInflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		public void apply(RewriteAlertController dialog) {
			if (mCustomTitleView != null) {
				dialog.setCustomTitle(mCustomTitleView);
			} else {
				if (mTitle != null) {
					dialog.setTitle(mTitle);
				}
				if (mIcon != null) {
					dialog.setIcon(mIcon);
				}
				if (mIconId >= 0) {
					dialog.setIcon(mIconId);
				}
			}
			if (mMessage != null) {
				dialog.setMessage(mMessage);
			}
			if (mPositiveButtonText != null) {
				dialog.setButton(DialogInterface.BUTTON_POSITIVE,
						mPositiveButtonText, mPositiveButtonListener, null);
			}
			if (mNegativeButtonText != null) {
				dialog.setButton(DialogInterface.BUTTON_NEGATIVE,
						mNegativeButtonText, mNegativeButtonListener, null);
			}
			if (mNeutralButtonText != null) {
				dialog.setButton(DialogInterface.BUTTON_NEUTRAL,
						mNeutralButtonText, mNeutralButtonListener, null);
			}
			if ((mItems != null) || (mAdapter != null)) {
				createListView(dialog);
			}
		}

		private void createListView(RewriteAlertController dialog) {
			final ListView listView = (ListView) mInflater.inflate(
					dialog.mListLayout, null);
			ListAdapter adapter;
			int layout = isAdapter ? dialog.mListItemLayout
					: dialog.mSingleChoiceItemLayout;
			adapter = (mAdapter != null) ? mAdapter
					: new ArrayAdapter<CharSequence>(mContext, layout, R.id.text1, mItems);
			dialog.mAdapter=adapter;
			dialog.mListView=listView;
		}
	}
}
