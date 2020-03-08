package com.wangkangli.roll_call.Activitys;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.annotation.UiThread;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.wangkangli.roll_call.Json.Login;
import com.wangkangli.roll_call.R;
import com.wangkangli.roll_call.tools.Fingerprint;
import com.wangkangli.roll_call.tools.GSONTools.GSONTool;
import com.wangkangli.roll_call.tools.PhoneService;
import com.wangkangli.roll_call.tools.ShareUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OriginalActivity extends AppCompatActivity {
    private Button btn_orginal_activate;
    private Button btn_orginal_login;
    String LocalImei;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_original);
        initView();
        initData();
        btn_orginal_login.setOnClickListener(new ButtonListener());
        btn_orginal_activate.setOnClickListener(new ButtonListener());


    }

    private String getPhoneNumber() {
        String forReturn = "";
        TelephonyManager mTelephonyMgr;
        mTelephonyMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        List<String> permissionList = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!permissionList.isEmpty()) {
            String[] permissions = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(this, permissions, 1);
        } else {
            forReturn = mTelephonyMgr.getLine1Number();
        }
        return forReturn;
    }

    public void initView() {
        btn_orginal_activate = findViewById(R.id.btn_orginal_activate);
        btn_orginal_login = findViewById(R.id.btn_orginal_login);
    }

    public void initData() {
        LocalImei = PhoneService.getImei(getApplicationContext());
    }


    class ButtonListener implements View.OnClickListener {
        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onClick(View v) {


            switch (v.getId()) {
                case R.id.btn_orginal_login:
                    Fingerprint fingerprint = new Fingerprint(OriginalActivity.this, new FingerprintManager.AuthenticationCallback() {

                        @Override
                        public void onAuthenticationError(int errorCode, CharSequence errString) {
                            //但多次指纹密码验证错误后，进入此方法；并且，不能短时间内调用指纹验证
                            Toast.makeText(OriginalActivity.this, errString, Toast.LENGTH_SHORT).show();


                        }


                        @Override
                        public void onAuthenticationHelp(int helpCode, CharSequence helpString) {

                            Toast.makeText(OriginalActivity.this, helpString, Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
                            Toast.makeText(OriginalActivity.this, "指纹识别成功", Toast.LENGTH_SHORT).show();
                            OkHttpClient client = new OkHttpClient();
                            RequestBody formBody = new FormBody
                                    .Builder()
                                    .add("fdhid", PhoneService.getImei(OriginalActivity.this))
                                    .build();
                            Log.d("TAG", "" + LocalImei);
                            Request request = new Request.Builder()
                                    .url("http://202.113.65.50:8080/imsoa/loginGetUserInfo.action")
                                    .post(formBody)
                                    .build();
                            Log.d("TAG", "build2");

                            client.newCall(request).enqueue(new Callback() {
                                @Override
                                public void onFailure(Call call, IOException e) {
                                    Log.d("TAG", "onFailure: " + e.getMessage());
                                }

                                @Override
                                public void onResponse(Call call, Response response) throws IOException {
                                    String resbody = response.body().string();
                                    Log.d("TAG", response.protocol() + " " + response.code() + " " + response.message()+resbody);
                                    Login login = GSONTool.parseJSONWithGSON(resbody, Login.class);
//                            RegisterBack registerBack = GSONTool.parseJSONWithGSON_RegisterBack(response_body);
                                    String[] ls_retsplit = login.getMsg().split(";");
                                    int i = 0;
                                    for (String a : ls_retsplit) {
                                        i++;
                                        Log.d("TAG", " " + a);
                                        if (i == 2){
                                            ShareUtils.putString(OriginalActivity.this, "uname", a);
                                            Log.d("存储", ShareUtils.getString(OriginalActivity.this, "uname", ""));
                                        }
                                        if (i == 3) {
                                            ShareUtils.putString(OriginalActivity.this, "uid", a);
                                            Log.d("存储", ShareUtils.getString(OriginalActivity.this, "uid", ""));
                                        }
                                        if (i == 4){
                                            ShareUtils.putString(OriginalActivity.this, "uclass", a);
                                            Log.d("存储", ShareUtils.getString(OriginalActivity.this, "uclass", ""));
                                        }
                                    }
                                    new Thread() {
                                        public void run() {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    String Phonenum = getPhoneNumber();
                                                    Toast.makeText(OriginalActivity.this, "进入主页面,下拉刷新通知" + Phonenum, Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }
                                    }.start();

                                }
                            });


                            startActivity(new Intent(OriginalActivity.this, MainActivity.class));
                        }

                        @Override
                        public void onAuthenticationFailed() {
                            Toast.makeText(OriginalActivity.this, "指纹识别失败", Toast.LENGTH_SHORT).show();
                        }
                    });
                    if (fingerprint.isFinger()) {
                        Toast.makeText(OriginalActivity.this, "请进行指纹识别", Toast.LENGTH_LONG).show();

                        fingerprint.startListening(null);
                    }

                    break;

                case R.id.btn_orginal_activate:
                    Toast.makeText(OriginalActivity.this, "进入激活界面", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent().setClass(OriginalActivity.this, RegisterActivity.class));
                    break;

            }
        }
    }
}
