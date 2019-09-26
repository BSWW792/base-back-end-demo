package wtf.demo.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wtf.demo.core.base.BaseServiceImpl;
import wtf.demo.core.util.DataUtil;
import wtf.demo.core.util.RedisUtil;
import wtf.demo.dao.TagDao;
import wtf.demo.dao.TagRelDao;
import wtf.demo.entity.bean.Tag;
import wtf.demo.entity.bean.TagRel;
import wtf.demo.service.TagService;

import java.util.ArrayList;
import java.util.List;

/**
 * 标签服务实现
 * @author gongjf
 * @since 2019年8月4日 上午9:37:49
 */
@Service
@Slf4j
public class TagServiceImpl extends BaseServiceImpl<Tag> implements TagService {

    @Value("${system.test-environment}")
    private boolean testEnvironment = false;

    @Autowired
    private TagDao tagDao;

    @Autowired
    private TagRelDao tagRelDao;

    @Autowired
    private RedisUtil redisUtil;

    // 缓存前缀（冒号分隔会在redis中进行分级）
    @Value("${cache.prefix}")
    private String cachePrefix;

    // 模块前缀（冒号分隔会在redis中进行分级）
    @Value("${cache.tag.prefix}")
    private String tagPrefix;

    // 根节点编码
    @Value("${cache.tag.root-code}")
    private String rootCode;

    // 批量操作计数
    private int operaTotal = 0;

    /*************** 工具方法 ***************/

    /**
     * 将数据放入缓存
     * @param parentCode
     * @param object
     */
    @Override
    public void setCache(String parentCode, Object object) {
        if(DataUtil.isEmpty(parentCode) || DataUtil.isEmpty(object)) return;
        try{
            String key = cachePrefix + tagPrefix + parentCode;
            redisUtil.set(key, object);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            e.printStackTrace();
        }
    }

    /**
     * 删除缓存中指定数据
     * @param parentCode
     */
    @Override
    public void deleteCache(String parentCode) {
        if(DataUtil.isEmpty(parentCode)) return;
        try{
            String key = cachePrefix + tagPrefix + parentCode;
            redisUtil.del(key);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            e.printStackTrace();
        }
    }

