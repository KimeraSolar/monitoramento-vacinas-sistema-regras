package kimeraSolar.vacinas.backend.configuration;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.Configuration;

import kimeraSolar.vacinas.domain.Vacina;

@Configuration
public class VacinaTiposConfiguration {
    
    private Map<String, Vacina.TipoVacina> vacinaTipos = new HashMap<>();

    public void addVacinaTipo(Vacina.TipoVacina tipoVacina){
        vacinaTipos.put(tipoVacina.getNome(), tipoVacina);
    }

    public Map<String, Vacina.TipoVacina> getVacinaTipos(){
        return vacinaTipos;
    }
}
