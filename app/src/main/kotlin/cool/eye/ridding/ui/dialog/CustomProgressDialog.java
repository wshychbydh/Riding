
/**************************************************************************************
 * [Project]
 * MyProgressDialog
 * [Package]
 * com.lxd.widgets
 * [FileName]
 * CustomProgressDialog.java
 * [Copyright]
 * Copyright 2012 LXD All Rights Reserved.
 * [History]
 * Version          Date              Author                        Record
 * --------------------------------------------------------------------------------------
 * 1.0.0           2012-4-27         lxd (rohsuton@gmail.com)        Create
 **************************************************************************************/

package cool.eye.ridding.ui.dialog;


import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.TextView;

import cool.eye.ridding.R;


public class CustomProgressDialog extends Dialog {
    private static CustomProgressDialog customProgressDialog = null;

    public CustomProgressDialog(Context context, int theme) {
        super(context, theme);
    }

    public static CustomProgressDialog createDialog(Context context) {
        customProgressDialog = new CustomProgressDialog(context, R.style.CustomProgressDialog);
        customProgressDialog.setContentView(R.layout.customprogressdialog);
        customProgressDialog.getWindow().getAttributes().gravity = Gravity.CENTER;

        return customProgressDialog;
    }

    public void onWindowFocusChanged(boolean hasFocus) {

        if (customProgressDialog == null) {
            return;
        }

        ImageView imageView = (ImageView) customProgressDialog.findViewById(R.id.loadingImageView);
        AnimationDrawable animationDrawable = (AnimationDrawable) imageView.getBackground();
        animationDrawable.start();
    }


    public CustomProgressDialog setMessage(String strMessage) {
        TextView tvMsg = (TextView) customProgressDialog.findViewById(R.id.id_tv_loadingmsg);

        if (tvMsg != null) {
            tvMsg.setText(strMessage);
        }

        return customProgressDialog;
    }
}
