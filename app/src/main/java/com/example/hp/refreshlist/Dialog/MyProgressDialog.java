package com.example.hp.refreshlist.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.hp.refreshlist.R;

/**
 * Created by hp on 2016/3/29.
 */
public class MyProgressDialog extends Dialog {

    private LoadingDialogView loadingDialogView;
    private TextView tv;
    private AnimationDrawable animationDrawable;
    private boolean cancelable=true;


    public MyProgressDialog(Context context) {
        super(context, R.style.MyDialog_style);
        init();
    }

   private void init(){
       View contentView=View.inflate(getContext(),R.layout.loading_view,null);
         if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.KITKAT){
             Window window=getWindow();
             WindowManager.LayoutParams params=window.getAttributes();
             params.flags|=WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
             window.setAttributes(params);
         }
       setContentView(contentView);
       setCanceledOnTouchOutside(true);
       contentView.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               if(cancelable){

                   dismiss();
               }
           }
       });
       loadingDialogView=(LoadingDialogView)findViewById(R.id.loadview);
       loadingDialogView.setBackgroundResource(R.drawable.loading);
       animationDrawable=(AnimationDrawable)loadingDialogView.getBackground();
       tv = (TextView) findViewById(R.id.tv);
       getWindow().setWindowAnimations(R.anim.alpha_in);
   }

    @Override
    public void show()
    {
        animationDrawable.start();
        super.show();
    }

    @Override
    public void dismiss() {
        animationDrawable.stop();
        super.dismiss();
    }

    @Override
    public void setCancelable(boolean cancelable) {
        this.cancelable = cancelable;
        super.setCancelable(cancelable);
    }

    @Override
    public void setTitle(CharSequence title) {
        tv.setText(title);
    }

    @Override
    public void setTitle(int titleId) {
        setTitle(getContext().getString(titleId));
    }
    public void setMessage(String title) {
        tv.setText(title);
    }

}
