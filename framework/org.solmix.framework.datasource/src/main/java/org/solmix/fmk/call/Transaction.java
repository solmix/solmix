package org.solmix.fmk.call;

import org.solmix.api.call.DataSourceCall;
import org.solmix.api.call.DataSourceCallFactory;
import org.solmix.api.context.SystemContext;
import org.solmix.api.datasource.DSRequest;
import org.solmix.api.datasource.DSResponse;
import org.solmix.api.exception.SlxException;


public class Transaction
{
    
    private  DataSourceCallImpl rpc;
    private boolean isCreated;
    public Transaction(DataSourceCall rpc,SystemContext sc){
        if(rpc==null){
            DataSourceCallFactory factory=   sc.getBean(DataSourceCallFactory.class);
            if(factory!=null){
                try {
                    rpc=factory.createRPCManager();
                } catch (SlxException e) {
                    //Ignore
                }
                if(rpc.getClass().isAssignableFrom(DataSourceCallImpl.class)){
                    isCreated=true;
                    this.rpc=DataSourceCallImpl.class.cast(rpc);
                }
                
            }
        }
        this.rpc=(DataSourceCallImpl)rpc;
        
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
