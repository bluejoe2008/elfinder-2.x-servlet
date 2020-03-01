package org.grapheco.elfinder.controller.executors;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.grapheco.elfinder.controller.ErrorException;
import org.grapheco.elfinder.controller.executor.CommandExecutor;
import org.json.JSONObject;

import org.grapheco.elfinder.controller.executor.AbstractJsonCommandExecutor;
import org.grapheco.elfinder.service.FsService;

/**
 * This is a command that should be executed when a matching command can't be
 * found.
 */
public class MissingCommandExecutor extends AbstractJsonCommandExecutor
		implements CommandExecutor
{
	@Override
	protected void execute(FsService fsService, HttpServletRequest request,
			ServletContext servletContext, JSONObject json) throws Exception
	{
		String cmd = request.getParameter("cmd");
		throw new ErrorException("errUnknownCmd", cmd);
	}
}
