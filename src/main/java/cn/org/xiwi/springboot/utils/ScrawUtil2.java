package cn.org.xiwi.springboot.utils;

import java.io.BufferedInputStream;
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

import cn.org.xiwi.springboot.utils.JsonUtils.ToolType;

public class ScrawUtil2 {
	public static void downloadImg(String urlStr,String prefix,String type,String endFix, Map<String, String> params) throws Exception {
		String path = "D:/opt/imgs/xiwi2/"+type+urlStr.replace(prefix, "");
	    path = path.replace(endFix, "");
	    if (FileUtils.isFileExist(path)) {
	    	System.out.println("file is exit = "+path);
			return;
		}
	    
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
	    
	    System.out.println("---in--");
		System.out.println(urlStr);
		System.out.println(path);
		System.out.println("---out--");
	    FileUtils.writeFile(path, bis);
	    
	    bis.close();
	    conn.disconnect();
	}
	static int max = 1;
	private static List<Pic> pics = new ArrayList<>(20);
	public static void get1(String url, String type, int index) {
		final CountDownLatch latch = new CountDownLatch(1);
		try {
			Document document = Jsoup.connect(url).timeout(90 * 1000).get();
			Elements elements = document.select("img[class=lazy]");
			for (Element ele : elements) {
				String img_ = ele.select("img").attr("data-original");
				String title_ = ele.select("img").attr("title");
				pics.add(new Pic(title_, type, img_));
				System.out.println("当页图片： "+title_+"\n"+img_);
			}
			System.out.println();
			if (max == 1) {
				Elements elements2 = document.select("div[class=pagelist]");
				for (Element ele : elements2) {
					String value_ = ele.select("a").text();
					String[] indexArr = value_.split(" ");
					max = Integer.valueOf(indexArr[indexArr.length-2]).intValue();
					System.out.println("max page = "+max);
				}
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
	
	public static class Pic{
		public String title;
		public String type;
		public String imgUrl;
		
		public Pic(){
			
		}
		
		public Pic(String title, String type, String imgUrl) {
			super();
			this.title = title;
			this.type = type;
			this.imgUrl = imgUrl;
		}
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((imgUrl == null) ? 0 : imgUrl.hashCode());
			result = prime * result + ((title == null) ? 0 : title.hashCode());
			result = prime * result + ((type == null) ? 0 : type.hashCode());
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Pic other = (Pic) obj;
			if (imgUrl == null) {
				if (other.imgUrl != null)
					return false;
			} else if (!imgUrl.equals(other.imgUrl))
				return false;
			if (title == null) {
				if (other.title != null)
					return false;
			} else if (!title.equals(other.title))
				return false;
			if (type == null) {
				if (other.type != null)
					return false;
			} else if (!type.equals(other.type))
				return false;
			return true;
		}
	}
	
	public static boolean isOk(String imgUrl,String prefix,String type,String endFix){
		if (imgUrl == null) {
			return false;
		}
		
		final CountDownLatch latch = new CountDownLatch(1);
		boolean isOk = true;
		try {
			downloadImg(imgUrl,prefix,type,endFix ,null);
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
	
	static String type = "people";//nature,game,food,artdesign,science,religion,music,army,interiordesign
	static String prefix = "http://img95.699pic.com";
	
	public static void main(String[] args) {
//		String[] datas = "nature,game,food,artdesign,science,religion,music,army,interiordesign,people".split(",");
//		for (String string : datas) {
//			type = string;
//			max = 1;
//			for (int i = 1; i <= max; i++) {
//				String url = "http://699pic.com/"+type+"-"+i+".html";
//				System.out.println("++++++++"+url);
//				get1(url,type,i);
//			}
////			try {
////				FileUtils.writeFile("D:/opt/imgs/xiwi2/"+type+"/json.json", JsonUtils.toJson(ToolType.FASTJSON, pics));
////			} catch (Exception e1) {
////				e1.printStackTrace();
////			}
//		}
//		
//		try {
//			FileUtils.writeFile("D:/opt/imgs/xiwi2/json.json", JsonUtils.toJson(ToolType.FASTJSON, pics));
//		} catch (Exception e1) {
//			e1.printStackTrace();
//		}
//		
		long startTime = System.currentTimeMillis() / 1000;
		new Thread(){
			@Override
			public void run() {
				int index = 0;
				while (again()) {
					index++;
					System.out.println("retry count = "+index+(System.currentTimeMillis() / 1000 - startTime));
				}
				System.out.println(System.currentTimeMillis() / 1000 - startTime);
			}
		}.start();
		
//		String img = "http://img95.699pic.com/photo/50000/6965.jpg_wh300.jpg";
//		String bugName = imgName(img);
//		System.out.println(imgName(img.replace(bugName, "")));
//		System.out.println(isOk(img, prefix, type, "_wh300.jpg"));
	}
	
	private static boolean again() {
		retry();
		try {
			for (Pic pic : pics) {
				downloadImg(pic.imgUrl, prefix, type, "_wh300.jpg", null);
			}
		} catch (Exception e) {
			return true;
		}
		return false;
	}
	
	private static void retry() {
		StringBuilder sBuilder = FileUtils.readFile("D:/opt/imgs/xiwi2/json.json", "UTF-8");
		try {
			pics = JsonUtils.getList(ToolType.FASTJSON, sBuilder.toString(), Pic.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	static String realImgName(String img){
		String bugName = imgName(img);
		return imgName(img.replace(bugName, ""));
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
