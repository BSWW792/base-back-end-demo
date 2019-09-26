package wtf.demo.service;

import wtf.demo.core.base.BaseService;
import wtf.demo.entity.bean.Tag;

import java.util.List;

/**
 * 标签服务
 * @author gongjf
 * @since 2019年6月13日 16:28:32
 */
public interface TagService extends BaseService<Tag> {

    /*************** 工具方法 ***************/

    /**
     * 从缓存中取出数据
     * @param parentCode
     */
    List<Tag> getCache(String parentCode);

    /**
     * 将数据放入缓存
     * @param parentCode
     * @param object
     */
    void setCache(String parentCode, Object object);

    /**
     * 删除缓存中指定数据
     * @param parentCode
     */
    void deleteCache(String parentCode);

    /*************** 缓存方法 ***************/

    /**
     * 查找父节点
     * @param code
     */
    Tag getParentNode(String code);

    /**
     * 查找子节点
     * @param parentCode
     */
    List<Tag> getChildrenNodes(String parentCode);

    /**
     * 将节点放入缓存中
     * @param parentCode
     * @param entity
     * @param isRemoveOld
     */
    void setNode(String parentCode, Tag entity, boolean isRemoveOld);

    /**
     * 移除节点，查找包含自己的缓存集合并移除自己
     * @param parentCode
     * @param code
     */
    void deleteNode(String parentCode, String code);

    /**
     * 获取树缓存
     * @param code 根节点编码
     */
    Tag getTree(String code);

    /**
     * 加载树缓存（不加载当前节点）
     * @param code
     */
    void setTree(String code);

    /**
     * 删除树缓存（不删除当前节点）
     * @param code
     */
    void deleteTree(String code);

    /*************** 持久方法 ***************/

    /**
     * 移动
     * @param newParent
     * @param entity
     */
    Tag move(Tag newParent, Tag entity);

}
