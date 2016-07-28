package com.audienl.simplezxing;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.DecodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.client.android.RGBLuminanceSource;
import com.google.zxing.client.android.SimpleScanActivity;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;

import java.util.Hashtable;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_SCAN = 1;
    private static final int REQUEST_CODE_GET_IMAGE = 2;

    @Bind(R.id.btn_simple_scan) Button mBtnSimpleScan;
    @Bind(R.id.btn_custom_scan) Button mBtnCustomScan;
    @Bind(R.id.tv_result) TextView mTvResult;
    @Bind(R.id.btn_scan_picture) Button mBtnScanPicture;

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
        mBtnScanPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //打开手机中的相册
                Intent innerIntent = new Intent(Intent.ACTION_GET_CONTENT); //"android.intent.action.GET_CONTENT"
                innerIntent.setType("image/*");
                Intent wrapperIntent = Intent.createChooser(innerIntent, "选择二维码图片");
                startActivityForResult(wrapperIntent, REQUEST_CODE_GET_IMAGE);
            }
        });
    }

    private ProgressDialog mProgress;
    private String photo_path = null;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_SCAN:
                    if (data != null) {
                        String result = data.getStringExtra(CustomScanActivity.RESULT_QRCODE_TEXT);
                        mTvResult.setText(result);
                    }
                    break;
                case REQUEST_CODE_GET_IMAGE:
                    //获取选中图片的路径
                    Cursor cursor = getContentResolver()
                            .query(data.getData(), null, null, null, null);
                    if (cursor.moveToFirst()) {
                        photo_path = cursor
                                .getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                    }
                    cursor.close();

                    mProgress = new ProgressDialog(context);
                    mProgress.setMessage("正在扫描...");
                    mProgress.setCancelable(false);
                    mProgress.show();

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Result result = scanningImage(photo_path);
                            if (result != null) {
                                Message m = mHandler.obtainMessage();
                                m.what = PARSE_BARCODE_SUC;
                                m.obj = result.getText();
                                mHandler.sendMessage(m);
                            } else {
                                Message m = mHandler.obtainMessage();
                                m.what = PARSE_BARCODE_FAIL;
                                m.obj = "Scan failed!";
                                mHandler.sendMessage(m);
                            }

                        }
                    }).start();

                    break;

            }
        }
    }

    private static final int PARSE_BARCODE_SUC = 300;
    private static final int PARSE_BARCODE_FAIL = 303;

    private static final String TAG = "MainActivity";

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            mProgress.dismiss();
            switch (msg.what) {
                case PARSE_BARCODE_SUC:
                    Log.i(TAG, "handleMessage: result = " + msg.obj);
                    Toast.makeText(MainActivity.this, (CharSequence) msg.obj, Toast.LENGTH_SHORT)
                         .show();
                    break;
                case PARSE_BARCODE_FAIL:
                    Toast.makeText(context, (String) msg.obj, Toast.LENGTH_LONG).show();
                    break;
            }
        }
    };

    private Bitmap scanBitmap;

    /**
     * 扫描二维码图片的方法
     */
    public Result scanningImage(String path) {
        if (TextUtils.isEmpty(path)) {
            return null;
        }
        Hashtable<DecodeHintType, String> hints = new Hashtable<DecodeHintType, String>();
        hints.put(DecodeHintType.CHARACTER_SET, "UTF8"); //设置二维码内容的编码

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true; // 先获取原大小
        scanBitmap = BitmapFactory.decodeFile(path, options);
        options.inJustDecodeBounds = false; // 获取新的大小
        int sampleSize = (int) (options.outHeight / (float) 200);
        if (sampleSize <= 0) sampleSize = 1;
        options.inSampleSize = sampleSize;
        scanBitmap = BitmapFactory.decodeFile(path, options);
        RGBLuminanceSource source = new RGBLuminanceSource(scanBitmap);
        BinaryBitmap bitmap1 = new BinaryBitmap(new HybridBinarizer(source));
        QRCodeReader reader = new QRCodeReader();
        try {
            return reader.decode(bitmap1, hints);

        } catch (NotFoundException e) {
            e.printStackTrace();
        } catch (ChecksumException e) {
            e.printStackTrace();
        } catch (FormatException e) {
            e.printStackTrace();
        }
        return null;
    }
}
