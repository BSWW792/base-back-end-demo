package wtf.demo.service;

import wtf.demo.core.base.BaseService;
import wtf.demo.entity.bean.User;

/**
 * 用户服务
 * @author gongjf
 * @since 2019年6月13日 16:28:32
 */
public interface UserService extends BaseService<User> {

    boolean hasRole(String id, String role);

}
