package kimeraSolar.vacinas.backend.configuration;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;
import org.kie.api.runtime.rule.FactHandle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import kimeraSolar.ruleEngineManagement.domain.WorkingMemory;
import kimeraSolar.vacinas.domain.Camara;
import kimeraSolar.vacinas.services.RuleEngine;

@Configuration
public class CamarasConfiguration {
    
    private Map<String, Camara> camaras = new HashMap<>();
    private Map<String, FactHandle> camarasFactHandle = new HashMap<>();

    @Autowired
    private RuleEngine ruleEngine;
    
    public void addCamara(Camara c){
        camaras.put(c.getObjectId(), c);
        WorkingMemory workingMemory = ruleEngine.ruleEngineManagement.getWorkingMemory();
        FactHandle f = workingMemory.getKieSession().insert(c);
        camarasFactHandle.put(c.getObjectId(), f);
    }

    public Map<String, Camara> getCamaras(){
        return camaras;
    }

    public Map<String, FactHandle> getCamarasFactHandle(){
        return camarasFactHandle;
    }

    public void removeCamara(String camaraId){
        camaras.remove(camaraId);
        FactHandle f = camarasFactHandle.get(camaraId);
        WorkingMemory workingMemory = ruleEngine.ruleEngineManagement.getWorkingMemory();
        workingMemory.getKieSession().delete(f);
        camarasFactHandle.remove(camaraId);
    }

    public Pair<Camara, FactHandle> getCamara(String camaraId){
        return Pair.of(camaras.get(camaraId), camarasFactHandle.get(camaraId));
    }

}
