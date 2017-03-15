package org.solmix.runtime.event;


public abstract class EventUtil {
	
	public static String getTopic(Class<?> eventClass){
		String name = eventClass.getName();
		name = name.replace('.', '/');
		if(name.indexOf('$')!=-1){
			name= name.replace('$', '/');
		}
		return name;
	}
	 public static void validateTopicName(String topic) {
	        char[] chars = topic.toCharArray();
	        int length = chars.length;
	        if (length == 0) {
	            throw new IllegalArgumentException("empty topic");
	        }
	        for (int i = 0; i < length; i++) {
	            char ch = chars[i];
	            if (ch == '/') {
	                // Can't start or end with a '/' but anywhere else is okay
	                if (i == 0 || (i == length - 1)) {
	                    throw new IllegalArgumentException("invalid topic: " + topic);
	                }
	                // Can't have "//" as that implies empty token
	                if (chars[i - 1] == '/') {
	                    throw new IllegalArgumentException("invalid topic: " + topic);
	                }
	                continue;
	            }
	            if (('A' <= ch) && (ch <= 'Z')) {
	                continue;
	            }
	            if (('a' <= ch) && (ch <= 'z')) {
	                continue;
	            }
	            if (('0' <= ch) && (ch <= '9')) {
	                continue;
	            }
	            if ((ch == '_') || (ch == '-')) {
	                continue;
	            }
	            throw new IllegalArgumentException("invalid topic: " + topic);
	        }
	    }
}
