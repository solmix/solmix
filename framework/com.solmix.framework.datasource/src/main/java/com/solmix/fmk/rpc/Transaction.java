package com.solmix.fmk.rpc;

import com.solmix.api.datasource.DSRequest;
import com.solmix.api.datasource.DSResponse;
import com.solmix.api.exception.SlxException;
import com.solmix.api.rpc.RPCManager;
import com.solmix.fmk.context.SlxContext;


public class Transaction
{
    
    private  RPCManagerImpl rpc;
    private boolean isCreated;
    public Transaction(RPCManager rpc){
        this.rpc=(RPCManagerImpl)rpc;
        
    }
    public Transaction() throws SlxException{
        RPCManager rpc  =SlxContext.getRPCManagerFactory().getRPCManager();
        this.rpc=(RPCManagerImpl)rpc;
        isCreated=true;
        
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
