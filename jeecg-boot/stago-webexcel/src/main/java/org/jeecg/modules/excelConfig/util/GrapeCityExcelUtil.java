package org.jeecg.modules.excelConfig.util;

import com.grapecity.documents.excel.IWorksheet;
import com.grapecity.documents.excel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.math.BigDecimal;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by mas on 2020/4/15.
 */
public class GrapeCityExcelUtil {
    protected static Logger logger = LoggerFactory.getLogger(GrapeCityExcelUtil.class);
    public static  String  Key="127.0.0.1,773657759564659#A0tkIN6QjN5kTN7cTN6MzN7IiOiQWSisnOiQkIsISP3c6SvcUb9VGcrBHaIp5SslFOKJDOvJUULFVOxYUc4h6dzRjV8d7KEh7Q7cEW7c4SvYnTUZTZ7IXYQZWSJh6cBdzMttSRh9WZpREMWJ6LWpkZ7gEZk96aiojITJCLwATM9cjM7YzN0IicfJye#4Xfd5nISNFNOJiOiMkIsIyM6BSY6FmSgwWZjhXRgI7bmBCduVWb5N6bEByQHJiOi8kI1tlOiQmcQJCLiEDM9ITMwAyMxEDMwIDMyIiOiQncDJCLig1jlzahlDZmpnInmnIvvXKtmnKpljIvv/KnmDoim7qlmr8roj9qpTZol7JgmLiOiEmTcJdL";
    private static GrapeCityExcelUtil instance;
    private GrapeCityExcelUtil(){

    }
    public static GrapeCityExcelUtil getInstance(){
        if(instance==null){
            instance=new GrapeCityExcelUtil();
        }
        return instance;
    }

    public Workbook getWorkbook(){
        Workbook.SetLicenseKey(Key);
        Workbook workbook = new Workbook();
        return workbook;
    }


    public static void main(String[] args) {

        long l = System.currentTimeMillis();
         Workbook workbook = new Workbook();;
        workbook.open("C:\\temp\\test001.xlsx");

        IWorksheet worksheet = workbook.getWorksheets().get("Proposition");
        for(int i=0;i<100;i++){

            worksheet.getRange("C34").setValue(100);
            worksheet.getRange("C36").setValue(100);
            worksheet.getRange("C38").setValue(100);
            worksheet.getRange("C40").setValue(100);
            worksheet.getRange("C42").setValue(100);
            worksheet.getRange("C44").setValue(100);
            workbook.getWorksheets().get("Dealer profit Sum").getRange("E24").getValue();
            //System.out.println();

        }


        logger.debug( (System.currentTimeMillis()-l)/1000  +"s");

    }



    public static void load() throws FileNotFoundException {
//        ExcelConfigBean e = new ExcelConfigBean().getInstance();
//
//        JdbcTemplate jdbcTemplate = e.setDataSource();
//        NPS_WebExcelUtils nps_webExcelUtils = new NPS_WebExcelUtils();
//        String  querySql = "SELECT temp.formId AS id\n" +
//                "FROM (\n" +
//                "SELECT id AS formId FROM WF_NSP_FORM_Head WHERE ID NOT IN(SELECT FORM_ID FROM WF_FlowInstance)\n" +
//                "UNION ALL\n" +
//                "SELECT FORM_ID AS formId FROM WF_FlowInstance WF JOIN WF_NSP_FORM_Head WNFH ON WF.FORM_ID=WNFH.ID AND WF.Status=-1\n" +
//                ") temp\n" +
//                "JOIN WF_NSP_FORM_Head WNFH ON WNFH.ID=temp.formId\n" +
//                "LEFT JOIN WF_FlowInstance WF1 ON temp.formId = WF1.FORM_ID\n" +
//                "JOIN WF_NSP_Contact WNC ON WNC.Npsheadid=temp.formId AND WNC.HospitalComment!='预览版本----确认后合同号自动生成' and WNC.ContactStartDate!='YYYY-MM-DD'\n" +
//                "AND getdate()>=cast( ContactStartDate as datetime) AND getdate()<=cast( ContactEndDate as datetime)\n" +
//                "JOIN SL010700 SU ON WNFH.DealerID=SU.SL01001 COLLATE Chinese_PRC_BIN\n" +
//                "LEFT JOIN WF_NSP_Exprot WNE ON WNE.NPS_HEAD_ID=temp.formId\n" +
//                "WHERE left(WNC.HospitalComment,3)='NPS' and  DATEDIFF(mm,CONVERT(datetime, WNC.ContactStartDate,101), getdate() )>=0 AND DATEDIFF(mm,CONVERT(datetime, WNC.ContactStartDate,101), getdate() )<=60\n" +
//                "GROUP BY temp.formId";
//        List<Map<String, Object>> dataList = jdbcTemplate.queryForList(querySql);
//
//        StringBuilder errorStr = new StringBuilder();
//        for (int i = 0; i < dataList.size(); i++) {
//            Map<String, Object> map = dataList.get(i);
//            String id = map.get("id").toString();
//            System.out.println(new SimpleDateFormat("hh:mm:ss").format(new Date()));
//            //测试写入
//            Workbook  workbook = GrapeCityExcelUtil.getInstance().getWorkbook();
//
//            String filePathById = nps_webExcelUtils.getFilePathById( id);
//            System.out.println(filePathById);
//            workbook.open(filePathById);
//            Map<String,Object> dataObject = (Map<String,Object>)nps_webExcelUtils.setData2Excel(id);
//
//            Iterator<Map.Entry<String, Object>> iterator = dataObject.entrySet().iterator();
//            while (iterator.hasNext()){
//                Map.Entry<String, Object> next = iterator.next();
//                try {
//                    nps_webExcelUtils.bindingData(workbook,next.getKey(),(List)next.getValue());
//                } catch (Exception e1) {
//                    errorStr.append("错误的id:"+id+"\n");
//                    break;
//                }
//            }
//            String filpath = NPS_WebExcelUtils.buildFileWithId(id);
//            System.out.println(filpath);
//            try {
//                workbook.save(filpath);
//            } catch (Exception e1) {
//               continue;
//            }
//
//        }
//        System.out.println(errorStr);
    }




    public static String cellName(int row,int col) {

        ArrayList<Character> list = new ArrayList();
        while (col >0) {
            list.add((char) ((col-1) % 26 + 'A' ));
            col /= 26;
        }
        StringBuffer buffer = new StringBuffer();
        for (int i = list.size() - 1; i >= 0; i--)
            buffer.append(list.get(i));
        buffer.append("" + row);
        return buffer.toString();
    }



}
