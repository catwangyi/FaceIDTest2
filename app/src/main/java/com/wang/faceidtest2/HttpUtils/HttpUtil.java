package com.wang.faceidtest2.HttpUtils;

import java.io.File;
import java.net.URLEncoder;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * @version $Rev$
 * @auther wangyi
 * @des ${TODO}
 * @updateAuther $Auther$
 * @updateDes ${TODO}
 */
public class HttpUtil {


    public static void login(String address, Map map,okhttp3.Callback callback){
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = new FormBody.Builder()
                .add("id",(String) map.get("id"))
                .add("pwd",(String)map.get("pwd") )
                .add("type", "login")
                .build();
        Request request = new Request.Builder()
                .url(address)
                .post(requestBody)
                .build();
        client.newCall(request).enqueue(callback);
    }



    /**
     * 上传经纬度
     * @param addr
     * @param j
     * @param w
     * @param callback
     */
    public static void uploadjwd(String addr,String j,String w,okhttp3.Callback callback){
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = new FormBody.Builder()
                .add("Latitude",j)
                .add("Longitude",w)
                .build();
        Request request = new Request.Builder()
                .url(addr)
                .post(requestBody)
                .build();
        client.newCall(request).enqueue(callback);
    }

    /**
     * 获得最近签到信息
     * @param addr
     * @param userid
     * @param callback
     */
    public static void getinfo(String addr,String userid,okhttp3.Callback callback){
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = new FormBody.Builder()
                .add("userid",userid)
                .build();
        Request request = new Request.Builder()
                .url(addr)
                .post(requestBody)
                .build();
        client.newCall(request).enqueue(callback);
    }


    /**
     * 获取员工信息
     * @param addr
     * @param userid
     * @param callback
     */
    public static void getstaff(String addr,String userid,okhttp3.Callback callback){
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = new FormBody.Builder()
                .add("userid",userid)
                .add("type","select")
                .build();
        Request request = new Request.Builder()
                .url(addr)
                .post(requestBody)
                .build();
        client.newCall(request).enqueue(callback);
    }

    public static void setleader(String addr,String userid,okhttp3.Callback callback){
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = new FormBody.Builder()
                .add("userid",userid)
                .add("type","setleader")
                .build();
        Request request = new Request.Builder()
                .url(addr)
                .post(requestBody)
                .build();
        client.newCall(request).enqueue(callback);
    }

    public static void setstaff(String addr,String userid,okhttp3.Callback callback){
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = new FormBody.Builder()
                .add("userid",userid)
                .add("type","setstaff")
                .build();
        Request request = new Request.Builder()
                .url(addr)
                .post(requestBody)
                .build();
        client.newCall(request).enqueue(callback);
    }


    public static void deletestaff(String addr,String userid,okhttp3.Callback callback){
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = new FormBody.Builder()
                .add("userid",userid)
                .add("type","delete")
                .build();
        Request request = new Request.Builder()
                .url(addr)
                .post(requestBody)
                .build();
        client.newCall(request).enqueue(callback);
    }

    /**
     * 验证时上传图片
     * @param file
     * @param id
     * @param url
     * @param callback
     */
    public static void uploadImg(File file,String id,String url, okhttp3.Callback callback){
       OkHttpClient okHttpClient = new OkHttpClient.Builder()
               .connectTimeout(5, TimeUnit.SECONDS)
               .readTimeout(60,TimeUnit.SECONDS )
               .writeTimeout(60,TimeUnit.SECONDS)
               .build();
        String filename;
        try {
            filename = URLEncoder.encode(file.getName(),"UTF-8");
            MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("imgname", filename)
                    .addFormDataPart("id", id)
                .addFormDataPart("img",filename,RequestBody.create(MediaType.parse("image/jpeg"),file ));
        RequestBody requestBody = builder.build();

        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        okHttpClient.newCall(request).enqueue(callback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 注册用
     * @param file
     * @param id
     * @param url
     * @param callback
     */
    public static void uploadImg(File file,String type,int regnum,String id,String url, okhttp3.Callback callback){
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .readTimeout(60,TimeUnit.SECONDS )
                .writeTimeout(60,TimeUnit.SECONDS)
                .build();
        String filename;
        try {
            filename = URLEncoder.encode(file.getName(),"UTF-8");
            MultipartBody.Builder builder = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("imgname", filename)
                    .addFormDataPart("type",type)
                    .addFormDataPart("id", id)
                    .addFormDataPart("regnum", String.valueOf(regnum))
                    .addFormDataPart("file",filename,RequestBody.create(MediaType.parse("image/jpeg"),file ));
            RequestBody requestBody = builder.build();

            Request request = new Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .build();
            okHttpClient.newCall(request).enqueue(callback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 注册时上传的文件
     * @param file
     * @param id
     * @param name
     * @param pwd
     * @param phone
     * @param email
     * @param type
     * @param url
     * @param callback
     */
    public static void uploadImg(File file,String id,String name ,String pwd,String phone,String email,String type,String url, okhttp3.Callback callback){
        String imageType = "multipart/form-data";
        RequestBody fileBody = RequestBody.create(MediaType.parse("image/jpg"), file);
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", "head_image", fileBody)
                .addFormDataPart("id", id)
                .addFormDataPart("pwd", pwd)
                .addFormDataPart("name", name)

                .addFormDataPart("type",type)
                .addFormDataPart("phone", phone)
                .addFormDataPart("email", email)
                .build();
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        final okhttp3.OkHttpClient.Builder httpBuilder = new OkHttpClient.Builder();
        OkHttpClient okHttpClient = httpBuilder
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10,TimeUnit.SECONDS )
                .writeTimeout(10,TimeUnit.SECONDS)
                .build();
        okHttpClient.newCall(request).enqueue(callback);
    }

    /**
     * 注册时上传的文件
     * @param file
     * @param id
     * @param name
     * @param pwd
     * @param regnum
     * @param phone
     * @param email
     * @param type
     * @param url
     * @param callback
     */
    public static void uploadImg(File file,String regnum,String id,String name ,String pwd,String phone,String email,String type,String url, okhttp3.Callback callback){
        String imageType = "multipart/form-data";
        RequestBody fileBody = RequestBody.create(MediaType.parse("image/jpg"), file);
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", "head_image", fileBody)
                .addFormDataPart("id", id)
                .addFormDataPart("pwd", pwd)
                .addFormDataPart("name", name)
                .addFormDataPart("regnum", regnum)
                .addFormDataPart("type",type)
                .addFormDataPart("phone", phone)
                .addFormDataPart("email", email)
                .build();
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        final okhttp3.OkHttpClient.Builder httpBuilder = new OkHttpClient.Builder();
        OkHttpClient okHttpClient = httpBuilder
                .connectTimeout(20, TimeUnit.SECONDS)
                .readTimeout(20,TimeUnit.SECONDS )
                .writeTimeout(20,TimeUnit.SECONDS)
                .build();
        okHttpClient.newCall(request).enqueue(callback);
    }
}
