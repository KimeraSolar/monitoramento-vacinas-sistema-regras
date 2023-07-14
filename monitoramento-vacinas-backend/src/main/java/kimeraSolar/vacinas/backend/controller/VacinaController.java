package kimeraSolar.vacinas.backend.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kimeraSolar.vacinas.backend.configuration.VacinaTiposConfiguration;
import kimeraSolar.vacinas.backend.schemas.VacinaSchema;
import kimeraSolar.vacinas.domain.Vacina;

@RestController
@RequestMapping("/vacinas")
public class VacinaController {
    Logger logger = LoggerFactory.getLogger(VacinaController.class);

    @Autowired
    private VacinaTiposConfiguration vacinaTiposConfiguration;

    @GetMapping("/tipos")
    public List<VacinaSchema.TipoSchema> getTipos(){
        logger.info("Requesting VacinaTipos Info");
        
        List<VacinaSchema.TipoSchema> response = new ArrayList<>();

        for(Map.Entry<String, Vacina.TipoVacina> entry : vacinaTiposConfiguration.getVacinaTipos().entrySet()){
            Vacina.TipoVacina tipo = entry.getValue();
            response.add(new VacinaSchema.TipoSchema(tipo.getNome(), tipo.getTempMin(), tipo.getTempMax(), tipo.getTempoDescarte()));
        }
        logger.info("Request will return {} VacinaTipos Info", response.size());
        
        return response;
    }

    @PostMapping("/tipos/inserir")
    public VacinaSchema.TipoSchema insereTipo(@RequestBody VacinaSchema.TipoSchema vTipoSchema){
        logger.info("Creating VacinaTipo {}", vTipoSchema.name);
        try{
            if(vacinaTiposConfiguration.getVacinaTipos().containsKey(vTipoSchema.name)){
                throw new Exception();
            }else{
                vacinaTiposConfiguration.addVacinaTipo(new Vacina.TipoVacina(vTipoSchema.name, vTipoSchema.tempMin, vTipoSchema.tempMax, vTipoSchema.tempoDescarte));
            }
        }catch(Exception exception){
            logger.warn("Failed to create VacinaTipo {}", vTipoSchema.name);
        }
        
        return vTipoSchema;
    }

}
