package org.jeecg.modules.elfinder.impl;

import org.jeecg.modules.elfinder.service.FsServiceConfig;

public class DefaultFsServiceConfig implements FsServiceConfig
{
	private int _tmbWidth;

	public void setTmbWidth(int tmbWidth)
	{
		_tmbWidth = tmbWidth;
	}

	@Override
	public int getTmbWidth()
	{
		return _tmbWidth;
	}
}
