package org.jeecg.modules.excelConfig.util;
import com.grapecity.documents.excel.IWorksheet;
import com.grapecity.documents.excel.Workbook;
import org.apache.commons.lang.StringUtils;
import org.jeecg.modules.excelConfig.entity.*;
import org.jeecg.modules.excelConfig.exector.WebExcelInterFace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.math.BigDecimal;
import java.util.*;


public class ReadBean2Excel {
    private Logger  logger = LoggerFactory.getLogger(ReadBean2Excel.class);
    private boolean temlateSyntax=true;
    private ExcelConfigBean excelConfigBean;
    private Workbook workbook ;
    public void init(ExcelConfigBean excelConfigBean ) throws Exception {
        this.excelConfigBean = excelConfigBean;
        ParameterBean template_filePath = excelConfigBean.getParameterByName("template_FilePath");
        ParameterBean temlateSyntaxParam = excelConfigBean.getParameterByName("temlateSyntax");//使用模板语法，葡萄城
        workbook = GrapeCityExcelUtil.getInstance().getWorkbook();

        if(temlateSyntaxParam!=null){
              temlateSyntax = Boolean.parseBoolean(temlateSyntaxParam.getValue());
        }
        String template="";
        if(template_filePath!=null) {
            template= template_filePath.getValue();
        }
        if (!StringUtils.isEmpty(template)){
            workbook.open(template);
        }

        ViewsBean viewsForQuery = excelConfigBean.getViewsForQuery();
        List<ViewBean> viewBeans = viewsForQuery.getViewBeans();

        for(int i=0;i<viewBeans.size();i++){

            ViewBean CurviewBean=viewBeans.get(i);
            boolean rootElement=    excelConfigBean.isRootView(CurviewBean);
            if (rootElement){
                Range range=new  Range();
                range.setTop(0);
                range.setLef(0);
                if (!StringUtils.isEmpty(template)  & temlateSyntax ){
                  ArrayList<Object>  dataSource = WrapToVO(CurviewBean,null);
                 //Init template global settings
                // workbook.getNames().add("TemplateOptions.KeepLineSize", "true");
                  workbook.addDataSource(CurviewBean.getModelId(), dataSource);
                }else{
                    range.setLef(range.getLef()+CurviewBean.getRange().getLef());
                    range.setTop(range.getTop()+CurviewBean.getRange().getTop());
                    range=  write2Workbook(range,CurviewBean,null);
                    logger.debug((CurviewBean.getModelId()+"                                                    ").substring(0,50)+ " 输出行记录 ："+range.getTop()+" 行");
                }
            }

        }

        if (!StringUtils.isEmpty(template)  & temlateSyntax ){
            workbook.getNames().add("TemplateOptions.KeepLineSize", "true");
            workbook.processTemplate();
        }

        if(excelConfigBean.getParameterByName("outputFilePath")==null |"".equalsIgnoreCase(excelConfigBean.getParameterByName("outputFilePath").getValue()) ){
            excelConfigBean.setParameterByName("outputFilePath", (UUID.randomUUID()).toString()+".xlsx");
        }
        workbook.save(excelConfigBean.getParameterByName("outputFilePath").getValue());
    }

    /**
    * 根据配置文件获取数据集，包装为多层VO，并设置 Set Get方法
    * 最终将此记录集传给模板文件进行解析
    *
    * 由于动态反射类，所有的字段名全部设置为小写，含有大写的无法读取，原因待查
    */

