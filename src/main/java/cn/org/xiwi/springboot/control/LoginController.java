package cn.org.xiwi.springboot.control;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cn.org.xiwi.springboot.impl.UserLoginImpl;
import cn.org.xiwi.springboot.inf.UserLogin;
import cn.org.xiwi.springboot.msg.UserMsg;

@RestController
public class LoginController {
	UserLogin userLogin = new UserLoginImpl();
	
	@RequestMapping("/login")
	private UserMsg login(@RequestParam(value="loginName", defaultValue="") String loginName,@RequestParam(value="pwd", defaultValue="") String pwd,@RequestParam(value="loginType", defaultValue="") String loginType){
		return userLogin.login(loginName, pwd, loginType);
	}
}
