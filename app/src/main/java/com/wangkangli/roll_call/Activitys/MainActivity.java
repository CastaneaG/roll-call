package com.wangkangli.roll_call.Activitys;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.wangkangli.roll_call.Fragments.HistoryFragment;
import com.wangkangli.roll_call.Fragments.NotifcationsFragment;
import com.wangkangli.roll_call.Fragments.SignInFragment;
import com.wangkangli.roll_call.Json.Qd;
import com.wangkangli.roll_call.Json.Qdrq;
import com.wangkangli.roll_call.Json.Qdsj;
import com.wangkangli.roll_call.R;
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

public class MainActivity extends AppCompatActivity {

    private TextView mTextMessage;
    Button btn_110;
    Button btn_qzls;
    private DrawerLayout mDrawerLayout;
    private Toolbar mToolbar;
    String strqdrqJson, strqdsjJson;
    TextView drawer_date1;
    TextView drawer_date2;
    TextView drawer_time;
    String strqdsj = "";
    Qdrq qdrq;
    public LocationClient locationClient;
    private MyLocationListener myLocationListener = new MyLocationListener();
    public double longitude; //经度
    public double latitude; //纬度
    private  int flagsms=0;
    private ArrayList<Qdsj> qdsjs = new ArrayList<>();

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_notifications:
                    Toast.makeText(MainActivity.this, "请下拉获取通知", Toast.LENGTH_SHORT).show();
                    NotifcationsFragment notifcationsFragment = new NotifcationsFragment();
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.ll_main, notifcationsFragment).commitAllowingStateLoss();

                    return true;
                case R.id.navigation_sign_in:
                    SignInFragment signInFragment = new SignInFragment();
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.ll_main, signInFragment).commitAllowingStateLoss();
                    return true;
                case R.id.navigation_history:
                    HistoryFragment historyFragment = new HistoryFragment();
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.ll_main, historyFragment).commitAllowingStateLoss();
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        locationClient = new LocationClient(MainActivity.this.getApplicationContext());
        locationClient.registerLocationListener(new MainActivity.MyLocationListener());
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.opendrawer);
        }
        btn_qzls.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                flagsms=100;
                List<String> permissionList = new ArrayList<>();
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
                }
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                    permissionList.add(Manifest.permission.READ_PHONE_STATE);
                }
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                }
                if (!permissionList.isEmpty()) {
                    String[] permissions = permissionList.toArray(new String[permissionList.size()]);
                    ActivityCompat.requestPermissions(MainActivity.this, permissions, 1);
                } else {
                    requestLocation();
                }
                Log.d("TAGweizhion", latitude + "," + longitude);


         //       sendSmsWithBody(MainActivity.this,"+8617695788621","我是来自"+ShareUtils.getString(MainActivity.this,"uclass","聋人工学院")+"的"+ShareUtils.getString(MainActivity.this,"uname","学生")+"，学号是"+ShareUtils.getString(MainActivity.this,"uid"," ")+",经纬度坐标为："+longitude+","+latitude+",");
            }
        });
        btn_110.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "长按自动发送报警短信并拨打110", Toast.LENGTH_LONG).show();
            }
        });
        btn_110.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                flagsms=110;

                // 检查是否获得了权限（Android6.0运行时权限）
                if (ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    // 没有获得授权，申请授权
                    if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                            Manifest.permission.CALL_PHONE)) {
                        // 返回值：
//                          如果app之前请求过该权限,被用户拒绝, 这个方法就会返回true.
//                          如果用户之前拒绝权限的时候勾选了对话框中”Don’t ask again”的选项,那么这个方法会返回false.
//                          如果设备策略禁止应用拥有这条权限, 这个方法也返回false.
                        // 弹窗需要解释为何需要该权限，再次请求授权
                        Toast.makeText(MainActivity.this, "请授权！", Toast.LENGTH_LONG).show();

                        // 帮跳转到该应用的设置界面，让用户手动授权
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        startActivity(intent);
                    } else {
                        // 不需要解释为何需要该权限，直接请求授权
                        ActivityCompat.requestPermissions(MainActivity.this,
                                new String[]{Manifest.permission.CALL_PHONE},
                                1);
                    }
                } else {
                    // 已经获得授权，可以打电话
                    CallPhone();
                }
                if (ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                    // 没有获得授权，申请授权
                    if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                            Manifest.permission.SEND_SMS)) {
                        // 返回值：
//                          如果app之前请求过该权限,被用户拒绝, 这个方法就会返回true.
//                          如果用户之前拒绝权限的时候勾选了对话框中”Don’t ask again”的选项,那么这个方法会返回false.
//                          如果设备策略禁止应用拥有这条权限, 这个方法也返回false.
                        // 弹窗需要解释为何需要该权限，再次请求授权
                        Toast.makeText(MainActivity.this, "请授权！", Toast.LENGTH_LONG).show();

                        // 帮跳转到该应用的设置界面，让用户手动授权
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        startActivity(intent);
                    } else {
                        // 不需要解释为何需要该权限，直接请求授权
                        ActivityCompat.requestPermissions(MainActivity.this,
                                new String[]{Manifest.permission.SEND_SMS},
                                1);
                    }
                } else {
                    // 已经获得授权，可以打电话
                    requestLocation();
//                    sendSMS("12110","我是天津理工大学学生，学号"+ ShareUtils.getString(MainActivity.this, "uid", "")+"我遇到了危险,短信来自学校手机app一键报警功能");
                }
                return false;
            }
        });
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        NotifcationsFragment notifcationsFragment = new NotifcationsFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.ll_main, notifcationsFragment)
                .commitAllowingStateLoss();

        OkHttpClient client = new OkHttpClient();
        RequestBody formBody = new FormBody
                .Builder()
                .build();
        Request request = new Request.Builder()
                .url("http://202.113.65.50:8080/imsoa/qdrqsz/wdispQdrqSz.action")
                .post(formBody)
                .build();
        Log.d("TAG", "签到日期");

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("TAG", "onFailure: " + e.getMessage());

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                strqdrqJson = response.body().string();
                Log.d("TAGqdrq", response.protocol() + " " + response.code() + " " + response.message() + " " + strqdrqJson/*+ " " + response.body().string()*/);
                Gson gson = new Gson();
