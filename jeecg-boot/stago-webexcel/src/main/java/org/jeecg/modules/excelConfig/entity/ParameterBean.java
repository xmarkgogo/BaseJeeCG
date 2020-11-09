package org.jeecg.modules.excelConfig.entity;

import org.jeecg.modules.excelConfig.util.ExchangeVariableTool;

import java.util.List;

/**
 * Created by qizhang on 2020/4/8.
 */
public class ParameterBean {

    private String name;
    private  Class cls;
    private String value;

    private String htmlType;//类型选择项:下拉框/文本输入框/单选框/日期选择
    private boolean isRequired;//是否是必填项
    private String desc;//描述
    private String modelId;//对应数据源的id: 从xml中的model中获取对应id的数据源

    private List    data_List;
    private boolean isShow;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Class getCls() {
        return cls;
    }

    public void setCls(Class cls) {
        this.cls = cls;
    }

    public String getValue() {
        if (this.value!=null ){
            if(this.value.contains("@@")){
                value = ExchangeVariableTool.getVariableValue(this.value);
            }
        }
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getHtmlType() {
        return htmlType;
    }

    public void setHtmlType(String htmlType) {
        this.htmlType = htmlType;
    }


    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public boolean isRequired() {
        return isRequired;
    }

    public void setRequired(boolean required) {

        isRequired = required;
    }

    public String getModelId() {
        return modelId;
    }

    public void setModelId(String modelId) {
        this.modelId = modelId;
    }



    public List getData_List() {
        return data_List;
    }

    public void setData_List(List data_List) {
        this.data_List = data_List;
    }

    public boolean isShow() {
        return isShow;
    }

    public void setShow(boolean show) {
        isShow = show;
    }
}
