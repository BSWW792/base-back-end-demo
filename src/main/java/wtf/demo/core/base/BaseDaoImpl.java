package wtf.demo.core.base;

import com.alibaba.druid.pool.DruidDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import wtf.demo.core.annotation.*;
import wtf.demo.core.pagination.Page;
import wtf.demo.core.pagination.Pagination;
import wtf.demo.core.pagination.Sortable;
import wtf.demo.core.util.DataUtil;
import wtf.demo.core.util.JacksonUtil;
import wtf.demo.core.util.JdbcUtil;
import wtf.demo.core.util.UUIDGenerator;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

/**
 * 基础持久层实现
 *
 * 未处理的问题：
 * 1.联合主键
 * 2.针对多层继承递归遍历字段，如：a继承b，b继承c，c继承d ……
 * 3.多层继承的每个父类都有主键，甚至组成联合主键
 *
 * @author gongjf
 * @since 2019年2月27日 下午3:31:28
 */
@Repository
@Slf4j
public abstract class BaseDaoImpl<T extends Base> implements BaseDao<T> {

	@Value("${system.test-environment}")
	private boolean testEnvironment = false;

	@Value("${format.grid}")
	private String grid = "4326";

	// 实体类名
	private Class<T> entityClass;
	// 表名
	private String tableName;

	// 列-字段关系映射集合
	private Map<String, Field> orm = new HashMap<String, Field>();
	// 列标注配置集合
	private Map<String, DBTableColumn> columns = new HashMap<String, DBTableColumn>();

	// ID字段的数据库名称
	private String idColumnName;
	// 删除标记字段的数据库名称（未必存在）
	private String delFlagColumnName;
	// 创建时间字段的数据库名称（未必存在）
	private String createTimeColumnName;
	// 更新时间字段的数据库名称（未必存在）
	private String updateTimeColumnName;

	@Resource
    private DruidDataSource dataSource;


	public BaseDaoImpl() {
		// 获取泛型类型
		entityClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
		// 获取表名
		if(entityClass.isAnnotationPresent(DBTable.class)) {
			DBTable table = entityClass.getAnnotation(DBTable.class);
			tableName = table.value();
		}
		// 获取父类实体字段名和类型
		Field[] fields = entityClass.getSuperclass().getDeclaredFields();
		for(Field field : fields){//遍历属性
			if(field.isAnnotationPresent(DBTableColumn.class)){
				DBTableColumn column = field.getAnnotation(DBTableColumn.class);
				orm.put(column.value(), field);
				columns.put(column.value(), column);
			}
		}
		// 获取实体字段名和类型
		fields = entityClass.getDeclaredFields();
		for(Field field : fields){//遍历属性
			if(field.isAnnotationPresent(DBTableColumn.class)){
				DBTableColumn column = field.getAnnotation(DBTableColumn.class);
				orm.put(column.value(), field);
				columns.put(column.value(), column);
				// 如果标识为ID
				if(field.isAnnotationPresent(DBId.class)) {
					idColumnName = column.value();
				}
				// 如果标识为删除标记
				if(field.isAnnotationPresent(DBDelFlag.class)) {
					delFlagColumnName = column.value();
				}
				// 如果标识为创建时间
				if(field.isAnnotationPresent(DBCreateTime.class)) {
					createTimeColumnName = column.value();
				}
				// 如果标识为更新时间
				if(field.isAnnotationPresent(DBUpdateTime.class)) {
					updateTimeColumnName = column.value();
				}
			}
		}
	}

	/**
	 * 获取所有列名的字符串，逗号分隔
	 * @return
	 */
	protected String getAllColumnStr() {
		StringBuffer sb = new StringBuffer();
		for(String columnName : orm.keySet()) {
			// 判断是否特殊类型
			DBTableColumn dbtc = columns.get(columnName);
			if(DataUtil.isNotEmpty(dbtc.type())) {
				// 如果是空间对象
				if("geometry".equalsIgnoreCase(dbtc.type())) {
					sb.append("ST_ASTEXT(");
					sb.append(columnName);
					sb.append(") as ");
					sb.append(columnName);
				}
			} else {
				sb.append(columnName);
			}
			sb.append(",");
		}
		return sb.substring(0, sb.length()-1);
	}

