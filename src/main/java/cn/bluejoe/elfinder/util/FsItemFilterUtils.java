package cn.bluejoe.elfinder.util;

import cn.bluejoe.elfinder.controller.executor.FsItemEx;
import cn.bluejoe.elfinder.service.FsItemFilter;

public abstract class FsItemFilterUtils
{
	public static FsItemFilter FILTER_ALL = new FsItemFilter()
	{
		@Override
		public boolean accepts(FsItemEx item)
		{
			return true;
		}
	};

	public static FsItemFilter FILTER_FOLDER = new FsItemFilter()
	{
		@Override
		public boolean accepts(FsItemEx item)
		{
			return item.isFolder();
		}
	};

	/**
	 * returns a FsItemFilter according to given mimeFilters
	 * 
	 * @param mimeFilters
	 * @return
	 */
	public static FsItemFilter createMimeFilter(final String[] mimeFilters)
	{
		if (mimeFilters == null || mimeFilters.length == 0)
			return FILTER_ALL;

		return new FsItemFilter()
		{
			@Override
			public boolean accepts(FsItemEx item)
			{
				String mimeType = item.getMimeType().toUpperCase();

				for (String mf : mimeFilters)
				{
					mf = mf.toUpperCase();
					if (mimeType.startsWith(mf + "/") || mimeType.equals(mf))
						return true;
				}
				return false;
			}
		};
	}

}
