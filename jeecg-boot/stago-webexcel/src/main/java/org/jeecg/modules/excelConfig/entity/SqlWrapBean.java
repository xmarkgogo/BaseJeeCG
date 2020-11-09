package org.jeecg.modules.excelConfig.entity;


import freemarker.template.Configuration;
import freemarker.template.Template;
import org.apache.commons.lang.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SqlWrapBean {

    /*
     SQL包装对象，如果对于读取的数据有特殊偏好，可以在此处重新处理
     增加自己的业务逻辑
     */
    private StringBuilder sqlTemplate;
    private ParametersBean _parametersBean;
    private Logger logger = LoggerFactory.getLogger(SqlWrapBean.class);

    public StringBuilder getSqlTemplate() {
        return sqlTemplate;
    }

    public void setSqlTemplate(StringBuilder sqlTemplate) {
        this.sqlTemplate = sqlTemplate;
    }

    public ParametersBean getParameTersBean() {
        return _parametersBean;
    }

    public void setParameTersBean(ParametersBean parameTersBean) {
        this._parametersBean = parameTersBean;
    }

    public String getFinalSQL() {
        String sql = this.sqlTemplate.toString();
        if (this.getParameTersBean() == null) {
            return sql;
        }
        Map m = new HashMap<>();
        List<ParameterBean> parameterBeans = this.getParameTersBean().getParameterBeans();
        for (int k = 0; k < parameterBeans.size(); k++) {
            ParameterBean parameterBean = parameterBeans.get(k);
            String name = parameterBean.getName();
            String value = parameterBean.getValue();
            //避免拼接SQL 导致注入BUG的替换操作
            value = StringEscapeUtils.escapeSql(value);


            m.put(name, value);
            //TODO
            /**
             此处应该判断参数的类型，根据类型构造SQL
             1、如果是数字类型，则不应该加''
             2、如果为字符串类型，应该加''
             不应该配置到到XML中，
             具体可用参考 ibaits的XML代码

             ~~~~~~~~~~~~~~~~待实现~~~~~~~~~~~~~~~
             */
            if ("null".equalsIgnoreCase(value)) {
                sql = sql.replace("$P!{" + name + "}", "");
            } else {
                sql = sql.replace("$P!{" + name + "}", value);
            }

        }
        sql = this.FreeMarkersrenderString(sql, m);
        //通过正则表，批量去除没有得到变量的数值，不能在SQL中保留$P!{var}
        while (sql.indexOf("$P!{") > 0) {
            int start = sql.indexOf("$P!{");
            int end = sql.indexOf("}") + 1;
            sql = sql.replace(sql.substring(start, end), "");
        }
        return sql;
    }

    /**
     * @param templateString
     * @param model
     * @return
     */
    public String FreeMarkersrenderString(String templateString, Map<String, ?> model) {
        try {
            StringWriter result = new StringWriter();
            Template t = new Template("Template_name", new StringReader(templateString), new Configuration());
            t.process(model, result);
            return result.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return  null;
    }
}
