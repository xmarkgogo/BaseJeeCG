package org.jeecg.modules.excelConfig.entity;

import com.google.common.collect.Maps;

import java.util.Map;

/**
 *
 * Created by qizhang on 2020/4/9.
 */
public class SqlValueBean {

    private String dataTableName;
    private Map<String , Object> fieldMap = Maps.newHashMap();

    public String getDataTableName() {
        return dataTableName;
    }

    public void setDataTableName(String dataTableName) {
        this.dataTableName = dataTableName;
    }

    public Map getFieldMap() {
        return fieldMap;
    }

    public void setFieldMap(String fieldName,Object fieldValue) {
        fieldMap.put(fieldName,fieldValue);
    }
    //TODO
    public String toSqlStr(){

        return "";
    }
}
