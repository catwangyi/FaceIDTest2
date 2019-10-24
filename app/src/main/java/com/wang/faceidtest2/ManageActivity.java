package com.wang.faceidtest2;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;

import com.wang.faceidtest2.HttpUtils.HttpUtil;
import com.wang.faceidtest2.Services.RunOnUI;
import com.wang.faceidtest2.Services.StaffItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ManageActivity extends AppCompatActivity {
    private List<StaffItem> mStaffItems;
    private ListView lv;
    private final String TAG = "ManageActivity";
    private CommonAdapter adapter;
    private String id;

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
        setContentView(R.layout.activity_manage);
        lv = findViewById(R.id.lv_staff);
        Intent intent = getIntent();
        id = (String)intent.getSerializableExtra("id");

        mStaffItems = new ArrayList<StaffItem>();

        HttpUtil.getstaff(getResources().getString(R.string.getstaff_addr), id, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                RunOnUI.Run(getApplicationContext(), "查询失败！");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String json = response.body().string();

                Log.i(TAG, "json数据："+json);

                try{
                    JSONArray jsonArray=new JSONArray(json);
                    for(int i=0;i<jsonArray.length();i++)
                    {
                        StaffItem item = new StaffItem();
                        JSONObject jsonObject=jsonArray.getJSONObject(i);
                        String id = jsonObject.getString("id");
                        String name = jsonObject.getString("name");
                        String status = jsonObject.getString("status");

                        item.setId(id);
                        item.setStatus(status);
                        item.setName(name);
                        mStaffItems.add(item);
                    }
                    Message msg = Message.obtain();
                    msg.what=1;
                    msg.obj = mStaffItems;
                    mHandler.sendMessage(msg);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });


    }

    private class CommonAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mStaffItems.size();
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
        public View getView(final int position, View convertView, ViewGroup parent) {
            View view;
            if (convertView==null){
                view = View.inflate(ManageActivity.this,R.layout.staffitem,null);
            }else {
                view = convertView;
            }
            final TextView tv_id = view.findViewById(R.id.staff_id);
            final CheckBox checkBox = view.findViewById(R.id.set_leader);
            final TextView tv_name = view.findViewById(R.id.staff_name);
            Button button = view.findViewById(R.id.delete);
            mStaffItems.get(position).setCheckBox(checkBox);
            mStaffItems.get(position).setDelete(button);
            tv_id.setText(mStaffItems.get(position).getId());
            tv_name.setText(mStaffItems.get(position).getName());

            if ("leader".equals(mStaffItems.get(position).getStatus())){
                checkBox.setChecked(true);
            }

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder=new AlertDialog.Builder(ManageActivity.this);
                    builder.setTitle("提醒：");
                    builder.setMessage("确定删除此人吗？");
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            HttpUtil.deletestaff(getResources().getString(R.string.getstaff_addr), mStaffItems.get(position).getId(), new Callback() {
                                @Override
                                public void onFailure(Call call, IOException e) {
                                    RunOnUI.Run(getApplicationContext(), "网络错误！");
                                }

                                @Override
                                public void onResponse(Call call, Response response) throws IOException {
                                    String state = response.header("state");
                                    if (state.equals("success")){
                                        //删除员工数据
                                        RunOnUI.Run(getApplicationContext(), "删除成功！");
                                        mStaffItems.remove(position);
                                        Message message = Message.obtain();
                                        message.obj = mStaffItems;
                                        message.what = 1;
                                        mHandler.sendMessage(message);
                                    }else {
                                        RunOnUI.Run(getApplicationContext(),"删除失败！" );
                                    }
                                }
                            });
                        }
                    });
                    builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    builder.show();

                }
            });

            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked){//选中
                        HttpUtil.setleader(getResources().getString(R.string.getstaff_addr), mStaffItems.get(position).getId(), new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                RunOnUI.Run(getApplicationContext(), "网络错误！");
                            }
                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                String state = response.header("state");
                                if (state.equals("success")){
                                    //设置成功
                                    RunOnUI.Run(getApplicationContext(), "设置成功！");
                                    checkBox.setChecked(true);
                                }else {
                                    RunOnUI.Run(getApplicationContext(),"设置失败！" );
                                }
                            }
                        });
                    }else{//取消选中
                        HttpUtil.setstaff(getResources().getString(R.string.getstaff_addr), mStaffItems.get(position).getId(), new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                RunOnUI.Run(getApplicationContext(), "网络错误！");
                            }
                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                String state = response.header("state");
                                if (state.equals("success")){
                                    //设置成功
                                    RunOnUI.Run(getApplicationContext(), "设置成功！");
                                    checkBox.setChecked(false);
                                }else {
                                    RunOnUI.Run(getApplicationContext(),"设置失败！" );
                                }
                            }
                        });
                    }
                }
            });

            return view;
        }
    }
}
