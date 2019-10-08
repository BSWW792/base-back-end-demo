package wtf.demo.api;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wtf.demo.core.base.BaseApi;
import wtf.demo.entity.bean.User;
import wtf.demo.service.UserService;

/**
 * 用户API
 * @author gongjf
 * @since 2019年6月13日 17:48:04
 */
@Api("用户API")
@RequestMapping("/user")
@RestController
@Slf4j
public class UserApi extends BaseApi<User> {

    @Autowired
    private UserService userService;

}
