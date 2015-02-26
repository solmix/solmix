package org.solmix.sgt.client.widgets;

import com.smartgwt.client.data.Criteria;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.FormItem;


public class AdvanceForm extends DynamicForm
{

    
    public Criteria getValuesAsCriteria(){
        
        Criteria _return = super.getValuesAsCriteria();
        
        FormItem[] items=  getFields();
        for(FormItem item:items){
            if(item instanceof AdvanceItem){
                AdvanceItem aitem =(AdvanceItem)item;
                _return.addCriteria(aitem.getHiddenName(),aitem.getHiddenValue());
                
            }
            
        }
        return _return;
        
    }
    public  void setValue(String fieldName, String value){
    	super.setValue(fieldName, value);
    	 FormItem[] items=  getFields();
         for(FormItem item:items){
             if(item instanceof AdvanceItem){
                 AdvanceItem aitem =(AdvanceItem)item;
                 if(fieldName.equals(aitem.getHiddenName())){
                	 aitem.setHiddenValue(value);
                 }
             }
         }
    }
}
