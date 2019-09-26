package wtf.demo.dao;

import wtf.demo.core.base.BaseDao;
import wtf.demo.entity.bean.Tag;

import java.util.List;

/**
 * 标签持久层
 * @author gongjf
 * @since 2019年6月13日 16:21:59
 */
public interface TagDao extends BaseDao<Tag> {

    List<Tag> findByParentId(String parentId);

    List<Tag> findByParentCode(String parentCode);

}
