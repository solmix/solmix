package com.smartgwt.extensions.advanceds.client;




public class HiddenForm  {
	
	public HiddenForm() {
		createHiddenForm();
	}
	
	public native void createHiddenForm() /*-{
	try {
		if($wnd.document.getElementById("__slx_hidden_form")==null){
		var hiddenForm =$wnd.document.createElement("FORM");
		hiddenForm.width="0px";
		hiddenForm.height="0px";
		hiddenForm.style.display="none";
		hiddenForm.method="post";
		hiddenForm.id="__slx_hidden_form";
		hiddenForm.target="_self";
		hiddenForm.name="__slx_hidden_form";
		var hiddenParm= $wnd.document.createElement("INPUT");
		hiddenParm.type="hidden";
		hiddenParm.name="_transaction";
		hiddenParm.id="_transaction";
		hiddenForm.appendChild(hiddenParm);
		$wnd.document.body.appendChild(hiddenForm);
		}
	} catch (e) {
		$wnd.alert(e);
	}
}-*/;
	/*public  void setData(String data){
		Element e = Document.get().createElement("input");
		e.setAttribute("value", data);
		Window.alert(e.toString());
	}*/

	public native void setData(String data) /*-{
		try {
			//var str=data.replace(/</g,"&lt;").replace(/>/g,"&gt;").replace(/"/g,"&quot;")
			//$wnd.alert(str);
		var hiddenForm =$wnd.document.getElementById("__slx_hidden_form");
//		$wnd.document.getElementById("_transaction").value=data;
		hiddenForm._transaction.value=data;
	} catch (e) {
		$wnd.alert(e);
	}
	}-*/;

	public native void setAction(String act)  /*-{
	try {
		var hiddenForm =$wnd.document.getElementById("__slx_hidden_form");
		hiddenForm.action=act;
	} catch (e) {
		$wnd.alert(e);
	}
	
	}-*/;

	public native void submit()   /*-{
	try {
	var hiddenForm =$wnd.document.getElementById("__slx_hidden_form");
	hiddenForm.submit();
	} catch (e) {
		$wnd.alert(e);
	}
	}-*/;
		

	
	
}
