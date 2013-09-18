package cn.bluejoe.elfinder.controller.executor;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;

import cn.bluejoe.elfinder.service.FsService;
import cn.bluejoe.elfinder.util.FsServiceUtils;

public abstract class AbstractCommandExecutor implements CommandExecutor
{
	protected void addChildren(Map<String, FsItemEx> map, FsItemEx fsi) throws IOException
	{
		for (FsItemEx f : fsi.listChildren())
		{
			map.put(f.getHash(), f);
		}
	}

	protected void addSubfolders(Map<String, FsItemEx> map, FsItemEx fsi) throws IOException
	{
		for (FsItemEx f : fsi.listChildren())
		{
			if (f.isFolder())
			{
				map.put(f.getHash(), f);
				addSubfolders(map, f);
			}
		}
	}

	protected void createAndCopy(FsItemEx src, FsItemEx dst) throws IOException
	{
		if (src.isFolder())
		{
			createAndCopyFolder(src, dst);
		}
		else
		{
			createAndCopyFile(src, dst);
		}
	}

	protected void createAndCopyFile(FsItemEx src, FsItemEx dst) throws IOException
	{
		dst.createFile();
		InputStream is = src.openInputStream();
		OutputStream os = dst.openOutputStream();
		IOUtils.copy(is, os);
		is.close();
		os.close();
	}

	protected void createAndCopyFolder(FsItemEx src, FsItemEx dst) throws IOException
	{
		dst.createFolder();

		for (FsItemEx c : src.listChildren())
		{
			if (c.isFolder())
			{
				createAndCopyFolder(c, new FsItemEx(dst, c.getName()));
			}
			else
			{
				createAndCopyFile(c, new FsItemEx(dst, c.getName()));
			}
		}
	}

	@Override
	public void execute(CommandExecutionContext ctx) throws Exception
	{
		FsService fileService = ctx.getFsServiceFactory().getFileService(ctx.getRequest(), ctx.getServletContext());
		execute(fileService, ctx.getRequest(), ctx.getResponse(), ctx.getServletContext());
	}

	public abstract void execute(FsService fsService, HttpServletRequest request, HttpServletResponse response,
			ServletContext servletContext) throws Exception;

	protected Object[] files2JsonArray(HttpServletRequest request, Collection<FsItemEx> list) throws IOException
	{
		List<Map<String, Object>> los = new ArrayList<Map<String, Object>>();
		for (FsItemEx fi : list)
		{
			los.add(getFsItemInfo(request, fi));
		}

		return los.toArray();
	}

	protected FsItemEx findCwd(FsService fsService, String target) throws IOException
	{
		//current selected directory
		FsItemEx cwd = null;
		if (target != null)
		{
			cwd = findItem(fsService, target);
		}

		if (cwd == null)
			cwd = new FsItemEx(fsService.getVolumes()[0].getRoot(), fsService);

		return cwd;
	}

	protected FsItemEx findItem(FsService fsService, String hash) throws IOException
	{
		return FsServiceUtils.findItem(fsService, hash);
	}

	protected Map<String, Object> getFsItemInfo(HttpServletRequest request, FsItemEx fsi) throws IOException
	{
		Map<String, Object> info = new HashMap<String, Object>();
		info.put("hash", fsi.getHash());
		info.put("mime", fsi.getMimeType());
		info.put("ts", fsi.getLastModified());
		info.put("size", fsi.getSize());
		info.put("read", fsi.isReadable(fsi) ? 1 : 0);
		info.put("write", fsi.isWritable(fsi) ? 1 : 0);
		info.put("locked", fsi.isLocked(fsi) ? 1 : 0);

		if (fsi.getMimeType().startsWith("image"))
		{
			StringBuffer qs = request.getRequestURL();
			info.put("tmb", qs.append(String.format("?cmd=tmb&target=%s", fsi.getHash())));
		}

		if (fsi.isRoot())
		{
			info.put("name", fsi.getVolumnName());
			info.put("volumeid", fsi.getVolumeId());
		}
		else
		{
			info.put("name", fsi.getName());
			info.put("phash", fsi.getParent().getHash());
		}

		if (fsi.isFolder())
		{
			info.put("dirs", fsi.hasChildFolder() ? 1 : 0);
		}

		return info;
	}

	protected String getMimeDisposition(String mime)
	{
		String[] parts = mime.split("/");
		String disp = ("image".equals(parts[0]) || "text".equals(parts[0]) ? "inline" : "attachments");
		return disp;
	}

	protected Map<String, Object> getOptions(HttpServletRequest request, FsItemEx cwd)
	{
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("path", cwd.getName());
		map.put("disabled", new String[0]);
		map.put("separator", "/");
		map.put("copyOverwrite", 1);
		map.put("archivers", new Object[0]);

		return map;
	}
}