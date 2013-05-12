package com.solmix.fmk.docs;

import com.solmix.api.rpc.RPCManager;


/**
 * <style type="text/css">table{
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
<h2><span class="hh">全局事物及批量操作用法简介</span></h2>

<h3><span class="hh">全局事物原理</span></h3>

相关方法：{@link com.solmix.api.datasource.DSRequest#getJoinTransaction()}
{@link com.solmix.api.datasource.DSRequest#isPartOfTransaction()}

如果要使用批量事物处理必须在DSRequest 中设置Rpc {@link com.solmix.api.datasource.DSRequest#setRpc(RPCManager)}
<p>&nbsp;</p>

<p>&nbsp;</p>

 * @author solmix.f@gmail.com
 * @version $Id$  2013-3-25
 */
public interface Transaction_doc
{

}
