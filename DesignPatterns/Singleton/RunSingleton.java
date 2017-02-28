public class RunSingleton
{
	public static void main(String [] args )
	{
		Singleton s = Singleton.getInstance();
		Singleton s2 = Singleton.getInstance();
		System.out.println(s == s2);

		Singleton2 s3 = Singleton2.getInstance();
		Singleton2 s4 = Singleton2.getInstance();
		System.out.println(s3.equals(s4));
	}

}
