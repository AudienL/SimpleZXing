package com.audienl.simplezxing;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.zxing.client.android.SimpleScanActivity;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_SCAN = 1;

    @Bind(R.id.btn_simple_scan) Button mBtnSimpleScan;
    @Bind(R.id.btn_custom_scan) Button mBtnCustomScan;
    @Bind(R.id.tv_result) TextView mTvResult;

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        context = this;

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
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_SCAN:
                    if (data != null) {
                        String result = data.getStringExtra(CustomScanActivity.RESULT_QRCODE_TEXT);
                        mTvResult.setText(result == null ? "扫描失败" : "结果：" + result);
                    }
                    break;
            }
        }
    }
}
