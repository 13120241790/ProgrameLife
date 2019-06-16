## Activity 

## Activity 的四个状态

running  
活动状态，用户可以点击屏幕并作出响应，处于Activity栈顶

paused  
失去焦点，无法与用户交互，被非全屏或透明Activity占据焦点

stopped  
不可见，被其他Activity完全覆盖

killed 
被回收掉

## Activity 的生命周期

onCreate() 创建活动 

onStart() 可见不可交互 

onResume() 可交互 

onPause() 暂停状态 

onStop() 停止 

onDestroy() 正在被销毁 

onRestart() 不可见变为可见 

## Activity 的任务栈

android任务栈又称为Task，它是一个栈结构，具有后进先出的特性，用于存放我们的Activity组件。

我们每次打开一个新的Activity或者退出当前Activity都会在一个称为任务栈的结构中添加或者减少一个Activity组件，因此一个任务栈包含了一个activity的集合, android系统可以通过Task有序地管理每个activity，并决定哪个Activity与用户进行交互:只有在任务栈栈顶的activity才可以跟用户进行交互。

在我们退出应用程序时，必须把所有的任务栈中所有的activity清除出栈时,任务栈才会被销毁。当然任务栈也可以移动到后台, 并且保留了每一个activity的状态. 可以有序的给用户列出它们的任务, 同时也不会丢失Activity的状态信息。

需要注意的是，一个App中可能不止一个任务栈，某些特殊情况下，单独一个Actvity可以独享一个任务栈。还有一点就是一个Task中的Actvity可以来自不同的App，同一个App的Activity也可能不在一个Task中。

### launchMode

1. Standard（默认）
每次新创建Activity，压入栈顶

2. SingleTop（栈顶复用）
检测任务栈顶是否存在该Activity，若存在则直接复用，调用 onNewIntent() 方法

3. SingleTask（栈内复用）
检测整个任务栈是否存在Activity，若存在则置于栈顶，并把栈上部的其他Activity销毁

4. SingleInstance（单一实例模式）
以singleInstance模式启动的Activity具有全局唯一性，即整个系统中只会存在一个这样的实例。
以singleInstance模式启动的Activity在整个系统中是单例的，如果在启动这样的Activiyt时，已经存在了一个实例，那么会把它所在的任务调度到前台，重用这个实例。
以singleInstance模式启动的Activity具有独占性，即它会独自占用一个任务，被他开启的任何activity都会运行在其他任务中。
被singleInstance模式的Activity开启的其他activity，能够在新的任务中启动，但不一定开启新的任务，也可能在已有的一个任务中开启。



## Activity状态保存与恢复


Activity提供了onSaveInstanceState()回调方法，这个方法可以保证在活动回收之前被调用， 
onSaveInstanceState()方法会携带一个Bundle类型的参数，Bundle提供了一些方法用于保存数据

```

 @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("data", "保存数据");
    }
    
 ```

保存的数据在onCreate()方法中可获取

```

 protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(savedInstanceState!=null){
        String data = savedInstanceState.getString("data");
        }
 }
```


## Activity 的启动流程
1. 启动分为两种 从系统桌面启动 和 应用内启动。系统桌面启动实质上是从一个应用的 activity 进入另外一个应用的 activity
2. 启动 Activity 的任务都是交给 ApplicationThread 来处理。
3. StartActivity 最终都是调到 startActivityForResult 只是 requestCode 传的 -1 
4. ApplicationThread ActivityThread ActivityManagerService... 未完待续

> https://blog.csdn.net/pihailailou/article/details/78545391

## Activity 与 Fragment 的通信

1. 接口回调
2. eventbus
3. Fragment 调用 Activity 可以通过 getActivity
4. Activity 调用 Fragment 获得fragment的引用要用FragmentManager，之后可以调用findFragmentById() 或者 findFragmentByTag()。

## Fragment 的生命周期
1. 
2. onAttach() 和 onCreate() 只在 Fragment 与 Activity 第一次关联时调用。
3. onDestroy() 和 onDetach() 只在 Fragment 的宿主 Activity 销毁时才会被调用。