/**
 * Copyright 2014 The Solmix Project
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.gnu.org/licenses/ 
 * or see the FSF site: http://www.fsf.org. 
 */

package org.solmix.runtime.exchange.attachment;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import javax.activation.DataHandler;
import javax.activation.DataSource;

import org.solmix.commons.util.StringUtils;
import org.solmix.runtime.exchange.Attachment;
import org.solmix.runtime.exchange.Message;
import org.solmix.runtime.io.CachedOutputStream;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2014年10月22日
 */

public final class AttachmentUtils
{

    private static final Random BOUND_RANDOM = new Random();
    private AttachmentUtils()
    {

    }

    public static boolean isTypeSupported(String contentType, List<String> types) {
        if (contentType == null) {
            return false;
        }
        contentType = contentType.toLowerCase();
        for (String s : types) {
            if (contentType.indexOf(s) != -1) {
                return true;
            }
        }
        return false;
    }

    public static String getUniqueBoundaryValue() {
        //generate a random UUID.
        //we don't need the cryptographically secure random uuid that
        //UUID.randomUUID() will produce.  Thus, use a faster
        //pseudo-random thing
        long leastSigBits = 0;
        long mostSigBits = 0;
        synchronized (BOUND_RANDOM) {
            mostSigBits = BOUND_RANDOM.nextLong();
            leastSigBits = BOUND_RANDOM.nextLong();
        }
        
        mostSigBits &= 0xFFFFFFFFFFFF0FFFL;  //clear version
        mostSigBits |= 0x0000000000004000L;  //set version

        leastSigBits &= 0x3FFFFFFFFFFFFFFFL; //clear the variant
        leastSigBits |= 0x8000000000000000L; //set to IETF variant
        
        UUID result = new UUID(mostSigBits, leastSigBits);
        
        return "uuid:" + result.toString();
    }
    public static void setStreamedAttachmentProperties(Message message, CachedOutputStream bos) throws IOException {
        Object directory = message.get(AttachmentDeserializer.ATTACHMENT_DIRECTORY);
        if (directory != null) {
            if (directory instanceof File) {
                bos.setOutputDir((File) directory);
            } else {
                bos.setOutputDir(new File((String) directory));
            }
        }

        Object threshold = message.get(AttachmentDeserializer.ATTACHMENT_MEMORY_THRESHOLD);
        if (threshold != null) {
            if (threshold instanceof Long) {
                bos.setThreshold((Long) threshold);
            } else {
                bos.setThreshold(Long.valueOf((String) threshold));
            }
        } else {
            bos.setThreshold(AttachmentDeserializer.THRESHOLD);
        }

        Object maxSize = message.get(AttachmentDeserializer.ATTACHMENT_MAX_SIZE);
        if (maxSize != null) {
            if (maxSize instanceof Long) {
                bos.setMaxSize((Long) maxSize);
            } else {
                bos.setMaxSize(Long.valueOf((String) maxSize));
            }
        }
    }
    public static Attachment createAttachment(InputStream stream, Map<String, List<String>> headers) 
        throws IOException {
        String id = cleanContentId(getHeader(headers, "Content-ID"));
        DefaultAttachment att = new DefaultAttachment(id);
        final String ct = getHeader(headers, "Content-Type");
        String cd = getHeader(headers, "Content-Disposition");
        String fileName = getContentDispositionFileName(cd);
        
        String encoding = null;
        for (Map.Entry<String, List<String>> e : headers.entrySet()) {
            String name = e.getKey();
            if (name.equalsIgnoreCase("Content-Transfer-Encoding")) {
                encoding = getHeader(headers, name);
            }
            att.setHeader(name, getHeaderValue(e.getValue()));
        }
        if (encoding == null) {
            encoding = "binary";
        }
        DataSource source = new AttachmentDataSource(ct, 
                                                     decode(stream, encoding));
        if (!StringUtils.isEmpty(fileName)) {
            ((AttachmentDataSource)source).setName(fileName);
        }
        att.setDataHandler(new DataHandler(source));        
        return att;
        
    }
    
    public static InputStream decode(InputStream in, String encoding) throws IOException {
        encoding = encoding.toLowerCase();

        // some encodings are just pass-throughs, with no real decoding.
        if ("binary".equals(encoding) 
            || "7bit".equals(encoding) 
            || "8bit".equals(encoding)) {
            return in;
        } else if ("base64".equals(encoding)) {
            return new Base64DecoderStream(in);
        } else if ("quoted-printable".equals(encoding)) {
            return new QuotedPrintableDecoderStream(in);
        } else {
            throw new IOException("Unknown encoding " + encoding);
        }
    }    
    public static String cleanContentId(String id) {
        if (id != null) {
            if (id.startsWith("<")) {
                // strip <>
                id = id.substring(1, id.length() - 1);
            }
            // strip cid:
            if (id.startsWith("cid:")) {
                id = id.substring(4);
            }
            // urldecode. Is this bad even without cid:? What does decode do with malformed %-signs, anyhow?
            try {
                id = URLDecoder.decode(id, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                //ignore, keep id as is
            }
        }
        return id;
    }
    static String getContentDispositionFileName(String cd) {
        if (StringUtils.isEmpty(cd)) {
            return null;
        }
        ContentDisposition c = new ContentDisposition(cd);
        String s = c.getParameter("filename");
        if (s == null) {
            s = c.getParameter("name");
        }
        return s;
    }
    static String getHeader(Map<String, List<String>> headers, String h) {
        return getHeaderValue(headers.get(h));
    }
    static String getHeader(Map<String, List<String>> headers, String h, String delim) {
        return getHeaderValue(headers.get(h), delim);
    }
    static String getHeaderValue(List<String> v) {
        if (v != null && v.size() > 0) {
            return v.get(0);
        }
        return null;
    }
    static String getHeaderValue(List<String> v, String delim) {
        if (v != null && v.size() > 0) {
            StringBuilder b = new StringBuilder();
            for (String s : v) {
                if (b.length() > 0) {
                    b.append(delim);
                }
                b.append(s);
            }
            return b.toString();
        }
        return null;
    }
}
