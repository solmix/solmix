package org.solmix.runtime.proxy.target;

import org.solmix.runtime.proxy.TargetClassAware;

public interface TargetSource extends TargetClassAware{

	@Override
	Class<?> getTargetClass();
	boolean isStatic() ;
	Object getTarget() throws Exception;
	
	void releaseTarget(Object target) throws Exception;
}
