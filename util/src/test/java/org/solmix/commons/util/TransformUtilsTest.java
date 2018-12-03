package org.solmix.commons.util;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.solmix.commons.util.TransformUtils.Transformer;

public class TransformUtilsTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testPrimitiveLong() {
		Long starder = new Long(1);
		long l = TransformUtils.transform(new Long(1), Long.TYPE);
		Assert.assertEquals(l, 1);

		Long l1 = TransformUtils.transform(new Long(1), Long.class);
		Assert.assertTrue(l1.equals(new Long(1)));

		Long l2 = TransformUtils.transform("1", Long.class);
		Assert.assertTrue(l2.equals(starder));

		Long l3 = TransformUtils.transform(new BigInteger("1"), Long.class);
		Assert.assertTrue(l3.equals(starder));

		Long l4 = TransformUtils.transform(new ArrayList<>(), Long.class);
		Assert.assertTrue(l4 == null);
	}

	@Test
	public void testForEach() {
		List<String> list = new ArrayList<String>();
		list.add("1");
		Collection<Long> tlist = TransformUtils.forEach(list, new Transformer<String, Long>() {

			@Override
			public Long transform(String obj) throws TransformException {
				return TransformUtils.transform(obj, Long.class);
			}
		});
		Assert.assertEquals(1, tlist.size());
		Collection<Long> tlist2 = TransformUtils.forEach(Collections.emptyList(), new Transformer<Object, Long>() {

			@Override
			public Long transform(Object obj) throws TransformException {
				return TransformUtils.transform(obj, Long.class);
			}
		});
		Assert.assertTrue(tlist2 instanceof List);

	}

}
