package wtf.demo.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import wtf.demo.core.base.BaseApi;
import wtf.demo.core.util.WebUtil;
import wtf.demo.entity.bean.Log;
import wtf.demo.service.LogService;

/**
 * 日志API
 * @author gongjf
 * @since 2019年4月4日 上午9:47:11
 */
@Api("日志API")
@RequestMapping("/log")
@RestController
@Slf4j
public class LogApi extends BaseApi<Log> {

    @Value("${system.test-environment}")
    private boolean testEnvironment = false;

    @Autowired
    private LogService logService;


    @ApiOperation(value = "版本日志", notes = "版本日志")
    @GetMapping(value = "/getReleaseLog")
    @ResponseStatus(HttpStatus.OK)
    public Object getReleaseLog() {
        Object result = null;
        try {
            result = logService.getReleaseLog();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            if(testEnvironment) e.printStackTrace();
        }
        return WebUtil.resultData(result);
    }

}