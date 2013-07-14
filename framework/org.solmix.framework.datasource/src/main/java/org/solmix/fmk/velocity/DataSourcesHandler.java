
package org.solmix.fmk.velocity;

import org.solmix.fmk.datasource.DefaultDataSourceManager;

/**
 * get data source
 * 
 * @author solmix.f@gmail.com.cn
 * @since 0.0.1
 * @version 110047 0.1.1
 * 
 */
public class DataSourcesHandler 
{

    public DataSourcesHandler()
    {
    }

    public Object get(Object key) {
        try {
            return DefaultDataSourceManager.getDataSource(key != null ? key.toString() : null);
        } catch (Exception e) {
            return null;
        }
    }
}
