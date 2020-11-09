package org.jeecg.modules.config;
import com.alibaba.druid.pool.DruidDataSource;
import org.jeecg.common.util.dynamic.db.DataSourceCachePool;
import org.jeecg.common.util.dynamic.db.DynamicDBUtil;
import org.springframework.jdbc.core.JdbcTemplate;

public class DBConfig {


    public static JdbcTemplate getJdbcTemplate()
    {
        DruidDataSource dataSource =DynamicDBUtil.getDbSourceByDbKey("master");
        return new JdbcTemplate(dataSource);
    }
}
