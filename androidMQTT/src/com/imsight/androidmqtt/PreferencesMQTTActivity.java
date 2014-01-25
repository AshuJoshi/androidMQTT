package com.imsight.androidmqtt;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class PreferencesMQTTActivity extends PreferenceActivity {
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);//instead of setContentView()
	}

}
