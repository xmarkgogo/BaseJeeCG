package org.jeecg.modules.excelConfig.entity;


/**
 * Created by qizhang on 2020/4/8.
 */
public class ModelBean {

    private String id;
    private String cls;//设置反射类
    private ParametersBean parametersBean;
    private SqlStrsBean sqlStrsBean;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    public SqlStrsBean getSqlStrsBean() {
        return sqlStrsBean;
    }

    public void setSqlStrsBean(SqlStrsBean sqlStrsBean) {
        this.sqlStrsBean = sqlStrsBean;
    }

    public ParametersBean getParametersBean() {
        return parametersBean;
    }

    public void setParametersBean(ParametersBean parametersBean) {
        this.parametersBean = parametersBean;
    }

    public String getCls() {
        return cls;
    }

    public void setCls(String cls) {
        this.cls = cls;
    }
}
