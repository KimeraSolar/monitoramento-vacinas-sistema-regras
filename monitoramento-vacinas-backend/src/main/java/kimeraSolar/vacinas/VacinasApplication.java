package kimeraSolar.vacinas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.kie.api.definition.rule.Rule;

import kimeraSolar.vacinas.services.RuleEngine;

@SpringBootApplication
public class VacinasApplication {


    static public void main( String[] args ){

        //test_engine(args);
        SpringApplication.run(VacinasApplication.class, args);

    }

    static public void test_engine( String[] args){
        RuleEngine.startEngine(args);

        for(Rule rule : RuleEngine.ruleEngineManagement.listRules()){
            System.out.println(rule.toString());
        }
    }
    
}
