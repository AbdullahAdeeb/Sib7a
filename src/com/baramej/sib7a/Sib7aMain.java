package com.baramej.sib7a;

import java.io.IOException;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.view.Display;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class Sib7aMain extends Sib7aPhysicsWorldPortrait implements OnItemSelectedListener {

	/**
	 * The serialization (saved instance state) Bundle key representing the
	 * current dropdown position.
	 */
	private Vibrator vibrator;
	public SharedPreferences usrPref = null;
	private SharedPreferences intPref;

	int sib7aIndex = 0;
	int maxCount = 33;
	int currentCount = 0;
	long[] LONG_VIBE_PATTERN = { 0, 200, 50, 200 };
	long[] SHORT_VIBE_PATTERN = { 0, 50, 50, 50 };

	Button defineSizeButton;
	SoundPool sPool = null;
	int[] soundsIds = new int[3];
	private TextView instructionsTxtView;
	private TextView countTxtView;
	private int SCREEN_WIDTH;
	private int SCREEN_HEIGHT;

	/*
	 * SUPERCLASS OVERIDE (non-Javadoc)
	 * 
	 * @see
	 * org.andengine.ui.activity.BaseGameActivity#onCreate(android.os.Bundle
	 * )
	 */

	@Override
	public void onCreateResources() {
		super.onCreateResources();
		this.startActivity(new Intent(Sib7aMain.this, SplashScreenActivity.class));

		this.instructionsTxtView = (TextView) findViewById(R.id.startInstructionsText);
		this.countTxtView = (TextView) findViewById(R.id.countTextView);
		this.defineSizeButton = (Button) findViewById(R.id.define_size_button);

		intPref = getSharedPreferences("internal_prefs", 0);

		usrPref = PreferenceManager.getDefaultSharedPreferences(getApplication());
		vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		loadSounds();

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Spinner spinner = (Spinner) findViewById(R.id.sib7a_spinner);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.counts_label_array,
				android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
		spinner.setOnItemSelectedListener(this);

	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		this.sib7aIndex = arg2;
		instructionsTxtView.setVisibility(View.VISIBLE);

		if (arg2 == 0) {
			this.maxCount = 33;
			this.defineSizeButton.setVisibility(View.INVISIBLE);
		} else if (arg2 == 1) {
			this.maxCount = 100;
			this.defineSizeButton.setVisibility(View.INVISIBLE);

		} else if (arg2 == 2) {
			
			maxCount = intPref.getInt("userDefinedCount", maxCount);
			System.out.println("\t\t>>>>>>>>> " + maxCount);

			this.defineSizeButton.setVisibility(View.VISIBLE);

		}

		if (currentCount >= maxCount) {
			incrementCount(); // to reset the current count and take
					  // care of all the left overs
		}

		System.out.println("/t/t>>>>>>>>>" + this.maxCount);

	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// no need
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == 24 || keyCode == 25) {
			onActionClick();
			return true;
		}
		return super.onKeyUp(keyCode, event);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == 24 || keyCode == 25) {
			return true;
		}
		// else if (keyCode == KeyEvent.KEYCODE_MENU) {
		// return startSettingsDialog();
		// }
		return super.onKeyDown(keyCode, event);
	}

	/*
	 * preferences menu
	 * 
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		getMenuInflater().inflate(R.menu.sib7a, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.action_settings:
			this.startSettingsDialog();
			return true;
		case R.id.action_abouts:
			Toast.makeText(getApplicationContext(), "Will be implemented soon !! :) ", Toast.LENGTH_SHORT).show();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/*
	 * ================================================================ //
	 * METHODS
	 */// ==============================================================
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

	public void onClickDefineSize(View view) {
		System.out.println("are we even here !!!! ");

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		View views = getLayoutInflater().inflate(R.layout.number_picker, null);

		final SharedPreferences.Editor intPrefEditor = intPref.edit();
		final NumberPicker picker = (NumberPicker) views.findViewById(R.id.np);

		picker.setMinValue(1);
		picker.setMaxValue(1000);
		picker.setValue(maxCount);
		builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				maxCount = picker.getValue();
				intPrefEditor.putInt("userDefinedCount", picker.getValue());
				intPrefEditor.commit();
				return;
			}
		});

		builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				return;
			}
		});

		builder.setView(views);

		final AlertDialog dialog = builder.create();

		dialog.show();
		//

		// LayoutInflater inflater = (LayoutInflater)
		// getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		// View npView =
		// getLayoutInflater().inflate(R.layout.number_picker, null);
		// new AlertDialog.Builder(this)
		// .setTitle("Define size:")
		// .setView(npView)
		// .setPositiveButton(R.string.dialog_ok,
		// new DialogInterface.OnClickListener() {
		// public void onClick(DialogInterface dialog, int whichButton)
		// {
		//
		// }
		// })
		// .setNegativeButton(R.string.dialog_cancel,
		// new DialogInterface.OnClickListener() {
		// public void onClick(DialogInterface dialog, int whichButton)
		// {
		// }
		// }).create().show();
	}

	public void onClickInfoButton(View v) {

	}

	private void startSettingsDialog() {
		Intent settingsDialongIntent = new Intent(this, SettingsDialog.class);
		this.startActivity(settingsDialongIntent);
	}

	private void onActionClick() {
		incrementCount();
		vibrate();
		playSound(0);
	}

	protected void incrementCount() {

		if (currentCount < maxCount) {
			currentCount++;
			instructionsTxtView.setVisibility(View.INVISIBLE);
			countTxtView.setText(Integer.toString(currentCount));
		} else {
			currentCount = 0;
			countTxtView.setText(Integer.toString(currentCount));
		}
	}

	/*
	 * Vibration
	 */
	private void vibrate() {
		if (!usrPref.getBoolean("isVibrate", true)) {
			return;
		}
		// first third and second third eg. 11 and 22 from 33
		if (currentCount == maxCount / 3 || currentCount == (maxCount / 3) * 2) {
			vibrator.vibrate(SHORT_VIBE_PATTERN, -1);
			// the end of the tasbee7
		} else if (currentCount == maxCount) {
			vibrator.vibrate(LONG_VIBE_PATTERN, -1);
		} else {
			// normal tasbee7
			vibrator.vibrate(25);
		}
	}

	private void loadSounds() {
		sPool = new SoundPool(2, AudioManager.FX_KEY_CLICK, 0);
		// load the sound that will be played at the beginning
		// first, to give
		// time to load
		try {
			soundsIds[2] = sPool.load(getAssets().openFd("soundFx/marbles_long.mp3"), 1);
		} catch (IOException e) {
			System.err.println("unable to load sound");
		}
		try {
			soundsIds[1] = sPool.load(getAssets().openFd("soundFx/marbles_short.mp3"), 1);
		} catch (IOException e) {
			System.err.println("unable to load sound");
		}
		try {
			soundsIds[0] = sPool.load(getAssets().openFd("soundFx/single_marble.mp3"), 2);
		} catch (IOException e) {
			System.err.println("unable to load sound");
		}

	}

	private void playSound(int index) {
		if (!usrPref.getBoolean("isSound", true)) {
			return;
		}
		AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
		float volume = (float) audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		sPool.play(soundsIds[index], volume, volume, 2, 0, 1);
	}

}
