# SimpleZXing

这是根据官方ZXing精简之后的项目，只保留扫描字符串的功能。
为了最简单的使用，本项目定义了一个 SimpleScanActivity，可以直接使用。
如果需要自定义的话，可以继承 SuperScanActivity，然后参考 SimpleScanActivity 自定义。

## APK：

[下载DEMO](https://github.com/AudienL/SimpleZXing/blob/master/document/app-release.apk?raw=true)

## 使用：

### 一、在 project 根目录的 build.gradle 中添加：

```groovy
allprojects {
    repositories {
        jcenter()
        maven { url "https://jitpack.io" }
    }
}
```

### 二、在 module 根目录的 build.gradle 中添加：

其中最后版本在 release 中查看，如：1.0
```groovy
dependencies {
    compile 'com.github.AudienL:SimpleZXing:最后版本'
}
```

### 三、使用

#### 1> SimpleScanActivity

```java
startActivity(new Intent(context, SimpleScanActivity.class));
```

#### 2> 自定义

```xml
<?xml version="1.0" encoding="UTF-8"?>
<merge xmlns:android="http://schemas.android.com/apk/res/android">

    <SurfaceView
        android:id="@+id/preview_view"
        android:layout_width="fill_parent"
        android:layout_height="fill_parent"/>

    <com.google.zxing.client.android.core.ViewfinderView
        android:id="@+id/viewfinder_view"
        android:layout_width="fill_parent"
        android:layout_height="fill_parent"/>

    <!-- 扫描方法提示 -->
    <TextView
        android:id="@+id/status_view"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_gravity="bottom|center_horizontal"
        android:background="#00000000"
        android:text="@string/msg_default_status"
        android:textColor="#ECF0F1"/>
</merge>
```
```java
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
```
```java
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
```