    /**
     * 从缓存中取出数据
     * @param parentCode
     */
    @Override
    public List<Tag> getCache(String parentCode) {
        if(DataUtil.isEmpty(parentCode)) return null;
        List<Tag> result = null;
        try{
            String key = cachePrefix + tagPrefix + parentCode;
            Object cache = redisUtil.get(key);
            if(DataUtil.isNotEmpty(cache) && cache instanceof List) {
                result = (List<Tag>) cache;
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            e.printStackTrace();
        }
        return result;
    }

    /*************** 缓存方法 ***************/

    /**
     * 查找父节点
     * @param code
     */
    @Override
    public Tag getParentNode(String code) {
        Tag entity = get(new Tag(code));
        return get(entity.getParentId());
    }

    /**
     * 查找子节点
     * @param parentCode
     */
    @Override
    public List<Tag> getChildrenNodes(String parentCode) {
        List<Tag> children = getCache(parentCode);
        if(DataUtil.isEmpty(children)) {
            children = tagDao.findByParentCode(parentCode);
            setCache(parentCode, children);
        }
        return children;
    }

    /**
     * 将节点放入缓存中
     * @param parentCode
     * @param entity
     * @param isRemoveOld
     */
    @Override
    public void setNode(String parentCode, Tag entity, boolean isRemoveOld) {
        if(DataUtil.isEmpty(parentCode) || DataUtil.isEmpty(entity)) return;

        List<Tag> children = getChildrenNodes(parentCode);
        if(DataUtil.isEmpty(children)) {
            children = new ArrayList<>();
        }
        else if(isRemoveOld) {
            for(int i=0, iLen=children.size(); i<iLen; i++) {
                Tag t = children.get(i);
                if(t.getId().equals(entity.getId())) {
                    children.remove(i);
                    break;
                }
            }
        }
        children.add(entity);
        setCache(parentCode, children);
    }

    /**
     * 移除节点，查找包含自己的缓存集合并移除自己
     * @param parentCode
     * @param code
     */
    @Override
    public void deleteNode(String parentCode, String code) {
        if(DataUtil.isEmpty(parentCode) || DataUtil.isEmpty(code)) return;

        List<Tag> children = getChildrenNodes(parentCode);
        if(DataUtil.isEmpty(children)) return;
        for(int i=0, iLen=children.size(); i<iLen; i++) {
            Tag t = children.get(i);
            if(t.getCode().equals(code)) {
                children.remove(i);
                break;
            }
        }
        // 删除元素后，列表可能为空
        if(DataUtil.isNotEmpty(children)) {
            setCache(parentCode, children);
        }
        else {
            deleteCache(parentCode);
        }
    }

    /**
     * 获取树缓存
     * @param code 根节点编码
     */
    @Override
    public Tag getTree(String code) {
        code = DataUtil.isNotEmpty(code) ? code : rootCode;
        Tag root = get(new Tag(code));
        loopGetTree(root);
        return root;
    }

    /**
     * 递归加载子节点
     * @param entity
     */
    private void loopGetTree(Tag entity) {
        if(DataUtil.isEmpty(entity)) return;
        List<Tag> children = getChildrenNodes(entity.getCode());
        if(DataUtil.isNotEmpty(children)) {
            for(Tag t : children) {
                loopGetTree(t);
            }
            entity.setChildren(children);
        }
    }

    /**
     * 加载树缓存（不加载当前节点自身）
     * @param code
     */
    @Override
    public void setTree(String code) {
        code = DataUtil.isNotEmpty(code) ? code : rootCode;
        Tag root = get(new Tag(code));
        loopSetTree(root);
    }

    /**
     * 递归加载子节点
     * @param entity
     */
    private void loopSetTree(Tag entity) {
        if(DataUtil.isEmpty(entity)) return;
        List<Tag> children = tagDao.findByParentId(entity.getId());
        if(DataUtil.isNotEmpty(children)) {
            for (Tag t : children) {
                loopSetTree(t);
            }
            setCache(entity.getCode(), children);
        }
        else {
            deleteCache(entity.getCode());
        }
    }

    /**
     * 删除树缓存（不删除当前节点）
     * @param code
     */
    @Override
    public void deleteTree(String code) {
        if(DataUtil.isEmpty(code)) return;
        loopDeleteTree(code);
    }

    /**
     * 递归删除子节点
     * @param parentCode
     */
    private void loopDeleteTree(String parentCode) {
        if(DataUtil.isEmpty(parentCode)) return;
        List<Tag> children = getChildrenNodes(parentCode);
        if(DataUtil.isNotEmpty(children)) {
            for (Tag t : children) {
                loopDeleteTree(t.getCode());
            }
            deleteCache(parentCode);
        }
    }

    /*************** 持久方法 ***************/

    /**
     * 保存节点数据并放入缓存
     * @param entity
     */
    @Override
    public Tag save(Tag entity) {
        entity = tagDao.save(entity, false);
        Tag parent = get(entity.getParentId());
        setNode(parent.getCode(), entity, true);
        return entity;
    }

    /**
     * 删除节点数据及其下所有子节点，并修改对应缓存
     * @param id
     */
    @Override
    public int delete(String id) {
        operaTotal = 0;
        Tag entity = get(id);
        if(DataUtil.isEmpty(entity)) return operaTotal;
        Tag parent = get(entity.getParentId());
        loopDelete(parent, entity);
        return operaTotal;
    }

    private void loopDelete(Tag parent, Tag entity) {
        if(DataUtil.isEmpty(parent) || DataUtil.isEmpty(entity)) return;
        List<Tag> children = getChildrenNodes(entity.getCode());
        if(DataUtil.isNotEmpty(children)) {
            for(Tag t : children) {
                loopDelete(entity, t);
            }
        }
        deleteNode(parent.getCode(), entity.getCode());
        tagRelDao.delete(new TagRel(entity.getId()));
        tagDao.delete(entity.getId());
    }

    /**
     * 移动
     * @param newParent
     * @param entity
     */
    @Transactional
    @Override
    public synchronized Tag move(Tag newParent, Tag entity) {
        if(DataUtil.isEmpty(newParent) || DataUtil.isEmpty(entity)) return null;

        Tag oldParent = get(entity.getParentId());
        if(DataUtil.isEmpty(oldParent)) {
            deleteNode(oldParent.getCode(), entity.getCode());
        }
        loopMove(newParent, entity);
        return entity;
    }

    /**
     * 递归移动子节点
     * @param parent
     * @param entity
     */
    private void loopMove(Tag parent, Tag entity) {
        if(DataUtil.isEmpty(parent) || DataUtil.isEmpty(entity)) return;
        String oldCode = entity.getCode();

        entity.setDelFlag(false);
        entity.setParentId(parent.getId());
        entity.setLevel(parent.getLevel() + 1);
        entity.setCode(parent.getCode() + "-" + entity.getId());
        entity = save(entity);
        setNode(parent.getCode(), entity, false);

        List<Tag> children = getChildrenNodes(oldCode);
        if(DataUtil.isNotEmpty(children)) {
            for(Tag t : children) {
                loopMove(entity, t);
            }
            deleteCache(oldCode);
            setCache(entity.getCode(), children);
        }
    }

}
