package org.jeecg.modules.elfinder.impl;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.jeecg.modules.elfinder.service.FsService;
import org.jeecg.modules.elfinder.service.FsServiceFactory;

/**
 * A StaticFsServiceFactory always returns one FsService, despite of whatever it
 * is requested
 *
 * @author bluejoe
 *
 */
public class StaticFsServiceFactory implements FsServiceFactory
{
	FsService _fsService;

	@Override
	public FsService getFileService(HttpServletRequest request,
			ServletContext servletContext)
	{
		return _fsService;
	}

	public FsService getFsService()
	{
		return _fsService;
	}

	public void setFsService(FsService fsService)
	{
		_fsService = fsService;
	}
}
