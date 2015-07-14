package cn.bluejoe.elfinder.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface FsVolume
{
	void createFile(FsItem fsi) throws IOException;

	void createFolder(FsItem fsi) throws IOException;

	void deleteFile(FsItem fsi) throws IOException;

	void deleteFolder(FsItem fsi) throws IOException;

	boolean exists(FsItem newFile);

	FsItem fromPath(String relativePath);

	String getDimensions(FsItem fsi);

	long getLastModified(FsItem fsi);

	String getMimeType(FsItem fsi);

	String getName();

	String getName(FsItem fsi);

	FsItem getParent(FsItem fsi);

	String getPath(FsItem fsi) throws IOException;

	FsItem getRoot();

	long getSize(FsItem fsi);

	String getThumbnailFileName(FsItem fsi);

	boolean hasChildFolder(FsItem fsi);

	boolean isFolder(FsItem fsi);

	boolean isRoot(FsItem fsi);

	FsItem[] listChildren(FsItem fsi);

	InputStream openInputStream(FsItem fsi) throws IOException;

	OutputStream openOutputStream(FsItem fsi) throws IOException;

	void rename(FsItem src, FsItem dst) throws IOException;
}
