package org.solmix.commons.collections;

import java.util.Set;

public interface StringValueMap {
	public String getValue(String key);

	public void setValue(String key, String value);

	public Set<String> getKeys();
}
