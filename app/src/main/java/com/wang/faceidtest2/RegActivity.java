package com.wang.faceidtest2;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.wang.faceidtest2.Common.Constants;
import com.wang.faceidtest2.HttpUtils.HttpUtil;
import com.wang.faceidtest2.Services.RunOnUI;

import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * @version $Rev$
 * @auther wangyi
 * @des ${TODO}
 * @updateAuther $Auther$
 * @updateDes ${TODO}
 */
public class RegActivity extends AppCompatActivity {
    private static final int TAKE_PHOTO = 1;
    private ImageView userimg;
    private EditText et_id;
    private EditText et_pwd;
    private EditText et_pwd_con;
    private EditText et_name;
    private EditText et_phone;
    private EditText et_email;
    private Button reg;
    private ProgressDialog mProgressDialog;
    private File imgsrc;
    private final  static  String TAG = "RegActivity";


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

        setContentView(R.layout.layout_reg);
        userimg = findViewById(R.id.userimg);
        et_id = findViewById(R.id.et_id_reg);
        et_pwd = findViewById(R.id.et_pwd_reg);
        et_pwd_con = findViewById(R.id.et_pwd_confirm_reg);
        et_name = findViewById(R.id.et_name_reg);
        et_phone = findViewById(R.id.et_phone_reg);
        et_email = findViewById(R.id.et_email_reg);
        reg = findViewById(R.id.reg);
        mProgressDialog = new ProgressDialog(RegActivity.this);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage("正在注册，请稍后...");
        //上传图片
        userimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = et_id.getText().toString();
                if (id.isEmpty()){
                    RunOnUI.Run(getApplicationContext(), "请先输入账号！");
                }
                else{
                    Intent intent = new Intent(getApplicationContext(),PreviewActivity.class);
                    intent.putExtra("id",id);
                    intent.putExtra("kind", "2");
                    startActivityForResult(intent,TAKE_PHOTO);
                }

            }
        });

        reg.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String id = et_id.getText().toString().trim();
                String pwd = et_pwd.getText().toString().trim();
                String pwd_con = et_pwd_con.getText().toString().trim();
                String name = et_name.getText().toString().trim();
                String phone = et_phone.getText().toString().trim();
                String email = et_email.getText().toString().trim();
                if ((!id.isEmpty())&&(!pwd.isEmpty())&&(!pwd.isEmpty())&&(!pwd_con.isEmpty())&&(!name.isEmpty())&&(!phone.isEmpty())&&(!email.isEmpty())){
                    if (pwd.equals(pwd_con)){

                        if (imgsrc!=null){
                            if (/*PhoneNumUtil.isMobileNumber(phone)*/true){

                                mProgressDialog.show();
                                HttpUtil.uploadImg(imgsrc, "5", id, name, pwd, phone, email, "reg", "http://"+ Constants.IP+getResources().getString(R.string.reg_addr), new Callback() {
                                    @Override
                                    public void onFailure(Call call, IOException e) {
                                        mProgressDialog.dismiss();
                                    }

                                    @Override
                                    public void onResponse(Call call, Response response) throws IOException {
                                        mProgressDialog.dismiss();
                                        String statu = response.header("statu");
                                        if ("success".equals(statu)){
                                            RunOnUI.Run(getApplicationContext(), "注册成功");
                                            finish();
                                        }else if ("exist".equals(statu)){
                                            RunOnUI.Run(getApplicationContext(), "账号已存在");
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
