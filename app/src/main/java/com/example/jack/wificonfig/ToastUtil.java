package com.example.jack.wificonfig;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by Jack on 2017/11/18.
 */

public class ToastUtil {
    public static void makeText(Context context, String content){
        Toast.makeText(context, content, Toast.LENGTH_SHORT).show();
    }
}
