package org.jeecg.modules.excelConfig.entity;

import java.util.List;

/**
 * Created by qizhang on 2020/3/30.
 */
public class ViewsBean {

    private String action;
    public void setViewBeans(List<ViewBean> viewBeans) {
        ViewBeans = viewBeans;
    }
    public List<ViewBean> getViewBeans() {
        return ViewBeans;
    }



    private List<ViewBean> ViewBeans;

    public String getAction() {
        return action;
    }
    public void setAction(String action) {
        this.action = action;
    }


}
