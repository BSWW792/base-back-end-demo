package wtf.demo.core.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import wtf.demo.core.util.DataUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Date;

/**
 * 方法计时切面
 * @author gongjf
 * @since 2019年6月13日 17:51:57
 */
@Component
@Aspect
@Order(2)
@Slf4j
public class TimeAspect {

    @Value("${system.test-environment}")
    private boolean testEnvironment = false;

    /**
     * 定义切入点，
     * 第一个*表示任何返回值，最后一个*表示任何方法，(..)表示任何参数
     * @param joinPoint 此对象中包含了被切入方法的信息
     * @return
     */
    @Around("execution(* wtf.demo.api.*.*(..))")
    public Object handlerControllerMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        Object result = null;
        try {
            if(testEnvironment) log.info("Time aspect start !");
            //获取被切入方法的参数
            Object[] args = joinPoint.getArgs();
            if(testEnvironment) {
                for (Object arg : args) {
                    if(DataUtil.isEmpty(arg)
                        || arg instanceof HttpServletRequest || arg instanceof HttpServletResponse || arg instanceof HttpSession
                        || arg instanceof MultipartFile || arg instanceof MultipartHttpServletRequest
                    ) continue;
                    log.info("args has: " + arg);
                }
            }
            long startTime = new Date().getTime();
            //执行目标方法,返回目标方法的返回值
            result = joinPoint.proceed();

            if(testEnvironment) log.info("耗时:" + (new Date().getTime() - startTime) + "ms");
        } catch (Throwable e) {
            log.error(e.getMessage());
            if(testEnvironment) e.printStackTrace();
        }

        return result;
    }

}