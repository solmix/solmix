package org.solmix.test.performance;

import org.solmix.runtime.SystemContext;
import org.solmix.runtime.SystemContextFactory;

public class ThreadContext {

	public static void main(String[] args) {
		 SystemContext sc=SystemContextFactory.getDefaultSystemContext(false);
		 System.out.print(sc);

	}

}
