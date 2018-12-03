package gsmattglobal.smsmessage;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.nio.charset.CharacterCodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import gsmattglobal.smsmessage.socket.UserFilterVO;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import io.realm.Sort;

public class FilterListActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    Realm realm;
    RealmResults<UserFilterVO> userfilterVO;
    FilterAdapter filterAdapter;
    EditText editText;
    Button addButton;
    Button delButton;
    TextView alertView;
    CheckBox checkBox;
    public ArrayList<String> checkListStr = new ArrayList<String>();
    public ArrayList<Integer> checkListInt = new ArrayList<Integer>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_list);

        editText = findViewById(R.id.filter_editText);
        addButton = findViewById(R.id.filter_add_button);
        delButton = findViewById(R.id.filter_delete_button);
        recyclerView = findViewById(R.id.filter_recyclerView);
        alertView = findViewById(R.id.filter_alertView);
        recyclerView.setHasFixedSize(true);

        addButton.setEnabled(false);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        Realm.init(this);
        realm = Realm.getDefaultInstance();
//        userfilterVO = realm.where(UserFilterVO.class).findAll();
        userfilterVO = realm.where(UserFilterVO.class).findAllSorted("time", Sort.ASCENDING);
//        smsvo = realm.where(SmsVO.class).findAllSorted("date", Sort.DESCENDING);
        filterAdapter = new FilterListActivity.FilterAdapter(userfilterVO, this);

        recyclerView.setAdapter(filterAdapter);
//        recyclerView.scrollToPosition(-1);
//        setupRealm();
        recyclerView.scrollToPosition(userfilterVO.size()-1);

        showView();

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() > 0) {
                    addButton.setEnabled(true);
                }else {
                    addButton.setEnabled(false);
                }
            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                filterAdapter.checkListBool.add(false);
//                Log.d("size " ,"size = " + filterAdapter.result.size() );
                realm.beginTransaction();
                UserFilterVO vo = realm.createObject(UserFilterVO.class);
                vo.name = editText.getText().toString();
                long now = System.currentTimeMillis();
                Date date = new Date();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmss", Locale.KOREA);
                vo.time = sdf.format(date);
                FilterVO fvo = realm.createObject(FilterVO.class);
                fvo.name = editText.getText().toString();
//                vo.isCheck = false;
//                vo.index = filterAdapter.result.size() -1 ;
                realm.commitTransaction();
                editText.setText("");
                Log.d("size " ,"R size = " + filterAdapter.result.size() + " L size = " + filterAdapter.checkListBool.size() );
                filterAdapter.notifyDataSetChanged();
                recyclerView.scrollToPosition(userfilterVO.size()-1);
                showView();
            }
        });

        delButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("delete" , " delete size = " + userfilterVO.size() );
//                for (int i=0; i<filterAdapter.checkList.size(); i++) {
//                    if ( filterAdapter.checkList.get(i) ) {
//                        filterAdapter.checkList.remove(i);
//                        Log.d("delete","delete = " + i);
//                        final RealmResults<FilterVO> fresult = realm.where(FilterVO.class).equalTo("index",i ).findAll();
//
//                        realm.executeTransaction(new Realm.Transaction() {
//                            @Override
//                            public void execute(Realm realm) {
//                                fresult.deleteAllFromRealm();
//                            }
//                        });
//
//                    }
//                }

                    for(int j = 0; j < checkListStr.size(); j++ ) {

                            Log.d("dd" ,  "  c = " + checkListStr.get(j) );
                            final RealmResults<UserFilterVO> ufresult = realm.where(UserFilterVO.class).equalTo("name", checkListStr.get(j) ).findAll();
                            final RealmResults<FilterVO> fresult = realm.where(FilterVO.class).equalTo("name", checkListStr.get(j) ).findAll();
                            realm.executeTransaction(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {

                                    ufresult.deleteAllFromRealm();
                                    fresult.deleteAllFromRealm();
                                }
                            });

                    }

//                    for (int i = 0; i < filterAdapter.checkListBool.size(); i++ ) {
//                        if (filterAdapter.checkListBool.get(i) ) {
//                            filterAdapter.checkListBool.remove(i);
//                        }
//                    }
                filterAdapter.checkListBool.clear();
                for (int i = 0 ; i < userfilterVO.size(); i++ ) {
                    filterAdapter.checkListBool.add(false);
                }
                checkListStr.clear();
