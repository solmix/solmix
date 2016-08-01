
package org.solmix.commons.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.solmix.commons.Constants;


public final class StringUtils
{

    public static final String[] EMPTY_STRING_ARRAY = new String[0];

    private static final Pattern KVP_PATTERN = Pattern.compile("([_.a-zA-Z0-9][-_.a-zA-Z0-9]*)[=](.*)"); 
    private static final Pattern INT_PATTERN = Pattern.compile("^\\d+$");
    
     StringUtils(){
    }
     static final String check = "^([a-z0-9A-Z]+[-|_|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";

     public static boolean isEmail(String email) {
         if (isBlank(email)) {
             return false;
         } else {
             Pattern pattern = Pattern.compile(check);
             Matcher matcher = pattern.matcher(email);
             return matcher.matches();
         }
     }
    public static boolean isBlank(String str) {
        if (str == null || str.length() == 0)
            return true;
        return false;
    }

    /**
     * is empty string.
     * 
     * @param str source string.
     * @return is empty.
     */
    public static boolean isEmpty(String str) {
        if (str == null || str.length() == 0)
            return true;
        return false;
    }
    
    public static boolean isEmpty(List<String> list) {
        if (list == null || list.size() == 0) {
            return true;
        }
        if (list.size() == 1 && isEmpty(list.get(0))) {
            return true;
        }
        return false;
    }
    /**
     * is not empty string.
     * 
     * @param str source string.
     * @return is not empty.
     */
    public static boolean isNotEmpty(String str) {
        return str != null && str.length() > 0;
    }

    /**
     * 
     * @param s1
     * @param s2
     * @return equals
     */
    public static boolean isEquals(String s1, String s2) {
        if (s1 == null && s2 == null)
            return true;
        if (s1 == null || s2 == null)
            return false;
        return s1.equals(s2);
    }

    /**
     * is integer string.
     * 
     * @param str
     * @return is integer
     */
    public static boolean isInteger(String str) {
        if (str == null || str.length() == 0)
            return false;
        return INT_PATTERN.matcher(str).matches();
    }

    public static int parseInteger(String str) {
        if (!isInteger(str))
            return 0;
        return Integer.parseInt(str);
    }

