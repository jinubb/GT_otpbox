package com.example.otpbox_android;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.DisconnectedBufferOptions;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private MqttAndroidClient mqttAndroidClient;
    private TextView tv_pw_501;
    private TextView tv_box_501;
    private TextView tv_pw_502;
    private TextView tv_box_502;
    private TextView tv_pw_503;
    private TextView tv_box_503;
    private Button btn_sendrasp_501;
    private Button btn_sendrasp_502;
    private Button btn_sendrasp_503;
    private String password;
    private String boxnumber;
    private String address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv_box_501 = (TextView)findViewById(R.id.tv_box_501);
        tv_pw_501 = (TextView)findViewById(R.id.tv_pw_501);
        tv_box_502 = (TextView)findViewById(R.id.tv_box_502);
        tv_pw_502 = (TextView)findViewById(R.id.tv_pw_502);
        tv_box_503 = (TextView)findViewById(R.id.tv_box_503);
        tv_pw_503 = (TextView)findViewById(R.id.tv_pw_503);
        btn_sendrasp_501 = (Button)findViewById(R.id.btn_501);
        btn_sendrasp_502 = (Button)findViewById(R.id.btn_502);
        btn_sendrasp_503 = (Button)findViewById(R.id.btn_503);
        mqttAndroidClient = new MqttAndroidClient(this,  "tcp://" + "211.197.225.191" + ":1883", MqttClient.generateClientId());

        // 2번째 파라메터 : 브로커의 ip 주소 , 3번째 파라메터 : client 의 id를 지정함 여기서는 paho 의 자동으로 id를 만들어주는것

        try {

            IMqttToken token = mqttAndroidClient.connect(getMqttConnectionOption());    //mqtttoken 이라는것을 만들어 connect option을 달아줌
            token.setActionCallback(new IMqttActionListener() {

                @Override

                public void onSuccess(IMqttToken asyncActionToken) {
                    mqttAndroidClient.setBufferOpts(getDisconnectedBufferOptions());    //연결에 성공한경우
                    Log.e("Connect_success", "Success");

                    // 익명함수 이용 구독
                    try {
                        mqttAndroidClient.subscribe("otpbox_rasp", 0, new IMqttMessageListener() {
                            @Override
                            public void messageArrived(String topic, MqttMessage message) throws Exception {
                                String msg = new String(message.getPayload());
                                Log.e("arrive message : ", msg);
                                JSONObject jsonObject = new JSONObject(msg);
                                password = jsonObject.getString("password");
                                boxnumber = jsonObject.getString("boxnumber");
                                address = jsonObject.getString("address");
                                if(address.equals("501")){
                                    tv_pw_501.setText(password);
                                    tv_box_501.setText(boxnumber);
                                }
                                else if(address.equals("502")){
                                    tv_pw_502.setText(password);
                                    tv_box_502.setText(boxnumber);
                                }
                                else if(address.equals("503")){
                                    tv_pw_503.setText(password);
                                    tv_box_503.setText(boxnumber);
                                }
                                else{
                                    Log.e("Error : ","JSON Parsing");
                                }
                                Log.e("address : ", address);

                            }
                        });
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {   //연결에 실패한경우
                    Log.e("connect_fail", "Failure " + exception.toString());
                }
            });
        } catch (
                MqttException e)
        {
            e.printStackTrace();
        }

        btn_sendrasp_501.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    String temp_boxnumber = String.valueOf(tv_box_501.getText());
                    if(temp_boxnumber.equals("None")){
                        Toast.makeText(getApplicationContext(),"받은 택배가 없습니다.", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        //otpbox_android 토픽으로 수령한 box number publish
                        mqttAndroidClient.publish("otpbox_android", temp_boxnumber.getBytes(), 0 , false );
                        tv_pw_501.setText("None");
                        tv_box_501.setText("None");
                        Toast.makeText(getApplicationContext(),"택배보관함의 상태를 변경하였습니다.", Toast.LENGTH_SHORT).show();
                    }
                } catch (MqttException e) {
                    e.printStackTrace();
                }
            }
        });
        btn_sendrasp_502.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    String temp_boxnumber = String.valueOf(tv_box_502.getText());
                    if(temp_boxnumber.equals("None")){
                        Toast.makeText(getApplicationContext(),"받은 택배가 없습니다.", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        //otpbox_android 토픽으로 수령한 box number publish
                        mqttAndroidClient.publish("otpbox_android", temp_boxnumber.getBytes(), 0 , false );
                        tv_pw_502.setText("None");
                        tv_box_502.setText("None");
                        Toast.makeText(getApplicationContext(),"택배보관함의 상태를 변경하였습니다.", Toast.LENGTH_SHORT).show();
                    }
                } catch (MqttException e) {
                    e.printStackTrace();
                }
            }
        });
        btn_sendrasp_503.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    String temp_boxnumber = String.valueOf(tv_box_503.getText());
                    if(temp_boxnumber.equals("None")){
                        Toast.makeText(getApplicationContext(),"받은 택배가 없습니다.", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        //otpbox_android 토픽으로 수령한 box number publish
                        mqttAndroidClient.publish("otpbox_android", temp_boxnumber.getBytes(), 0 , false );
                        tv_pw_503.setText("None");
                        tv_box_503.setText("None");
                        Toast.makeText(getApplicationContext(),"택배보관함의 상태를 변경하였습니다.", Toast.LENGTH_SHORT).show();
                    }
                } catch (MqttException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    private DisconnectedBufferOptions getDisconnectedBufferOptions() {
        DisconnectedBufferOptions disconnectedBufferOptions = new DisconnectedBufferOptions();
        disconnectedBufferOptions.setBufferEnabled(true);
        disconnectedBufferOptions.setBufferSize(100);
        disconnectedBufferOptions.setPersistBuffer(true);
        disconnectedBufferOptions.setDeleteOldestMessages(false);
        return disconnectedBufferOptions;
    }



    private MqttConnectOptions getMqttConnectionOption() {
        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setCleanSession(false);
        mqttConnectOptions.setAutomaticReconnect(true);
        mqttConnectOptions.setWill("aaa", "I am going offline".getBytes(), 1, true);
        return mqttConnectOptions;
    }
}