//                for (int i = 0; i < filterVO.)

                filterAdapter.notifyDataSetChanged();
                recyclerView.scrollToPosition(userfilterVO.size()-1);
                showView();
//                if (checkList.size() > 0) {
//
//                    for(int i=0; i<checkList.size(); i++) {
//
//                        filterAdapter.checkList.remove(checkListInt.get(i));
//                        final RealmResults<FilterVO> result = realm.where(FilterVO.class).equalTo("name",checkList.get(i)).findAll();
//
//                        realm.executeTransaction(new Realm.Transaction() {
//                            @Override
//                            public void execute(Realm realm) {
//                                result.deleteAllFromRealm();
//                            }
//                        });
//
////                        realm.beginTransaction();
////
////
////                        realm.commitTransaction();
//                    }
//                    checkList.clear();
//                    checkListInt.clear();
//                }

            }
        });


    }

    private void showView() {

        if (userfilterVO.size() == 0) {
            alertView.setVisibility(View.VISIBLE);
        }else {
            alertView.setVisibility(View.INVISIBLE);
        }
    }

    private void setupRealm() {

        realm.addChangeListener(new RealmChangeListener<Realm>() { //데이터베이스 값이 변경되면 갱신
            @Override
            public void onChange(Realm realm) {
                Log.d("Realm", "add value");
                filterAdapter.notifyDataSetChanged();
                recyclerView.scrollToPosition(userfilterVO.size()-1);
            }
        });

//        realm.removeChangeListener(new RealmChangeListener<Realm>() {
//            @Override
//            public void onChange(Realm realm) {
////                Log.d("Realm", "delete value");
//                filterAdapter.notifyDataSetChanged();
//                recyclerView.scrollToPosition(filterVO.size()-1);
//            }
//        });
    }

    public void addList(String s) {
        checkListStr.add(s);
//        checkListInt.add(i);
        String st = "";
        for (int i = 0; i < checkListStr.size(); i ++ ) {
            st += checkListStr.get(i) + " ,";
        }
        Log.d("dd"  ," st = " + st ) ;
    }
    public void delList(String s) {

        if (checkListStr.size() > 0) {

            for(int i=0; i<checkListStr.size(); i++) {

                if (checkListStr.get(i).equals(s)) {
                    checkListStr.remove(i);
                }
//                if (checkListInt.get(i).equals(num)) {
//                    checkListInt.remove(i);
//                }

            }
            String st = "";
            for (int i = 0; i < checkListStr.size(); i ++ ) {
                st += checkListStr.get(i) + " ,";
            }
            Log.d("dd"  ," st = " + st ) ;
        }

    }


    public static class FilterAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        RealmResults<UserFilterVO> result;
        Context mContext = null;
        public boolean[] checkArr;
        public List<Boolean> checkListBool = new ArrayList<>();
        public List<Integer> indexList = new ArrayList<>();

        public static class FilterViewHolder extends RecyclerView.ViewHolder {

            TextView textView;
            CheckBox checkBox;

            public FilterViewHolder(View view) {
                super(view);
                textView = view.findViewById(R.id.filter_item_textView);
                checkBox = view.findViewById(R.id.filter_item_checkBox);
            }
        }

        FilterAdapter(RealmResults<UserFilterVO> vo, Context context) {
            this.result = vo;
            this.mContext = context;
            this.checkArr = new boolean[this.result.size()];
            for (int i=0; i<this.result.size(); i++) {
                this.checkListBool.add(false);
            }

        }


        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_filter, parent, false);
            return new FilterViewHolder(v);
        }

        int pos;
        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {


            final FilterViewHolder filterViewHolder = (FilterViewHolder) holder;
            filterViewHolder.textView.setText(result.get(position).name);
//            Log.d("dddd", "ooooooooooo ===  " + position);
//            Log.d("size" , "chList = " + checkList.size() + "  result = " + result.size() + " index = " + result.get(position).index + "");
//            Log.d("index" , " index = " + result.get(position).index + "  ch = " + checkList.get(result.get(position).index) ) ;
//            if( checkList.get(result.get(position).index -1) ) {
//                filterViewHolder.checkBox.setChecked(true);
//            }else {
//                filterViewHolder.checkBox.setChecked(false);
//            }
//             Log.d("dd " ,"bool size = " + checkListBool.size() + "  position = " + position );
            if (checkListBool.get(position) ) {

                filterViewHolder.checkBox.setChecked(true);
            }else {
                filterViewHolder.checkBox.setChecked(false);
            }
//            for (int i = 0 ; i < indexList.size(); i++ ) {
//                if (indexList.get(i) == result.get(position).index) {
//                    filterViewHolder.checkBox.setChecked(true);
//                }else {
//                    filterViewHolder.checkBox.setChecked(false);
//                }
//            }

//            if (checkArr.length <= position) {
//              if( result.get(position).isCheck ){
//                  filterViewHolder.checkBox.setChecked(true);
//              }else {
//                  filterViewHolder.checkBox.setChecked(false);
//              }
//                if( checkList.get(position)) {
//
//                    filterViewHolder.checkBox.setChecked(true);
//                }else {
//                    filterViewHolder.checkBox.setChecked(false);
//                }
//            }


            filterViewHolder.checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("indexList",  "position = " + position + "bool size = " + checkListBool.size() );
//                    for(int i=0; i<indexList.size(); i++) {
//                        if (indexList.get(i) == result.get(position).index) {
//                            indexList.remove(i);
//                        }else {
//                            indexList.add(result.get(position).index);
//                        }
//                    }
//                    Log.d("ffffffff","fffffff= " + result.get(position).index );
//                    if( checkList.get(result.get(position).index) ) {
//                        checkList.set(result.get(position).index, false);
//                    }else {
//                        checkList.set(result.get(position).index, true);
//                    }

                    if( checkListBool.get(position) ) {
                        ((FilterListActivity) mContext).delList(result.get(position).name );
                        checkListBool.set(position, false);
                    }else {
                        ((FilterListActivity) mContext).addList(result.get(position).name );
                        checkListBool.set(position, true);
                    }



//                    if (result.get(position).isCheck) {
//                        result.get(position).isCheck = false;
//                        filterViewHolder.checkBox.setChecked(false);
//                    }else {
//                        result.get(position).isCheck = true;
//                        filterViewHolder.checkBox.setChecked(true);
//                    }
//                    if (checkList.get(position) ) {
//                        ((FilterListActivity) mContext).delList(result.get(position).name, position);
//                        checkList.add(position, false);
//                    }else {
//                        ((FilterListActivity) mContext).addList(result.get(position).name, position);
//                        checkList.add(position, true);
//                    }
//                    pos = position;
                }
            });
