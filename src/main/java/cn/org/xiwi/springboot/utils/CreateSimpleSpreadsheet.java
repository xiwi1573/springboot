package cn.org.xiwi.springboot.utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CountDownLatch;

import javax.xml.bind.JAXBException;

import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.SpreadsheetMLPackage;
import org.docx4j.openpackaging.parts.PartName;
import org.docx4j.openpackaging.parts.SpreadsheetML.WorkbookPart;
import org.docx4j.openpackaging.parts.SpreadsheetML.WorksheetPart;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.xlsx4j.exceptions.Xlsx4jException;
import org.xlsx4j.jaxb.Context;
import org.xlsx4j.org.apache.poi.ss.usermodel.DataFormatter;
import org.xlsx4j.sml.CTRst;
import org.xlsx4j.sml.CTXstringWhitespace;
import org.xlsx4j.sml.Cell;
import org.xlsx4j.sml.Row;
import org.xlsx4j.sml.STCellType;
import org.xlsx4j.sml.SheetData;
import org.xlsx4j.sml.Worksheet;

import com.alibaba.druid.util.StringUtils;

import cn.org.xiwi.springboot.utils.CollinsResultScrawUtil.Collins;
import cn.org.xiwi.springboot.utils.CollinsResultScrawUtil.CollinsItem;
import cn.org.xiwi.springboot.utils.JsonUtils.ToolType;

public class CreateSimpleSpreadsheet {
	private static final String TEST_WORKBOOK_NAME = "/Users/xiwi/Desktop/阅读单词分类8.28_handler.xlsx";
	static final int COUNT = 3;
	private static final String TEST_WORKBOOK_NAME_R = "/Users/xiwi/Desktop/阅读单词分类8.28_handler_r_"+COUNT+".xlsx";
	static List<Collins> results = null;
	public static void main(String[] args) throws Exception {
		try {
			results = getWholeWords();
		

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
								FileUtils.writeFile("/Users/xiwi/Desktop/word/"+COUNT+"/jobWords.json", json);
								FileUtils.writeFile("/Users/xiwi/Desktop/word/"+COUNT+"/jobErrorWords.json", errors);
								FileUtils.writeFile("/Users/xiwi/Desktop/word/"+COUNT+"/jobNoCollinsWords.json", noCollins);
								name(results);
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

	public static void name(List<Collins> collins) throws Exception {

		SpreadsheetMLPackage pkg = SpreadsheetMLPackage.createPackage();

		addSheet(pkg,collins);

		pkg.save(new File(TEST_WORKBOOK_NAME_R));

		System.out.println("\n\n done .. " + TEST_WORKBOOK_NAME_R);
	}

	private static void addSheet(SpreadsheetMLPackage pkg,List<Collins> collins) throws Xlsx4jException, Docx4JException, JAXBException {
		
		for (int i = 0; i < collins.size(); i++) {
			Collins collin = collins.get(i);
			WorksheetPart sheet = pkg.createWorksheetPart(new PartName(collin.sheetName), "Sheet" + (i + 1), i + 1);

			List<CollinsItem> items = collin.items;
			for (int j = 0; j < items.size(); j++) {
				CollinsItem item = items.get(j);
				addContent(j + 1, item, sheet);
			}
		}
	}

	private static Cell createCell(String content) {

		Cell cell = Context.getsmlObjectFactory().createCell();
		
		CTXstringWhitespace ctx = Context.getsmlObjectFactory().createCTXstringWhitespace();
		ctx.setValue(content);
		
		CTRst ctrst = new CTRst();
		ctrst.setT(ctx);

		cell.setT(STCellType.INLINE_STR);
		cell.setIs(ctrst); // add ctrst as inline string
		
		return cell;
		
	}
	
	private static void addContent(long rowIndex, CollinsItem item, WorksheetPart sheet) throws Docx4JException {
		String[] items = item.items;
		SheetData sheetData = sheet.getContents().getSheetData();

		Row row = Context.getsmlObjectFactory().createRow();
		row.setR(rowIndex);
		char index = 'A';
		for (int j = 0; j < item.str.length; j++) {
			System.out.println((String.valueOf(index) + rowIndex)+"   "+item.str[j]);
			Cell cell0 = createCell(replace(item.str[j]));
			cell0.setR(String.valueOf(index) + String.valueOf(rowIndex));
			row.getC().add(cell0);
			index++;
		}
		if (rowIndex != 1) {
			if (items != null && items.length == 1) {
				Cell cell = createCell(replace(items[0]));
				cell.setV(replace(items[0]));
				cell.setR("K" + rowIndex);
				row.getC().add(cell);
			} else if (items != null && items.length == 2) {
				Cell cell = createCell(replace(items[0]));
				cell.setR("K" + rowIndex);
				row.getC().add(cell);
				Cell cell2 = createCell(replace(items[1]));
				cell2.setR("L" + rowIndex);
				row.getC().add(cell2);
			}
		}
		sheetData.getRow().add(row);
	}

	public static String replace(String content) {
		return StringUtils.isEmpty(content) ? "" : content;
	}
	
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
			results.add(collins);
		}
		return results;
	}

	private static List<CollinsItem> getWords(WorksheetPart sheet, DataFormatter formatter) throws Docx4JException {
		Worksheet ws = sheet.getContents();
		SheetData data = ws.getSheetData();
		List<CollinsItem> items = new ArrayList<>(16);
		CollinsItem item = null;
		List<Row> rows = data.getRow();
		int size = rows.size();
		Row row = null;
		for (int i = 0; i < size; i++) {
			row = rows.get(i);
			List<Cell> cells = row.getC();
			item = new CollinsItem();
			item.str = new String[cells.size()];
			for (int j = 0; j < cells.size(); j++) {
				Cell cell = null;
				cell = cells.get(j);
				String text = replace(formatter.formatCellValue(cell));
				
				item.row = cell.getR();
				item.line = String.valueOf(i);
				item.str[j] = text;
				if (j == 3) {
					if (!StringUtils.isEmpty(text)) {
						if (text.contains(" ")) {
							text = text.split(" ")[0];
						}
						item.word = text.replaceAll("[\\u4e00-\\u9fa5]", "").toLowerCase(Locale.US);
					}
				}
			}
			items.add(item);
		}
		return items;
	}
	private static final String prefix = "http://youdao.com/w/";
	private static final String tail = "/#keyfrom=dict2.top";
	private static final String regEx_html = "<[^>]+>"; // 定义HTML标签的正则表达式
	
	public static String[] getWords(String word) {
		StringBuilder sb = new StringBuilder(prefix);
		sb.append(word).append(tail);
		String[] items = null;
		final CountDownLatch latch = new CountDownLatch(1);
		try {
			Document document = Jsoup.connect(sb.toString()).timeout(60 * 1000).get();
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
	static List<String> noCollins = new ArrayList<>();
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
}
