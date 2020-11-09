package org.jeecg.modules.elfinder.controller.executors;

import com.alibaba.fastjson.JSON;
import com.grapecity.documents.excel.Workbook;

import org.jeecg.modules.elfinder.controller.executor.AbstractJsonCommandExecutor;
import org.jeecg.modules.elfinder.controller.executor.CommandExecutor;
import org.jeecg.modules.elfinder.localfs.LocalFsItem;
import org.jeecg.modules.elfinder.service.FsItem;
import org.jeecg.modules.elfinder.service.FsService;
import org.jeecg.modules.excelConfig.entity.ExcelConfigBean;
import org.jeecg.modules.excelConfig.entity.ParameterBean;
import org.jeecg.modules.excelConfig.entity.ParametersBean;
import org.jeecg.modules.excelConfig.util.ConfigReadFactory;
import org.jeecg.modules.excelConfig.util.GrapeCityExcelUtil;
import org.jeecg.modules.excelConfig.util.ReadBean2SQL;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.*;

public class WebExcelSaveCommandExecutor extends AbstractJsonCommandExecutor implements
		CommandExecutor
{

	protected static Logger logger = LoggerFactory.getLogger(WebExcelSaveCommandExecutor.class);
	@Override
	@ResponseBody
	public void execute(FsService fsService, HttpServletRequest request,
						ServletContext servletContext, JSONObject json) throws Exception
	{
		long l = System.currentTimeMillis();
		String target = request.getParameter("target");
		String jsr = request.getParameter("JsonStr");
		String formJsonStr = request.getParameter("formJsonStr");
		//FsItemEx FSIE = super.findItem(fsService, target);
		FsItem FSI = fsService.fromHash(target);
		String FilePath= ((LocalFsItem) FSI).getFile().getAbsolutePath();
		/**
		 * 获取了前段传来的Json数据
		 * a ，转存为Excel
		 * b，调用Excel配置类进行读取
		 * c，将保存结果返回
		 * c，
		 */
        //todo

		Workbook workbook = GrapeCityExcelUtil.getInstance().getWorkbook();
		//读取文件 ，如果是xml格式，则调用后台进行处理


		 if(FilePath.indexOf(".isqd")>=0){

             String tempFileName = UUID.randomUUID().toString();
             String tempFilePath = "d:\\"+tempFileName+".xlsx";
			 workbook.fromJson(jsr);
             workbook.save(tempFilePath);
			 logger.debug("解析-保存文件.isqd:"+tempFilePath);
			 ExcelConfigBean excelConfigBean = ConfigReadFactory.getExcelConfigBeanElement(FilePath);

			 //将前端所有参数重新赋值
			 ParametersBean parametersBean=excelConfigBean.getParametersBean();
			 List<ParameterBean> parameterBeans = parametersBean.getParameterBeans();
			 Map<String,Object> mapTypes = JSON.parseObject(formJsonStr);
			 Iterator<Map.Entry<String, Object>> iterator = mapTypes.entrySet().iterator();
			 while (iterator.hasNext()){
				 Map.Entry<String, Object> next = iterator.next();
				 String key = next.getKey();
				 for (int i = 0; i < parameterBeans.size(); i++) {
					 String name = parameterBeans.get(i).getName();
					 if (key.equals(name)){
						 String value = request.getParameter(key);
						 excelConfigBean.setParameterByName(name, value);
					 }
				 }
			 }
			 excelConfigBean.setParameterByName("template_FilePath", tempFilePath);
             excelConfigBean.setParametersBean(parametersBean);
			 ReadBean2SQL readBean2SQL=new ReadBean2SQL();
			 //检查 配置文件是否存在save方法
			 boolean haveSaveMethod = excelConfigBean.isHaveSaveMethod();
			 if (haveSaveMethod){
				 try {
					 readBean2SQL.init(excelConfigBean);
					 readBean2SQL.runSql();
					 json.put("content", "文件保存成功,并写入到数据库.");
					 json.put("OK", "OK");
				 }catch (Exception e){
					 json.put("content", e.getMessage());
					 json.put("OK", "ERROR");
				 }

			 }else {
				 json.put("content", "文件为只读文件,不能保存到服务器.");
				 json.put("OK", "OK");
			 }
		 }

		//如果是普通Excel，则进行JSON转换，保存到本地,然后返回成功
		 if(FilePath.indexOf(".xlsx")>=0 | FilePath.indexOf(".JSON")  >=0){
			 workbook.fromJson(jsr);
			 workbook.save(FilePath);

		     String  SaveJsonFileName=FilePath.replaceAll(".xlsx",".JSON");
			 File file = new File(SaveJsonFileName);
			 if (file.exists()) { // 如果已存在,删除旧文件
				 file.delete();
			 }
			 file.createNewFile();
			 Writer write = new OutputStreamWriter(new FileOutputStream(file), "UTF-8");
			 write.write(jsr);
			 write.flush();
			 write.close();

			logger.debug("解析-保存文件.xlsx:"+FilePath);
			json.put("content", "文件保存成功");
			json.put("OK", "OK");
		}

		System.out.println("耗时："+(l - System.currentTimeMillis()) );
	}


}
