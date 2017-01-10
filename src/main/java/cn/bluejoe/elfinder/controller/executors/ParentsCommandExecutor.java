package cn.bluejoe.elfinder.controller.executors;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.json.JSONObject;

import cn.bluejoe.elfinder.controller.ErrorException;
import cn.bluejoe.elfinder.controller.executor.AbstractJsonCommandExecutor;
import cn.bluejoe.elfinder.controller.executor.CommandExecutor;
import cn.bluejoe.elfinder.controller.executor.FsItemEx;
import cn.bluejoe.elfinder.service.FsService;

public class ParentsCommandExecutor extends AbstractJsonCommandExecutor
		implements CommandExecutor
{
	// This is a limit on the number of parents so that a badly implemented
	// FsService can't
	// result in a runaway thread.
	final static int LIMIT = 1024;

	@Override
	public void execute(FsService fsService, HttpServletRequest request,
			ServletContext servletContext, JSONObject json) throws Exception
	{
		String target = request.getParameter("target");

		Map<String, FsItemEx> files = new HashMap<String, FsItemEx>();
		FsItemEx fsi = findItem(fsService, target);
		for (int i = 0; !fsi.isRoot(); i++)
		{
			super.addSubfolders(files, fsi);
			fsi = fsi.getParent();
			if (i > LIMIT)
			{
				throw new ErrorException(
						"Reached recursion limit on parents of: " + LIMIT);
			}
		}

		json.put("tree", files2JsonArray(request, files.values()));
	}
}
