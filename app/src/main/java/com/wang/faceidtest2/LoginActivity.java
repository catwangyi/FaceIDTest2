package com.wang.faceidtest2;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.arcsoft.face.ErrorInfo;
import com.arcsoft.face.FaceEngine;
import com.wang.faceidtest2.Common.Constants;
import com.wang.faceidtest2.EncodeUtils.EncodeUtils;
import com.wang.faceidtest2.HttpUtils.DownloadUtil;
import com.wang.faceidtest2.HttpUtils.HttpUtil;
import com.wang.faceidtest2.Services.RunOnUI;
import com.wang.faceidtest2.Services.User;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Call;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {
//  private Button login;
    private Button reg;
    private EditText et_id_login;
    private EditText et_pwd_login;
    private static final int ACTION_REQUEST_PERMISSIONS = 0x001;
    private String id;
    private ProgressDialog mProgressDialog;
    private String pwd;
    private User user;
    private static final String[] NEEDED_PERMISSIONS = new String[]{
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.CAMERA,
            Manifest.permission.INTERNET,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };
    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;
    private CheckBox mCheckBox;
    private String imgpath;
    private Map mMap;
    private final String TAG="LoginActivity";

    @Override
    protected void onDestroy() {
        id = et_id_login.getText().toString();
        pwd = et_pwd_login.getText().toString();

        mEditor = mPreferences.edit();

        if (mCheckBox.isChecked()){
            //复选框被选中
            mEditor.putBoolean("remeber_pwd", true);
            mEditor.putString("id",id );
            mEditor.putString("pwd",pwd );
        }else {
            mEditor.clear();
        }
        mEditor.apply();
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //login = findViewById(R.id.login_ac);
        et_id_login = findViewById(R.id.et_id_login);
        reg = findViewById(R.id.reg);
        et_pwd_login = findViewById(R.id.et_pwd_login);
        mCheckBox = findViewById(R.id.remeber_pwd);
        mMap = new HashMap();
        mPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        boolean isRemeber = mPreferences.getBoolean("remeber_pwd",false);//boolean值为若找不到，返回的值
        mProgressDialog = new ProgressDialog(LoginActivity.this);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage("正在登录···");

        if (!checkPermissions(NEEDED_PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, NEEDED_PERMISSIONS, ACTION_REQUEST_PERMISSIONS);
            Log.i(TAG,"提前申请权限！" );
        }

        if (isRemeber){
            //将账号和密码都设置到文本框中
            String id = mPreferences.getString("id","" );
            String pwd = mPreferences.getString("pwd","" );
            et_id_login.setText(id);
            et_pwd_login.setText(pwd);
            mCheckBox.setChecked(true);
        }





    }

    private boolean checkPermissions(String[] neededPermissions) {
        if (neededPermissions == null || neededPermissions.length == 0) {
            return true;
        }
        boolean allGranted = true;
        for (String neededPermission : neededPermissions) {
            //ActivityCompat.requestPermissions(LoginActivity.this, new String[]{ neededPermission},1 );
            allGranted &= ContextCompat.checkSelfPermission(this, neededPermission) == PackageManager.PERMISSION_GRANTED;
        }
        return allGranted;
    }

    public void activeEngine(final View view){

        if (!checkPermissions(NEEDED_PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, NEEDED_PERMISSIONS, ACTION_REQUEST_PERMISSIONS);
            Log.i(TAG,"申请权限！" );
            return;
        }
        if (view != null) {
            view.setClickable(false);
        }
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                FaceEngine faceEngine = new FaceEngine();
                int activeCode = faceEngine.active(LoginActivity.this, Constants.APP_ID, Constants.SDK_KEY);
                emitter.onNext(activeCode);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Integer>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Integer activeCode) {
                if (activeCode == ErrorInfo.MOK) {
                    RunOnUI.Run(getApplicationContext(),getString(R.string.active_success));
                    Log.i(TAG,getString(R.string.active_success));
                } else if (activeCode == ErrorInfo.MERR_ASF_ALREADY_ACTIVATED) {
                    //RunOnUI.Run(getApplicationContext(),getString(R.string.already_activated));
                    Log.i(TAG, "already_activated"+getString(R.string.already_activated));
                } else {
                    RunOnUI.Run(getApplicationContext(),getString(R.string.active_failed, activeCode));
                    Log.i(TAG,"active faild" + getString(R.string.active_failed, activeCode));
                }

                if (view != null) {
                    view.setClickable(true);
                }
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }
    public void login(final View view) {
        id = et_id_login.getText().toString();
        pwd = et_pwd_login.getText().toString();

        new Thread(){
            @Override
            public void run() {
                Log.i(TAG, "开始下载！");
                DownloadUtil downloadUtil = new DownloadUtil();
                downloadUtil.download(getResources().getString(R.string.getp_addr),id,getExternalCacheDir().getPath(),id+".jpg", new DownloadUtil.OnDownloadListener() {
                    @Override
                    public void onDownloadSuccess(File file) {
                        //RunOnUI.Run(getApplicationContext(),"下载成功");
                        Log.i(TAG, "下载成功！"+file.getPath());
                    }
                    @Override
                    public void onDownloading(int progress) {
                    }
                    @Override
                    public void onDownloadFailed(Exception e) {
                    }
                });
            }
        }.start();

        if (true){
            if (!checkPermissions(NEEDED_PERMISSIONS)) {
                ActivityCompat.requestPermissions(LoginActivity.this, NEEDED_PERMISSIONS, ACTION_REQUEST_PERMISSIONS);
                return;
            }
            activeEngine(view);
            mProgressDialog.show();

            if (id.isEmpty()||pwd.isEmpty()){//输入不合法
                Toast.makeText(getApplicationContext(),"账号或密码不能为空！" ,Toast.LENGTH_SHORT).show();
                mProgressDialog.dismiss();
            }else if(id.length()>20||pwd.length()>20){//输入错误
                Toast.makeText(getApplicationContext(),"请输入正确的账号和密码！" ,Toast.LENGTH_SHORT).show();
                mProgressDialog.dismiss();
            }else{//输入合法
                //开始上传账号密码

                mMap.put("id",id);
                mMap.put("pwd",pwd);

                HttpUtil.login(getResources().getString(R.string.login_addr),mMap ,new okhttp3.Callback(){
                    @Override
                    public void onFailure(Call call, final IOException e) {
                        RunOnUI.Run(LoginActivity.this, "连接失败！");
                        Log.i(TAG, "连接失败！"+e.getMessage());
                        mProgressDialog.dismiss();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {

                        Log.i(TAG, "----------------------"+response.header("statu"));
                        String statu = response.header("statu");

                        //得到返回的登录结果
                        if("nosuchid".equals(statu)){
                            RunOnUI.Run(LoginActivity.this,"用户名不存在" );
                        }else if("wrongpwd".equals(statu)){
                            RunOnUI.Run(LoginActivity.this,"密码错误");
                        }else if ("success".equals(statu)){
                            //登录成功
                            String name = URLDecoder.decode(response.header("name"),"utf-8" );
                            Log.i(TAG,"name:"+name );
                            String phone = response.header("phone");
                            String status = response.header("status");
                            String pwd = response.header("pwd");
                            String email = response.header("email");
                            id = response.header("userid");
                            Log.i(TAG+"编码：", EncodeUtils.getEncoding(name));
                            mEditor = mPreferences.edit();
                            if (mCheckBox.isChecked()){
                                //复选框被选中
                                mEditor.putBoolean("remeber_pwd", true);
                                mEditor.putString("id",id);
                                mEditor.putString("pwd",pwd);
                            }else {
                                mEditor.clear();
                            }
                            mEditor.commit();
                            Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                            intent.putExtra("name", name);
                            intent.putExtra("userid", id);
                            intent.putExtra("email",email );
                            intent.putExtra("pwd",pwd );
                            Log.i(TAG,"密码："+pwd);
                            intent.putExtra("status",status);
                            intent.putExtra("phone",phone);
                            imgpath = getExternalCacheDir().getPath()+"/"+id+".jpg";
                            intent.putExtra("imgpath",imgpath );
                            startActivity(intent);
                            finish();
                            //mProgressDialog.dismiss();
                        }
                        mProgressDialog.dismiss();
                    }
                } );
            }
        }else{
            RunOnUI.Run(getApplicationContext(), "网络错误");
        }
    }

    public void reg(final View view){
        activeEngine(view);
        Intent intent = new Intent(getApplicationContext(),RegActivity.class);
        startActivity(intent);

    }



}
