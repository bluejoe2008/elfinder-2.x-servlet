package cn.bluejoe.elfinder.controller.executors;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;

import cn.bluejoe.elfinder.controller.executor.AbstractCommandExecutor;
import cn.bluejoe.elfinder.controller.executor.CommandExecutor;
import cn.bluejoe.elfinder.controller.executor.FsItemEx;
import cn.bluejoe.elfinder.service.FsService;
import cn.bluejoe.elfinder.util.MimeTypesUtils;

public class FileCommandExecutor extends AbstractCommandExecutor implements CommandExecutor
{
	@Override
	public void execute(FsService fsService, HttpServletRequest request, HttpServletResponse response,
			ServletContext servletContext) throws Exception
	{
		String target = request.getParameter("target");
		boolean download = "1".equals(request.getParameter("download"));
		FsItemEx fsi = super.findItem(fsService, target);
		String mime = fsi.getMimeType();

		response.setCharacterEncoding("utf-8");
		response.setContentType(mime);
		//String fileUrl = getFileUrl(fileTarget);
		//String fileUrlRelative = getFileUrl(fileTarget);
		String fileName = fsi.getName();
		fileName = new String(fileName.getBytes(), "ISO8859-1");
		if (download || MimeTypesUtils.isUnknownType(mime))
		{
			response.setHeader("Content-Disposition", getMimeDisposition(mime) + "; filename=" + fileName);
			//response.setHeader("Content-Location", fileUrlRelative);
			response.setHeader("Content-Transfer-Encoding", "binary");
		}

		OutputStream out = response.getOutputStream();
		InputStream is = null;
		response.setContentLength((int) fsi.getSize());
		try
		{
			// serve file
			is = fsi.openInputStream();
			IOUtils.copy(is, out);
			out.flush();
			out.close();
		}
		finally
		{
			if (is != null)
			{
				try
				{
					is.close();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}
	}
}
