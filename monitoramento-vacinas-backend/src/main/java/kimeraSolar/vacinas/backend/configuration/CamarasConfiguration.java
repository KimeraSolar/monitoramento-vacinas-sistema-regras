package kimeraSolar.vacinas.backend.configuration;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.Configuration;

import kimeraSolar.vacinas.domain.Camara;

@Configuration
public class CamarasConfiguration {
    
    private Map<String, Camara> camaras = new HashMap<>();

    public void addCamara(Camara c){
        camaras.put(c.getObjectId(), c);
    }

    public Map<String, Camara> getCamaras(){
        return camaras;
    }

    public void removeCamara(String camaraId){
        camaras.remove(camaraId);
    }

}
