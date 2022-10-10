package kimeraSolar.vacinas;

import org.kie.api.definition.rule.Rule;

import kimeraSolar.vacinas.services.RuleEngine;

public class App {
    

    static public void main( String[] args){

        RuleEngine.startEngine(args);

        for(Rule rule : RuleEngine.ruleEngineManagement.listRules()){
            System.out.println(rule.toString());
        }

    }

    
    
}
