package wtf.demo.core.base;

import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import wtf.demo.core.pagination.Pagination;
import wtf.demo.core.pagination.Sortable;
import wtf.demo.core.util.DataUtil;
import wtf.demo.core.util.JacksonUtil;
import wtf.demo.core.util.WebUtil;

import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Map;

/**
 * 基础API
 * @author gongjf
 * @since 2019年6月11日 17:58:51
 */
@RestController
@Slf4j
public abstract class BaseApi<T extends Base> {

    @Value("${system.test-environment}")
    private boolean testEnvironment = false;

    // 实体类名
    private Class<T> entityClass;

    @Autowired
    private BaseService<T> service;

    public BaseApi() {
        // 获取泛型类型
        entityClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    @ApiOperation(value = "保存", notes = "保存")
    @PostMapping(value = "/")
    @ResponseStatus(HttpStatus.CREATED)
    public Object save(@RequestBody T param) {
        Object result = null;
        try {
            result = service.save(param);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            if(testEnvironment) e.printStackTrace();
        }
        return WebUtil.resultData(result);
    }

    @ApiOperation(value = "删除", notes = "根据id删除")
    @DeleteMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Object delete(@PathVariable("id") String id) {
        Object result = null;
        try {
            service.delete(id);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            if(testEnvironment) e.printStackTrace();
        }
        return WebUtil.resultData(result);
    }

    @ApiOperation(value = "批量删除", notes = "根据id串批量删除")
    @DeleteMapping(value = "/deleteBatchId")
    @ResponseStatus(HttpStatus.OK)
    public Object deleteBatchId(@RequestBody Map<String, Object> param) {
        Object result = null;
        try {
            List<String> ids = (List<String>) param.get("ids");
            service.deleteBatchId(ids);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            if(testEnvironment) e.printStackTrace();
        }
        return WebUtil.resultData(result);
    }

    @ApiOperation(value = "获取计数", notes = "获取计数")
    @PutMapping(value = "/count")
    @ResponseStatus(HttpStatus.OK)
    public Object count(@RequestBody T param) {
        Object result = null;
        try {
            result = service.count(param);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            if(testEnvironment) e.printStackTrace();
        }
        return WebUtil.resultData(result);
    }

    @ApiOperation(value = "根据id获取", notes = "根据id获取")
    @GetMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Object get(@PathVariable("id") String id) {
        Object result = null;
        try {
            result = service.get(id);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            if(testEnvironment) e.printStackTrace();
        }
        return WebUtil.resultData(result);
    }

    @ApiOperation(value = "获取", notes = "获取")
    @PutMapping(value = "/entity")
    @ResponseStatus(HttpStatus.OK)
    public Object get(@RequestBody T param) {
        Object result = null;
        try {
            result = service.get(param);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            if(testEnvironment) e.printStackTrace();
        }
        return WebUtil.resultData(result);
    }

    @ApiOperation(value = "获取排序后的", notes = "获取排序后的")
    @PutMapping(value = "/getWithSort")
    @ResponseStatus(HttpStatus.OK)
    public Object getWithSort(@RequestBody Map<String, Object> param) {
        Object result = null;
        try {
            String sortField = (String) param.get("sortField");
            String sortDirect = (String) param.get("sortDirect");
            T entity = JacksonUtil.mapToClass(param, entityClass);
            result = service.getWithSort(entity, new Sortable(sortField, sortDirect));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            if(testEnvironment) e.printStackTrace();
        }
        return WebUtil.resultData(result);
    }

    @ApiOperation(value = "获取列表", notes = "获取列表")
    @PutMapping(value = "/list")
    @ResponseStatus(HttpStatus.OK)
    public Object list(@RequestBody Map<String, Object> param) {
        Object result = null;
        try {
            // 获取排序参数
            String sortField = (String) param.get("sortField");
            String sortDirect = (String) param.get("sortDirect");
            T entity = JacksonUtil.mapToClass(param, entityClass);
            result = service.list(entity, new Sortable(sortField, sortDirect));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            if(testEnvironment) e.printStackTrace();
        }
        return WebUtil.resultData(result);
    }

    @ApiOperation(value = "获取分页列表", notes = "获取分页列表")
    @PutMapping(value = "/page")
    @ResponseStatus(HttpStatus.OK)
    public Object page(@RequestBody Map<String, Object> param) {
        Object result = null;
        try {
            // 获取排序参数
            String sortField = (String) param.get("sortField");
            String sortDirect = (String) param.get("sortDirect");
            // 获取分页参数
            Integer pageNo = (Integer) param.get("pageNo");
            Integer pageSize = (Integer) param.get("pageSize");
            pageNo = DataUtil.isEmpty(pageNo) || pageNo <= 1 ? 1 : pageNo;
            pageSize = DataUtil.isEmpty(pageSize) ? 10 : pageSize;
            T entity = JacksonUtil.mapToClass(param, entityClass);
            result = service.page(entity, new Sortable(sortField, sortDirect), new Pagination(pageNo, pageSize));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            if(testEnvironment) e.printStackTrace();
        }
        return WebUtil.resultData(result);
    }

    @ApiOperation(value = "获取ID列表", notes = "获取ID列表")
    @PutMapping(value = "/idList")
    @ResponseStatus(HttpStatus.OK)
    public Object idList(@RequestBody Map<String, Object> param) {
        Object result = null;
        try {
            // 获取排序参数
            String sortField = (String) param.get("sortField");
            String sortDirect = (String) param.get("sortDirect");
            T entity = JacksonUtil.mapToClass(param, entityClass);
            result = service.idList(entity, new Sortable(sortField, sortDirect));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            if(testEnvironment) e.printStackTrace();
        }
        return WebUtil.resultData(result);
    }

    @ApiOperation(value = "获取ID分页列表", notes = "获取ID分页列表")
    @PutMapping(value = "/idPage")
    @ResponseStatus(HttpStatus.OK)
    public Object idPage(@RequestBody Map<String, Object> param) {
        Object result = null;
        try {
            // 获取排序参数
            String sortField = (String) param.get("sortField");
            String sortDirect = (String) param.get("sortDirect");
            // 获取分页参数
            Integer pageNo = (Integer) param.get("pageNo");
            Integer pageSize = (Integer) param.get("pageSize");
            pageNo = DataUtil.isEmpty(pageNo) || pageNo <= 1 ? 1 : pageNo;
            pageSize = DataUtil.isEmpty(pageSize) ? 10 : pageSize;
            T entity = JacksonUtil.mapToClass(param, entityClass);
            result = service.idPage(entity, new Sortable(sortField, sortDirect), new Pagination(pageNo, pageSize));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            if(testEnvironment) e.printStackTrace();
        }
        return WebUtil.resultData(result);
    }

}
