package com.solmix.sgt.client.widgets;

import com.smartgwt.client.widgets.Canvas;

public abstract class AbstractFactory implements WidgetFactory {

	  protected Object[] parameters;
	   protected Canvas container;

	   protected String id;

	   protected Canvas instance;
	   protected String desc="";

	   public void setParameters(Object... paramaters)
	   {
	      this.parameters = paramaters;
	   }
	  public void setContainer(Canvas containerTarget){
	       container=containerTarget;
	   }
	   public String getID()
	   {
	      if (instance == null)
	         instance = this.create();
	      if (instance != null)
	         id = instance.getID();
	      return id;
	   }

	   public String getDescription()
	   {
	      return desc;
	   }
	   /**
	    * {@inheritDoc}
	    * 
	    * @see com.solmix.sgt.widgets.solmix.web.client.WidgetFactory#getName()
	    */
	   @Override
	   public String getName() {
	      String clzName= this.getClass().getName();
	     return clzName.substring(0, clzName.lastIndexOf("$"));
	   }

}
