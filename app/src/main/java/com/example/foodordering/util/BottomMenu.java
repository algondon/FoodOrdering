package com.example.foodordering.util;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.PopupWindow;

import com.example.foodordering.R;

public class BottomMenu implements OnClickListener,OnTouchListener{
 
	private PopupWindow popupWindow;
    private Button btn_wxLogin,btn_msgCheckLogin,btnCancel;
    private View mMenuView;
    private Activity mContext;
    private OnClickListener clickListener;
 
    public BottomMenu(Activity context,OnClickListener clickListener) {
        LayoutInflater inflater = LayoutInflater.from(context);
        this.clickListener=clickListener;
        mContext=context;
        mMenuView = inflater.inflate(R.layout.layout_popwindow, null);
        btn_wxLogin = (Button) mMenuView.findViewById(R.id.btn_wxLogin);
        btn_msgCheckLogin = (Button) mMenuView.findViewById(R.id.btn_msgCheckLogin);
        btnCancel = (Button) mMenuView.findViewById(R.id.btn_cancel);
        btnCancel.setOnClickListener(this);
        btn_wxLogin.setOnClickListener(this);
        btn_msgCheckLogin.setOnClickListener(this);
        popupWindow=new PopupWindow(mMenuView,LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT,true);
        popupWindow.setAnimationStyle(R.style.popwin_anim_style);
        ColorDrawable dw = new ColorDrawable(context.getResources().getColor(R.color.white));
        popupWindow.setBackgroundDrawable(dw);
        mMenuView.setOnTouchListener(this);
    }

    public void show(){
    	View rootView=((ViewGroup)mContext.findViewById(android.R.id.content)).getChildAt(0);
        popupWindow.showAtLocation(rootView, Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0); 
    }
    
	@Override
	public void onClick(View view) {
		popupWindow.dismiss();
		switch (view.getId()) {
		case R.id.btn_cancel:
			break;
		default:
			clickListener.onClick(view);
			break;
		}
	}

	@Override
	public boolean onTouch(View arg0, MotionEvent event) {
		int height = mMenuView.findViewById(R.id.pop_layout).getTop();
        int y=(int) event.getY();
        if(event.getAction()==MotionEvent.ACTION_UP){
            if(y<height){
            	popupWindow. dismiss();
            }
        }
        return true;
	}
 
}
