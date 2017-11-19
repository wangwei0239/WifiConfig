package com.example.jack.wificonfig;

import android.net.wifi.ScanResult;

/**
 * Created by Jack on 2017/11/19.
 */

public class ScanResultBean {
    private String state;
    private ScanResult scanResult;

    public ScanResultBean(String state, ScanResult scanResult) {
        this.state = state;
        this.scanResult = scanResult;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public ScanResult getScanResult() {
        return scanResult;
    }

    public void setScanResult(ScanResult scanResult) {
        this.scanResult = scanResult;
    }
}
