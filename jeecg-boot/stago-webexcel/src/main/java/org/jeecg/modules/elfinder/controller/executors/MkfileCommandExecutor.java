package org.jeecg.modules.elfinder.controller.executors;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.json.JSONObject;

import org.jeecg.modules.elfinder.controller.executor.AbstractJsonCommandExecutor;
import org.jeecg.modules.elfinder.controller.executor.CommandExecutor;
import org.jeecg.modules.elfinder.controller.executor.FsItemEx;
import org.jeecg.modules.elfinder.service.FsItemFilter;
import org.jeecg.modules.elfinder.service.FsService;

public class MkfileCommandExecutor extends AbstractJsonCommandExecutor
		implements CommandExecutor
{
	@Override
	public void execute(FsService fsService, HttpServletRequest request,
			ServletContext servletContext, JSONObject json) throws Exception
	{
		String target = request.getParameter("target");
		String name = request.getParameter("name");

		FsItemEx fsi = super.findItem(fsService, target);
		FsItemEx dir = new FsItemEx(fsi, name);
		dir.createFile();

		// if the new file is allowed to be display?
		FsItemFilter filter = getRequestedFilter(request);
		json.put(
				"added",
				filter.accepts(dir) ? new Object[] { getFsItemInfo(request, dir) }
						: new Object[0]);
	}
}
