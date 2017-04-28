package cn.org.xiwi.springboot.redis.utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Properties;

public class FileUtil {
	public static String getPropertyValue(String name,String key){
		return null;
	}
	
	public static int getPropertyValueInt(String name,String key) {
		return 0;
	}
	
	public static Boolean getPropertyValueBoolean(String name,String key) {
		return true;
	}
	
	
//	public static void main(String[] args) { 
//	     Properties prop = new Properties();     
//	     try{
//	         //读取属性文件a.properties
//	         InputStream in = new BufferedInputStream(new FileInputStream("a.properties"));
//	         prop.load(in);     ///加载属性列表
//	         Iterator<String> it=prop.stringPropertyNames().iterator();
//	         while(it.hasNext()){
//	             String key=it.next();
//	             System.out.println(key+":"+prop.getProperty(key));
//	         }
//	         in.close();
//	         
//	         ///保存属性到b.properties文件
//	         FileOutputStream oFile = new FileOutputStream("b.properties", true);//true表示追加打开
//	         prop.setProperty("phone", "10086");
//	         prop.store(oFile, "The New properties file");
//	         oFile.close();
//	     }
//	     catch(Exception e){
//	         System.out.println(e);
//	     }
//	 } 
	
	public static void main(String[] args) {
		Properties prop = new Properties();
		String path = Thread.currentThread().getContextClassLoader().getResource("").getPath();
		try {
			InputStream is = new FileInputStream(path + "/log4j2.properties");

			prop.load(is);
			/// 加载属性列表
			Iterator<String> it = prop.stringPropertyNames().iterator();
			while (it.hasNext()) {
				String key = it.next();
				System.out.println(key + "=" + prop.getProperty(key));
			}

			is.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
