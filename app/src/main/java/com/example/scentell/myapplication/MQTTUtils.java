package com.example.scentell.myapplication;

/**
 * Created by scentell on 11/01/2017.
 */


import android.util.Log;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import static android.content.ContentValues.TAG;

public class MQTTUtils {

    private MqttClient client;

    public MqttClient getClient() {
        return client;
    }


    private String _clientName;
    private String _url;
    private int _port;

    public MQTTUtils(String clientName, String url, int port){
        _clientName = clientName;
        _url = url;
        _port = port;

    }

    public boolean connect() {
        try {
            MemoryPersistence persistance = new MemoryPersistence();
            client = new MqttClient("tcp://" + _url + ":" + _port, _clientName, persistance);
            client.connect();

            return true;
        } catch (MqttException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void disconnect()
    {
        if(!client.isConnected()) {
            try {
            client.disconnect();
            } catch (MqttPersistenceException e) {
                e.printStackTrace();
            }
             catch (MqttException e) {
                e.printStackTrace();
             }
        }
    }

    public boolean pub(String topic, String payload) {
        MqttMessage message = new MqttMessage(payload.getBytes());
        while(!client.isConnected()){
            Log.v(TAG, "Trying to reconnect");
            connect();

        }
        try {
            client.publish(topic, message);
            return true;
        } catch (MqttPersistenceException e) {
            e.printStackTrace();
        } catch (MqttException e) {
            e.printStackTrace();
        }
        return false;
    }

}
