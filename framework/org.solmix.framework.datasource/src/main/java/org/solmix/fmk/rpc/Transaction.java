package org.solmix.fmk.rpc;

import org.solmix.api.context.SystemContext;
import org.solmix.api.datasource.DSRequest;
import org.solmix.api.datasource.DSResponse;
import org.solmix.api.exception.SlxException;
import org.solmix.api.rpc.RPCManager;
import org.solmix.api.rpc.RPCManagerFactory;


public class Transaction
{
    
    private  RPCManagerImpl rpc;
    private boolean isCreated;
    public Transaction(RPCManager rpc,SystemContext sc){
        if(rpc==null){
            RPCManagerFactory factory=   sc.getBean(RPCManagerFactory.class);
            if(factory!=null){
                try {
                    rpc=factory.createRPCManager();
                } catch (SlxException e) {
                    //Ignore
                }
                if(rpc.getClass().isAssignableFrom(RPCManagerImpl.class)){
                    isCreated=true;
                    this.rpc=RPCManagerImpl.class.cast(rpc);
                }
                
            }
        }
        this.rpc=(RPCManagerImpl)rpc;
        
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
