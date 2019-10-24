package com.wang.faceidtest2;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.wang.faceidtest2.HttpUtils.HttpUtil;
import com.wang.faceidtest2.Services.RunOnUI;

import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class PersonActivity extends AppCompatActivity {
    private static final int TAKE_PHOTO = 1;
    private ImageView userimg;
    private EditText et_pwd;
    private TextView tv_id;
    private EditText et_pwd_con;
    private EditText et_name;
    private EditText et_phone;
    private EditText et_email;
    private Button change;
    private File imgsrc;
    private String pwd;
    private String email;
    private String phone;
    private String name;
    private String id;
    private String status;
    private String imgpath;
    private ProgressDialog mProgressDialog;
    private final  static  String TAG = "PersonActivity";


    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {

            super.handleMessage(msg);
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode){
            case TAKE_PHOTO:
                //如果处理拍照的话
                imgsrc = (File) data.getSerializableExtra("file_return");
                final Bitmap bitmap= BitmapFactory.decodeFile(imgsrc.getPath());
                userimg.setImageBitmap(bitmap);
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person);
        userimg = findViewById(R.id.userimg);
        tv_id = findViewById(R.id.et_id_reg);
        et_pwd = findViewById(R.id.et_pwd_reg);
        et_pwd_con = findViewById(R.id.et_pwd_confirm_reg);
        et_name = findViewById(R.id.et_name_reg);
        et_phone = findViewById(R.id.et_phone_reg);
        et_email = findViewById(R.id.et_email_reg);
        change = findViewById(R.id.change);
        final Intent intent=getIntent();
        id = (String)intent.getSerializableExtra("id");
        pwd = (String)intent.getSerializableExtra("pwd");
        Log.i(TAG, "密码："+pwd);
        Log.i(TAG,"头像文件路径"+imgpath );
        name = (String)intent.getSerializableExtra("name");
        phone = (String)intent.getSerializableExtra("phone");
        email = (String)intent.getSerializableExtra("email");
        imgpath = (String)intent.getSerializableExtra("imgpath");
        status = (String)intent.getSerializableExtra("status");

        tv_id.setText(id);
        et_pwd.setText(pwd);
        et_pwd_con.setText(pwd);
        et_name.setText(name);
        et_phone.setText(phone);
        et_email.setText(email);

        imgsrc = new File(imgpath);
        mProgressDialog = new ProgressDialog(PersonActivity.this);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage("正在修改，请稍后...");
        Bitmap bitmap = BitmapFactory.decodeFile(imgsrc.getPath());
        //userimg.setImageBitmap(bitmap);

        userimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),PreviewActivity.class);
                intent.putExtra("id",id);
                intent.putExtra("kind", "3");
                startActivityForResult(intent,TAKE_PHOTO);
            }
        });

        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pwd = et_pwd.getText().toString().trim();
                String pwd_con = et_pwd_con.getText().toString().trim();
                final String name = et_name.getText().toString().trim();
                final String phone = et_phone.getText().toString().trim();
                String email = et_email.getText().toString().trim();
                if ((!pwd.isEmpty())&&(!pwd.isEmpty())&&(!pwd_con.isEmpty())&&(!name.isEmpty())&&(!phone.isEmpty())&&(!email.isEmpty())){
                    if (pwd.equals(pwd_con)){

                        if (imgsrc!=null){
                            if (/*PhoneNumUtil.isMobileNumber(phone)*/true){

                                mProgressDialog.show();
                                HttpUtil.uploadImg(imgsrc, "5",id, name, pwd, phone, email,"update", getResources().getString(R.string.reg_addr), new Callback() {
                                    @Override
                                    public void onFailure(Call call, IOException e) {
                                        mProgressDialog.dismiss();
                                    }

                                    @Override
                                    public void onResponse(Call call, Response response) throws IOException {
                                        mProgressDialog.dismiss();
                                        String state = response.header("state");
                                        if ("success".equals(state)){
                                            RunOnUI.Run(getApplicationContext(), "修改成功");
                                            Intent intent = new Intent();
                                            intent.putExtra("name", name);
                                            intent.putExtra("phone", phone);
                                            setResult(2,intent);
                                            finish();
                                        }else{
                                            RunOnUI.Run(getApplicationContext(), "修改失败");
                                        }

                                    }
                                });
                            }else {
                                RunOnUI.Run(getApplicationContext(),"请输入正确的电话号码" );
                            }
                        }else{

                            RunOnUI.Run(getApplicationContext(), "请上传头像！");
                        }
                    }else{

                        RunOnUI.Run(getApplicationContext(),"输入密码不一致！" );
                    }
                }else{
                    RunOnUI.Run(getApplicationContext(),"输入不能为空！" );
                }
            }
        });


    }
}
