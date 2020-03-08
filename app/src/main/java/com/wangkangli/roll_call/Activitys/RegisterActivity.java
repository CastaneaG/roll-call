package com.wangkangli.roll_call.Activitys;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.wangkangli.roll_call.HttpCodes.RegisterBack;
import com.wangkangli.roll_call.Json.Jh;
import com.wangkangli.roll_call.Json.Qd;
import com.wangkangli.roll_call.R;
import com.wangkangli.roll_call.tools.GSONTools.GSONTool;
import com.wangkangli.roll_call.tools.PhoneService;

import java.io.IOException;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class RegisterActivity extends AppCompatActivity {
    private EditText student_uid, student_name;
    private Spinner spinner_grade, spinner_classes;
    private ArrayAdapter<String> classesArrayAdapter;
    private ArrayAdapter<String> gradeArrayAdapter;
    private SimpleAdapter mSimpleAdapter;
    private Button button_student_register;
    private String uid = null, sname = null, grade = null, classes = null;
    private TextView tv_mac;
    private TextView tv_imei;
    private String LocalMacAddress, LocalImei, PhoneNum;
private Jh jh;
String strjhJson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initView();
        initData();

        tv_mac.setText(LocalMacAddress);
        tv_imei.setText(LocalImei);
        button_student_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uid = student_uid.getText().toString();
                sname = student_name.getText().toString();

                if (uid != null && sname != null) {


                    OkHttpClient client = new OkHttpClient();
                    RequestBody formBody = new FormBody
                            .Builder()
                            .add("fyhid", uid)
                            .add("fyhm", sname)
                            .add("flxdh", PhoneNum)
                            .add("fdhid", LocalImei)
                            .build();
                    Log.d("TAG", LocalMacAddress + "" + PhoneNum);
                    Request request = new Request.Builder()
                            .url("http://202.113.65.50:8080/imsoa/loginUserActivate.action")
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
                            strjhJson = response.body().string();
                            Log.d("TAG", response.protocol() + " " + response.code() + " " + response.message() + " " + strjhJson);
                            Gson gson = new Gson();
//                JsonParser jsonParser = new JsonParser();
//                JsonArray jsonElements = jsonParser.parse(strqdrqJson).getAsJsonArray();
//                for (JsonElement brhistory : jsonElements) {
                            jh = GSONTool.parseJSONWithGSON(strjhJson, Jh.class);
                            if (response.isSuccessful()){
                                Message msg = new Message();
                                msg.what = 1;
                                handler.sendMessage(msg);
                            }
//                            RegisterBack registerBack = GSONTool.parseJSONWithGSON_RegisterBack(response_body);


                        }
                    });

//                    Response response = null;
//                    try {
//                        response = client.newCall(request).execute();
//                        Log.d("TAG","call");
//                        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                    Log.d("TAG","ok");


//                    Call call = client.newCall(request);
//                    call.enqueue(new Callback() {
//                        @Override
//                        public void onFailure(Call call, IOException e) {
//                            Log.d("TAG","failure");
//                        }
//
//                        @Override
//                        public void onResponse(Call call, Response response) throws IOException {
//                            final String responseStr = response.body().string();
//
//                            Log.d("TAG","Response");
//                        }
//                    });


                } else {
                    Toast.makeText(RegisterActivity.this, "注册失败，请将所有信息填写完整", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }



    private void initView() {
        student_uid = findViewById(R.id.student_uid);

        student_name = findViewById(R.id.student_name);

        spinner_grade = findViewById(R.id.grade);
        spinner_classes = findViewById(R.id.classes);
        button_student_register = findViewById(R.id.btn_student_register);
        tv_mac = findViewById(R.id.tv_mac);
        tv_imei = findViewById(R.id.tv_imei);
    }
    public Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    Toast.makeText(RegisterActivity.this,jh.getMsg(),Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };
    private void initData() {
//        LocalMacAddress = getLocalMacAddress();
//        LocalImei = getImei();
//        PhoneNum = getPhoneNum();
        try {
            LocalMacAddress = PhoneService.getLocalMacAddress(getApplicationContext());
        } catch (SocketException e) {
            e.printStackTrace();
        }
        LocalImei = PhoneService.getImei(getApplicationContext());
        PhoneNum = PhoneService.getPhoneNum(getApplicationContext());
        tv_mac.setText(LocalMacAddress);
        tv_imei.setText(LocalImei);
        List<String> list_grade = new ArrayList<>();
        list_grade.add("2015");
        list_grade.add("2016");
        list_grade.add("2017");
        list_grade.add("2018");
        list_grade.add("2019");
        list_grade.add("2020");
        list_grade.add("2021");
        list_grade.add("2022");
        gradeArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list_grade);
        gradeArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_grade.setAdapter(gradeArrayAdapter);

        spinner_grade.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                ArrayAdapter<String> adapter = (ArrayAdapter<String>) adapterView.getAdapter();
                grade = adapter.getItem(position).toString();
                System.out.println(grade);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        List<String> list_classes = new ArrayList<>();
        list_classes.add("计科1班");
        list_classes.add("计科2班");
        list_classes.add("信安1班");
        list_classes.add("信安2班");
        list_classes.add("信安3班");
        list_classes.add("信安4班");
        list_classes.add("计算机1班");
        list_classes.add("计算机2班");
        list_classes.add("计算机3班");
        list_classes.add("计算机4班");
        list_classes.add("中加1班");
        list_classes.add("中加2班");
        list_classes.add("中加3班");
        list_classes.add("中加4班");
        list_classes.add("中加5班");
        list_classes.add("中加6班");
        classesArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, list_classes);
        //classesArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_classes.setAdapter(classesArrayAdapter);
        spinner_classes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                ArrayAdapter<String> adapter = (ArrayAdapter<String>) adapterView.getAdapter();
                classes = adapter.getItem(position).toString();
                System.out.println(classes);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }


}
