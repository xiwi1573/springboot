package cn.org.xiwi.springboot.impl;

import java.util.HashMap;
import java.util.Map;

import cn.org.xiwi.springboot.bean.User;
import cn.org.xiwi.springboot.inf.UserLogin;
import cn.org.xiwi.springboot.msg.UserMsg;

public class UserLoginImpl implements UserLogin {

	private static Map<String, User> mCacheUserInfos = new HashMap<String, User>();
	
	@Override
	public UserMsg login(String userName, String pwd, String loginType) {
		synchronized (mCacheUserInfos) {
			User user = new User();
			user.setLoginName(userName);
			user.setPwd(pwd);
			UserMsg userMsg = new UserMsg();
			if (mCacheUserInfos.containsValue(user)) {
				userMsg.setData(mCacheUserInfos.get(""+user.hashCode()));
				userMsg.setCode(0);
				userMsg.setMsg("登录成功");
				return userMsg;
			}
			userMsg.setCode(-1);
			userMsg.setMsg("账户名不存在");
			return userMsg;
		}
	}

	@Override
	public UserMsg logout(String token) {
		if (mCacheUserInfos.containsKey(token)) {
			synchronized (mCacheUserInfos) {
				mCacheUserInfos.remove(token);
				return new UserMsg(); 
			}
		}
		return null;
	}

}
