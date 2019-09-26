package wtf.demo.task;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import wtf.demo.service.impl.TagServiceImpl;

import java.util.Random;

/**
 * 数据字典任务
 *
 * @author: gongjf
 * @date: 2019年5月5日 10:24:48
 */
@Component
@Slf4j
public class TagCacheTask {

    public static Random random = new Random();

    @Autowired
    private TagServiceImpl tagService;

    /**
     * 加载字典树缓存（不加载当前节点）
     * @throws Exception
     */
    @Async("taskExecutor")
    public void setTree(String code) throws Exception {
        // 睡眠一段时间后执行
        Thread.sleep(100);
        tagService.setTree(code);
    }

    /**
     * 删除字典树缓存（不删除当前节点）
     * @throws Exception
     */
    @Async("taskExecutor")
    public void deleteTree(String code) throws Exception {
        // 睡眠一段时间后执行
        Thread.sleep(100);
        tagService.deleteTree(code);
    }

}
