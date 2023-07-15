package kimeraSolar.vacinas.backend.configuration;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;
import org.kie.api.runtime.rule.FactHandle;
import org.springframework.context.annotation.Configuration;

import kimeraSolar.ruleEngineManagement.domain.WorkingMemory;
import kimeraSolar.vacinas.domain.Vacina;
import kimeraSolar.vacinas.services.RuleEngine;

@Configuration
public class VacinasConfiguration {
    
    private Map<String, Vacina> vacinas = new HashMap<>();
    private Map<String, FactHandle> vacinasFactHandle = new HashMap<>();
    
    public FactHandle addVacina(Vacina v){
        vacinas.put(v.getVacinaId(), v);
        WorkingMemory workingMemory = RuleEngine.ruleEngineManagement.getWorkingMemory();
        FactHandle f = workingMemory.getKieSession().insert(v);
        vacinasFactHandle.put(v.getVacinaId(), f);
        return f;
    }

    public Map<String, Vacina> getVacinas(){
        return vacinas;
    }

    public Map<String, FactHandle> getVacinasFactHandle(){
        return vacinasFactHandle;
    }

    public void removeVacina(String vacinaId){
        vacinas.remove(vacinaId);
        FactHandle f = vacinasFactHandle.get(vacinaId);
        WorkingMemory workingMemory = RuleEngine.ruleEngineManagement.getWorkingMemory();
        workingMemory.getKieSession().delete(f);
        vacinasFactHandle.remove(vacinaId);
    }

    public Pair<Vacina, FactHandle> getVacina(String vacinaId){
        return Pair.of(vacinas.get(vacinaId), vacinasFactHandle.get(vacinaId));
    }
}
