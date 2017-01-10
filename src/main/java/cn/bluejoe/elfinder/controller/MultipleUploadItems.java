package cn.bluejoe.elfinder.controller;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.io.IOUtils;

/**
 * this class stores upload files in the request attributes for later usage
 * 
 * @author bluejoe
 *
 */
public class MultipleUploadItems
{
	List<FileItemStream> _items = new ArrayList<FileItemStream>();

	public List<FileItemStream> items()
	{
		return _items;
	}

	/**
	 * find items with given form field name
	 * 
	 * @param itemName
	 * @return
	 */
	public List<FileItemStream> items(String fieldName)
	{
		List<FileItemStream> filteredItems = new ArrayList<FileItemStream>();
		for (FileItemStream fis : _items)
		{
			if (fis.getFieldName().equals(fieldName))
				filteredItems.add(fis);
		}

		return filteredItems;
	}

	public void addItem(FileItemStream fis)
	{
		_items.add(fis);
	}

	public void addItemProxy(final FileItemStream item) throws IOException
	{
		InputStream stream = item.openStream();
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		IOUtils.copy(stream, os);
		final byte[] bs = os.toByteArray();
		stream.close();

		addItem((FileItemStream) Proxy.newProxyInstance(this.getClass()
				.getClassLoader(), new Class[] { FileItemStream.class },
				new InvocationHandler()
				{
					@Override
					public Object invoke(Object proxy, Method method,
							Object[] args) throws Throwable
					{
						if ("openStream".equals(method.getName()))
						{
							return new ByteArrayInputStream(bs);
						}

						return method.invoke(item, args);
					}
				}));
	}

	public void writeInto(HttpServletRequest request)
			throws FileUploadException, IOException
	{
		// store items for compatablity
		request.setAttribute(FileItemStream.class.getName(), _items);
		request.setAttribute(MultipleUploadItems.class.getName(), this);
	}

	public static MultipleUploadItems loadFrom(HttpServletRequest request)
	{
		return (MultipleUploadItems) request
				.getAttribute(MultipleUploadItems.class.getName());
	}
}
