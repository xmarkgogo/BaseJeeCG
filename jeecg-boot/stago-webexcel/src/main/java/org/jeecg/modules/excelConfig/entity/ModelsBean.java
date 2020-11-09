package org.jeecg.modules.excelConfig.entity;

import java.util.List;

/**
 * Created by qizhang on 2020/4/8.
 */
public class ModelsBean {
    private List<ModelBean> ModelBeans;
    private String cls;

    public  String getCls() {
        return cls;
    }
    public void   setCls(String cls) {
        this.cls = cls;
    }

    public List<ModelBean> getModelBeans() {
        return ModelBeans;
    }
    public void setModelBeans(List<ModelBean> modelBeans) {
        ModelBeans = modelBeans;
    }





}