//                JsonParser jsonParser = new JsonParser();
//                JsonArray jsonElements = jsonParser.parse(strqdrqJson).getAsJsonArray();
//                for (JsonElement brhistory : jsonElements) {
                qdrq = GSONTool.parseJSONWithGSON(strqdrqJson, Qdrq.class);

//                    brHistories.add(brHistory);
                    if (response.isSuccessful()) {
                        Message msg = new Message();
                        msg.what = 2;
                        handler.sendMessage(msg);
                    }
//
//                }

            }
        });


        OkHttpClient client2 = new OkHttpClient();
        RequestBody formBody2 = new FormBody
                .Builder()
                .build();
        Request request2 = new Request.Builder()
                .url("http://202.113.65.50:8080/imsoa/qdsjsz/wdispQdsjSzList.action")
                .post(formBody)
                .build();
        Log.d("TAG", "签到时间");

        client2.newCall(request2).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("TAG", "onFailure: " + e.getMessage());

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                strqdsjJson = response.body().string();
                Log.d("TAGqdsj", response.protocol() + " " + response.code() + " " + response.message() + " " + strqdsjJson/*+ " " + response.body().string()*/);
                Gson gson = new Gson();
                JsonParser jsonParser = new JsonParser();
                JsonArray jsonElements = jsonParser.parse(strqdsjJson).getAsJsonArray();
                for (JsonElement qdsj : jsonElements) {
                    Qdsj qdsj1 = gson.fromJson(qdsj, Qdsj.class);
                    strqdsj = (strqdsj + qdsj1.getFqssj() + "-" + qdsj1.getFjssj() + "\n");

                    if (response.isSuccessful()) {
                        Message msg = new Message();
                        msg.what = 1;
                        handler.sendMessage(msg);
                    }
//
                }


            }
        });


    }

    private void initView() {
        mDrawerLayout = findViewById(R.id.drawerlayout);
        mToolbar = findViewById(R.id.toolbar);
        drawer_date1 = findViewById(R.id.drawer_date1);
        drawer_date2 = findViewById(R.id.drawer_date2);
        drawer_time = findViewById(R.id.drawer_time);
        btn_110 = findViewById(R.id.btn_110);
        btn_qzls = findViewById(R.id.btn_qzls);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;
            default:
        }
        return true;
    }

    private void requestLocation() {
        Log.d("TAG request", "dianjihou");
        locationClient.start();
    }

    public static void sendSmsWithBody(Context context, String number, String body) {
        Intent sendIntent = new Intent(Intent.ACTION_SENDTO);
        sendIntent.setData(Uri.parse("smsto:" + number));
        sendIntent.putExtra("sms_body", body);
        context.startActivity(sendIntent);
    }
    private void CallPhone() {
        String number = "110";
        if (TextUtils.isEmpty(number)) {
            // 提醒用户
            // 注意：在这个匿名内部类中如果用this则表示是View.OnClickListener类的对象，
            // 所以必须用MainActivity.this来指定上下文环境。
            Toast.makeText(MainActivity.this, "号码不能为空！", Toast.LENGTH_SHORT).show();
        } else {
            // 拨号：激活系统的拨号组件
            Intent intent = new Intent();
            // 意图对象：动作 + 数据

            intent.setAction(Intent.ACTION_CALL);
            // 设置动作
            Uri data = Uri.parse("tel:" + number); // 设置数据
            intent.setData(data);
            startActivity(intent);
            //激活Activity组件
        }
    }
    public void sendSMS(String phoneNumber, String message) {
        // 获取短信管理器
        android.telephony.SmsManager smsManager = android.telephony.SmsManager
                .getDefault();
        // 拆分短信内容（手机短信长度限制）
        List<String> divideContents = smsManager.divideMessage(message);
        for (String text : divideContents) {
            smsManager.sendTextMessage(phoneNumber, null, text, null,
                    null);
        }
    }
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 授权成功，继续打电话
                    CallPhone();
                } else {
                    // 授权失败！
                    Toast.makeText(this, "授权失败！", Toast.LENGTH_LONG).show();
                }
                break;
            }
        }

    }
    public Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    drawer_time.setText(strqdsj);
                    break;
                case 2:
                    drawer_date1.setText("开始时间：" + qdrq.getFqsrq());
                    drawer_date2.setText("结束时间：" + qdrq.getFjsrq());
                    break;
                default:
                    break;
            }
        }
    };
    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            Log.d("qiandaowancheng", (String.valueOf(longitude)+String.valueOf(latitude)));
            latitude = bdLocation.getLatitude();    //获取纬度信息
            longitude = bdLocation.getLongitude();

if (flagsms==100) {
    sendSmsWithBody(MainActivity.this, "+8617695788621", "我是来自" + ShareUtils.getString(MainActivity.this, "uclass", "聋人工学院") + "的" + ShareUtils.getString(MainActivity.this, "uname", "学生") + "，学号是" + ShareUtils.getString(MainActivity.this, "uid", " ") + ",经纬度坐标为：" + longitude + "," + latitude + "(调用百度地图API),");
}
if (flagsms==110){
    sendSMS("12110","我是天津理工大学学生，学号"+ ShareUtils.getString(MainActivity.this, "uid", "")+",经纬度坐标为：" + longitude + "," + latitude + "(调用百度地图API),"+"我遇到了危险,短信来自学校手机app一键报警功能");
}
        }
    }
}
