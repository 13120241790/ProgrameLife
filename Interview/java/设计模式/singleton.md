
//简化版 饿汉式
class Singleton{

    private static Singleton singleton = new Singleton();

    private Singleton(){

    }

    public static Singleton getInstance(){
        return singleton;
    }

}


//简化版 懒汉式
class Singleton{

    private static Singleton singleton;

    private Singleton(){

    }

    public static Singleton getInstance(){
        if(singleton == null){
            singleton = new Singleton();
        }
        return singleton;
    }

}

//懒汉式线程安全版  每次都需要维持同步安全 效率低下

class Singleton{

    private static Singleton singleton;

    private Singleton(){

    }

    public static synchronized Singleton getInstance(){
        if(singleton == null){
            singleton = new Singleton();
        }
        return singleton;
    }

}


//线程安全效率最高版 懒汉式
class Singleton{

    private static Singleton singleton;

    private Singleton(){

    }

    public static Singleton getInstance(){

        if(singleton == null){

            synchronized(Singleton.class){
                    if(singleton == null){
                        return new Singleton();
                }
            }
            
        }

        return singleton;
    }

}

