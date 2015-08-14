package cn.bluejoe.elfinder.service;

import cn.bluejoe.elfinder.controller.executor.FsItemEx;


/**
 * A FsItemFilter tells if a FsItem is matched or not
 * 
 * @author bluejoe
 *
 */
public interface FsItemFilter
{
	public boolean accepts(FsItemEx item);
}
