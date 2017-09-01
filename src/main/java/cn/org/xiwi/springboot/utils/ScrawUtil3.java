package cn.org.xiwi.springboot.utils;

import java.io.BufferedInputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CountDownLatch;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.validation.constraints.Size;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import cn.org.xiwi.springboot.utils.JsonUtils.ToolType;
import cn.org.xiwi.springboot.utils.ScrawUtil2.Pic;

/**
 * 爬取银行卡信息
 */
public class ScrawUtil3 {
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

		System.out.println("---in--" + bankBins.size());
		System.out.println(urlStr);
		System.out.println(path);
		System.out.println("---out--");
		FileUtils.writeFile(path, bis);

		bis.close();
		conn.disconnect();
	}

	private static List<String> bankBins = new ArrayList<>(20);
	private volatile static int errorCount = 0;

	public static void getBankBinList(String url) {
		final CountDownLatch latch = new CountDownLatch(1);
		try {
			Document document = Jsoup.connect(url).timeout(90 * 1000).get();
			Elements elements = document.select("ul");
			for (Element ele : elements) {
				String bin = ele.select("a").text().replace("开头", "");
				bankBins.addAll(Arrays.asList(bin.split(" ")));
				System.out.println(bin + "\n");
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

	public static void getBankBinDetailInfo(String url, String bin) {
		System.out.println("get2 = " + url);
		final CountDownLatch latch = new CountDownLatch(1);
		try {
			Document document = Jsoup.connect(url).timeout(90 * 1000).get();
			Elements elements = document.select("table[width=550]");
			for (Element ele : elements) {
				Elements elements_ = ele.select("td[class*=STYLE]");
				int index = 0;
				BankBasicInfo info = new BankBasicInfo();
				for (Element ele_ : elements_) {
					if (!ele_.hasAttr("align")) {
						System.out.println(ele_.text());
						switch (index) {
						case 0:
							info.setBin(ele_.text());
							break;
						case 1:
							info.setBankName(ele_.text());
							break;
						case 2:
							info.setBankNum(ele_.text());
							break;
						case 3:
							info.setBankAddr(ele_.text());
							break;
						default:
							break;
						}
						index++;
					}
				}
				if (info.getBin() != null) {
					bankBasicInfos.add(info);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			errorList.add(bin);
			errorCount++;
		} finally {
			latch.countDown();
		}
		try {
			latch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private static List<BankBasicInfo> bankBasicInfos = new ArrayList<>(20);

	private static class BankBasicInfo {
		private String bin;
		private String bankName;
		private String bankNum;
		private String bankAddr;

		public String getBin() {
			return bin;
		}

		public void setBin(String bin) {
			this.bin = bin;
		}

		public String getBankName() {
			return bankName;
		}

		public void setBankName(String bankName) {
			this.bankName = bankName;
		}

		public String getBankNum() {
			return bankNum;
		}

		public void setBankNum(String bankNum) {
			this.bankNum = bankNum;
		}

		public String getBankAddr() {
			return bankAddr;
		}

		public void setBankAddr(String bankAddr) {
			this.bankAddr = bankAddr;
		}

		@Override
		public String toString() {
			return "BankBasicInfo [bin=" + bin + ", bankName=" + bankName + ", bankNum=" + bankNum + ", bankAddr="
					+ bankAddr + "]";
		}
	}

	private volatile static List<String> errorList = new ArrayList<>();

	public static void main(String[] args) {
		getBankBinList(
				"http://cache.baiducontent.com/c?m=9f65cb4a8c8507ed4fece763105392230e54f7367b84805268d4e419ce3b4611143abaa679795142ced1393a41f9464b9cf021063d1456b58cbe8a5ddccb85585b9f543e676cf55663d4&p=916fd715d9c041ae40fcf82d02148c&newp=8239d615d9c041ae0e9fc7710f5c98231610db2151d4d5166b82c825d7331b001c3bbfb423241305d2c278660ba94e59ebf5357830032ba3dda5c91d9fb4c57479de55&user=baidu&fm=sc&query=%D2%F8%D0%D0%BF%A8%BA%C5&qid=ff8f03e20001e8a7&p1=6");
		try {
			FileUtils.writeFile("/Users/xiwi/Desktop/cache_volley/bankBins.json",
					JsonUtils.toJson(ToolType.FASTJSON, bankBins));
		} catch (Exception e) {
		}
		// retry();
		System.out.println("bankBins length = " + bankBins.size());
		int index = 0;
		for (String string : bankBins) {
			getBankBinDetailInfo("http://www.guabu.com/bank/bin/" + string, string);
			System.out.println(errorCount+ " index = " + (index++));
		}

		System.out.println("errorList size = " + errorCount);
		index = 0;
		errorCount = 0;
		for (String string : errorList) {
			getBankBinDetailInfo("http://www.guabu.com/bank/bin/" + string, string);
			System.out.println(errorCount+ " index = " + (index++));
		}

		try {
			FileUtils.writeFile("/Users/xiwi/Desktop/cache_volley/bankBinDetails.json",
					JsonUtils.toJson(ToolType.FASTJSON, bankBasicInfos));
		} catch (Exception e) {
		}
	}

	private static void retry() {
		try {
			StringBuilder sBuilder = FileUtils.readFile("/Users/xiwi/Desktop/cache_volley/bankBinDetails.json",
					"UTF-8");
			bankBasicInfos = JsonUtils.getList(ToolType.FASTJSON, sBuilder.toString(), BankBasicInfo.class);

			System.out.println("size = " + bankBasicInfos.size());
			sBuilder = FileUtils.readFile("/Users/xiwi/Desktop/cache_volley/bankBins.json", "UTF-8");
			bankBins = JsonUtils.getList(ToolType.FASTJSON, sBuilder.toString(), String.class);

			for (BankBasicInfo bankBasicInfo : bankBasicInfos) {
				bankBins.remove(bankBasicInfo.bin);
			}

		} catch (Exception e) {
			e.printStackTrace();
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
}
