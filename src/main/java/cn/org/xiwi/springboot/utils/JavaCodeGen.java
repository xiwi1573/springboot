package cn.org.xiwi.springboot.utils;

import java.lang.reflect.Constructor;  
import java.lang.reflect.InvocationHandler;  
import java.lang.reflect.Method;  
import java.lang.reflect.Proxy;  
import java.util.LinkedList;  
import java.util.List;  
import java.util.Random;  
  
  
/** 
 * @author David Ding 
 * 
 */  
interface IJavaGen {  
    int getRandomInt();  
  
    void printClassInfo();  
}  
  
abstract class JavaGen implements IJavaGen {  
    protected List<String> calledMethods = new LinkedList<>(); // property  
  
    public int getRandomInt() { // Get a random integer  
        return new Random().nextInt(10000);  
    }  
  
    public abstract void printClassInfo();  
}  
  
class JavaGenHandler implements InvocationHandler {  
    private JavaGen mTarget;  
  
    public JavaGenHandler(JavaGen javaGen) {  
        mTarget = javaGen;  
    }  
  
    // 动态注入  
    @Override  
    public Object invoke(Object obj, Method method, Object[] params) throws Throwable {  
        Object ret = null;  
  
        String name = method.getName();  
        if (name.equals("getRandomInt")) { // intercept the getRandomInt  
            ret = method.invoke(mTarget, params); // call the base method  
            System.out.println("Print random int: " + ret);  
        } else if (name.equals("printClassInfo")) { // print class info  
            System.out.println("Class: " + obj.getClass());  
            method.invoke(mTarget, params);  
        }  
  
        mTarget.calledMethods.add(name); // change the property  
  
        return ret;  
    }  
  
}  
  
public class JavaCodeGen {  
    public static void main(String[] args) {  
        try {  
            Class<?> genClass = Proxy.getProxyClass(IJavaGen.class.getClassLoader(), IJavaGen.class);  
            Constructor<?> cons = genClass.getConstructor(InvocationHandler.class);  
            JavaGen target = new JavaGen() {  
  
                @Override  
                public void printClassInfo() {  
                    System.out.println("I have to implement this method! fuck!");  
                }  
            };  
            IJavaGen javaGen = (IJavaGen) cons.newInstance(new JavaGenHandler(target));  
            javaGen.getRandomInt();  
            javaGen.printClassInfo();  
            System.out.println(target.calledMethods);  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
    }  
}
