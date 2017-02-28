#String
## String  又叫 字符串， 是 Java 中最常用的类。位于 java.lang 包下。
## String 是常量 Java 在内存中维护了一个 StringPool 池; 例如 "aaa"; 就相当于在 StringPool 创建这个常量;
## String s = "bbb"; String s1 = "bbb"; s1 不会再去创建，而是复用 StringPool 中已经有的 "bbb"
## String s = "sss"; String s = new String("sss"); 前者引用指向 StringPool; 后者先去 StringPool 找 "sss" ，如果没有则创建. 引用指向堆内存;
## 在 for 循环等大量拼接字符串的代码中建议不要直接 + 号拼接,以减小内存开销。  建议用 StringBuffer(速度慢，线程安全。适合多线程操作字符串使用) 或者 StringBuilder(速度快，线程不安全。适合单线程操作字符串使用) 的 append 方法;

## 关于 String 的 equals 方法，该方法重写了 Object 的 equals 方法，重写后效果具有比较两个字符串内容是否一致，代码如下:
public boolean equals(Object anObject) {
        if (this == anObject) { 判断两个地址值一样直接返回 true
            return true;
        }
        if (anObject instanceof String) { 判断不是字符串类型直接返回 false
            String anotherString = (String) anObject;  
	int n = value.length;   先比较两组字符串长度 不一致返回 false 再拆开每个字符串内容比较 完全一致返回 true
            if (n == anotherString.value.length) {
                char v1[] = value;
                char v2[] = anotherString.value;
                int i = 0;
                while (n-- != 0) {
                    if (v1[i] != v2[i])
                            return false;
                    i++;
                }
                return true;
            }
        }
        return false;
		}	   	
 
