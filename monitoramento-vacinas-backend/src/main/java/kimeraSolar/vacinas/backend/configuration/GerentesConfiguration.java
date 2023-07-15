package kimeraSolar.vacinas.backend.configuration;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;
import org.kie.api.runtime.rule.FactHandle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import kimeraSolar.ruleEngineManagement.domain.WorkingMemory;
import kimeraSolar.vacinas.domain.Gerente;
import kimeraSolar.vacinas.services.RuleEngine;

@Configuration
public class GerentesConfiguration {

    private Map<String, Gerente> gerentes = new HashMap<>();
    private Map<String, FactHandle> gerentesFactHandle = new HashMap<>();

    @Autowired
    private RuleEngine ruleEngine;
 
    public void addGerente(Gerente g){
        gerentes.put(g.getObjectId(), g);
        WorkingMemory workingMemory = ruleEngine.ruleEngineManagement.getWorkingMemory();
        FactHandle f = workingMemory.getKieSession().insert(g);
        gerentesFactHandle.put(g.getObjectId(), f);
    }

    public Map<String, Gerente> getGerentes(){
        return gerentes;
    }

    public Map<String, FactHandle> getGerentesFactHandle(){
        return gerentesFactHandle;
    }

    public Pair<Gerente, FactHandle> getGerente(String gerenteId){
        return Pair.of(gerentes.get(gerenteId), gerentesFactHandle.get(gerenteId));
    }
    
}
