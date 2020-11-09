package org.jeecg.modules.excelConfig.entity;

import java.util.List;

/**
 * Created by mas on 2020/8/4.
 */
public class WebExcelViewBean {
    private List<PlusBean> PlusBean;

    public List<PlusBean> getPlusBean() {
        return PlusBean;
    }

    public void setPlusBean(List<PlusBean> plusBean) {
        PlusBean = plusBean;
    }

    public String getJsSource() {
        return JsSource;
    }

    public void setJsSource(String jsSource) {
        JsSource = jsSource;
    }

    private String JsSource;

}
