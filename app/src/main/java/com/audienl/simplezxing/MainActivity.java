package com.audienl.simplezxing;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.zxing.client.android.SimpleScanActivity;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_SCAN = 1;

    private Context context;

    private Button mBtnSimpleScan;
    private Button mBtnCustomScan;
    private TextView mTvResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;

        mBtnSimpleScan = (Button) findViewById(R.id.btn_simple_scan);
        mBtnCustomScan = (Button) findViewById(R.id.btn_custom_scan);
        mTvResult = (TextView) findViewById(R.id.tv_result);

        mBtnSimpleScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 默认Activity
                startActivity(new Intent(context, SimpleScanActivity.class));
            }
        });
        mBtnCustomScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 自定义Activity
                startActivityForResult(new Intent(context, CustomScanActivity.class), REQUEST_CODE_SCAN);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_SCAN && resultCode == RESULT_OK && data != null) {
            String result = data.getStringExtra(CustomScanActivity.RESULT_QRCODE_TEXT);
            mTvResult.setText(result);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
