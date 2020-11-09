package org.jeecg.modules.elfinder.util;

import java.io.IOException;

import org.jeecg.modules.elfinder.service.FsItem;
import org.jeecg.modules.elfinder.controller.executor.FsItemEx;
import org.jeecg.modules.elfinder.service.FsService;

public abstract class FsServiceUtils
{
	public static FsItemEx findItem(FsService fsService, String hash)
			throws IOException
	{
		FsItem fsi = fsService.fromHash(hash);


		if (fsi == null)
		{
			return null;
		}

		return new FsItemEx(fsi, fsService);
	}
}
