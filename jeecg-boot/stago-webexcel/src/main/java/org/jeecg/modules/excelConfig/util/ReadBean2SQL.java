package org.jeecg.modules.excelConfig.util;
import com.grapecity.documents.excel.*;
import org.apache.commons.lang.StringUtils;
import org.jeecg.common.util.DateUtils;
import org.jeecg.modules.excelConfig.entity.*;
import org.jeecg.modules.excelConfig.exector.WebExcelInterFace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.File;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * Created by qizhang on 2020/4/8.
 */

public class ReadBean2SQL {

    private Logger logger = LoggerFactory.getLogger(ReadBean2SQL.class);
    private ExcelConfigBean excelConfigBean;

    public void init(ExcelConfigBean excelConfigBean) throws Exception {
        this.excelConfigBean = excelConfigBean;
        Workbook workbook = GrapeCityExcelUtil.getInstance().getWorkbook();
        XlsxOpenOptions options = new XlsxOpenOptions();
        options.setImportFlags(EnumSet.of(ImportFlags.Data));
        String ExcelImportSource="";

        if(excelConfigBean.getParameterByName("template_FilePath")!=null){
            ExcelImportSource= excelConfigBean.getParameterByName("template_FilePath").getValue();
        }

        File file=new File(ExcelImportSource);
        if(file.exists()){
            workbook.open(ExcelImportSource, options);
        }else{
            throw  new Exception("ExcelSource ERROR"+ExcelImportSource);
        }

        logger.debug("读取数据文件："+ExcelImportSource);
        workbook.open(ExcelImportSource, options);
        ViewsBean ViewsForSave = excelConfigBean.getViewsForSave();
        List<ViewBean> viewBeans = ViewsForSave.getViewBeans();

        for(int i=0;i<viewBeans.size();i++){

            ViewBean CurrentViewBean=viewBeans.get(i);
            boolean rootElement=    excelConfigBean.isRootView(CurrentViewBean);
            if (rootElement) {
                Range range=CurrentViewBean.getRange();
                getDataAndCell(range,CurrentViewBean,null,workbook);
                //递归调用 每次返回最后的范围
            }
        }
    }


