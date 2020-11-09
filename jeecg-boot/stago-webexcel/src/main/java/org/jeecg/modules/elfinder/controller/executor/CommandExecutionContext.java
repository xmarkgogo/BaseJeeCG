package org.jeecg.modules.elfinder.controller.executor;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jeecg.modules.elfinder.service.FsServiceFactory;

public interface CommandExecutionContext
{
	FsServiceFactory getFsServiceFactory();

	HttpServletRequest getRequest();

	HttpServletResponse getResponse();

	ServletContext getServletContext();
}
