package org.jeecg.modules.excelConfig.entity;

/**
 * Created by qizhang on 2020/3/27.
 */
public class PropertyBean {

    /**
     * 单元格位置
     * -1:虚拟cell,需要根据getValueFunction来获取值
     * sheetName.A2:具体cell位置
     * sheetName.A :列固定,行号为父节点中的beginRowIndex
     */
    private String cellIndex;
    /**
     * 物理表中对应的字段
     */
    private String feild;
    /**
     * 物理表中对应的字段
     */
    private String fieldRowValue;
    /**
     * 虚拟cell位置时,要映射的类中的方法
     */
    private String getValueFunction;
    /**
     * 映射关联 其他节点的内容
     */
    private String ref;
    /**
     * 规定cell内值的数据类型
     */
    private String type;
    /**
     * 数据验证格式
     */
    private String warningFormat;
    /**
     * 是否为主键
     */
    private boolean isPrimary;

    private int required=-1;//设置默认值，要使用有设置默认值的习惯

    /**
     * 排序序列号
     */
    public int sortNum ;

    public String getCellIndex() {
        return cellIndex;
    }

    public void setCellIndex(String cellIndex) {
        this.cellIndex = cellIndex;
    }

    public String getFeild() {
        return feild;
    }

    public void setFeild(String feild) {
        this.feild = feild;
    }

    public String getGetValueFunction() {
        return getValueFunction;
    }

    public void setGetValueFunction(String getValueFunction) {
        this.getValueFunction = getValueFunction;
    }

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getWarningFormat() {
        return warningFormat;
    }

    public void setWarningFormat(String warningFormat) {
        this.warningFormat = warningFormat;
    }

    public boolean isPrimary() {
        return isPrimary;
    }

    public void setPrimary(boolean primary) {
        isPrimary = primary;
    }

    public int getSortNum() {
        return sortNum;
    }

    public void setSortNum(int sortNum) {
        this.sortNum = sortNum;
    }

    public String getFieldRowValue() {
        return fieldRowValue;
    }

    public void setFieldRowValue(String fieldRowValue) {

        this.fieldRowValue = fieldRowValue;
    }

    public int getRequired() {
        return required;
    }

    public void setRequired(int required) {
        this.required = required;
    }
}
