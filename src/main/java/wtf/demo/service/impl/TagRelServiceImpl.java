package wtf.demo.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import wtf.demo.core.base.BaseServiceImpl;
import wtf.demo.core.util.DataUtil;
import wtf.demo.core.util.StringUtils;
import wtf.demo.dao.TagDao;
import wtf.demo.dao.TagRelDao;
import wtf.demo.entity.bean.Tag;
import wtf.demo.entity.bean.TagRel;
import wtf.demo.service.TagRelService;

import java.util.List;

/**
 * 标签关联服务实现
 * @author gongjf
 * @since 2019年8月4日 上午9:37:49
 */
@Service
@Slf4j
public class TagRelServiceImpl extends BaseServiceImpl<TagRel> implements TagRelService {

    @Value("${system.test-environment}")
    private boolean testEnvironment = false;

    @Autowired
    private TagRelDao tagRelDao;

    @Autowired
    private TagDao tagDao;

    @Override
    public Tag saveBatch(TagRel entity) {
        List<String> ids = StringUtils.stringToList(entity.getRelId());
        Tag tag = tagDao.get(entity.getTagId());
        if(DataUtil.isNotEmpty(tag)) {
            for(String id : ids) {
                TagRel tr = new TagRel(entity.getTagId(), id, entity.getRelTable());
                delete(tr);
                save(tr);
            }
        }
        return tag;
    }

}
