package wtf.demo.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import wtf.demo.core.base.BaseServiceImpl;
import wtf.demo.core.util.DataUtil;
import wtf.demo.dao.UserDao;
import wtf.demo.entity.bean.User;
import wtf.demo.service.UserService;

/**
 * 用户服务实现
 * @author gongjf
 * @since 2019年6月13日 17:45:23
 */
@Service
@Slf4j
public class UserServiceImpl extends BaseServiceImpl<User> implements UserService {

	@Value("${system.test-environment}")
	private boolean testEnvironment = false;

	@Autowired

	private UserDao userDao;

	@Override
	public boolean hasRole(String id, String role) {
		if(DataUtil.isEmpty(id) || DataUtil.isEmpty(role)) return false;
		User user = userDao.get(id);
		if(DataUtil.isNotEmpty(user) && user.getRoleCode().contains(role)) return true;
		return false;
	}

}
