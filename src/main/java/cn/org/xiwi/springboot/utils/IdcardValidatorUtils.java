package cn.org.xiwi.springboot.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 身份证合法性校验
 * </p>
 * <p>
 * <pre>
 * --15位身份证号码：第7、8位为出生年份(两位数)，第9、10位为出生月份，第11、12位代表出生日期，第15位代表性别，奇数为男，偶数为女。
 * --18位身份证号码：第7、8、9、10位为出生年份(四位数)，第11、第12位为出生月份，第13、14位代表出生日期，第17位代表性别，奇数为男，偶数为女。
 *    最后一位为校验位
 * </pre>
 *
 * @author xiwi
 */
public class IdcardValidatorUtils {

    /**
     * <pre>
     * 省、直辖市代码表：
     *     11 : 北京  12 : 天津  13 : 河北       14 : 山西  15 : 内蒙古
     *     21 : 辽宁  22 : 吉林  23 : 黑龙江  31 : 上海  32 : 江苏
     *     33 : 浙江  34 : 安徽  35 : 福建       36 : 江西  37 : 山东
     *     41 : 河南  42 : 湖北  43 : 湖南       44 : 广东  45 : 广西      46 : 海南
     *     50 : 重庆  51 : 四川  52 : 贵州       53 : 云南  54 : 西藏
     *     61 : 陕西  62 : 甘肃  63 : 青海       64 : 宁夏  65 : 新疆
     *     71 : 台湾
     *     81 : 香港  82 : 澳门
     *     91 : 国外
     * </pre>
     */
    private static String cityCode[] = {"11", "12", "13", "14", "15", "21",
            "22", "23", "31", "32", "33", "34", "35", "36", "37", "41", "42",
            "43", "44", "45", "46", "50", "51", "52", "53", "54", "61", "62",
            "63", "64", "65", "71", "81", "82", "91"};

    /**
     * 每位加权因子
     */
    private static int power[] = {7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5,
            8, 4, 2};

    public static Map<String, String> cityCodes = new HashMap<String, String>();  
    /** 台湾身份首字母对应数字 */  
    public static Map<String, Integer> twFirstCode = new HashMap<String, Integer>();  
    /** 香港身份首字母对应数字 */  
    public static Map<String, Integer> hkFirstCode = new HashMap<String, Integer>();  
    static {  
        cityCodes.put("11", "北京");  
        cityCodes.put("12", "天津");  
        cityCodes.put("13", "河北");  
        cityCodes.put("14", "山西");  
        cityCodes.put("15", "内蒙古");  
        cityCodes.put("21", "辽宁");  
        cityCodes.put("22", "吉林");  
        cityCodes.put("23", "黑龙江");  
        cityCodes.put("31", "上海");  
        cityCodes.put("32", "江苏");  
        cityCodes.put("33", "浙江");  
        cityCodes.put("34", "安徽");  
        cityCodes.put("35", "福建");  
        cityCodes.put("36", "江西");  
        cityCodes.put("37", "山东");  
        cityCodes.put("41", "河南");  
        cityCodes.put("42", "湖北");  
        cityCodes.put("43", "湖南");  
        cityCodes.put("44", "广东");  
        cityCodes.put("45", "广西");  
        cityCodes.put("46", "海南");  
        cityCodes.put("50", "重庆");  
        cityCodes.put("51", "四川");  
        cityCodes.put("52", "贵州");  
        cityCodes.put("53", "云南");  
        cityCodes.put("54", "西藏");  
        cityCodes.put("61", "陕西");  
        cityCodes.put("62", "甘肃");  
        cityCodes.put("63", "青海");  
        cityCodes.put("64", "宁夏");  
        cityCodes.put("65", "新疆");  
        cityCodes.put("71", "台湾");  
        cityCodes.put("81", "香港");  
        cityCodes.put("82", "澳门");  
        cityCodes.put("91", "国外");  
        twFirstCode.put("A", 10);  
        twFirstCode.put("B", 11);  
        twFirstCode.put("C", 12);  
        twFirstCode.put("D", 13);  
        twFirstCode.put("E", 14);  
        twFirstCode.put("F", 15);  
        twFirstCode.put("G", 16);  
        twFirstCode.put("H", 17);  
        twFirstCode.put("J", 18);  
        twFirstCode.put("K", 19);  
        twFirstCode.put("L", 20);  
        twFirstCode.put("M", 21);  
        twFirstCode.put("N", 22);  
        twFirstCode.put("P", 23);  
        twFirstCode.put("Q", 24);  
        twFirstCode.put("R", 25);  
        twFirstCode.put("S", 26);  
        twFirstCode.put("T", 27);  
        twFirstCode.put("U", 28);  
        twFirstCode.put("V", 29);  
        twFirstCode.put("X", 30);  
        twFirstCode.put("Y", 31);  
        twFirstCode.put("W", 32);  
        twFirstCode.put("Z", 33);  
        twFirstCode.put("I", 34);  
        twFirstCode.put("O", 35);  
        hkFirstCode.put("A", 1);  
        hkFirstCode.put("B", 2);  
        hkFirstCode.put("C", 3);  
        hkFirstCode.put("R", 18);  
        hkFirstCode.put("U", 21);  
        hkFirstCode.put("Z", 26);  
        hkFirstCode.put("X", 24);  
        hkFirstCode.put("W", 23);  
        hkFirstCode.put("O", 15);  
        hkFirstCode.put("N", 14);  
    }  
    
