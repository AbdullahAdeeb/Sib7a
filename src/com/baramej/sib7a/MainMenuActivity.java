package com.baramej.sib7a;

import java.util.prefs.Preferences;

import android.R.style;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.view.Display;
import android.view.Menu;
import android.view.View;

public class MainMenuActivity extends Activity {

	// TODO set to zero to get actual sizes
	public static int SCREEN_WIDTH = 0;
	public static int SCREEN_HEIGHT = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		System.out.println((int)((-5)/(-5)));
		defineScreenSize();
		// start splash screen
		setContentView(R.layout.activity_main_menu);
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