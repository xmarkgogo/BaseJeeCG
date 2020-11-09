package org.jeecg.modules.elfinder.controller.executors;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.jeecg.modules.elfinder.controller.executor.CommandExecutor;
import org.json.JSONObject;

import org.jeecg.modules.elfinder.controller.executor.AbstractJsonCommandExecutor;
import org.jeecg.modules.elfinder.controller.executor.FsItemEx;
import org.jeecg.modules.elfinder.service.FsService;

public class RmCommandExecutor extends AbstractJsonCommandExecutor implements
		CommandExecutor
{
	@Override
	public void execute(FsService fsService, HttpServletRequest request,
			ServletContext servletContext, JSONObject json) throws Exception
	{
		String[] targets = request.getParameterValues("targets[]");
		String current = request.getParameter("current");
		List<String> removed = new ArrayList<String>();

		for (String target : targets)
		{
			FsItemEx ftgt = super.findItem(fsService, target);
			ftgt.delete();
			removed.add(ftgt.getHash());
		}

		json.put("removed", removed.toArray());
	}
}
