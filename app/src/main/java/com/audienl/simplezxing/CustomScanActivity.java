package com.audienl.simplezxing;

import android.content.Intent;
import android.os.Bundle;

import com.google.zxing.client.android.SuperScanActivity;

public class CustomScanActivity extends SuperScanActivity {
    public static final String RESULT_QRCODE_TEXT = "qrcode_text";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_scan);
    }

    @Override
    public void handlerResult(CharSequence result) {
        Intent intent = new Intent();
        intent.putExtra(RESULT_QRCODE_TEXT, result);
        setResult(RESULT_OK, intent);
        finish();
    }
}
