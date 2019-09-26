package wtf.demo.dao.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import wtf.demo.core.base.BaseDaoImpl;
import wtf.demo.core.pagination.Pagination;
import wtf.demo.core.pagination.Sortable;
import wtf.demo.core.util.DataUtil;
import wtf.demo.core.util.JdbcUtil;
import wtf.demo.core.util.StringUtils;
import wtf.demo.dao.TagRelDao;
import wtf.demo.entity.bean.Tag;
import wtf.demo.entity.bean.TagRel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

/**
 * 标签关联持久层实现
 * @author gongjf
 * @since 2019年6月13日 16:21:29
 */
@Slf4j
@Repository
public class TagRelDaoImpl extends BaseDaoImpl<TagRel> implements TagRelDao {

    @Value("${system.test-environment}")
    private boolean testEnvironment = false;

}
