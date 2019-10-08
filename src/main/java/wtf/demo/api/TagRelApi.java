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
import wtf.demo.entity.Enum.ReturnStatusType;
import wtf.demo.entity.bean.TagRel;
import wtf.demo.service.TagRelService;

/**
 * 标签关联API
 * @author gongjf
 * @since 2019年9月5日 上午9:47:11
 */
@Api("标签关联API")
@RequestMapping("/tagRel")
@RestController
@Slf4j
public class TagRelApi extends BaseApi<TagRel> {

    @Autowired
    private TagRelService tagRelService;

    @ApiOperation(value = "批量保存", notes = "批量保存")
    @PostMapping(value = "/saveBatch")
    @ResponseStatus(HttpStatus.CREATED)
    public Object saveBatch(@RequestBody TagRel param) {
        Object result = null;
        try {
            result = tagRelService.saveBatch(param);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            if(testEnvironment) e.printStackTrace();
            return WebUtil.resultMsg(ReturnStatusType.Error, "");
        }
        return WebUtil.resultData(result);
    }

}
