package org.solmix.web.client;

/**
 * Interface to represent the messages contained in resource bundle:
 * 	M:/workspace/platform/core/trunk/solmix-web/solmix-app/src/main/resources/org/solmix/web/client/Constants.properties'.
 */
public interface Constants extends com.google.gwt.i18n.client.Messages {
  
  /**
   * Translated "zh_CN".
   * 
   * @return translated "zh_CN"
   */
  @DefaultMessage("zh_CN")
  @Key("default.locale")
  String default_locale();

  /**
   * Translated "80".
   * 
   * @return translated "80"
   */
  @DefaultMessage("80")
  @Key("default_menu_width")
  String default_menu_width();

  /**
   * Translated "Logo".
   * 
   * @return translated "Logo"
   */
  @DefaultMessage("Logo")
  @Key("logo")
  String logo();
}
