package gsmattglobal.smsmessage;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;

public class SplashActivity extends AppCompatActivity {

    ProgressBar progressBar;
    HandlerThread ht;
    SharedPreferences pre;
    SharedPreferences.Editor editor;
    Boolean check = true;
    Handler handler;
    final static String foldername = Environment.getExternalStorageDirectory().getAbsolutePath()+"/TestLog";
    final static String filename = "logfile.txt";
    Realm realm;
    List<String> readList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        progressBar = findViewById(R.id.splashActivity_progressBar);

        pre = getSharedPreferences("SMS",0);
        editor = pre.edit();

        Realm.init(this);
        realm = Realm.getDefaultInstance();
        String ff = pre.getString("first","first");
        if ( ff == "first" ) {
            readData();
        }else {
            Intent intent = new Intent(getApplicationContext(),SMSmainActivity.class);
            startActivity(intent);
            finish();
        }

//        if (ff == "first") {
//            Log.d("FFF",">>>>>>>First");
//
//            ht = new HandlerThread("ht");
//            ht.start();
//            handler = new Handler(ht.getLooper() ) ;
//            handler.post(new Runnable() {
//                @Override
//                public void run() {
//
//                    progressBar.setVisibility(View.VISIBLE);
//                    readData();
////                    while (true) {
////                        if (check == false) {
////                            ht.quit();
////                            Intent intent = new Intent(getApplicationContext(),SMSmainActivity.class);
////                            startActivity(intent);
////                            finish();
////                            break;
////                        }
////                    }
//
//                }
//            });
//        }else {
//
//            Intent intent = new Intent(getApplicationContext(),SMSmainActivity.class);
//            startActivity(intent);
//            finish();
//        }

//        new DownloadOrders().execute();


    }






    private void inputData() {

        String data = null;

//        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();


        final int inx= 0;
        try {
            InputStream inputStream = getResources().openRawResource(R.raw.filter);
            final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String result="";
            String line="";
//            i = inputStream.read();

            while ( (line=reader.readLine()) != null ) {
                String[] s = line.split("=");
                String ss = s[1];
                String mm = "";
                for(int j=0; j<ss.length(); j++) {
                    if (ss.charAt(j) != '"' && ss.charAt(j) != ';' && ss.charAt(j) != ' ') {
                        mm += ss.charAt(j);
                    }
                }


//                rr.copyFromRealm(realm.createObject(FilterVO.class) );
//                final String finalMm = mm;
                final String finalMm = mm;
                final Realm rr = Realm.getDefaultInstance();
                rr.executeTransactionAsync(new Realm.Transaction() {
                    @Override
                    public void execute(Realm Erealm) {
                        FilterVO vo = Erealm.createObject(FilterVO.class);
                        vo.name = finalMm.trim();
//                        FilterVO vv = new FilterVO();
////                        Log.d("D" ,"dddddd = " + finalMm );
//                        vv.name = finalMm.trim();
//                        Erealm.copyFromRealm(vv);
//                        realm.copyFromRealm(vv);
//                        realm.commitTransaction();
//                        rr.beginTransaction();
//                        FilterVO vo = Erealm.createObject(FilterVO.class) ;
//                        vo.name = finalMm.trim();
//                        realm.commitTransaction();
////                        FilterVO vo = realm.copyFromRealm(realm.createObject(FilterVO.class) );
//                      Log.d("mm", "mm = " + finalMm );

                    }
                }, new Realm.Transaction.OnSuccess() {
                    @Override
                    public void onSuccess() {
                        Log.d("d", "SSSSSSSSSSSSSSSSSSS");
                        rr.close();
                    }
                }, new Realm.Transaction.OnError() {
                    @Override
                    public void onError(Throwable error) {
                        Log.d("d", "FFFFFFFFFFFFFFFFFFFF");
                    }
                });
//                realm.beginTransaction();
//                FilterVO vo = realm.copyFromRealm(realm.createObject(FilterVO.class) );
////                Log.d("mm", "mm = " + mm );
//                vo.name = mm.trim();
////                vo.index = inx;
////                vo.isCheck = false;
////                inx ++;
//                realm.commitTransaction();

                try{
                    File dir = new File (foldername);
                    //디렉토리 폴더가 없으면 생성함
                    if(!dir.exists()){
                        dir.mkdir();
                    }
                    //파일 output stream 생성
                    FileOutputStream fos = new FileOutputStream(foldername+"/"+filename, true);
                    //파일쓰기
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fos));
                    writer.write( mm.trim() );
                    writer.newLine();
                    writer.flush();

                    writer.close();
                    fos.close();
                }catch (IOException e){
                    e.printStackTrace();
                }
//                result += mm + '\n';
//                Log.d("st", "=" + result );
//                byteArrayOutputStream.write(i);
//                i = inputStream.read();
            }
            reader.close();
//            data = new String(byteArrayOutputStream.toByteArray(), "MS949");
            inputStream.close();
        }catch (IOException e) {
            e.printStackTrace();
        }

        editor.putString("first","second");
        editor.commit();
        check = false;

    }

    private void readData() {

        String line = "";
        try {

            InputStream inputStream = getResources().openRawResource(R.raw.filter);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream) );
            while ( (line=reader.readLine()) != null ) {
                String[] s = line.split("=");
                String ss = s[1];
                String mm = "";
                for(int j=0; j < ss.length(); j++) {
                    if (ss.charAt(j) != '"' && ss.charAt(j) != ';' && ss.charAt(j) != ' ') {
                        mm += ss.charAt(j);
                    }
                }
                readList.add( mm.trim() );
            }
        }catch (IOException e) {
            e.printStackTrace();
        }

        inputRealm2();

    }

    private void inputRealm() {

//        Realm realm = Realm.getDefaultInstance();

        // Realm을 사용합니다.
//                realm.createAllFromJson(Order.class, api.getNewOrders());
//                Order firstOrder = realm.where(Order.class).findFirst();
//                long orderId = firstOrder.getId(); // Id of order
//                return orderId;
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm Erealm) {

                for (int i = 0; i < readList.size(); i++ ) {
                    FilterVO vo = Erealm.createObject(FilterVO.class);
                    vo.name = readList.get(i);
                }
            }
        });

        editor.putString("first","second");
        editor.commit();
        check = false;

        Intent intent = new Intent(getApplicationContext(),SMSmainActivity.class);
        startActivity(intent);
        finish();

    }

    private void inputRealm2() {

//        Realm realm = Realm.getDefaultInstance();

        // Realm을 사용합니다.
//                realm.createAllFromJson(Order.class, api.getNewOrders());
//                Order firstOrder = realm.where(Order.class).findFirst();
//                long orderId = firstOrder.getId(); // Id of order
//                return orderId;
        realm.beginTransaction();
        for (int i = 0; i < readList.size(); i++ ) {
            FilterVO vo = realm.createObject(FilterVO.class);
            vo.name = readList.get(i);
        }
        realm.commitTransaction();

        editor.putString("first","second");
        editor.commit();
        check = false;

        Intent intent = new Intent(getApplicationContext(),SMSmainActivity.class);
        startActivity(intent);
        finish();

    }



}
