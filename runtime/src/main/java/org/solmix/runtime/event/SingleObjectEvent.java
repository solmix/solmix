package org.solmix.runtime.event;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class SingleObjectEvent<T> implements IEvent {

	public static final String CONTENT = "event.content";
	
	private final String topic;

	private final Map<String, Object> arguments;

	public SingleObjectEvent(String topic, T obj) {
		EventUtil.validateTopicName(topic);
		this.topic = topic;
		if(!(obj instanceof Serializable)){
			throw new IllegalArgumentException("Event object must be implements java.io.Serializable");
		}
		Map<String, Object>  dic= new HashMap<String, Object> (1);
		dic.put(CONTENT, obj);
		arguments=Collections.unmodifiableMap(dic);
	}

	@Override
	public Map<String, ?> getProperties() {
		return arguments;
	}

	@Override
	public Object getProperty(String name) {
		return arguments.get(name);
	}

	@SuppressWarnings("unchecked")
	public T getContent(){
		return (T)arguments.get(CONTENT);
	}
	
	@Override
	public String getTopic() {
		return topic;
	}

}
