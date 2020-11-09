package org.jeecg.modules.elfinder.controller.executors;
import org.apache.commons.lang.StringUtils;
import org.jeecg.common.util.SpringContextUtils;
import org.jeecg.modules.elfinder.controller.executor.AbstractJsonCommandExecutor;
import org.jeecg.modules.elfinder.controller.executor.CommandExecutor;
import org.jeecg.modules.elfinder.localfs.LocalFsItem;
import org.jeecg.modules.elfinder.service.FsItem;
import org.jeecg.modules.elfinder.service.FsService;
import org.jeecg.modules.excelConfig.entity.*;
import org.jeecg.modules.excelConfig.util.ConfigReadFactory;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

public class WebExcelParametersCommandExecutor extends AbstractJsonCommandExecutor implements
		CommandExecutor
{
	private Logger logger = LoggerFactory.getLogger(WebExcelParametersCommandExecutor.class);


	@Override
	public void execute(FsService fsService, HttpServletRequest request,
						ServletContext servletContext, JSONObject json) throws Exception
	{

		    JdbcTemplate jdbcTemplate = SpringContextUtils.getBean(JdbcTemplate.class);
			String target = request.getParameter("target");
			//FsItemEx FSIE = super.findItem(fsService, target);
			FsItem FSI = fsService.fromHash(target);
			String FilePath= ((LocalFsItem) FSI).getFile().getAbsolutePath();
			json.put("OK", "OK");
		    json.put("Parameters", "{}");

		   //如果是ISQD,则说明是一个报表文件，读取配置的根节点的参数
			if(FilePath.lastIndexOf(".isqd")>=0){
			ExcelConfigBean excelConfigBean = ConfigReadFactory.getExcelConfigBeanElement(FilePath);
			excelConfigBean.setJdbcTemplate(jdbcTemplate);

			List<ParameterBean> PTS =new ArrayList<ParameterBean>()	;
			ParametersBean parametersBean_total = excelConfigBean.getParametersBean();
			List<ParameterBean> parameterBeans = parametersBean_total.getParameterBeans();
			for(ParameterBean pbn:parameterBeans){
				logger.debug(pbn.getName()+'-'+pbn.getValue());
				if (pbn.isShow()){
					try {
						String htmlType = pbn.getHtmlType();
						if (StringUtils.isEmpty(htmlType)||htmlType.toLowerCase().equals("input")){

						}else if ("select".equals(htmlType.toLowerCase())){
							String modelId = pbn.getModelId();
							ModelBean dataModel = excelConfigBean.getModelById(modelId);

							StringBuilder sqlStr = dataModel.getSqlStrsBean().getSqlStr();
							SqlWrapBean sqlWrapBean = new SqlWrapBean();
							sqlWrapBean.setParameTersBean(dataModel.getParametersBean());
							sqlWrapBean.setSqlTemplate(sqlStr);
							String sql = sqlWrapBean.getFinalSQL();
							List dataForList = excelConfigBean.getDataForList(sql);
							pbn.setData_List(dataForList);
						}
					} catch (Exception e) {
						 json.put("error","Cause->"+e.getMessage());
						 e.printStackTrace();
					}
					PTS.add(pbn);
				}
				logger.debug(pbn.getName()+'-'+pbn.getValue());
			}
			json.put("Parameters", PTS);
	}
	}
}
