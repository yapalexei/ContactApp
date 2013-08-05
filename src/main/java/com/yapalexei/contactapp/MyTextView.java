package com.yapalexei.contactapp;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

/**
 * Created by alexeiyagolnikov on 8/4/13.
 */
public class MyTextView extends TextView {

    public MyTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public MyTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MyTextView(Context context) {
        super(context);
        init();
    }

    public void init() {
        if (!this.getRootView().isInEditMode()){
            Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/cfmidnight.ttf");
            setTypeface(tf ,1);
        }

    }

}