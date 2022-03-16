package org.grapheco.elfinder.controller.executors;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.json.JSONObject;

import org.grapheco.elfinder.controller.executor.AbstractJsonCommandExecutor;
import org.grapheco.elfinder.controller.executor.CommandExecutor;
import org.grapheco.elfinder.controller.executor.FsItemEx;
import org.grapheco.elfinder.service.FsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This returns the dimensions on an image.
 */
public class DimCommandExecutor extends AbstractJsonCommandExecutor implements
		CommandExecutor
{
	private static final Logger LOGGER = LoggerFactory.getLogger(DimCommandExecutor.class);

	@Override
	protected void execute(FsService fsService, HttpServletRequest request,
			ServletContext servletContext, JSONObject json) throws Exception
	{
		String target = request.getParameter("target");
		FsItemEx item = findItem(fsService, target);
		// If it's not an image then just return empty JSON.
		if (item.getMimeType().startsWith("image"))
		{
			InputStream inputStream = null;
			try
			{
				inputStream = item.openInputStream();
				BufferedImage image = ImageIO.read(inputStream);
				int width = image.getWidth();
				int height = image.getHeight();
				json.put("dim", String.format("%dx%d", width, height));
			}
			catch (IOException ioe)
			{
				String message = "Failed load image to get dimensions: "
						+ item.getPath();
				if (LOGGER.isDebugEnabled())
				{
					LOGGER.debug(message, ioe);
				}
				else
				{
					LOGGER.warn(message);
				}

			}
			finally
			{
				if (inputStream != null)
				{
					try
					{
						inputStream.close();
					}
					catch (IOException ioe)
					{
						LOGGER.debug(
								"Failed to close stream to: " + item.getPath(),
								ioe);
					}
				}
			}

		}
		else
		{
			LOGGER.debug("dim command called on non-image: " + item.getPath());
		}
	}
}
