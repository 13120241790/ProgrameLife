package cn.rongcloud.im.utils;

import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.text.TextUtils;
import android.util.Log;

import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;


/**
 * Created by AMing on 17/4/21.
 * Company RongCloud
 */

public class UploadImageUtils {

    public static final String TAG = "UploadImageUtils";

    /**
     * 共有云上传图片到七牛存储方法,需要和 app server 交互获取相关 token
     *
     * @param domain
     * @param imageToken
     * @param imagePath      本地 File://xxx.png or jpg
     * @param resultListener 使用层回调
     */
    public static void uploadImage(final String domain, String imageToken, Uri imagePath, final IResultListener resultListener) {
        if (resultListener == null) {
            throw new RuntimeException("upload callback is null!");
        }
        if (TextUtils.isEmpty(domain) && TextUtils.isEmpty(imageToken) && TextUtils.isEmpty(imagePath.toString())) {
            throw new RuntimeException("upload parameter is null!");
        }
        File imageFile = new File(imagePath.getPath());
        new UploadManager().put(imageFile, null, imageToken, new UpCompletionHandler() {

            @Override
            public void complete(String s, ResponseInfo responseInfo, JSONObject jsonObject) {
                if (responseInfo.isOK()) {
                    try {
                        String key = (String) jsonObject.get("key");
                        String resultUrl = "http://" + domain + "/" + key;
                        Log.e("uploadImage", resultUrl);
                        if (!TextUtils.isEmpty(resultUrl)) {
                            resultListener.onSuccess(resultUrl);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    resultListener.onError();
                }
            }
        }, null);
    }


    private static android.os.Handler handler;

    /**
     * 私有云上传图片方法
     *
     * @param imagePath           文件本地地址
     * @param domain              上传成功后,组拼图片 url 的 String 值
     * @param upload_IP           上传服务器地址
     * @param rongCloudImageToken 用户身份验证 (需要在上传请求中携带)
     * @param resultListener      使用层回调
     */
    public static void uploadPrivateImage(Uri imagePath, final String domain, final String upload_IP, final String rongCloudImageToken, final IResultListener resultListener) {
        if (resultListener == null) {
            throw new RuntimeException("upload callback is null!");
        }
        if (TextUtils.isEmpty(domain) && TextUtils.isEmpty(upload_IP) && TextUtils.isEmpty(rongCloudImageToken) && TextUtils.isEmpty(imagePath.toString())) {
            throw new RuntimeException("upload parameter is null!");
        }
        final File imageFile = new File(imagePath.getPath());
        if (handler == null) {
            HandlerThread uploadThread = new HandlerThread("uploadThread");
            uploadThread.start();
            handler = new Handler(uploadThread.getLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    uploadFile(imageFile, upload_IP, rongCloudImageToken, domain, resultListener);
                }
            });
        } else {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    uploadFile(imageFile, upload_IP, rongCloudImageToken, domain, resultListener);
                }
            });
        }
    }

    private static void uploadFile(File file, String RequestURL, String imageToken, String domain, final IResultListener resultListener) {
        int res;
        String result;
        String BOUNDARY = UUID.randomUUID().toString();
        String PREFIX = "--", LINE_END = "\r\n";
        String CONTENT_TYPE = "multipart/form-data";

        try {
            URL url = new URL(RequestURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(10000);
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Charset", "utf-8");
            conn.setRequestProperty("connection", "keep-alive");
            conn.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary=" + BOUNDARY);

            if (file != null) {
                DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
                dos.write(getFormData(imageToken, file.getName(), BOUNDARY).getBytes());
                InputStream is = new FileInputStream(file);
                byte[] bytes = new byte[1024];
                int len;
                while ((len = is.read(bytes)) != -1) {
                    dos.write(bytes, 0, len);
                }
                is.close();
                dos.write(LINE_END.getBytes());
                byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINE_END)
                        .getBytes();
                dos.write(end_data);
                dos.flush();
                res = conn.getResponseCode();
                Log.e(TAG, "response code:" + res);
                if (res == 200) {
                    Log.e(TAG, "request success");
                    InputStream input = conn.getInputStream();
                    StringBuffer sb1 = new StringBuffer();
                    int ss;
                    while ((ss = input.read()) != -1) {
                        sb1.append((char) ss);
                    }
                    result = sb1.toString();
                    JSONObject jo;
                    JSONObject path;
                    try {
                        jo = new JSONObject(result);
                        String rc_url = jo.getString("rc_url");
                        path = new JSONObject(rc_url);
                        String finalUrl = domain + path.getString("path");
                        resultListener.onSuccess(finalUrl);
                        Log.e(TAG, "rc_url:" + rc_url);
                        Log.e(TAG, "finalUrl:" + finalUrl);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Log.e(TAG, "result : " + result);
                } else {
                    Log.e(TAG, "request error");
                    resultListener.onError();
                }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
            resultListener.onError();
        } catch (IOException e) {
            e.printStackTrace();
            resultListener.onError();
        }
    }


    private static String getFormData(String token, String fileName, String Boundary) {
        String formData = "--";
        formData += Boundary;
        formData += "\r\nContent-Disposition: form-data; name=\"token\"\r\n\r\n";
        formData += token;
        formData += "\r\n--";
        formData += Boundary;
        formData += "\r\nContent-Disposition: form-data; name=\"key\"\r\n\r\n";
        formData += fileName;
        formData += "\r\n--";
        formData += Boundary;
        formData += "\r\nContent-Disposition: form-data; name=\"file\"; filename=\"";
        formData += fileName;
        formData += "\"\r\nContent-Type: ";
        formData += "image_jpeg";
        formData += "\r\n\r\n";

        return formData;
    }


    public interface IResultListener {
        void onSuccess(String resultUrl);

        void onError();
    }
}
