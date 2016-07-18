package org.solmix.runtime.support.ext;

import java.util.HashSet;
import java.util.Set;

import org.solmix.runtime.Container;
import org.solmix.runtime.ContainerEvent;
import org.solmix.runtime.ContainerListener;
import org.solmix.runtime.extension.ExtensionContainer;
import org.solmix.runtime.extension.ExtensionInfo;

public abstract class LoadExtInfoListenerSupport implements ContainerListener {

	private Set<ExtensionInfo> infos=new HashSet<ExtensionInfo>();
	@Override
	public void handleEvent(ContainerEvent event) {
		switch (event.getType()) {
		case ContainerEvent.INITIALIZING:
			Container c=event.getContainer();
			if(!(c instanceof ExtensionContainer)){
				throw new IllegalStateException("LoadExtInfoListenerSupport only support ExtensionContainer");
			}
			loadExtensionInfoForContainer((ExtensionContainer)c);
			break;
		default:
			return;
		}

	}

	protected void loadExtensionInfoForContainer(ExtensionContainer c) {
		bind();
		for(ExtensionInfo info:infos){
			c.getExtensionManager().addLocalExtensionInfo(info);
		}
	}

	protected abstract void bind() ;
	
	protected void  bind(Class<?> clazz){
		infos.add(new ExtensionInfo(clazz));
	}

}
