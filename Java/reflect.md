reflect

反射是 Java 中运行期非编译器动态行为
反射在一定程度上可以打破类的封装性，在类的外部调用 private 的方法或属性

1. 反射获取类或者对象的 class 对象的方式


  ```

    1 Class<?> clazz = Class.forName("java.lang.Object"); 需要知  道包全名
	
	2 Class<?> clazz = Object.class 
	
	3 Obiect obj = new Obiect() obj.getClass();
   	
  ```
	
	
2. 通过类的 class 对象生成类的对象

	Object obj = clazz.newInstance();
	
3. 获取对应的方法

		clazz.getMethod()

	
	
4. 通过 invoke 访问对应类的方法
5. 获得对应的构造方法以及生成对应对象（构造方法接收参数）

		Constructor cons = class.getConstructor(new Class[]{});
		
		Object obj = cons.newInstance(new Object[]{});

6. 获取成员变量属性

		Field [] fields = clazz.getDeclaredFields();
		for(Field field : fields){
			field.getName(); 
		} 	
	
7. 访问私有方法
		
		A a = new A();
		Calss<?> clazz = a.getClass();
		
		Method method = clazz.getDeclaredMethod("方法名",new Class[]{String.class});		
		
		// new Class[]{String.class} 表示形参的个数与类型
		
		method.setAccessible(true); // 压制 Java 权限访问检查
		
		method.invoke(a,new Obiect[]{"111"});	
		
8. 访问私有属性成员变量

		A a = new A();
		 
		Calss<?> clazz = a.getClass();
		
		Field field = clazz.getDeclaredField("name"); //获取字段
		
		field.setAccessible(true);
		
		field.set(a,"aming");
		
