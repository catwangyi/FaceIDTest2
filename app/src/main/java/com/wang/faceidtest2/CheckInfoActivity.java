package com.wang.faceidtest2;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.wang.faceidtest2.HttpUtils.HttpUtil;
import com.wang.faceidtest2.Services.InfoItem;
import com.wang.faceidtest2.Services.RunOnUI;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class CheckInfoActivity extends AppCompatActivity {
    private List<InfoItem> mInfoItems;
    private ListView lv;
    private final String TAG = "CheckInfoActivity";
    private CommonAdapter adapter;
    private String userid;
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    if (adapter==null){
                        adapter=new CommonAdapter();
                        lv.setAdapter(adapter);
                    }else{
                        //通知数据适配器更新数据，而不是new新的数据适配器
                        adapter.notifyDataSetChanged();
                    }
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_info);
        lv=findViewById(R.id.lv);
        Intent intent = getIntent();
        userid = (String)intent.getSerializableExtra("userid");
        Log.i(TAG,"userid:" +userid);

        mInfoItems = new ArrayList<InfoItem>();

        HttpUtil.getinfo(getResources().getString(R.string.getinfo_addr), userid, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                RunOnUI.Run(getApplicationContext(),"请求失败！" );
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                InfoItem item = new InfoItem();
                //处理结果

                String json = response.body().string();

                Log.i(TAG, "json数据："+json);

                try{
                    JSONArray jsonArray=new JSONArray(json);
                    for(int i=0;i<jsonArray.length();i++)
                    {
                        JSONObject jsonObject=jsonArray.getJSONObject(i);
                        String time=jsonObject.getString("time");
                        String statu=jsonObject.getString("statu");
                        Log.i(TAG,"时间："+time );
                        Log.i(TAG,"状态"+statu );
                        item.setTime(time);
                        item.setStatus(statu);
                        mInfoItems.add(item);
                    }
                    Message msg = Message.obtain();
                    msg.what=1;
                    msg.obj = mInfoItems;
                    mHandler.sendMessage(msg);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }



    private class CommonAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return mInfoItems.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;
            if (convertView==null){
                view = View.inflate(CheckInfoActivity.this,R.layout.infoitem,null);
            }else {
                view = convertView;
            }
            final TextView tv_infoitem_time = view.findViewById(R.id.time);
            final TextView tv_infoitem_status = view.findViewById(R.id.status);

            tv_infoitem_time.setText(mInfoItems.get(position).getTime().toString());
            tv_infoitem_status.setText(mInfoItems.get(position).getStatus().toString());
            return view;
        }
    }
}
