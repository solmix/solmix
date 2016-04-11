package org.solmix.commons.net;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public abstract class IpListFromUrl {
    private static final Logger LOG = LoggerFactory.getLogger(IpListFromUrl.class);

    /**
     * The string indicating the start of the comments in a line containing the
     * IP address in a file URL
     */
    private static final String COMMENT_STR = " #";

    /**
     * This character at the start of a line indicates a comment line in a URL
     * file
     */
    private static final char COMMENT_CHAR = '#';

    /**
     * This method is used to read all interfaces from an URL file.
     *
     * <pre>
     * The file URL is read and each entry in this file checked. Each line
     *  in the URL file can be one of -
     *  &lt;IP&gt;&lt;space&gt;#&lt;comments&gt;
     *  or
     *  &lt;IP&gt;
     *  or
     *  #&lt;comments&gt;
     *
     *  Lines starting with a '#' are ignored and so are characters after
     *  a '&lt;space&gt;#' in a line.
     * </pre>
     *
     * @param url
     *            The url file to read
     * @return list of IPs in the file
     */
    public static List<String> fetch(final String url) {
        final List<String> iplist = new ArrayList<String>();

        URL u = null;
        InputStream stream = null;
        InputStreamReader isr = null;
        BufferedReader br = null;

        try {
            // open the file indicated by the url
            u = new URL(url);
            stream = u.openStream();

            // check to see if the file exists
            if (stream != null) {
                isr = new InputStreamReader(stream, "UTF-8");
                br = new BufferedReader(isr);

                String ipLine = null;
                String specIP = null;

                // get each line of the file and turn it into a specific range
                while ((ipLine = br.readLine()) != null) {
                    ipLine = ipLine.trim();
                    if (ipLine.length() == 0 || ipLine.charAt(0) == COMMENT_CHAR) {
                        // blank line or skip comment
                        continue;
                    }

                    // check for comments after IP
                    final int comIndex = ipLine.indexOf(COMMENT_STR);
                    if (comIndex == -1) {
                        specIP = ipLine;
                    } else {
                        specIP = ipLine.substring(0, comIndex);
                        ipLine = ipLine.trim();
                    }

                    iplist.add(specIP);
                }
            } else {
                // log something
                LOG.warn("URL does not exist: {}", url);
            }
        } catch (final IOException e) {
            LOG.error("Error reading URL: {}: {}", url, e.getLocalizedMessage());
        } finally {
            closeQuietly(br);
            closeQuietly(isr);
            closeQuietly(stream);
        }

        return iplist;
    }

    private static void closeQuietly(final Closeable obj) {
        if (obj == null) return;

        try {
            obj.close();
        } catch (final Exception e) {
            LOG.warn("Failed to close {}", obj, e);
        }
    }
}
