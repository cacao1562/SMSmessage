package gsmattglobal.smsmessage.socket;


import android.graphics.Point;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/**
 * Created by tj.kang on 2017-06-08.
 * 끊어줘야 하는 상황에서 꼭 끊어줘야 한다.
 * 테스트가 안된 코드이므로 꼭 테스트를 해야한다.
 */

public class SocketManager {

    /**
     * 소켓을 연결할 URL 주소
     */                                         //http://interaction.g-smattglobal.kr:50050
//    public final static String URL = "http://dev.g-smatt.com:3004"; //Happo-en Business events 2017
    public final static String URL = "http://interaction.g-smattglobal.kr:3014"; //Happo-en Business events 2017

    /**
     * 다중기기 지원을 위한 기기 구별 id
     */
    private String device_id = "android_test_id";

    /**
     * 방진입하는 방법으로 바뀌었다. 그래서 각 지역마다 방 이름이 세팅되어 있음.
     * */
    private String mRoomNum = "sms";

    /**
     * 소켓 IO 용 객체
     */
    private Socket mSocket;

    /**
     * 소켓 관련 콜백을 받기 위한 인터페이스
     */
    private ISocket mISocket;

    /**
     * 싱글톤 사용하기 위한 객체
     */
    private static class Singleton {
        private static final SocketManager instance = new SocketManager();
    }

    /**
     * 인스턴스를 얻어옴
     */
    public static SocketManager getInstance() {
        return Singleton.instance;
    }

    /**
     * 생성자 만든다
     */
    private SocketManager() {

        initConnection();
        connect();

    }

    /**
     * 콜백 관련 리스너를 넣어준다.
     */
    public void addListener(ISocket iSocket) {

        this.mISocket = iSocket;

    }

    /**
     * 소켓을 실제로 연결하지는 않고 초기 값을 넣어준다, 실제 연결시에는 connect를 꼭 넣어줘야 한다. On값을 꼭 체크해봐야한다. 각 앱마다 다르다.
     */
    protected void initConnection() {

        mSocket = IO.socket(URI.create(URL));

        mSocket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {

            @Override
            public void call(Object... args) {
                // 커넥션 성공.
                Log.d("socket" , " Socket connected ");
                if (mISocket != null) {

                    mISocket.onConnect();

                }

                emitJoinRoom(mRoomNum);

            }

        }).on("event", new Emitter.Listener() {

            @Override
            public void call(Object... args) {

                // event 값을 통해서 받았을 때.

                if (mISocket != null) {

                    mISocket.onData("event", args);

                }

            }

        }).on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {

            @Override
            public void call(Object... args) {
                // 끊겼을때.

                if (mISocket != null) {

                    mISocket.onDisconnect();

                }

            }

        });

    }

    /**
     * 소켓 연결 시켜준다.
     */
    public void connect() {

        if (mSocket.connected() == false) {

            mSocket.connect();

        }

    }

    /**
     * 스트링값을 보내준다.
     */
//    public void sendString(String val){
//
//        mSocket.send(val);
//
//    }
    public void emitSms( String date, String number, String message) {

        JSONObject jo = new JSONObject();
        try {
            jo.put("date", date);
            jo.put("number", number);
            jo.put("msg", message);
        }catch (JSONException e) {
            e.printStackTrace();
        }
        mSocket.emit("send", jo);

    }

    public void emitSend(String type, float x, float y, String color, int weight) {

        JSONObject jo = new JSONObject();
        try {
            jo.put("id", device_id);
            jo.put("type", type);
            jo.put("x", x);
            jo.put("y", y);
            jo.put("color", color);
            jo.put("weight", weight);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mSocket.emit("send", jo);
    }

    public void emitDelete(String type) {

        JSONObject jo = new JSONObject();
        try {
            jo.put("id", device_id);
            jo.put("type", type);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mSocket.emit("delete", jo);
    }

    public void emitStopapp() {

        JSONObject jo = new JSONObject();
        try {
            jo.put("id", device_id);
            jo.put("state", true);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mSocket.emit("stopapp", jo);
    }

    public void emitJoinRoom(String roomNum){

        mSocket.emit("joinRoom", roomNum);

    }

    public void emitLeaveRoom(String roomNum){

        mSocket.emit("leaveRoom", roomNum);

    }

    /**
     * 소켓을 끊어준다. 꼭 넣어줘야 함!!!!!!!!!
     */
    public void disconnect() {

        if (mSocket != null && mSocket.connected()) {

            mSocket.disconnect();

        }

    }

    public void setUserId( String deviceId ){

        this.device_id = deviceId;

    }

    public void setRoomNum( String roomNum ){

        this.mRoomNum = roomNum;

    }


    public void emitShung(int type, Point sPoint, android.graphics.Point ePoint, int cWidth, int cHeight, int motionSpeed) {

        int PACKET_ID = 2;
        JSONObject resultObj = new JSONObject();
        try {
            JSONObject startObj = new JSONObject();
            startObj.put("x", sPoint.x);
            startObj.put("y", sPoint.y);

            JSONObject endObj = new JSONObject();
            endObj.put("x", ePoint.x);
            endObj.put("y", ePoint.y);

            JSONObject sizeObj = new JSONObject();
            sizeObj.put("x", cWidth);
            sizeObj.put("y", cHeight);

            resultObj.put("PacketID", PACKET_ID);
            resultObj.put("Type", type);
            resultObj.put("Position", startObj);
            resultObj.put("Heading", endObj);
            resultObj.put("Size", sizeObj);
            resultObj.put("Velocity", motionSpeed);
        } catch(Exception e) {
            Log.e("jsonParse", e.toString());
        }

        mSocket.emit("send", resultObj);
        Log.d("shung","~~~S h u n g ~~");
    }

}
