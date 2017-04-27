package cn.org.xiwi.springboot.control;

import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cn.org.xiwi.springboot.impl.CommonImpl;
import cn.org.xiwi.springboot.inf.CommonUtils;
import cn.org.xiwi.springboot.msg.BankCardListMsg;
import cn.org.xiwi.springboot.msg.BankCardValidateInfoMsg;
import cn.org.xiwi.springboot.msg.BankInfoMsg;
import cn.org.xiwi.springboot.msg.IDCardMsg;
import cn.org.xiwi.springboot.pay.bank.AliBankCardValidatedInfo;
import cn.org.xiwi.springboot.pay.bank.BankCardValidateInfo;
import cn.org.xiwi.springboot.pay.bank.CardType;
import cn.org.xiwi.springboot.pay.utils.BankUtils;
import cn.org.xiwi.springboot.utils.OkHttpUtils;
import cn.org.xiwi.springboot.utils.OkHttpUtils.MNetCallback;

@RestController
public class CommonController {

	private static Logger logger = LoggerFactory.getLogger(CommonController.class.getSimpleName());

	CommonUtils common = new CommonImpl();

	@RequestMapping("/idCardInfo")
	private IDCardMsg idCardInfoGet(@RequestParam(value = "format", defaultValue = "") String format,
			@RequestParam(value = "idCardNum", defaultValue = "") String idCardNum) {
		logger.debug("idCardInfoGet");
		return common.idCardInfo(format, idCardNum);
	}

	@RequestMapping(value = "/idCardInfo", method = RequestMethod.POST)
	private IDCardMsg idCardInfoPost(@RequestParam(value = "format", defaultValue = "") String format,
			@RequestParam(value = "idCardNum", defaultValue = "") String idCardNum) {
		logger.debug("idCardInfoPost");
		return common.idCardInfo(format, idCardNum);
	}

	@RequestMapping("/bankCardList")
	private BankCardListMsg bankCardListGet(){
		BankCardListMsg bankCardListMsg = new BankCardListMsg();
		bankCardListMsg.setCode(0);
		bankCardListMsg.setMsg("处理成功");
		bankCardListMsg.setData(BankUtils.getSupportBanks());
		return bankCardListMsg;
	}
	
	@RequestMapping("/bankCardBin")
	private BankInfoMsg bankCardInfoGet(
			@RequestParam(value = "cardBin", defaultValue = "") String cardBin) {
		BankInfoMsg bankInfoMsg = new BankInfoMsg();
		String[] arr = null;
		if (cardBin.length() >= 6) {
			char[] cardNumber = cardBin.toCharArray();// 卡号
			String name = BankUtils.getNameOfBank(cardNumber, 0);// 获取银行卡的信息
			arr = name.split("\\.");
		}else {
			bankInfoMsg.setCode(-1);
			bankInfoMsg.setMsg("获取失败,长度不符合银行卡bin码规则");
			return bankInfoMsg;
		}
		
		if (arr != null && arr.length == 2) {
			bankInfoMsg.setData(BankUtils.getBank(arr[0]));
			bankInfoMsg.setCode(0);
			bankInfoMsg.setMsg("获取成功");
		}else {
			bankInfoMsg.setCode(-1);
			bankInfoMsg.setMsg("获取失败");
		}
		return bankInfoMsg;
	}
	
	@RequestMapping("/bankCardValidate")
	private BankCardValidateInfoMsg bankCardValidateInfoGet(
			@RequestParam(value = "cardNum", defaultValue = "") String cardNum) {
		logger.debug("bankCardValidateInfoGet in");
		OkHttpUtils httpUtils = OkHttpUtils.getInstance();
		final CountDownLatch latch = new CountDownLatch(1);
		BankCardValidateInfoMsg bankCardValidateInfoMsg = new BankCardValidateInfoMsg();
		final MNetCallback<AliBankCardValidatedInfo> callback = new MNetCallback<AliBankCardValidatedInfo>(
				AliBankCardValidatedInfo.class) {

			@Override
			public void onFailure(AliBankCardValidatedInfo error) {
				System.out.println(error);
				bankCardValidateInfoMsg.setCode(error.isValidated() ? 0 : -1);
				bankCardValidateInfoMsg.setMsg("验证不通过");
				latch.countDown();
			}

			@Override
			public void onSuccess(AliBankCardValidatedInfo resp) {
				System.out.println(resp);
				bankCardValidateInfoMsg.setCode(resp.isValidated() ? 0 : -1);
				bankCardValidateInfoMsg.setMsg(resp.isValidated() ? "验证通过" : resp.getMessages().get(0).getErrorCodes());
				if (resp.isValidated()) {
					BankCardValidateInfo bankCardValidateInfo = new BankCardValidateInfo();
					bankCardValidateInfo.setBank(resp.getBank());
					bankCardValidateInfo.setCardType(resp.getCardType());
					bankCardValidateInfo.setCardNum(resp.getKey());
					bankCardValidateInfo.setValidated(resp.isValidated());
					bankCardValidateInfo.setCardTypeName(CardType.SAVINGS.getCardType().equals(resp.getCardType())
							? CardType.SAVINGS.getCardTypeName() : CardType.VISA.getCardTypeName());
					bankCardValidateInfoMsg.setData(bankCardValidateInfo);
				}
				latch.countDown();
			}
		};

		httpUtils.doGet(
				"https://ccdcapi.alipay.com/validateAndCacheCardInfo.json?_input_charset=utf-8&cardNo="+cardNum+"&cardBinCheck=true",
				null, null, callback);
		try {
			latch.await();
		} catch (InterruptedException e) {
		}
		logger.debug("bankCardValidateInfoGet out");
		return bankCardValidateInfoMsg;
	}
}
