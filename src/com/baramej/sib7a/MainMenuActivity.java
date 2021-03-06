package com.baramej.sib7a;

import android.R.style;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.Menu;
import android.view.View;

public class MainMenuActivity extends Activity {

	// TODO set to zero to get actual sizes
	public static int SCREEN_WIDTH = 0;
	public static int SCREEN_HEIGHT = 0;
	
	private boolean isSplashStarted = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		System.out.println((int)((-5)/(-5)));
		defineScreenSize();
		setContentView(R.layout.activity_main_menu);

		// start splash screen
		isSplashStarted = true;
		this.startActivity(new Intent(MainMenuActivity.this, SplashScreenActivity.class));
		System.err.println("splash is done");
		setTheme(style.Theme_Holo);
		setTitle(R.string.title_main_menu);
		onClick_startSib7a();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is
		// present.
		getMenuInflater().inflate(R.menu.main_menu, menu);
		return true;
	}

	private void defineScreenSize() {
		if (SCREEN_WIDTH == 0 || SCREEN_HEIGHT == 0) {
			Display display = getWindowManager().getDefaultDisplay();
			Point size = new Point();
			display.getSize(size);
			SCREEN_WIDTH = size.x;
			SCREEN_HEIGHT = size.y;
			System.out.println(SCREEN_WIDTH);
			System.out.println(SCREEN_HEIGHT);
		}
	}

	public void onClick_startSib7a() {
		System.out.println("starting sib7a");
		Intent intent = new Intent(MainMenuActivity.this, Sib7aMain.class);
		this.startActivity(intent);

	}
	/*
	 * Used by the layout to start the sib7aActivity
	 */
	public void onClick_startSib7a(View v) {
		Intent intent = new Intent(MainMenuActivity.this, Sib7aMain.class);
		this.startActivity(intent);

	}

	public void onClick_startPhyiscsWorld(View v) {
		Intent intent = new Intent(MainMenuActivity.this, Sib7aPhysicsWorld.class);
		this.startActivity(intent);
	}
}