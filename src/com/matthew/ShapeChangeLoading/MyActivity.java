package com.matthew.ShapeChangeLoading;

import android.app.Activity;
import android.os.Bundle;
import com.matthew.ShapeChangeLoading.ui.ShapeLoadingView;

public class MyActivity extends Activity {
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
//        ShapeLoadingView view = (ShapeLoadingView) findViewById(R.id.shapeloadingview);
//        view.startAnimator();
    }
}
