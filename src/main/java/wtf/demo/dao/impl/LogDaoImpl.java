package wtf.demo.dao.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import wtf.demo.core.base.BaseDaoImpl;
import wtf.demo.core.pagination.Pagination;
import wtf.demo.core.pagination.Sortable;
import wtf.demo.core.util.DataUtil;
import wtf.demo.core.util.JdbcUtil;
import wtf.demo.dao.LogDao;
import wtf.demo.entity.bean.Log;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * 日志持久层实现
 * @author gongjf
 * @since 2019年4月4日 下午3:31:28
 */
@Slf4j
@Repository
public class LogDaoImpl extends BaseDaoImpl<Log> implements LogDao {

	@Value("${system.test-environment}")
	private boolean testEnvironment = false;

	@Override
	public PreparedStatement setCustomConditions(Connection conn, Log entity, StringBuffer sql, Sortable sortArgs, Pagination pageArgs) throws SQLException {
		// 追加条件
		StringBuffer whereStr = new StringBuffer(" WHERE 1=1 ");
		if(DataUtil.isNotEmpty(entity)) {
			if(DataUtil.isNotEmpty(entity.getId())) {
				whereStr.append("and id=? ");
			}
			if(DataUtil.isNotEmpty(entity.getCreateTime())) {
				whereStr.append("and create_time>=? ");
			}
			if(DataUtil.isNotEmpty(entity.getCreateTimeEnd())) {
				whereStr.append("and create_time<? ");
			}
			if(DataUtil.isNotEmpty(entity.getCreator())) {
				whereStr.append("and creator like ? ");
			}
			if(DataUtil.isNotEmpty(entity.getCreatorId())) {
				whereStr.append("and creator_id = ? ");
			}
			if(DataUtil.isNotEmpty(entity.getMethod())) {
				whereStr.append("and method like ? ");
			}
			if(DataUtil.isNotEmpty(entity.getIp())) {
				whereStr.append("and ip like ? ");
			}
			if(DataUtil.isNotEmpty(entity.getHost())) {
				whereStr.append("and host like ? ");
			}
			if(DataUtil.isNotEmpty(entity.getUrl())) {
				whereStr.append("and url like ? ");
			}
			if(DataUtil.isNotEmpty(entity.getLogType())) {
				whereStr.append("and log_type = ? ");
			} else if(DataUtil.isNotEmpty(entity.getLogTypeIds())) {
				String[] ids = entity.getLogTypeIds().split(",");
				whereStr.append("and log_type in (");
				for(int i=0, iLen=ids.length; i<iLen; i++) {
					whereStr.append("?");
					if(i<iLen-1) whereStr.append(",");
				}
				whereStr.append(")");
			}
			if(DataUtil.isNotEmpty(entity.getKeyword())) {
				whereStr.append("and ( 0=1 ");
				whereStr.append("or creator like ? ");
				whereStr.append("or method like ? ");
				whereStr.append("or ip like ? ");
				whereStr.append("or host like ? ");
				whereStr.append("or url like ? ");
				whereStr.append(")");
			}
		}
		// 追加排序和分页
		sql.append(setSortAndPagination(whereStr, sortArgs, pageArgs));

		// 测试环境打印语句
		if(testEnvironment) log.info(sql.toString());
		PreparedStatement ps = conn.prepareStatement(sql.toString());

		// 填入条件值
		if(DataUtil.isNotEmpty(entity)) {
			int count = 1;
			if(DataUtil.isNotEmpty(entity.getId())) {
				JdbcUtil.setString(ps, count++, entity.getId());
			}
			if(DataUtil.isNotEmpty(entity.getCreateTime())) {
				JdbcUtil.setTimestamp(ps, count++, entity.getCreateTime());
			}
			if(DataUtil.isNotEmpty(entity.getCreateTimeEnd())) {
				JdbcUtil.setTimestamp(ps, count++, entity.getCreateTimeEnd());
			}
			if(DataUtil.isNotEmpty(entity.getCreator())) {
				JdbcUtil.setString(ps, count++, "%" + entity.getCreator() + "%");
			}
			if(DataUtil.isNotEmpty(entity.getCreatorId())) {
				JdbcUtil.setString(ps, count++, entity.getCreatorId());
			}
			if(DataUtil.isNotEmpty(entity.getMethod())) {
				JdbcUtil.setString(ps, count++, "%" + entity.getMethod() + "%");
			}
			if(DataUtil.isNotEmpty(entity.getIp())) {
				JdbcUtil.setString(ps, count++, "%" + entity.getIp() + "%");
			}
			if(DataUtil.isNotEmpty(entity.getHost())) {
				JdbcUtil.setString(ps, count++, "%" + entity.getHost() + "%");
			}
			if(DataUtil.isNotEmpty(entity.getUrl())) {
				JdbcUtil.setString(ps, count++, "%" + entity.getUrl() + "%");
			}
			if(DataUtil.isNotEmpty(entity.getLogType())) {
				JdbcUtil.setInt(ps, count++, entity.getLogType());
			} else if(DataUtil.isNotEmpty(entity.getLogTypeIds())) {
				String[] ids = entity.getLogTypeIds().split(",");
				for(int i=0, iLen=ids.length; i<iLen; i++) {
					JdbcUtil.setInt(ps, count++, Integer.parseInt(ids[i]));
				}
			}
			if(DataUtil.isNotEmpty(entity.getKeyword())) {
				JdbcUtil.setString(ps, count++, "%" + entity.getKeyword() + "%");
				JdbcUtil.setString(ps, count++, "%" + entity.getKeyword() + "%");
				JdbcUtil.setString(ps, count++, "%" + entity.getKeyword() + "%");
				JdbcUtil.setString(ps, count++, "%" + entity.getKeyword() + "%");
				JdbcUtil.setString(ps, count++, "%" + entity.getKeyword() + "%");
			}
		}
		return ps;
	}

}