    /**
     * 验证所有的身份证的合法性
     *
     * @param idcard 身份证
     * @return 合法返回true，否则返回false
     */
    public static boolean isValidatedAllIdcard(String idcard) {
        if (idcard == null || "".equals(idcard)) {
            return false;
        }
        if (idcard.length() == 15) {
            return validate15IDCard(idcard);
        }
        return validate18Idcard(idcard);
    }

    /**
     * 获取身份证的第17位，奇数为男性，偶数为女性
     * 必须先校验证件号通过后才可使用此方法
     *
     * @return
     */
    public static int getGenderCode(String idcard) {
        String sex = idcard.substring(16, 17);
        if (Integer.parseInt(sex) % 2 == 0) {
            return 2;
        } else {
            return 1;
        }
    }

    /**
     * 根据身份编号获取年龄
     *
     * @param idCard
     *            身份编号
     * @return 年龄
     */
    public static int getAgeByIdCard(String idCard) {
        int iAge = 0;
        if (idCard.length() == 15) {
            idCard = convertIdcarBy15bit(idCard);
        }
        String year = idCard.substring(6, 10);
        Calendar cal = Calendar.getInstance();
        int iCurrYear = cal.get(Calendar.YEAR);
        iAge = iCurrYear - Integer.valueOf(year);
        return iAge;
    }

    /**
     * 根据身份编号获取生日
     *
     * @param idCard
     *            身份编号
     * @return 生日(yyyyMMdd)
     */
    public static String getBirthByIdCard(String idCard) {
        Integer len = idCard.length();
        if (len < 15) {
            return null;
        } else if (len == 15) {
            idCard = convertIdcarBy15bit(idCard);
        }
        return idCard.substring(6, 14);
    }

