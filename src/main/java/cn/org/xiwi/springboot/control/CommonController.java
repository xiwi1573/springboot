package cn.org.xiwi.springboot.control;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cn.org.xiwi.springboot.impl.CommonImpl;
import cn.org.xiwi.springboot.inf.CommonUtils;
import cn.org.xiwi.springboot.msg.IDCardMsg;

@RestController
public class CommonController {

	private static Logger logger = LoggerFactory.getLogger(CommonController.class.getSimpleName());
	
	CommonUtils common = new CommonImpl();
	
	@RequestMapping("/idCardInfo")
	private IDCardMsg idCardInfoGet(@RequestParam(value="format", defaultValue="") String format,@RequestParam(value="idCardNum", defaultValue="") String idCardNum){
		logger.debug("idCardInfoGet");
		return common.idCardInfo(format, idCardNum);
	}
	
	@RequestMapping(value = "/idCardInfo" ,method=RequestMethod.POST)
	private IDCardMsg idCardInfoPost(@RequestParam(value="format", defaultValue="") String format,@RequestParam(value="idCardNum", defaultValue="") String idCardNum){
		logger.debug("idCardInfoPost");
		return common.idCardInfo(format, idCardNum);
	}
}
