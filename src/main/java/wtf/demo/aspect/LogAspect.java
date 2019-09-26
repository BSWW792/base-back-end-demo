package wtf.demo.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import wtf.demo.core.util.DataUtil;
import wtf.demo.core.util.RequestUtil;
import wtf.demo.core.util.SessionUtil;
import wtf.demo.entity.bean.Log;
import wtf.demo.entity.bean.User;
import wtf.demo.service.LogService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 日志记录切面
 * @author gongjf
 * @since 2019年6月13日 17:49:09
 */
@Component
@Aspect
@Order(3)
@Slf4j
public class LogAspect {

    @Value("${system.test-environment}")
    private boolean testEnvironment = false;

    @Value("${logging.ascept.method}")
    private String asceptMethod = "delete,remove,update,modify,set,import,export";

    //注入Service用于把日志保存数据库，实际项目入库采用队列做异步
    @Resource
    private LogService logService;

    /**
     * 定义切入点，
     * 第一个*表示任何返回值，最后一个*表示任何方法，(..)表示任何参数
     *
     * @param joinPoint 此对象中包含了被切入方法的信息
     * @return
     */
    @Around("execution(* wtf.demo.api.*.*(..))")
    public Object handlerControllerMethod(ProceedingJoinPoint joinPoint) {
        Object result = null;
        try {
            if(testEnvironment) log.info("Log aspect start !");
            // 获取用户
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            User user = SessionUtil.I.getCurrentUser(request.getSession());
            // 只记录入库的系统用户
            if(DataUtil.isEmpty(user)) {
                return joinPoint.proceed();
            }

            // 获取操作方法
            MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
            String method = methodSignature.getMethod().getName();
            // 只记录指定方法
            String[] asceptMethodArr = asceptMethod.split(",");
            for(int i=0, iLen=asceptMethodArr.length; i<iLen; i++) {
                String am = asceptMethodArr[i];
                if(method.contains(am)) {
                    break;
                } else if(i == iLen-1) {
                    return joinPoint.proceed();
                }
            }

            // 获取被切入方法的参数
            Object[] args = joinPoint.getArgs();
            Map<String, Object> argJson = new HashMap<String, Object>();
            int i=0;
            for (Object arg : args) {
                // 跳过空值、http对象、文件对象
                if(DataUtil.isEmpty(arg)
                    || arg instanceof HttpServletRequest || arg instanceof HttpServletResponse || arg instanceof HttpSession
                    || arg instanceof MultipartFile || arg instanceof MultipartHttpServletRequest
                ) {
                    i++;
                    continue;
                }
                argJson.put("" + i, arg);
            }

            // 获取IP地址
            String ip = RequestUtil.getIpAddr(request);

            // 计时
            long startTime = new Date().getTime();
            //执行目标方法,返回目标方法的返回值
            result = joinPoint.proceed();

            // 保存日志
            Log log = new Log();
            log.setCreateTime(new Date());
            log.setCreator(user.getName());
            log.setCreatorId(user.getId());
            log.setArgs(argJson);
            log.setMethod((joinPoint.getTarget().getClass().getName() + "." + joinPoint.getSignature().getName()));
            log.setSpendTime((new Date().getTime() - startTime));
            log.setLogType(1);
            log.setIp(ip + ":" + request.getRemotePort());
            log.setHost(request.getRemoteHost());
            log.setUrl(request.getRequestURL().toString());
            logService.save(log);

        } catch (Throwable e) {
            log.error(e.getMessage());
            if(testEnvironment) e.printStackTrace();
        }

        return result;
    }

}
