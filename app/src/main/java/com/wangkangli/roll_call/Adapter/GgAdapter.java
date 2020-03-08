package com.wangkangli.roll_call.Adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.wangkangli.roll_call.Json.Gg;
import com.wangkangli.roll_call.Json.GgInfo;
import com.wangkangli.roll_call.R;
import com.wangkangli.roll_call.WklApplication;
import com.wangkangli.roll_call.tools.GSONTools.GSONTool;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class GgAdapter extends RecyclerView.Adapter<GgAdapter.ViewHolder> {
    private List<Gg> mGgList;
    String strGgInfoJson;
    int flag = 0;
    private Context context;

    static class ViewHolder extends RecyclerView.ViewHolder {
        View ggView;
        TextView ggTitle;
        TextView ggDate;
        TextView ggAuthor;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ggView = itemView;
            ggTitle = itemView.findViewById(R.id.gg_title);
            ggDate = itemView.findViewById(R.id.gg_date);
            ggAuthor = itemView.findViewById(R.id.gg_author);

        }
    }

    public GgAdapter(List<Gg> ggList) {
        mGgList = ggList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull final ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.gg_item, viewGroup, false);
        final ViewHolder holder = new ViewHolder(view);
        context = viewGroup.getContext();
        holder.ggView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {


                int position = holder.getAdapterPosition();
                Gg gg = mGgList.get(position);
                OkHttpClient client = new OkHttpClient();
                RequestBody formBody = new FormBody
                        .Builder()
                        .add("id", gg.getId())
                        .build();
                Request request = new Request.Builder()
                        .url("http://202.113.65.50:8080/imsoa/ggxw/wdispGgxwInfoById.action")
                        .post(formBody)
                        .build();
                Log.d("TAG", "获取详细信息");

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.d("TAG", "onFailure: " + e.getMessage());
                        flag = 0;
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        strGgInfoJson = response.body().string();
                        Log.d("TAG", response.protocol() + " " + response.code() + " " + response.message()/*+ " " + response.body().string()*/);
                        if (response.isSuccessful()) {
                            Message msg = new Message();
                            msg.what = 1;
                            handler.sendMessage(msg);
                        }
                        flag = 1;
                    }
                });






                Toast.makeText(v.getContext(), "youclick:" + gg.getId(), Toast.LENGTH_SHORT).show();
            }
        });

        return holder;
    }

    public Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    GgInfo ggInfo = GSONTool.parseJSONWithGSON(strGgInfoJson, GgInfo.class);
                    Log.d("TAG", "主体部分" + ggInfo.getFggnr());
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("公告");
                    View view = LayoutInflater.from(context).inflate(R.layout.gginfo_view, null);

                    TextView textView = view.findViewById(R.id.tv_gginfo);
                    String ggInfoMian = ggInfo.getFggnr();

                    textView.setText(Html.fromHtml(ggInfoMian));

                    builder.setView(view);
                    AlertDialog dialog = builder.show();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Gg gg = mGgList.get(i);
        viewHolder.ggTitle.setText(gg.getFggbt());
        viewHolder.ggDate.setText(gg.getFfbrq());
        viewHolder.ggAuthor.setText(gg.getFyhm());
    }


    @Override
    public int getItemCount() {
        return mGgList.size();
    }
}