    /**
     * <p>
     * 判断18位身份证的合法性
     * </p>
     * 根据〖中华人民共和国国家标准GB11643-1999〗中有关公民身份号码的规定，公民身份号码是特征组合码，由十七位数字本体码和一位数字校验码组成。
     * 排列顺序从左至右依次为：六位数字地址码，八位数字出生日期码，三位数字顺序码和一位数字校验码。
     * <p>
     * 顺序码: 表示在同一地址码所标识的区域范围内，对同年、同月、同 日出生的人编定的顺序号，顺序码的奇数分配给男性，偶数分配 给女性。
     * </p>
     * <p>
     * 1.前1、2位数字表示：所在省份的代码； 2.第3、4位数字表示：所在城市的代码； 3.第5、6位数字表示：所在区县的代码；
     * 4.第7~14位数字表示：出生年、月、日； 5.第15、16位数字表示：所在地的派出所的代码；
     * 6.第17位数字表示性别：奇数表示男性，偶数表示女性；
     * 7.第18位数字是校检码：也有的说是个人信息码，一般是随计算机的随机产生，用来检验身份证的正确性。校检码可以是0~9的数字，有时也用x表示。
     * </p>
     * <p>
     * 第十八位数字(校验码)的计算方法为： 1.将前面的身份证号码17位数分别乘以不同的系数。从第一位到第十七位的系数分别为：7 9 10 5 8 4
     * 2 1 6 3 7 9 10 5 8 4 2
     * </p>
     * <p>
     * 2.将这17位数字和系数相乘的结果相加。
     * </p>
     * <p>
     * 3.用加出来和除以11，看余数是多少
     * </p>
     * 4.余数只可能有0 1 2 3 4 5 6 7 8 9 10这11个数字。其分别对应的最后一位身份证的号码为1 0 X 9 8 7 6 5 4 3
     * 2。
     * <p>
     * 5.通过上面得知如果余数是2，就会在身份证的第18位数字上出现罗马数字的Ⅹ。如果余数是10，身份证的最后一位号码就是2。
     * </p>
     *
     * @param idcard
     * @return
     */
    public static boolean validate18Idcard(String idcard) {
        if (idcard == null) {
            return false;
        }

        // 非18位为假
        if (idcard.length() != 18) {
            return false;
        }
        // 获取前17位
        String idcard17 = idcard.substring(0, 17);

        // 前17位全部为数字
        if (!isDigital(idcard17)) {
            return false;
        }

        String provinceid = idcard.substring(0, 2);
        // 校验省份
        if (!checkProvinceid(provinceid)) {
            return false;
        }

        // 校验出生日期
        String birthday = idcard.substring(6, 14);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

        try {
            Date birthDate = sdf.parse(birthday);
            String tmpDate = sdf.format(birthDate);
            if (!tmpDate.equals(birthday)) {// 出生年月日不正确
                return false;
            }

        } catch (ParseException e1) {

            return false;
        }

        // 获取第18位
        String idcard18Code = idcard.substring(17, 18);

        char c[] = idcard17.toCharArray();

        int bit[] = converCharToInt(c);

        int sum17 = 0;

        sum17 = getPowerSum(bit);

        // 将和值与11取模得到余数进行校验码判断
        String checkCode = getCheckCodeBySum(sum17);
        if (null == checkCode) {
            return false;
        }
        // 将身份证的第18位与算出来的校码进行匹配，不相等就为假
        if (!idcard18Code.equalsIgnoreCase(checkCode)) {
            return false;
        }

        return true;
    }

    /**
     * 校验15位身份证
     * <p>
     * <pre>
     * 只校验省份和出生年月日
     * </pre>
     *
     * @param idcard
     * @return
     */
    public static boolean validate15IDCard(String idcard) {
        if (idcard == null) {
            return false;
        }
        // 非15位为假
        if (idcard.length() != 15) {
            return false;
        }

        // 15全部为数字
        if (!isDigital(idcard)) {
            return false;
        }

        String provinceid = idcard.substring(0, 2);
        // 校验省份
        if (!checkProvinceid(provinceid)) {
            return false;
        }

        String birthday = idcard.substring(6, 12);

        SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd");

        try {
            Date birthDate = sdf.parse(birthday);
            String tmpDate = sdf.format(birthDate);
            if (!tmpDate.equals(birthday)) {// 身份证日期错误
                return false;
            }

        } catch (ParseException e1) {

            return false;
        }

        return true;
    }

    /** 
     * 验证10位身份编码是否合法 
     *  
     * @param idCard 
     *            身份编码 
     * @return 身份证信息数组 
     *         <p> 
     *         [0] - 台湾、澳门、香港 [1] - 性别(男M,女F,未知N) [2] - 是否合法(合法true,不合法false) 
     *         若不是身份证件号码则返回null 
     *         </p> 
     */  
    public static String[] validateIdCard10(String idCard) {  
        String[] info = new String[3];  
        String card = idCard.replaceAll("[\\(|\\)]", "");  
        if (card.length() != 8 && card.length() != 9 && idCard.length() != 10) {  
            return null;  
        }  
        if (idCard.matches("^[a-zA-Z][0-9]{9}{1}")) { // 台湾  
            info[0] = "台湾";  
            System.out.println("11111");  
            String char2 = idCard.substring(1, 2);  
            if (char2.equals("1")) {  
                info[1] = "M";  
            } else if (char2.equals("2")) {  
                info[1] = "F";  
            } else {  
                info[1] = "N";  
                info[2] = "false";  
                return info;  
            }  
            info[2] = validateTWCard(idCard) ? "true" : "false";  
        } else if (idCard.matches("^[1|5|7][0-9]{6}\\(?[0-9A-Z]\\)?{1}")) { // 澳门  
            info[0] = "澳门";  
            info[1] = "N";  
            info[2] = validateAMCard(idCard) ? "true" : "false";  
        } else if (idCard.matches("^[A-Z]{1,2}[0-9]{6}\\(?[0-9A]\\)?{1}")) { // 香港  ^((\s?[A-Za-z])|([A-Za-z]{2}))\d{6}(([0−9aA])|([0-9aA]))$
            info[0] = "香港";  
            info[1] = "N";  
            info[2] = validateHKCard(idCard) ? "true" : "false";  
        } else {  
            return null;  
        }  
        return info;  
    }  

