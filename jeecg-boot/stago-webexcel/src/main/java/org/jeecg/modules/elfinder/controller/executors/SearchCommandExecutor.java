package org.jeecg.modules.elfinder.controller.executors;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.jeecg.modules.elfinder.controller.executor.CommandExecutor;
import org.jeecg.modules.elfinder.util.FsItemFilterUtils;
import org.json.JSONObject;

import org.jeecg.modules.elfinder.controller.executor.AbstractJsonCommandExecutor;
import org.jeecg.modules.elfinder.service.FsService;

public class SearchCommandExecutor extends AbstractJsonCommandExecutor
		implements CommandExecutor
{
	@Override
	public void execute(FsService fsService, HttpServletRequest request,
			ServletContext servletContext, JSONObject json) throws Exception
	{
		json.put(
				"files",
				files2JsonArray(request, FsItemFilterUtils.filterFiles(
						fsService.find(FsItemFilterUtils
								.createFileNameKeywordFilter(request
										.getParameter("q"))), super
								.getRequestedFilter(request))));
	}
}
