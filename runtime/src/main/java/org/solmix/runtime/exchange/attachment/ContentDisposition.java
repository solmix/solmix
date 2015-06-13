
package org.solmix.runtime.exchange.attachment;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ContentDisposition {
    private static final String CD_HEADER_PARAMS_EXPRESSION =
        "(([\\w]+( )?\\*?=( )?\"[^\"]+\")|([\\w]+( )?\\*?=( )?[^;]+))";
    private static final Pattern CD_HEADER_PARAMS_PATTERN =
            Pattern.compile(CD_HEADER_PARAMS_EXPRESSION);

    private static final String CD_HEADER_EXT_PARAMS_EXPRESSION =
            "(UTF-8|ISO-8859-1)''((?:%[0-9a-f]{2}|\\S)+)";
    private static final Pattern CD_HEADER_EXT_PARAMS_PATTERN =
            Pattern.compile(CD_HEADER_EXT_PARAMS_EXPRESSION);

    private String value;
    private String type;
    private Map<String, String> params = new LinkedHashMap<String, String>();

    public ContentDisposition(String value) {
        this.value = value;

        String tempValue = value;

        int index = tempValue.indexOf(';');
        if (index > 0 && !(tempValue.indexOf('=') < index)) {
            type = tempValue.substring(0, index).trim();
            tempValue = tempValue.substring(index + 1);
        }

        String extendedFilename = null;
        Matcher m = CD_HEADER_PARAMS_PATTERN.matcher(tempValue);
        while (m.find()) {
            String[] pair = m.group().trim().split("=");
            String paramName = pair[0].trim();
            String paramValue = pair.length == 2 ? pair[1].trim().replace("\"", "") : "";
            // filename* looks like the only CD param that is human readable
            // and worthy of the extended encoding support. Other parameters 
            // can be supported if needed, see the complete list below
            /*
                http://www.iana.org/assignments/cont-disp/cont-disp.xhtml#cont-disp-2

                filename            name to be used when creating file [RFC2183]
                creation-date       date when content was created [RFC2183]
                modification-date   date when content was last modified [RFC2183]
                read-date           date when content was last read [RFC2183]
                size                approximate size of content in octets [RFC2183]
                name                original field name in form [RFC2388]
                voice               type or use of audio content [RFC2421]
                handling            whether or not processing is required [RFC3204]
             */
            if ("filename*".equals(paramName)) {
                // try to decode the value if it matches the spec
                try {
                    Matcher matcher = CD_HEADER_EXT_PARAMS_PATTERN.matcher(paramValue);
                    if (matcher.matches()) {
                        String encodingScheme = matcher.group(1);
                        String encodedValue = matcher.group(2);
                        paramValue = Rfc5987Util.decode(encodedValue, encodingScheme);
                        extendedFilename = paramValue;
                    }
                } catch (UnsupportedEncodingException e) {
                    // would be odd not to support UTF-8 or 8859-1
                }
            }
            params.put(paramName, paramValue);
        }
        if (extendedFilename != null) {
            params.put("filename", extendedFilename);
        }
    }

    public String getType() {
        return type;
    }

    public String getParameter(String name) {
        return params.get(name);
    }

    public Map<String, String> getParameters() {
        return Collections.unmodifiableMap(params);
    }

    public String toString() {
        return value;
    }
    
    static class Rfc5987Util{
        private static final Pattern ENCODED_VALUE_PATTERN = Pattern.compile("%[0-9a-f]{2}|\\S",
            Pattern.CASE_INSENSITIVE);

    private Rfc5987Util() {

    }

    public static String encode(final String s) throws UnsupportedEncodingException {
        return encode(s, "UTF-8");
    }

    public static String encode(final String s, String encoding) throws UnsupportedEncodingException {
        final byte[] rawBytes = s.getBytes(encoding);
        final int len = rawBytes.length;
        final StringBuilder sb = new StringBuilder(len << 1);
        final char[] digits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            'a', 'b', 'c', 'd', 'e', 'f'};
        final byte[] attributeChars = {'!', '#', '$', '&', '+', '-', '.', '0',
            '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D',
            'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q',
            'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '^', '_', '`', 'a',
            'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n',
            'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '|',
            '~'};
        for (final byte b : rawBytes) {
            if (Arrays.binarySearch(attributeChars, b) >= 0) {
                sb.append((char) b);
            } else {
                sb.append('%');
                sb.append(digits[0x0f & (b >>> 4)]);
                sb.append(digits[b & 0x0f]);
            }
        }

        return sb.toString();
    }

    public static String decode(String s, String encoding)
        throws UnsupportedEncodingException {
        Matcher matcher = ENCODED_VALUE_PATTERN.matcher(s);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        while (matcher.find()) {
            String matched = matcher.group();
            if (matched.startsWith("%")) {
                Integer value = Integer.parseInt(matched.substring(1), 16);
                bos.write(value);
            } else {
                bos.write(matched.charAt(0));
            }
        }

        return new String(bos.toByteArray(), encoding);
    }
    }
}
