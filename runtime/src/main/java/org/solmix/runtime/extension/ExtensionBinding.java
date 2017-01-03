package org.solmix.runtime.extension;

import java.util.HashSet;
import java.util.Set;

public abstract class ExtensionBinding {
	private final Set<ExtensionInfo> extensionInfos = new HashSet<ExtensionInfo>();

	public Set<ExtensionInfo> getExtensionInfos() {
		return extensionInfos;
	}

	protected abstract void bind();

	protected void bind(Class<?> clazz) {
		ExtensionInfo ei = new ExtensionInfo(clazz);
		ei.setDeferred(true);
		extensionInfos.add(ei);
	}

	protected void bind(Class<?> intf, Class<?> clazz) {
		bind(intf, clazz, true);
	}

	protected void bind(Class<?> intf, Class<?> clazz, boolean deferred) {
		ExtensionInfo ei = new ExtensionInfo(clazz);
		ei.setDeferred(deferred);
		if (clazz.isAssignableFrom(intf)) {
			ei.setInterfaceName(intf.getName());
		}
		extensionInfos.add(ei);
	}

}
