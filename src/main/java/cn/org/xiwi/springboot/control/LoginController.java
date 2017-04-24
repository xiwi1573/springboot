package cn.org.xiwi.springboot.control;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cn.org.xiwi.springboot.impl.UserLoginImpl;
import cn.org.xiwi.springboot.inf.UserLogin;
import cn.org.xiwi.springboot.msg.UserMsg;

@RestController
public class LoginController {
	UserLogin userLogin = new UserLoginImpl();
	private static Logger logger = LoggerFactory.getLogger(LoginController.class.getSimpleName());
	
	@RequestMapping("/login")
	private UserMsg loginGet(@RequestParam(value="loginName", defaultValue="") String loginName,@RequestParam(value="pwd", defaultValue="") String pwd,@RequestParam(value="loginType", defaultValue="") String loginType){
		logger.debug("loginGet");
		return userLogin.login(loginName, pwd, loginType);
	}
	
	@RequestMapping(value = "/login" ,method=RequestMethod.POST)
	private UserMsg loginPost(@RequestParam(value="loginName", required=true,defaultValue="") String loginName,@RequestParam(value="pwd", required=true,defaultValue="") String pwd,@RequestParam(value="loginType", required=true,defaultValue="") String loginType){
		logger.debug("loginPost");
		return userLogin.login(loginName, pwd, loginType);
	}
}
