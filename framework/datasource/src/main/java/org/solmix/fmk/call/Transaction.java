
package org.solmix.fmk.call;

import org.solmix.api.call.DSCall;
import org.solmix.api.call.DSCallManager;
import org.solmix.api.call.DSCallManagerFactory;
import org.solmix.api.datasource.DSRequest;
import org.solmix.api.datasource.DSResponse;
import org.solmix.api.exception.SlxException;
import org.solmix.fmk.SlxContext;
import org.solmix.runtime.Context;
import org.solmix.runtime.SystemContext;

public class Transaction
{

    private DSCallImpl dsc;

    private boolean isCreated;

    public Transaction(DSCall dsc, final SystemContext sc)
    {
        if (dsc == null) {
            DSCallManagerFactory factory = sc.getBean(DSCallManagerFactory.class);
            if (factory != null) {
                try {
                    DSCallManager manager = factory.createSimpleDSCallManager();
                    dsc = manager.getDSCall();
                    Context c = SlxContext.getContext();
                    if(c!=null){
                    	dsc.setRequestContext(c);
                    }
                } catch (SlxException e) {
                    // Ignore
                }
                
            }
        }
        if (DSCallImpl.class.isAssignableFrom(dsc.getClass())) {
            isCreated = true;
            this.dsc = DSCallImpl.class.cast(dsc);
        }

    }

    public Transaction(final SystemContext sc) throws SlxException
    {
        this(null, sc);
    }

    public void begin() throws SlxException {
        dsc.beginTransaction();

    }

    public void rollback() throws SlxException {
        dsc.rollback();
    }

    public DSResponse execute(final XAOp op) throws SlxException {
        return dsc.execute(op);

    }

    public DSResponse execute(DSRequest request) throws SlxException {
        return dsc.transactionExecute(request);

    }

    public void end() {
        dsc.endTransaction();
        if (isCreated)
            dsc = null;
    }

}
