
package org.solmix.web.client;

import org.solmix.web.client.gin.SolmixGinjector;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.CssResource.NotStrict;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;
import com.gwtplatform.mvp.client.DelayedBindRegistry;

public class Solmix implements EntryPoint
{

   interface GlobalResource extends ClientBundle
   {

      @NotStrictx
      @Source("Solmix.css")
      CssResource css();
   }
 public static final String URL_SCAN_PKG="org.solmix.web.client.sandbox.view.MenuConfigView";
   private static final SolmixGinjector ginjector = GWT.create(SolmixGinjector.class);

   private static Messages messages;

   private static Constants constants;
   public static final String USER_NAME_KEY="user_name";

   public void onModuleLoad()
   {
      // 这里使用延迟加载的方法，让界面在moduleLoad()中去加载，
      // 原因是：让日志模块能捕捉到在应用程序中的所有异常
      Log.setUncaughtExceptionHandler();
      Scheduler.get().scheduleDeferred(new ScheduledCommand() {

         public void execute()
         {
            moduleLoad();

         }
      });
      // DelayedBindRegistry.bind(ginjector);
      // ginjector.getPlaceManager().revealCurrentPlace();
      RootPanel.getBodyElement().removeChild(RootPanel.get("loadingWrapper").getElement());
   }

   private void moduleLoad()
   {
      try
      {
         if (!Log.isLoggingEnabled())
         {
            Window.alert("Logging is disabled.");
         }
         GWT.<GlobalResource> create(GlobalResource.class).css().ensureInjected();

         messages = GWT.create(Messages.class);
         constants = GWT.create(Constants.class);

         // this is required by gwt-platform proxy's generator
         DelayedBindRegistry.bind(ginjector);
         ginjector.getPlaceManager().revealCurrentPlace();
         // get Host Page name
         // Dictionary dictionary = Dictionary.getDictionary("Pages");
         // revealCurrentPlace(dictionary.get("page"));

      } catch (Exception e)
      {
         Log.error("Loading error:" + e.getMessage());
         e.printStackTrace();
         Window.alert(e.getMessage());
      }

   }

   public static Messages getMessages()
   {
      return messages;
   }

   public static Constants getConstants()
   {
      return constants;
   }
   public static SolmixGinjector getGinjector(){
      return ginjector;
   }
//   public static UrlContext getUrlContext(){
//      return ginjector.getUrlContext();
//   }
   public static String getRelativeURL(String url){
      String moduleBase="";
      if(GWT.isScript()){
          moduleBase = GWT.getModuleBaseURL();
      }
      return moduleBase+url;
   }

}
