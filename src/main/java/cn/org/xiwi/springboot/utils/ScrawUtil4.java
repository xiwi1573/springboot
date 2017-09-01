package cn.org.xiwi.springboot.utils;

import java.io.BufferedInputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CountDownLatch;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.validation.constraints.Size;

import org.apache.http.conn.util.PublicSuffixList;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import cn.org.xiwi.springboot.msg.BankCardValidateInfoMsg;
import cn.org.xiwi.springboot.pay.bank.AliBankCardValidatedInfo;
import cn.org.xiwi.springboot.pay.bank.BankCardValidateInfo;
import cn.org.xiwi.springboot.pay.bank.CardType;
import cn.org.xiwi.springboot.utils.JsonUtils.ToolType;
import cn.org.xiwi.springboot.utils.OkHttpUtils.MNetCallback;
import cn.org.xiwi.springboot.utils.ScrawUtil2.Pic;

/**
 * 爬取视觉 用户信息   http://shijue.me/community/rating
 */
public class ScrawUtil4 {
	//http://shijue.me/community/search?type=json&page=1&size=20&license=-1&orderby=recommendTime
	
	//http://shijue.me/community/search?type=json&page=20&size=20&license=-1&category=吉祥物&orderby=rating
	
	
	
	
	public static void downloadImg(String urlStr, String prefix, String type, String endFix, Map<String, String> params)
			throws Exception {
		String path = "/Users/xiwi/Documents/design/pic/" + type + urlStr.replace(prefix, "");
		path = path.replace(endFix, "");
		if (FileUtils.isFileExist(path)) {
			System.out.println("file is exit = " + path);
			return;
		}

		StringBuilder sb = new StringBuilder();

		if (null != params && params.size() > 0) {
			sb.append("?");
			Entry<String, String> en;
			for (Iterator<Entry<String, String>> ir = params.entrySet().iterator(); ir.hasNext();) {
				en = ir.next();
				sb.append(en.getKey() + "=" + URLEncoder.encode(en.getValue(), "utf-8") + (ir.hasNext() ? "&" : ""));
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
		FileUtils.writeFile(path, bis);

		bis.close();
		conn.disconnect();
	}

	public static void getBankBinList(final String url) {
		final CountDownLatch latch = new CountDownLatch(1);
		try {
			OkHttpUtils httpUtils = OkHttpUtils.getInstance();
			final MNetCallback<SH> callback = new MNetCallback<SH>(
					SH.class) {

				@Override
				public void onFailure(SH error) {
					System.out.println("onFailure = "+error);

					latch.countDown();
				}

				@Override
				public void onSuccess(SH resp) {
					System.out.println(url+"onSuccess = "+resp);
					if (resp != null && resp.dataArray != null && resp.dataArray.size() > 0) {
						SHS.add(resp);
					}
					latch.countDown();
				}
			};

			Map<String, String> headersParams = new HashMap<>();
			
			headersParams.put("Host", "shijue.me");
			headersParams.put("Connection", "keep-alive");
			headersParams.put("Accept", "*/*");
			headersParams.put("Accept-Encoding", "gzip, deflate, sdch");
			headersParams.put("Referer", "http://shijue.me/community/discovery");
			headersParams.put("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/57.0.2987.133 Safari/537.36");
			httpUtils.doGet(
					url,
					headersParams, null, callback);
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

	private static List<SH> SHS = new ArrayList<>();
	
	public static void main(String[] args) {
		try {
			for (int i = 1; i < 21; i++) {
				getBankBinList("http://shijue.me/community/search?type=json&page="+i+"&size=20&license=-1&orderby=recommendTime");
				Thread.currentThread().sleep(2000);
			}
			
			FileUtils.writeFile("/Users/xiwi/Desktop/cache_volley/ShiJueUser.json",
					JsonUtils.toJson(ToolType.FASTJSON, SHS));
		} catch (Exception e) {
		}
	}

	public static boolean isOk(String imgUrl, String prefix, String type, String endFix) {
		if (imgUrl == null) {
			return false;
		}

		final CountDownLatch latch = new CountDownLatch(1);
		boolean isOk = true;
		try {
			downloadImg(imgUrl, prefix, type, endFix, null);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			if ("请求异常状态值:404".equals(e.getMessage())) {
				isOk = false;
			} else {
				isOk = false;
			}
		} finally {
			latch.countDown();
		}
		try {
			latch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return isOk;
	}

	static String suffixes = "avi|mpeg|3gp|mp3|mp4|wav|jpeg|gif|jpg|png|apk|exe|pdf|rar|zip|docx|doc";
	static Pattern pat = Pattern.compile("[\\w]+[\\.](" + suffixes + ")");// 正则判断

	static String realImgName(String img) {
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
	
	public static class SH{
		public List<SHArt> dataArray;

		public List<SHArt> getDataArray() {
			return dataArray;
		}

		public void setDataArray(List<SHArt> dataArray) {
			this.dataArray = dataArray;
		}

		@Override
		public String toString() {
			return "SH [dataArray=" + dataArray + "]";
		}
		
		
	}
	
	public static class SHArt{
		public SHUser uploaderInfo;

		public SHUser getUploaderInfo() {
			return uploaderInfo;
		}

		public void setUploaderInfo(SHUser uploaderInfo) {
			this.uploaderInfo = uploaderInfo;
		}

		@Override
		public String toString() {
			return "SHArt [uploaderInfo=" + uploaderInfo + "]";
		}
		
	}
	
	public static class SHUser{
		public long birthday;
		public long createdTime;
		public String about;
		public String avatar;
		public String field;
		public String id;
		public String unit;
		public String area;
		public String address;
		public String nickName;
		public String userName;
		public long getBirthday() {
			return birthday;
		}
		public void setBirthday(long birthday) {
			this.birthday = birthday;
		}
		public long getCreatedTime() {
			return createdTime;
		}
		public void setCreatedTime(long createdTime) {
			this.createdTime = createdTime;
		}
		public String getAbout() {
			return about;
		}
		public void setAbout(String about) {
			this.about = about;
		}
		public String getAvatar() {
			return avatar;
		}
		public void setAvatar(String avatar) {
			this.avatar = avatar;
		}
		public String getField() {
			return field;
		}
		public void setField(String field) {
			this.field = field;
		}
		public String getId() {
			return id;
		}
		public void setId(String id) {
			this.id = id;
		}
		public String getUnit() {
			return unit;
		}
		public void setUnit(String unit) {
			this.unit = unit;
		}
		public String getArea() {
			return area;
		}
		public void setArea(String area) {
			this.area = area;
		}
		public String getAddress() {
			return address;
		}
		public void setAddress(String address) {
			this.address = address;
		}
		public String getNickName() {
			return nickName;
		}
		public void setNickName(String nickName) {
			this.nickName = nickName;
		}
		public String getUserName() {
			return userName;
		}
		public void setUserName(String userName) {
			this.userName = userName;
		}
		@Override
		public String toString() {
			return "SHUser [birthday=" + birthday + ", createdTime=" + createdTime + ", about=" + about + ", avatar="
					+ avatar + ", field=" + field + ", id=" + id + ", unit=" + unit + ", area=" + area + ", address="
					+ address + ", nickName=" + nickName + ", userName=" + userName + "]";
		}
		
	}
	
}
