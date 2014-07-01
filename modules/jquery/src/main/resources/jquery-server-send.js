/*******************************************************************
 * Copyright (c) 2008-2030 积成电子股份有限公司.
 * All rights reserved. 
 * 
 * 功能:Jquery插件,统一处理Ajax客户端和服务端的交互
 * 
 * @author fufuzhong@ieslab.cn
 * @version 1.0.0
 * @modification 
 * 版本管理:BugFix版本  1.0.x
 * 		   新功能版本  1.1.x
 * 		   新构架版本  2.x.x
 *******************************************************************/
var slx=window.slx?window.slx:{};slx.msg={};
slx.msg.nods='没有配置数据源!';
slx.msg.notype='没有指定操作类型!';
slx.msg._1='获取服务器数据失败!';
slx.msg._3='无权限!';
slx.msg._4='服务端验证错误!';
slx.msg._5='登陆错误!';
slx.msg._6='超过最大尝试登陆!';
slx.msg._7='需要登陆!';
slx.msg._8='登陆成功!';
slx.msg._10='提交事物失败!';
slx.REQUEST_PREFIX="/data/bin/";
slx.REQUEST_SUFFIX=".ds";
slx.TX_NUM=0;
slx._ns='http://www.solmix.org/xmlns/requestdata/v1.0.1';
slx._xsi='http://www.w3.org/2001/XMLSchema-instance';

//get request url form requestdata
slx.Root='<%=request.getContextPath()%>';
slx.logFailedMsg=function(status){
	var msg=slx.msg;
	switch (status){
	case -1:
		alsert(msg._1);
	case -3:
		alsert(msg._3);
	case -4:
		alsert(msg._4);
	case -5:
		alsert(msg._5);
	case -6:
		alsert(msg._6);
	case -7:
		alsert(msg._7);
	case -8:
		alsert(msg._8);
	case -10:
		alsert(msg._10);
	}
};
slx.getURL=function(data){
	if(data==null){
		alert("request is null!");
		return;
	}
	var msg=slx.msg;
	if(data.ds==null||data.ds==undefined){
		alert(msg.nods);
		return;
	}
	if(data.opType==null||data.opType==undefined){
		alert(msg.notype);
		return;
	}
	return slx.Root+slx.REQUEST_PREFIX+data.opType+'/'+data.ds+REQUEST_SUFFIX;
};
slx._buildxml=function(_1){
	var msg=slx.msg;
	var x='<elem>';
	if(_1.appID)
		x+='<appID>'+_1.appID+'</appID>';
	if(_1.componentId)
		x+='<componentId>'+_1.componentId+'</componentId>';
	if(_1.startRow)
		x+='<startRow>'+_1.startRow+'</startRow>';
	if(_1.endRow)
		x+='<endRow>'+_1.endRow+'</endRow>';
	if(_1.totalRow)
		x+='<totalRow>'+_1.totalRow+'</totalRow>';
	if(_1.sortBy)
		x+='<sortBy>'+_1.sortBy+'</sortBy>';
	if(_1.repo)
		x+='<repo>'+_1.repo+'</repo>';
	if(_1.textMatchStyle)
		x+='<textMatchStyle>'+_1.textMatchStyle+'</textMatchStyle>';
	//ajax不支持后台导出
	if(_1.ds)
		x+='<dataSource>'+_1.ds+'</dataSource>';
	else
		alert(msg.nods);
	if(_1.opType)
		x+='<operationType>'+_1.opType+'</operationType>';
	else
		alert(msg.notype);
	if(_1.opId)
		x+='<operationId>'+_1.opId+'</operationId>';
	if(_1.criteria){
		var criteria=_1.criteria;
		if($.isPlainObject(criteria)){
			x+='<criteria><map>';
			for(c in criteria){
				if(typeof(criteria[c])=="function"){
					criteria[c];
				}else{
					x+='<'+c+'>'+criteria[c]+'</'+c+'>';
				}
			}
			x+='</map></criteria>';
		}
	}
	if(_1.values){
		var values=_1.values;
		if($.isPlainObject(values)){
			x+='<values><map>';
			for(c in values){
				if(typeof(values[c])=="function"){
					values[c];
				}else{
					x+='<'+c+'>'+values[c]+'</'+c+'>';
				}
			}
			x+='</map></values>';
		}
	}
	//自定义jquery ajax不支持oldValues
	x+='</elem>';
	return x;
	
};
//add jquery global function used to send ajax to server.
jQuery.send=function(xml,callback){
	var transaction={};
	var _1=slx;
	var reqUrl;
	if($.isArray(xml)){
		reqUrl=_1.getURL(xml[0]);
	}else{
		reqUrl=_1.getURL(xml);
	}
	slx.TX_NUM=slx.TX_NUM+1;
	var data='<transaction  xmlns="'+_1._ns+'" xmlns:xsi="'+_1._xsi+'">';
	data+='<transactionNum>'+slx.TX_NUM+'</transactionNum><operations>';
	if($.isArray(xml)){
		for(  f in xml){
			data+=_1._buildxml(f);
		}
	}else{
		data+=_1._buildxml(xml);
	}
	data+='</operations></transaction>';
	transaction.__payload=data;
	$.post(reqUrl,transaction, function(response){
		//var dataObj=$.parseJSON(response);
		var res=response.response;
		var status=res.status;
		if(status==0){
			callback(res.data);
		}else{
			slx.logFailedMsg(status);
		
		}
	});
};