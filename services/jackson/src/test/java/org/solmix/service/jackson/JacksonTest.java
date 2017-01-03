package org.solmix.service.jackson;

import static org.junit.Assert.*;

import java.io.StringWriter;
import java.io.Writer;

import org.junit.Test;
import org.solmix.exchange.data.ObjectWriter;
import org.solmix.runtime.Container;
import org.solmix.runtime.Containers;

public class JacksonTest {

	@Test
	public void test() {
		Container c = Containers.getDefaultContainer();
		JacksonDataProcessor processer =Containers.injectResource(c, JacksonDataProcessor.class);
		processer.initialize(null);
		assertNotNull(processer);
		SerialedBean bean =getBean();
		ObjectWriter<Writer> writer =processer.createWriter(Writer.class);
		StringWriter sw =new StringWriter();
		writer.write(bean, sw);
		String text = sw.toString();
		System.out.println(text);
	}

	private SerialedBean getBean(){
		SerialedBean b =new SerialedBean();
		b.setInternal(new InternalBean("ressss"));
		b.setName("aaaaaaaaaaaname");
		b.setIgnore(new InternalBean("ignore"));
		b.setBool(true);
		return b;
	}
}
