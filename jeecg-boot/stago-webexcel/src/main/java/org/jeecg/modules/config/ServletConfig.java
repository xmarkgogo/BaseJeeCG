package org.jeecg.modules.config;

import org.jeecg.modules.elfinder.servlet.ConnectorServlet;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
/**
 *
 <servlet>
 <servlet-name>elfinder-connector-servlet</servlet-name>
 <servlet-class>org.grapheco.elfinder.servlet.ConnectorServlet
 </servlet-class>
 </servlet>
 <servlet-mapping>
 <servlet-name>elfinder-connector-servlet</servlet-name>
 <url-pattern>/elfinder-servlet/connector</url-pattern>
 </servlet-mapping>
 */
/**
 * 这个是一个初始化的入口类
 */
@Configuration
public class ServletConfig {
    @Bean
    public ServletRegistrationBean myServletRegistration() {
        ServletRegistrationBean registration = new ServletRegistrationBean(new ConnectorServlet());
        registration.addUrlMappings("/elfinder-servlet/connector");
        return registration;
    }
}
