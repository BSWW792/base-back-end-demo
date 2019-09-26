package wtf.demo.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import wtf.demo.task.TagCacheTask;

/**
 * 系统启动监听器
 * @author gongjf
 * @since 2019年4月18日 10:24:48
 */
@Component
@Slf4j
public class StartupListener implements ApplicationListener<ContextRefreshedEvent> {

    @Value("${system.test-environment}")
    private boolean testEnvironment = false;

    @Autowired
    private TagCacheTask tagCacheTask;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        try {
            // 加载标签树缓存
            tagCacheTask.setTree(null);
        }
        catch (Exception e) {
            if(testEnvironment) e.printStackTrace();
        }
    }

}
