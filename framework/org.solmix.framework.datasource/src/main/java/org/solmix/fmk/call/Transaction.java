
package org.solmix.fmk.call;

import org.solmix.api.call.DSCall;
import org.solmix.api.call.DSCallManager;
import org.solmix.api.call.DSCallManagerFactory;
import org.solmix.api.context.SystemContext;
import org.solmix.api.datasource.DSRequest;
import org.solmix.api.datasource.DSResponse;
import org.solmix.api.exception.SlxException;

public class Transaction
{

    private DSCallImpl dsc;

    private boolean isCreated;

    public Transaction(DSCall dsc, SystemContext sc)
    {
        if (dsc == null) {
            DSCallManagerFactory factory = sc.getBean(DSCallManagerFactory.class);
            if (factory != null) {
                try {
                    DSCallManager manager = factory.createSimpleDSCallManager();
                    dsc = manager.getDSCall();
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

    public Transaction(SystemContext sc) throws SlxException
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
