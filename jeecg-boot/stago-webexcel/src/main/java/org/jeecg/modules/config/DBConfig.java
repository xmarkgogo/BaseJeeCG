package org.jeecg.modules.config;
import org.jeecg.common.util.SpringContextUtils;
import org.springframework.jdbc.core.JdbcTemplate;
public class DBConfig {

    public static JdbcTemplate getJdbcTemplate()
    {
        JdbcTemplate jdbcTemplate = SpringContextUtils.getBean(JdbcTemplate.class);
        return  jdbcTemplate;
    }
}
