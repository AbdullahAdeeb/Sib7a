package com.baramej.sib7a;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.text.InputFilter.LengthFilter;
import android.widget.Toast;
import android.app.Activity;

public class SettingsDialog extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings_dialog);
		
        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(R.id.prefsContainerFrame, new Prefs())
                .commit();
    
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		Toast.makeText(getApplicationContext(), R.string.settings_dialog_done, Toast.LENGTH_SHORT).show();
		
	}

}

class Prefs extends PreferenceFragment {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		addPreferencesFromResource(R.xml.user_prefs);
	}
	

}
