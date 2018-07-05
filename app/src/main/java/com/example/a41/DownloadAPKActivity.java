package com.example.a41;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import org.xutils.common.Callback;
import org.xutils.common.task.PriorityExecutor;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.File;
@ContentView(R.layout.activity_download_apk)
public class DownloadAPKActivity extends AppCompatActivity {
    public String url = "http://softfile.3g.qq.com:8080/msoft/179/24659/43549/qq_hd_mini_1.4.apk";
    @ViewInject(R.id.btn_apk)
    private Button btn;
    Callback.Cancelable cancelable;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);  //对于注解模块进行初始化
        setProgressDialog();
    }

    private void setProgressDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setIcon(R.mipmap.ic_launcher);
        progressDialog.setTitle("下载apk");
        progressDialog.setMessage("正在玩命下载中......");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setButton(ProgressDialog.BUTTON_NEGATIVE, "暂停", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                暂停下载
                cancelable.cancel();
            }
        });
    }

    @Event(value = R.id.btn_apk,type = View.OnClickListener.class)
    private void buttonClick(View view){
//        执行网络请求操作
        RequestParams params = new RequestParams(url);
        params.setAutoRename(false);  //设置是否根据头信息命名文件
        params.setAutoResume(true);  //设置文件下载断点续传
        params.setExecutor(new PriorityExecutor(2,true));  //设置加载图片的线程池
        params.setCancelFast(true);  //设置是否可以立即停止
//        设置缓存的路径
        String filePath = this.getExternalCacheDir()+ File.separator+"hello.apk";
        params.setSaveFilePath(filePath);
        cancelable = x.http().get(params, new Callback.ProgressCallback<File>() {
            @Override
            public void onSuccess(final File file) {
//                请求成功时，回调的方法
                AlertDialog.Builder builder = new AlertDialog.Builder(DownloadAPKActivity.this);
                builder.setTitle("提示信息").setMessage("下载成功，是否要安装apk？")
                            .setPositiveButton("确定",
                                    new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
//                              执行apk安装的步骤
                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                intent.setDataAndType(Uri.fromFile(file),
                                        "application/vnd.android.package-archive");
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                DownloadAPKActivity.this.startActivity(intent);
                            }
                        }).setNegativeButton("取消",null);
                builder.create().show();
            }
            @Override
            public void onError(Throwable throwable, boolean b) {
//              请求失败时，会执行的方法
            }
            @Override
            public void onCancelled(Callback.CancelledException e) {
//              请求被取消时，会执行的方法
                progressDialog.dismiss();
            }
            @Override
            public void onFinished() {
//              请求完成时，会执行的方法
                progressDialog.dismiss();
            }
            @Override
            public void onWaiting() {
            }
            @Override
            public void onStarted() {
//                开始网络请求时，会执行的方法
                progressDialog.show();
            }
            @Override
            public void onLoading(long total, long current, boolean b) {
//                  设置对话框当中的进度条
                progressDialog.setProgress((int)(current*100/total));
            }
        });
    }
}
