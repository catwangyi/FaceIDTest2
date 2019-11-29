package com.wang.faceidtest2;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ZoomControls;

import com.arcsoft.face.FaceEngine;
import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.wang.faceidtest2.Common.Constants;
import com.wang.faceidtest2.HttpUtils.HttpUtil;
import com.wang.faceidtest2.LBSUtils.LBSUtils;
import com.wang.faceidtest2.Services.RunOnUI;
import com.wang.faceidtest2.util.ConfigUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    private static final int TAKE_PHOTO = 1;
    private static final int LOGIN_SUCCESS = 2;
    private static final int LOGIN_ERROR = 3;
    private static final String TAG = "MainActivity";
    private Uri imageUri;

    private ProgressDialog mProgressDialog;
    private String imagePath_take;
    public LocationClient mLocationClient;
    private MapView mMapView;
    private BaiduMap baiduMap;
    private FloatingActionButton mFloatingActionButton;
    private boolean isFirstLocate = true;//用来显示是否是第一次定位
    private DrawerLayout mDrawerLayout;
    private String name;
    private ImageView imageView;
    private BDLocation mBDLocation;
    private String pwd;
    private TextView username_tv;
    private String email;
    private String phone;
    private String id;
    private String status;
    private String imgpath;
    private TextView userphone_tv;
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1://刚开始
                    break;
                case 2:
                    Bitmap bitmap = (Bitmap) msg.obj;
                    Log.i(TAG, "设置头像！");
                    imageView.setImageBitmap(bitmap);
                    break;

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());//这句话需要加在setContentView之前
        setContentView(R.layout.activity_main);
        Toolbar toolbar =findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);//禁止手动滑动弹出
        //mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);//打开手动滑动弹出
        ActionBar actionBar = getSupportActionBar();
        final Intent intent=getIntent();
        name = (String)intent.getSerializableExtra("name");
        id = (String)intent.getSerializableExtra("userid");
        imgpath = (String)intent.getSerializableExtra("imgpath");
        status = (String)intent.getSerializableExtra("status");//职位
        email = (String)intent.getSerializableExtra("email");
        phone = (String)intent.getSerializableExtra("phone");
        pwd = (String)intent.getSerializableExtra("pwd");
        Log.i(TAG,"密码："+pwd );

        Log.i(TAG,"status"+status );
        Log.i(TAG,"userid"+id );
        final NavigationView navigationView = findViewById(R.id.nav_view);//NavigationView

        //设置Header内容
        View headerView= navigationView.getHeaderView(0);
        username_tv = headerView.findViewById(R.id.username);
        username_tv.setText(name);
        userphone_tv = headerView.findViewById(R.id.phone);
        imageView = headerView.findViewById(R.id.id_avatar);

        userphone_tv.setText((String)intent.getSerializableExtra("phone"));
        Log.i(TAG+"名字", name);
        if (actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);//菜单按钮
        }

        if (!"staff".equals(status)){
            navigationView.getMenu().findItem(R.id.manage).setVisible(true);
            navigationView.getMenu().findItem(R.id.tj).setVisible(true);
        }

        //navigationView.setCheckedItem(R.id.nav_log);//设置默认选项
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener(){

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {//处理按钮事件；
                Intent intent1;
                switch (menuItem.getItemId()){
                    case R.id.person_details:
                        Intent intent = new Intent(getApplicationContext(),PersonActivity.class);
                        intent.putExtra("id", id);
                        intent.putExtra("imgpath",imgpath);
                        intent.putExtra("name", name);
                        intent.putExtra("userid", id);
                        intent.putExtra("email",email );
                        intent.putExtra("pwd",pwd );
                        intent.putExtra("status",status);
                        intent.putExtra("phone",phone);
                        startActivityForResult(intent,2);
                        break;
                    case R.id.nav_log://最近记录
                        intent1 = new Intent(getApplicationContext(), CheckInfoActivity.class);
                        intent1.putExtra("id",id );
                        startActivity(intent1);
                        break;
                    case R.id.logout://退出
                        intent1 = new Intent(getApplicationContext(),LoginActivity.class);
                        startActivity(intent1);
                        finish();
                        break;
                    case R.id.manage:
                        intent1 = new Intent(getApplicationContext(), ManageActivity.class);
                        intent1.putExtra("id",id );
                        startActivity(intent1);
                        break;
                    case R.id.tj:
                        intent1 = new Intent(getApplicationContext(),tjActivity.class);
                        startActivity(intent1);
                        break;
                }
                //关闭滑动菜单
                //mDrawerLayout.closeDrawers();
                return true;
            }
        });

        ConfigUtil.setFtOrient(getApplicationContext(), FaceEngine.ASF_OP_0_HIGHER_EXT);
        mProgressDialog = new ProgressDialog(MainActivity.this);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage("正在上传图片，请稍后...");
        mFloatingActionButton = findViewById(R.id.login);

        mLocationClient = new LocationClient(getApplicationContext());
        mLocationClient.registerLocationListener(new MyLocationListener());

        mMapView= findViewById(R.id.bdmapView);
        //隐藏baidu的logo
        View child = mMapView.getChildAt(1);
        if (child != null && (child instanceof ImageView || child instanceof ZoomControls)){
            child.setVisibility(View.INVISIBLE);
        }
        mMapView.showZoomControls(false);//放大缩小控件
        mMapView.showScaleControl(true);//比例尺控件

        baiduMap = mMapView.getMap();
        baiduMap.setMyLocationEnabled(true);

        List<String> permissionList = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_PHONE_STATE)!= PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!permissionList.isEmpty()){
          String[] permissions = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(MainActivity.this,permissions ,1 );
        }else{
            requestLocation();
        }



        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),PreviewActivity.class);
                intent.putExtra("id", id);
                intent.putExtra("kind", "1");
                startActivityForResult(intent,TAKE_PHOTO);
            }
        });
    }



    private void requestLocation(){
        LocationClientOption option = new LocationClientOption();
        option.setScanSpan(1000);
        option.setOpenGps(true); // 打开gps
        option.setCoorType("bd09ll");//需要设置坐标偏移标准，否则定位不准确,后面是字母L
        mLocationClient.setLocOption(option);
        mLocationClient.start();//打开地图定位图层
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar,menu );
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.refresh:
                isFirstLocate = true;
                mLocationClient.start();//重新定位
                break;
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;
        }
        return true;
    }

    private void navigateTo(BDLocation location){
        if(location!=null){
            if (isFirstLocate){
                LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude() );
                Log.i(TAG, LBSUtils.locationUpdates(location));
                MapStatusUpdate update = MapStatusUpdateFactory.newLatLng(latLng);
                baiduMap.animateMapStatus(update);
                update = MapStatusUpdateFactory.zoomTo(18f);
                baiduMap.animateMapStatus(update);//设置地图位置
                isFirstLocate = false;
            }
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(location.getDirection())
                    .latitude(location.getLatitude())//纬度
                    .longitude(location.getLongitude())//经度
                    .build();
            baiduMap.setMyLocationData(locData);
        }else{
            Toast.makeText(getApplicationContext(), "没有获取到定位信息！", Toast.LENGTH_SHORT).show();
        }

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLocationClient.stop();
        mMapView.onDestroy();
        baiduMap.setMyLocationEnabled(false);
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
        Log.i(TAG, "执行onResume");
        navigateTo(mBDLocation);
    }

    @Override
    public void onPause(){
        super.onPause();
        mMapView.onPause();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1:
                if (grantResults.length>0){
                    for (int result:grantResults){
                        if (result!=PackageManager.PERMISSION_GRANTED){
                            Toast.makeText(MainActivity.this,"必须同意所有的权限才能使用本程序！" ,Toast.LENGTH_SHORT).show();
                            finish();
                            return;
                        }
                    }
                    requestLocation();
                }else{
                    Toast.makeText(MainActivity.this,"未知错误！" ,Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            default:
        }
    }

    @Override
    public void onBackPressed() {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Log.i(TAG,"requestCode"+requestCode );
        Log.i(TAG,"resultCode"+resultCode );
        switch (requestCode){
            case TAKE_PHOTO://拍照
                if (resultCode == RESULT_OK){
                    try{
                        mProgressDialog.show();
                        final File src = (File) data.getSerializableExtra("file_return");
                        Log.i(TAG, "上传的经纬度"+LBSUtils.locationUpdates(mBDLocation));

                        HttpUtil.uploadjwd("http://"+ Constants.IP+getResources().getString(R.string.jwd_addr), ""+mBDLocation.getLongitude(), ""+mBDLocation.getLatitude(), new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                Log.i(TAG,"上传经纬度失败："+e.getMessage() );
                                RunOnUI.Run(getApplicationContext(), "上传失败");
                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                String statu = response.header("statu");
                                Log.i(TAG,"经纬度反馈："+statu );
                                //在范围内
                                if (!TextUtils.isEmpty(statu)){
                                    HttpUtil.uploadImg(src,statu,id,"http://"+Constants.IP+getResources().getString(R.string.upload_addr), new Callback() {
                                        @Override
                                        public void onFailure(Call call, IOException e) {
                                            RunOnUI.Run(getApplicationContext(),"上传失败"+e.getMessage());
                                            Log.i(TAG,"上传失败"+e.getMessage());
                                            Log.i(TAG,"文件名："+src.getName() );
                                            mProgressDialog.dismiss();
                                        }

                                        @Override
                                        public void onResponse(Call call, Response response) {
                                            Log.i(TAG,"上传成功");
                                            Log.i(TAG,"文件名："+src.getName() );
                                            //是否是本人
                                            String statu = response.header("statu");
                                            if ("success".equals(statu)){
                                                RunOnUI.Run(getApplicationContext(),"签到成功!");
                                            }else{
                                                RunOnUI.Run(getApplicationContext(),"签到失败，请洗脸！");
                                            }
                                            mProgressDialog.dismiss();
                                        }
                                    });
                                }else{
                                    //不在范围内
                                    RunOnUI.Run(getApplicationContext(),"请在允许范围内签到！" );
                                    mProgressDialog.dismiss();
                                }
                            }
                        });

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
                    break;
            case 2:
                if (resultCode==RESULT_OK){
                    Log.i(TAG, "修改MainACctivity中的信息");
                    name =(String) data.getSerializableExtra("name");
                    phone =(String) data.getSerializableExtra("phone");
                    email = (String)data.getSerializableExtra("email");
                    pwd = (String)data.getSerializableExtra("pwd");
                    username_tv.setText(name);
                    userphone_tv.setText(phone);
                    break;
                }
        }
    }



    /**
     * 定位
     */
    public class MyLocationListener extends BDAbstractLocationListener {
    @SuppressLint("RestrictedApi")
    @Override
    public void onReceiveLocation(BDLocation bdLocation) {
        mBDLocation = bdLocation;
        if (bdLocation.getLocType()==BDLocation.TypeGpsLocation||bdLocation.getLocType()==BDLocation.TypeNetWorkLocation){
            mFloatingActionButton.setVisibility(View.VISIBLE);
            mBDLocation = bdLocation;
            navigateTo(bdLocation);
            Log.i(TAG, LBSUtils.locationUpdates(bdLocation));
        }else {
            if (!isFirstLocate){
                Toast.makeText(getApplicationContext(), "发生未知错误，定位失败！", Toast.LENGTH_SHORT).show();
                Log.i(TAG,"发生未知错误，定位失败！" );
            }
        }
    }

}

}
