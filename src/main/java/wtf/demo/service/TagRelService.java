package wtf.demo.service;

import wtf.demo.core.base.BaseService;
import wtf.demo.entity.bean.Tag;
import wtf.demo.entity.bean.TagRel;

/**
 * 标签关联服务
 * @author gongjf
 * @since 2019年6月13日 16:28:32
 */
public interface TagRelService extends BaseService<TagRel> {

    Tag saveBatch(TagRel entity);

}
