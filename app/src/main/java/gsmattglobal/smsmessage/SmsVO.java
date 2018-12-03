package gsmattglobal.smsmessage;

import io.realm.RealmObject;

/**
 * Created by ABC on 2018-10-08.
 */

public class SmsVO extends RealmObject {

    public String number;
    public String date;
    public String msg;
    public boolean check;

}