    /**
     * Returns true if s is a legal Java identifier.
     * <p>
     * <a href="http://www.exampledepot.com/egs/java.lang/IsJavaId.html">more
     * info.</a>
     */
    public static boolean isJavaIdentifier(String s) {
        if (s.length() == 0 || !Character.isJavaIdentifierStart(s.charAt(0))) {
            return false;
        }
        for (int i = 1; i < s.length(); i++) {
            if (!Character.isJavaIdentifierPart(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static boolean isContains(String values, String value) {
        if (values == null || values.length() == 0) {
            return false;
        }
        return isContains(Constants.COMMA_SPLIT_PATTERN.split(values), value);
    }

    /**
     * 
     * @param values
     * @param value
     * @return contains
     */
    public static boolean isContains(String[] values, String value) {
        if (value != null && value.length() > 0 && values != null
            && values.length > 0) {
            for (String v : values) {
                if (value.equals(v)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isNumeric(String str) {
        if (str == null) {
            return false;
        }
        int sz = str.length();
        for (int i = 0; i < sz; i++) {
            if (Character.isDigit(str.charAt(i)) == false) {
                return false;
            }
        }
        return true;
    }

    /**
     * translat.
     * 
     * @param src source string.
     * @param from src char table.
     * @param to target char table.
     * @return String.
     */
    public static String translat(String src, String from, String to) {
        if (isEmpty(src))
            return src;
        StringBuilder sb = null;
        int ix;
        char c;
        for (int i = 0, len = src.length(); i < len; i++) {
            c = src.charAt(i);
            ix = from.indexOf(c);
            if (ix == -1) {
                if (sb != null)
                    sb.append(c);
            } else {
                if (sb == null) {
                    sb = new StringBuilder(len);
                    sb.append(src, 0, i);
                }
                if (ix < to.length())
                    sb.append(to.charAt(ix));
            }
        }
        return sb == null ? src : sb.toString();
    }

 // ==========================================================================
    // 字符串分割函数。
    //
    // 将字符串按指定分隔符分割。
    // ==========================================================================

    /**
     * 将字符串按指定字符分割。
     * <p>
     * 分隔符不会出现在目标数组中，连续的分隔符就被看作一个。如果字符串为<code>null</code>，则返回<code>null</code>。
     * <p/>
     * <pre>
     * StringUtil.split(null, *)         = null
     * StringUtil.split("", *)           = []
     * StringUtil.split("a.b.c", '.')    = ["a", "b", "c"]
     * StringUtil.split("a..b.c", '.')   = ["a", "b", "c"]
     * StringUtil.split("a:b:c", '.')    = ["a:b:c"]
     * StringUtil.split("a b c", ' ')    = ["a", "b", "c"]
     * </pre>
     * <p/>
     * </p>
     *
     * @param str           要分割的字符串
     * @param separatorChar 分隔符
     * @return 分割后的字符串数组，如果原字符串为<code>null</code>，则返回<code>null</code>
     */
    public static String[] split(String str, char separatorChar) {
        if (str == null) {
            return null;
        }

        int length = str.length();

        if (length == 0) {
            return EMPTY_STRING_ARRAY;
        }

        List<String> list = new LinkedList<String>();
        int i = 0;
        int start = 0;
        boolean match = false;

        while (i < length) {
            if (str.charAt(i) == separatorChar) {
                if (match) {
                    list.add(str.substring(start, i));
                    match = false;
                }

                start = ++i;
                continue;
            }

            match = true;
            i++;
        }

        if (match) {
            list.add(str.substring(start, i));
        }

        return list.toArray(new String[list.size()]);
    }

    /**
     * 将字符串按指定字符分割。
     * <p>
     * 分隔符不会出现在目标数组中，连续的分隔符就被看作一个。如果字符串为<code>null</code>，则返回<code>null</code>。
     * <p/>
     * <pre>
     * StringUtil.split(null, *)                = null
     * StringUtil.split("", *)                  = []
     * StringUtil.split("abc def", null)        = ["abc", "def"]
     * StringUtil.split("abc def", " ")         = ["abc", "def"]
     * StringUtil.split("abc  def", " ")        = ["abc", "def"]
     * StringUtil.split(" ab:  cd::ef  ", ":")  = ["ab", "cd", "ef"]
     * StringUtil.split("abc.def", "")          = ["abc.def"]
     * </pre>
     * <p/>
     * </p>
     *
     * @param str            要分割的字符串
     * @param separatorChars 分隔符
     * @return 分割后的字符串数组，如果原字符串为<code>null</code>，则返回<code>null</code>
     */
    public static String[] split(String str, String separatorChars) {
        return split(str, separatorChars, -1);
    }

    /**
     * 将字符串按指定字符分割。
     * <p>
     * 分隔符不会出现在目标数组中，连续的分隔符就被看作一个。如果字符串为<code>null</code>，则返回<code>null</code>。
     * <p/>
     * <pre>
     * StringUtil.split(null, *, *)                 = null
     * StringUtil.split("", *, *)                   = []
     * StringUtil.split("ab cd ef", null, 0)        = ["ab", "cd", "ef"]
     * StringUtil.split("  ab   cd ef  ", null, 0)  = ["ab", "cd", "ef"]
     * StringUtil.split("ab:cd::ef", ":", 0)        = ["ab", "cd", "ef"]
     * StringUtil.split("ab:cd:ef", ":", 2)         = ["ab", "cdef"]
     * StringUtil.split("abc.def", "", 2)           = ["abc.def"]
     * </pre>
     * <p/>
     * </p>
     *
     * @param str            要分割的字符串
     * @param separatorChars 分隔符
     * @param max            返回的数组的最大个数，如果小于等于0，则表示无限制
     * @return 分割后的字符串数组，如果原字符串为<code>null</code>，则返回<code>null</code>
     */
    public static String[] split(String str, String separatorChars, int max) {
        if (str == null) {
            return null;
        }

        int length = str.length();

        if (length == 0) {
            return EMPTY_STRING_ARRAY;
        }

        List<String> list = new LinkedList<String>();
        int sizePlus1 = 1;
        int i = 0;
        int start = 0;
        boolean match = false;

        if (separatorChars == null) {
            // null表示使用空白作为分隔符
            while (i < length) {
                if (Character.isWhitespace(str.charAt(i))) {
                    if (match) {
                        if (sizePlus1++ == max) {
                            i = length;
                        }

                        list.add(str.substring(start, i));
                        match = false;
                    }

                    start = ++i;
                    continue;
                }

                match = true;
                i++;
            }
        } else if (separatorChars.length() == 1) {
            // 优化分隔符长度为1的情形
            char sep = separatorChars.charAt(0);

            while (i < length) {
                if (str.charAt(i) == sep) {
                    if (match) {
                        if (sizePlus1++ == max) {
                            i = length;
                        }

                        list.add(str.substring(start, i));
                        match = false;
                    }

                    start = ++i;
                    continue;
                }

                match = true;
                i++;
            }
        } else {
            // 一般情形
            while (i < length) {
                if (separatorChars.indexOf(str.charAt(i)) >= 0) {
                    if (match) {
                        if (sizePlus1++ == max) {
                            i = length;
                        }

                        list.add(str.substring(start, i));
                        match = false;
                    }

                    start = ++i;
                    continue;
                }

                match = true;
                i++;
            }
        }

        if (match) {
            list.add(str.substring(start, i));
        }

        return list.toArray(new String[list.size()]);
    }

    /**
     * join string.
     * 
     * @param array String array.
     * @return String.
     */
    public static String join(String[] array) {
        if (array.length == 0)
            return "";
        StringBuilder sb = new StringBuilder();
        for (String s : array)
            sb.append(s);
        return sb.toString();
    }

    /**
     * join string like javascript.
     * 
     * @param array String array.
     * @param split split
     * @return String.
     */
    public static String join(String[] array, char split) {
        if (array.length == 0)
            return "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < array.length; i++) {
            if (i > 0)
                sb.append(split);
            sb.append(array[i]);
        }
        return sb.toString();
    }
    
    public static String join(Object[] array, String split) {
        if (array.length == 0)
            return "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < array.length; i++) {
            if (i > 0)
                sb.append(split);
            sb.append(array[i]);
        }
        return sb.toString();
    }
    

    /**
     * join string like javascript.
     * 
     * @param array String array.
     * @param split split
     * @return String.
     */
    public static String join(String[] array, String split) {
        if (array.length == 0)
            return "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < array.length; i++) {
            if (i > 0)
                sb.append(split);
            sb.append(array[i]);
        }
        return sb.toString();
    }
   
    public static String join(Collection<String> coll, String split) {
        if (coll.isEmpty())
            return "";

        StringBuilder sb = new StringBuilder();
        boolean isFirst = true;
        for (String s : coll) {
            if (isFirst)
                isFirst = false;
            else
                sb.append(split);
            sb.append(s);
        }
        return sb.toString();
    }

    /**
     * parse key-value pair.
     * 
     * @param str string.
     * @param itemSeparator item separator.
     * @return key-value map;
     */
    private static Map<String, String> parseKeyValuePair(String str,
        String itemSeparator) {
        String[] tmp = str.split(itemSeparator);
        Map<String, String> map = new HashMap<String, String>(tmp.length);
        for (int i = 0; i < tmp.length; i++) {
            Matcher matcher = KVP_PATTERN.matcher(tmp[i]);
            if (matcher.matches() == false)
                continue;
            map.put(matcher.group(1), matcher.group(2));
        }
        return map;
    }

    public static String getQueryStringValue(String qs, String key) {
        Map<String, String> map = StringUtils.parseQueryString(qs);
        return map.get(key);
    }

    /**
     * parse query string to Parameters.
     * 
     * @param qs query string.
     * @return Parameters instance.
     */
    public static Map<String, String> parseQueryString(String qs) {
        if (qs == null || qs.length() == 0)
            return new HashMap<String, String>();
        return parseKeyValuePair(qs, "\\&");
    }

    public static String getServiceKey(Map<String, String> ps) {
        StringBuilder buf = new StringBuilder();
        String group = ps.get(Constants.GROUP_KEY);
        if (group != null && group.length() > 0) {
            buf.append(group).append("/");
        }
        buf.append(ps.get(Constants.INTERFACE_KEY));
        String version = ps.get(Constants.VERSION_KEY);
        if (version != null && version.length() > 0) {
            buf.append(":").append(version);
        }
        return buf.toString();
    }

    public static String toQueryString(Map<String, String> ps) {
        StringBuilder buf = new StringBuilder();
        if (ps != null && ps.size() > 0) {
            for (Map.Entry<String, String> entry : new TreeMap<String, String>(
                ps).entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                if (key != null && key.length() > 0 && value != null
                    && value.length() > 0) {
                    if (buf.length() > 0) {
                        buf.append("&");
                    }
                    buf.append(key);
                    buf.append("=");
                    buf.append(value);
                }
            }
        }
        return buf.toString();
    }
    public static String splitToCamelName(String splitName, String split) {
        if (splitName == null || splitName.length() == 0) {
            return splitName;
        }
        if(splitName.indexOf(split)<0){
            return splitName;
        }
        StringTokenizer tokens = new StringTokenizer(splitName, split);
        StringBuilder buf = new StringBuilder();
        boolean first=true;
        while(tokens.hasMoreTokens()){
            String t=tokens.nextToken();
            t=t.toLowerCase();
            if(first){
                buf.append(t);
                first=false;
            }else{
                buf.append(t.substring(0, 1).toUpperCase()).append(t.substring(1, t.length()));
            }
        }
        return buf.toString();
    }

    public static String camelToSplitName(String camelName, String split) {
        if (camelName == null || camelName.length() == 0) {
            return camelName;
        }
        StringBuilder buf = null;
        for (int i = 0; i < camelName.length(); i++) {
            char ch = camelName.charAt(i);
            if (ch >= 'A' && ch <= 'Z') {
                if (buf == null) {
                    buf = new StringBuilder();
                    if (i > 0) {
                        buf.append(camelName.substring(0, i));
                    }
                }
                if (i > 0) {
                    buf.append(split);
                }
                buf.append(Character.toLowerCase(ch));
            } else if (buf != null) {
                buf.append(ch);
            }
        }
        return buf == null ? camelName : buf.toString();
    }
    
    public static String toString(Throwable e) {
        return toString(null, e);
    }

    public static String toString(String msg, Throwable e) {
        StringWriter w = new StringWriter();
       
        PrintWriter p = new PrintWriter(w);
        p.print(e.getClass().getName()+":");
        if (msg != null) {
            w.write(msg+": " );
        }
        if (e.getMessage() != null) {
            p.print( e.getMessage());
        }
        p.println();
        try {
            e.printStackTrace(p);
            return w.toString();
        } finally {
            p.close();
        }
    }
    
   
    public static String trimToNull(String str) {
        if (str == null) {
            return null;
        }

        String result = str.trim();

        if (result == null || result.length() == 0) {
            return null;
        }

        return result;
    }

    public static String defaultIfEmpty(String str, String defaultStr) {
        return str == null || str.length() == 0 ? defaultStr : str;
    }

    /**
     * @param path
     * @return
     */
     public static String trimToEmpty(String str) {
        if (str == null) {
            return "";
        }

        return str.trim();
    }
     
     public static String replace(String inString, String oldPattern, String newPattern) {
 		if (!hasLength(inString) || !hasLength(oldPattern) || newPattern == null) {
 			return inString;
 		}
 		StringBuilder sb = new StringBuilder();
 		int pos = 0; // our position in the old string
 		int index = inString.indexOf(oldPattern);
 		// the index of an occurrence we've found, or -1
 		int patLen = oldPattern.length();
 		while (index >= 0) {
 			sb.append(inString.substring(pos, index));
 			sb.append(newPattern);
 			pos = index + patLen;
 			index = inString.indexOf(oldPattern, pos);
 		}
 		sb.append(inString.substring(pos));
 		// remember to append any characters to the right of a match
 		return sb.toString();
 	}
     
     public static boolean hasLength(String str) {
 		return hasLength((CharSequence) str);
 	}
     
 	public static boolean hasLength(CharSequence str) {
		return (str != null && str.length() > 0);
	}

    public static String[] tokenizeToStringArray(String str, String delimiters, boolean trimTokens, boolean ignoreEmptyTokens) {

        if (str == null) {
            return null;
        }
        StringTokenizer st = new StringTokenizer(str, delimiters);
        List<String> tokens = new ArrayList<String>();
        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            if (trimTokens) {
                token = token.trim();
            }
            if (!ignoreEmptyTokens || token.length() > 0) {
                tokens.add(token);
            }
        }
        return tokens.toArray(new String[tokens.size()]);
    }

    public static int countOccurrencesOf(String str, String sub) {
        if (str == null || sub == null || str.length() == 0 || sub.length() == 0) {
            return 0;
        }
        int count = 0;
        int pos = 0;
        int idx;
        while ((idx = str.indexOf(sub, pos)) != -1) {
            ++count;
            pos = idx + sub.length();
        }
        return count;
    }

    public static String toLowerCase(String str) {
        if (str == null) {
            return null;
        }

        return str.toLowerCase();
    }

    public static boolean containsNone(String str, String invalidChars) {
        if (str == null || invalidChars == null) {
            return true;
        }

        return containsNone(str, invalidChars.toCharArray());
    }
    public static boolean containsNone(String str, char[] invalid) {
        if (str == null || invalid == null) {
            return true;
        }

        int strSize = str.length();
        int validSize = invalid.length;

        for (int i = 0; i < strSize; i++) {
            char ch = str.charAt(i);

            for (int j = 0; j < validSize; j++) {
                if (invalid[j] == ch) {
                    return false;
                }
            }
        }

        return true;
    }

    public static String substring(String str, int start) {
        if (str == null) {
            return null;
        }

        if (start < 0) {
            start = str.length() + start;
        }

        if (start < 0) {
            start = 0;
        }

        if (start > str.length()) {
            return ObjectUtils.EMPTY_STRING;
        }

        return str.substring(start);
    }

    public static String substring(String str, int start, int end) {
        if (str == null) {
            return null;
        }

        if (end < 0) {
            end = str.length() + end;
        }

        if (start < 0) {
            start = str.length() + start;
        }

        if (end > str.length()) {
            end = str.length();
        }

        if (start > end) {
            return ObjectUtils.EMPTY_STRING;
        }

        if (start < 0) {
            start = 0;
        }

        if (end < 0) {
            end = 0;
        }

        return str.substring(start, end);
    }

    /**
     * @param name
     * @param string
     * @return
     */
    public static String trim(String str, String stripChars) {
        return trim(str, stripChars, 0);
    }
    
    private static String trim(String str, String stripChars, int mode) {
        if (str == null) {
            return null;
        }

        int length = str.length();
        int start = 0;
        int end = length;

        // 扫描字符串头部
        if (mode <= 0) {
            if (stripChars == null) {
                while (start < end && Character.isWhitespace(str.charAt(start))) {
                    start++;
                }
            } else if (stripChars.length() == 0) {
                return str;
            } else {
                while (start < end && stripChars.indexOf(str.charAt(start)) != -1) {
                    start++;
                }
            }
        }

        // 扫描字符串尾部
        if (mode >= 0) {
            if (stripChars == null) {
                while (start < end && Character.isWhitespace(str.charAt(end - 1))) {
                    end--;
                }
            } else if (stripChars.length() == 0) {
                return str;
            } else {
                while (start < end && stripChars.indexOf(str.charAt(end - 1)) != -1) {
                    end--;
                }
            }
        }

        if (start > 0 || end < length) {
            return str.substring(start, end);
        }

        return str;
    }
    /**
     * Split the string by delimiter
     * 
     * @param toSplit
     * @param delimiter
     * @return
     */
    public static List<String> simpleSplit(String toSplit, String delimiter) {
        if (toSplit == null) {
            return null;
        } else {
            List<String> output = new ArrayList<String>();
            StringTokenizer tokens = new StringTokenizer(toSplit, delimiter, true);
            boolean lastTokenWasDelimiter = false;
            while (tokens.hasMoreTokens()) {
                String token = tokens.nextToken();
                if (!token.equals(delimiter)) {
                    output.add(token);
                    lastTokenWasDelimiter = false;
                } else {
                    if (lastTokenWasDelimiter)
                        output.add("");
                    lastTokenWasDelimiter = true;
                }
            }
            return output;
        }
    }
    
	public static String formatDuration(long duration) {
		return formatDuration(duration, 0, false);
	}

	public static String formatDuration(long duration, int scale,
			boolean minDigits) {
		long hours, mins;
		int digits;
		double millis;

		hours = duration / 3600000;
		duration -= hours * 3600000;

		mins = duration / 60000;
		duration -= mins * 60000;

		millis = (double) duration / 1000;

		StringBuffer buf = new StringBuffer();

		if (hours > 0 || minDigits == false) {
			buf.append(
					hours < 10 && minDigits == false ? "0" + hours : String
							.valueOf(hours)).append(':');
			minDigits = false;
		}

		if (mins > 0 || minDigits == false) {
			buf.append(
					mins < 10 && minDigits == false ? "0" + mins : String
							.valueOf(mins)).append(':');
			minDigits = false;
		}

		// Format seconds and milliseconds
		NumberFormat fmt = NumberFormat.getInstance();
		digits = (minDigits == false || (scale == 0 && millis >= 9.5) ? 2 : 1);
		fmt.setMinimumIntegerDigits(digits);
		fmt.setMaximumIntegerDigits(2); // Max of 2
		fmt.setMinimumFractionDigits(0); // Don't need any
		fmt.setMaximumFractionDigits(scale);

		buf.append(fmt.format(millis));

		return buf.toString();
	}
	private static final char QUOTE = '\'';
	private static final char DOUBLEQUOTE = '"';
	private static final char BACKSLASH = '\\';
	public static String[] splitCommandLine(String str, boolean extract) {
		List list = new ArrayList();
		int slen;
		char c;
		int i=0;
		int tokstart=0;

		if ((str == null) || ((str = str.trim()).length() == 0)) {
			return new String[0];
		}

		slen = str.length();

		while (true) {
			if (i < slen) {
				c = str.charAt(i);
			}
			else {
				c = '\0';
			}

			if (c == BACKSLASH) {
				i++;
				if (i < slen) {
					i++;
				}
			}
			else if (c == QUOTE) {
				i = skipSingleQuoted(str, slen, ++i);
			}
			else if (c == DOUBLEQUOTE) {
				i = skipDoubleQuoted(str, slen, ++i);
			}
			else if ((c == '\0') || spctabnl(c)) {
				String token = str.substring(tokstart, i);
				if (extract) {
					token = extractQuoted(token);
				}
				list.add(token);
				while ((i < slen) && spctabnl(str.charAt(i))) {
					i++;
				}
				if (i < slen) {
					tokstart = i;
				}
				else {
					break;
				}
			}
			else {
				i++;
			}
		}
		return (String[])list.toArray(new String[list.size()]);
	}
	public static String extractQuoted(String str) {
		if (str.length() == 0) {
			return str;
		}
		if (str.charAt(0) == QUOTE) {
			str = extractSingleQuoted(str);
		}
		else if (str.charAt(0) == DOUBLEQUOTE) {
			str = extractDoubleQuoted(str);
		}
		return str;
	}
	private static String extractDoubleQuoted(String str) {
		int slen = str.length();
		int i=0;
		int pass_next=0;
		int dquote=0;
		StringBuffer temp = new StringBuffer(slen);

		while (i < slen) {
			char c = str.charAt(i);
			if (pass_next != 0) {
				if (dquote == 0) {
					temp.append('\\');
				}
				pass_next = 0;
				temp.append(c);
			}
			else if (c == BACKSLASH) {
				pass_next++;
			}
			else if (c != DOUBLEQUOTE) {
				temp.append(c);
			}
			else {
				dquote ^= 1;
			}
			i++;
		}

		if (dquote != 0) {
			throw new IllegalArgumentException("Unbalanced quotation marks");
		}

		return temp.toString();
	}

	private static String extractSingleQuoted(String str) {
		char first = str.charAt(0);
		char last = str.charAt(str.length()-1);
		if (first == QUOTE) {
			if (last == QUOTE) {
				return str.substring(1, str.length()-1);
			}
			else {
				throw new IllegalArgumentException("Unbalanced quotation marks");
			}
		}
		else {
			return str;
		}
	}
	private static boolean spctabnl(char c) {
		return (c == ' ') || (c == '\t') || (c == '\n');
	}

	private static int skipSingleQuoted(String str, int slen, int sind) {
		int i = sind;

		while ((i < slen) && str.charAt(i) != QUOTE) {
			i++;
		}
		if (i < slen) {
			i++;
		}
		return i;
	}

	private static int skipDoubleQuoted(String str, int slen, int sind) {
		int i = sind;
		int pass_next = 0;

		while (i < slen) {
			char c = str.charAt(i);
			if (pass_next != 0) {
				pass_next = 0;
				i++;
			}
			else if (c == BACKSLASH) {
				pass_next++;
				i++;
			}
			else if (c != DOUBLEQUOTE) {
				i++;
			}
			else {
				break;
			}
		}

		if (i < slen) {
			i++;
		}
		return i;
	}

}