package wtf.demo.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import wtf.demo.core.base.BaseServiceImpl;
import wtf.demo.core.util.FileUtil;
import wtf.demo.core.util.JacksonUtil;
import wtf.demo.dao.LogDao;
import wtf.demo.entity.bean.Log;
import wtf.demo.service.LogService;

/**
 * 日志服务实现
 * @author gongjf
 * @since 2019年4月4日 上午9:37:49
 */
@Service
@Slf4j
public class LogServiceImpl extends BaseServiceImpl<Log> implements LogService {

	@Value("${system.test-environment}")
	private boolean testEnvironment = false;

	@Autowired
	private LogDao logDao;

	@Value("${file.data.path}")
	private String dataFilePath;

	@Value("${file.release}")
	private String releaseFileName = "release.json";


	@Override
	public Object getReleaseLog() {
		String json = FileUtil.readFileString(dataFilePath + releaseFileName);
		return JacksonUtil.jsonToList(json);
	}

}
