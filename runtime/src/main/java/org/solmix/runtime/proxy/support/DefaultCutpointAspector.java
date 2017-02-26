package org.solmix.runtime.proxy.support;

import org.solmix.runtime.proxy.Aspect;
import org.solmix.runtime.proxy.Aspector;

public class DefaultCutpointAspector implements Aspector {

	private Cutpoint cutpoint = Cutpoint.TRUE;
	private Aspect aspect;
	
	public DefaultCutpointAspector(){
		
	}
	public DefaultCutpointAspector(Aspect aspect) {
		this(Cutpoint.TRUE,aspect);
	}
	
	public DefaultCutpointAspector(Cutpoint cutpoint,Aspect advice) {
		this.cutpoint = cutpoint;
		setAspect(advice);
	}

	@Override
	public Aspect getAspect() {
		return aspect;
	}

	public void setAspect(Aspect aspect) {
		this.aspect = aspect;
	}
	@Override
	public boolean isPerInstance() {
		return true;
	}
	public Cutpoint getCutpoint() {
		return cutpoint;
	}
	public void setCutpoint(Cutpoint cutpoint) {
		this.cutpoint = cutpoint;
	}

}
