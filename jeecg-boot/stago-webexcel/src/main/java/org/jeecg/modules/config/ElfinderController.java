package org.jeecg.modules.config;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.excelConfig.entity.ExcelConfigBean;
import org.jeecg.modules.excelConfig.util.ConfigReadFactory;
import org.jeecg.modules.excelConfig.util.ReadBean2Excel;
import org.jeecg.modules.excelConfig.util.ReadBean2SQL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/elfinder")
@Slf4j
public class ElfinderController {
    @Autowired
    JdbcTemplate jdbcTemplate;
    @RequestMapping("/html")
    public ModelAndView ftl(ModelAndView modelAndView) {
        modelAndView.setViewName("/elfinder/index");
        List<String> userList = new ArrayList<String>();
        userList.add("admin");
        userList.add("user1");
        userList.add("user2");
        log.info("--------------test--------------");
        modelAndView.addObject("userList", userList);
        return modelAndView;
    }


    @RequestMapping("/test")
    public Result test(ModelAndView modelAndView) throws Exception {

        log.info("--------------query--------------");
        ExcelConfigBean excelConfigBean = ConfigReadFactory.getExcelConfigBeanElement("D:\\A_CODE_WORK\\jeecg-boot\\jeecg-boot\\stago-webexcel\\src\\main\\java\\org\\jeecg\\modules\\excelConfig\\test\\xml\\template\\00ExceConfigExample.xml");

        excelConfigBean.setJdbcTemplate(jdbcTemplate);
   /*     ReadBean2Excel readBean2Excel=new ReadBean2Excel();

        excelConfigBean.setParameterByName("outputFilePath", "d:\\aaa.xlsx");
        readBean2Excel.init(excelConfigBean);*/
        log.info("--------------save--------------");
        ReadBean2SQL readBean2SQL=new ReadBean2SQL();
        readBean2SQL.init(excelConfigBean);
        readBean2SQL.runSql();


        Result vo=new Result();
        return vo;
    }


}
