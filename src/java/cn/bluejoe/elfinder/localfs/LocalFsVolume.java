package cn.bluejoe.elfinder.localfs;

import hidden.org.codehaus.plexus.util.FileUtils;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import cn.bluejoe.elfinder.service.FsItem;
import cn.bluejoe.elfinder.service.FsVolume;
import cn.bluejoe.elfinder.util.MimeTypesUtils;

public class LocalFsVolume implements FsVolume
{
	String _name;

	File _rootDir;

	private File asFile(FsItem fsi)
	{
		return ((LocalFsItem) fsi).getFile();
	}

	@Override
	public void createFile(FsItem fsi) throws IOException
	{
		asFile(fsi).createNewFile();
	}

	@Override
	public void createFolder(FsItem fsi) throws IOException
	{
		asFile(fsi).mkdirs();
	}

	@Override
	public void deleteFile(FsItem fsi) throws IOException
	{
		File file = asFile(fsi);
		if (!file.isDirectory())
		{
			file.delete();
		}
	}

	@Override
	public void deleteFolder(FsItem fsi) throws IOException
	{
		File file = asFile(fsi);
		if (file.isDirectory())
		{
			FileUtils.deleteDirectory(file);
		}
	}

	@Override
	public boolean exists(FsItem newFile)
	{
		return asFile(newFile).exists();
	}

	private LocalFsItem fromFile(File file)
	{
		return new LocalFsItem(this, file);
	}

	@Override
	public FsItem fromPath(String relativePath)
	{
		return fromFile(new File(_rootDir, relativePath));
	}

	@Override
	public String getDimensions(FsItem fsi)
	{
		return null;
	}

	@Override
	public long getLastModified(FsItem fsi)
	{
		return asFile(fsi).lastModified();
	}

	@Override
	public String getMimeType(FsItem fsi)
	{
		File file = asFile(fsi);
		if (file.isDirectory())
			return "directory";

		String ext = FileUtils.getExtension(file.getName());
		if (ext != null && !ext.isEmpty())
		{
			String mimeType = MimeTypesUtils.getMimeType(ext);
			return mimeType == null ? MimeTypesUtils.UNKNOWN_MIME_TYPE : mimeType;
		}

		return MimeTypesUtils.UNKNOWN_MIME_TYPE;
	}

	public String getName()
	{
		return _name;
	}

	@Override
	public String getName(FsItem fsi)
	{
		return asFile(fsi).getName();
	}

	@Override
	public FsItem getParent(FsItem fsi)
	{
		return fromFile(asFile(fsi).getParentFile());
	}

	@Override
	public String getPath(FsItem fsi) throws IOException
	{
		String fullPath = asFile(fsi).getCanonicalPath();
		String rootPath = _rootDir.getCanonicalPath();
		String relativePath = fullPath.substring(rootPath.length());
		return relativePath.replace('\\', '/');
	}

	@Override
	public FsItem getRoot()
	{
		return fromFile(_rootDir);
	}

	public File getRootDir()
	{
		return _rootDir;
	}

	@Override
	public long getSize(FsItem fsi)
	{
		return asFile(fsi).length();
	}

	@Override
	public String getThumbnailFileName(FsItem fsi)
	{
		return null;
	}

	@Override
	public boolean hasChildFolder(FsItem fsi)
	{
		return asFile(fsi).isDirectory() && asFile(fsi).listFiles(new FileFilter()
		{

			@Override
			public boolean accept(File arg0)
			{
				return arg0.isDirectory();
			}
		}).length > 0;
	}

	@Override
	public boolean isFolder(FsItem fsi)
	{
		return asFile(fsi).isDirectory();
	}

	@Override
	public boolean isRoot(FsItem fsi)
	{
		return _rootDir == asFile(fsi);
	}

	@Override
	public FsItem[] listChildren(FsItem fsi)
	{
		List<FsItem> list = new ArrayList<FsItem>();
		File[] cs = asFile(fsi).listFiles();
		if (cs == null)
		{
			return new FsItem[0];
		}

		for (File c : cs)
		{
			list.add(fromFile(c));
		}

		return list.toArray(new FsItem[0]);
	}

	@Override
	public InputStream openInputStream(FsItem fsi) throws IOException
	{
		return new FileInputStream(asFile(fsi));
	}

	@Override
	public OutputStream openOutputStream(FsItem fsi) throws IOException
	{
		return new FileOutputStream(asFile(fsi));
	}

	@Override
	public void rename(FsItem src, FsItem dst) throws IOException
	{
		asFile(src).renameTo(asFile(dst));
	}

	public void setName(String name)
	{
		_name = name;
	}

	public void setRootDir(File rootDir)
	{
		if (!rootDir.exists())
		{
			rootDir.mkdirs();
		}

		_rootDir = rootDir;
	}
}
