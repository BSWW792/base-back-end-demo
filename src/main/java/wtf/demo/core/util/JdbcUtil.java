package wtf.demo.core.util;

import lombok.extern.slf4j.Slf4j;
import org.postgresql.util.PGobject;
import org.springframework.beans.factory.annotation.Value;

import java.sql.*;

@Slf4j
public class JdbcUtil {

    @Value("${system.test-environment}")
    private boolean testEnvironment = false;

    public static void free(Connection conn, PreparedStatement[] sts, ResultSet rs) {
        try {
            conn.setAutoCommit(true);
            if (rs != null) {
                rs.close();
            }

            PreparedStatement[] var6 = sts;
            int var5 = sts.length;

            for (int var4 = 0; var4 < var5; ++var4) {
                PreparedStatement preparedStatement = var6[var4];
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
            }

            if (conn != null) {
                conn.close();
            }
        } catch (SQLException var7) {
            log.error(var7.getMessage(), var7);
        }

    }

    public static void free(Connection conn, PreparedStatement st, ResultSet rs) {
        try {
            conn.setAutoCommit(true);
            if (rs != null) {
                rs.close();
            }

            if (st != null) {
                st.close();
            }

            if (conn != null) {
                conn.close();
            }
        } catch (SQLException var4) {
            log.error(var4.getMessage(), var4);
        }

    }

    public static void free(Connection conn, Statement st, ResultSet rs) {
        try {
            if (rs != null) {
                rs.close();
            }

            if (st != null) {
                st.close();
            }

            if (conn != null) {
                conn.close();
            }
        } catch (SQLException var4) {
            log.error(var4.getMessage(), var4);
        }

    }

    public static void roleback(Connection conn) {
        try {
            conn.rollback();
        } catch (SQLException var2) {
            log.error(var2.getMessage(), var2);
        }

    }

    public static void setString(PreparedStatement ps, int index, String value) throws SQLException {
        if (ps != null) {
            if (value == null) {
                ps.setNull(index, 12);
            } else {
                ps.setString(index, value);
            }

        }
    }

    public static void setDouble(PreparedStatement ps, int index, Double value) throws SQLException {
        if (ps != null) {
            if (value == null) {
                ps.setNull(index, 8);
            } else {
                ps.setDouble(index, value);
            }

        }
    }

    public static void setInt(PreparedStatement ps, int index, Integer value) throws SQLException {
        if (ps != null) {
            if (value == null) {
                ps.setNull(index, 4);
            } else {
                ps.setInt(index, value);
            }

        }
    }

    public static void setBoolean(PreparedStatement ps, int index, Boolean value) throws SQLException {
        if (ps != null) {
            if (value == null) {
                ps.setNull(index, 16);
            } else {
                ps.setBoolean(index, value);
            }

        }
    }

    public static void setDate(PreparedStatement ps, int index, java.util.Date value) throws SQLException {
        if (ps != null) {
            if (value == null) {
                ps.setNull(index, 91);
            } else {
                java.sql.Date d = new java.sql.Date(value.getTime());
                ps.setDate(index, d);
            }

        }
    }

    public static void setTimestamp(PreparedStatement ps, int index, java.util.Date value) throws SQLException {
        if (ps != null) {
            if (value == null) {
                ps.setNull(index, 93);
            } else {
                Timestamp d = new Timestamp(value.getTime());
                ps.setTimestamp(index, d);
            }

        }
    }

    public static void setJson(PreparedStatement ps, int index, String value) throws SQLException {
        if (ps != null) {
            PGobject jsonObject = new PGobject();
            jsonObject.setType("json");
            if (value == null) {
                jsonObject.setValue((String) null);
            } else {
                jsonObject.setValue(value);
            }

            ps.setObject(index, jsonObject);
        }
    }

    public static void setUUID(PreparedStatement ps, int index, String value) throws SQLException {
        if (ps != null) {
            PGobject uuidObject = new PGobject();
            uuidObject.setType("uuid");
            uuidObject.setValue(value);
            ps.setObject(index, uuidObject);
        }
    }

    public static void setLong(PreparedStatement ps, int index, Long value) throws SQLException {
        if (ps != null) {
            if (value == null) {
                ps.setNull(index, -5);
            } else {
                ps.setLong(index, value);
            }

        }
    }
}