    public ArrayList<Object>  WrapToVO(ViewBean CurentityBean, ViewBean ParentEntityBean) throws Exception {

        PropertysBean propertysBean=null;
        if(ParentEntityBean!=null){
            propertysBean=ParentEntityBean.getPropertys();
        }
        // 判断是否有接管类。如果有，使用接管类
        List  dataList = excelConfigBean.getDataForList(CurentityBean.getModelId(),propertysBean);
        if(dataList==null){
         throw new Exception("查询数据异常："+CurentityBean.getModelId());
        }
        //包装记录集
         ArrayList<Object> entitys = new ArrayList<Object>();
        //循环数据集，
        for (int k = 0; k < dataList.size(); k++) {
            Map o = (Map)dataList.get(k);
            HashMap propertyMap = new HashMap();//动态Bean属性
            PropertysBean propertys = CurentityBean.getPropertys();
            List<PropertyBean> propertyBeans = propertys.getPropertyBeans();
            for (int l = 0; l < propertyBeans.size(); l++) {
                PropertyBean propertyBean = propertyBeans.get(l);
                String feild = propertyBean.getFeild();
                propertyMap.put(feild.toLowerCase(),  Class.forName(propertyBean.getType()));//添加动态属性
            }

            //判断是否有下级节点

            List<ViewBean> childEntityBeanList = excelConfigBean.getChildViewForQueryBeanList(CurentityBean.getModelId());
            if (childEntityBeanList.size()>0){
                for (int j = 0; j < childEntityBeanList.size(); j++) {
                    ViewBean child= childEntityBeanList.get(j);
                    propertyMap.put( child.getModelId(),Class.forName("java.util.ArrayList"));
                }
            }
            // 生成动态 Bean的工具，将属性传入到工具类
            DynamicEntity bean = new DynamicEntity(propertyMap);
            Object object = bean.getObject();
            //为动态Bean赋值
            for (int l = 0; l < propertyBeans.size(); l++) {
                PropertyBean propertyBean = propertyBeans.get(l);
                String feild = propertyBean.getFeild();
                Object feildValue = o.get(feild);

               String   ClasName= propertyBean.getType();
                if ( "java.lang.String".equalsIgnoreCase(ClasName)) {
                    feildValue = String.valueOf( feildValue);
                } else if  ( "java.lang.Double".equalsIgnoreCase(ClasName)) {
                    feildValue = Double.valueOf(((Number) feildValue).doubleValue());
                } else if  ( "java.lang.Integer".equalsIgnoreCase(ClasName)) {
                    feildValue = Integer.parseInt(String.valueOf( feildValue).trim());
                } else if ( "java.lang.Number".equalsIgnoreCase(ClasName)) {
                    feildValue = Double.valueOf(((BigDecimal) feildValue).doubleValue());
                } else  {

                }

                if(propertyBean.getGetValueFunction()!=null){
                    logger.debug((propertyBean.getGetValueFunction()));
                }


                bean.setValue(feild.toLowerCase(),  feildValue); //  Bean 填充数据

                propertyBean.setFieldRowValue(String.valueOf( feildValue));
                propertyBeans.set(l,propertyBean);
            }
            CurentityBean.setPropertys(propertys);

            if (childEntityBeanList.size()>0){
                for (int j = 0; j < childEntityBeanList.size(); j++) {
                    //包装记录集
                    ViewBean child= childEntityBeanList.get(j);
                    String rsRef= child.getModelId();
                    ArrayList<Object> Sub_entitys= WrapToVO(childEntityBeanList.get(j),CurentityBean);
                    bean.setValue(rsRef,  Sub_entitys); //  Bean 填充数据
                }
            }
             object = bean.getObject();
            entitys.add(object);
        }
       return  entitys;
    }

    /**
     * 不使用Excel模板，直接输出到Excel中，按照View中的配置的规则进行输出
     * @param range
     * @param CurentityBean
     * @param ParentEntityBean
     * @param workbook
     * @return
     * @throws Exception
     */

