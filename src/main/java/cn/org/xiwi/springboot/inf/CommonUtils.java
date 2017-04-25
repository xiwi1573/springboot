package cn.org.xiwi.springboot.inf;

import cn.org.xiwi.springboot.msg.IDCardMsg;

public interface CommonUtils {
	/**
	 * format字段为保留字段，暂时未使用
	 * 此API暂时只提供身份证号码校验，其他证件类型号码校验还有待进一步测试
	 * */
	IDCardMsg idCardInfo(String format,String idCardNum);
}
