package org.jeecg.modules.excelConfig.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import com.google.common.collect.Lists;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.jeecg.modules.excelConfig.entity.*;


/**
 * 获取映射配置
 * 约束:xml文件获取的所有内容,在做比较过程中,统一将其变为小写字母
 * Created by qizhang on 2020/3/27.
 */
public class ConfigReadFactory {

    /**
     * 传入 rootElement 对象,返回 ExcelConfigBean 实体
     * @parameter rootElement
     * @return ExcelConfigBean
     */
    public static ExcelConfigBean getExcelConfigBeanElement(String path){
        ExcelConfigBean excelConfigBean = new ExcelConfigBean();

        try {
            InputStream in = new FileInputStream(path);

            SAXReader reader = new SAXReader();
            Document document = reader.read(in);
            Element rootElement = document.getRootElement();
            //递归遍历根节点所有的下一级子节点
            List<Element> listElement=rootElement.elements();
            for(Element e:listElement){
                String name = e.getName();
                name = name.toLowerCase();
                if ("views".equalsIgnoreCase(name)){
                   if(e.attribute("action").getValue().equalsIgnoreCase("query")){
                       ViewsBean viewsBean = getViews(e);
                       excelConfigBean.setViewsForQuery(viewsBean);
                   }
                    if(e.attribute("action").getValue().equalsIgnoreCase("save")){
                        ViewsBean  viewsBean = getViews(e);
                        excelConfigBean.setViewsForSave(viewsBean);
                    }
                }else if ("models".equalsIgnoreCase(name)){
                     ModelsBean modelsBean = getModels(e);
                     excelConfigBean.setModelsBean(modelsBean);
                }else if ("parameters".equalsIgnoreCase(name)){
                    ParametersBean parametersBeanElement = getParameters(e);
                    excelConfigBean.setParametersBean(parametersBeanElement);
                }else if ("WebExcleView".equalsIgnoreCase(name)){//增加配置文件XML，用来实现WebExcel前端组件显示
                    WebExcelViewBean webExcelViewBean =new WebExcelViewBean();
                    List<Attribute> listAttr=e.attributes();
                    for(Attribute attr:listAttr){
                        if ("JsSource".equalsIgnoreCase(attr.getName())){
                            webExcelViewBean.setJsSource(attr.getValue());
                        }
                      }
                    List<Element> PlusElement=e.elements();
                    List<PlusBean> PlusBeans= new ArrayList<>();
                    for(Element sub:PlusElement){
                        PlusBean plusBean  = new PlusBean();
                        List<Attribute> subAttr=sub.attributes();
                        for(Attribute attr: subAttr){
                            if ("cellRange".equalsIgnoreCase(attr.getName())){
                                plusBean.setCellRange(attr.getValue());
                            }
                            if ("CellType".equalsIgnoreCase(attr.getName())){
                                plusBean.setCellType(attr.getValue());
                            }
                            if ("modelid".equalsIgnoreCase(attr.getName())){
                                plusBean.setModelid(attr.getValue());
                            }
                            if ("sheetName".equalsIgnoreCase(attr.getName())){
                                plusBean.setSheetName(attr.getValue());
                            }
                            if ("cls".equalsIgnoreCase(attr.getName())){
                                plusBean.setCls(attr.getValue());
                            }
                        }
                        PlusBeans.add(plusBean);
                    }
                    webExcelViewBean.setPlusBean(PlusBeans);
                    excelConfigBean.setWebExcelViewBean(webExcelViewBean);
                  }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return excelConfigBean;
    }

    private static ViewsBean getViews(Element XML){
        List<ViewBean> ViewBeans = Lists.newArrayList();
        ViewsBean viewsBean = new ViewsBean();
        //当前节点的名称、文本内容和属性
        List<Attribute> listAttr=XML.attributes();//当前节点的所有属性的list
        for(Attribute attr:listAttr){//遍历当前节点的所有属性
            String name=attr.getName();//属性名称
            String value=attr.getValue();//属性的值
            name = name.toLowerCase();
            if ("action".equalsIgnoreCase(name)){
                viewsBean.setAction(value);
            }
        }

        //递归遍历根节点所有的下一级子节点
        List<Element> listElement=XML.elements();
        for(Element e:listElement){
            ViewBean viewBean = getView(e);
            ViewBeans.add(viewBean);
        }
        viewsBean.setViewBeans(ViewBeans);
        return viewsBean;
    }


    private static ViewBean getView(Element entityElement){
        ViewBean entityBean = new ViewBean();
        //当前节点的名称、文本内容和属性
        List<Attribute> listAttr=entityElement.attributes();//当前节点的所有属性的list
        for(Attribute attr:listAttr){//遍历当前节点的所有属性
            String name=attr.getName();//属性名称
            String value=attr.getValue();//属性的值
            name = name.toLowerCase();
            if ("type".equalsIgnoreCase(name)){
                entityBean.setType(value);
            }else if ("modelId".equalsIgnoreCase(name)){
                entityBean.setModelId(value);
            }
            else if ("ref".equalsIgnoreCase(name)){
                entityBean.setRef(value);
            }else if ("sortnum".equalsIgnoreCase(name)){
                entityBean.setSortNum(Integer.parseInt(value));
            }else if ("left".equalsIgnoreCase(name)){
                entityBean.setLeft(value);
            }else if ("top".equalsIgnoreCase(name)){
                entityBean.setTop(value);
            }else if ("sheetname".equalsIgnoreCase(name)){
                entityBean.setSheetName(value);
            }else {

            }
        }
        //递归遍历根节点所有的下一级子节点
        List<Element> listElement=entityElement.elements();
        for(Element e:listElement){
            if ("propertys".equalsIgnoreCase(e.getName().toLowerCase())){
                PropertysBean propertysBean = getPropertys(e);
                entityBean.setPropertys(propertysBean);
            }
        }
        return entityBean;
    }



    private static ModelsBean getModels(Element XML){
        ModelsBean modelsBean = new ModelsBean();
        List<ModelBean> ModelBeans = Lists.newArrayList();
        //递归遍历根节点所有的下一级子节点
        List<Element> listElement=XML.elements();
        for(Element e:listElement){
            if ("model".equalsIgnoreCase(e.getName().toLowerCase())){
                ModelBean modelBean = getModel(e);
                ModelBeans.add(modelBean);
            }
        }

        modelsBean.setCls(XML.attribute("cls").getValue());
        modelsBean.setModelBeans(ModelBeans);
        return modelsBean;
    }


    private static ModelBean getModel(Element XML){
        ModelBean modelBean  = new ModelBean();
        //当前节点的名称、文本内容和属性
        List<Attribute> listAttr=XML.attributes();//当前节点的所有属性的list
        for(Attribute attr:listAttr){//遍历当前节点的所有属性
            String name=attr.getName();//属性名称
            String value=attr.getValue();//属性的值
            if ("id".equalsIgnoreCase(name.toLowerCase())){
                modelBean.setId(value);
            }
        }
        //递归遍历根节点所有的下一级子节点
        List<Element> listElement=XML.elements();
        for(Element e:listElement){

            if ("parameters".equalsIgnoreCase(e.getName().toLowerCase())){
                ParametersBean parametersBeanElement = getParameters(e);
                modelBean.setParametersBean(parametersBeanElement);
            }else if ("sqlstrs".equalsIgnoreCase(e.getName().toLowerCase())){
                SqlStrsBean sqlStrsBean = getSqlStrs(e);
                modelBean.setSqlStrsBean(sqlStrsBean);
            }
        }

        return modelBean;
    }




    private static PropertysBean getPropertys(Element propertysElement){
        PropertysBean propertysBean = new PropertysBean();
        List<PropertyBean> propertyBeans = Lists.newArrayList();
        //递归遍历根节点所有的下一级子节点
        List<Element> listElement=propertysElement.elements();
        for(Element e:listElement){
            PropertyBean propertyBean = getProperty(e);
            propertyBeans.add(propertyBean);
        }
        propertysBean.setPropertyBeans(propertyBeans);
        return propertysBean;
    }


    private static PropertyBean getProperty(Element propertyElement){
        PropertyBean propertyBean = new PropertyBean();


        //当前节点的名称、文本内容和属性
        List<Attribute> listAttr=propertyElement.attributes();//当前节点的所有属性的list
        for(Attribute attr:listAttr){//遍历当前节点的所有属性
            String name=attr.getName();//属性名称
            String value=attr.getValue();//属性的值
            name = name.toLowerCase();
            if ("cellindex".equalsIgnoreCase(name)){
                propertyBean.setCellIndex(value);
            }else if ("feild".equalsIgnoreCase(name)){
                propertyBean.setFeild(value);
            }else if ("getValueFunction".equalsIgnoreCase(name)){
                propertyBean.setGetValueFunction(value);
            }else if ("ref".equalsIgnoreCase(name)){
                propertyBean.setRef(value);
            }else if ("type".equalsIgnoreCase(name)){
                propertyBean.setType(value);
            }else if ("warningformat".equalsIgnoreCase(name)){
                propertyBean.setWarningFormat(value);
            }else if ("isprimary".equalsIgnoreCase(name)){
                propertyBean.setRef(value);
            }else if ("sortnum".equalsIgnoreCase(name)){
                propertyBean.setSortNum(Integer.parseInt(value));
            }else if ("required".equalsIgnoreCase(name)){
                propertyBean.setRequired(Integer.parseInt(value));
            }else if ("feildrowvalue".equalsIgnoreCase(name)){
                propertyBean.setFieldRowValue(value);
            }else if ("feildrowvalue".equalsIgnoreCase(name)){
                propertyBean.setFieldRowValue(value);
            }
        }
        return propertyBean;
    }


    private static ParametersBean getParameters(Element parametersElement){
        ParametersBean parametersBean = new ParametersBean();
        List<ParameterBean> parameterBeans = Lists.newArrayList();
        //递归遍历根节点所有的下一级子节点
        List<Element> listElement=parametersElement.elements();
        for(Element e:listElement){
            ParameterBean parameterBean = getParameter(e);
            parameterBeans.add(parameterBean);
        }
        parametersBean.setParameterBeans(parameterBeans);
        return parametersBean;
    }

    private static ParameterBean getParameter(Element parameterElement){
        ParameterBean parameterBean = new ParameterBean();
        //当前节点的名称、文本内容和属性
        List<Attribute> listAttr=parameterElement.attributes();//当前节点的所有属性的list
        for(Attribute attr:listAttr){//遍历当前节点的所有属性
            String name=attr.getName();//属性名称
            String value=attr.getValue();//属性的值
            name = name.toLowerCase();
            if ("name".equalsIgnoreCase(name)){
                parameterBean.setName(value);
            }else if ("class".equalsIgnoreCase(name)){
                parameterBean.setCls(null);
            }else if ("value".equalsIgnoreCase(name)){
                parameterBean.setValue(value);
            }else if ("htmltype".equalsIgnoreCase(name)){
                parameterBean.setHtmlType(value);
            }else if ("isrequired".equalsIgnoreCase(name)){
                if ("0".equalsIgnoreCase(value)){
                    parameterBean.setRequired(true);
                }else if ("1".equalsIgnoreCase(value)){
                    parameterBean.setRequired(false);
                }
            }else if ("isshow".equalsIgnoreCase(name)){
                if ("0".equalsIgnoreCase(value)){
                    parameterBean.setShow(true);
                }else if ("1".equalsIgnoreCase(value)){
                    parameterBean.setShow(false);
                }
            }else if ("desc".equalsIgnoreCase(name)){
                parameterBean.setDesc(value);
            }else if ("modelId".equalsIgnoreCase(name)){
                parameterBean.setModelId(value);
            }
        }

        return parameterBean;
    }



    private static SqlStrsBean getSqlStrs(Element queryStrsElement){
        SqlStrsBean sqlStrsBean = new SqlStrsBean();
        //递归遍历根节点所有的下一级子节点
        List<Element> listElement=queryStrsElement.elements();
        for(Element e:listElement){
            String name = e.getName();
            if ("sqlStr".equalsIgnoreCase(name.toLowerCase())){
                String composeSql = "";
                composeSql = composeSql + e.getText();
                sqlStrsBean.setSqlStr(new StringBuilder(composeSql));
            }
        }
        return sqlStrsBean;
    }

    public static void main(String[] args) {
           String  XMLPath= "E:\\CODE\\src\\main\\java\\com\\thinkgem\\jeesite\\common\\excelConfig\\xml\\TE.xml";
            ExcelConfigBean excelConfigBean = ConfigReadFactory.getExcelConfigBeanElement(XMLPath);


    }

}
