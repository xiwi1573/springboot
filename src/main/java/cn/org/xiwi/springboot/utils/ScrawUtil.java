package cn.org.xiwi.springboot.utils;

import static org.hamcrest.CoreMatchers.nullValue;

import java.io.BufferedInputStream;
import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CountDownLatch;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class ScrawUtil {
	public static String httpGet(String urlStr, Map<String, String> params) throws Exception {
	    StringBuilder sb = new StringBuilder();
	    if (null != params && params.size() > 0) {
	        sb.append("?");
	        Entry<String, String> en;
	        for (Iterator<Entry<String, String>> ir = params.entrySet().iterator(); ir.hasNext();) {
	            en = ir.next();
	            sb.append(en.getKey() + "=" + URLEncoder.encode(en.getValue(),"utf-8") + (ir.hasNext() ? "&" : ""));
	        }
	    }
	    URL url = new URL(urlStr + sb);
	    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	    conn.setConnectTimeout(30000);
	    conn.setReadTimeout(30000);
	    conn.setRequestMethod("GET");
	    if (conn.getResponseCode() != 200)
	        throw new Exception("请求异常状态值:" + conn.getResponseCode());
	    BufferedInputStream bis = new BufferedInputStream(conn.getInputStream());
//	    Reader reader = new InputStreamReader(bis,"gbk");
//	    char[] buffer = new char[2048];
//	    int len = 0;
//	    CharArrayWriter caw = new CharArrayWriter();
//	    while ((len = reader.read(buffer)) > -1)
//	        caw.write(buffer, 0, len);
//	    reader.close();
	    
	    FileUtils.writeFile("D:/opt/imgs/"+(urlStr.replace("http://t2.27270.com/", "")), bis);
	    
	    bis.close();
	    conn.disconnect();
	    //System.out.println(caw);
//	    return caw.toString();
	    return null;
	}
	// max 50
	public static void get1(String url) {
		final CountDownLatch latch = new CountDownLatch(1);
		try {
			Document document = Jsoup.connect(url).timeout(60 * 1000).get();

//			System.out.println("method 1:\n" + document.select("div[class=bd]div[id=mkPic]").html());

			Element element = document.select("ul[class=picList]").first();
			
			Elements elements = element.select("div[class=pic]");
		
			for (Element ele : elements) {
				String img_ = ele.select("img").attr("src");
				String url_ = ele.select("a").attr("href");
				String title_ = ele.select("a").attr("title");
				System.out.println(img_+"\n"+url_+"\n"+title_+"\n");
				get2(url_);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			latch.countDown();
		}
		try {
			latch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public static void get2(String url) {
		final CountDownLatch latch = new CountDownLatch(1);
		try {
			Document document = Jsoup.connect(url).timeout(60 * 1000).get();

//			System.out.println("method 1:\n" + document.select("div[class=bd]div[id=mkPic]").html());

			Element element = document.select("div[class=articleV4Body]div[id=picBody]").first();

			String img = element.select("img").attr("src");
			
			for (int i = 1; i <= 50; i++) {
				String tempImg = imgUrl(img, i);
				System.out.println(tempImg+" , "+isOk(tempImg));
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			latch.countDown();
		}
		try {
			latch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public static boolean isOk(String imgUrl){
		if (imgUrl == null) {
			return false;
		}
		final CountDownLatch latch = new CountDownLatch(1);
		boolean isOk = true;
		try {
			httpGet(imgUrl, null);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			if ("请求异常状态值:404".equals(e.getMessage())) {
				isOk = false;
			}else {
				isOk = false;
			}
		}finally {
			latch.countDown();
		}
		try {
			latch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return isOk;
	}
	static String suffixes="avi|mpeg|3gp|mp3|mp4|wav|jpeg|gif|jpg|png|apk|exe|pdf|rar|zip|docx|doc";  
	static Pattern pat=Pattern.compile("[\\w]+[\\.]("+suffixes+")");//正则判断  
	public static void main(String[] args) {
		get1("http://www.27270.com/ent/rentiyishu/");
	}
	
	static String imgName(String baseImgUrl) {
		String imgUrl = null;
		Matcher mc = pat.matcher(baseImgUrl);// 条件匹配
		while (mc.find()) {
			String substring = mc.group();// 截取文件名后缀名
			imgUrl = substring;
		}
		return imgUrl;
	}
	
	static String imgUrl(String baseImgUrl, int index) {
		String imgUrl = null;
		Matcher mc = pat.matcher(baseImgUrl);// 条件匹配
		while (mc.find()) {
			String substring = mc.group();// 截取文件名后缀名
			String[] temp = substring.split("\\.");
			imgUrl = baseImgUrl.replace(substring, "") + index + "." + temp[1];
		}
		return imgUrl;
	}
}