	/**
	 * 根据结果集返回对应类型的list集合
	 * @param rs
	 * @return
	 * @throws Exception
	 */
	protected List<T> getListByResultSet(ResultSet rs) throws Exception {
		List<T> list = new ArrayList<T>();
		while (rs.next()) {
			Map<String, Object> map = new HashMap<String, Object>();
			for (String column : orm.keySet()) {
				Field field = orm.get(column);
				map.put(field.getName(), rs.getObject(column));
			}
			T entity = JacksonUtil.mapToClass(map, entityClass);
			list.add(entity);
		}
		return list;
	}

	/**
	 * 根据结果集返回ID的list集合
	 * @param rs
	 * @return
	 * @throws Exception
	 */
	protected List<String> getIdListByResultSet(ResultSet rs) throws Exception {
		List<String> list = new ArrayList<>();
		while (rs.next()) {
			list.add(rs.getString(idColumnName));
		}
		return list;
	}

	/**
	 * 设置参数
	 * @param ps
	 * @param index
	 * @param fieldValue
	 * @param fieldClass
	 * @throws Exception
	 */
	protected void setParams(PreparedStatement ps, Integer index, Object fieldValue , Class<?> fieldClass) throws Exception {
		// 空值则判断字段类型
		if(DataUtil.isEmpty(fieldValue)) {
			if(fieldClass == Date.class) JdbcUtil.setTimestamp(ps, index, null);
			else if(fieldClass == String.class) JdbcUtil.setString(ps, index, null);
			else if(fieldClass == Integer.class) JdbcUtil.setInt(ps, index, null);
			else if(fieldClass == Boolean.class) JdbcUtil.setBoolean(ps, index, null);
			else if(fieldClass == Long.class) JdbcUtil.setLong(ps, index, null);
			else if(fieldClass == Double.class) JdbcUtil.setDouble(ps, index, null);
			else if(fieldClass == Map.class) JdbcUtil.setJson(ps, index, null);
			else JdbcUtil.setInt(ps, index, null);
		}
		else {
			if(fieldValue instanceof Date) JdbcUtil.setTimestamp(ps, index, (Date) fieldValue);
			else if(fieldValue instanceof String) JdbcUtil.setString(ps, index, (String) fieldValue);
			else if(fieldValue instanceof Integer) JdbcUtil.setInt(ps, index, (Integer) fieldValue);
			else if(fieldValue instanceof Boolean) JdbcUtil.setBoolean(ps, index, (Boolean) fieldValue);
			else if(fieldValue instanceof Long) JdbcUtil.setLong(ps, index, (Long) fieldValue);
			else if(fieldValue instanceof Double) JdbcUtil.setDouble(ps, index, (Double) fieldValue);
			else if(fieldValue instanceof Map) JdbcUtil.setJson(ps, index, JacksonUtil.objectToJson(fieldValue));
			else if(fieldValue instanceof BaseTypeEnum) JdbcUtil.setInt(ps, index, ((BaseTypeEnum) fieldValue).getValue());
		}
	}

