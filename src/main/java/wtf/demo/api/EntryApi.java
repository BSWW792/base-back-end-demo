package wtf.demo.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import wtf.demo.core.util.*;
import wtf.demo.entity.Enum.ReturnStatusType;
import wtf.demo.entity.bean.Menu;
import wtf.demo.entity.bean.Role;
import wtf.demo.entity.bean.User;
import wtf.demo.service.UserService;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * 入口API
 * @author gongjf
 * @since 2019年6月13日 17:48:29
 */
@Api("入口API")
@RequestMapping(value = "/entry")
@RestController
@Slf4j
public class EntryApi {

	@Value("${system.test-environment}")
	private boolean testEnvironment = false;

	@Autowired
	private UserService userService;


	@ApiOperation(value = "测试", notes = "测试")
	@GetMapping(value = "/test")
	@ResponseStatus(HttpStatus.OK)
	public Object test() {
		return "test ok";
	}

	@ApiOperation(value = "登入", notes = "登入")
	@PutMapping(value = "/login")
	@ResponseStatus(HttpStatus.OK)
	public Object login(@RequestBody Map<String, Object> params, HttpServletRequest request) {
		try {
			if(DataUtil.isNotEmpty(params)) {
				User user = JacksonUtil.mapToClass(params, User.class);
				if(DataUtil.isNotEmpty(user.getName()) && DataUtil.isNotEmpty(user.getPassword())) {
					user = userService.get(user);
					if(DataUtil.isNotEmpty(user)) {
						SessionUtil.I.setCurrentUser(request.getSession(), user);
						// 获取权限信息
						if (DataUtil.isEmpty(user.getRoleCode())) {
							return WebUtil.resultMsg(ReturnStatusType.NoAccess, "该用户暂时没有权限,请联系管理员分配权限！");
						}
						Map<String, Object> data = JacksonUtil.objectToMap(user);
						data.put("menus", getUserMenus(user.getRoles()));
						return WebUtil.resultDataMsg(data, "登入成功");
					}
					return WebUtil.resultMsg(ReturnStatusType.NoData, "用户名或密码错误!!!");
				}
			}
		}
		catch (Exception e) {
			log.error(e.getMessage(), e);
            if(testEnvironment) e.printStackTrace();
            return WebUtil.resultMsg(ReturnStatusType.Error, "");
		}
		return WebUtil.resultMsg(ReturnStatusType.NoData, "请填写帐号密码!!!");
	}

	@ApiOperation(value = "登出", notes = "登出")
	@DeleteMapping(value = "/logout")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public Object logout(HttpServletRequest request) {
		SessionUtil.I.setCurrentUser(request.getSession(), null);
		return WebUtil.resultMsg(ReturnStatusType.Sucess, "登出成功");
	}

	@ApiOperation(value = "注册", notes = "注册")
	@PostMapping(value = "/register")
	@ResponseStatus(HttpStatus.OK)
	public Object register(@RequestBody Map<String, Object> params, HttpServletRequest request) {
		try {
			if(DataUtil.isNotEmpty(params)) {
				User user = JacksonUtil.mapToClass(params, User.class);
				if(DataUtil.isNotEmpty(user.getName()) && DataUtil.isNotEmpty(user.getPassword())) {
					// 先查找有没有同名用户
					User u = userService.get(new User(user.getName()));
					if(DataUtil.isNotEmpty(u)) {
						return WebUtil.resultMsg(ReturnStatusType.HasUnValidParameter, "该用户名称已存在！");
					}
					user.setCreateTime(new Date());
					user = userService.save(user);
					if(DataUtil.isNotEmpty(user)) {
						SessionUtil.I.setCurrentUser(request.getSession(), user);
//						// 获取权限信息
//						if (StringUtils.isEmpty(user.getRoleCode())) {
//							return WebUtil.resultMsg(ReturnStatusType.NoAccess, "该用户暂时没有权限,请联系管理员分配权限！");
//						}
						Map<String, Object> data = JacksonUtil.objectToMap(user);
//						data.put("menus", getUserMenus(user.getRoles()));
						return WebUtil.resultDataMsg(data, "注册成功，请转到登录页面进行登录");
					}
					return WebUtil.resultMsg(ReturnStatusType.ApiDataError, "请填写帐号密码!!!");
				}
			}
		}
		catch (Exception e) {
			log.error(e.getMessage(), e);
            if(testEnvironment) e.printStackTrace();
            return WebUtil.resultMsg(ReturnStatusType.Error, "");
		}
		return WebUtil.resultMsg(ReturnStatusType.Error, "注册失败!!!");
	}

	@ApiOperation(value = "修改密码", notes = "修改密码")
	@PostMapping(value = "/modifyPass")
	@ResponseStatus(HttpStatus.OK)
	public Object modifyPass(@RequestBody Map<String, Object> params, HttpServletRequest request) {
		try {
			if(DataUtil.isNotEmpty(params)) {
				User user = JacksonUtil.mapToClass(params, User.class);
				if(DataUtil.isNotEmpty(user.getName()) && DataUtil.isNotEmpty(user.getPassword())) {
					User findOne = new User();
					findOne.setName(user.getName());
					findOne = userService.get(findOne);
					if(DataUtil.isNotEmpty(findOne)) {
						findOne.setPassword(user.getPassword());
						user = userService.save(findOne);
						SessionUtil.I.setCurrentUser(request.getSession(), user);
						// 获取权限信息
						if (DataUtil.isEmpty(user.getRoleCode())) {
							return WebUtil.resultMsg(ReturnStatusType.NoAccess, "该用户暂时没有权限,请联系管理员分配权限！");
						}
						Map<String, Object> data = JacksonUtil.objectToMap(user);
						data.put("menus", getUserMenus(user.getRoles()));
						return WebUtil.resultDataMsg(data, "密码修改成功");
					}
					return WebUtil.resultMsg(ReturnStatusType.NoData, "帐号不存在");
				}
			}
		}
		catch (Exception e) {
			log.error(e.getMessage(), e);
            if(testEnvironment) e.printStackTrace();
            return WebUtil.resultMsg(ReturnStatusType.Error, "");
		}
		return WebUtil.resultMsg(ReturnStatusType.NoData, "请填写帐号密码!!!");
	}

	/**
	 * 拉取用户菜单
	 * @param roles
	 * @return
	 */
	private List getUserMenus(List<Role> roles) throws CloneNotSupportedException {
		// 遍历每个模块，构建模块-菜单关系
		List<Object> organizedMenu = new ArrayList<>();

		return organizedMenu;
	}
}
