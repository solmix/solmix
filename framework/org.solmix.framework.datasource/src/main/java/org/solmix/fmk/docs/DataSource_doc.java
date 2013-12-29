/*
 * ========THE SOLMIX PROJECT=====================================
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.gnu.org/licenses/ 
 * or see the FSF site: http://www.fsf.org. 
 */

package org.solmix.fmk.docs;

import org.solmix.api.datasource.DataSourceData;


/**
 * @author solmix.f@gmail.com
 * @since 0.0.1
 * @version 110035 2011-2-5 solmix-ds
 */
public class DataSource_doc
{

    /**
     * <table border=1 >
     * <tr>
     * <td COLSPAN="2" width="100%" align="center" style="background-color : #BBFFFF;font-size:14pt">数据源(DataSource)</td>
     * </tr>
     * *
     * <tr>
     * <td COLSPAN="2" width="100%" align="left" style="background-color : #BBFFFF;font-size:12pt">
     * <li>实现流程</td>
     * </tr>
     * <tr>
     * <td>
     * 
     * 数据源的开始与数据源管理接口服务 {@link org.solmix.api.datasource.DataSourceManagerService DataSourceManagerService} 默认的接口实现为
     * {@link org.solmix.fmk.datasource.DataSourceManager DataSourceManager}. 通过数据源管理服务的方法
     * {@link org.solmix.fmk.datasource.DataSourceManager#get(String) get()} 获取数据源。在生成环境下，数据源从通过数据源池
     * {@link org.solmix.fmk.pool.PoolManager PoolManager} 中获取，PoolManager 是一个池管理器，通过实现{@link org.solmix.api.pool}
     * 下定义的接口来完成具体的池管理功能。数据源管理池的默认实现是 {@link org.solmix.fmk.datasource.PoolableDataSourceFactory
     * PoolableDataSourceFactory}。池管理器第一次通过调用
     * {@link org.solmix.api.pool.SlxKeyedPoolableObjectFactory#makeUnpooledObject(Object) makeUnpooledObject}
     * 往数据源池中加载对象。{@link org.solmix.fmk.datasource.DataSourceProvider#forName(String) DataSourceProvider.forName}
     * 的任务就是为其提供数据源。 数据源的初始化通过三个步骤完成：
     * <li>前置处理器：加载和初始化配置
     * <li>自定义处理器：根据各个数据源的特性，对数据做处理
     * <li>后置处理器：对所有的数据进行统一处理，以提供一个可用的数据源配置集合。 {@link org.solmix.fmk.datasource.DataSourceProvider#forName(String)}
     * responsible to provided datasource Before {@link org.solmix.api.datasource.DataSource#init(DataSourceData)}
     * parser the xml-format data by {@link org.solmix.api.datasource.ParserHandler}</td>
     * </tr>
     * <tr>
     * <td COLSPAN="2" width="100%" align="left" style="background-color : #BBFFFF;font-size:12pt">
     * <li>各种数据源可用的数据项定义</td>
     * </tr>
     * <tr>
     * <td COLSPAN="2" width="100%" align="center" style="background-color : #BBFFFF;font-size:10pt">通用（BASIC)数据源</td>
     * </tr>
     * <tr>
     * <td COLSPAN="2" width="100%" align="left" >
     * 通用（basic)数据源和自定义（custom)数据源的区别在于:通用数据源在初始化时，需要处理：自动生成骨架（schema）、处理数据源继承、依赖、权限、字段（Fields）和基本的验证功能。 <br>
     * 而自定义数据源不经过这些流程，仅仅是将配置缓存在数据源上下文环境中（data source context） <br>
     * serverType不填,默认为basic。</td>
     * </tr>
     * <tr>
     * <td >通用</td>
     * <td>{@link org.solmix.api.jaxb.TdataSource}</td>
     * </tr>
     * <tr>
     * <td COLSPAN="2" width="100%" align="center" style="background-color : #BBFFFF;font-size:10pt">SQL数据源</td>
     * </tr>
     * <tr>
     * <td >通用</td>
     * <td>{@link org.solmix.api.jaxb.TdataSource}</td>
     * </tr>
     * 
     * <tr>
     * <td COLSPAN="2">通用</td>
     * </tr>
     * <tr>
     * <td COLSPAN="2" width="100%" align="center" style="background-color : #BBFFFF;font-size:10pt">通过service的方式实现自定义操作
     * </td>
     * </tr>
     * <tr>
     * <td COLSPAN="2">可以通过定义serverType为custom 《下面各种绑定操作类型可以任意定义》
     * <p>
     * serverType为基准类型，定义绑定操作类型为custom 也可以实现自定义service
     * <p>
     * 对于sql类型，绑定类型为基准类型，单可以设置customSQL属性来覆盖其它自动生成的语句</td>
     * </tr>
     * </table>
     */
   public void doc()
   {

   }
}
