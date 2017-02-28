public class Singleton
{
	private Singleton(){}

	private static  Singleton singleton = new Singleton(); //饿汉式 因为是静态的成员变量 在java代码被编译成 class 文件被java 虚拟机装载时这个//对象的实例已经被创建, 不管使用还是不使用。

	public static Singleton getInstance()
	{

	return singleton;
	
	}


}