//            for (int i = 0 ; i < indexList.size(); i++ ) {
//                if (indexList.get(i) == result.get(position).index) {
//                    filterViewHolder.checkBox.setChecked(true);
//                }else {
//                    filterViewHolder.checkBox.setChecked(false);
//                }
//            }
//            if (checkList.get(position) ) {
//                filterViewHolder.checkBox.setChecked(true);
//            }else {
//                filterViewHolder.checkBox.setChecked(false);
//            }
//            filterViewHolder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//                @Override
//                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                    Log.d("pp" , "position22 = " + position + "pp = " + pp );
//
//
//                    if (isChecked) {
//
//                        checkArr[pp] = false;
//                        Log.d("dddd", "uuuu");
//                    }else {
//                        checkArr[pp] = true;
//                        Log.d("dddd", "cccc");
//                    }
//                }
//            });

//            filterViewHolder.checkBox.setChecked(false);
//            filterViewHolder.checkBox.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//
//                    if ( ((CheckBox)v).isChecked() ) {
//                        ((FilterListActivity) mContext).addList(result.get(position).name);
//                        Log.d("size ", "size = " + ((FilterListActivity) mContext).checkList.size() + "name = " + result.get(position).name );
//                    }else {
//                        ((FilterListActivity) mContext).delList(result.get(position).name);
//                        Log.d("size ", "size22 = " + ((FilterListActivity) mContext).checkList.size() + "name = " + result.get(position).name );
//                    }
//                }
//            });

//            filterViewHolder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//                @Override
//                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//
//                    if (isChecked) {
//
////                        Log.d("check" , "position = " + position + " name = " + result.get(position).name);
////
//                        ((FilterListActivity) mContext).addList(result.get(position).name);
////                        Log.d("size", "size = " + ((FilterListActivity) mContext).checkList.size() );
//
//
//                    }else {
////                        Log.d("check" , "uncheck position = " + position + " name = " + result.get(position).name);
//                        ((FilterListActivity) mContext).delList(result.get(position).name);
//
//                    }
//                }
//            });

        }

        @Override
        public int getItemCount() {
//            Log.d("ddd","getItemCount = " + result.size() );
            return result.size();
        }


    }


}
