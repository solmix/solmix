package org.solmix.runtime.proxy;

import org.solmix.test.bean.ComplexBean;

public class ComplexBeanProxy extends ComplexBean {

	private ComplexBean proxy;
	public  ComplexBeanProxy(ComplexBean proxy){
		this.proxy=proxy;
	}
}