    private boolean getDataAndCell(Range range, ViewBean CurrentViewBean, PropertysBean ParentPropertysValue, Workbook workbook) throws Exception {

        String dataSetId = CurrentViewBean.getModelId();
        String sheetName = CurrentViewBean.getSheetName();
        int top=range.getTop();
        //避免堆栈溢出，减少递归调用
        while (true){
            logger.debug("读取模型配置："+CurrentViewBean.getModelId()+" 读取第"+(top+1)+"行数据");
            PropertysBean propertys = CurrentViewBean.getPropertys();
            List<PropertyBean> propertyBeans = propertys.getPropertyBeans();

                int left = Integer.parseInt(CurrentViewBean.getLeft());
                for (int l = 0; l < propertyBeans.size()  ; l++) {
                    PropertyBean propertyBean = propertyBeans.get(l);

                    String CellIndex = propertyBean.getCellIndex();
                    if (!"-1".equalsIgnoreCase(CellIndex)) {
                        top +=  Integer.parseInt(CellIndex.split(",")[0]);//行
                        left += Integer.parseInt(CellIndex.split(",")[1]);//列
                      //  String cell =GrapeCityExcelUtil.cellName(top,left);
                        IWorksheet worksheet = null;
                        if (StringUtils.isEmpty(sheetName)){
                            worksheet = workbook.getWorksheets().get(0);
                        }else {
                            worksheet = workbook.getWorksheets().get(sheetName);
                            if (null==worksheet){
                                worksheet = workbook.getWorksheets().get(0);
                            }
                        }
                        String cell = com.grapecity.documents.excel.CellInfo.CellIndexToName(top,left);
                        Object feildValue = worksheet.getRange(cell).getCells().getValue();//读取Excel中数据
                               feildValue = worksheet.getRange(cell).getCells().getText();

                        //flag 位置获取的内容 是否为空 ?返回true或false,并与它的valueEmpty的值进行比较,一旦出现不匹配的情况,则退出当前循环
                        int flag = (feildValue == null  || String.valueOf(feildValue).length() <= 0)?0:1;
                        int isvalueEmpty = propertyBean.getRequired();
                        if(isvalueEmpty!= flag){
                           //-1 代表 即使获取的值是空,也跳过
                            if (-1!=isvalueEmpty){
                                        String tip="";
                                        if(propertyBean.getRequired()==0){
                                            tip="必须为空";
                                        }
                                        if(propertyBean.getRequired()==1){
                                            tip="必须不能为空";
                                        }
                                        if(propertyBean.getRequired()==-1){
                                            tip="代表不检查单元格退出条件";
                                        }
                                        logger.debug("在单元格["+cell+"]设置终止条件["+tip+"],实际获取的值为：["+feildValue+"]");


                               //判断是否有下级
                                System.out.println(CurrentViewBean.getModelId());
                                List<ViewBean> childViewForSaveBeanList = excelConfigBean.getChildViewForSaveBeanList(CurrentViewBean.getModelId());
                                if(childViewForSaveBeanList.size()>0){
                                    getDataAndCell(range,childViewForSaveBeanList.get(0),propertys,workbook);
                                }
                                return false ;
                            }
                        }

                        String   ClasName= propertyBean.getType();
                        try{
                            if ( "java.lang.String".equalsIgnoreCase(ClasName)) {
                                feildValue = String.valueOf( feildValue);
                            } else if  ( "java.lang.Double".equalsIgnoreCase(ClasName)) {
                                feildValue = Double.valueOf(((Number) feildValue).doubleValue());
                            } else if  ( "java.lang.Integer".equalsIgnoreCase(ClasName)) {
                                feildValue = Integer.parseInt(String.valueOf( feildValue).trim());
                            } else if ( "java.lang.Number".equalsIgnoreCase(ClasName)) {
                                feildValue = Double.valueOf(((BigDecimal) feildValue).doubleValue());
                            } else  if ( "java.lang.Date".equalsIgnoreCase(ClasName))  {
                                if(feildValue==null | "".equalsIgnoreCase(String.valueOf(feildValue).trim())){
                                    feildValue = "1900/01/01";
                                }else {
                                    //判断是否是字符串格式日期
                                    DateFormat df2 = new SimpleDateFormat("yyyy/MM/dd");// HH:mm:ss
                                     boolean isTxtDate=false;
                                     Integer integerDay=0;
                                    //判断是否为日期格式，实际保存的值为数字
                                    try {
                                        integerDay= Integer.parseInt(String.valueOf( feildValue).trim());
                                    }catch (Exception E){
                                        isTxtDate=true;
                                    }
                                    if(isTxtDate){
                                        try{
                                           // feildValue=df2.format(DateUtils.parseDate(String.valueOf(feildValue)));
                                        }catch (Exception e){
                                            throw   new Exception("单元格 "+cell+"数据类型期望：yyyy/MM/dd"+" 实际格式为："+feildValue);
                                        }
                                    }else{
                                        Calendar calendar = new GregorianCalendar(1900,0,-1);
                                        Date d = calendar.getTime();
                                       // Date dd = DateUtils.addDays(d, integerDay);
                                       // feildValue = df2.format(dd);
                                    }
                                }
                            }else{
                            }
                            propertyBean.setFieldRowValue(String.valueOf(feildValue));
                            propertyBeans.set(l, propertyBean);



                        }catch (Exception ex){
                          throw   new Exception("单元格 "+cell +" -> 数据类型期望："+ClasName+" 实际是："+feildValue);
                        }

                    } else {
                        //TODO//如果是虚拟字段，则直接通过类获取数值
                        propertyBean.setFieldRowValue("newid()");
                        propertyBeans.set(l, propertyBean);
                    }
                }
                propertys.setPropertyBeans(propertyBeans);


                String CLS=excelConfigBean.getModelsBean().getCls();
                if (CLS!=null){
                    if(CLS!=null & (!"".equalsIgnoreCase(CLS))){
                        Class c = Class.forName(CLS);
                        WebExcelInterFace ImplClass = (WebExcelInterFace) c.newInstance();
                        ImplClass.onBeforeSave(excelConfigBean,dataSetId,propertys);
                    }
                }
                //属性值都赋值完成后，进行循环属性，构造SQL
                SqlWrapBean sql = excelConfigBean.getSqlWrapBean(dataSetId, propertys,ParentPropertysValue);
                excelConfigBean.addWrapBeanToList(sql);
                top=top+1;//当前实体循环
                range.setTop(top);//下级实体循环

               //避免堆栈溢出，减少递归调用
               // getDataAndCell(range,CurrentViewBean,propertys,workbook);
                //递归返回，接上下文，恢复现场，如果到达根原则，则退出
                ViewBean VB=  excelConfigBean.getSaveViewBeanById(CurrentViewBean.getRef());
                if(VB!=null){
                    getDataAndCell(range,VB,propertys,workbook);
                }
        }
        //return true;
    }

    public boolean GetSqlToTxt(String Path) throws  Exception {
        PrintStream ps = new PrintStream(Path);
        System.setOut(ps);//把创建的打印输出流赋给系统。即系统下次向 ps输出
        List<SqlWrapBean> sqlPackinList = excelConfigBean.getWrapBeanList();
        try {
            for (SqlWrapBean wrapBean : sqlPackinList){
                System.out.println(wrapBean.getFinalSQL());
            }
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
        return true;
    }
    public int runSql() throws  Exception{
        int recCount=0;
        List<SqlWrapBean> sqlPackinList = excelConfigBean.getWrapBeanList();
        JdbcTemplate jdbcTemplate=excelConfigBean.getJdbcTemplate();
        Connection conn = null;
        PreparedStatement pstmt = null;
        String SQL="";
        try {
            conn = jdbcTemplate.getDataSource().getConnection();
            conn.setAutoCommit(false);
            for (SqlWrapBean wrapBean : sqlPackinList){
                recCount+=1;
                SQL=wrapBean.getFinalSQL();
                logger.debug("RunSQL："+SQL);
                pstmt = conn.prepareStatement(SQL);
                pstmt.execute();
            }
            conn.commit();

        } catch (SQLException e) {
            conn.rollback();//回滚
            logger.error("StackTrace SQL=\r\n["+SQL+"]");
            e.printStackTrace();
            throw new Exception("执行SQL出错\n"+e.getMessage()+"\n"+"SQL="+SQL);
        } finally {
            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return recCount;
    }

}

