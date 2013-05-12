
package com.solmix.fmk.servlet;

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

import com.solmix.commons.io.ByteCountingOutputStream;
import com.solmix.commons.io.IByteCounter;
import com.solmix.commons.util.IOUtil;

public class SlxFileItem implements FileItem
{

    private static final long serialVersionUID = 5709803430443515506L;

    public SlxFileItem(String fieldName)
    {
        expectedSize = -1L;
        this.fieldName = fieldName;
    }

    public SlxFileItem(String fieldName, String contentType, boolean isFormField, String fileName, IByteCounter byteCounter)
    {
        this(fieldName);
        this.contentType = contentType;
        this.isFormField = isFormField;
        this.fileName = fileName;
        this.byteCounter = byteCounter;
    }

    public void delete() {
    }

    public byte[] get() {
        return data.toByteArray();
    }

    public String getContentType() {
        return contentType;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String name) {
        fieldName = name;
    }

    public String getName() {
        return fileName;
    }

    public InputStream getInputStream() {
        return new ByteArrayInputStream(get());
    }

    public OutputStream getOutputStream() throws IOException {
        if (os == null) {
            data = new ByteArrayOutputStream();
            os = data;
            if (byteCounter != null)
                os = new ByteCountingOutputStream(data, byteCounter);
        }
        return os;
    }

    public long getSize() {
        return (long) data.size();
    }

    public String getString() {
        return new String(get());
    }

    public String getString(String encoding) throws UnsupportedEncodingException {
        return new String(get(), encoding);
    }

    public boolean isFormField() {
        return isFormField;
    }

    public void setFormField(boolean state) {
        isFormField = state;
    }

    public boolean isInMemory() {
        return true;
    }

    public void write(File file) throws Exception {
        IOUtil.copyStreams(getInputStream(), new FileOutputStream(file));
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

    public void setErrors(List errors) {
        byteCounter.setErrors(errors);
    }

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
}
