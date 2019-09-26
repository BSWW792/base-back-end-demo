package wtf.demo.core.util;

import org.springframework.beans.factory.annotation.Value;
import wtf.demo.entity.bean.User;

import javax.servlet.http.HttpSession;

/**
 * session工具
 * @author gongjf
 * @since 2019年5月1日 下午3:30:28
 */
public class SessionUtil {

    @Value("${system.test-environment}")
    private boolean testEnvironment = false;

    private static final String USER_SESSION_KEY = "user_info";

    public static final SessionUtil I = new SessionUtil();

    public User getCurrentUser(HttpSession session) {
        if (session == null) {
            return null;
        }
        return (User) session.getAttribute(USER_SESSION_KEY);
    }

    public void setCurrentUser(HttpSession session, Object o) {
        if (session == null) {
            return;
        }
        session.setAttribute(USER_SESSION_KEY, o);
    }

}
