package org.solmix.fmk.call;

import org.solmix.api.call.DSCManager;
import org.solmix.api.call.DSCManagerFactory;
import org.solmix.api.context.SystemContext;
import org.solmix.api.datasource.DSRequest;
import org.solmix.api.datasource.DSResponse;
import org.solmix.api.exception.SlxException;


public class Transaction
{
    
    private  DSCManagerImpl rpc;
    private boolean isCreated;
    public Transaction(DSCManager rpc,SystemContext sc){
        if(rpc==null){
            DSCManagerFactory factory=   sc.getBean(DSCManagerFactory.class);
            if(factory!=null){
                try {
                    rpc=factory.createRPCManager();
                } catch (SlxException e) {
                    //Ignore
                }
                if(rpc.getClass().isAssignableFrom(DSCManagerImpl.class)){
                    isCreated=true;
                    this.rpc=DSCManagerImpl.class.cast(rpc);
                }
                
            }
        }
        this.rpc=(DSCManagerImpl)rpc;
        
    }
    public Transaction(SystemContext sc) throws SlxException{
        this(null,sc);
    }
    public void begin(){
        rpc.beginTransaction();
        
    }
    public void rollback() throws SlxException{
        rpc.rollback();
    }
    
    public  DSResponse     execute(final XAOp op) throws SlxException {
        return rpc.execute(op);
        
    }
    
    public   DSResponse     execute(DSRequest request) throws SlxException {
        return rpc.transactionExecute(request);
        
    }
    
    public void end(){
        rpc.endTransaction();
        if(isCreated)
            rpc=null;
    }
    

}
