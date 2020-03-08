package com.wangkangli.roll_call.Fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.wangkangli.roll_call.Json.Qd;
import com.wangkangli.roll_call.Json.Qdrq;
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

public class SignInFragment extends Fragment {
    Button btn_signin;
    public LocationClient locationClient;
    private MyLocationListener myLocationListener = new MyLocationListener();
    public double longitude; //经度
    public double latitude; //纬度
    String strqdJson;
    private TextView tv_qdstate;
    private Qd qd;
    @Nullable

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_signin, container, false);

        return view;

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        locationClient = new LocationClient(getContext().getApplicationContext());
        btn_signin = getActivity().findViewById(R.id.btn_signin);
        tv_qdstate = getActivity().findViewById(R.id.tv_qdstate);
        locationClient.registerLocationListener(new MyLocationListener());
        btn_signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                List<String> permissionList = new ArrayList<>();
                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
                }
                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                    permissionList.add(Manifest.permission.READ_PHONE_STATE);
                }
                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                }
                if (!permissionList.isEmpty()) {
                    String[] permissions = permissionList.toArray(new String[permissionList.size()]);
                    ActivityCompat.requestPermissions(getActivity(), permissions, 1);
                } else {
                    requestLocation();
                }
                Log.d("TAGweizhion", latitude + "," + longitude);
            }
        });

    }


    private void requestLocation() {
        Log.d("TAG request", "dianjihou");
        locationClient.start();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0) {
                    for (int result : grantResults) {
                        if (result != PackageManager.PERMISSION_GRANTED) {
                            Toast.makeText(getContext(), "必须同意本程序所有权限", Toast.LENGTH_SHORT).show();
                            getActivity().finish();
                            return;
                        }
                    }
                    requestLocation();
                } else {
                    Toast.makeText(getContext(), "未知错误", Toast.LENGTH_SHORT).show();
                    getActivity().finish();
                }
                break;
            default:
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
//        if (locationClient != null) {
//            locationClient.stop();
//        }

    }

    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            Log.d("qiandaowancheng", (String.valueOf(longitude)+String.valueOf(latitude)));
            double latitude = bdLocation.getLatitude();    //获取纬度信息
            double longitude = bdLocation.getLongitude();
            btn_signin.setText("正在上传");
            if (latitude!=0&&longitude!=0&&btn_signin.getText().toString().equals("正在上传")){
                OkHttpClient client = new OkHttpClient();
            RequestBody formBody = new FormBody
                    .Builder()
                    .add("fyhid", ShareUtils.getString(getContext(), "uid", ""))
                    .add("fdhid", PhoneService.getImei(getContext()))

                    .add("fqdjd",String.valueOf(longitude))
                    .add("fqdwd",String.valueOf(latitude))
                    .build();
            Request request = new Request.Builder()
                    .url("http://202.113.65.50:8080/imsoa/yhqd/wyhqd.action")
                    .post(formBody)
                    .build();
            Log.d("TAG", "签到"+ShareUtils.getString(getContext(), "uid", ""));

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.d("TAG", "onFailure: " + e.getMessage());

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    strqdJson = response.body().string();
                    Log.d("TAGqd", response.protocol() + " " + response.code() + " " + response.message() + " " + strqdJson/*+ " " + response.body().string()*/);
//                    Gson gson = new Gson();
//                    JsonParser jsonParser = new JsonParser();
//                    JsonArray jsonElements = jsonParser.parse(strqdJson).getAsJsonArray();
                    Gson gson = new Gson();
//                JsonParser jsonParser = new JsonParser();
//                JsonArray jsonElements = jsonParser.parse(strqdrqJson).getAsJsonArray();
//                for (JsonElement brhistory : jsonElements) {
                     qd = GSONTool.parseJSONWithGSON(strqdJson, Qd.class);
                    if (response.isSuccessful()&&qd.getSuccess().equals("true")){
                        Message msg = new Message();
                        msg.what = 1;
                        handler.sendMessage(msg);
                    }




                }
            });
            }
        }
    }
    public Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    tv_qdstate.setText("签到状态：\n" + qd.getMsg());
                    btn_signin.setText("上传成功");
                    break;
                default:
                    break;
            }
        }
    };

//

}
