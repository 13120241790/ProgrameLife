#Git增加gitignore忽略配置文件
通常我们在 github 上创建项目时候，会让我们自行选择根据语言生成忽略文件。忽略一些没有必要每次编译运行都会重新生成的一些文件 例如 class 文件。或者一些密钥不想在每次编辑操作git时候都被跟踪, 如果 git 没有为我们自动生成 我们需要学会创建并且知道如何去配置。

1 创建 .gitignore 文件。在项目 git 目录下执行 touch .gitignore 创建该隐藏文件 <br>
![image](https://github.com/13120241790/ProgrameLife/blob/master/Image/git1.png)<br>

2 配置忽略文件中我们需要忽略的内容 上图 cat .gitignore
- # 是注释
- *.class 的意思是忽略后缀为 class 的文件。class 为 Java编译 .java文件后生成的 class 文件需要被 Java 虚拟机加载, 开发者不需要关心他故此可以忽略
- bin/ or gen/ 忽略 ide 每次运行都会生成的文件夹中内容，通常这种文件夹内容体积不小。通过源码可以生成 上传没意义 故此忽略

3 测试效果
![image](https://github.com/13120241790/ProgrameLife/blob/master/Image/git2.png)<br>
![image](https://github.com/13120241790/ProgrameLife/blob/master/Image/git1.png)<br>
图2 中可以看到没有创建忽略文件时候 class 文件是被上传到 github 中了
图3 通过命令行操作可以看出
- 我把原本存在的 class 文件 通过 rm *.class 命令删除了
- 增加忽略文件后我用 javac 命令编译三个 java 文件，重新生成了三个 class 文件
- git status 命令想去查询这三个文件应该是 new file 新增文件的状态 , 但是 git 告诉我目前项目下是干净的状态

由此可得 创建配置 gitignore 文件生效。
