package org.jeecg.modules.elfinder.controller.executors;

import java.io.ByteArrayInputStream;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.json.JSONObject;

import org.jeecg.modules.elfinder.controller.executor.AbstractJsonCommandExecutor;
import org.jeecg.modules.elfinder.controller.executor.CommandExecutor;
import org.jeecg.modules.elfinder.controller.executor.FsItemEx;
import org.jeecg.modules.elfinder.service.FsService;

public class PutCommandExecutor extends AbstractJsonCommandExecutor implements
		CommandExecutor
{
	@Override
	public void execute(FsService fsService, HttpServletRequest request,
			ServletContext servletContext, JSONObject json) throws Exception
	{
		String target = request.getParameter("target");

		FsItemEx fsi = super.findItem(fsService, target);
		fsi.writeStream(new ByteArrayInputStream(request
				.getParameter("content").getBytes("utf-8")));
		json.put("changed", new Object[] { super.getFsItemInfo(request, fsi) });
	}
}
