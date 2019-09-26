package wtf.demo.dao.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import wtf.demo.core.base.BaseDaoImpl;
import wtf.demo.core.pagination.Pagination;
import wtf.demo.core.pagination.Sortable;
import wtf.demo.core.util.DataUtil;
import wtf.demo.core.util.JdbcUtil;
import wtf.demo.dao.UserDao;
import wtf.demo.entity.bean.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * 用户持久层实现
 * @author gongjf
 * @since 2019年6月13日 16:21:29
 */
@Slf4j
@Repository
public class UserDaoImpl extends BaseDaoImpl<User> implements UserDao {

	@Value("${system.test-environment}")
	private boolean testEnvironment = false;

	@Override
	public PreparedStatement setCustomConditions(Connection conn, User entity, StringBuffer sql, Sortable sortArgs, Pagination pageArgs) throws SQLException {
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
			if(DataUtil.isNotEmpty(entity.getName())) {
				whereStr.append("and name like ? ");
			}
			if(DataUtil.isNotEmpty(entity.getPassword())) {
				whereStr.append("and password = ? ");
			}
			if(DataUtil.isNotEmpty(entity.getTelephone())) {
				whereStr.append("and telephone like ? ");
			}
			if(DataUtil.isNotEmpty(entity.getRoleCode())) {
				whereStr.append("and role_code like ? ");
			}
			if(DataUtil.isNotEmpty(entity.getKeyword())) {
				whereStr.append("and ( 0=1 ");
				whereStr.append("or name like ? ");
				whereStr.append("or telephone like ? ");
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
			if(DataUtil.isNotEmpty(entity.getName())) {
				JdbcUtil.setString(ps, count++, "%" + entity.getName() + "%");
			}
			if(DataUtil.isNotEmpty(entity.getPassword())) {
				JdbcUtil.setString(ps, count++, entity.getPassword());
			}
			if(DataUtil.isNotEmpty(entity.getTelephone())) {
				JdbcUtil.setString(ps, count++, "%" + entity.getTelephone() + "%");
			}
			if(DataUtil.isNotEmpty(entity.getRoleCode())) {
				JdbcUtil.setString(ps, count++, "%" + entity.getRoleCode() + "%");
			}
			if(DataUtil.isNotEmpty(entity.getKeyword())) {
				JdbcUtil.setString(ps, count++, "%" + entity.getKeyword() + "%");
				JdbcUtil.setString(ps, count++, "%" + entity.getKeyword() + "%");
			}
		}
		return ps;
	}

}
