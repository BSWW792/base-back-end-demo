package wtf.demo.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import wtf.demo.core.base.BaseServiceImpl;
import wtf.demo.dao.RoleDao;
import wtf.demo.entity.bean.Role;
import wtf.demo.service.RoleService;

/**
 * 角色服务实现
 * @author gongjf
 * @since 2019年8月4日 上午9:37:49
 */
@Service
@Slf4j
public class RoleServiceImpl extends BaseServiceImpl<Role> implements RoleService {

    @Value("${system.test-environment}")
    private boolean testEnvironment = false;

    @Autowired
    private RoleDao roleDao;

}