	/**
	 * 验证澳门身份证号码 
     *  
     * @param idCard 
     *            身份证号码 
     * @return 验证码是否符合 
     * 
	 * 澳门： X/NNNNNN/Y
	 * 
	 * A B C D N S M F
	 * 
	 * 身份证号码由8个拉丁数字组成格式为“X/NNNNNN/Y
	 * 
	 * 在“/”符号前加上一个拉丁数字1、5或7以代表其取证时代，而在“/”符号后加上的拉丁数字则为查核用数码
	 * 
	 * 智能身份证将原有格式（X/NNNNNN/Y）改为XNNNNNN(Y)
	 */
	public static boolean validateAMCard(String idCard) {
		return idCard.matches("^[1|5|7][0-9]{6}\\([0-9Aa]\\)");
	}
    
    /** 
     * 验证台湾身份证号码 
     *  
     * @param idCard 
     *            身份证号码 
     * @return 验证码是否符合 
     */  
    public static boolean validateTWCard(String idCard) {  
        String start = idCard.substring(0, 1);  
        String mid = idCard.substring(1, 9);  
        String end = idCard.substring(9, 10);  
        Integer iStart = twFirstCode.get(start);  
        Integer sum = iStart / 10 + (iStart % 10) * 9;  
        char[] chars = mid.toCharArray();  
        Integer iflag = 8;  
        for (char c : chars) {  
            sum = sum + Integer.valueOf(c + "") * iflag;  
            iflag--;  
        }  
        return (sum % 10 == 0 ? 0 : (10 - sum % 10)) == Integer.valueOf(end) ? true  
                : false;  
    }  
  
    /** 
     * 验证香港身份证号码(存在Bug，部份特殊身份证无法检查) 
     * <p> 
     * 身份证前2位为英文字符，如果只出现一个英文字符则表示第一位是空格，对应数字58 前2位英文字符A-Z分别对应数字10-35 
     * 最后一位校验码为0-9的数字加上字符"A"，"A"代表10 
     * </p> 
     * <p> 
     * 将身份证号码全部转换为数字，分别对应乘9-1相加的总和，整除11则证件号码有效 
     * </p> 
     *  
     * @param idCard 
     *            身份证号码 
     * @return 验证码是否符合 
     */  
    public static boolean validateHKCard(String idCard) {  
        try {
        	String card = idCard.replaceAll("[\\(|\\)]", "");  
            Integer sum = 0;  
            if (card.length() == 9) {  
                sum = (Integer.valueOf(card.substring(0, 1).toUpperCase()  
                        .toCharArray()[0]) - 55)  
                        * 9  
                        + (Integer.valueOf(card.substring(1, 2).toUpperCase()  
                                .toCharArray()[0]) - 55) * 8;  
                card = card.substring(1, 9);  
            } else {  
                sum = 522 + (Integer.valueOf(card.substring(0, 1).toUpperCase()  
                        .toCharArray()[0]) - 55) * 8;  
            }  
            String mid = card.substring(1, 7);  
            String end = card.substring(7, 8);  
            char[] chars = mid.toCharArray();  
            Integer iflag = 7;  
            for (char c : chars) {  
                sum = sum + Integer.valueOf(c + "") * iflag;  
                iflag--;  
            }  
            if (end.toUpperCase().equals("A")) {  
                sum = sum + 10;  
            } else {  
                sum = sum + Integer.valueOf(end);  
            }  
            return (sum % 11 == 0) ? true : false;  
		} catch (Exception e) {
			return false;
		}
    }  
    
