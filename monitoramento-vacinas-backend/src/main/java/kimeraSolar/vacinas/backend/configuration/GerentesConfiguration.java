package kimeraSolar.vacinas.backend.configuration;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.Configuration;

import kimeraSolar.vacinas.domain.Gerente;

@Configuration
public class GerentesConfiguration {

    private Map<String, Gerente> gerentes = new HashMap<>();

    public void addGerente(Gerente g){
        gerentes.put(g.getObjectId(), g);
    }

    public Map<String, Gerente> getGerentes(){
        return gerentes;
    }
    
}
