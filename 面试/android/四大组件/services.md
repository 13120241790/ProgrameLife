## Service 
不需要界面可见
## Service 的声明方式
1. 继承 Service
2. manifest 注册这个 Service

## Service 的启动和停止

### 1

```
Intent startIntent = new Intent(this, MyService.class);  
             startService(startIntent); 
             
              Intent stopIntent = new Intent(this, MyService.class);  
             stopService(stopIntent);  
             
```

或者在服务中 stopSelf  方法来结束

### 2

Service和Activity之间通信使用 Bind Service

```

package com.example.servicetest;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class MyService extends Service {  
      
    public static final String TAG = "MyService";  
  
    private MyBinder mBinder = new MyBinder();  
  
    @Override  
    public void onCreate() {  
        super.onCreate();  
        Log.d(TAG, "onCreate");  
    }  
  
    @Override  
    public int onStartCommand(Intent intent, int flags, int startId) {  
        Log.d(TAG, "onStartCommand");  
        return super.onStartCommand(intent, flags, startId);  
    }  
  
    @Override  
    public void onDestroy() {  
        super.onDestroy();  
        Log.d(TAG, "onDestroy");  
    }  
  
    @Override  
    public IBinder onBind(Intent intent) {  
        return mBinder;  //在这里返回新建的MyBinder类
    }  
  
    //MyBinder类，继承Binder：让里面的方法执行下载任务，并获取下载进度
    class MyBinder extends Binder {  
  
        public void startDownload() {  
            Log.d("TAG", "startDownload() executed");  
            // 执行具体的下载任务  
        }
        public int getProgress(){
            Log.d("TAG", "getProgress() executed");  
            return 0;
        }
  
    }  
  
}

```


```

package com.example.servicetest;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity implements OnClickListener {

    private Button button1_start_service;
    private Button button2_stop_service;
    private Button button3_bind_service;
    private Button button4_unbind_service;

    private MyService.MyBinder myBinder;
    
    //匿名内部类：服务连接对象
    private ServiceConnection connection = new ServiceConnection() {
        
        //当服务异常终止时会调用。注意，解除绑定服务时不会调用
        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
        
        //和服务绑定成功后，服务会回调该方法
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            myBinder = (MyService.MyBinder) service;
            //在Activity中调用Service里面的方法
            myBinder.startDownload();
            myBinder.getProgress();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button1_start_service = (Button) findViewById(R.id.button1_start_service);
        button2_stop_service = (Button) findViewById(R.id.button2_stop_service);
        button3_bind_service = (Button) findViewById(R.id.button3_bind_service);
        button4_unbind_service = (Button) findViewById(R.id.button4_unbind_service);

        button1_start_service.setOnClickListener(this);
        button2_stop_service.setOnClickListener(this);
        button3_bind_service.setOnClickListener(this);
        button4_unbind_service.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.button1_start_service:
            Intent startIntent = new Intent(this, MyService.class);
            startService(startIntent);
            break;
        case R.id.button2_stop_service:
            Intent stopIntent = new Intent(this, MyService.class);
            stopService(stopIntent);
            break;
        case R.id.button3_bind_service:
            Intent bindIntent = new Intent(this, MyService.class);
            bindService(bindIntent, connection, BIND_AUTO_CREATE);
            break;
        case R.id.button4_unbind_service:
            unbindService(connection);
            break;

        default:
            break;
        }
    }

}

```

我们想解除Activity和Service之间的关联怎么办呢？调用一下unbindService()方法就可以了，这也是Unbind Service按钮的点击事件里实现的逻辑。


started服务与bind服务的区别：

区别一：生命周期

通过started方式的服务会一直运行在后台，需要由组件本身或外部组件来停止服务才会以结束运行
bind方式的服务，生命周期就要依赖绑定的组件
区别二：参数传递

started服务可以给启动的服务对象传递参数，但无法获取服务中方法的返回值
bind服务可以给启动的服务对象传递参数，也可以通过绑定的业务对象获取返回结果
 

## IntentService

服务中的代码默认运行在主线程中，如果直接在服务里执行一些耗时操作，容易造成ANR（Application Not Responding）异常，所以就需要用到多线程的知识了。

```

 7 public class MyService extends Service {  
 8       
 9     public static final String TAG = "MyService";   
10   
11     //服务执行的操作
12     @Override  
13     public int onStartCommand(Intent intent, int flags, int startId) {  
14         new Thread(new Runnable() {
15             public void run() {
16                 //处理具体的逻辑
17                 stopSelf();  //服务执行完毕后自动停止
18             }
19         }).start();        
20         return super.onStartCommand(intent, flags, startId);  
21     }
22 
23     @Override
24     public IBinder onBind(Intent intent) {
25         // TODO Auto-generated method stub
26         return null;
27     }      
28  
29 }

```

这个时候  Android 还提供了 IntentService 来更好的解决了这个问题
新建一个MyIntentService类，继承自IntentService，并重写父类的onHandleIntent()方法


AIDL 跨进程通讯的步骤
1 声明服务(manifest,类中)
2 编写 .AIDL 文件
3 生成.java文件 这个 java 文件是一个接口类(例如 A) 里面有 stub 内部抽象类需要去实现
4 服务中的 onBind 方法的返回值返回 A 的实例
5 业务类中去 bindService 这个服务 
 mContext.bindService(intent, connection, Context.BIND_AUTO_CREATE);
 6 ServiceConnection 的实现对象 connection 中的 方法返回 onServiceConnected 参数 IBinder service 然后 IHandler.Stub.asInterface(service) 获取到 A 的实例
 通讯建立完毕

1 同应用同进程
2 同应用不同进程 需要在 manifest 中指定 android:process 属性
3 不同应用 需要提供 intent filter 给其他 App 隐式调用


参考自: https://www.cnblogs.com/smyhvae/p/4070518.html

