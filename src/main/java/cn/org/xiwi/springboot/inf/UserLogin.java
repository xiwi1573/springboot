package cn.org.xiwi.springboot.inf;

import cn.org.xiwi.springboot.msg.UserMsg;

public interface UserLogin {
	UserMsg login(String userName, String pwd, String loginType);

	UserMsg logout(String token);
}
