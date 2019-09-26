package wtf.demo.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import wtf.demo.core.base.BaseServiceImpl;
import wtf.demo.core.util.RedisUtil;
import wtf.demo.dao.MenuDao;
import wtf.demo.entity.bean.Menu;
import wtf.demo.service.MenuService;

/**
 * 菜单服务实现
 * @author gongjf
 * @since 2019年8月4日 上午9:37:49
 */
@Service
@Slf4j
public class MenuServiceImpl extends BaseServiceImpl<Menu> implements MenuService {

    @Value("${system.test-environment}")
    private boolean testEnvironment = false;

    @Autowired
    private MenuDao menuDao;

    @Autowired
    private RedisUtil redisUtil;

    // 缓存前缀（冒号分隔会在redis中进行分级）
    @Value("${cache.prefix}")
    private String cachePrefix;

    // 模块前缀（冒号分隔会在redis中进行分级）
    @Value("${cache.menu.prefix}")
    private String menuPrefix;

    // 根节点编码
    @Value("${cache.menu.root-code}")
    private String rootCode;

    // 批量操作计数
    private int operaTotal = 0;


}
