package com.dacheng.wificlient;

import android.content.Context;
import android.graphics.Color;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.dacheng.wifi.ConnectPacket;
import com.dacheng.wifi.WifiApManager;
import com.dacheng.wifi.WifiClientService;
import com.dacheng.wifi.WifiConfig;
import com.dacheng.wifi.WifiHostService;
import com.dacheng.wifi.WifiService;

public class SendActivity extends AppCompatActivity implements View.OnClickListener {
    private final static String TAG = "WifiClientService";
    private static WifiService mClientService;
    private boolean isActive = true;
    TextView textView1;

    private Button mWakeUpBtn;
    private Button mLoadingBtn;
    private Button mTtsBtn1;
    private Button mTtsBtn2;
    private Button mTtsBtn3;
    private Button mTtsBtn4;
    private Button mTtsBtn5;
    private Button mTtsBtn6;
    private WifiService.ReceiveListener mListener;
    private final Handler mHandler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    textView1.setText("已建立连接");
                    break;
                case 0:
                    textView1.setText("连接失败");
                    break;
                case 200:
                    Toast.makeText(SendActivity.this,"success!",Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }

        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send);
        textView1 = (TextView) findViewById(R.id.text1);
        mWakeUpBtn = (Button) findViewById(R.id.wakeup);
        mLoadingBtn = (Button) findViewById(R.id.loading);
        mTtsBtn1 = (Button) findViewById(R.id.tts1);
        mTtsBtn2 = (Button) findViewById(R.id.tts2);
        mTtsBtn3 = (Button) findViewById(R.id.tts3);
        mTtsBtn4 = (Button) findViewById(R.id.tts4);
        mTtsBtn5 = (Button) findViewById(R.id.tts5);
        mTtsBtn6 = (Button) findViewById(R.id.tts6);

        mWakeUpBtn.setOnClickListener(this);
        mLoadingBtn.setOnClickListener(this);
        mTtsBtn1.setOnClickListener(this);
        mTtsBtn2.setOnClickListener(this);
        mTtsBtn3.setOnClickListener(this);
        mTtsBtn4.setOnClickListener(this);
        mTtsBtn5.setOnClickListener(this);
        mTtsBtn6.setOnClickListener(this);



        mListener = new WifiService.ReceiveListener() {
            @Override
            public void onReceive(ConnectPacket packet) {
                if (packet != null && packet.choiseIndex == 200 ) {
                    mHandler.sendEmptyMessage(200);
                }
            }
        };
    }


    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 0, 0, "开始连接");
        menu.add(0, 1, 0, "断开连接");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 0:
                connectHotspot();
                break;
            case 1:
                if(mClientService != null){
                    isActive = false;
                    mClientService.stop();
                    textView1.setText("已断开连接");
                }
               break;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isActive = false;
    }


    private void connectHotspot(){
        isActive = true;
        String ip = getHotspotLocalIpAddress(this);
        textView1.setText("start connecting ip :" +ip);
        Log.e(TAG,"start connect ip :" +ip);
        mClientService = new WifiClientService(ip,mListener);
        connect(10);
    }


    /**
     * 获取开启便携热点后自身热点IP地址
     * @param context
     * @return
     */
    public static String getHotspotLocalIpAddress(Context context) {
        WifiManager wifimanager = (WifiManager) context.getSystemService(context.WIFI_SERVICE);
        DhcpInfo dhcpInfo = wifimanager.getDhcpInfo();
        if(dhcpInfo != null) {
            int address = dhcpInfo.gateway;
            return ((address & 0xFF)
                    + "." + ((address >> 8) & 0xFF)
                    + "." + ((address >> 16) & 0xFF)
                    + "." + ((address >> 24) & 0xFF));
        }
        return null;
    }


    private void send(int idx){
        if(mClientService != null){
            mClientService.send(new ConnectPacket(idx));
        }else {
            Log.e(TAG,"mClientService is null");
        }
    }

    //连接host
    protected void connect( final int timeoutSecond) {
        Thread thread = new Thread(new Runnable() {

            public void run() {
                mClientService.connect();

                int trialsCounter = 0;
                int COUNTER_MAX = timeoutSecond * 2;
                while(!mClientService.isConnected() && trialsCounter < COUNTER_MAX && isActive) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    trialsCounter++;
                }
                if(trialsCounter < COUNTER_MAX && isActive){
                    //连接成功
                    mHandler.sendEmptyMessage(1);
                } else if(isActive){
                    //连接失败
                    mHandler.sendEmptyMessage(0);
                    mClientService.stop();
                }
            }
        });
        thread.start();
    }

    //wakeup : 0 tts1: 1 tts2: 2

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.wakeup:
                send(1);
                break;
            case R.id.tts1:
                send(2);
                view.setBackgroundColor(Color.parseColor("#666666"));
                break;
            case R.id.tts2:
                send(3);
                view.setBackgroundColor(Color.parseColor("#666666"));
                break;
            case R.id.tts3:
                send(4);
                view.setBackgroundColor(Color.parseColor("#666666"));
                break;
            case R.id.tts4:
                send(5);
                view.setBackgroundColor(Color.parseColor("#666666"));
                break;
            case R.id.tts5:
                send(6);
                view.setBackgroundColor(Color.parseColor("#666666"));
                break;
            case R.id.tts6:
                send(7);
                view.setBackgroundColor(Color.parseColor("#666666"));
                break;
            case R.id.loading:
                send(8);
                break;
        }
    }
}
