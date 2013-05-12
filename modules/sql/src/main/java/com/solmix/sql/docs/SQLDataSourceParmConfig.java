/*
 * SOLMIX PROJECT
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

package com.solmix.sql.docs;

/**
 * 
<style type="text/css">table{
    width: 100%;background-color: #6699ff;
    }
    h3{
    background-color: #003366; border: 1px solid rgb(204, 204, 204); padding: 5px 10px;
    }
    h2{
    background-color: #003366; border: 1px solid rgb(204, 204, 204); padding: 5px 10px;
    }
      th{
      background-color:#6699ff;
      }
      td{
      background-color: rgb(249, 252, 252);
      }
      .title{
      color: #003366; font-weight: bold; text-align: center;
      }
    .hh{
    color: #ccffff; font-weight: bold; text-align: center;
    }
</style>
<h2><span class="hh">sql数据库连接相关配置</span></h2>

<h3><span class="hh">连接池配置</span></h3>

<p>与普通数据源连接池的不同 之处是：数据源可以用多种方式取得，并且多个数据源共享一个连接池。</p>

<p>默认使用数据源连接池，如果不使用可以通过配置*.used.pool=false来去除这一特性。</p>

<p><strong>注意如果使用了JNDI数据源，数据源本身已经是从数据源池中获取，设置*.used.pool=false</strong></p>

<p>连接池配置格式为：sql.param=*,其中param为下列参数。</p>

<table border="0" cellpadding="1" cellspacing="1">
      <tbody>
            <tr>
                  <th><span class="title">配置参数</span></th>
                  <th><span class="title">类型</span></th>
                  <th><span class="title">配置描述</span></th>
            </tr>
            <tr>
                  <td><strong>maxActive</strong></td>
                  <td>Integer</td>
                  <td>
                  <p>指明能从池中借出的对象的最大数目。如果这个值不是正数，表示没有限制。</p>
                  </td>
            </tr>
            <tr>
                  <td><strong>maxIdle</strong></td>
                  <td><span>Integer</span></td>
                  <td><span style="line-height: 1.6em;">指明能从池中最大空闲对象数</span></td>
            </tr>
            <tr>
                  <td><strong>maxWait</strong></td>
                  <td><span>Integer</span></td>
                  <td>指明若在对象池空时调用borrowObject方法的行为被设定成等待，最多等待多少毫秒。如果等待时间超过了这个数值，则会抛出一个java.util.NoSuchElementException异常。如果这个值不是正数，表示无限期等待。</td>
            </tr>
            <tr>
                  <td><strong>whenExhaustedAction</strong></td>
                  <td>String &nbsp;</td>
                  <td>
                  <p>指定在池中借出对象的数目已达极限的情况下，调用它的borrowObject方法时的行为。可以选用的值有：</p>

                  <p>GenericObjectPool.WHEN_EXHAUSTED_BLOCK，表示等待；<br />
                  GenericObjectPool.WHEN_EXHAUSTED_GROW，表示创建新的实例（不过这就使maxActive参数失去了意义）；<br />
                  GenericObjectPool.WHEN_EXHAUSTED_FAIL，表示抛出一个java.util.NoSuchElementException异常。</p>
                  </td>
            </tr>
            <tr>
                  <td><strong>testOnBorrow</strong></td>
                  <td>Boolean</td>
                  <td>定在借出对象时是否进行有效性检查。默认为true</td>
            </tr>
            <tr>
                  <td><strong>testWhileIdle</strong></td>
                  <td><span>Boolean</span></td>
                  <td>设定在进行后台对象清理时，是否还对没有过期的池内对象进行有效性检查。不能通过有效性检查的对象也将被回收。</td>
            </tr>
            <tr>
                  <td><strong>timeBetweenEvictionRunsMillis</strong></td>
                  <td><span>Integer</span></td>
                  <td>设定间隔每过多少毫秒进行一次后台对象清理的行动。如果这个值不是正数，则实际上不会进行后台对象清理。</td>
            </tr>
            <tr>
                  <td><strong>minEvictableIdleTimeMillis</strong></td>
                  <td><span>Integer</span></td>
                  <td>设定在进行后台对象清理时，视休眠时间超过了多少毫秒的对象为过期。过期的对象将被回收。如果这个值不是正数，那么对休眠时间没有特别的约束。</td>
            </tr>
            <tr>
                  <td><strong>numTestsPerEvictionRun</strong></td>
                  <td><span>Integer</span></td>
                  <td>设定在进行后台对象清理时，每次检查几个对象。如果这个值不是正数，则每次检查的对象数是检查时池内对象的总数乘以这个值的负倒数再向上取整的结果――也就是说，如果这个值是-2（-3、-4、-5&hellip;&hellip;）的话，那么每次大约检查当时池内对象总数的1/2（1/3、1/4、1/5&hellip;&hellip;）左右。</td>
            </tr>
            <tr>
                  <td><strong>enabled</strong></td>
                  <td><span>Boolean</span></td>
                  <td>是否使用对象连接池</td>
            </tr>
      </tbody>
</table>

<h3><span class="hh">SQL数据源一般配置</span></h3>

<p>配置格式为：databaseName.param。其中注明为OSGI环境，表明只能OSGI环境下才可用，没注明的可以在任意环境下使用</p>

<table border="0" cellpadding="1" cellspacing="1">
      <tbody>
            <tr>
                  <th><span class="title" style="font-weight: normal;"><font color="#333333">配置参数</font></span></th>
                  <th><span class="title" style="font-weight: normal;"><font color="#333333">类型</font></span></th>
                  <th><span class="title" style="font-weight: normal;"><font color="#333333">配置描述</font></span></th>
                  <th><span style="font-weight: normal;">备注</span></th>
            </tr>
            <tr>
                  <td>used.pool</td>
                  <td><span>Boolean</span></td>
                  <td>指明是否使用对象连接池，默认为true。</td>
                  <td colspan="1" rowspan="5">通用配置</td>
            </tr>
            <tr>
                  <td>interface.type</td>
                  <td>String</td>
                  <td>指明获得数据源的接口类型:
                  <p>{@link com.solmix.sql.EInterfaceType#DATASOURCE}</p>

                  <p>{@link com.solmix.sql.EInterfaceType#DRIVERMANAGER}</p>

                  <p>{@link com.solmix.sql.EInterfaceType#JNDI}</p>

                  <p>{@link com.solmix.sql.EInterfaceType#JNDIOSGI}</p>

                  <p>{@link com.solmix.sql.EInterfaceType#OSGI}</p>
                  </td>
            </tr>
            <tr>
                  <td>database.type</td>
                  <td>String</td>
                  <td>指明数据库类型：oracle,mysql,postgresql,sqlserver,db2,db2iSeries,hsqldb,generic,cache,::hibernate::。</td>
            </tr>
            <tr>
                  <td>pingTest</td>
                  <td><span style="background-color: rgb(249, 252, 252);">String</span></td>
                  <td>指明用于ping的sql语句。</td>
            </tr>
            <tr>
                  <td>autoJoinTransactions</td>
                  <td>String</td>
                  <td>指明是否允许数据库事物加入内置的自动事物机制，该值默认为&ldquo;true&rdquo;</td>
            </tr>
            <tr>
                  <td>jndiosgi</td>
                  <td>String</td>
                  <td>当interface.type设置为{@link com.solmix.sql.EInterfaceType#JNDIOSGI}时使用，指明数据库连接的url，例如：&nbsp;(osgi.jndi.service.name=jdbc/NoTxOracleDataSource)</td>
                  <td>OSGI+JNDI集成</td>
            </tr>
            <tr>
                  <td>osgi</td>
                  <td>String</td>
                  <td>当interface.type设置为{@link com.solmix.sql.EInterfaceType#OSGI}时使用，指明数据库连接的url.</td>
                  <td>OSGI</td>
            </tr>
            <tr>
                  <td>jndi</td>
                  <td>String</td>
                  <td>当interface.type设置为{@link com.solmix.sql.EInterfaceType#JNDI}时使用，指明数据库连接的url.例如：&nbsp;jdbc/RCCEEI2</td>
                  <td>JNDI查找的数据源</td>
            </tr>
            <tr>
                  <td>driver</td>
                  <td>String</td>
                  <td>当interface.type设置为{@link com.solmix.sql.EInterfaceType#DRIVERMANAGER}和<span style="background-color: rgb(249, 252, 252);">{@link com.solmix.sql.EInterfaceType#DATASOURCE}</span>时使用,为<span style="background-color: rgb(249, 252, 252);">DRIVERMANAGER时代表</span>数据库驱动，例如：&nbsp;oracle.jdbc.driver.OracleDriver，为<span style="background-color: rgb(249, 252, 252);">DATASOURCE为数据库默认的数据源，例如：oracle.jdbc.pool.</span><span style="background-color: rgb(249, 252, 252);">OracleDataSource</span></td>
                  <td colspan="1" rowspan="9">
                  <p>通过驱动获取连接时<span style="line-height: 1.6em;">需要配置的参数</span></p>
                  </td>
            </tr>
            <tr>
                  <td><span style="background-color: rgb(249, 252, 252);">interface.credentialsInURL</span></td>
                  <td><span style="background-color: rgb(249, 252, 252);">Boolean</span></td>
                  <td><span style="background-color: rgb(249, 252, 252);">使用</span><span style="background-color: rgb(249, 252, 252);">DRIVERMANAGER是，指明验证方式是否在url中。默认为false</span></td>
            </tr>
            <tr>
                  <td>driver.username</td>
                  <td>String</td>
                  <td>使用数据库驱动连接时的用户名</td>
            </tr>
            <tr>
                  <td>driver.password</td>
                  <td>String</td>
                  <td>使用数据库驱动连接时的密码</td>
            </tr>
            <tr>
                  <td>driver.url</td>
                  <td>String</td>
                  <td>使用数据库驱动连接时的连接URL</td>
            </tr>
            <tr>
                  <td><span style="background-color: rgb(249, 252, 252);">driver.</span>driverName</td>
                  <td><span style="background-color: rgb(249, 252, 252);">String</span></td>
                  <td>数据库名称，一般和<span style="background-color: rgb(249, 252, 252);">database.type相同</span></td>
            </tr>
            <tr>
                  <td>serverName</td>
                  <td><span style="background-color: rgb(249, 252, 252);">String</span></td>
                  <td>数据库主机url</td>
            </tr>
            <tr>
                  <td><span style="background-color: rgb(249, 252, 252);">driver.</span>portNumber</td>
                  <td>Integer</td>
                  <td>端口号</td>
            </tr>
            <tr>
                  <td>databaseName</td>
                  <td><span style="background-color: rgb(249, 252, 252);">String</span></td>
                  <td>数据库名称</td>
            </tr>
            <tr>
                  <td>log.enabled</td>
                  <td>Boolean</td>
                  <td><span style="background-color: rgb(249, 252, 252);">当interface.type设置为DATASOURCE是，可以设置该参数为true来输出日志。</span></td>
                  <td colspan="1">&nbsp;</td>
            </tr>
      </tbody>
</table>

 */

public interface SQLDataSourceParmConfig
{

}
