package org.jeecg.modules.excelConfig.entity;

/**
 * Created by qizhang on 2020/3/27.
 */
public class ViewBean {

    /**
     * 子节点集合
     */
    private PropertysBean propertys;

    private Range range= new Range();

    public Range getRange() {
        int t= Integer.parseInt( this.getTop());
        int l=  Integer.parseInt(this.getLeft());
        range.setLef(l);
        range.setTop(t);
        return range;
    }


    /**
     * ONE: 1:1,LOOP:1:N
     */
    private String type ;

    /**
     * 指向的模型配置
     */
    private String modelId;

    /**
     * 关联关系
     */
    private String ref;

    /**
     * 左侧偏移量 默认为0 查询用
     */
    private String left;

    /**
     * 上侧偏移量 默认为0 查询用
     */
    private String top;

    /**
     * entity的排序
     */
    private Integer sortNum;
    /**
     * sheet 名称
     */
    private String sheetName ;


    public PropertysBean getPropertys() {
        return propertys;
    }

    public void setPropertys(PropertysBean propertys) {
        this.propertys = propertys;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    public String getLeft() {
        return left;
    }

    public void setLeft(String left) {
        this.left = left;
    }

    public String getTop() {
        return top;
    }

    public void setTop(String top) {
        this.top = top;
    }

    public Integer getSortNum() {
        return sortNum;
    }

    public void setSortNum(Integer sortNum) {
        this.sortNum = sortNum;
    }

    public void setRange(Range range) {
        this.range = range;
    }
    public String getModelId() {
        return modelId;
    }

    public void setModelId(String modelId) {
        this.modelId = modelId;
    }

    public String getSheetName() {
        return sheetName;
    }

    public void setSheetName(String sheetName) {
        this.sheetName = sheetName;
    }
}
