
package org.solmix.advanceds.client;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.smartgwt.client.core.DataClass;

public class Request extends DataClass
{

    public Request()
    {
        this(false);
    }

    public Request(boolean useSmartRPC)
    {
        if (!useSmartRPC)
            setAttribute("transactionNum", getTransactionNum());
    }

    public native int getTransactionNum() /*-{
		try {
			$wnd.slx.transactionNum = $wnd.slx.transactionNum + 1
			return $wnd.slx.transactionNum;
		} catch (e) {
			$wnd.alert(e);
		}
    }-*/;

    public String getJscallback() {
        return getAttributeAsString("jscallback");
    }

    public void setJscallback(String value) {
        setAttribute("jscallback", value);
    }

    public Boolean isOmitNullMapValuesInResponse() {
        return getAttributeAsBoolean("omitNullMapValuesInResponse");
    }

    /**
     * Sets the value of the omitNullMapValuesInResponse property.
     * 
     * @param value allowed object is {@link Boolean }
     * 
     */
    public void setOmitNullMapValuesInResponse(Boolean value) {
        setAttribute("omitNullMapValuesInResponse", value);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void setRoperations(List<Roperation> operations) {
        Map map = new HashMap();
        map.put("elem", operations);
        this.setAttribute("operations", map);
    }

    public void setRoperations(Roperation... operations) {
        DataClass ops = new DataClass();
        ops.setAttribute("elem", operations);
        this.setAttribute("operations", ops);
    }
}
