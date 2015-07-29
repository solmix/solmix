
package org.solmx.service.template.support;


public interface TemplateSearchingStrategy {
    /** 取得用来缓存模板搜索结果的key。 */
    Object getKey(String templateName);

    /**
     * 查找template，如果找到，则返回<code>true</code>。
     * <p>
     * 可更改matcher参数中的模板名称和后缀。
     * </p>
     */
    boolean findTemplate(TemplateMatcher matcher);
}
