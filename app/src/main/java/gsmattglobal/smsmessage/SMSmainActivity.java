package gsmattglobal.smsmessage;

import android.accessibilityservice.GestureDescription;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.provider.Telephony;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.Sort;

public class SMSmainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    Realm realm;
    MyAdapter myAdapter;
    public RealmResults<SmsVO> smsvo;

    Button filterButton;


    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recycler_activity);

        Realm.init(this);
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        realm = Realm.getDefaultInstance();
//      smsvo = realm.where(SmsVO.class).findAll();
        smsvo = realm.where(SmsVO.class).findAllSorted("date", Sort.DESCENDING);
        myAdapter = new MyAdapter(smsvo);
        recyclerView.setAdapter(myAdapter);
//        dialog = ProgressDialog.show(SMSmainActivity.this, "","잠시 기다려주세요",true);
//        query();

        setupRealm();

        filterButton = findViewById(R.id.mainActivity_filter_button);
        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(),FilterListActivity.class);
                startActivity(intent);
            }
        });

        RealmResults<FilterVO> result = realm.where(FilterVO.class).findAll();
//        Realm.getGlobalInstanceCount(Realm.getDefaultConfiguration());
        int c = Realm.getGlobalInstanceCount(realm.getDefaultConfiguration());

        Log.d("dd", " cccccccccc = " + c );
        Log.d("dd", " dd = " + result.size() );
//        for (int x = 0; x < 100; x++ ) {
//            Log.d("dd", " dd = " + result.get(x).name );
//        }
    }

//    private void query() {
//        RealmQuery<SmsVO> query = realm.where(SmsVO.class);
//        query.equalTo("msg", "가나");
//        RealmResults<SmsVO> result = query.findAll();
//
//        Log.d("sss","main onCreate " + result.size() );
//    }


    private void setupRealm() {

        realm.addChangeListener(new RealmChangeListener<Realm>() { //데이터베이스 값이 변경되면 갱신
            @Override
            public void onChange(Realm realm) {
                Log.d("Realm", "reload table");
                myAdapter.notifyDataSetChanged();
            }
        });
    }


    public static class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        public RealmResults<SmsVO> smsvo;
        Realm realm;
        public static class MyViewHolder extends RecyclerView.ViewHolder {
            TextView number;
            TextView date;
            TextView msg;
            ImageView imgView;
            MyViewHolder(View view) {
                super(view);
                number = view.findViewById(R.id.tv_number);
                date = view.findViewById(R.id.tv_date);
                msg = view.findViewById(R.id.tv_msg);
                imgView = view.findViewById(R.id.item_imageView);
            }
        }

        public MyAdapter(RealmResults<SmsVO> sms) {
            this.smsvo = sms;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_activity, parent, false);
            return new MyViewHolder(v);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            MyViewHolder myViewHolder = (MyViewHolder) holder;

            myViewHolder.number.setText(smsvo.get(position).number);
            myViewHolder.date.setText(smsvo.get(position).date);
            myViewHolder.msg.setText(smsvo.get(position).msg);

            if (smsvo.get(position).check) { //필터 결과에따라 이미지 교체
                myViewHolder.imgView.setImageResource(R.drawable.confirm);
            }else {
                myViewHolder.imgView.setImageResource(R.drawable.cancel);
            }
//
        }

        @Override
        public int getItemCount() {
            return smsvo.size();
        }

    }

//    Comparator<SmsVO> Desc = new Comparator<SmsVO>() {
//        @Override
//        public int compare(SmsVO item1, SmsVO item2) {
//            return item2.date.compareTo(item1.date);
//            // 아래와 같은 코드 /* int ret ; if (item1.getText().compareTo(item2.getText()) < 0) /
//            // / item1이 작은 경우, ret = 1 ; else if (item1.getText().compareTo(item2.getText()) == 0) ret = 0 ;
//            // else // item1이 큰 경우, ret = -1 ; return ret ; */ } } ;
//        }
//    };



}
