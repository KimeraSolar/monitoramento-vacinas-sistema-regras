package kimeraSolar.vacinas.backend.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kimeraSolar.vacinas.backend.configuration.CamarasConfiguration;
import kimeraSolar.vacinas.backend.schemas.CamaraSchema;
import kimeraSolar.vacinas.backend.schemas.VacinaSchema;
import kimeraSolar.vacinas.domain.Camara;
import kimeraSolar.vacinas.domain.Vacina;
import kimeraSolar.vacinas.domain.Eventos.LeituraTemperatura;

@RestController
@RequestMapping("/vacinas")
public class CamaraController {

    Logger logger = LoggerFactory.getLogger(CamaraController.class);

    @Autowired
    private CamarasConfiguration camarasConfiguration;
    
    public static class TemperaturaObject{

        public TemperaturaObject(Date key, Float value){
            this.key = key;
            this.value = value;
        }

        public Date key;
        public Float value;
    }

    @GetMapping("/{camaraid}")
    public CamaraSchema getCamaraInfo(@PathVariable("camaraid") String camaraId){
        logger.info("Requesting Info from Camara {}", camaraId);
        CamaraSchema response = null;
        try{
            Camara c = camarasConfiguration.getCamaras().get(camaraId);
            float[] location = {c.getLocal().getLatitude(), c.getLocal().getLongitude()};
                
            response = new CamaraSchema(c.getStatus(), c.getTemp().getTemp(), c.getObjectId(), location );
                
        }catch(NullPointerException exception){
            logger.warn("Request Failed for Camara", camaraId);
        }
        return response;
    }

    @GetMapping("/{camaraid}/temperaturas")
    public List<TemperaturaObject> getTemperaturas(@PathVariable("camaraid") String camaraId){
        logger.info("Requesting Temperaturas Info from Camara {}", camaraId);
        
        List<TemperaturaObject> temperaturas = null;
        try{
            temperaturas = new ArrayList<>();
            Camara c = camarasConfiguration.getCamaras().get(camaraId);
            for( Map.Entry<Date, LeituraTemperatura> temperatura : c.getTemperaturas().entrySet()){
                temperaturas.add(
                    new TemperaturaObject(temperatura.getKey(), temperatura.getValue().getTemp())
                );
            }
            logger.info("Request will return {} Temperaturas Info", temperaturas.size());
        }catch(NullPointerException exception){
            logger.warn("Request Failed for Camara", camaraId);
        }
        return temperaturas;
    }

    @GetMapping("/{camaraid}/vacinas")
    public List<VacinaSchema> getVacinas(@PathVariable("camaraid") String camaraId){
        logger.info("Requesting Vacinas Info from Camara {}", camaraId);
        
        List<VacinaSchema> vacinas = new ArrayList<>();
        try{
            Camara c = camarasConfiguration.getCamaras().get(camaraId);
            for( Vacina v : c.getVacinas()){
                Vacina.TipoVacina t = v.getTipo();
                vacinas.add( 
                    new VacinaSchema(t.getNome(), (float) t.getTempMax(), (float) t.getTempMin(), v.getAbastecimento(), v.getStatus())
                );
            }
            logger.info("Request will return {} Vacinas Info", vacinas.size());
        }catch(NullPointerException exception){
            logger.warn("Request Failed for Camara {}", camaraId);
        }

        return vacinas;
    }
    
}
