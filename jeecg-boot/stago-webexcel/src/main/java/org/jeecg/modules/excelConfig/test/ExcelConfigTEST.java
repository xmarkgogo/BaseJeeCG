package org.jeecg.modules.excelConfig.test;

import org.jeecg.modules.excelConfig.entity.ExcelConfigBean;
import org.jeecg.modules.excelConfig.util.ConfigReadFactory;
import org.jeecg.modules.excelConfig.util.ReadBean2Excel;

import java.io.File;
import java.util.UUID;
/**
 * Created by mas on 2020/5/15.
 */
public class ExcelConfigTEST {
    static  String  XMLPath= "\\src\\main\\java\\com\\thinkgem\\jeesite\\common\\excelConfig\\test\\xml\\template";
    static  String  Excel_File= "d:\\"+ UUID.randomUUID().toString()+".xlsx";
    public static void main(String[] args) throws Exception {
        File file = new File("");
        String filePath = file.getCanonicalPath();
        XMLPath=filePath+XMLPath;

        testReadXMLtoTemlateExcel();
        testReadXMLtoExcel();

    }


    public static   void  testReadXMLtoExcel() throws Exception {
        ExcelConfigBean excelConfigBean = ConfigReadFactory.getExcelConfigBeanElement(XMLPath+"/00ExceConfigExample.xml");
        ReadBean2Excel readBean2Excel=new ReadBean2Excel();
        excelConfigBean.setParameterByName("template_FilePath", "C:\\temo\\test.xlsx");
        excelConfigBean.setParameterByName("outputFilePath", Excel_File);
        readBean2Excel.init(excelConfigBean);
        //读取Excel判断
    }


    public static   void  testReadXMLtoTemlateExcel() throws Exception {
        ExcelConfigBean excelConfigBean = ConfigReadFactory.getExcelConfigBeanElement(XMLPath+"/00ExceConfigExample.xml");
        ReadBean2Excel readBean2Excel=new ReadBean2Excel();
        excelConfigBean.setParameterByName("outputFilePath", Excel_File);
        excelConfigBean.setParameterByName("template_FilePath", XMLPath+"/xlsx/00ExceConfigExame.xlsx");
        readBean2Excel.init(excelConfigBean);
    }





}
