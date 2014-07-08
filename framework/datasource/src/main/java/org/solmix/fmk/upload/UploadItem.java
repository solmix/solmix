
package org.solmix.fmk.upload;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemHeaders;

import org.solmix.commons.io.ByteCountingOutputStream;
import org.solmix.commons.io.IByteCounter;
import org.solmix.commons.util.IOUtils;

public class UploadItem implements FileItem
{

    private static final long serialVersionUID = 5709803430443515506L;
    private FileItemHeaders header;

    public UploadItem(String fieldName)
    {
        expectedSize = -1L;
        this.fieldName = fieldName;
    }

    public UploadItem(String fieldName, String contentType, boolean isFormField, String fileName, IByteCounter byteCounter)
    {
        this(fieldName);
        this.contentType = contentType;
        this.isFormField = isFormField;
        this.fileName = fileName;
        this.byteCounter = byteCounter;
    }

    @Override
    public void delete() {
    }

    @Override
    public byte[] get() {
        return data.toByteArray();
    }

    @Override
    public String getContentType() {
        return contentType;
    }

    @Override
    public String getFieldName() {
        return fieldName;
    }

    @Override
    public void setFieldName(String name) {
        fieldName = name;
    }

    @Override
    public String getName() {
        return fileName;
    }

    @Override
    public InputStream getInputStream() {
        return new ByteArrayInputStream(get());
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        if (os == null) {
            data = new ByteArrayOutputStream();
            os = data;
            if (byteCounter != null)
                os = new ByteCountingOutputStream(data, byteCounter);
        }
        return os;
    }

    @Override
    public long getSize() {
        return data.size();
    }

    @Override
    public String getString() {
        return new String(get());
    }

    @Override
    public String getString(String encoding) throws UnsupportedEncodingException {
        return new String(get(), encoding);
    }

    @Override
    public boolean isFormField() {
        return isFormField;
    }

    @Override
    public void setFormField(boolean state) {
        isFormField = state;
    }

    @Override
    public boolean isInMemory() {
        return true;
    }

    @Override
    public void write(File file) throws Exception {
        IOUtils.copyStreams(getInputStream(), new FileOutputStream(file));
    }

    public void setTotalSize(long size) {
        expectedSize = size;
    }

    public long getTotalSize() {
        if (expectedSize == -1L && byteCounter != null)
            setTotalSize(byteCounter.getTotalBytes());
        return expectedSize;
    }

    public String getFileName() {
        return getName();
    }

    public void setFileName(String name) {
        fileName = name;
    }

    public String getShortFileName() {
        return shortFileName;
    }

    public void setShortFileName(String name) {
        shortFileName = name;
    }

    @SuppressWarnings("rawtypes")
    public void setErrors(List errors) {
        byteCounter.setErrors(errors);
    }

    @SuppressWarnings("rawtypes")
    public List getErrors() {
        return byteCounter.getErrors();
    }

    String fieldName;

    String contentType;

    boolean isFormField;

    String fileName;

    OutputStream os;

    ByteArrayOutputStream data;

    IByteCounter byteCounter;

    long expectedSize;

    String shortFileName;

    /**
     * {@inheritDoc}
     * 
     * @see org.apache.commons.fileupload.FileItemHeadersSupport#getHeaders()
     */
    @Override
    public FileItemHeaders getHeaders() {
        return header;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.apache.commons.fileupload.FileItemHeadersSupport#setHeaders(org.apache.commons.fileupload.FileItemHeaders)
     */
    @Override
    public void setHeaders(FileItemHeaders arg0) {
        this.header=arg0;
        
    }
}
