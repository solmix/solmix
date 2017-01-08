package org.solmix.service.event;

import java.util.Hashtable;

import org.junit.Assert;
import org.junit.Test;
import org.osgi.framework.Filter;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;
import org.solmix.runtime.event.IEvent;
import org.solmix.service.event.filter.CachedTopicFilter;
import org.solmix.service.event.util.LeastRecentlyUsedCacheMap;

public class FilterTest {

	@Test
	public void test() {
		try {
			Cache<String, String> topicCache = new LeastRecentlyUsedCacheMap<String, String>(
					10);
			final TopicFilter topicFilter = new CachedTopicFilter(topicCache,
					true);
			String str = topicFilter.createFilter("/xxxx/topic");
			Filter filter = FrameworkUtil.createFilter(str);
			Hashtable<String,String> dic =getDic("/xxxx/topic");
			boolean matched = filter.match(dic);
			Assert.assertTrue(matched);
			Hashtable<String,String> dic1 =getDic("/xxxx/");
			Assert.assertTrue(!filter.match(dic1));
			Hashtable<String,String> dic2 =getDic("/xxxx/asd");
			Assert.assertTrue(!filter.match(dic2));
			Hashtable<String,String> dic3 =getDic("/xxxx/topicaac");
			Assert.assertTrue(!filter.match(dic3));
			Hashtable<String,String> dic4 =getDic("/xxxx/topic/aac");
			Assert.assertTrue(!filter.match(dic4));
		} catch (InvalidSyntaxException e) {
			e.printStackTrace();
		}
	}
	private Hashtable<String, String> getDic(String topic){
		Hashtable<String,String> dic = new Hashtable<String,String>();
		dic.put(IEvent.EVENT_TOPIC, topic);
		return dic;
	}

}
