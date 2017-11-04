package com.live.air.task.Utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.live.air.task.R;


public enum FullScreenTransparentLoading {
    INSTANCE;

    Context mContext;
    private Dialog mLoadingDialog;
    private static boolean isLoading;

    FullScreenTransparentLoading() {}

    public void init(Context context) {
        mContext = context;

        mLoadingDialog=new Dialog(mContext, AlertDialog.THEME_HOLO_LIGHT);
        final Window window = mLoadingDialog.getWindow();
        window.requestFeature(Window.FEATURE_NO_TITLE);
        mLoadingDialog.setContentView(R.layout.fullscreenloading);
        mLoadingDialog.setCancelable(false);

        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.dimAmount = 0.4f;
        window.setAttributes(lp);
    }

    public void launch1(String msg) {
        TextView tv = (TextView) mLoadingDialog.findViewById(R.id.loading_msg);
        ProgressBar pb = (ProgressBar)mLoadingDialog.findViewById(R.id.progressBar3);
        pb.setBackgroundColor(mContext.getResources().getColor(R.color.white));
        tv.setText(msg);
        tv.setVisibility(View.VISIBLE);
        isLoading = true;
        mLoadingDialog.show();

    }

    public void launch() {
        try {
            TextView tv = (TextView) mLoadingDialog.findViewById(R.id.loading_msg);
            ProgressBar pb = (ProgressBar) mLoadingDialog.findViewById(R.id.progressBar3);
            pb.setBackgroundColor(mContext.getResources().getColor(R.color.transparent));
            tv.setText("");
            tv.setVisibility(View.GONE);
            isLoading = true;
            mLoadingDialog.show();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void dismiss() {
        try {
            isLoading = false;
            mLoadingDialog.dismiss();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public boolean isLoading(){
        return this.isLoading;
    }
}
