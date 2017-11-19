package com.example.jack.wificonfig;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

public class MainActivity extends AppCompatActivity {


    private Button search;
    private Button getInfo;
    private ListView listView;
    private MyAdapter adapter;
    private WifiManager wifiManager;
    private IntentFilter mIntentFilter;
    private ScanResultBean curBean;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        adapter = new MyAdapter(this);
        search = findViewById(R.id.search);
        getInfo = findViewById(R.id.get_info);
        listView = findViewById(R.id.list);
        listView.setAdapter(adapter);
        initBroadCast();
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchWifi();
            }
        });

        getInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WifiInfo info = wifiManager.getConnectionInfo();
                info.getSupplicantState().describeContents();
                ToastUtil.makeText(MainActivity.this,info.getSSID());
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final ScanResult scanResult = ((ScanResultBean) parent.getAdapter().getItem(position)).getScanResult();
                final EditText editText = new EditText(MainActivity.this);
                editText.setText("password");
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("设置WiFi")
                        .setMessage(scanResult.BSSID+":"+scanResult.SSID)
                        .setView(editText)
                        .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                connectToWifi(scanResult.SSID, editText.getText().toString());
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            }
        });
    }

    private void initBroadCast(){
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        mIntentFilter.addAction(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION);
        mIntentFilter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiManager.RSSI_CHANGED_ACTION);
        mIntentFilter.addAction(WifiManager.NETWORK_IDS_CHANGED_ACTION);
        registerReceiver(broadcastReceiver, mIntentFilter);
    }

    private void updateConnectionState(String state){
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        String simpleSSID = wifiInfo.getSSID().substring(1, wifiInfo.getSSID().length()-1);
        if(curBean == null || !curBean.getScanResult().SSID.equals(simpleSSID)){
            for(int i = 0; i < adapter.getCount(); i++){
                if(adapter.getItem(i).getScanResult().SSID.equals(simpleSSID)){
                    curBean = adapter.getItem(i);
                    break;
                }
            }
        }
        if(curBean != null){
            curBean.setState(state);
            adapter.notifyDataSetChanged();
        }
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(action)){
//                Log.i("WW_1", "NETWORK_STATE_CHANGED_ACTION------");
                NetworkInfo networkInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                Log.i("WW_1", networkInfo.getDetailedState().name());
                updateConnectionState(networkInfo.getDetailedState().name());
//                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
//                Log.i("WW_1", "Connected Wifi:"+wifiInfo.getSSID());
//                String simpleSSID = wifiInfo.getSSID().substring(1, wifiInfo.getSSID().length()-1);

//                if(curBean == null || !curBean.getScanResult().SSID.equals(simpleSSID)){
//                    Log.i("WW_1", "First inside");
//                    for(int i = 0; i < adapter.getCount(); i++){
//                        Log.i("WW_1", "Loop condition:"+adapter.getItem(i).getScanResult().SSID.equals(simpleSSID)+" curada:"+adapter.getItem(i).getScanResult().SSID+" wifiinfo:"+simpleSSID);
//                        if(adapter.getItem(i).getScanResult().SSID.equals(simpleSSID)){
//                            curBean = adapter.getItem(i);
//                            Log.i("WW_1", "get item");
////                            break;
//                        }
//                    }
//                }
//                if(curBean != null){
//                    Log.i("WW_1", "update");
//                    curBean.setState(networkInfo.getDetailedState().name());
//                    adapter.notifyDataSetChanged();
//                }
            }else if (WifiManager.SCAN_RESULTS_AVAILABLE_ACTION.equals(action)){
//                Log.i("WW_2", "SCAN_RESULTS_AVAILABLE_ACTION");
            }else if (WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION.equals(action)){
//                Log.i("WW_3", "SUPPLICANT_CONNECTION_CHANGE_ACTION");
            }else if (WifiManager.SUPPLICANT_STATE_CHANGED_ACTION.equals(action)){
//                Log.i("WW_4", "SUPPLICANT_STATE_CHANGED_ACTION------");
                SupplicantState supplicantState = intent.getParcelableExtra(WifiManager.EXTRA_NEW_STATE);
                Log.i("WW_4", supplicantState.name());
                int errorCode = intent.getIntExtra(WifiManager.EXTRA_SUPPLICANT_ERROR, -1);
                Log.i("WW_4", "Error:"+errorCode);
                if(errorCode == 1){
                    updateConnectionState("密码错误");
                }
            }else if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(action)){
//                Log.i("WW_5", "WIFI_STATE_CHANGED_ACTION");
            }else if (WifiManager.RSSI_CHANGED_ACTION.equals(action)){
//                Log.i("WW_6", "RSSI_CHANGED_ACTION");
            }else if (WifiManager.NETWORK_IDS_CHANGED_ACTION.equals(action)){
//                Log.i("WW_7", "NETWORK_IDS_CHANGED_ACTION");
            }
        }
    };

    public void connectToWifi(String ssid, String password){
        WifiConfiguration wifiConfiguration = new WifiConfiguration();
        wifiConfiguration.SSID = "\""+ssid+"\"";
        wifiConfiguration.preSharedKey = "\""+password+"\"";
        int nid = wifiManager.addNetwork(wifiConfiguration);
        wifiManager.enableNetwork(nid, true);
    }

    public void searchWifi(){
        wifiManager.startScan();
        adapter.clear();
        List<ScanResult> results = wifiManager.getScanResults();
        for(ScanResult scanResult : results){
            adapter.add(new ScanResultBean("", scanResult));
        }
        adapter.notifyDataSetChanged();
    }

    class MyAdapter extends ArrayAdapter<ScanResultBean>{

        private Context context;

        public MyAdapter(@NonNull Context context) {
            super(context, 0);
            this.context = context;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            if(convertView == null){
                convertView = LayoutInflater.from(context).inflate(R.layout.adapter_wifi_list, null);
            }
            ScanResultBean scanResultBean = getItem(position);
            TextView wifiName = convertView.findViewById(R.id.wifi_name);
            TextView wifiState = convertView.findViewById(R.id.wifi_state);
            wifiName.setText(scanResultBean.getScanResult().SSID);
            wifiState.setText(scanResultBean.getState());
            return convertView;
        }
    }
}