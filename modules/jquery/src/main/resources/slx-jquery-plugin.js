var slx=window.slx?window.slx:{};
slx.transactionNum=0;
slx._ns='http://www.solmix.org/xmlns/requestdata/v1.0.1';
slx._xsi='http://www.w3.org/2001/XMLSchema-instance';
//get request url form requestdata
slx.getURL=function(data){
	if(data==null){
		alert("request is null!");
		return;
	}
	if(data.ds==null||data.ds==undefined){
		alert("datasource must be setting!");
		return;
	}
	if(data.opType==null||data.opType==undefined){
		alert("datasource OperatioinType  must be setting!");
		return;
	}
	return '/data/bin/'+data.opType+'/'+data.ds+'.ds';
}
slx._buildxml=function(_1){
	var x='<elem>';
	if(_1.appID)
		x+='<appID>'+_1.appID+'</appID>';
	if(_1.componentId)
		x+='<componentId>'+_1.componentId+'</componentId>';
	if(_1.startRow)
		x+='<startRow>'+_1.startRow+'</startRow>';
	if(_1.endRow)
		x+='<endRow>'+_1.endRow+'</endRow>';
	if(_1.ds)
		x+='<dataSource>'+_1.ds+'</dataSource>';
	if(_1.opType)
		x+='<operationType>'+_1.opType+'</operationType>';
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
			//	x+='<'+criteria.c+'>';
				
			}
			
			x+='</map></criteria>';
		}
		
	}
		
			
	
	x+='</elem>';
	return x;
	
}
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
	slx.transactionNum=slx.transactionNum+1;
	var data='<transaction  xmlns="'+_1._ns+'" xmlns:xsi="'+_1._xsi+'">';
	data+='<transactionNum>'+slx.transactionNum+'</transactionNum><operations>';
	if($.isArray(xml)){
		for(  f in xml){
			data+=_1._buildxml(f);
		}
	}else{
		data+=_1._buildxml(xml);
	}
	//data+='<elem><dataSource>SYSINIT</dataSource><operationType>fetch</operationType><operationId>getMenu</operationId><criteria><map><aa>bb</aa></map></criteria></elem>';
	data+='</operations></transaction>';
	transaction._transaction=data;
	$.post(reqUrl,transaction, function(response){
		var dataObj=$.parseJSON(response);
		var res=dataObj.response;
		var status=res.status;
		if(status==0){
			callback(res.data)
		}else{
			alert(status);
		}
	});
}