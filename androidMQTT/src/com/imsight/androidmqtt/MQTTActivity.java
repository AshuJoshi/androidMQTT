package com.imsight.androidmqtt;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.imsight.androidmqtt.MQTTService.LocalBinder;

public class MQTTActivity extends Activity implements OnSharedPreferenceChangeListener {
	
	private static final String TAG ="MQTT";
	private StatusUpdateReceiver statusUpdateIntentReceiver;  
	private MQTTMessageReceiver  messageIntentReceiver;
	private Intent mqttService;
	
	private	Button mStartServiceButton;
	private Button mStopServiceButton;
	private Button mPublishMsgButton;
	private EditText mPublishMsgText;
	
	private MQTTService mService;
	private boolean mBound;
	
	private SharedPreferences	mqttSettings;
	  

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mqtt);
		

		mStartServiceButton = (Button)findViewById(R.id.startsvc);
		mStopServiceButton = (Button)findViewById(R.id.stopsvc);
		mPublishMsgButton = (Button)findViewById(R.id.pubmsg);
		mPublishMsgText = (EditText)findViewById(R.id.publishMsgText);
		
		mqttService = new Intent(MQTTActivity.this, MQTTService.class);
		
		
		mStartServiceButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Log.d(TAG, "StartSvc Button Clicked");
				
				SharedPreferences settings = getSharedPreferences(MQTTService.APP_ID, 0);
				String value1 = settings.getString("broker", null);
				String value2 = settings.getString("topic", null);
				
				if (value1 == null || value2 == null) {
					Log.d(TAG, "topic or broker value null");
				} else {
			    
					Log.d(TAG, "Broker is: " + value1);
					Log.d(TAG, "Topic is: " + value2);
					
				// Intent svc = new Intent(MQTTActivity.this, MQTTService.class);  
				startService(mqttService);
				bindService(mqttService, mConnection, Context.BIND_AUTO_CREATE);
				
				}
			}
		});
		
		mStopServiceButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Log.d(TAG, "Stop Svc Button Clicked");
				unbindService(mConnection);
				stopService(mqttService);
			}
		});
		
		mPublishMsgButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Log.d(TAG, "Publish Message");
				String theMessage = mPublishMsgText.getText().toString();
				mService.publishMessageToTopic(theMessage);
				
			}
		});
		
		statusUpdateIntentReceiver = new StatusUpdateReceiver();  
	    IntentFilter intentSFilter = new IntentFilter(MQTTService.MQTT_STATUS_INTENT);  
	    registerReceiver(statusUpdateIntentReceiver, intentSFilter);  

	    messageIntentReceiver = new MQTTMessageReceiver();  
	    IntentFilter intentCFilter = new IntentFilter(MQTTService.MQTT_MSG_RECEIVED_INTENT);  
	    registerReceiver(messageIntentReceiver, intentCFilter);
	    
	    mqttSettings = PreferenceManager.getDefaultSharedPreferences(this);//get the preferences that are allowed to be given
	    mqttSettings.registerOnSharedPreferenceChangeListener(this);//set the listener to listen for changes in the preferences

	    /*
		SharedPreferences settings = getSharedPreferences(MQTTService.APP_ID, 0);  
	    SharedPreferences.Editor editor = settings.edit();  
	    editor.putString("broker", "192.168.0.128");  
	    editor.putString("topic",  "IOE-Sensor");	  
	    editor.commit();
		*/
	    
	   


	
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.mqtt, menu);
		return true;
	}
	
	//called when an option is clicked
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
	  switch(item.getItemId()){//decide which MenuItem was pressed based on its id
	  case R.id.item_prefs:
	  	startActivity(new Intent(this, PreferencesMQTTActivity.class));//start the PrefsActivity.java
	  	break;
	  }

	  return true; //to execute the event here
	}
	
	//called when the preferences are changed in any way
	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,String key) {
		
		String b1 = sharedPreferences.getString("broker", "");
		String t1 = sharedPreferences.getString("topic", "");
	
		Log.d(TAG, "Value of Broker from prefs is: " + b1);
		Log.d(TAG, "Value of Topic from prefs is: " + t1);
		
		SharedPreferences settings = getSharedPreferences(MQTTService.APP_ID, 0);  
	    SharedPreferences.Editor editor = settings.edit();  
	    editor.putString("broker", b1);  
	    editor.putString("topic",  t1);	  
	    editor.commit();

	   // txtMessage1.setText(prefs.getString("custom_message_1", ""));
	   // txtMessage2.setText(prefs.getString("custom_message_2", ""));
	}


	
	
	 public class StatusUpdateReceiver extends BroadcastReceiver  
	    {  
	        @Override   
	        public void onReceive(Context context, Intent intent)  
	        {  
	            Bundle notificationData = intent.getExtras();  
	            String newStatus = notificationData.getString(MQTTService.MQTT_STATUS_MSG);	   
	            
	            Log.d(TAG, "Inside StatusUpdateReceiver, newStatus is: " + newStatus);
	        }  
	    }  
	    public class MQTTMessageReceiver extends BroadcastReceiver  
	    {  
	        @Override   
	        public void onReceive(Context context, Intent intent)  
	        {  
	            Bundle notificationData = intent.getExtras();  
	            String newTopic = notificationData.getString(MQTTService.MQTT_MSG_RECEIVED_TOPIC);  
	            String newData  = notificationData.getString(MQTTService.MQTT_MSG_RECEIVED_MSG);	
	            
	            Log.d(TAG, "Inside MQTTMessageReceiver, newTopic is: " + newTopic);
	            Log.d(TAG, "Inside MQTTMessageReceiver, newData is: " + newData);

	        }  
	    }  
	
	    private ServiceConnection mConnection = new ServiceConnection() {

	        @Override
	        public void onServiceConnected(ComponentName className,
	                IBinder service) {
	            
	        	Log.d(TAG, "Inside onServiceConnected");
	        	// We've bound to LocalService, cast the IBinder and get LocalService instance
	            LocalBinder binder = (LocalBinder) service;
	            mService = (MQTTService)binder.getService();
	            
	            // mService = ((MQTTService.LocalBinder) service).getService();
	            
	            mBound = true;
	        }

	        @Override
	        public void onServiceDisconnected(ComponentName arg0) {
	            mBound = false;
	        }
	    };
	    

	    
}
