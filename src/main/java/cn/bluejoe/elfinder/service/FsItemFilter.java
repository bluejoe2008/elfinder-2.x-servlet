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
	//TODO: bad designs: FsItemEx should not used here
	//top level interfaces do not recognize FsItemEx which is a wrapper class
	public boolean accepts(FsItemEx item);
}
