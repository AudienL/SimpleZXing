package com.audienl.simplezxing;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.zxing.client.android.SuperScanActivity;

public class CustomScanActivity extends SuperScanActivity {
    public static final String RESULT_QRCODE_TEXT = "qrcode_text";

    private Button mBtnGetImage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_scan);

        mBtnGetImage = (Button) findViewById(R.id.btn_get_image);
        mBtnGetImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLocalImage();
            }
        });
    }

    private ProgressDialog mProgressDialog;

    @Override
    public void onGetImageOk(final String imgPath) {
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("正在扫描...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();

        new Thread(new Runnable() {
            @Override
            public void run() {
                scanningImage(imgPath);
            }
        }).start();
    }

    @Override
    public void handlerResult(final CharSequence result) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mProgressDialog != null) mProgressDialog.dismiss();

                Intent intent = new Intent();
                intent.putExtra(RESULT_QRCODE_TEXT, result);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }
}
