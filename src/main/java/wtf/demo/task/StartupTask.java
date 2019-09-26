package wtf.demo.task;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Random;

/**
 * 系统启动任务
 * @author gongjf
 * @since 2019年4月18日 10:24:48
 */
@Component
@Slf4j
public class StartupTask {

    @Value("${system.test-environment}")
    private boolean testEnvironment = false;

    public static Random random = new Random();

    private final Integer sleepTime = 5000;

    @Async("taskExecutor")
    public void pullLostBlockTask() throws Exception {
        // 睡眠一段时间后执行
        Thread.sleep(sleepTime);
    }

}