	/**
	 * 设置自定义条件
	 * 只作简单组装：ID、删除标记、字符串、整型、布尔值、日期
	 * 复杂条件请重写该方法以实现自定义逻辑
	 * @param conn
	 * @param entity
	 * @param sql
	 * @param sortArgs
	 * @param pageArgs
	 * @return
	 * @throws Exception
	 */
	public PreparedStatement setCustomConditions(Connection conn, T entity, StringBuffer sql, Sortable sortArgs, Pagination pageArgs) throws Exception {
		StringBuffer whereStr = new StringBuffer(" WHERE 1=1 ");
		Set<String> keySet = orm.keySet();

		// 追加条件
		if(DataUtil.isNotEmpty(entity)) {
			for(String key : keySet) {
				Field field = orm.get(key);
				field.setAccessible(true);
				Object fieldValue = field.get(entity);

				// 如果定义了删除标记，填充默认值
				if(DataUtil.isNotEmpty(delFlagColumnName) && delFlagColumnName.equals(key)) {
					fieldValue = DataUtil.isEmpty(fieldValue) ? false : fieldValue;
					field.set(entity, fieldValue);
				}

				if(DataUtil.isNotEmpty(fieldValue)) {
					if(idColumnName.equals(key)
						|| key.contains("id")
						|| fieldValue instanceof Integer
						|| fieldValue instanceof Boolean
					) {
						whereStr.append("and " + key + "=? ");
					}
					else if(fieldValue instanceof String) {
						whereStr.append("and " + key + " like ? ");
					}
					else if(fieldValue instanceof Date) {
						if(key.contains("End")) {
							whereStr.append("and " + key + "<? ");
						}
						else {
							whereStr.append("and " + key + ">=? ");
						}
					}
				}
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
			for (String key : keySet) {
				Field field = orm.get(key);
				field.setAccessible(true);
				Object fieldValue = field.get(entity);

				if(DataUtil.isNotEmpty(fieldValue)) {
					if(idColumnName.equals(key) || key.contains("id")) {
						JdbcUtil.setString(ps, count++, (String) fieldValue);
					}
					else if(fieldValue instanceof Boolean) {
						JdbcUtil.setBoolean(ps, count++, (Boolean) fieldValue);
					}
					else if(fieldValue instanceof Integer) {
						JdbcUtil.setInt(ps, count++, (Integer) fieldValue);
					}
					else if(fieldValue instanceof String) {
						JdbcUtil.setString(ps, count++, "%" + fieldValue + "%");
					}
					else if(fieldValue instanceof Date) {
						JdbcUtil.setTimestamp(ps, count++, (Date) fieldValue);
					}
				}
			}
		}

		return ps;
	}

	/**
	 * 追加排序和分页参数
	 * @param sql
	 * @param sortArgs
	 * @param pageArgs
	 * @return
	 */
	public String setSortAndPagination(StringBuffer sql, Sortable sortArgs, Pagination pageArgs) {
		if(DataUtil.isNotEmpty(sortArgs)) {
			sql.append(" order by ").append(sortArgs.getSortField()).append(" ").append(sortArgs.getSortDirect());
		}
		if(DataUtil.isNotEmpty(pageArgs)) {
			sql.append(" limit ").append(pageArgs.getPageSize()).append(" offset ").append(pageArgs.getFirstOffset());
		}
		return sql.toString();
	}

	/**
	 * 新增
	 * @param entity
	 * @return
	 */
	@Override
	public T add(T entity) {
		if (entity == null) return null;

		Connection conn = null;
        PreparedStatement ps = null;
		ResultSet rs = null;
		StringBuffer sql;
        try {
            conn = dataSource.getConnection();

			// 填充ID
			Field idField = orm.get(idColumnName);
			idField.setAccessible(true);
			idField.set(entity, UUIDGenerator.getUUID());
			// 填充删除标记
			if(DataUtil.isNotEmpty(delFlagColumnName)) {
				Field delFlagField = orm.get(delFlagColumnName);
				if(DataUtil.isNotEmpty(delFlagField)) {
					delFlagField.setAccessible(true);
					delFlagField.set(entity, delFlagField.get(entity) != null ? delFlagField.get(entity) : false);
				}
			}
			// 填充创建时间
			if(DataUtil.isNotEmpty(createTimeColumnName)) {
				Field createTimeField = orm.get(createTimeColumnName);
				if(DataUtil.isNotEmpty(createTimeField)) {
					createTimeField.setAccessible(true);
					createTimeField.set(entity, new Date());
				}
			}

            // 拼装sql
            StringBuffer columnsStr = new StringBuffer(); // 字段串
            StringBuffer qmsStr = new StringBuffer(); // 问号串
			for(String columnName : orm.keySet()) {
//				// ID除外
//				if(idColumnName.equals(columnName)) continue;
				Field field = orm.get(columnName);
				field.setAccessible(true);
				Object fieldValue = field.get(entity);
				// 空值过滤
				if(DataUtil.isEmpty(fieldValue)) continue;

				columnsStr.append(columnName);
				columnsStr.append(",");

				// 判断是否特殊类型
				DBTableColumn dbtc = columns.get(columnName);
				if(DataUtil.isNotEmpty(dbtc.type())) {
					// 如果是空间对象
					if("geometry".equalsIgnoreCase(dbtc.type())) {
						qmsStr.append("ST_GeomFromText(?,");
						qmsStr.append(grid);
						qmsStr.append(")");
					}
				} else {
					qmsStr.append("?");
				}
				qmsStr.append(",");
			}
			sql = new StringBuffer("INSERT INTO ");
			sql.append(tableName);
			sql.append(" (");
			sql.append(columnsStr.substring(0, columnsStr.length()-1));
			sql.append(") VALUES (");
			sql.append(qmsStr.substring(0, qmsStr.length()-1));
			sql.append(") RETURNING ");
			sql.append(idColumnName);

			// 测试环境打印语句
			if(testEnvironment) log.info(sql.toString());
			ps = conn.prepareStatement(sql.toString());

			// 填充参数
			int count = 1;
			for(String columnName : orm.keySet()) {
//				// ID除外
//				if(idColumnName.equals(columnName)) continue;
				Field field = orm.get(columnName);
				field.setAccessible(true);
				Object fieldValue = field.get(entity);
				// 空值过滤
				if(DataUtil.isEmpty(fieldValue)) continue;
				setParams(ps, count++, fieldValue, field.getType());
			}

			rs = ps.executeQuery();
			// 返回完成新增的对象的ID
			if(rs.next()) {
				return get(rs.getString(1));
			}
        } catch (Exception e) {
            log.error(e.getMessage(), e);
			if(testEnvironment) e.printStackTrace();
        } finally {
            JdbcUtil.free(conn, ps, rs);
        }
        return null;
	}

	/**
	 * 批量新增
	 * 不过滤空值
	 * @param entityList
	 * @return
	 */
	@Override
	public int addBatch(List<T> entityList) {
		if (DataUtil.isEmpty(entityList)) return 0;
		if(entityList.size() == 1) {
			add(entityList.get(0));
			return 1;
		}

		Connection conn = null;
		PreparedStatement ps = null;
		StringBuffer sql;
		try {
			conn = dataSource.getConnection();

			// 填充默认参数
			for(T entity : entityList) {
				// 填充ID
				Field idField = orm.get(idColumnName);
				idField.setAccessible(true);
				idField.set(entity, UUIDGenerator.getUUID());
				// 填充删除标记
				if(DataUtil.isNotEmpty(delFlagColumnName)) {
					Field delFlagField = orm.get(delFlagColumnName);
					if(DataUtil.isNotEmpty(delFlagField)) {
						delFlagField.setAccessible(true);
						delFlagField.set(entity, delFlagField.get(entity) != null ? delFlagField.get(entity) : false);
					}
				}
				// 填充创建时间
				if(DataUtil.isNotEmpty(createTimeColumnName)) {
					Field createTimeField = orm.get(createTimeColumnName);
					if(DataUtil.isNotEmpty(createTimeField)) {
						createTimeField.setAccessible(true);
						createTimeField.set(entity, new Date());
					}
				}
			}

			// 拼装sql
			StringBuffer columnsStr = new StringBuffer(); // 字段串
			StringBuffer qmsStr = new StringBuffer(); // 问号串
			for(String columnName : orm.keySet()) {
//				// ID除外
//				if(idColumnName.equals(columnName)) continue;
				Field field = orm.get(columnName);
				field.setAccessible(true);

				columnsStr.append(columnName);
				columnsStr.append(",");

				// 判断是否特殊类型
				DBTableColumn dbtc = columns.get(columnName);
				if(DataUtil.isNotEmpty(dbtc.type())) {
					// 如果是空间对象
					if("geometry".equalsIgnoreCase(dbtc.type())) {
						qmsStr.append("ST_GeomFromText(?,");
						qmsStr.append(grid);
						qmsStr.append(")");
					}
				} else {
					qmsStr.append("?");
				}
				qmsStr.append(",");
			}
			sql = new StringBuffer("INSERT INTO ");
			sql.append(tableName);
			sql.append(" (");
			sql.append(columnsStr.substring(0, columnsStr.length()-1));
			sql.append(") VALUES (");
			sql.append(qmsStr.substring(0, qmsStr.length()-1));
			sql.append(") RETURNING ");
			sql.append(idColumnName);

			// 测试环境打印语句
			if(testEnvironment) log.info(sql.toString());
			ps = conn.prepareStatement(sql.toString());

			// 批量填充参数
			for(T entity : entityList) {
				int count = 1;
				for(String columnName : orm.keySet()) {
//					// ID除外
//					if(idColumnName.equals(columnName)) continue;
					Field field = orm.get(columnName);
					field.setAccessible(true);
					Object fieldValue = field.get(entity);
					setParams(ps, count++, fieldValue, field.getType());
				}
				ps.addBatch();
			}

			// 统计结果
			int[] result = ps.executeBatch();
			int total = 0;
			for(int i : result) {
				total += i;
			}

			ps.clearBatch();
			return total;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			if(testEnvironment) e.printStackTrace();
		} finally {
			JdbcUtil.free(conn, ps, null);
		}
		return 0;
	}

	/**
	 * 更新
	 * @param entity
	 * @param isAllowNull
	 * @return
	 */
	@Override
	public synchronized T update(T entity, boolean isAllowNull) {
		if (entity == null) return null;

		Connection conn = null;
		PreparedStatement ps = null;
		StringBuffer sql;
		try {
			conn = dataSource.getConnection();

			// 填充更新时间
			if(DataUtil.isNotEmpty(updateTimeColumnName)) {
				Field updateTimeField = orm.get(updateTimeColumnName);
				if(DataUtil.isNotEmpty(updateTimeField)) {
					updateTimeField.setAccessible(true);
					updateTimeField.set(entity, new Date());
				}
			}

			// 拼装sql
			StringBuffer columnsStr = new StringBuffer();
			for(String columnName : orm.keySet()) {
				// ID除外
				if(idColumnName.equals(columnName)) continue;
				Field field = orm.get(columnName);
				field.setAccessible(true);
				Object fieldValue = field.get(entity);
				// 不允许空值则跳过
				if(DataUtil.isEmpty(fieldValue) && !isAllowNull) continue;

				columnsStr.append(columnName);
				columnsStr.append("=");
				// 判断是否特殊类型
				DBTableColumn dbtc = columns.get(columnName);
				if(DataUtil.isNotEmpty(dbtc.type())) {
					// 如果是空间对象
					if("geometry".equalsIgnoreCase(dbtc.type())) {
						columnsStr.append("ST_GeomFromText(?,");
						columnsStr.append(grid);
						columnsStr.append(")");
					}
				} else {
					columnsStr.append("?");
				}
				columnsStr.append(",");
			}
			sql = new StringBuffer("UPDATE ");
			sql.append(tableName);
			sql.append(" SET ");
			sql.append(columnsStr.substring(0, columnsStr.length()-1));
			sql.append(" WHERE ");
			sql.append(idColumnName);
			sql.append("=?");

			// 测试环境打印语句
			if(testEnvironment) log.info(sql.toString());
			ps = conn.prepareStatement(sql.toString());

			// 填充参数
			int count = 1;
			for(String columnName : orm.keySet()) {
				// ID除外
				if(idColumnName.equals(columnName)) continue;
				Field field = orm.get(columnName);
				field.setAccessible(true);
				Object fieldValue = field.get(entity);
				// 不允许空值则跳过
				if(DataUtil.isEmpty(fieldValue) && !isAllowNull) continue;

				setParams(ps, count++, fieldValue, field.getType());
			}
			// 填充ID
			Field idField = orm.get(idColumnName);
			idField.setAccessible(true);
			String idValue = (String) idField.get(entity);
			JdbcUtil.setString(ps, count, idValue);

			ps.executeUpdate();
			return get(idValue);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			if(testEnvironment) e.printStackTrace();
		} finally {
			JdbcUtil.free(conn, ps, null);
		}
		return null;
	}

	/**
	 * 批量更新
	 * 不过滤空值
	 * @param entityList
	 * @return
	 */
	@Override
	public int updateBatch(List<T> entityList) {
		if (DataUtil.isEmpty(entityList)) return 0;
		if(entityList.size() == 1) {
			update(entityList.get(0), true);
			return 1;
		}

		Connection conn = null;
		PreparedStatement ps = null;
		StringBuffer sql;
		try {
			conn = dataSource.getConnection();

			// 填充默认参数
			for(T entity : entityList) {
				// 填充更新时间
				if(DataUtil.isNotEmpty(updateTimeColumnName)) {
					Field updateTimeField = orm.get(updateTimeColumnName);
					if(DataUtil.isNotEmpty(updateTimeField)) {
						updateTimeField.setAccessible(true);
						updateTimeField.set(entity, new Date());
					}
				}
			}

			// 拼装sql
			StringBuffer columnsStr = new StringBuffer();
			for(String columnName : orm.keySet()) {
				// ID除外
				if(idColumnName.equals(columnName)) continue;
				Field field = orm.get(columnName);
				field.setAccessible(true);

				columnsStr.append(columnName);
				columnsStr.append("=");
				// 判断是否特殊类型
				DBTableColumn dbtc = columns.get(columnName);
				if(DataUtil.isNotEmpty(dbtc.type())) {
					// 如果是空间对象
					if("geometry".equalsIgnoreCase(dbtc.type())) {
						columnsStr.append("ST_GeomFromText(?,");
						columnsStr.append(grid);
						columnsStr.append(")");
					}
				} else {
					columnsStr.append("?");
				}
				columnsStr.append(",");
			}
			sql = new StringBuffer("UPDATE ");
			sql.append(tableName);
			sql.append(" SET ");
			sql.append(columnsStr.substring(0, columnsStr.length()-1));
			sql.append(" WHERE ");
			sql.append(idColumnName);
			sql.append("=?");

			// 测试环境打印语句
			if(testEnvironment) log.info(sql.toString());
			ps = conn.prepareStatement(sql.toString());

			// 批量填充参数
			for(T entity : entityList) {
				int count = 1;
				for(String columnName : orm.keySet()) {
					// ID除外
					if(idColumnName.equals(columnName)) continue;
					Field field = orm.get(columnName);
					field.setAccessible(true);
					Object fieldValue = field.get(entity);
					setParams(ps, count++, fieldValue, field.getType());
				}
				// 填充ID
				Field idField = orm.get(idColumnName);
				idField.setAccessible(true);
				String idValue = (String) idField.get(entity);
				JdbcUtil.setString(ps, count, idValue);
				ps.addBatch();
			}

			// 统计结果
			int[] result = ps.executeBatch();
			int total = 0;
			for(int i : result) {
				total += i;
			}

			ps.clearBatch();
			return total;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			if(testEnvironment) e.printStackTrace();
		} finally {
			JdbcUtil.free(conn, ps, null);
		}
		return 0;
	}

	/**
	 * 保存
	 * @param entity
	 * @param isAllowNull
	 * @return
	 */
	@Override
	public T save(T entity, boolean isAllowNull) {
		if(DataUtil.isEmpty(entity)) return null;

		try {
			// 获取ID
			Field idField = orm.get(idColumnName);
			idField.setAccessible(true);
			String idValue = (String) idField.get(entity);
			// 无ID就新增，有ID就更新
			if(DataUtil.isEmpty(idValue)) return add(entity);
			else return update(entity, isAllowNull);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			if(testEnvironment) e.printStackTrace();
		}
		return null;
	}

	/**
	 * 根据实体条件进行复数删除
	 * @param entity
	 * @return
	 */
	@Override
	public int delete(T entity) {
		if (entity == null) return 0;

		Connection conn = null;
		PreparedStatement ps = null;
		StringBuffer sql;
		try {
			conn = dataSource.getConnection();

			// 拼装sql
			sql = new StringBuffer("DELETE FROM ");
			sql.append(tableName);
			ps = setCustomConditions(conn, entity, sql, null, null);

			// 删除前查询影响数目
			int count = count(entity);

			ps.execute();
			return count;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			if(testEnvironment) e.printStackTrace();
		} finally {
			JdbcUtil.free(conn, ps, null);
		}
		return 0;
	}

	/**
	 * 删除
	 * @param id
	 * @return
	 */
	@Override
	public int delete(String id) {
		if (id == null) return 0;

		Connection conn = null;
		PreparedStatement ps = null;
		StringBuffer sql;
		try {
			conn = dataSource.getConnection();

			// 拼装sql
			sql = new StringBuffer("DELETE FROM ");
			sql.append(tableName);
			sql.append(" WHERE ");
			sql.append(idColumnName);
			sql.append("=?");

			// 测试环境打印语句
			if(testEnvironment) log.info(sql.toString());
			ps = conn.prepareStatement(sql.toString());

			// 填充参数值
			JdbcUtil.setString(ps, 1, id);

			ps.executeUpdate();
			return 1;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			if(testEnvironment) e.printStackTrace();
		} finally {
			JdbcUtil.free(conn, ps, null);
		}
		return 0;
	}

	/**
	 * 根据ID列表进行复数删除
	 * @param idList
	 * @return
	 */
	@Override
	public int deleteBatchId(List<String> idList) {
		if (DataUtil.isEmpty(idList)) return 0;

		Connection conn = null;
		PreparedStatement ps = null;
		StringBuffer sql;
		try {
			conn = dataSource.getConnection();

			// 拼装sql
			sql = new StringBuffer("DELETE FROM ");
			sql.append(tableName);
			sql.append(" WHERE ");
			sql.append(idColumnName);
			sql.append("=?");

			// 测试环境打印语句
			if(testEnvironment) log.info(sql.toString());
			ps = conn.prepareStatement(sql.toString());

			// 填充参数值
			for(String id : idList) {
				JdbcUtil.setString(ps, 1, id);
				ps.addBatch();
			}

			ps.executeBatch();
			ps.clearBatch();
			return idList.size();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			if(testEnvironment) e.printStackTrace();
		} finally {
			JdbcUtil.free(conn, ps, null);
		}
		return 0;
	}

	/**
	 * 根据实体条件进行计数
	 * @param entity
	 * @return
	 */
	@Override

	public int count(T entity) {
		if (entity == null) return 0;

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		StringBuffer sql;
		try {
			conn = dataSource.getConnection();

			// 拼装sql
			sql = new StringBuffer("SELECT ");
			sql.append("count(1) ");
			sql.append("FROM ");
			sql.append(tableName);
			ps = setCustomConditions(conn, entity, sql, null, null);

			rs = ps.executeQuery();
			rs.next();
			return rs.getInt(1);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			if(testEnvironment) e.printStackTrace();
		} finally {
			JdbcUtil.free(conn, ps, rs);
		}
		return 0;
	}

	/**
	 * 根据查询sql进行计数
	 * @param querySql
	 * @return
	 */
	@Override
	public int countByQuerySql(StringBuffer querySql) {
		if(DataUtil.isEmpty(querySql) || querySql.indexOf("select ") <0) return 0;

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		StringBuffer sql;
		try {
			conn = dataSource.getConnection();

			// 拼装sql
			sql = new StringBuffer("SELECT ");
			sql.append("count(1) ");
			sql.append("FROM ");
			sql.append("(");
			sql.append(querySql);
			sql.append(") ");
			sql.append("countByQuerySql ");

			// 测试环境打印语句
			if(testEnvironment) log.info(sql.toString());
			ps = conn.prepareStatement(sql.toString());

			rs = ps.executeQuery();
			rs.next();
			return rs.getInt(1);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			if(testEnvironment) e.printStackTrace();
		} finally {
			JdbcUtil.free(conn, ps, rs);
		}
		return 0;
	}

	/**
	 * 根据ID获取单条记录
	 * @param id
	 * @return
	 */
	@Override
	public T get(String id) {
		if (id == null) return null;

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		StringBuffer sql;
		try {
			conn = dataSource.getConnection();

			// 拼装sql
			sql = new StringBuffer("SELECT ");
			sql.append(getAllColumnStr());
			sql.append(" FROM ");
			sql.append(tableName);
			sql.append(" WHERE ");
			sql.append(idColumnName);
			sql.append("=?");

			// 测试环境打印语句
			if(testEnvironment) log.info(sql.toString());
			ps = conn.prepareStatement(sql.toString());

			// 填充参数值
			JdbcUtil.setString(ps, 1, id);

			rs = ps.executeQuery();
			List<T> list = getListByResultSet(rs);
			if(DataUtil.isNotEmpty(list)) return list.get(0);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			if(testEnvironment) e.printStackTrace();
		} finally {
			JdbcUtil.free(conn, ps, rs);
		}
		return null;
	}

	/**
	 * 根据多个实体条件获取单条记录
	 * 因返回结果列表的第一条，该方法获取的数据有可能不正确
	 * @param entity
	 * @return
	 */
	@Override
	public T get(T entity) {
		if (entity == null) return null;

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		StringBuffer sql;
		try {
			conn = dataSource.getConnection();

			// 拼装sql
			sql = new StringBuffer("SELECT ");
			sql.append(getAllColumnStr());
			sql.append(" FROM ");
			sql.append(tableName);
			ps = setCustomConditions(conn, entity, sql, new Sortable(), new Pagination(1, 1));

			rs = ps.executeQuery();
			List<T> list = getListByResultSet(rs);
			if(DataUtil.isNotEmpty(list)) return list.get(0);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			if(testEnvironment) e.printStackTrace();
		} finally {
			JdbcUtil.free(conn, ps, rs);
		}
		return null;
	}

	/**
	 * 根据实体条件和排序参数查找单条记录
	 * @param entity
	 * @param sortable
	 * @return
	 */
	@Override
	public T getWithSort(T entity, Sortable sortable) {
		Page<T> page = page(entity, sortable, new Pagination(1, 1));
		if(DataUtil.isNotEmpty(page)) {
			List<T> list = page.getResult();
			if(DataUtil.isNotEmpty(list)) {
				return list.get(0);
			}
		}
		return null;
	}

	/**
	 * 根据实体条件查询列表
	 * @param entity
	 * @param sortable
	 * @return
	 */
	@Override
	public List<T> list(T entity, Sortable sortable) {
		if (entity == null) return null;

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		StringBuffer sql;
		try {
			conn = dataSource.getConnection();

			// 拼装sql
			sql = new StringBuffer("SELECT ");
			sql.append(getAllColumnStr());
			sql.append(" FROM ");
			sql.append(tableName);
			ps = setCustomConditions(conn, entity, sql, sortable, null);

			rs = ps.executeQuery();
			List<T> list = getListByResultSet(rs);
			return list;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			if(testEnvironment) e.printStackTrace();
		} finally {
			JdbcUtil.free(conn, ps, rs);
		}
		return null;
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
		if (entity == null) return null;

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		StringBuffer sql;
		try {
			conn = dataSource.getConnection();

			// 拼装sql
			sql = new StringBuffer("SELECT ");
			sql.append(getAllColumnStr());
			sql.append(" FROM ");
			sql.append(tableName);
			ps = setCustomConditions(conn, entity, sql, sortable, pagination);

			rs = ps.executeQuery();
			List<T> list = getListByResultSet(rs);
			int count = count(entity);

			// 组装分页
			Page<T> page = new Page<>();
			page.setResult(list);
			page.setPageSize(pagination.getPageSize());
			page.setStart(pagination.getFirstOffset());
			page.setTotalCount(count);
			return page;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			if(testEnvironment) e.printStackTrace();
		} finally {
			JdbcUtil.free(conn, ps, rs);
		}
		return null;
	}

	/**
	 * 根据实体条件查询ID列表
	 * @param entity
	 * @param sortable
	 * @return
	 */
	@Override
	public List<String> idList(T entity, Sortable sortable) {
		if (entity == null) return null;

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		StringBuffer sql;
		try {
			conn = dataSource.getConnection();

			// 拼装sql
			sql = new StringBuffer("SELECT ");
			sql.append(idColumnName);
			sql.append(" FROM ");
			sql.append(tableName);
			ps = setCustomConditions(conn, entity, sql, sortable, null);

			rs = ps.executeQuery();
			List<String> list = getIdListByResultSet(rs);
			return list;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			if(testEnvironment) e.printStackTrace();
		} finally {
			JdbcUtil.free(conn, ps, rs);
		}
		return null;
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
		if (entity == null) return null;

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		StringBuffer sql;
		try {
			conn = dataSource.getConnection();

			// 拼装sql
			sql = new StringBuffer("SELECT ");
			sql.append(idColumnName);
			sql.append(" FROM ");
			sql.append(tableName);
			ps = setCustomConditions(conn, entity, sql, sortable, pagination);

			rs = ps.executeQuery();
			List<String> list = getIdListByResultSet(rs);
			int count = count(entity);

			// 组装分页
			Page<String> page = new Page<>();
			page.setResult(list);
			page.setPageSize(pagination.getPageSize());
			page.setStart(pagination.getFirstOffset());
			page.setTotalCount(count);
			return page;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			if(testEnvironment) e.printStackTrace();
		} finally {
			JdbcUtil.free(conn, ps, rs);
		}
		return null;
	}


	public DruidDataSource getDataSource() {
		return dataSource;
	}

	public String getGrid() {
		return grid;
	}

	public Class<T> getEntityClass() {
		return entityClass;
	}

	public String getTableName() {
		return tableName;
	}

	public Map<String, Field> getOrm() {
		return orm;
	}

	public Map<String, DBTableColumn> getColumns() {
		return columns;
	}

	public String getIdColumnName() {
		return idColumnName;
	}

	public String getDelFlagColumnName() {
		return delFlagColumnName;
	}

	public String getCreateTimeColumnName() {
		return createTimeColumnName;
	}

	public String getUpdateTimeColumnName() {
		return updateTimeColumnName;
	}
}
