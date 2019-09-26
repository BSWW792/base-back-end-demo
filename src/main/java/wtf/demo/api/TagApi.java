package wtf.demo.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import wtf.demo.core.base.BaseApi;
import wtf.demo.core.util.WebUtil;
import wtf.demo.entity.bean.Tag;
import wtf.demo.service.TagService;

/**
 * 标签API
 * @author gongjf
 * @since 2019年4月4日 上午9:47:11
 */
@Api("标签API")
@RequestMapping("/tag")
@RestController
@Slf4j
public class TagApi extends BaseApi<Tag> {

    @Value("${system.test-environment}")
    private boolean testEnvironment = false;

    @Autowired
    private TagService tagService;


    @ApiOperation(value = "根据code获取父标签", notes = "根据id获取父标签")
    @GetMapping(value = "/getParentNode/{code}")
    @ResponseStatus(HttpStatus.OK)
    public Object getParentNode(@PathVariable("code") String code) {
        Object result = null;
        try {
            result = tagService.getParentNode(code);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            if(testEnvironment) e.printStackTrace();
        }
        return WebUtil.resultData(result);
    }

    @ApiOperation(value = "根据id获取子标签", notes = "根据id获取子标签")
    @GetMapping(value = "/getChildrenNodes/{code}")
    @ResponseStatus(HttpStatus.OK)
    public Object getChildrenNodes(@PathVariable("code") String code) {
        Object result = null;
        try {
            result = tagService.getChildrenNodes(code);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            if(testEnvironment) e.printStackTrace();
        }
        return WebUtil.resultData(result);
    }

}
