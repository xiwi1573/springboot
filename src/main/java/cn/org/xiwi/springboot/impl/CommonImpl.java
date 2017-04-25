package cn.org.xiwi.springboot.impl;

import org.springframework.util.StringUtils;

import cn.org.xiwi.springboot.bean.IDCard;
import cn.org.xiwi.springboot.inf.CommonUtils;
import cn.org.xiwi.springboot.msg.IDCardMsg;
import cn.org.xiwi.springboot.utils.IdcardValidatorUtils;

public class CommonImpl implements CommonUtils{

	@Override
	public IDCardMsg idCardInfo(String format, String idCardNum) {
		IDCardMsg idCardMsg = new IDCardMsg();
		if (!StringUtils.isEmpty(idCardNum) && IdcardValidatorUtils.isValidatedAllIdcard(idCardNum)) {
			IDCard idCard = new IDCard();
			idCard.setIdCardNum(idCardNum);
			idCard.setAge(IdcardValidatorUtils.getAgeByIdCard(idCardNum));
			idCard.setGender(IdcardValidatorUtils.getGenderCode(idCardNum) == 1 ? "M":"F");
			idCard.setGenderCode(IdcardValidatorUtils.getGenderCode(idCardNum));
			idCard.setBirthday(IdcardValidatorUtils.getBirthByIdCard(idCardNum));
			idCard.setProvince(IdcardValidatorUtils.getProvince(idCardNum));
			idCardMsg.setData(idCard);
			idCardMsg.setCode(0);
			idCardMsg.setMsg("成功处理");
		}else {
			idCardMsg.setMsg("证件号码格式错误");
			idCardMsg.setCode(-1);
		}
		return idCardMsg;
	}

}
