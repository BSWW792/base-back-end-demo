package wtf.demo.core.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import wtf.demo.entity.Enum.ReturnStatusType;
import wtf.demo.entity.bean.User;
import wtf.demo.core.util.DataUtil;
import wtf.demo.core.util.SessionUtil;
import wtf.demo.core.util.WebUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 会话检测切面
 * @author gongjf
 * @since 2019年6月13日 17:52:17
 */
@Component
@Aspect
@Order(1)
@Slf4j
public class SessionAspect {

    @Value("${system.test-environment}")
    private boolean testEnvironment = false;

    /**
     * 定义切入点，
     * 第一个*表示任何返回值，最后一个*表示任何方法，(..)表示任何参数
     * 不拦截：EntryApi、receive前缀方法、push前缀方法
     *
     * @param joinPoint 此对象中包含了被切入方法的信息
     * @return
     */
    @Around(
        "!execution(* wtf.demo.api.EntryApi.*(..)) " +
        "&& !execution(* wtf.demo.api.UserApi.*(..)) " +
        "&& !execution(* wtf.demo.api.*.receive*(..)) " +
        "&& !execution(* wtf.demo.api.*.push*(..)) " +
        "&& execution(* wtf.demo.api.*.*(..)) "
    )
    public Object handlerControllerMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        Object result = null;
        try {
            if(testEnvironment) log.info("Token aspect start !");

            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            HttpServletRequest request = attributes.getRequest();
            User user = SessionUtil.I.getCurrentUser(request.getSession());
            if(DataUtil.isEmpty(user)) {
                return WebUtil.resultMsg(ReturnStatusType.UnLogin, "请登录后再操作");
            }

            //执行目标方法,返回目标方法的返回值
            result = joinPoint.proceed();
        } catch (Throwable e) {
            log.error(e.getMessage());
            if(testEnvironment) e.printStackTrace();
        }

        return result;
    }

}
