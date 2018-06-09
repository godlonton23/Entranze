package com.godlontonconsulting.entranze.utils;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.godlontonconsulting.entranze.R;


public class NoInternetDialog extends Dialog {

    public NoInternetDialog(Context context) {
        super(context, R.style.CustomDialog_FullView);

        setContentView(R.layout.my_snackbar);
        this.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        Window window = getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();

        setCanceledOnTouchOutside(true);

        wlp.gravity = Gravity.TOP;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);

        ((Button) findViewById(R.id.btn_dismiss)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }
}
