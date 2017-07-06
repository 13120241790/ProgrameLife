# RCE 分析

## 核心抽象类 ITask 

 必须要实现抽象方法 onInit 初始化数据库

param: 1 Context 上下文 2 EABConfig  数据库配置 3 TaskDispatcher 任务调度者

EABConfig 导航地址 文件服务器地址 appkey app server 地址的配置 (前面两项是给私有云使用)

继承 ITask 的类:

1. AuthTask 
包含与 app server 的接口交互 login 登录 logout 登出 send_code 发送验证码 verify 验证验证码 等

2. CacheTask
包含了对 userid account password token 的本地缓存

3 IMTask 
获取融云 kit 和 lib 的实例 ， 融云相关的监听 和 接口处理都在此类 