    /** 
     *根据身份证号，自动获取对应的星座 
     *  
     * @param idCard 
     *            身份证号码 
     * @return 星座 
     */  
    public static String getConstellationById(String idCard) {  
        if (!isValidatedAllIdcard(idCard))  
            return "";  
        int month = getMonthByIdCard(idCard);  
        int day = getDateByIdCard(idCard);  
        String strValue = "";  
  
        if ((month == 1 && day >= 20) || (month == 2 && day <= 18)) {  
            strValue = "水瓶座";  
        } else if ((month == 2 && day >= 19) || (month == 3 && day <= 20)) {  
            strValue = "双鱼座";  
        } else if ((month == 3 && day > 20) || (month == 4 && day <= 19)) {  
            strValue = "白羊座";  
        } else if ((month == 4 && day >= 20) || (month == 5 && day <= 20)) {  
            strValue = "金牛座";  
        } else if ((month == 5 && day >= 21) || (month == 6 && day <= 21)) {  
            strValue = "双子座";  
        } else if ((month == 6 && day > 21) || (month == 7 && day <= 22)) {  
            strValue = "巨蟹座";  
        } else if ((month == 7 && day > 22) || (month == 8 && day <= 22)) {  
            strValue = "狮子座";  
        } else if ((month == 8 && day >= 23) || (month == 9 && day <= 22)) {  
            strValue = "处女座";  
        } else if ((month == 9 && day >= 23) || (month == 10 && day <= 23)) {  
            strValue = "天秤座";  
        } else if ((month == 10 && day > 23) || (month == 11 && day <= 22)) {  
            strValue = "天蝎座";  
        } else if ((month == 11 && day > 22) || (month == 12 && day <= 21)) {  
            strValue = "射手座";  
        } else if ((month == 12 && day > 21) || (month == 1 && day <= 19)) {  
            strValue = "魔羯座";  
        }  
  
        return strValue;  
    }  
      
      
    /** 
     *根据身份证号，自动获取对应的生肖 
     *  
     * @param idCard 
     *            身份证号码 
     * @return 生肖 
     */  
    public static String getZodiacById(String idCard) { // 根据身份证号，自动返回对应的生肖  
        if (!isValidatedAllIdcard(idCard))  
            return "";  
  
        String sSX[] = { "猪", "鼠", "牛", "虎", "兔", "龙", "蛇", "马", "羊", "猴", "鸡", "狗" };  
        int year = getYearByIdCard(idCard);  
        int end = 3;  
        int x = (year - end) % 12;  
  
        String retValue = "";  
        retValue = sSX[x];  
  
        return retValue;  
    }  
      
      
    /** 
     *根据身份证号，自动获取对应的天干地支 
     *  
     * @param idCard 
     *            身份证号码 
     * @return 天干地支 
     */  
    public static String getChineseEraById(String idCard) { // 根据身份证号，自动返回对应的天干地支  
        if (!isValidatedAllIdcard(idCard))  
            return "";  
  
        String sTG[] = { "癸", "甲", "乙", "丙", "丁", "戊", "己", "庚", "辛", "任" };  
        String sDZ[] = { "亥", "子", "丑", "寅", "卯", "辰", "巳", "午", "未", "申", "酉", "戌" };  
  
        int year = getYearByIdCard(idCard);  
        int i = (year - 3) % 10;  
        int j = (year - 3) % 12;  
  
        String retValue = "";  
        retValue = sTG[i] + sDZ[j];  
  
        return retValue;  
    } 
    
    /** 
     * 根据身份编号获取生日年 
     *  
     * @param idCard 
     *            身份编号 
     * @return 生日(yyyy) 
     */  
    public static Short getYearByIdCard(String idCard) {  
        Integer len = idCard.length();  
        if (len < 15) {  
            return null;  
        } else if (len == 15) {  
            idCard = convertIdcarBy15bit(idCard);  
        }  
        return Short.valueOf(idCard.substring(6, 10));  
    }  
  
    /** 
     * 根据身份编号获取生日月 
     *  
     * @param idCard 
     *            身份编号 
     * @return 生日(MM) 
     */  
    public static Short getMonthByIdCard(String idCard) {  
        Integer len = idCard.length();  
        if (len < 15) {  
            return null;  
        } else if (len == 18) {  
            idCard = convertIdcarBy15bit(idCard);  
        }  
        return Short.valueOf(idCard.substring(10, 12));  
    }  
  
    /** 
     * 根据身份编号获取生日天 
     *  
     * @param idCard 
     *            身份编号 
     * @return 生日(dd) 
     */  
    public static Short getDateByIdCard(String idCard) {  
        Integer len = idCard.length();  
        if (len < 15) {  
            return null;  
        } else if (len == 15) {  
            idCard = convertIdcarBy15bit(idCard);  
        }  
        return Short.valueOf(idCard.substring(12, 14));  
    } 
    
