package org.solmix.runtime.extension;

import org.solmix.commons.util.AntMatcher;
import org.solmix.runtime.Container;
import org.solmix.runtime.ContainerFactory;

public class ContainerReference {
	private String id;
	private Container ref;
	private String filter;
	private AntMatcher matcher = new AntMatcher();

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public synchronized Container getRef() {
		if (id != null && ref == null) {
			Container[] containers = ContainerFactory.getContainers();
			for (Container c : containers) {
				if (c.getId().equals(id)) {
					ref = c;
					break;
				}
			}
		}
		return ref;
	}

	public String getFilter() {
		return filter;
	}

	public void setFilter(String filter) {
		this.filter = filter;
	}

	public <T> boolean match(Class<T> type) {
		return matcher.match(filter, type.getName());

	}

}
