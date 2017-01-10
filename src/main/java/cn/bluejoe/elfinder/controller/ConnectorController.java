package cn.bluejoe.elfinder.controller;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import cn.bluejoe.elfinder.controller.executor.CommandExecutionContext;
import cn.bluejoe.elfinder.controller.executor.CommandExecutor;
import cn.bluejoe.elfinder.controller.executor.CommandExecutorFactory;
import cn.bluejoe.elfinder.service.FsServiceFactory;

@Controller
@RequestMapping("connector")
public class ConnectorController
{
	@Resource(name = "commandExecutorFactory")
	private CommandExecutorFactory _commandExecutorFactory;

	@Resource(name = "fsServiceFactory")
	private FsServiceFactory _fsServiceFactory;

	@RequestMapping
	public void connector(HttpServletRequest request,
			final HttpServletResponse response) throws IOException
	{
		try
		{
			request = parseMultipartContent(request);
		}
		catch (Exception e)
		{
			throw new IOException(e.getMessage());
		}

		String cmd = request.getParameter("cmd");
		CommandExecutor ce = _commandExecutorFactory.get(cmd);

		if (ce == null)
		{
			// This shouldn't happen as we should have a fallback command set.
			throw new FsException(String.format("unknown command: %s", cmd));
		}

		try
		{
			final HttpServletRequest finalRequest = request;
			ce.execute(new CommandExecutionContext()
			{

				@Override
				public FsServiceFactory getFsServiceFactory()
				{
					return _fsServiceFactory;
				}

				@Override
				public HttpServletRequest getRequest()
				{
					return finalRequest;
				}

				@Override
				public HttpServletResponse getResponse()
				{
					return response;
				}

				@Override
				public ServletContext getServletContext()
				{
					return finalRequest.getSession().getServletContext();
				}
			});
		}
		catch (Exception e)
		{
			throw new FsException("unknown error", e);
		}
	}

	public CommandExecutorFactory getCommandExecutorFactory()
	{
		return _commandExecutorFactory;
	}

	public FsServiceFactory getFsServiceFactory()
	{
		return _fsServiceFactory;
	}

	private HttpServletRequest parseMultipartContent(
			final HttpServletRequest request) throws Exception
	{
		if (!ServletFileUpload.isMultipartContent(request))
			return request;

		// non-file parameters
		final Map<String, String> requestParams = new HashMap<String, String>();

		// Parse the request
		ServletFileUpload sfu = new ServletFileUpload();
		String characterEncoding = request.getCharacterEncoding();
		if (characterEncoding == null)
		{
			characterEncoding = "UTF-8";
		}

		sfu.setHeaderEncoding(characterEncoding);
		FileItemIterator iter = sfu.getItemIterator(request);
		MultipleUploadItems uploads = new MultipleUploadItems();

		while (iter.hasNext())
		{
			FileItemStream item = iter.next();

			// not a file
			if (item.isFormField())
			{
				InputStream stream = item.openStream();
				requestParams.put(item.getFieldName(),
						Streams.asString(stream, characterEncoding));
				stream.close();
			}
			else
			{
				// it is a file!
				String fileName = item.getName();
				if (fileName != null && !"".equals(fileName.trim()))
				{
					uploads.addItemProxy(item);
				}
			}
		}

		uploads.writeInto(request);

		// 'getParameter()' method can not be called on original request object
		// after parsing
		// so we stored the request values and provide a delegate request object
		return (HttpServletRequest) Proxy.newProxyInstance(this.getClass()
				.getClassLoader(), new Class[] { HttpServletRequest.class },
				new InvocationHandler()
				{
					@Override
					public Object invoke(Object arg0, Method arg1, Object[] arg2)
							throws Throwable
					{
						// we replace getParameter() and getParameterValues()
						// methods
						if ("getParameter".equals(arg1.getName()))
						{
							String paramName = (String) arg2[0];
							return requestParams.get(paramName);
						}

						if ("getParameterValues".equals(arg1.getName()))
						{
							String paramName = (String) arg2[0];

							// normalize name 'key[]' to 'key'
							if (paramName.endsWith("[]"))
								paramName = paramName.substring(0,
										paramName.length() - 2);

							if (requestParams.containsKey(paramName))
								return new String[] { requestParams
										.get(paramName) };

							// if contains key[1], key[2]...
							int i = 0;
							List<String> paramValues = new ArrayList<String>();
							while (true)
							{
								String name2 = String.format("%s[%d]",
										paramName, i++);
								if (requestParams.containsKey(name2))
								{
									paramValues.add(requestParams.get(name2));
								}
								else
								{
									break;
								}
							}

							return paramValues.isEmpty() ? new String[0]
									: paramValues.toArray(new String[0]);
						}

						return arg1.invoke(request, arg2);
					}
				});
	}

	public void setCommandExecutorFactory(
			CommandExecutorFactory _commandExecutorFactory)
	{
		this._commandExecutorFactory = _commandExecutorFactory;
	}

	public void setFsServiceFactory(FsServiceFactory _fsServiceFactory)
	{
		this._fsServiceFactory = _fsServiceFactory;
	}
}