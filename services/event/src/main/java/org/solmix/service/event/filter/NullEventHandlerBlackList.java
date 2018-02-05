package org.solmix.service.event.filter;

import java.util.Collections;
import java.util.Set;

import org.solmix.runtime.event.IEventHandler;
import org.solmix.service.event.EventHandlerBlackList;

/**
 * 不起用黑名单功能
 * @author solmix
 *
 */
public class NullEventHandlerBlackList implements EventHandlerBlackList {

	@Override
	public void add(IEventHandler handler) {

	}

	@Override
	public boolean contains(IEventHandler handler) {
		return false;
	}

    @Override
    public Set<IEventHandler> getBlankListHandlers() {
        return Collections.emptySet();
    }

}
