package com.wangkangli.roll_call.Fragments;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.wangkangli.roll_call.Adapter.HistoryAdapter;
import com.wangkangli.roll_call.Json.BRHistory;
import com.wangkangli.roll_call.R;
import com.wangkangli.roll_call.tools.ShareUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class HistoryFragment extends Fragment {
    private Button btn_chaxun;
    private String year, month;
    private Spinner spinner_month;
    EditText editText_year;
    String strBRHistoryJson;
    private ArrayList<BRHistory> brHistories = new ArrayList<>();
    private ArrayAdapter<String> monthArrayAdapter;

    public HistoryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_history, container, false);

        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        initData();
        btn_chaxun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OkHttpClient client = new OkHttpClient();
                RequestBody formBody = new FormBody
                        .Builder()
                        .add("fyhid", ShareUtils.getString(getContext(), "uid", ""))
                        .build();
                Request request = new Request.Builder()
                        .url("http://202.113.65.50:8080/imsoa/yhqd/wdispYhqdByIdList.action")
                        .post(formBody)
                        .build();
                Log.d("TAG", "本人签到情况");

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.d("TAG", "onFailure: " + e.getMessage());

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        strBRHistoryJson = response.body().string();
                        Log.d("TAG", response.protocol() + " " + response.code() + " " + response.message() + " " + strBRHistoryJson/*+ " " + response.body().string()*/);
                        Gson gson = new Gson();
                        JsonParser jsonParser = new JsonParser();
                        JsonArray jsonElements = jsonParser.parse(strBRHistoryJson).getAsJsonArray();

                        for (JsonElement brhistory : jsonElements) {
                            BRHistory brHistory = gson.fromJson(brhistory, BRHistory.class);
                            brHistories.add(brHistory);
                            if (response.isSuccessful()) {
                                Message msg = new Message();
                                msg.what = 1;
                                handler.sendMessage(msg);
                            }

                        }

                    }
                });
            }
        });
    }

    private void initView() {
        spinner_month = getActivity().findViewById(R.id.spinner_month);
        editText_year = getActivity().findViewById(R.id.et_year);
        btn_chaxun = getActivity().findViewById(R.id.btn_chaxun);
    }

    private void initData() {
        editText_year.setText(Integer.valueOf(Calendar.getInstance().get(Calendar.YEAR)).toString());

        List<String> list_month = new ArrayList<>();
        list_month.add("1");
        list_month.add("2");
        list_month.add("3");
        list_month.add("4");
        list_month.add("5");
        list_month.add("6");
        list_month.add("7");
        list_month.add("8");
        list_month.add("9");
        list_month.add("10");
        list_month.add("11");
        list_month.add("12");
        monthArrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, list_month);
        monthArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_month.setAdapter(monthArrayAdapter);
        spinner_month.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                ArrayAdapter<String> adapter = (ArrayAdapter<String>) adapterView.getAdapter();
                month = adapter.getItem(position).toString();
                System.out.println(month);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


    }

    public Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    RecyclerView historyrecyclerView = getActivity().findViewById(R.id.history_recyclerview);
                    LinearLayoutManager historylayoutManager = new LinearLayoutManager(getContext());
                    historylayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                    historyrecyclerView.setLayoutManager(historylayoutManager);
                    if (historyrecyclerView.getItemDecorationCount() == 0) {
                        historyrecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
                    }
                    HistoryAdapter wqdAdapter = new HistoryAdapter(brHistories);
                    historyrecyclerView.setAdapter(wqdAdapter);

                    break;
                default:
                    break;
            }
        }
    };

}
