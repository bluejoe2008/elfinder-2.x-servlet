package org.grapheco.elfinder.util;

import java.io.IOException;

import org.grapheco.elfinder.controller.executor.FsItemEx;
import org.grapheco.elfinder.service.FsItem;
import org.grapheco.elfinder.service.FsService;

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
