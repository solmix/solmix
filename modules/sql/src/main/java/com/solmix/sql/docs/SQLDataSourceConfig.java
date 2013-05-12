package com.solmix.sql.docs;


/**
<style type="text/css">table{
    width: 100%;background-color: #6699ff;
    }
    h3{
    background-color: #003366; border: 1px solid rgb(204, 204, 204); padding: 5px 10px;
    }
    h2{
    background-color: #003366; border: 1px solid rgb(204, 204, 204); padding: 5px 10px;
    }
 h4{
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
<h2><span class="hh">SQL数据源配置</span></h2>

<h3><span class="hh">Field配置</span></h3>

<h4><span class="hh">Add类配置相关</span></h4>

<ul>
      <li>首先根据输入的参数来自动组装插入语句的 values域。</li>
      <li>然后根据Field的类型来覆盖参数中组装的value域</li>
      <li>如果配置了customInsertExpression，获取customInsertExpression的值覆盖以上的值。</li>
</ul>
 
 * @author solmix.f@gmail.com
 * @version $Id$  2013-3-7
 */
public interface SQLDataSourceConfig
{

}