    /**
     * 将15位的身份证转成18位身份证
     *
     * @param idcard
     * @return
     */
    public static String convertIdcarBy15bit(String idcard) {
        if (idcard == null) {
            return null;
        }

        // 非15位身份证
        if (idcard.length() != 15) {
            return null;
        }

        // 15全部为数字
        if (!isDigital(idcard)) {
            return null;
        }

        String provinceid = idcard.substring(0, 2);
        // 校验省份
        if (!checkProvinceid(provinceid)) {
            return null;
        }

        String birthday = idcard.substring(6, 12);

        SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd");

        Date birthdate = null;
        try {
            birthdate = sdf.parse(birthday);
            String tmpDate = sdf.format(birthdate);
            if (!tmpDate.equals(birthday)) {// 身份证日期错误
                return null;
            }

        } catch (ParseException e1) {
            return null;
        }

        Calendar cday = Calendar.getInstance();
        cday.setTime(birthdate);
        String year = String.valueOf(cday.get(Calendar.YEAR));

        String idcard17 = idcard.substring(0, 6) + year + idcard.substring(8);

        char c[] = idcard17.toCharArray();
        String checkCode = "";

        // 将字符数组转为整型数组
        int bit[] = converCharToInt(c);

        int sum17 = 0;
        sum17 = getPowerSum(bit);

        // 获取和值与11取模得到余数进行校验码
        checkCode = getCheckCodeBySum(sum17);

        // 获取不到校验位
        if (null == checkCode) {
            return null;
        }
        // 将前17位与第18位校验码拼接
        idcard17 += checkCode;
        return idcard17;
    }

    /**
     * 校验省份
     *
     * @param provinceid
     * @return 合法返回TRUE，否则返回FALSE
     */
    private static boolean checkProvinceid(String provinceid) {
        for (String id : cityCode) {
            if (id.equals(provinceid)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 根据身份证号获取省份
     * 使用时请先校验身份证证件号码是否合法
     *
     * @param idcard
     * @return 合法返回省份，否则返回 null
     */
    public static String getProvince(String idcard){
    	String provinceid = idcard.substring(0, 2);
    	return cityCodes.get(provinceid);
    }
    
    /**
     * 数字验证
     *
     * @param str
     * @return
     */
    private static boolean isDigital(String str) {
        return str.matches("^[0-9]*$");
    }

    /**
     * 将身份证的每位和对应位的加权因子相乘之后，再得到和值
     *
     * @param bit
     * @return
     */
    private static int getPowerSum(int[] bit) {

        int sum = 0;

        if (power.length != bit.length) {
            return sum;
        }

        for (int i = 0; i < bit.length; i++) {
            for (int j = 0; j < power.length; j++) {
                if (i == j) {
                    sum = sum + bit[i] * power[j];
                }
            }
        }
        return sum;
    }

    /**
     * 将和值与11取模得到余数进行校验码判断
     *
     * @param checkCode
     * @param sum17
     * @return 校验位
     */
    private static String getCheckCodeBySum(int sum17) {
        String checkCode = null;
        switch (sum17 % 11) {
            case 10:
                checkCode = "2";
                break;
            case 9:
                checkCode = "3";
                break;
            case 8:
                checkCode = "4";
                break;
            case 7:
                checkCode = "5";
                break;
            case 6:
                checkCode = "6";
                break;
            case 5:
                checkCode = "7";
                break;
            case 4:
                checkCode = "8";
                break;
            case 3:
                checkCode = "9";
                break;
            case 2:
                checkCode = "x";
                break;
            case 1:
                checkCode = "0";
                break;
            case 0:
                checkCode = "1";
                break;
        }
        return checkCode;
    }

    /**
     * 将字符数组转为整型数组
     *
     * @param c
     * @return
     * @throws NumberFormatException
     */
    private static int[] converCharToInt(char[] c) throws NumberFormatException {
        int[] a = new int[c.length];
        int k = 0;
        for (char temp : c) {
            a[k++] = Integer.parseInt(String.valueOf(temp));
        }
        return a;
    }

    public static void main(String[] args) throws Exception {
        String idcard15 = "130321860311519";
        String idcard18 = "52242619811105565X";//52242619811105565X     210102198617083732
        // 15位身份证
        System.out.println(isValidatedAllIdcard(idcard15));
        // 18位身份证
        System.out.println(isValidatedAllIdcard(idcard18));
        // 15位身份证转18位身份证
        System.out.println(convertIdcarBy15bit(idcard15));
        System.out.println(validateAMCard("5215299(8)"));//C668668(E)香港
        System.out.println(Arrays.toString(validateIdCard10("1000248(3)")));//1000248(3)澳门
        System.out.println(Arrays.toString(validateIdCard10("1228150(7)")));//1000248(3)澳门
        System.out.println(Arrays.toString(validateIdCard10("5157370(7)")));//1000248(3)澳门
    }
}