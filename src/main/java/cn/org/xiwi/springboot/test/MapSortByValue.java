package cn.org.xiwi.springboot.test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

/**
 * @功能说明：对map按照值进行排序
 * @author:linghushaoxia
 * @time:2015年8月5日下午4:42:47
 * @version:1.0
 * 
 */
public class MapSortByValue {
	public static Map<String, Long> sortMapByValue(Map<String, Long> map) {
		if (map == null || map.isEmpty()) {
			return map;
		}
		// 排序结果，LinkedHashMap保证put进入的元素的顺序
		Map<String, Long> sortMap = new LinkedHashMap<String, Long>();
		List<Map.Entry<String, Long>> listEntry = new ArrayList<Map.Entry<String, Long>>(map.entrySet());
		// 排序
		Collections.sort(listEntry, new Comparator<Map.Entry<String, Long>>() {
			@Override
			public int compare(Entry<String, Long> o1, Entry<String, Long> o2) {
				return o1.getValue().compareTo(o2.getValue());
			}
		});
		// 保存排序结果
		for (int i = 0; i < listEntry.size(); i++) {
			sortMap.put(listEntry.get(i).getKey(), listEntry.get(i).getValue());
		}
		return sortMap;
	}

	static String base = "A/--BCDecvbnklpddE012FGH&#IJKL456789MN=OPQRSTassertyuoncdrftgpvfewUVWXYZ3";

	/**
	 * 
	 * @功能说明:获取随机字符
	 * @param:length 随机字符串长度
	 * @return：String
	 * @time:2015年8月5日下午6:36:41
	 * @author:linghushaoxia
	 * @exception:
	 *
	 */
	public static String getRandomString(int length) {
		Random random = new Random();
		StringBuffer sb = new StringBuffer();
		int number;
		for (int i = 0; i < length; i++) {
			number = random.nextInt(base.length());
			sb.append(base.charAt(number));
		}
		return sb.toString();
	}

	/**
	 * 
	 * @功能说明:测试按值排序map的耗时
	 * @param:
	 * @return：void
	 * @time:2015年8月5日下午6:43:43
	 * @author:linghushaoxia
	 * @exception:
	 *
	 */
	public static void main(String[] args) {
		// 按照值排序
		Map<String, Long> mapValue = new HashMap<String, Long>();
		Random random = new Random();
		// 开始计时
		
		// 构造待排序记录
		for (Long i = 0L; i < 1000000; i++) {
			mapValue.put(getRandomString(128), random.nextLong());
		}
		long begin = System.currentTimeMillis();
		// 排序
		mapValue = sortMapByValue(mapValue);
		// 结束排序
		long end = System.currentTimeMillis();
		System.out.println("100万记录耗时:" + (end - begin) + "ms");
	}

}
