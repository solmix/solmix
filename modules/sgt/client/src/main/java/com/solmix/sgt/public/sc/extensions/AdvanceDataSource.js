var slx=window.slx?window.slx:{};
slx.transactionNum=0;
slx.restURLPrefix="data";
isc.A.dataFormatParamName="slx_dataFormat";
isc.B._maxIndex=isc.C+2;
isc.defineClass("AdvanceDataSource",RestDataSource);
isc.A=isc.RestDataSource.getPrototype();
isc.B=isc._allFuncs;
isc.C=isc.B._maxIndex;
isc.D=isc._funcClasses;
isc.D[isc.C]=isc.A.Class;
isc.A.dataFormat="json";
isc.A.operationBindings=[ {operationType:"fetch",dataProtocol:"postMessage"},  {operationType:"add",dataProtocol:"postMessage"}, {operationType:"remove",dataProtocol:"postMessage"},  {operationType:"update",dataProtocol:"postMessage"}];
isc.B.push(isc.A.transformRequest=function isc_AdvanceDataSource_transformRequest(_1){
	var _2=this.getDataProtocol(_1);
	_1.dataProtocol="postMessage";
	this.dataProtocol="postMessage";
	//only used postmessage format to server.
	_2="postMessage";
	_1.isRestRequest=!(this.disableQueuing||this.clientOnly);
	_1.dataFormat=this.dataFormat;
	if(_2=="postMessage"){
		if(_1.params==null){
			_1.params={}
		}
		_1.params[this.dataFormatParamName]=this.dataFormat;
		slx.transactionNum=slx.transactionNum+1;
		var _3={transactionNum:slx.transactionNum};
		if(this.jscallback)
			_3.jscallback=this.jscallback;
		if(this.omitNullMapValuesInResponse)
			_3.omitNullMapValuesInResponse=omitNullMapValuesInResponse;
		var _a={};_3.operations={elem:[_a]};
		_a.appID="defaultApplication";
		if(_1.componentId)
			_a.componentId=_1.componentId;
		if(_1.operationId)
			_a.operationId=_1.operationId;
		if(this.outputs)
			_a.outputs=this.outputs;
		if(_1.startRow!=null)
			_a.startRow=_1.startRow;
		if(_1.endRow!=null)
			_a.endRow=_1.endRow;
		if(_1.sortBy!=null)
			_a.sortBy=_1.sortBy;
		if(_1.textMatchStyle!=null)
			_a.textMatchStyle=_1.textMatchStyle;
		if(_1.parentNode!=null)
			_a.parentNode=isc.Tree.getCleanNodeData(_1.parentNode);
		if(_1.requestId)
			_a.requestId=_1.requestId;
		/* if(_1.exportResults)
			_a.exportResults=_1.exportResults;
		if(_1.exportAs)
		_a.exportAs=_1.exportAs;
		if(_1.exportFilename)
		_a.exportFilename=_1.exportFilename;
		if(_1.lineBreakStyle)
		_a.lineBreakStyle=_1.lineBreakStyle;
		if(_1.exportDelimiter)
		_a.exportDelimiter=_1.exportDelimiter;
		if(_1.exportTitleSeparatorChar)
		_a.exportTitleSeparatorChar=_1.exportTitleSeparatorChar;
		if(_1.exportDisplay)
		_a.exportDisplay=_1.exportDisplay;
		if(_1.exportHeader)
		_a.exportHeader=_1.exportHeader;
		if(_1.exportFooter)
		_a.exportFooter=_1.exportFooter; */
		_a.dataSource=this.getID().replace("$","/");
		if(_1.operationType!=null)
			_a.operationType=_1.operationType;
		if(this.repo)
			_a.repo=this.repo;
		
		if(isc.DataSource.get("$89s")==null){
			isc.DataSource.create({
				ID:"$89s",
					fields:[{name:"_constructor",xmlAttribute:false},{name:"criteria",multiple:true,type:"$89s",childTagName:"criterion"},{name:"oldValues"}]
			})}
		var _4=isc.DataSource.create({fields:[{name:"data",type:"$89s"},{name:"oldValues"}]});
		if(this.autoConvertRelativeDates==true){
			if(this.logIsInfoEnabled("relativeDates")){
				this.logInfo("Calling convertRelativeDates from getServiceInputs - data is\n\n"+isc.echoFull(_1.data))}
			var _5=this.convertRelativeDates(_1.data);
			if(this.logIsInfoEnabled("relativeDates")){
				this.logInfo("Called convertRelativeDates from getServiceInputs - data is\n\n"+isc.echoFull(_5))}
		_1.data=_5}
		var _t=_1.operationType;
		if(_t=="fetch"||_t=="remove"){_a.criteria=_1.data;}else if(_t=="update"||_t=="add"){_a.values=_1.data;}
		
		if(_1.oldValues)
		_a.oldValues=_1.oldValues;
		if(!_1.contentType){
			_1.contentType=(this.dataFormat=="json"?"application/json":"text/xml")}
		var _6;
		_1.$92v=_1.data;
		if(this.dataFormat=="json"){
			if(_3.data!=null)
				_3.data=this.serializeFields(_3.data);
			if(_3.oldValues!=null)
				_3.oldValues=this.serializeFields(_3.oldValues);
			var _7={prettyPrint:this.prettyPrintJSON};
			_6=isc.JSON.encode(_3,_7)
		}else{
			var _8={ignoreConstructor:true,schema:this};
			_6=_4.xmlSerialize(_3,_8,null,"request")
		}
		_4.destroy();
		//alert(_6);
		return _6
	}else{
		if(_2!="getParams"&&_2!="postParams"){
			this.logWarn("RestDataSource operation:"+_1.operationID+", of type "+_1.operationType+" has dataProtocol specified as '"+_2+"'. Supported protocols are 'postParams', 'getParams' and 'postMessage' only. Defaulting to 'getParams'.");
			_1.dataProtocol='getParams'}
		var _3=isc.addProperties({},_1.data,_1.params);
		if(this.sendMetaData){
			if(!this.parameterNameMap){
				var _9={};
				_9[this.metaDataPrefix+"operationType"]="operationType";
				_9[this.metaDataPrefix+"operationId"]="operationId";
				_9[this.metaDataPrefix+"startRow"]="startRow";
				_9[this.metaDataPrefix+"endRow"]="endRow";
				_9[this.metaDataPrefix+"sortBy"]="sortBy";
				_9[this.metaDataPrefix+"useStrictJSON"]="useStrictJSON";
				_9[this.metaDataPrefix+"textMatchStyle"]="textMatchStyle";
				_9[this.metaDataPrefix+"oldValues"]="oldValues";
				_9[this.metaDataPrefix+"componentId"]="componentId";
				_9[this.metaDataPrefix+"parentNode"]="parentNode";
				this.parameterNameMap=_9
		}
		for(var _10 in this.parameterNameMap){
			var _11=_1[this.parameterNameMap[_10]];
			if(_11!=null){
				if(_10=="$97q"){
					_3[_10]=isc.Tree.getCleanNodeData(_11)
				}else{_3[_10]=_11}
			}
		}
		_3[this.metaDataPrefix+"dataSource"]=this.getID();
		_3["isc_metaDataPrefix"]=this.metaDataPrefix
	}
	_3[this.dataFormatParamName]=this.dataFormat;return _3
}
},
isc.A.transformResponse=function isc_AdvanceDataSource_transformResponse(_1,_2,_3){
	  if(_1.status<0||!_3){_1.queueStatus=-1;return _1}
if(this.dataFormat=="json"){
if(isc.isAn.Array(_3)){
var _4=_3.length==1&&_3[0]&&_3[0].response!=null;
this.logWarn("RestDataSource transformResponse(): JSON response text is incorrectly formatted as an Array rather than a simple response object."+(_4?" Array contains a single entry which appears to be a validly formatted response object - using this.":""));
if(_4)_3=_3[0]}
else if(_3.response==null){
this.logWarn("RestDataSouce transformResponse(): JSON response text does not appear to be in standard response format.")
}

var _5=_3.response||{};
_1.status=this.getValidStatus(_5.status);
_1.queueStatus=this.getValidStatus(_5.queueStatus);
if(_1.status==isc.DSResponse.STATUS_VALIDATION_ERROR){
var _6=_5.errors;if(isc.isAn.Array(_6)){
	if(_6.length>1){this.logWarn("server returned an array of errors - ignoring all but the first one")
}
_6=_6[0]}
_1.errors=_6;
if(_5.data!=null)
	_1.data=_5.data
}else if(_1.status<0){
	if(_5.data!=null){
		_1.data=_5.data
	}else{
		var _6=_5.errors;if(isc.isAn.Array(_6)){
			if(_6.length>1){this.logWarn("server returned an array of errors - ignoring all but the first one")
		}
		_6=_6[0]}
		_1.data=_6;
	}
	_1.totalRows=_1.startRow;_1.endRow=_1.startRow
}
if(_5.totalRows!=null)
_1.totalRows=_5.totalRows;
if(_5.startRow!=null)
_1.startRow=_5.startRow;
if(_5.endRow!=null)
_1.endRow=_5.endRow;
if(_5.totalRows!=null)
	_1.totalRows=_5.totalRows;
}else{
if(_2.clientOnly)return _1;_1.status=this.getValidStatus(_3.selectString("//status"));_1.queueStatus=this.getValidStatus(_3.selectString("//queueStatus"));
if(_1.status==isc.DSResponse.STATUS_VALIDATION_ERROR){var _6=_3.selectNodes("//errors");_6=isc.xml.toJS(_6);
if(_6.length>1){this.logWarn("server returned an array of errors - ignoring all but the first one")}
_6=_6[0];_1.errors=_6;var _7=_3.selectString("//data");if(_7)_1.data=_7}else if(_1.status<0){_1.data=_3.selectString("//data")}
var _8=_3.selectNumber("//totalRows");if(_8!=null)_1.totalRows=_8;var _9=_3.selectNumber("//startRow");if(_9!=null)_1.startRow=_9;var _10=_3.selectNumber("//endRow");if(_10!=null)_1.endRow=_10}
return _1},
isc.A.getDataURL=function isc_AdvanceDataSource_getDataURL(_1){
	var _2=_1.operationType;
	if(_2=="fetch"&&this.fetchDataURL!=null)
		return this.fetchDataURL;
	if(_2=="update"&&this.updateDataURL!=null)
		return this.updateDataURL;
	if(_2=="add"&&this.addDataURL!=null)
		return this.addDataURL;
	if(_2=="remove"&&this.removeDataURL!=null)
		return this.removeDataURL;
	if(_1.dataSource!=null&&_1.operationType!=null){
		return url=slx.restURLPrefix+"/bin/"+_1.operationType+"/"+_1.dataSource.replace("$","/")+".ds";
	}
	return this.Super("getDataURL",arguments)});