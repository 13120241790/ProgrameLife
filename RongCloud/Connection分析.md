# Connection分析

# RongIM 层
RongIM.connect()

1 判断是否已经 RongIM.init 或者 在主进程初始化
2 缓存本地 connect 用户 token 到 SharedPreferences
3 初始化用户信息数据库 RongUserInfoManager
4 调用 Lib 层 connect 接口且返回回调

# RongIMClient 层

维护了一个 单例 的 RongIMClient 引用
重要变量 mLibHandler 初始化 内部类 AidlConnection 实现 ServiceConnection 接口 

# private ConnectionStatusListener.ConnectionStatus mConnectionStatus 
1 mConnectionStatus 初始状态为 DISCONNECTED

SingletonHolder.sInstance.mLibHandler.connect(token, new IStringCallback.Stub() 调用的 LibHandlerStub connect 调用的 NativeClient connect 



# 需要注意的问题:

connect 的时候 SingletonHolder.sInstance.mLibHandler == null)  日志输出 [connect] mLibHandler is null, connect waiting for bind service
connect 没回调 IPC 进程没起来 Android Studio 断点也没有 :ipc 一般可能有两个原因:

1: 没有加载 so 文件 例如没有添加 或者 so 类型不齐全，或者 gradle 配置路径不对

2: manifest 配置 lib 相关服务广播不齐全

如果在 Android Studio 中的 logcat 没有查询到关键日志 例如 java.lang.UnsatisfiedLinkError: dalvik.system.PathClassLoader[DexPathList[[zip file "/data/app/rongcloud.connection-2/base.apk"],nativeLibraryDirectories=[/data/app/rongcloud.connection-2/lib/arm64, /data/app/rongcloud.connection-2/base.apk!/lib/arm64-v8a, /vendor/lib64, /system/lib64]]] couldn't find "libRongIMLib.so"


可去命令行窗口 adb logcat -d > fimename.txt 输出到本地文件再查看 此时的日志齐全一些

# connect 初始化准备 connect 代码执行顺序:

1 先走 RongIMClient 构造方法 优先走于 application 的 RongIM.init 

因为此处有个 恶汉式的单例 
 private static class SingletonHolder {
        static RongIMClient sInstance = new RongIMClient();
    }

静态的 class 在类装载的时候就已经加载到虚拟机了 
恶汉式的优点是加载速度快 不管用不用到都去 new 实例 没有线程安全隐患 懒汉式反之

在构造的时候 已经把 connect 相关的 
mStatusListener = new StatusListener();
 mConnectChangeReceiver = new ConnectChangeReceiver();
mAidlConnection = new AidlConnection();
做了相关构造 和 初始化

2 然后走 application 的 RongIMClinet.init 
SingletonHolder.sInstance.initBindService(); 关键方法
走了 此方法后 随即才会 走mLibHandler 里面实现的 onServiceConnected 
此时 mLibHandler 才获得实例  :ipc 进程才起来 

3 IPC 进程起来后 把 connect 连接状态的监听相继设置 文件和导航地址也设置
注册 ConnectChangeReceiver 广播
IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
            intentFilter.addAction(ConnectChangeReceiver.RECONNECT_ACTION);
            intentFilter.addAction(Intent.ACTION_USER_PRESENT);
            mContext.registerReceiver(mConnectChangeReceiver, intentFilter);


ConnectChangeReceiver 继承于  WakefulRongReceiver 其实就是把 Android 源码中的 WakefulBroadcastReceiver 抽取过来改个名字 作用是通过接收特定广播 intent 获取当前 network 变化 来启动 service 
例如: startWakefulService(context, new Intent(context, ReConnectService.class));

act=android.intent.action.USER_PRESENT 解锁
act=android.net.conn.CONNECTIVITY_CHANGE 网络变化
RECONNECT_ACTION = "action_reconnect" sdk 内部发送的重连的广播 action


由以上初始化相关 connect 我们可以分析出两点对

1 :ipc 进程在初始化的时候就已经起来，初始化后断点 Android studio 应该可以选择到 包名:ipc 
2 开发者问哪些监听是在初始化后设置 还是在 connect 后设置。由上面的代码分析明显可以得出清晰的结论 connectStatus 监听是在初始化后就可以设置

# NativeClient 层
NativeClient 层断点选中 :ipc
connect 方法中 
1 添加 NavigationObserver 观察者
2 调用  NavigationClient.getInstance().getCMPServerString(mContext, appKey, token);  传入当前需要连接的客户的 appkey 和 token 
如果是有缓存的 NavigationCacheHelper.isCacheValid 就直接从缓存中拿 并且通过 navigationObserver 把 cmp 数据  回调给 NativeClient 层 
如果没有缓存 则 调用 request 方法 post 请求去服务端重新获取 获取回来缓存一份到 SharedPreferences 且 回调给 NativeClinet 层

3 回到 NativeClient 层 将导航地址转换为 cmp 数组调用 tryConnect tryConnect 方法中解析 cmp 获得端口号 和 ip 随即调用 jni 层 protected native void Connect 方法 回调一个状态码 和 当前 connect userid 然后回调 一层一层向上传回去到 RongIM 层


# connectStatus connect状态的分析 每次切换网络观察 onStatusChange 都会输出当前状态 到 下一个状态的变化
例如 onStatusChange : cur = CONNECTED, to = NETWORK_UNAVAILABLE, retry = 0


# 自动重连分析 
通过 ConnectChangeReceiver 分析特定广播 和 网络变换等 start ReConnectService 服务 ReConnectService 服务内的 onHandleIntent 里面调用 RongIMClient 的 reconnect 方法 

ReconnectRunnable 重连会调用此线程 重连次数每次+1 此处还是去发广播到 ReconnectRunnable 去调 ReconnectRunnable 里面的重连

重连的监听 StatusListener  内 实现 onChanged 方法对各种 Status 做了各种处理 排查被踢 和 正在 connect (过滤频繁 connect 的情况) 调用 onStatusChange 方法先把状态通过 sConnectionListener.onChanged(status); 传给上层

mHandler.postDelayed(mReconnectRunnable, mReconnectInterval[mReconnectCount] * RECONNECT_INTERVAL);  多久后去执行这个线程

# IntentService是继承并处理异步请求的一个类，在IntentService内有一个工作线程来处理耗时操作，启动IntentService的方式和启动传统的Service一样，同时，当任务执行完后，IntentService会自动停止，而不需要我们手动去控制或stopSelf()。另外，可以启动IntentService多次，而每一个耗时操作会以工作队列的方式在IntentService的onHandleIntent回调方法中执行，并且，每次只会执行一个工作线程，执行完第一个再执行第二个，以此类推。Intent服务开启后，执行完onHandleIntent里面的任务就自动销毁结束，通过打印的线程名称可以发现是新开了一个线程来处理耗时操作的，即是耗时操作也可以被这个线程管理和执行，同时不会产生ANR的情况。
