package com.wangkangli.roll_call.Fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.wangkangli.roll_call.Adapter.GgAdapter;
import com.wangkangli.roll_call.Json.Gg;
import com.wangkangli.roll_call.R;

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


public class NotifcationsFragment extends Fragment {
    private RefreshLayout refreshLayout;

    private ArrayList<Gg> ggs = new ArrayList<>();

    public NotifcationsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        return inflater.inflate(R.layout.fragment_notifcations, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        refreshLayout = getActivity().findViewById(R.id.refreshLayout);
        OkHttpClient client = new OkHttpClient();
        RequestBody formBody = new FormBody
                .Builder()
                .add("fyhid", "20185111")
                .build();
        Request request = new Request.Builder()
                .url("http://202.113.65.50:8080/imsoa/ggxw/wdispGgList.action")
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
                String strGgJson = response.body().string();
                Log.d("TAG", response.protocol() + " " + response.code() + " " + response.message()/* + " " + response.body().string()*/);
                Gson gson = new Gson();
                JsonParser jsonParser = new JsonParser();
                JsonArray jsonElements = jsonParser.parse(strGgJson).getAsJsonArray();

                for (JsonElement gg : jsonElements) {
                    Gg gg1 = gson.fromJson(gg, Gg.class);
                    ggs.add(gg1);
                }
//                            RegisterBack registerBack = GSONTool.parseJSONWithGSON_RegisterBack(response_body);


            }
        });

        RecyclerView recyclerView = getActivity().findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        GgAdapter adapter = new GgAdapter(ggs);
        recyclerView.setAdapter(adapter);


        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                OkHttpClient client = new OkHttpClient();
                RequestBody formBody = new FormBody
                        .Builder()
                        .add("fyhid", "20185111")
                        .build();
                Request request = new Request.Builder()
                        .url("http://202.113.65.50:8080/imsoa/ggxw/wdispGgList.action")
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
                        String strGgJson = response.body().string();
                        Log.d("TAG", response.protocol() + " " + response.code() + " " + response.message()+strGgJson/* + " " + response.body().string()*/);
                        Gson gson = new Gson();
                        JsonParser jsonParser = new JsonParser();
                        JsonArray jsonElements = jsonParser.parse(strGgJson).getAsJsonArray();
                        ggs.clear();
                        for (JsonElement gg : jsonElements) {
                            Gg gg1 = gson.fromJson(gg, Gg.class);
                            ggs.add(gg1);
                        }
//                            RegisterBack registerBack = GSONTool.parseJSONWithGSON_RegisterBack(response_body);


                    }
                });

                RecyclerView recyclerView = getActivity().findViewById(R.id.recyclerView);
                LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
                layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                recyclerView.setLayoutManager(layoutManager);
                if (recyclerView.getItemDecorationCount() == 0) {
                    recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
                }
                GgAdapter adapter = new GgAdapter(ggs);
                recyclerView.setAdapter(adapter);
                if (adapter != null) {
                    refreshlayout.finishRefresh(1000/*,false*/);//传入false表示刷新失败
                }
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshlayout) {
                refreshlayout.finishLoadMore(2000/*,false*/);//传入false表示加载失败
            }
        });
        super.onActivityCreated(savedInstanceState);
    }
}
