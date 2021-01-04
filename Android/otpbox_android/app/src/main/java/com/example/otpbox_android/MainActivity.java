package com.example.otpbox_android;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {

    //mqtt 클라이언트
    private MqttAndroidClient mqttAndroidClient;

    //수신한 데이터 변수
    private String password;
    private String boxnumber;
    private String address;

    //메인화면 변수 선언
    LinearLayout linear_main;
    Button btn_start;
    Button btn_stop;

    //501호 변수 선언
    LinearLayout add501;
    TextView tv_pw_501_msg1;
    TextView tv_pw_501_msg2;
    TextView tv_pw_501_msg3;
    TextView tv_box_501_msg1;
    TextView tv_box_501_msg2;
    TextView tv_box_501_msg3;
    Button btn_501_msg1;
    Button btn_501_msg2;
    Button btn_501_msg3;
    RelativeLayout add501_message1;
    RelativeLayout add501_message2;
    RelativeLayout add501_message3;
    LinearLayout add501_nobox;
    boolean add501_msg1_val = true;
    boolean add501_msg2_val = true;
    boolean add501_msg3_val = true;

    //502호 변수 선언
    LinearLayout add502;
    TextView tv_pw_502_msg1;
    TextView tv_pw_502_msg2;
    TextView tv_pw_502_msg3;
    TextView tv_box_502_msg1;
    TextView tv_box_502_msg2;
    TextView tv_box_502_msg3;
    Button btn_502_msg1;
    Button btn_502_msg2;
    Button btn_502_msg3;
    RelativeLayout add502_message1;
    RelativeLayout add502_message2;
    RelativeLayout add502_message3;
    LinearLayout add502_nobox;
    boolean add502_msg1_val = true;
    boolean add502_msg2_val = true;
    boolean add502_msg3_val = true;

    //503호 변수 선언
    LinearLayout add503;
    TextView tv_pw_503_msg1;
    TextView tv_pw_503_msg2;
    TextView tv_pw_503_msg3;
    TextView tv_box_503_msg1;
    TextView tv_box_503_msg2;
    TextView tv_box_503_msg3;
    Button btn_503_msg1;
    Button btn_503_msg2;
    Button btn_503_msg3;
    RelativeLayout add503_message1;
    RelativeLayout add503_message2;
    RelativeLayout add503_message3;
    LinearLayout add503_nobox;
    boolean add503_msg1_val = true;
    boolean add503_msg2_val = true;
    boolean add503_msg3_val = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //메인화면 레이아웃
        linear_main = (LinearLayout)findViewById(R.id.linear_main);
        btn_start = (Button)findViewById(R.id.btn_start);
        btn_stop = (Button)findViewById(R.id.btn_stop);

        btn_start.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                //백그라운드 추가
            }
        });
        btn_stop.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                //백그라운드 추가
            }
        });

        //501호 레이아웃
        add501 = (LinearLayout)findViewById(R.id.add501);

        tv_pw_501_msg1 = (TextView)findViewById(R.id.tv_pw_501_msg1);
        tv_pw_501_msg2 = (TextView)findViewById(R.id.tv_pw_501_msg2);
        tv_pw_501_msg3 = (TextView)findViewById(R.id.tv_pw_501_msg3);

        tv_box_501_msg1 = (TextView)findViewById(R.id.tv_box_501_msg1);
        tv_box_501_msg2 = (TextView)findViewById(R.id.tv_box_501_msg2);
        tv_box_501_msg3 = (TextView)findViewById(R.id.tv_box_501_msg3);

        btn_501_msg1 = (Button)findViewById(R.id.btn_501_msg1);
        btn_501_msg2 = (Button)findViewById(R.id.btn_501_msg2);
        btn_501_msg3 = (Button)findViewById(R.id.btn_501_msg3);

        add501_message1 = (RelativeLayout)findViewById(R.id.add501_message1);
        add501_message2 = (RelativeLayout)findViewById(R.id.add501_message2);
        add501_message3 = (RelativeLayout)findViewById(R.id.add501_message3);
        add501_nobox = (LinearLayout)findViewById(R.id.add501_nobox);

        //502호 레이아웃
        add502 = (LinearLayout)findViewById(R.id.add502);

        tv_pw_502_msg1 = (TextView)findViewById(R.id.tv_pw_502_msg1);
        tv_pw_502_msg2 = (TextView)findViewById(R.id.tv_pw_502_msg2);
        tv_pw_502_msg3 = (TextView)findViewById(R.id.tv_pw_502_msg3);

        tv_box_502_msg1 = (TextView)findViewById(R.id.tv_box_502_msg1);
        tv_box_502_msg2 = (TextView)findViewById(R.id.tv_box_502_msg2);
        tv_box_502_msg3 = (TextView)findViewById(R.id.tv_box_502_msg3);

        btn_502_msg1 = (Button)findViewById(R.id.btn_502_msg1);
        btn_502_msg2 = (Button)findViewById(R.id.btn_502_msg2);
        btn_502_msg3 = (Button)findViewById(R.id.btn_502_msg3);

        add502_message1 = (RelativeLayout)findViewById(R.id.add502_message1);
        add502_message2 = (RelativeLayout)findViewById(R.id.add502_message2);
        add502_message3 = (RelativeLayout)findViewById(R.id.add502_message3);
        add502_nobox = (LinearLayout)findViewById(R.id.add502_nobox);

        //503호 레이아웃
        add503 = (LinearLayout)findViewById(R.id.add503);

        tv_pw_503_msg1 = (TextView)findViewById(R.id.tv_pw_503_msg1);
        tv_pw_503_msg2 = (TextView)findViewById(R.id.tv_pw_503_msg2);
        tv_pw_503_msg3 = (TextView)findViewById(R.id.tv_pw_503_msg3);

        tv_box_503_msg1 = (TextView)findViewById(R.id.tv_box_503_msg1);
        tv_box_503_msg2 = (TextView)findViewById(R.id.tv_box_503_msg2);
        tv_box_503_msg3 = (TextView)findViewById(R.id.tv_box_503_msg3);

        btn_503_msg1 = (Button)findViewById(R.id.btn_503_msg1);
        btn_503_msg2 = (Button)findViewById(R.id.btn_503_msg2);
        btn_503_msg3 = (Button)findViewById(R.id.btn_503_msg3);

        add503_message1 = (RelativeLayout)findViewById(R.id.add503_message1);
        add503_message2 = (RelativeLayout)findViewById(R.id.add503_message2);
        add503_message3 = (RelativeLayout)findViewById(R.id.add503_message3);
        add503_nobox = (LinearLayout)findViewById(R.id.add503_nobox);

        //MQTT클라이언트 객체 생성
        mqttAndroidClient = new MqttAndroidClient(this,  "tcp://" + "211.197.225.191" + ":1883", MqttClient.generateClientId());

        try {
            //mqtttoken 생성
            IMqttToken token = mqttAndroidClient.connect(getMqttConnectionOption());
            token.setActionCallback(new IMqttActionListener() {

                //연결에 성공한경우
                public void onSuccess(IMqttToken asyncActionToken) {
                    mqttAndroidClient.setBufferOpts(getDisconnectedBufferOptions());
                    Log.e("Connect_success", "Success");
                    // 익명함수 이용 구독 콜백함수
                    // 서버에서 보낸 메시지 수신 topic : otpbox_server_to_android
                    try {
                        mqttAndroidClient.subscribe("otpbox_server_to_android", 0, new IMqttMessageListener() {
                            @Override
                            public void messageArrived(String topic, MqttMessage message) throws Exception {
                                String msg = new String(message.getPayload());
                                Log.e("arrive message : ", msg);

                                //JSON Parsing
                                JSONObject jsonObject = new JSONObject(msg);
                                password = jsonObject.getString("password");
                                boxnumber = jsonObject.getString("boxnumber");
                                address = jsonObject.getString("address");

                                //해당하는 TextView에 출력
                                if(address.equals("501")){
                                    Log.e("test message : ", "1");
                                    add501_nobox.setVisibility(View.GONE);
                                    if(add501_msg1_val == true){
                                        add501_msg1_val = false;
                                        Log.e("test message : ", "2");
                                        add501_message1.setVisibility(View.VISIBLE);
                                        tv_pw_501_msg1.setText("*"+password+"#");
                                        tv_box_501_msg1.setText(boxnumber);
                                        Log.e("test message : ", "3");
                                    }
                                    else if(add501_msg2_val == true){
                                        tv_pw_501_msg2.setText("*"+password+"#");
                                        tv_box_501_msg2.setText(boxnumber);
                                        add501_msg2_val = false;
                                        add501_message2.setVisibility(View.VISIBLE);
                                    }
                                    else if(add501_msg3_val == true){
                                        tv_pw_501_msg3.setText("*"+password+"#");
                                        tv_box_501_msg3.setText(boxnumber);
                                        add501_msg3_val = false;
                                        add501_message3.setVisibility(View.VISIBLE);
                                    }
                                    else{
                                        Log.e("Error :", "Allocate password add_501");
                                    }
                                }
                                else if(address.equals("502")){
                                    add502_nobox.setVisibility(View.GONE);
                                    if(add502_msg1_val == true){
                                        add502_msg1_val = false;
                                        add502_message1.setVisibility(View.VISIBLE);
                                        tv_pw_502_msg1.setText("*"+password+"#");
                                        tv_box_502_msg1.setText(boxnumber);
                                    }
                                    else if(add502_msg2_val == true){
                                        tv_pw_502_msg2.setText("*"+password+"#");
                                        tv_box_502_msg2.setText(boxnumber);
                                        add502_msg2_val = false;
                                        add502_message2.setVisibility(View.VISIBLE);
                                    }
                                    else if(add502_msg3_val == true){
                                        tv_pw_502_msg3.setText("*"+password+"#");
                                        tv_box_502_msg3.setText(boxnumber);
                                        add502_msg3_val = false;
                                        add502_message3.setVisibility(View.VISIBLE);
                                    }
                                    else{
                                        Log.e("Error :", "Allocate password add_502");
                                    }
                                }
                                else if(address.equals("503")){
                                    add503_nobox.setVisibility(View.GONE);
                                    if(add503_msg1_val == true){
                                        add503_msg1_val = false;
                                        add503_message1.setVisibility(View.VISIBLE);
                                        tv_pw_503_msg1.setText("*"+password+"#");
                                        tv_box_503_msg1.setText(boxnumber);
                                    }
                                    else if(add503_msg2_val == true){
                                        tv_pw_503_msg2.setText("*"+password+"#");
                                        tv_box_503_msg2.setText(boxnumber);
                                        add503_msg2_val = false;
                                        add503_message2.setVisibility(View.VISIBLE);
                                    }
                                    else if(add503_msg3_val == true){
                                        tv_pw_503_msg3.setText("*"+password+"#");
                                        tv_box_503_msg3.setText(boxnumber);
                                        add503_msg3_val = false;
                                        add503_message3.setVisibility(View.VISIBLE);
                                    }
                                    else{
                                        Log.e("Error :", "Allocate password add_503");
                                    }
                                }
                            }
                        });
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }
                }
                //연결에 실패한경우
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.e("connect_fail", "Failure " + exception.toString());
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }

        //501호 수령완료 버튼
        btn_501_msg1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("OTPbox_GTCoding");
                builder.setMessage("택배를 수령하시겠습니까?");
                builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        try{
                            String temp_boxnumber = String.valueOf(tv_box_501_msg1.getText());
                            //서버로 box number 전송
                            mqttAndroidClient.publish("otpbox_android_to_server", temp_boxnumber.getBytes(), 0 , false );
                            add501_message1.setVisibility(View.GONE);
                            if(add501_message2.getVisibility() == View.GONE && add501_message3.getVisibility() == View.GONE){
                                add501_nobox.setVisibility(View.VISIBLE);
                            }
                            add501_msg1_val = true;
                            Toast.makeText(getApplicationContext(),"택배를 수령했습니다.", Toast.LENGTH_SHORT).show();
                        }catch (MqttException e){
                            e.printStackTrace();
                        }
                    }
                });
                builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(getApplicationContext(),"취소했습니다.",Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setIcon(R.drawable.nobox);
                builder.show();
            }
        });
        btn_501_msg2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("OTPbox_GTCoding");
                builder.setMessage("택배를 수령하시겠습니까?");
                builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        try {
                            String temp_boxnumber = String.valueOf(tv_box_501_msg2.getText());
                            //서버로 box number 전송
                            mqttAndroidClient.publish("otpbox_android_to_server", temp_boxnumber.getBytes(), 0 , false );
                            add501_message2.setVisibility(View.GONE);
                            if(add501_message1.getVisibility() == View.GONE && add501_message3.getVisibility() == View.GONE){
                                add501_nobox.setVisibility(View.VISIBLE);
                            }
                            add501_msg2_val = true;
                            Toast.makeText(getApplicationContext(),"택배를 수령했습니다.", Toast.LENGTH_SHORT).show();
                        } catch (MqttException e) {
                            e.printStackTrace();
                        }
                    }
                });
                builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(getApplicationContext(),"취소했습니다.",Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setIcon(R.drawable.nobox);
                builder.show();
            }
        });
        btn_501_msg3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("OTPbox_GTCoding");
                builder.setMessage("택배를 수령하시겠습니까?");
                builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        try {
                            String temp_boxnumber = String.valueOf(tv_box_501_msg3.getText());
                            //서버로 box number 전송
                            mqttAndroidClient.publish("otpbox_android_to_server", temp_boxnumber.getBytes(), 0 , false );
                            add501_message3.setVisibility(View.GONE);
                            if(add501_message1.getVisibility() == View.GONE && add501_message2.getVisibility() == View.GONE){
                                add501_nobox.setVisibility(View.VISIBLE);
                            }
                            add501_msg3_val = true;
                            Toast.makeText(getApplicationContext(),"택배를 수령했습니다.", Toast.LENGTH_SHORT).show();
                        } catch (MqttException e) {
                            e.printStackTrace();
                        }
                    }
                });
                builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(getApplicationContext(),"취소했습니다.",Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setIcon(R.drawable.nobox);
                builder.show();
            }
        });

        //502호 수령완료 버튼
        btn_502_msg1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("OTPbox_GTCoding");
                builder.setMessage("택배를 수령하시겠습니까?");
                builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        try {
                            String temp_boxnumber = String.valueOf(tv_box_502_msg1.getText());
                            //서버로 box number 전송
                            mqttAndroidClient.publish("otpbox_android_to_server", temp_boxnumber.getBytes(), 0 , false );
                            add502_message1.setVisibility(View.GONE);
                            if(add502_message2.getVisibility() == View.GONE && add502_message3.getVisibility() == View.GONE){
                                add502_nobox.setVisibility(View.VISIBLE);
                            }
                            add502_msg1_val = true;
                            Toast.makeText(getApplicationContext(),"택배를 수령했습니다.", Toast.LENGTH_SHORT).show();
                        } catch (MqttException e) {
                            e.printStackTrace();
                        }
                    }
                });
                builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(getApplicationContext(),"취소했습니다.",Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setIcon(R.drawable.nobox);
                builder.show();
            }
        });
        btn_502_msg2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("OTPbox_GTCoding");
                builder.setMessage("택배를 수령하시겠습니까?");
                builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        try {
                            String temp_boxnumber = String.valueOf(tv_box_502_msg2.getText());
                            //서버로 box number 전송
                            mqttAndroidClient.publish("otpbox_android_to_server", temp_boxnumber.getBytes(), 0 , false );
                            add502_message2.setVisibility(View.GONE);
                            if(add502_message1.getVisibility() == View.GONE && add502_message3.getVisibility() == View.GONE){
                                add502_nobox.setVisibility(View.VISIBLE);
                            }
                            add502_msg2_val = true;
                            Toast.makeText(getApplicationContext(),"택배를 수령했습니다.", Toast.LENGTH_SHORT).show();
                        } catch (MqttException e) {
                            e.printStackTrace();
                        }
                    }
                });
                builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(getApplicationContext(),"취소했습니다.",Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setIcon(R.drawable.nobox);
                builder.show();
            }
        });
        btn_502_msg3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("OTPbox_GTCoding");
                builder.setMessage("택배를 수령하시겠습니까?");
                builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        try {
                            String temp_boxnumber = String.valueOf(tv_box_502_msg3.getText());
                            //서버로 box number 전송
                            mqttAndroidClient.publish("otpbox_android_to_server", temp_boxnumber.getBytes(), 0 , false );
                            add502_message3.setVisibility(View.GONE);
                            if(add502_message1.getVisibility() == View.GONE && add502_message2.getVisibility() == View.GONE){
                                add502_nobox.setVisibility(View.VISIBLE);
                            }
                            add502_msg3_val = true;
                            Toast.makeText(getApplicationContext(),"택배를 수령했습니다.", Toast.LENGTH_SHORT).show();
                        } catch (MqttException e) {
                            e.printStackTrace();
                        }
                    }
                });
                builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(getApplicationContext(),"취소했습니다.",Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setIcon(R.drawable.nobox);
                builder.show();
            }
        });

        //503호 수령완료 버튼
        btn_503_msg1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("OTPbox_GTCoding");
                builder.setMessage("택배를 수령하시겠습니까?");
                builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        try {
                            String temp_boxnumber = String.valueOf(tv_box_503_msg1.getText());
                            //서버로 box number 전송
                            mqttAndroidClient.publish("otpbox_android_to_server", temp_boxnumber.getBytes(), 0 , false );
                            add503_message1.setVisibility(View.GONE);
                            if(add503_message2.getVisibility() == View.GONE && add503_message3.getVisibility() == View.GONE){
                                add503_nobox.setVisibility(View.VISIBLE);
                            }
                            add503_msg1_val = true;
                            Toast.makeText(getApplicationContext(),"택배를 수령했습니다.", Toast.LENGTH_SHORT).show();
                        } catch (MqttException e) {
                            e.printStackTrace();
                        }
                    }
                });
                builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(getApplicationContext(),"취소했습니다.",Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setIcon(R.drawable.nobox);
                builder.show();
            }
        });
        btn_503_msg2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("OTPbox_GTCoding");
                builder.setMessage("택배를 수령하시겠습니까?");
                builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        try {
                            String temp_boxnumber = String.valueOf(tv_box_503_msg2.getText());
                            //서버로 box number 전송
                            mqttAndroidClient.publish("otpbox_android_to_server", temp_boxnumber.getBytes(), 0 , false );
                            add503_message2.setVisibility(View.GONE);
                            if(add503_message1.getVisibility() == View.GONE && add503_message3.getVisibility() == View.GONE){
                                add503_nobox.setVisibility(View.VISIBLE);
                            }
                            add503_msg2_val = true;
                            Toast.makeText(getApplicationContext(),"택배를 수령했습니다.", Toast.LENGTH_SHORT).show();
                        } catch (MqttException e) {
                            e.printStackTrace();
                        }
                    }
                });
                builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(getApplicationContext(),"취소했습니다.",Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setIcon(R.drawable.nobox);
                builder.show();
            }
        });
        btn_503_msg3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("OTPbox_GTCoding");
                builder.setMessage("택배를 수령하시겠습니까?");
                builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        try {
                            String temp_boxnumber = String.valueOf(tv_box_503_msg3.getText());
                            //서버로 box number 전송
                            mqttAndroidClient.publish("otpbox_android_to_server", temp_boxnumber.getBytes(), 0 , false );
                            add503_message3.setVisibility(View.GONE);
                            if(add503_message1.getVisibility() == View.GONE && add503_message2.getVisibility() == View.GONE){
                                add503_nobox.setVisibility(View.VISIBLE);
                            }
                            add503_msg3_val = true;
                            Toast.makeText(getApplicationContext(),"택배를 수령했습니다.", Toast.LENGTH_SHORT).show();
                        } catch (MqttException e) {
                            e.printStackTrace();
                        }
                    }
                });
                builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(getApplicationContext(),"취소했습니다.",Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setIcon(R.drawable.nobox);
                builder.show();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.select_add, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.item_main:
                linear_main.setVisibility(View.VISIBLE);
                add501.setVisibility(View.GONE);
                add502.setVisibility(View.GONE);
                add503.setVisibility(View.GONE);
                Toast.makeText(this,"Main menu", Toast.LENGTH_SHORT).show();
                break;
            case R.id.item_501:
                linear_main.setVisibility(View.GONE);
                add501.setVisibility(View.VISIBLE);
                add502.setVisibility(View.GONE);
                add503.setVisibility(View.GONE);
                Toast.makeText(this,"add 501", Toast.LENGTH_SHORT).show();
                item.setChecked(true);
                break;
            case R.id.item_502:
                linear_main.setVisibility(View.GONE);
                add501.setVisibility(View.GONE);
                add502.setVisibility(View.VISIBLE);
                add503.setVisibility(View.GONE);
                Toast.makeText(this,"add 502", Toast.LENGTH_SHORT).show();
                item.setChecked(true);
                break;
            case R.id.item_503:
                linear_main.setVisibility(View.GONE);
                add501.setVisibility(View.GONE);
                add502.setVisibility(View.GONE);
                add503.setVisibility(View.VISIBLE);
                Toast.makeText(this,"add 503", Toast.LENGTH_SHORT).show();
                item.setChecked(true);
                break;
        }
        return true;
    }
}
