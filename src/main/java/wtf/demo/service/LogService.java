package wtf.demo.service;

import wtf.demo.core.base.BaseService;
import wtf.demo.entity.bean.Log;

/**
 * 日志服务
 * @author gongjf
 * @since 2019年4月4日 上午9:37:49
 */
public interface LogService extends BaseService<Log> {

    Object getReleaseLog();

}
