package wtf.demo.core.base;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import wtf.demo.core.pagination.Page;
import wtf.demo.core.pagination.Pagination;
import wtf.demo.core.pagination.Sortable;
import wtf.demo.core.util.DataUtil;

import java.util.List;

/**
 * 基础服务层实现
 * @author gongjf
 * @since 2019年6月11日 17:39:53
 */
@Service
@Slf4j
public abstract class BaseServiceImpl<T extends Base> implements BaseService <T> {

    @Value("${system.test-environment}")
    private boolean testEnvironment = false;

    @Autowired
    private BaseDao<T> dao;

    /**
     * 保存
     * @param entity
     * @return
     */
    @Override
    public T save(T entity) {
        return dao.save(entity, false);
    }

    /**
     * 批量新增
     * 不过滤空值
     * @param entityList
     * @return
     */
    @Override
    public int addBatch(List<T> entityList) {
        return dao.addBatch(entityList);
    }

    /**
     * 批量更新
     * 不过滤空值
     * @param entityList
     * @return
     */
    @Override
    public int updateBatch(List<T> entityList) {
        return dao.updateBatch(entityList);
    }

    /**
     * 根据实体条件进行复数删除
     * @param entity
     * @return
     */
    @Override
    public int delete(T entity) {
        return dao.delete(entity);
    }

    /**
     * 删除
     * @param id
     * @return
     */
    @Override
    public int delete(String id) {
        return dao.delete(id);
    }

    /**
     * 根据ID列表进行复数删除
     * @param idList
     * @return
     */
    @Override
    public int deleteBatchId(List<String> idList) {
        return dao.deleteBatchId(idList);
    }

    /**
     * 根据实体条件进行计数
     * @param entity
     * @return
     */
    @Override
    public int count(T entity) {
        return dao.count(entity);
    }

    /**
     * 根据ID获取单条记录
     * @param id
     * @return
     */
    @Override
    public T get(String id) {
        return dao.get(id);
    }

    /**
     * 根据多个实体条件获取单条记录
     * 因返回结果列表的第一条，该方法获取的数据有可能不正确
     * @param entity
     * @return
     */
    @Override
    public T get(T entity) {
        return dao.get(entity);
    }

    /**
     * 根据实体条件和排序参数查找单条记录
     * @param entity
     * @param sortable
     * @return
     */
    @Override
    public T getWithSort(T entity, Sortable sortable) {
        return dao.getWithSort(entity, sortable);
    }

    /**
     * 根据实体条件查询列表
     * @param entity
     * @param sortable
     * @return
     */
    @Override
    public List<T> list(T entity, Sortable sortable) {
        return dao.list(entity, sortable);
    }

    /**
     * 根据实体条件查询分页列表
     * @param entity
     * @param sortable
     * @param pagination
     * @return
     */
    @Override
    public Page<T> page(T entity, Sortable sortable, Pagination pagination) {
        return dao.page(entity, sortable, pagination);
    }

    /**
     * 根据实体条件查询ID列表
     * @param entity
     * @param sortable
     * @return
     */
    @Override
    public List<String> idList(T entity, Sortable sortable) {
        return dao.idList(entity, sortable);
    }

    /**
     * 根据实体条件查询ID分页列表
     * @param entity
     * @param sortable
     * @param pagination
     * @return
     */
    @Override
    public Page<String> idPage(T entity, Sortable sortable, Pagination pagination) {
        return dao.idPage(entity, sortable, pagination);
    }

}
