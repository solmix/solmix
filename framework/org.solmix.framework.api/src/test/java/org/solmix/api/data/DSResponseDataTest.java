package org.solmix.api.data;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Test;

public class DSResponseDataTest {

	@Test
	public void test() {
		DSResponseData data = new DSResponseData();
		Map<String,String> a= new HashMap<String,String>();
		a.put("aa", "1");
		List list = new ArrayList();
		list.add(a);
		data.setData(list);
		
		Map<String,String> dd =data.getData(Map.class);
		Assert.assertEquals("1", dd.get("aa"));
		data.setData(a);
		dd =data.getData(Map.class);
		Assert.assertEquals("1", dd.get("aa"));
		Map a2= new HashMap();
		a2.put("s1", "s1");
		a2.put("s2", 1);
		a2.put("s3", 2);
		a2.put("s4", new Date());
		a2.put("s5", Long.valueOf(1111111111111l));
		data.setData(a2);
		Bean1 test =data.getData(Bean1.class);
		Assert.assertEquals("s1", test.getS1());
	}

}
