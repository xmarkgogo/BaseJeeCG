package org.jeecg.modules.elfinder.controller.executors;
import org.jeecg.common.util.SpringContextUtils;
import org.jeecg.modules.elfinder.controller.executor.AbstractJsonCommandExecutor;
import org.jeecg.modules.elfinder.controller.executor.CommandExecutor;
import org.jeecg.modules.elfinder.localfs.LocalFsItem;
import org.jeecg.modules.elfinder.service.FsItem;
import org.jeecg.modules.elfinder.service.FsService;
import org.jeecg.modules.excelConfig.entity.*;
import org.jeecg.modules.excelConfig.util.ConfigReadFactory;
import org.jeecg.modules.excelConfig.util.NodeBean;
import org.jeecg.modules.excelConfig.util.ReadBean2Excel;
import org.jeecg.modules.excelConfig.util.TreeBuilder;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.util.*;


public class WebExcelCommandExecutor extends AbstractJsonCommandExecutor implements  CommandExecutor
{

	private Logger logger = LoggerFactory.getLogger(WebExcelCommandExecutor.class);
	@Override
	public void execute(FsService fsService, HttpServletRequest request,
						ServletContext servletContext, JSONObject json) throws Exception {
		/**
		 * 返回前端JSON
		 * 含有：{filetype:[xlsx/json];filename:xxx.xlsx;}
		 *
		 */

		long l=System.currentTimeMillis();
		String target = request.getParameter("target");
		//FsItemEx FSIE = super.findItem(fsService, target);
		FsItem FSI = fsService.fromHash(target);
		String FilePath = ((LocalFsItem) FSI).getFile().getAbsolutePath();


		//如果是ISQD结尾且读取正常，先输出Excel，然后转为JSON返回
		if (FilePath.lastIndexOf(".isqd") >= 0) {
			getJsonResult(FilePath,request,json);
		}

		if (FilePath.lastIndexOf(".xlsx") >= 0) {
			//Workbook workbook = GrapeCityExcelUtil.getInstance().getWorkbook();
			//workbook.open(FilePath);
			json.put("fileType" ,"xlsx");
			json.put("fileName",Base64.getEncoder().encodeToString(FilePath.getBytes("UTF-8")));
			//json = workbook.toJson();
		}

	   if (FilePath.lastIndexOf(".JSON") >= 0) {
		   json.put("fileType" ,"json");
		   File file = new File(FilePath);
		   BufferedReader reader = null;
		   StringBuffer sbf = new StringBuffer();
		   try {
			   reader = new BufferedReader(new FileReader(file));
			   String tempStr;
			   while ((tempStr = reader.readLine()) != null) {
				   sbf.append(tempStr);
			   }
			   reader.close();
			 //  json = sbf.toString();
			  json.put("content",sbf.toString());
		   } catch (IOException e) {
			   e.printStackTrace();
		   } finally {
			   if (reader != null) {
				   try {
					   reader.close();
				   } catch (IOException e1) {
					   e1.printStackTrace();
				   }
			   }
		   }
	   }

		System.out.println("耗时："+( System.currentTimeMillis()-l )+"毫秒" );
	}


