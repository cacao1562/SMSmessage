package gsmattglobal.smsmessage.socket;

/**
 * Created by tj.kang on 2017-06-08.
 */

public interface ISocket {

    /**연결시켜준다.*/
    void onConnect();

    /**연결 끊어준다. 생명주기에 따라 넣어야 한다.*/
    void onDisconnect();

    /**데이터가 들어왔을때 콜백이 들어오게 된다.*/
    void onData(String onName, Object obj);

}
