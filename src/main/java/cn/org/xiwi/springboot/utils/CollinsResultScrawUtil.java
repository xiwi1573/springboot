package cn.org.xiwi.springboot.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Stack;
import java.util.concurrent.CountDownLatch;

import javax.net.ssl.HttpsURLConnection;

import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.SpreadsheetMLPackage;
import org.docx4j.openpackaging.parts.SpreadsheetML.WorkbookPart;
import org.docx4j.openpackaging.parts.SpreadsheetML.WorksheetPart;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.w3c.dom.css.CSSCharsetRule;
import org.xlsx4j.exceptions.Xlsx4jException;
import org.xlsx4j.org.apache.poi.ss.usermodel.DataFormatter;
import org.xlsx4j.sml.Cell;
import org.xlsx4j.sml.Row;
import org.xlsx4j.sml.SheetData;
import org.xlsx4j.sml.Worksheet;

import com.alibaba.druid.util.StringUtils;
import com.sun.tools.classfile.Annotation.element_value;

import cn.org.xiwi.springboot.msg.BankCardValidateInfoMsg;
import cn.org.xiwi.springboot.utils.JsonUtils.ToolType;
import cn.org.xiwi.springboot.utils.OkHttpUtils.MNetCallback;

public class CollinsResultScrawUtil {

	private static final String prefix = "http://youdao.com/w/";
	private static final String tail = "/#keyfrom=dict2.top";
	private static final String TEST_WORKBOOK_NAME = "/Users/xiwi/Downloads/阅读单词分类8.28.xlsx";

	public static void main(String[] args) {
		// "https://api.douban.com/v2/book/search?tag=小说&start="+40+"&count=100"

		// StringBuilder sb = new StringBuilder(prefix);
		// sb.append("biologist").append(tail);
		// String rString = HttpProxy.doGetRequest(sb.toString());
		// System.out.println(rString);
		//
		// String[] result = getWordsTemp("biologist");
		// System.out.println("----- " + Arrays.toString(result));
//		mm();
		old();
	}

	private static void mm() {
		noCollins = FileUtils.readFileToList("/Users/xiwi/Desktop/word/jobNoCollinsWords.json", "utf-8");
		if (noCollins != null) {
			System.out.println(noCollins.size());
			for (String str : noCollins) {
				temp.push(str);
			}
		}
		m(temp.pop());
	}

	static Stack<String> temp = new Stack<String>();

	private static void old() {
		try {

			List<Collins> results = getWholeWords();

			for (int i = 0; i < results.size(); i++) {
				new ScrawThread(results.get(i)).start();
			}
			long start = System.currentTimeMillis();
			new Thread() {
				public void run() {
					while (true) {
						try {
							sleep(300);
						} catch (InterruptedException e) {
						}
						if (count == 12) {
							try {
								String json = JsonUtils.toJson(ToolType.GSON, results);
								FileUtils.writeFile("/Users/xiwi/Desktop/word/jobWords.json", json);
								FileUtils.writeFile("/Users/xiwi/Desktop/word/jobErrorWords.json", errors);
								FileUtils.writeFile("/Users/xiwi/Desktop/word/jobNoCollinsWords.json", noCollins);

//								for (String word : noCollins) {
//									String[] result = getWordsTemp(word);
//									System.out.println(word + " ----- " + Arrays.toString(result));
//								}

							} catch (Exception e) {
							}
							System.out.println("finish job = " + (System.currentTimeMillis() - start) / 1000.0f);
							break;
						}
					}
				};
			}.start();

		} catch (Xlsx4jException e) {
			e.printStackTrace();
		} catch (Docx4JException e) {
			e.printStackTrace();
		}
	}