		public void getJsonResult(String FilePath ,HttpServletRequest request,JSONObject  json) throws Exception {
			JdbcTemplate jdbcTemplate = SpringContextUtils.getBean(JdbcTemplate.class);
			//存放到配置文件中指定的目录，这个是临时文件，7天自动删除
			String outputFileName = UUID.randomUUID().toString();
			String elfinder = "d:";
			String tempFilePath = elfinder + "\\" + outputFileName + ".xlsx";
			ExcelConfigBean excelConfigBean = ConfigReadFactory.getExcelConfigBeanElement(FilePath);
			//从当前报表为目标,找到模板文件,模板文件需为相对路径
			excelConfigBean.setJdbcTemplate(jdbcTemplate);
			ParameterBean templateParameterBean = excelConfigBean.getParameterByName("template_FilePath");
			if (templateParameterBean != null) {
				String xmltemplate_FilePath = templateParameterBean.getValue().trim();
				if (!"".equals(xmltemplate_FilePath)) {
					String temletPath = FilePath.substring(0, FilePath.lastIndexOf("\\"));
					String FinaltemletPath = temletPath + "\\" + xmltemplate_FilePath;
					File file = new File(FinaltemletPath);
					if (!file.exists()) {
						// 文件不存在
						throw new RuntimeException("--ERROR PATH" + temletPath + "\\" + xmltemplate_FilePath);
					} else {
						//更换为实际绝对路径
						excelConfigBean.setParameterByName("template_FilePath", FinaltemletPath);
					}
				}
			}

			excelConfigBean.setParametersBean(excelConfigBean.setParameterByName("outputFilePath", tempFilePath));

			//遍历根参数，比较参数名称，如果一致的话，将参数值赋值到Bean中，让参数生效。 只负责全局参数更新。
			Map<String, String[]> parameterMap = request.getParameterMap();
			ParametersBean parametersBean = excelConfigBean.getParametersBean();
			logger.debug("遍历参数");
			Iterator<Map.Entry<String, String[]>> iterator = parameterMap.entrySet().iterator();
			while (iterator.hasNext()) {
				Map.Entry<String, String[]> next = iterator.next();
				String key = next.getKey();
				String[] value = next.getValue();
				logger.debug(key + "--------" + value[0]);
				if (parametersBean != null) {
					List<ParameterBean> parameterBeans = parametersBean.getParameterBeans();
					for (int j = 0; j < parameterBeans.size(); j++) {
						ParameterBean parameterBean = parameterBeans.get(j);
						String name = parameterBean.getName();
						if (key.equalsIgnoreCase(name)) {
							parameterBean.setValue(value[0]);
							parameterBeans.set(j, parameterBean);
						}
					}

				}
			}

			excelConfigBean.setParametersBean(parametersBean);
			ReadBean2Excel readBean2Excel = new ReadBean2Excel();
			readBean2Excel.init(excelConfigBean);//初始化修改过的xml
			FilePath = excelConfigBean.getParameterByName("outputFilePath").getValue();
			/*Workbook workbook = GrapeCityExcelUtil.getInstance().getWorkbook();
			workbook.open(FilePath);
			String ReturnJson = workbook.toJson();*/
			json.put("fileName",Base64.getEncoder().encodeToString(FilePath.getBytes("UTF-8")));
			json.put("fileType" ,"xlsx");
			//获取插件信息===================JS 前台插件
			WebExcelViewBean webExcelViewBean = excelConfigBean.getWebExcelViewBean();
			if(webExcelViewBean!=null){
				List<PlusBean> plusBeanS = webExcelViewBean.getPlusBean();
				if(plusBeanS!=null){
					for(int i=0;i<plusBeanS.size();i++){
						PlusBean plusBean=plusBeanS.get(i);
						if(plusBean.getModelid()!=null & !"".equalsIgnoreCase(plusBean.getModelid())){
							ModelBean dataModel = excelConfigBean.getModelById(plusBean.getModelid());
							StringBuilder sqlStr = dataModel.getSqlStrsBean().getSqlStr();
							SqlWrapBean sqlWrapBean = new SqlWrapBean();
							sqlWrapBean.setParameTersBean(dataModel.getParametersBean());
							sqlWrapBean.setSqlTemplate(sqlStr);
							String sql = sqlWrapBean.getFinalSQL();
							List dataForList = excelConfigBean.getDataForList(sql);
							plusBean.setModelData(dataForList);
							if("GroupList".equalsIgnoreCase(plusBean.getCellType())){
								List<NodeBean>nodes = new ArrayList();
								//必须要指定元素  ID PID  Text Value
								for (int j = 0; j < dataForList.size(); j++) {
									Map m= (Map) dataForList.get(j);
									String id   =String.valueOf(m.get("id"));
									String pid  =String.valueOf(m.get("pid"));
									String text =String.valueOf(m.get("text"));
									String value=String.valueOf(m.get("value" ));
									NodeBean p1 = new NodeBean(id, pid,text,value);
									nodes.add(p1);
								}
								TreeBuilder treeBuilder = new TreeBuilder(nodes);
								List<NodeBean> nodeTree= treeBuilder.buildTree();
								plusBean.setModelData(nodeTree);
							}

						}
						plusBeanS.set(i,plusBean);
					}
				}
				json.put("plus", plusBeanS);
			}
		}


	public static void main(String[] args) throws Exception {
		final Base64.Decoder decoder = Base64.getDecoder();
		final Base64.Encoder encoder = Base64.getEncoder();
		final String text = "c:aasdasdfss字串文字";
		final byte[] textByte = text.getBytes("UTF-8");
//编码
		final String encodedText = encoder.encodeToString(textByte);
		System.out.println(encodedText);
//解码
		System.out.println(new String(decoder.decode(encodedText), "UTF-8"));
	}
}
