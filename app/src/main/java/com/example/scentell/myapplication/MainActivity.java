package com.example.scentell.myapplication;


import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.media.AudioManager;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.TextView;


import static android.content.ContentValues.TAG;
import static com.example.scentell.myapplication.R.id.textView;




// in onCreate put


class SettingsContentObserver extends ContentObserver {

    int previousVolume;
    Context context;
    private MQTTUtils mqttClient;

    public SettingsContentObserver(Handler handler) {
        super(handler);
    }

    public SettingsContentObserver(Context c, Handler handler, MQTTUtils mqttClient) {
        super(handler);
        context=c;

        AudioManager audio = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        previousVolume = audio.getStreamVolume(AudioManager.STREAM_MUSIC);
      //  Log.v(TAG, "init volume : ");mqttClient


    }

    @Override
    public boolean deliverSelfNotifications() {
        return super.deliverSelfNotifications();
    }

    @Override
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);
        Log.v(TAG, "Settings change detected");


        AudioManager audio = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        int currentVolume = audio.getStreamVolume(AudioManager.STREAM_MUSIC);

        int delta=previousVolume-currentVolume;

        if(delta>0)
        {
            Log.v(TAG, "Decreased");
            previousVolume=currentVolume;
            //myMqtt.pub("ECS/state", "2");
        }
        else if(delta<0)
        {
            Log.v(TAG, "Increased");
            previousVolume=currentVolume;
            //myMqtt.pub("ECS/state", "1");
        }




    }


}


class MediaButtonReceiver extends BroadcastReceiver {
    public MediaButtonReceiver() {
        super();
    }

    @Override
    public void onReceive(Context context, Intent intent) {


        String intentAction = intent.getAction();
        if (!Intent.ACTION_BATTERY_LOW.equals(intentAction)) {
            return;
        }

       //     MQTTUtils.pub("ECS/state", "2");
    }
}
public class MainActivity extends AppCompatActivity {

    TextView myTextView;
    //  in your activity.

    SettingsContentObserver mSettingsContentObserver ;

    String server = "77.146.227.62";
    String clientName = "IPTV_APP";
    int port = 1357;
    MQTTUtils myMqtt = new MQTTUtils(clientName, server, port);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myTextView = (TextView) findViewById(R.id.textView);
        IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_LOW);
        MediaButtonReceiver r = new MediaButtonReceiver();
        registerReceiver(r, filter);

        mSettingsContentObserver = new SettingsContentObserver(this, new Handler(), myMqtt);
        getApplicationContext().getContentResolver().registerContentObserver(android.provider.Settings.System.CONTENT_URI, true, mSettingsContentObserver );




        boolean result =  myMqtt.connect();

        if(result) {

            myTextView.setText("Connection established");
//            myMqtt.pub("ECS/state", "1");


        }
        else{
            myTextView.setText("Connection failed");
        }
    }

    @Override
    public void onPause()
    {
        Log.v(TAG, "Pause detected");
        myMqtt.pub("ECS/state", "4");
        super.onPause();
    }
    @Override
    public void onResume()
    {
        Log.v(TAG, "Pause detected");
        myMqtt.pub("ECS/state", "5");
        super.onResume();
    }
    @Override
    public void onStop()
    {
        Log.v(TAG, "Stop detected");
        myMqtt.disconnect();
        super.onStop();
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        //Send your own custom broadcast message
        Log.v(TAG, "Keypress detected");
        myMqtt.pub("MEDIA/ATV/KeyPress", String.valueOf(keyCode));

        switch(keyCode){
            case KeyEvent.KEYCODE_VOLUME_UP:
                myMqtt.pub("MEDIA/AVR/Command", "VOL_UP");
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                myMqtt.pub("MEDIA/AVR/Command", "VOL_DOWN");
            case KeyEvent.KEYCODE_DPAD_CENTER:
                Log.v(TAG, "SELECT");
                event.startTracking();
                return true;
            case KeyEvent.KEYCODE_DPAD_UP:

            case KeyEvent.KEYCODE_DPAD_DOWN:
            case KeyEvent.KEYCODE_DPAD_LEFT:
            case KeyEvent.KEYCODE_DPAD_RIGHT:


        }

        //Directoy return to avoid key event to be catched by system.
        //(Home button is not identified as a key press, so exiting from teh app is still OK
        return true;
    }

    private boolean _handledMenuButton=false;

    @Override
    public boolean onKeyUp(final int keyCode,final KeyEvent event) {
        switch(keyCode) {
            case KeyEvent.KEYCODE_DPAD_CENTER:
                Log.v(TAG, "UP!");
                return true;
        }
        return super.onKeyUp(keyCode,event);
    }

    @Override
    public boolean onKeyLongPress(final int keyCode, final KeyEvent event) {
        switch(keyCode) {
            case KeyEvent.KEYCODE_DPAD_CENTER:
                Log.v(TAG, "LONG!!!!!!!!!!!!!!!");
                _handledMenuButton=true;
                return true;
        }
        return super.onKeyLongPress(keyCode,event);
    }





}
