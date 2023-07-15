package kimeraSolar.vacinas.services.configurations.RegrasMonitoramento;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.springframework.context.annotation.Configuration;

import kimeraSolar.ruleEngineManagement.domain.RulePackage;
import kimeraSolar.ruleEngineManagement.services.packageServices.RuleFileReader;
import kimeraSolar.ruleEngineManagement.services.packageServices.implementation.RuleFileReaderImpl;

@Configuration
public class RulesFileReader {

    public RulePackage getRulesFromFile(String ruleFileName){

        RuleFileReader ruleFileReader = new RuleFileReaderImpl();

        RulePackage rulePackage = new RulePackage();
        try {
            rulePackage = ruleFileReader.readRuleFile(ruleFileName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return rulePackage;
    }
    
}
