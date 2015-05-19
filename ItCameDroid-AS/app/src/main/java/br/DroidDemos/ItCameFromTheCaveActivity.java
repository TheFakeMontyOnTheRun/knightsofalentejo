package br.DroidDemos;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;

public class ItCameFromTheCaveActivity extends Activity {
	
	ItCameView view;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view=new ItCameView(this);
		setContentView(view);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
    	return view.onKeyDown(keyCode, event);
    }
    
    @Override
    protected void onPause() {
    	view.playing = false;
    	super.onPause();
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
    	return view.onKeyUp(keyCode, event);
    }
    
//    @Override
//    protected void onPause() {
//    	
//    	view.playing = false;
//    	super.onPause();
//    }
//    
//    @Override
//    protected void onResume() {
//    	view.playing = true;
//    	super.onResume();
//    }
    
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
    	
    	view.playing = hasFocus;
    	
    	super.onWindowFocusChanged(hasFocus);
    }
    
}