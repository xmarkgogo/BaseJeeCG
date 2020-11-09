package org.jeecg.modules.excelConfig.impl;

import org.jeecg.modules.excelConfig.entity.ExcelConfigBean;
import org.jeecg.modules.excelConfig.entity.PropertyBean;
import org.jeecg.modules.excelConfig.entity.PropertysBean;
import org.jeecg.modules.excelConfig.entity.SqlWrapBean;
import org.jeecg.modules.excelConfig.exector.AbstractWebExcel;
import org.jeecg.modules.excelConfig.exector.WebExcelInterFace;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 这个是一个范例类，用来理解如何使用自定义接口
 */
public class ImplWebExcel extends AbstractWebExcel implements WebExcelInterFace {

    @Override
    public PropertysBean onGetCellVue(String ViewBeanId, PropertysBean propertys) throws Exception {

        List<PropertyBean> propertyBeans = propertys.getPropertyBeans();
        for (int l = 0; l < propertyBeans.size(); l++) {
            PropertyBean propertyBean = propertyBeans.get(l);
            String feild = propertyBean.getFeild();
            if("ZZStudent_Query".equalsIgnoreCase(ViewBeanId) &"VFeild".equalsIgnoreCase(feild) ){
                String feildValue =propertyBean.getFieldRowValue();
                propertyBean.setFieldRowValue(getVue("18"));
                propertyBeans.set(l,propertyBean);
            }

        }
        return propertys;
    }
    @Override
    public List onGetDataList(ExcelConfigBean instance, String CurrentModelID, PropertysBean propertys) throws Exception {

        if("virtual_Query".equalsIgnoreCase(CurrentModelID)){
            ArrayList R=new ArrayList();
            Map m=new HashMap<>();
            m.put("DESC","字段DESC");
            m.put("NAME","自定义字段NAME，来源于自定义类");
            R.add(m);
            return  R;
        }else{
            SqlWrapBean PackingBean= instance.getSqlWrapBean(CurrentModelID,propertys);
            return instance.getDataForList(PackingBean.getFinalSQL());
        }
    }


    public String getVue(String moneyNumText) {
        int moneyNum=Integer.parseInt(moneyNumText.trim());
        String res="";
        int i=3;
        if(moneyNum==0)
            return "零";
        while(moneyNum>0){
            res=ChineseUnit[i++]+res;
            res=ChineseNum[moneyNum%10]+res;
            moneyNum/=10;
        }
        return res.replaceAll("零[拾佰仟]", "零")
                .replaceAll("零+亿", "亿").replaceAll("零+万", "万")
                .replaceAll("零+元", "零").replaceAll("零+", "零");
    }
    private static final char[] ChineseNum = {'零', '壹', '贰', '叁', '肆', '伍', '陆', '柒', '捌', '玖'};
    private static final char[] ChineseUnit = {'里', '分', '角', '元', '拾', '佰', '仟', '万', '拾', '佰', '仟', '亿', '拾', '佰', '仟'};


}
