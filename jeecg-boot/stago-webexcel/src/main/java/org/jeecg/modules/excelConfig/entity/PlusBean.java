package org.jeecg.modules.excelConfig.entity;

import java.util.List;

/**
 * Created by mas on 2020/8/4.
 */
public class PlusBean {
    private List modelData;
    private String cellRange;
    private String cellType;
    private String cls;
    private String modelid;
    public String getCellRange() {
        return cellRange;
    }
    public void setCellRange(String cellRange) {
        this.cellRange = cellRange;
    }
    public String getCellType() {
        return cellType;
    }
    public void setCellType(String cellType) {
        this.cellType = cellType;
    }
    public String getModelid() {
        return modelid;
    }
    public void setModelid(String modelid) {
        this.modelid = modelid;
    }

    public String getCls() {
        return cls;
    }

    public void setCls(String cls) {
        this.cls = cls;
    }


    public String getSheetName() {
        return sheetName;
    }

    public void setSheetName(String sheetName) {
        this.sheetName = sheetName;
    }

    private String sheetName;

    public List getModelData() {
        return modelData;
    }

    public void setModelData(List modelData) {
        this.modelData = modelData;
    }



}