    public Range write2Workbook(Range range ,ViewBean CurentityBean, ViewBean ParentEntityBean) throws Exception {

            PropertysBean propertysBean=null;
            if(ParentEntityBean!=null){
              propertysBean=ParentEntityBean.getPropertys();
            }
            // 判断是否有接管类。如果有，使用接管类
           List  dataList = excelConfigBean.getDataForList(CurentityBean.getModelId(),propertysBean);
           String sheetName = CurentityBean.getSheetName();
            if (sheetName==null){
                logger.debug( "未指定SheetName");
                sheetName="Sheet1";
            }
            //每次计算一次相对偏移 TOP为全局
            int top =range.getTop();

           //循环数据集，填充的Bean中
            for (int k = 0; k < dataList.size(); k++) {
                int left= Integer.parseInt(CurentityBean.getLeft());//计算偏移 left每行记录后清零

                Map o = (Map)dataList.get(k);

                PropertysBean propertys = CurentityBean.getPropertys();
                List<PropertyBean> propertyBeans = propertys.getPropertyBeans();

                //将当前数据集中查询到的数据，填充到属性集中
                for (int l = 0; l < propertyBeans.size(); l++) {
                    PropertyBean propertyBean = propertyBeans.get(l);
                    String feild = propertyBean.getFeild();
                    Object feildValue = o.get(feild);
                    propertyBean.setFieldRowValue(String.valueOf( feildValue));
                    propertyBeans.set(l,propertyBean);

                 }
                //如果有自定义行触发类，则调用行触发类进行数据处理
                String CLS=excelConfigBean.getModelsBean().getCls();
                if(CLS!=null  & !"".equalsIgnoreCase(CLS)){
                    Class c = Class.forName(CLS);
                      WebExcelInterFace ImplClass = (WebExcelInterFace) c.newInstance();
                    propertys=  ImplClass.onGetCellVue(CurentityBean.getModelId(),propertys);
                }
                CurentityBean.setPropertys(propertys);


                //处理数据，写入Excel
                for (int l = 0; l < propertyBeans.size(); l++) {
                    PropertyBean propertyBean = propertyBeans.get(l);
                    Object feildValue =propertyBean.getFieldRowValue();

                    String CellIndex= propertyBean.getCellIndex();
                    if(!"-1".equalsIgnoreCase(CellIndex)){
                        top+=  Integer.parseInt(  CellIndex.split(",") [0]);//行
                        left+=  Integer.parseInt( CellIndex.split(",")[1]);//列

                        IWorksheet worksheet = workbook.getWorksheets().get(sheetName);
                        if (null==worksheet){
                            worksheet = workbook.getWorksheets().add();
                        }

                        String   ClasName= propertyBean.getType();
                        if(String.valueOf(feildValue).trim().equalsIgnoreCase("null")){
                            worksheet.getRange(top,left).setValue(null);
                        }else{
                            if ( "java.lang.String".equalsIgnoreCase(ClasName)) {
                                // feildValue = String.valueOf( feildValue);
                                worksheet.getRange(top,left).setValue(feildValue);
                            } else if  ( "java.lang.Double".equalsIgnoreCase(ClasName)) {
                                // feildValue = Double.valueOf(((Number) feildValue).doubleValue());
                                worksheet.getRange(top,left).setValue(Double.parseDouble(String.valueOf(feildValue).trim()));
                            } else if  ( "java.lang.Integer".equalsIgnoreCase(ClasName)) {
                                //  feildValue = Integer.parseInt(String.valueOf( feildValue).trim());
                                try {
                                    Integer I_Value=Integer.parseInt(String.valueOf(feildValue).trim());
                                    worksheet.getRange(top,left).setValue(I_Value);
                                }catch (Exception ex){
                                    throw  new Exception("希望得到Integer类型但是字段值为："+feildValue);
                                }

                            }
                        }
                    }
                }
                range.setTop(top+1);
                top+=1;

                //判断是否有下级节点 这个Bean作为参数，传递到下级实体中
                List<ViewBean> childEntityBeanList = excelConfigBean.getChildViewForQueryBeanList(CurentityBean.getModelId());

                if (childEntityBeanList.size()>0){
                    for (int j = 0; j < childEntityBeanList.size(); j++) {
                        Range  rangesub=    write2Workbook(range,childEntityBeanList.get(j),CurentityBean);
                        range.setTop(rangesub.getTop());
                        top=rangesub.getTop();
                    }
                }
            }
            return range;
        }




    //在使用模板后，是否使用葡萄城Excel的语法结构
    //默认为FALSE 即 使用模板就使用葡萄城Excel的语法结构
    public void setTemlateSyntax(boolean temlateSyntax) {
        this.temlateSyntax = temlateSyntax;
    }
}
