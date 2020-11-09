package org.jeecg.modules.elfinder.controller.executors;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.json.JSONObject;

import org.jeecg.modules.elfinder.controller.executor.AbstractJsonCommandExecutor;
import org.jeecg.modules.elfinder.controller.executor.CommandExecutor;
import org.jeecg.modules.elfinder.controller.executor.FsItemEx;
import org.jeecg.modules.elfinder.service.FsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MkdirCommandExecutor extends AbstractJsonCommandExecutor implements
		CommandExecutor
{
	protected static Logger logger = LoggerFactory.getLogger(MkdirCommandExecutor.class);
	@Override
	public void execute(FsService fsService, HttpServletRequest request,
			ServletContext servletContext, JSONObject json) throws Exception
	{

		request.setCharacterEncoding ("UTF-8");
        String target = request.getParameter("target");
		String name = request.getParameter("name");
	//	name = new String(name .getBytes("iso8859-1"),"utf-8");

		logger.debug("MARK----------------"+name);

		FsItemEx fsi = super.findItem(fsService, target);
		FsItemEx dir = new FsItemEx(fsi, name);
		dir.createFolder();

		json.put("added", new Object[] { getFsItemInfo(request, dir) });
	}
}
