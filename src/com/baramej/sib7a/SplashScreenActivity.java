package com.baramej.sib7a;

import android.R.style;
import android.os.Bundle;
import android.os.Handler;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.view.Menu;
import android.view.View;
import android.webkit.WebView;

@SuppressLint("NewApi") public class SplashScreenActivity extends Activity {

	private static final long SPLASH_DURATION = 4000;
	// time
	private boolean mIsBackButtonPressed;

	@SuppressLint({ "SetJavaScriptEnabled", "ResourceAsColor" }) @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setTheme(style.Theme_Black_NoTitleBar_Fullscreen);
//		this.setTitle(R.string.title_activity_splash_screen);
		setContentView(R.layout.activity_splash_screen);
		
//		View splash = (View)inflater.inflate(R.layout.abc, null);
//		splash.setBackgroundColor(R.color.black);
//		
		WebView webView = (WebView) findViewById(R.id.webView1);
		webView.setBackgroundColor(Color.BLACK);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setAllowUniversalAccessFromFileURLs(true);
		webView.loadUrl("file:///android_asset/splashScreenOne/splashScreenOne.html");
		
		Handler handler = new Handler();

		// run a thread after 2 seconds to start the home screen
		handler.postDelayed(new Runnable() {

			@Override
			public void run() {

				// make sure we close the splash screen so the user won't come
				// back when it presses back key

				finish();

				if (!mIsBackButtonPressed) {
					// start the home screen if the back button wasn't pressed
					// already
//					Intent intent = new Intent(SplashScreen.this,Masba7aActivity.class);
//					SplashScreen.this.startActivity(intent);
				}

			}

		}, SPLASH_DURATION); // time in milliseconds (1 second = 1000
								// milliseconds) until the run() method will be
								// called

	}

	@Override
	public void onBackPressed() {

		// set the flag to true so the next activity won't start up
		mIsBackButtonPressed = true;
		super.onBackPressed();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.splash_screen, menu);
		return true;
	}

}
