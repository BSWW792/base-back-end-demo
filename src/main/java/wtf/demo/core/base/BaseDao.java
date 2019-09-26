package wtf.demo.core.base;

import wtf.demo.core.pagination.Page;
import wtf.demo.core.pagination.Pagination;
import wtf.demo.core.pagination.Sortable;

import java.util.List;

/**
 * 基础持久层
 * @author gongjf
 * @since 2019年2月27日 下午3:31:01
 */
public interface BaseDao<T extends Base> {

    /**
     * 新增
     * @param entity
     * @return
     */
	T add(T entity);

    /**
     * 批量新增
     * 不过滤空值
     * @param entityList
     * @return
     */
    int addBatch(List<T> entityList);

    /**
     * 更新
     * @param entity
     * @param isAllowNull
     * @return
     */
    T update(T entity, boolean isAllowNull);

    /**
     * 批量更新
     * 不过滤空值
     * @param entityList
     * @return
     */
    int updateBatch(List<T> entityList);

    /**
     * 保存
     * @param entity
     * @param isAllowNull
     * @return
     */
    T save(T entity, boolean isAllowNull);

    /**
     * 根据实体条件进行复数删除
     * @param entity
     * @return
     */
    int delete(T entity);

    /**
     * 删除
     * @param id
     * @return
     */
    int delete(String id);

    /**
     * 根据ID列表进行复数删除
     * @param idList
     * @return
     */
    int deleteBatchId(List<String> idList);

    /**
     * 根据实体条件进行计数
     * @param entity
     * @return
     */
    int count(T entity);

    /**
     * 根据sql进行计数
     * @param sql
     * @return
     */
    int countByQuerySql(StringBuffer sql);

    /**
     * 根据ID获取单条记录
     * @param id
     * @return
     */
    T get(String id);

    /**
     * 根据多个实体条件获取单条记录
     * 因返回结果列表的第一条，该方法获取的数据有可能不正确
     * @param entity
     * @return
     */
    T get(T entity);

    /**
     * 根据实体条件和排序参数查找单条记录
     * @param entity
     * @param sortable
     * @return
     */
    T getWithSort(T entity, Sortable sortable);

    /**
     * 根据实体条件查询列表
     * @param entity
     * @param sortable
     * @return
     */
    List<T> list(T entity, Sortable sortable);

    /**
     * 根据实体条件查询分页列表
     * @param entity
     * @param sortable
     * @param pagination
     * @return
     */
    Page<T> page(T entity, Sortable sortable, Pagination pagination);

    /**
     * 根据实体条件查询ID列表
     * @param entity
     * @param sortable
     * @return
     */
    List<String> idList(T entity, Sortable sortable);

    /**
     * 根据实体条件查询ID分页列表
     * @param entity
     * @param sortable
     * @param pagination
     * @return
     */
    Page<String> idPage(T entity, Sortable sortable, Pagination pagination);

}
