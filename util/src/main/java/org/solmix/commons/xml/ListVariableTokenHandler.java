package org.solmix.commons.xml;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class ListVariableTokenHandler implements TokenHandler
{
    private List<Map<String,Object>> variables=new  ArrayList<Map<String,Object>>();

    public ListVariableTokenHandler(Map<String,Object> variables) {
      this.variables.add(variables);
    }

    @Override
    public String handleToken(String content) {
        for(Map<String,Object> variable:variables){
            if (variable!=null&&variable.containsKey(content)) {
                return variable.get(content).toString();
              }
        }
      return "${" + content + "}";
    }
    
    public void addVariables(Map<String,Object> vars){
        variables.add(vars);
    }

}
