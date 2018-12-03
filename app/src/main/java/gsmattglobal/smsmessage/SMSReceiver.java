package gsmattglobal.smsmessage;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.BufferUnderflowException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import gsmattglobal.smsmessage.socket.SocketManager;
import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmResults;

public class SMSReceiver extends BroadcastReceiver {

    private Realm realm;
    private boolean check;
    boolean filter;

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.

//        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
//
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                context.startForegroundService(new Intent(context, MyService.class));
//            }else {
//                context.startService(new Intent(context, MyService.class));
//            }
//
//        }
        if (Telephony.Sms.Intents.SMS_RECEIVED_ACTION.equals(intent.getAction()) ) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

                Realm.init(context);
                realm = Realm.getDefaultInstance();
                String message = "";
                String address = "";
                String time = "";

                for (SmsMessage smsMessage : Telephony.Sms.Intents.getMessagesFromIntent(intent) ){

                    message = smsMessage.getMessageBody(); //메세지 내용
                    address = smsMessage.getOriginatingAddress(); //전화번호
                    Date curDate = new Date(smsMessage.getTimestampMillis());
                    SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyy년 MM월 dd일 HH시 mm분 ss초", Locale.KOREA);
                    time = mDateFormat.format(curDate);  //시간
                    Log.d("sms" , " message = " + message );
                    Log.d("sms" , " address = " + address );
                    Log.d("sms" , "   time  = " + time );

                }

                RealmResults<FilterVO> result = realm.where(FilterVO.class).findAll();
                Log.d("log","filter vo size = " + result.size() );

                for (int i = 0; i < result.size(); i++) {

                    String n = result.get(i).name;

                    if (n != null) {

                        if ( message.contains(n) ) {
                            Log.d("mm", "////////////필터 = " + message);
                            filter = false;
                            break;
                        }else {
                            Log.d("mm", "no filtering");
                            filter = true;
                        }

                    }

                }
                if (address.substring(0,3).equals("010") && !(address.substring(0,7).equals("[Web발신]")) && filter) {

                    SocketManager.getInstance().emitSms(time, address, message);
                    check = true;

                }else {
//                Log.d("dd" , "number = " + origNumber.substring(0,3) + " filter = " + filter );
                    check = false;
                }

                realm.beginTransaction(); //데이터베이스에 쓰기

                SmsVO smsVO = realm.createObject(SmsVO.class);
                smsVO.number = address;
                smsVO.date = time;
                smsVO.msg = message;
                smsVO.check = check;

                realm.commitTransaction();

            }

        }
//        if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED") ) {
//
//            if (context == null) {
//                Log.d("cc", "context = null ");
//            }else {
//                Log.d("cc", "context = not null ");
//            }
//            Realm.init(context);
//            realm = Realm.getDefaultInstance();
//
//            Log.d("Date", "SMS _ RECEIVED" );
////            context.startService(new Intent(context, MyService.class));
//            Bundle bundle = intent.getExtras();
//            Object messages[] = (Object[])bundle.get("pdus");
//            SmsMessage smsMessage[] = new SmsMessage[messages.length];
//
//            for (int i = 0; i < messages.length; i++) {
//                smsMessage[i] = SmsMessage.createFromPdu((byte[])messages[i]);
//            }
//
//            Date curDate = new Date(smsMessage[0].getTimestampMillis());
//            SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyy년 MM월 dd일 HH시 mm분 ss초", Locale.KOREA);
////            SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyyMMddHHmmss", Locale.KOREA);
//
//            String originDate = mDateFormat.format(curDate);
//            String origNumber = smsMessage[0].getOriginatingAddress();
//            String Message = smsMessage[0].getMessageBody().toString();
//
//            Log.d("Date", "Date = " + originDate );
//            Log.d("Number", "Number = " + origNumber );
//            Log.d("Message", "Message = " + Message );
//
//
//
//            RealmResults<FilterVO> result = realm.where(FilterVO.class).findAll();
//            Log.d("log","filter vo size = " + result.size() );
//
//            for (int i = 0; i < result.size(); i++) {
//                String n = result.get(i).name;
//
//                if (n != null) {
//
//                    if ( Message.contains(n) ) {
//                        Log.d("mm", "////////////필터 = " + Message);
//                        filter = false;
//                        break;
//                    }else {
//                        Log.d("mm", "no filtering");
//                        filter = true;
//                    }
//                }
//
////                int inx = Message.indexOf(n);
////                if (inx != -1) {
////                    filter = false;
////                    check = false;
////                    break;
////                }else if( inx == -1) {
////                    filter = true;
////                    check = true;
////                }
//            }
////            Log.d("dd" , "number TF= " + origNumber.substring(0,3).equals("010") + " filter = " + filter );
//            if (origNumber.substring(0,3).equals("010") && filter) {
//                SocketManager.getInstance().emitSms(originDate, origNumber, Message);
//                check = true;
//            }else {
////                Log.d("dd" , "number = " + origNumber.substring(0,3) + " filter = " + filter );
//                check = false;
//            }
//
//
//
//            realm.beginTransaction();
//
//            SmsVO smsVO = realm.createObject(SmsVO.class);
//            smsVO.number = origNumber;
//            smsVO.date = originDate;
//            smsVO.msg = Message;
//            smsVO.check = check;
//
//            realm.commitTransaction();
//
//        }
//        throw new UnsupportedOperationException("Not yet implemented");
    }



}



//        String ss = "";
//                    for (int i = 0; i < message.length(); i++) {
//        ss += String.format("U+%04X ", message.codePointAt(i));
//        }
//        Log.d("sms" , "   /////////////////////////  = " + ss );
//        String zz = "";
//        try {
//        zz = URLEncoder.encode(message,"UTF-8");
//        } catch (UnsupportedEncodingException e) {
//        zz = message;
//        }
//        String xx = "";
//        try {
//
//        xx = URLDecoder.decode(zz, "UTF-8");
//        } catch (UnsupportedEncodingException e) {
//        xx = zz;
//        }
//        Log.d("sms" , "   33333333333333333333333  = " + xx );