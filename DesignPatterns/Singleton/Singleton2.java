public class Singleton2
{
	private static Singleton2 singleton;

	private Singleton2()
	{

	}
	
	public static Singleton2 getInstance()  //懒汉式 需要的时候才去 new 实例，多线程中有风险
	{
		if(singleton == null)
		{
			singleton = new Singleton2();
		}
		return singleton;
	}
}
