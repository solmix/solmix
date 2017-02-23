package org.solmix.runtime.transaction.support;

import java.io.IOException;

import org.solmix.runtime.resource.InputStreamResource;
import org.solmix.runtime.resource.ResourceResolver;

public class TransactionProxyResolver implements ResourceResolver {

	@Override
	public <T> T resolve(String resourceName, Class<T> resourceType) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public InputStreamResource getAsStream(String location) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public InputStreamResource[] getAsStreams(String locationPattern)
			throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

}
