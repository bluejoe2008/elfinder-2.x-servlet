package cn.bluejoe.elfinder.service;

import java.io.IOException;

import cn.bluejoe.elfinder.controller.executor.FsItemEx;

public interface FsService
{
	FsItem fromHash(String hash) throws IOException;

	String getHash(FsItem item) throws IOException;

	FsSecurityChecker getSecurityChecker();

	String getVolumeId(FsVolume volume);

	FsVolume[] getVolumes();

	FsServiceConfig getServiceConfig();

	/**
	 * find files by name pattern, this is often implemented upon a metadata
	 * store, or lucene-like search engines
	 * 
	 * @param filter
	 * @return
	 */

	// TODO: bad designs: FsItemEx should not used here
	//top level interfaces should only know FsItem instead of FsItemEx
	FsItemEx[] find(FsItemFilter filter);
}