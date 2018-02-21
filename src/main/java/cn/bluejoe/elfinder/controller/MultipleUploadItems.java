package cn.bluejoe.elfinder.controller;

import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

/**
 * this class stores upload files in the request attributes for later usage
 *
 * @author bluejoe
 */
public class MultipleUploadItems {
    Logger _logger = Logger.getLogger(this.getClass());
    List<FileItemStream> _items = new ArrayList<FileItemStream>();
    File _tempDir;

    public List<FileItemStream> items() {
        return _items;
    }

    public MultipleUploadItems(File tempDir) {
        _tempDir = tempDir;
    }

    /**
     * find items with given form field name
     *
     * @param fieldName
     * @return
     */
    public List<FileItemStream> items(String fieldName) {
        List<FileItemStream> filteredItems = new ArrayList<FileItemStream>();
        for (FileItemStream fis : _items) {
            if (fis.getFieldName().equals(fieldName))
                filteredItems.add(fis);
        }

        return filteredItems;
    }

    public void addItem(FileItemStream fis) {
        _items.add(fis);
    }

    public void addItemProxy(final FileItemStream item) throws IOException {
        InputStream stream = item.openStream();
        //ByteArrayOutputStream os = new ByteArrayOutputStream();
        //create a temp source
        final File source = File.createTempFile("elfinder_upload_", "", _tempDir);
        FileOutputStream os = new FileOutputStream(source);
        IOUtils.copy(stream, os);
        os.close();
        //final byte[] bs = os.toByteArray();
        stream.close();
        _logger.debug(String.format("saving item: %s", source.getCanonicalPath()));
        addItem((FileItemStream) Proxy.newProxyInstance(this.getClass()
                        .getClassLoader(), new Class[]{FileItemStream.class, Finalizable.class},
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method,
                                         Object[] args) throws Throwable {
                        if ("openStream".equals(method.getName())) {
                            //return new ByteArrayInputStream(bs);
                            return new FileInputStream(source);
                        }
                        if ("finalize".equals(method.getName())) {
                            source.delete();
                            _logger.debug(String.format("removing item: %s", source.getCanonicalPath()));
                            return null;
                        }
                        return method.invoke(item, args);
                    }
                }));
    }

    public void writeInto(HttpServletRequest request)
            throws FileUploadException, IOException {
        // store items for compatablity
        request.setAttribute(FileItemStream.class.getName(), _items);
        request.setAttribute(MultipleUploadItems.class.getName(), this);
    }

    public static MultipleUploadItems loadFrom(HttpServletRequest request) {
        return (MultipleUploadItems) request
                .getAttribute(MultipleUploadItems.class.getName());
    }

    public static void finalize(HttpServletRequest request) {
        MultipleUploadItems mui = loadFrom(request);
        if (mui != null) {
            for (FileItemStream fis : mui.items()) {
                if (fis instanceof Finalizable) {
                    ((Finalizable) fis).finalize();
                }
            }
        }
    }

    interface Finalizable {
        void finalize();
    }
}