	private static void m(final String word) {
		StringBuilder sb = new StringBuilder(prefix);
		sb.append(word).append(tail);
		OkHttpUtils httpUtils = OkHttpUtils.getInstance();
		final MNetCallback<String> callback = new MNetCallback<String>(String.class) {

			@Override
			public void onFailure(String error) {
				System.out.println(error);
			}

			@Override
			public void onSuccess(String resp) {
				System.out.println(resp);

				String[] items = parseJsoup(resp);

				System.out.println(word + " ---- " + (items != null && items.length > 0));
				if (items != null && items.length > 0) {
					CollinsItem item = new CollinsItem();
					cList.add(item);
					System.out.println("---------------------------------------------------"+word);
				}
				
				if (!temp.isEmpty()) {
					String word_ = temp.pop();
					if (word_ != null) {
						m(word_);
					}
				}else {
					try {
						FileUtils.writeFile("/Users/xiwi/Desktop/word/jobHomeWords.json", JsonUtils.toJson(ToolType.GSON, cList));
						FileUtils.writeFile("/Users/xiwi/Desktop/word/jobChangeWords.json", JsonUtils.toJson(ToolType.GSON, noChangeCollins));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		};
		httpUtils.doGet(sb.toString(), null, null, callback);
	}

	static List<CollinsItem> cList = new ArrayList<>();
	
	private static String[] parseJsoup(String content) {
		String[] items = null;
		Document document = Jsoup.parse(content);
		Element child_1 = document.select("div[class=collinsToggle trans-container]").first();

		Element child_2 = null;
		if (child_1 != null) {
			child_2 = child_1.select("ul[class=ol]").first();
		} else {
			return null;
		}
		if (child_2 != null) {
			Elements childs = child_2.select("li");
			int size = childs.size();
			int length = size > 2 ? 2 : size;
			items = new String[length];
			for (int i = 0; i < length; i++) {
				Element child = childs.get(i);
				Element ch_1 = child.select("div[class=collinsMajorTrans]").first();
				Element resilt = ch_1.select("p").first();
				items[i] = resilt.toString().replaceAll(regEx_html, "");
			}
		}
		return items;
	}

	private static String[] parseJsoup2(String content) {
		String[] items = null;
		Document document = Jsoup.parse(content);
		Element child_1 = document.select("div[class=collinsToggle trans-container]").first();

		Element child_2 = null;
		if (child_1 != null) {
			child_2 = child_1.select("ul[class=ol]").first();
		} else {
			return null;
		}
		if (child_2 != null) {
			Elements childs = child_2.select("li");
			int size = childs.size();
			int length = size > 2 ? 2 : size;
			items = new String[length];
			for (int i = 0; i < length; i++) {
				Element child = childs.get(i);
				Element ch_1 = child.select("div[class=collinsMajorTrans]").first();
				Element resilt = ch_1.select("p").first();
				items[i] = resilt.toString().replaceAll(regEx_html, "");
			}
		}
		
		if (items == null || items.length == 0) {
			Element element = child_1.select("a[href=/w/behaviour/?keyfrom=dict.collins]").first();
			if (element != null) {
				String string = element.toString().replaceAll(regEx_html, "");
				
				noChangeCollins.add(string);
				
				System.out.println(string+"  ---------------");
			}
		}
		return items;
	}
	
	static int count = 0;

	public static class ScrawThread extends Thread {
		Collins collins;

		ScrawThread(Collins collins) {
			this.collins = collins;
		}

		@Override
		public void run() {
			int size = collins.items.size();
			for (int i = 0; i < size; i++) {
				String word = collins.items.get(i).word;
				long st = System.currentTimeMillis();
				String[] items = getWords(word);

				System.out.println(word + " spend time = " + (System.currentTimeMillis() - st) / 1000.0f);

				try {
					sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				collins.items.get(i).items = items;
				if (items == null || items.length == 0) {
					noCollins.add(word);
				}
			}
			count++;
		}
	}

	static List<String> noCollins = new ArrayList<>();

	static List<String> noChangeCollins = new ArrayList<>();
	
	public static List<Collins> getWholeWords() throws Xlsx4jException, Docx4JException {
		SpreadsheetMLPackage xlsxPkg = SpreadsheetMLPackage.load(new java.io.File(TEST_WORKBOOK_NAME));

		WorkbookPart workbookPart = xlsxPkg.getWorkbookPart();

		int sheetLength = 12;

		List<Collins> results = new ArrayList<>();
		Collins collins = null;
		for (int i = 0; i < sheetLength; i++) {
			WorksheetPart sheet = workbookPart.getWorksheet(i);
			DataFormatter formatter = new DataFormatter();
			collins = new Collins();
			collins.items = getWords(sheet, formatter);
			collins.sheetName = sheet.getPartName().getName();
			System.out.println("sheetName=" + collins.sheetName+"----"+sheet.getPartName().getName());
			results.add(collins);
		}
		return results;
	}

	private static List<CollinsItem> getWords(WorksheetPart sheet, DataFormatter formatter) throws Docx4JException {
		Worksheet ws = sheet.getContents();
		SheetData data = ws.getSheetData();
		int index = 1;
		List<CollinsItem> items = new ArrayList<>(16);
		CollinsItem item = null;
		for (Row r : data.getRow()) {
			for (Cell c : r.getC()) {
				String text = formatter.formatCellValue(c);
				if (index > 1 && c.getR().contains("D") && !StringUtils.isEmpty(text)) {
					if (text.contains(" ")) {
						text = text.split(" ")[0];
					}
					item = new CollinsItem();
					item.word = text.replaceAll("[\\u4e00-\\u9fa5]", "").toLowerCase(Locale.US);
					items.add(item);
				}
			}
			index++;
		}
		return items;
	}

	private static final String regEx_html = "<[^>]+>"; // 定义HTML标签的正则表达式

	public static String[] getWordsTemp(String word) {
		StringBuilder sb = new StringBuilder(prefix);
		sb.append(word).append(tail);
		String[] items = null;
		final CountDownLatch latch = new CountDownLatch(1);
		try {
			Document document = Jsoup.connect(sb.toString()).timeout(30 * 1000).get();
			Element child_1 = document.select("div[class=collinsToggle trans-container]").first();

			Element child_2 = null;
			if (child_1 != null) {
				System.out.println(child_1.toString());
				child_2 = child_1.select("ul[class=ol]").first();
			} else {
				System.out.println(document.toString());
			}
			if (child_2 != null) {
				Elements childs = child_2.select("li");
				int size = childs.size();
				int length = size > 2 ? 2 : size;
				items = new String[length];
				for (int i = 0; i < length; i++) {
					Element child = childs.get(i);
					Element ch_1 = child.select("div[class=collinsMajorTrans]").first();
					Element resilt = ch_1.select("p").first();
					items[i] = resilt.toString().replaceAll(regEx_html, "");
				}
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
		return items;
	}

	public void getDocByJsoup(String href) {
		String ip = "221.237.155.64";
		int port = 9797;
		try {
			Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(ip, port));

			URL url = new URL(href);
			HttpsURLConnection urlcon = (HttpsURLConnection) url.openConnection(proxy);
			urlcon.connect(); // 获取连接
			InputStream is = urlcon.getInputStream();
			BufferedReader buffer = new BufferedReader(new InputStreamReader(is));
			StringBuffer bs = new StringBuffer();
			String l = null;
			while ((l = buffer.readLine()) != null) {
				bs.append(l);
			}
			System.out.println(bs.toString());
			Document doc = Jsoup.parse(bs.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String[] getWords(String word) {
		StringBuilder sb = new StringBuilder(prefix);
		sb.append(word).append(tail);
		String[] items = null;
		final CountDownLatch latch = new CountDownLatch(1);
		try {
			Document document = Jsoup.connect(sb.toString()).timeout(30 * 1000).get();
			Element child_1 = document.select("div[class=collinsToggle trans-container]").first();
			Element child_2 = null;
			if (child_1 != null) {
				child_2 = child_1.select("ul[class=ol]").first();
			} else {
				addError(word);
			}
			if (child_2 != null) {
				Elements childs = child_2.select("li");
				int size = childs.size();
				int length = size > 2 ? 2 : size;
				items = new String[length];
				for (int i = 0; i < length; i++) {
					Element child = childs.get(i);
					Element ch_1 = child.select("div[class=collinsMajorTrans]").first();
					Element resilt = ch_1.select("p").first();
					items[i] = resilt.toString().replaceAll(regEx_html, "");
				}
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
		return items;
	}

	public static void addError(String word) {
		errors.add(word);
		System.out.println(errors.size() + "     " + word);
	}

	public static List<String> errors = new ArrayList<>();

	public static class Collins {
		public String sheetName;
		public String partName;
		public List<CollinsItem> items;
		@Override
		public String toString() {
			return "Collins [sheetName=" + sheetName + ", partName=" + partName + ", items=" + items + "]";
		}
		
	}

	public static class CollinsItem {
		public String row;
		public String line;
		public String word;
		public String[] str;
		public String[] items;
		@Override
		public String toString() {
			return "CollinsItem [row=" + row + ", line=" + line + ", word=" + word + ", str=" + Arrays.toString(str)
					+ ", items=" + Arrays.toString(items) + "]";
		}
		
	}
}
